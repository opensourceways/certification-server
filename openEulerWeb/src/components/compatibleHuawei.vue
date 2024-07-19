<template>
  <div class="content" v-show="isactive == 'true'">
    <TitleInput
      :title="title"
      placeholder="placeholder"
      @inputChange="inputChange"
    ></TitleInput>
    <ScreenCondition
      :textList="textList1"
      :textTitle="textTitle"
      @handleChange="handleChange1"
    ></ScreenCondition>
    <ScreenCondition
      :textList="textList3"
      :textTitle="textTitle2"
      @handleChange="handleChange3"
    ></ScreenCondition>
    <div class="review" v-show="selections.length > 0" @click="handleReview">
      批量审核
    </div>
    <div class="review1" v-show="selections.length === 0">批量审核</div>
    <el-table
      :data="tableData"
      style="width: 100%; margin-top: 24px"
      @selection-change="selectionChange"
    >
      <el-table-column
        type="selection"
        width="55"
        :selectable="(row) => row.status === '审核中'"
      ></el-table-column>
      <el-table-column
        prop="productName"
        label="测试产品名称"
        show-overflow-tooltip
        width="130"
      >
        <template slot-scope="scoped">
          <span
            style="color: #002fa7"
            @click="goDetails(scoped.row)"
            class="cursor"
            >{{ scoped.row.productName }}</span
          >
        </template>
      </el-table-column>
      <el-table-column
        prop="companyName"
        label="ISV公司名称"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        prop="productVersion"
        label="测试产品版本号"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        prop="systemName"
        label="操作系统名称"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        prop="systemVersion"
        label="操作系统版本号"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        prop="serverPlatform"
        label="硬件算力平台"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        prop="serverSupplier"
        label="硬件厂商"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        prop="serverModel"
        label="硬件型号"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        prop="productType"
        label="产品类型"
        width="110px"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        prop="status"
        label="状态"
        show-overflow-tooltip
        width="110px"
      >
        <template slot-scope="scoped">
          <div
            class="processStatusFail"
            v-if="scoped.row.status && scoped.row.status.slice(-3) == '已驳回'"
          >
            {{ scoped.row.status }}
          </div>
          <div
            class="processStatus"
            v-if="scoped.row.status && scoped.row.status != '已通过'"
          >
            待审核
          </div>
          <div class="processStatusSuccess" v-else>
            {{ scoped.row.status }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scoped">
          <span class="edits cursor" @click="goDetails(scoped.row)">{{
            scoped.row.operation
          }}</span>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="sizes,prev,pager,next,jumper"
        :total="total"
      ></el-pagination>
    </div>
    <el-dialog
      title="批量审核"
      :visible.sync="dialogVisible"
      width="550px"
      center
    >
      <div>
        <div style="padding: 0 24px 0 24px">
          <div class="selecteds">
            <img src="../assets/images/remind.png" alt="" />
            <span style="margin-left: 8px"
              >本次已选中{{
                selections.length
              }}条兼容数据，您可以批量通过或驳回。</span
            >
          </div>
          <div style="margin-top: 34px; margin-left: -20px">
            <el-form
              :model="ruleForm"
              :rules="rules"
              ref="ruleForm"
              label-width="100px"
              class="demo-ruleForm"
            >
              <el-form-item label="审核结果" prop="handlerResult">
                <el-radio-group v-model="ruleForm.handlerResult">
                  <el-radio :label="1">通过</el-radio>
                  <el-radio :label="2">驳回</el-radio></el-radio-group
                >
              </el-form-item>
              <el-form-item label="审核意见" prop="handlerComment">
                <el-input
                  placeholder="请输入内容"
                  resize="none"
                  type="textarea"
                  v-model="ruleForm.handlerComment"
                  maxlength="1000"
                  show-word-limit
                ></el-input>
              </el-form-item>
            </el-form>
          </div>
          <div class="buttons" style="margin-left: 80px">
            <div class="button1" @click="handleResult">确定</div>
            <div class="button2" @click="dialogVisible = false">取消</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import TitleInput from "@/components/titleInput.vue";
import ScreenCondition from "@/components/screenCondition.vue";
import {
  status,
  testOrganization,
  parentIndustryType,
} from "@/assets/js/publicData.js";
export default {
  components: {
    TitleInput,
    ScreenCondition,
  },
  data() {
    return {
      dataIdList: [],
      ruleForm: { handlerResult: null, handlerComment: null },
      rules: {
        handlerResult: [
          { required: true, message: "请选择审核结果", trigger: "change" },
        ],
        handlerComment: [
          { required: true, message: "请填写审核意见", trigger: "change" },
        ],
      },
      dialogVisible2: false,
      checked: false,
      fileList: [],
      nolist: false,
      dialogVisible: false,
      productName1: null,
      status: null,
      statusOptions: ["审核中", "已通过", "已驳回"],
      isactive: "true",
      title: "兼容数据审核",
      textTitle: "状态",
      textTitle1: "测试机构",
      textTitle2: "产品类型",
      textTitle3: "申请人",
      authenticationStatus: [],
      testOrganization: [],
      industryType: [],
      textList1: [],
      textList2: [],
      textList3: [],
      applicant: [{ name: "我的申请", active: false }],
      placeholder: "请输入产品名称搜索内容",
      currentPage: 1,
      pageSize: 10,
      total: 0,
      inputValue: "",
      reviewStatus: [],
      productName: "",
      selectMyApplication: [],
      testOrgan: [],
      tableData: [],
      statusStr: [],
      productTypes: [],
      selections: [],
    };
  },
  created() {
    this.initializingProcessFn();

    setTimeout(() => {
      if (
        this.$store.state.useName.roleNames &&
        this.$store.state.useName.roleNames.includes("欧拉社区旗舰店")
      ) {
        this.getTableList();
      }
    }, 1000);
  },
  methods: {
    handleResult() {
      this.$refs.ruleform.validate((valid) => {
        if (valid) {
          let params = {
            dataIdList: this.dataIdList,
            handlerResult: this.ruleform.handlerResult,
            handlerComment: this.ruleform.handlerComment,
          };
          this.axios
            .post("/compatibleData/approvalCompatibleData", params)
            .then((response) => {
              if (response.data.code === 200) {
                this.getTableList();
                this.dialogVisible = false;
                this.$refs.ruleform.resetFields();
                this.$message.success("审核成功！");
              } else {
                this.$message.error(response.data.message);
              }
            });
        } else {
          return false;
        }
      });
    },
    handleReview() {
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.ruleform.resetFields();
      });
    },
    selectionChange(val) {
      this.dataIdList = val.map((item) => {
        return item.dataId;
      });
      this.selections = val;
    },
    initializingProcessFn() {
      this.authenticationStatus = JSON.parse(JSON.stringify(status));
      this.testOrganization = JSON.parse(JSON.stringify(testOrganization));
      this.industryType = JSON.parse(JSON.stringify(parentIndustryType));
      for (let i = 0; i < this.authenticationStatus.length; i++) {
        this.authenticationStatus[i] = {
          name: this.authenticationStatus[i],
          active: false,
        };
      }
      this.textList1 = JSON.parse(JSON.stringify(this.authenticationStatus));
      for (let i = 0; i < this.testOrganization.length; i++) {
        this.testOrganization[i] = {
          name: this.testOrganization[i],
          active: false,
        };
      }
      this.textList2 = JSON.parse(JSON.stringify(this.testOrganization));
      for (let i = 0; i < this.industryType.length; i++) {
        this.industryType[i] = {
          name: this.industryType[i],
          active: false,
        };
      }
      this.textList3 = JSON.parse(JSON.stringify(this.industryType));
    },
    inputChange(value) {
      this.currentPage = 1;
      this.productName = value;
      this.getTableList();
    },
    handleChange1(value) {
      this.statusStr = value.map((item) => {
        return item === "待审核"
          ? "审核中"
          : item === "通过"
          ? "已通过"
          : "已驳回";
      });
      this.currentPage = 1;
      this.getTableList();
    },
    handleChange2(value) {
      this.currentPage = 1;
      this.reviewStatus = value;
      this.getTableList();
    },
    handleChange3(value) {
      this.currentPage = 1;
      this.productTypes = value;
      this.getTableList();
    },
    getTableList() {
      let params = {
        curPage: this.currentPage,
        pageSize: this.pageSize,
        productName: this.productName,
        productTypes: this.productTypes,
        statusStr: this.statusStr,
      };
      this.axios
        .post("/compatibleData/findDataList", params)
        .then((response) => {
          if (response.data.code === 200) {
            this.tableData = response.data.result.data;
            this.total = response.data.result.total;
          } else {
            this.$message.error(response.data.message);
          }
        });
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getTableList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getTableList();
    },
    goDetails(row) {
      this.$router.push({
        path: "/compatibleDetailHuawei",
        query: {
          dataId: row.dataId,
        },
      });
    },
  },
};
</script>
<style lang="less" scoped>
.review {
  width: 128px;
  height: 48px;
  border: 1px solid #002fa7;
  color: #002fa7;
  text-align: center;
  line-height: 48px;
  margin: 24px 0;
  cursor: pointer;
}
.review1 {
  width: 128px;
  height: 48px;
  border: 1px solid #002fa7;
  color: #002fa7;
  text-align: center;
  line-height: 48px;
  margin: 24px 0;
  opacity: 0.4;
  cursor: not-allowed;
}
.processStatus {
  width: 72px;
  height: 24px;
  background: #52a3ff;
  border-radius: 2px;
  color: #fff;
  text-align: center;
}
.processStatusSuccess {
  width: 72px;
  height: 24px;
  background: #24ab36;
  border-radius: 2px;
  color: #fff;
  text-align: center;
}
.processStatusFail {
  width: 72px;
  height: 24px;
  background: #e32020;
  border-radius: 2px;
  color: #fff;
  text-align: center;
}
::v-deep .el-textarea .el-input__count {
  line-height: 12px;
  background: none;
}
::v-deep .el-textarea__inner {
  border-radius: 0;
  height: 126px;
}
::v-deep .el-radio__inner::after {
  width: 9px;
  height: 9px;
  background-color: #000;
}
::v-deep .el-radio__input.is-checked .el-radio__inner {
  border-color: #000;
  background: none;
}
::v-deep .el-radio__input.is-checked + .el-radio__label {
  color: #000;
}
.selecteds {
  display: flex;
  align-items: center;
  color: #000;
}
.pagination {
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.desc {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #000;
}
.buttons {
  display: flex;
  margin-top: 32px;
  .button1 {
    width: 144px;
    height: 48px;
    background: #000;
    font-size: 16px;
    color: #fff;
    text-align: center;
    line-height: 48px;
    cursor: pointer;
  }
  .button2 {
    margin-left: 19px;
    width: 144px;
    height: 48px;
    border: 1px solid #000;
    font-size: 16px;
    color: #000;
    text-align: center;
    line-height: 48px;
    cursor: pointer;
  }
}
.grid-box {
  margin-top: 16px;
}
::v-deep .grid-box .el-table th.el-table__cell {
  padding: 4px 0;
  color: #4e5865;
  background: none;
  font-size: 12px;
}
::v-deep .grid-box .el-table .el-table__cell {
  padding: 8px 0 !important;
  color: #000;
  font-size: 12px;
  .cell {
    padding: 0 8px !important;
    line-height: 12px;
    font-weight: 400;
  }
}
::v-deep
  .grid-box
  .el-table
  .el-table__header
  .el-table__cell:nth-of-type(2)
  .cell {
  border-left: 1px solid #dfe5ef;
  border-right: 1px solid #dfe5ef;
}
.choose-file {
  width: 66px;
  height: 24px;
  background: #000;
  color: #fff;
  line-height: 24px;
  text-align: center;
  margin: 0 auto;
}
.choose-file1 {
  width: 66px;
  height: 24px;
  background: #000;
  color: #fff;
  line-height: 24px;
  text-align: center;
  cursor: pointer;
}
.upload-box1 {
  display: flex;
  margin-top: 8px;
  justify-content: center;
}
::v-deep .el-upload-dragger {
  width: 496px;
  height: 240px;
}
::v-deep .el-dialog__header {
  font-size: 16px;
  color: #000;
  padding: 16px 0 16px 24px;
}
::v-deep .el-dialog__body {
  padding: 0 0 24px 0;
}
.dialog-title {
  font-size: 16px;
  width: 544px;
  height: 56px;
}
::v-deep .el-table th.el-table__cell > .cell {
  padding-left: 24px !important;
}
::v-deep .el-table .cell {
  padding-left: 24px !important;
}
::v-deep .search-status .el-input__inner {
  height: 48px;
  border-radius: 0px;
}
::v-deep .search-status .el-select {
  margin-left: 0px !important;
}
::v-deep .product-name .el-input__inner {
  height: 48px;
  border-radius: 0px;
}
.box {
  margin-top: 40px;
  display: flex;
  justify-content: space-between;
}
.upload-box {
  width: 196px;
  height: 48px;
  border: 1px solid #002fa7;
  display: flex;
  align-items: center;
  color: #002fa7;
  justify-content: center;
  line-height: 48px;
  cursor: pointer;
}
.title {
  font-size: 36px;
  font-weight: 300;
  text-align: center;
  margin-top: 48px;
}
.topMenu {
  width: 100%;

  height: 48px;
  background: #fdfefe;
  box-shadow: 0px 1px 4px 0px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
  div {
    height: 46px;
    line-height: 46px;
    text-align: center;
    font-size: 16px;
    margin-left: 40px;
    color: #555;
    cursor: pointer;
  }
  .active {
    color: #002fa7;
    border-bottom: 2px solid #002fa7;
  }
}
.certificationApplication {
  display: flex;
  flex-direction: column;
  align-items: center;
  .content {
    width: 146px;
    .myCertified {
      height: 136px;
      text-align: center;
      line-height: 136px;
      span {
        font-size: 36px;
      }
    }
    .processStatus {
      height: 24px;
      background: #52a3ff;
      border-radius: 2px;
      color: #fff;
      text-align: center;
    }
    .processStatusSuccess {
      height: 24px;
      background: #24ab36;
      border-radius: 2px;
      color: #fff;
      text-align: center;
    }
    .processStatusFail {
      height: 24px;
      background: #e32020;
      border-radius: 2px;
      color: #fff;
      text-align: center;
    }
    .edits {
      color: #002fa7;
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
.certificationApplication {
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
