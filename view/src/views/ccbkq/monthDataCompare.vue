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

        <el-select v-model="query.status" placeholder="是否异常" style="width: 240px">
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>

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
        v-model:page="page"
        v-loading="loading"
        :showIndex="true"
        :data="tableData"
        :row-class-name="tableRowClassName"
        @getTableData="getTableData"
        @selection-change="handleSelectionChange"
      >
        <el-table-column prop="username" label="姓名" align="center" />
        <el-table-column prop="work_date" label="工作日" align="center" />
        <el-table-column prop="大楼上班" label="大楼上班" align="center">
          <template #default="{ row }">
            <span>{{ date2Time(row.大楼上班)}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="大楼下班" label="大楼下班" align="center">
          <template #default="{ row }">
            <span>{{ date2Time(row.大楼下班) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="大楼时差" label="大楼时差" align="center">
        </el-table-column>
        <el-table-column prop="移动上班" label="移动上班" align="center">
          <template #default="{ row }">
            <span>{{ date2Time(row.移动上班) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="移动下班" label="移动下班" align="center">
          <template #default="{ row }">
            <span>{{ date2Time(row.移动下班) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="移动时差" label="移动时差" align="center" />
        <el-table-column prop="上班分钟差" label="上班分钟差" align="center" />
        <el-table-column prop="下班分钟差" label="下班分钟差" align="center" />
        <el-table-column prop="DEDUCTION" label="扣减" align="center" />
        <el-table-column prop="MINS_JS" label="结算分钟数" align="center" />
        <el-table-column prop="DAYS_JS" label="结算人天" align="center" />
        <el-table-column prop="MSG" label="备注" align="center" />
      </Table>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, provide, reactive, inject, watch } from "vue";
import Table from "@/components/table/index.vue";
import { Page } from "@/components/table/type";
import { getMonthDataCompare, exportMonthDataCompareApi } from "@/api/ccbkq/monthData";
import { ElMessage } from "element-plus";
import type { LayerInterface } from "@/components/layer/index.vue";
import { Plus, Search, Delete, Download } from "@element-plus/icons";
import dayjs from 'dayjs';
import numeral from 'numeral';

export default defineComponent({
  name: "treeTable",
  components: {
    Table,
  },

  setup() {
    const statusOptions = [
      {
        value: '',
        label: '-',
      },
      {
        value: '0',
        label: '正常',
      },
      {
        value: '1',
        label: '异常',
      },
    ]

    // 存储搜索用的数据
    const query = reactive({
      username: "",
      dateRange: "",
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
    // 分页参数, 供table使用
    const page: Page = reactive({
      index: 1,
      size: 20,
      total: 0,
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
      loading.value = true;
      if (init) {
        page.index = 1;
      }

      query.startDate = ''
      query.endDate = ''
      if (query.dateRange && query.dateRange.length >= 2) {
        query.startDate = query.dateRange[0]
        query.endDate = query.dateRange[1]
      }

      let params = {
        page: {
          pageIndex: page.index,
          pageSize: page.size,
        },
        data: query,
      };
      getMonthDataCompare(params)
        .then((res) => {
          let data = res.data.list;
          if (Array.isArray(data)) {
            // data.forEach((d) => {
            //   const select = selectData.find((select) => select.value === d.choose);
            //   select ? (d.chooseName = select.label) : (d.chooseName = d.choose);
            //   const radio = radioData.find((select) => select.value === d.radio);
            //   radio ? (d.radioName = radio.label) : d.radio;
            // });
          }
          tableData.value = res.data;
          // 总记录数
          page.total = Number(res.page.totalRecords);
        })
        .catch((error) => {
          console.error("===>", error);
          tableData.value = [];
          page.index = 1;
          page.total = 0;
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

      exportMonthDataCompareApi(query)
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

    const tableRowClassName = ({row, rowIndex}) => { //改变某行的背景色
      console.info("ROW ", rowIndex)
      if (row.STATUS === 1) {
        console.info("ROW ", rowIndex, " 异常")
        return "error";
      } 
    };

    const date2Time = (date:any) => { //改变某行的背景色
      return date == null? '' :dayjs(date).format("HH:mm:ss")
    };

    // getTableData(true)
    return {
      Plus,
      Search,
      Delete,
      Download,
      query,
      statusOptions,
      tableData,
      chooseData,
      loading,
      page,
      layer,
      handleSelectionChange,
      getTableData,
      exportTableData,
      dayjs,
      numeral,
      tableRowClassName,
      date2Time
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
    background: #F5A623;
  }
</style>
