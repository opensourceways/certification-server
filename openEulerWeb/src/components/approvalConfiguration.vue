<template>
  <div class="approvalConfiguration">
    <div class="content">
      <TitleInput
        :title="title"
        :placeholder="placeholder"
        @inputChange="inputChange"
      ></TitleInput>
      <ScreenCondition
        :textList="textList"
        :textTitle="textTitle"
        @handleChange="handleChange"
      ></ScreenCondition>
      <el-table :data="tableData" style="width: 100%; margin-top: 24px">
        <el-table-column
          prop="innovationCenterName"
          label="审批路径"
          show-overflow-tooltip
          width="235"
        ></el-table-column>
        <el-table-column
          prop="planReview"
          label="方案审核"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="reportFirstTrial"
          label="报告初审"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="reportReexamination"
          label="报告复核"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="certFirstTrail"
          label="证书初审"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="certReexamination"
          label="证书签发"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column prop="updateTime" label="新增日期"></el-table-column>
        <el-table-column prop="address" label="操作">
          <template>
            <div class="options"></div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-agination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="sizes,prev,pager,next,jumper"
          :total="total"
        ></el-agination>
      </div>
    </div>
  </div>
</template>
<script>
import TitleInput from "@/components/titleInput.vue";
import ScreenCondition from "@/components/screenCondition.vue";
import { testOrganization } from "@/assets/js/publicData";
export default {
  components: {
    TitleInput,
    ScreenCondition,
  },
  data() {
    return {
      title: "审批配置",
      tableData: [],
      textTitle: "测试机构",
      textList: [],
      testOrganization: [],
      reviewStatus: [],
      inputValue: "",
      currentPage: 1,
      pageSize: 10,
      total: 100,
      placeholder: "请输入创新中心",
    };
  },
  mounted() {
    this.initializingProcessFn();
    this.getInforMation();
  },
  methods: {
    initializingProcessFn() {
      this.testOrganization = JSON.parse(JSON.stringify(testOrganization));
      for (var i = 0; i < this.testOrganization.length; i++) {
        this.testOrganization[i] = {
          name: this.testOrganization[i],
          active: false,
        };
      }
      this.textList = JSON.parse(JSON.stringify(this.testOrganization));
    },
    inputChange(value) {
      this.inputValue = value;
      this.getInforMation();
    },
    handleChange(value) {
      this.reviewStatus = value;
      this.getInforMation();
    },
    getInforMation() {
      let params = {
        searchIcName: this.inputValue,
        curPage: this.currentPage,
        pageSize: this.pageSize,
        icNameList: this.reviewStatus,
      };
      this.axios
        .post("/approvalPath/getApprovalPathBypage", params)
        .then((response) => {
          if (response.data.code === 200) {
            this.tableData = response.data.result.records;
            this.total = reponse.data.result.total;
          } else {
            this.$message.error(response.data.result.errorMsg);
          }
        });
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getInforMation();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getInforMation();
    },
  },
};
</script>
<style lang="less" scoped>
.approvalConfiguration {
  display: flex;
  justify-content: center;
  .content {
    width: 1416px;
    .options {
      display: flex;
      .edits {
        color: #002fa7;
        margin-right: 20px;
      }
      .delete {
        color: #e42121;
      }
    }
    .pagination {
      height: 100px;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
}
</style>
<style lang="less">
.approvalConfiguration {
  .el-table th.el-table__cell {
    background: #e5e8f0;
    color: #000;
  }
  .el-table th.el-table__cell > .cell {
    padding-left: 40px;
  }
  .el-table .cell {
    padding-left: 40px;
  }
}
</style>
