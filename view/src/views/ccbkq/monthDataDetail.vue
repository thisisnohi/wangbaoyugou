<template>
  <div class="layout-container">
    <div class="layout-container-form flex space-between">
      <div class="layout-container-form-handle"></div>
      <div class="layout-container-form-search">
        <el-input
          style="width: 230px"
          v-model="query.username"
          placeholder="姓名,多个以,分隔"
          @change="getTableData(true)"
        ></el-input>

        <el-date-picker
          style="width: 230px"
          v-model="query.dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
        <el-button
          type="primary"
          :icon="Search"
          class="search-btn"
          @click="getTableData(true)"
          >{{ $t("message.common.search") }}</el-button>
        <el-button
            type="success"
            :icon="Download"
            @click="exportTableData()"
            >导出</el-button>
      </div>
    </div>
    <div class="layout-container-table">
      <Table
        ref="table"
        v-loading="loading"
        stripe
        :show-page="false"
        :showIndex="true"
        :data="tableData"
        @getTableData="getTableData"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          v-for="(item,index) in tableColumnList"
          :label-class-name="item.TABLE_HEAD_CSS"
          :class-name="item.TABLE_HEAD_CSS"
          :key="index"
          :prop="item.value"
          :label="item.label"
          align="center"
        >
          <template #default="{ row }">
              <el-tooltip class="item" effect="dark" placement="top"> 
                <template v-slot:content>
                  <div>     姓名: {{ showItemTips(row[item.value + '_INFO'], 'USERNAME') }} 日期: {{ showItemTips(row[item.value + '_INFO'], 'WORK_DATE') }} </div>
                  <div>上下班时间: {{ showItemTips(row[item.value + '_INFO'], 'START_TIME') }}  -  {{ showItemTips(row[item.value + '_INFO'], 'END_TIME') }} </div>
                  <div>上班分钟: {{ showItemTips(row[item.value + '_INFO'], 'MINS') }} 扣减分钟: {{ showItemTips(row[item.value + '_INFO'], 'DEDUCTION') }} </div>
                  <div>结算分钟: {{ showItemTips(row[item.value + '_INFO'], 'MINS_JS') }} 结算人天: {{ showItemTips(row[item.value + '_INFO'], 'DAYS_JS') }}</div>
                  <div>考勤状态: {{ showItemTips(row[item.value + '_INFO'], 'KQ_STATUS') }}</div>
                  <div>备注: {{ showItemTips(row[item.value + '_INFO'], 'KQ_MSG') }} </div>
                </template>
                <span v-if="row[item.value + '_STATUS'] !=='N'" >{{ row[item.value] }}</span>
                <span v-else class="error">{{ row[item.value] }}</span>
              </el-tooltip>
          </template>
        </el-table-column>
      </Table>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, provide, reactive, inject, watch } from "vue";
import Table from "@/components/table/index.vue";
import { Page } from "@/components/table/type";
import { monthDataDetail, exportMonthDataDetailApi } from "@/api/ccbkq/monthData";
import { ElMessage } from "element-plus";
import type { LayerInterface } from "@/components/layer/index.vue";
import { Plus, Search, Delete, Download } from "@element-plus/icons";
import dayjs from "dayjs";
import numeral from "numeral";

export default defineComponent({
  name: "treeTable",
  components: {
    Table,
  },

  setup() {
    const tableColumnList = ref([]);

    // 存储搜索用的数据
    const query = reactive({
      username: "",
      dateRange: [dayjs().subtract(3, 'month').set('date', 1).format('YYYY-MM-DD'), dayjs().set('date', 1).subtract(1, 'day').format('YYYY-MM-DD')],
      status: "",
      startDate: "",
      endDate: "",
    });
    // 弹窗控制器
    const layer: LayerInterface = reactive({
      show: false,
      title: "新增",
      showButton: true,
    });
    const loading = ref(false);
    const tableData = ref([]);
    const chooseData = ref([]);
    const handleSelectionChange = (val: []) => {
      chooseData.value = val;
    };
    // 获取表格数据
    // params <init> Boolean ，默认为false，用于判断是否需要初始化分页
    const getTableData = (init: boolean) => {
      console.info("===> getTableData");
      
      query.startDate = "";
      query.endDate = "";
      if (query.dateRange && query.dateRange.length >= 2) {
        query.startDate = query.dateRange[0];
        query.endDate = query.dateRange[1];
      } else {
        console.error("时间范围必选");
        ElMessage.error('时间范围必选')
        return;
      }
      // 月份差
      // const monthDiff = dayjs(query.endDate).diff(query.startDate, "month");
      // let monthStart = dayjs(query.startDate).month() + 1;
      // console.info('monthStart:' + monthStart + ',monthDiff:' + monthDiff)

      loading.value = true;
      monthDataDetail(query)
        .then((res) => {
          console.info(res)
          tableData.value = res.data.dataList;
          tableColumnList.value = res.data.columnList;
        })
        .catch((error) => {
          console.error("===>", error);
          tableData.value = [];
        })
        .finally(() => {
          console.info("===>finally");
          loading.value = false;
        });
    };
    const tableRowClassName = ({ row, rowIndex }) => {
      //改变某行的背景色
      console.info("ROW ", rowIndex);
      if (row.STATUS === 1) {
        console.info("ROW ", rowIndex, " 异常");
        return "error";
      }
    };

    // 导出
    const exportTableData = () => {
      console.info("===> exportTableData");
      loading.value = true;

      query.startDate = ''
      query.endDate = ''
      if (query.dateRange && query.dateRange.length >= 2) {
        query.startDate = query.dateRange[0]
        query.endDate = query.dateRange[1]
      }

      exportMonthDataDetailApi(query)
        .then((res) => {
          console.info('下载完成')
        })
        .catch((error) => {
          console.error("===>", error);
        })
        .finally(() => {
          console.info("===>finally");
          loading.value = false;
        });
    };

    // Tips展示
    const showItemTips = (item: any, name:string) => {
      if (name) {
        const rs = item[name]
        if (rs) {
          return rs
        }
        return '';
      }
      let tips = `
      <div>姓名:${item.USERNAME} 日期: ${item.WORK_DATE} <br></div>
      <div>上下班时间：[${item.START_TIME} - ${item.END_TIME}] <br></div>
      上班分钟: [${item.MINS}] 扣减分钟:[${item.DEDUCTION}] <br>
      结算分钟: [${item.MINS_JS}] 结算人天: [${item.DAYS_JS}]<br>
      考勤状态: [${item.KQ_STATUS}] 备注: [${item.KQ_MSG}]<br>
      `;
      return tips
    };

    return {
      Plus,
      Search,
      Delete,
      Download,
      query,
      tableColumnList,
      tableData,
      chooseData,
      loading,
      layer,
      handleSelectionChange,
      getTableData,
      exportTableData,
      dayjs,
      numeral,
      showItemTips,
      tableRowClassName,
    };
  },
});
</script>

<style lang="scss">
.layout-container {
  height: 100%;
  margin: 0 0 0 10px;
  width: calc(100% - 10px);
}
.el-table tr.error {
  background: #f5a623;
}
.el-table thead th.WEEKEND, .el-table tbody tr td.WEEKEND,
.el-table--enable-row-hover .el-table__body tr:hover>td.WEEKEND
{
  background: #f5a623;
}
span.error{
  color: red;
}
</style>
