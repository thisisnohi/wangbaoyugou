<template>
  <div class="layout-container">
    <div class="layout-container-form flex space-between">
      <div class="layout-container-form-handle"></div>
      <div class="layout-container-form-search">
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
          >{{ $t("message.common.search") }}</el-button
        >
      </div>
    </div>
    <div class="layout-container-table">
      <el-row :gutter="20">
        <el-col :span="8">
          <div>
            <h6>结算考勤数据重复</h6>
            <Table
              ref="userWorkRepeatTable"
              stripe
              v-loading="loading"
              :show-page="false"
              :showIndex="false"
              :data="userWorkRepeatTableData"
              @getTableData="getUserWorkRepeatTableData"
            >
              <el-table-column prop="USERNAME" label="姓名" align="center" />
              <el-table-column prop="WORK_DATE" label="日期" align="center" />
              <el-table-column prop="COUNT" label="COUNT" align="center" />
              <el-table-column prop="SUM" label="SUM" align="center" />
              <el-table-column prop="PROJECT" label="PROJECT" align="center" />
            </Table>
          </div>
        </el-col>
        <el-col :span="8">
          <div>
            <h6>结算考勤有，月份考勤无</h6>
            <Table
              ref="jsWithOutAppTable"
              stripe
              v-loading="loading"
              :show-page="false"
              :showIndex="false"
              :data="jsWithOutAppTableData"
              @getTableData="getJsWithOutAppTableData"
            >
              <el-table-column prop="USERNAME" label="姓名" align="center" />
              <el-table-column prop="WORK_DATE" label="日期" align="center" />
              <el-table-column prop="DAYS_JS" label="人天" align="center" />
            </Table>
          </div>
        </el-col>
        <el-col :span="8">
          <div>
            <h6>月份考勤有，结算考勤无</h6>
            <Table
              ref="appWithOutJsTable"
              stripe
              v-loading="loading"
              :show-page="false"
              :showIndex="false"
              :data="appWithOutJsTableData"
              @getTableData="getAppWithOutJsTableData"
            >
              <el-table-column prop="USERNAME" label="姓名" align="center" />
              <el-table-column prop="WORK_DATE" label="日期" align="center" />
              <el-table-column prop="DAYS_JS" label="人天" align="center" />
            </Table>
          </div>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <div>
            <h6>app与结算考勤不一致</h6>
            <Table
              ref="daysDiffTable"
              stripe
              v-loading="loading"
              :show-page="false"
              :showIndex="false"
              :data="daysDiffTableData"
              @getTableData="getDaysDiffTableData"
            >
              <el-table-column prop="USERNAME" label="姓名" align="center" />
              <el-table-column prop="WORK_DATE" label="日期" align="center" />
              <el-table-column prop="DAYS_JS" label="结算人天" align="center"  />
              <el-table-column prop="DAYS_KQ" label="考勤人天" align="center" />
              <el-table-column prop="START_TIME" label="考勤开始" align="center" :formatter="formatter" />
              <el-table-column prop="END_TIME" label="考勤结束" align="center" :formatter="formatter" />
              <el-table-column prop="MINS" label="打卡分钟数" align="center" />
              <el-table-column prop="DEDUCTION" label="扣减" align="center" />
              <el-table-column prop="MINS_JS" label="结算分钟数" align="center" />
            </Table>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, inject, watch } from "vue";
