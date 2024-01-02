<template>
  <div class="layout-container">
    <div class="layout-container-form flex-direction-column" v-loading="listLoading">
      <el-form ref="queryForm" class="text-left" :model="searchParam" :rules="searchParamRule" :inline="true" label-width="120px" @keyup.enter.native="queryBtn">
        <el-form-item prop="name">
          <el-input v-model="searchParam.name" placeholder="员工名" style="width: 98%;" />
        </el-form-item>
      </el-form>
      <div class="form-item text-left">
        <el-button type="primary" :icon="Search" :loading="listLoading" @click="queryBtn">查询</el-button>
        <el-button :icon="RefreshLeft" @click="resetBtn">重置</el-button>
      </div>
    </div>
    <div class="layout-container-table">

      <el-table :data="tableData" style="width: 100%" stripe highlight-current-row border>

        <el-table-column align="center" prop="date" label="序号" min-width="5%">
          <template #default="row">
            <span>{{ row.$index+1 }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="name" label="员工姓名" min-width="10% ">
          <template #default="{row}">
            <span>{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="phone" label="手机号" min-width="15%">
          <template #default="{row}">
            <span>{{ row.phone }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" label="token" prop="accesstoken" min-width="25%">
          <template #default="{row}">
            <el-input v-model="row.accesstoken"  />
          </template>
        </el-table-column>
        <el-table-column align="center" prop="cardid" label="证件号码" min-width="20%">
          <template #default="{row}">
            <span>{{ row.cardid }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="personid" label="personid" min-width="20%">
          <template #default="{row}">
            <span>{{ row.personid }}</span>
          </template>
        </el-table-column>
       <el-table-column label="操作" min-width="5%">
          <template #default="{row,$index}">
            <el-button type="warning" @click="save(row)">保存</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          v-if="showPage"
          v-model:current-page="page.pageIndex"
          class="system-page"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="page.totalRecords"
          :page-size="page.pageSize"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
      >
      </el-pagination>
    </div>
  </div>
</template>

<script>
import {userInfoSave, userTokenQuery} from '@/api/dingtalk/kaoqing'
import { Refresh, RefreshLeft, Search, Edit } from "@element-plus/icons"
export default {
  components: { Refresh },
  computed: {
    Refresh() {
      return Refresh
    },
    RefreshLeft() {
      return RefreshLeft
    },
    Search() {
      return Search
    },
    Edit() {
      return Edit
    }
  },
  provide() {
    return {
      closeDialog: this.closeDialog,
      queryBtn: this.queryBtn
    }
  },
  data() {
    return {
      listLoading: false,
      showPage: true,
      pageSizes: { type: Array, default: [10, 20, 50, 100] },
      pageLayout: { type: String, default: "total, sizes, prev, pager, next, jumper" }, // 分页需要显示的东西，默认全部
      syncLoading: false,
      mainPage: true,
      downPage: false,
      dialogFlag: false,
      recruitId: '',
      rowIndex: '',
      rowObj: {},
      recruitDetailTabName: 'baseInfo',
      searchParam: {
        name
      },
      tableData: [],
      page: {
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        totalRecords: 0
      },
      timer: null
    }
  },
  async created() {
    await this.getList()
  },
  methods: {
    // 分页相关：监听页码切换事件
    handleCurrentChange  (val)  {
      if (this.timer) {
        this.page.index = 1
      } else {
        this.page.index = val
        this.getList()
      }
    },
    // 分页相关：监听单页显示数量切换事件
    handleSizeChange(val){
      this.timer = 'work'
      setTimeout(() => {
        this.timer = null
      }, 100)
      this.page.pageSize = val
      this.getList()
    },
    // 查询
    queryBtn() {
      const $this = this
      $this.$refs['queryForm'].validate(async valid => {
        if (valid) {
          $this.page.pageIndex = 1
          await $this.getList()
          $this.downPage = false
        }
      })
    },
    baseStatPassedChanged() {
      this.queryBtn()
    },
    async getList(searchParam) {
      this.listLoading = true
      try {
        const { data, page } = await userTokenQuery(this.page, this.searchParam)
        this.tableData = data
        this.page = page
      } catch (e) {
        console.error(e)
        this.listLoading = false
        this.$message.warning(e)
      }
      this.listLoading = false
    },
    // 重置
    resetBtn() {
      // this.$refs.queryForm.resetFields()
      this.listLoading = false
      this.syncLoading = false
      this.searchParam = {
        queryTimeResult: [],
        queryLocationResult: [],
      }
    },
    closeDialog() {
      this.dialogFlag = false
    },
    // 保存
    async save(row) {
      console.log(row)
      const { data } = await userInfoSave(row)
      await $this.getList()
    }

  }
}
</script>
<style scoped>
.el-col > div, .el-col input {
  width: 98%;
}
.el-col .el-input {
  width: 98%;
}
.el-select__tags {
  white-space: nowrap;
  overflow: hidden;
}
</style>
