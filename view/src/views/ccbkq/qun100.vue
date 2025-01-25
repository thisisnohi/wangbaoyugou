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
        stripe
        v-loading="loading"
        :show-page="false"
        :showIndex="true"
        :data="tableData"
        @getTableData="getTableData"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          v-for="(item,index) in tableColumnList"
          :key="index"
          :prop="item.value"
          :label="item.label"
          align="center"
        >
          <el-table-column
            v-if="item.isLeaf !== 'Y'"
            v-for="(subItem, subIndex) in item.subList"
            :key="subItem"
            :prop="subItem.value"
            :label="subItem.label"
            align="center"
          />
        </el-table-column>
      </Table>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, provide, reactive, inject, watch } from "vue";
import Table from "@/components/table/index.vue";
import { Page } from "@/components/table/type";
import { monthData, exportMonthDataApi } from "@/api/ccbkq/monthData";
import { ElMessage } from "element-plus";
import type { LayerInterface } from "@/components/layer/index.vue";
import { Plus, Search, Delete } from "@element-plus/icons";
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
      monthData(query)
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

      exportMonthDataApi(query)
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
    
    const tableRowClassName = ({ row, rowIndex }) => {
      //改变某行的背景色
      console.info("ROW ", rowIndex);
      if (row.STATUS === 1) {
        console.info("ROW ", rowIndex, " 异常");
        return "error";
      }
    };

    return {
      Plus,
      Search,
      Delete,
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
</style>