import Table from "@/components/table/index.vue";
import {
  userWorkRepeat,
  jsWithOutApp,
  appWithOutJs,
  daysDiff,
} from "@/api/ccbkq/monthData";
import { ElMessage } from "element-plus";
import { Plus, Search, Delete } from "@element-plus/icons";
import dayjs from "dayjs";

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
      project: "",
      dateRange: [
        dayjs().subtract(3, "month").set("date", 1).format("YYYY-MM-DD"),
        dayjs().set("date", 1).subtract(1, "day").format("YYYY-MM-DD"),
      ],
      status: "",
      // 是否显示项目
      byProject: true,
      startDate: "",
      endDate: "",
    });
    const loading = ref(false);
    const userWorkRepeatTableData = ref([]);
    const jsWithOutAppTableData = ref([]);
    const appWithOutJsTableData = ref([]);
    const daysDiffTableData = ref([]);

    // 获取表格数据
    const getUserWorkRepeatTableData = (init: boolean) => {
      console.info("===> getUserWorkRepeatTableData");
      query.startDate = "";
      query.endDate = "";
      if (query.dateRange && query.dateRange.length >= 2) {
        query.startDate = query.dateRange[0];
        query.endDate = query.dateRange[1];
      } else {
        console.error("时间范围必选");
        ElMessage.error("时间范围必选");
        return;
      }
      loading.value = true;
      userWorkRepeat(query)
        .then((res) => {
          console.info(res);
          userWorkRepeatTableData.value = res.data;
        })
        .catch((error) => {
          console.error("===>", error);
          userWorkRepeatTableData.value = [];
        })
        .finally(() => {
          console.info("===>finally");
          loading.value = false;
        });
    };

    // 获取表格数据
    const getJsWithOutAppTableData = (init: boolean) => {
      console.info("===> jsWithOutAppTableData");
      query.startDate = "";
      query.endDate = "";
      if (query.dateRange && query.dateRange.length >= 2) {
        query.startDate = query.dateRange[0];
        query.endDate = query.dateRange[1];
      } else {
        console.error("时间范围必选");
        ElMessage.error("时间范围必选");
        return;
      }
      loading.value = true;
      jsWithOutApp(query)
        .then((res) => {
          console.info(res);
          jsWithOutAppTableData.value = res.data;
        })
        .catch((error) => {
          console.error("===>", error);
          jsWithOutAppTableData.value = [];
        })
        .finally(() => {
          console.info("===>finally");
          loading.value = false;
        });
    };

    // 获取表格数据
    const getAppWithOutJsTableData = (init: boolean) => {
      console.info("===> appWithOutJsTableData");
      query.startDate = "";
      query.endDate = "";
      if (query.dateRange && query.dateRange.length >= 2) {
        query.startDate = query.dateRange[0];
        query.endDate = query.dateRange[1];
      } else {
        console.error("时间范围必选");
        ElMessage.error("时间范围必选");
        return;
      }
      loading.value = true;
      appWithOutJs(query)
        .then((res) => {
          console.info(res);
          appWithOutJsTableData.value = res.data;
        })
        .catch((error) => {
          console.error("===>", error);
          appWithOutJsTableData.value = [];
        })
        .finally(() => {
          console.info("===>finally");
          loading.value = false;
        });
    };

    // 获取表格数据
    const getDaysDiffTableData = (init: boolean) => {
      console.info("===> daysDiffTableData");
      query.startDate = "";
      query.endDate = "";
      if (query.dateRange && query.dateRange.length >= 2) {
        query.startDate = query.dateRange[0];
        query.endDate = query.dateRange[1];
      } else {
        console.error("时间范围必选");
        ElMessage.error("时间范围必选");
        return;
      }
      loading.value = true;
      daysDiff(query)
        .then((res) => {
          console.info(res);
          daysDiffTableData.value = res.data;
        })
        .catch((error) => {
          console.error("===>", error);
          daysDiffTableData.value = [];
        })
        .finally(() => {
          console.info("===>finally");
          loading.value = false;
        });
    };

    // 刷新所有数据
    const getTableData = (flag: boolean) => {
      getUserWorkRepeatTableData(flag);
      getJsWithOutAppTableData(flag);
      getAppWithOutJsTableData(flag);
      getDaysDiffTableData(flag);
    };

    // 格式化日期
    const formatter = (row: any, column: any) => {
      return dayjs(row[column.property]).format("HH:mm:ss");
    };

    return {
      Plus,
      Search,
      Delete,
      query,
      tableColumnList,
      loading,
      dayjs,
      formatter,
      userWorkRepeatTableData,
      jsWithOutAppTableData,
      appWithOutJsTableData,
      daysDiffTableData,
      getTableData,
      getUserWorkRepeatTableData,
      getJsWithOutAppTableData,
      getAppWithOutJsTableData,
      getDaysDiffTableData,
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
.el-table thead th.WEEKEND,
.el-table tbody tr td.WEEKEND,
.el-table--enable-row-hover .el-table__body tr:hover > td.WEEKEND {
  background: #f5a623;
}
span.error {
  color: red;
}
</style>
