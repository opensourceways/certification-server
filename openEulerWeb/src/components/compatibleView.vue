<template>
  <div class="content">
    <div class="title">兼容性数据管理</div>
    <div class="box">
      <div
        class="upload-box"
        @click="
          dialogVisible = true;
          handleDel();
        "
      >
        <span>上传兼容性数据</span
        ><i style="margin-left: 8px" class="el-icon-right"></i>
      </div>
      <div style="display: flex">
        <div style="width: 160px; height: 48px" class="search-status">
          <el-select
            @change="handelSearch()"
            v-model="status"
            multiple
            collapse-tags
            style="margin-left: 20px"
            placeholder="状态选择"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item"
              :label="item"
              :value="item"
            ></el-option>
          </el-select>
        </div>
        <div style="margin-left: 13px" class="product-name">
          <el-input
            @change="handelSearch()"
            v-model="productName"
            placeholder="请输入产品名称搜索内容"
            prefix-icon="el-icon-search"
          >
          </el-input>
        </div>
      </div>
    </div>
    <el-table :data="tableData" style="width: 100%; margin-top: 24px">
      <el-table-column
        prop="productName"
        label="测试产品名称"
        show-overflow-tooltip
        width="160"
      >
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
        prop="severPlatform"
        label="硬件算力平台"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        prop="severSupplier"
        label="硬件厂商"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        prop="severModel"
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
      <el-table-column label="操作" width="80px">
        <template slot-scope="scoped">
          <span class="edits cursor" @click="goDetails(scoped.row)">{{
            查看
          }}</span>
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
    <el-dialog
      title="上传兼容性数据"
      :visible.sync="dialogVisible"
      width="544px"
    >
      <div v-if="fileList.length === 0">
        <div style="margin-left: 24px; margin-top: 16px; font-size: 12px">
          点击<span
            style="color: #002fa7; cursor: pointer"
            @click="handleDownload"
            >下载材料模板</span
          >
        </div>
        <div class="upload-box1">
          <el-upload
            :header="headers()"
            :before-upload="handleBeforeUpload"
            class="uppload-demo"
            :show-file-list="false"
            drag
            :action="axios.defaults.baseURL + '/compatibleData/uploadTemplate'"
            on-success="handleSuccess"
          >
            <i style="color: #8d98aa" class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或</div>
            <div>
              <div class="choose-file" style="margin-top: 30px">选择文件</div>
            </div>
          </el-upload>
        </div>
      </div>
      <div v-else>
        <div style="padding: 24px 24px 0 24px">
          <el-upload
            :header="headers()"
            :before-upload="handleBeforeUpload"
            class="uppload-demo"
            :show-file-list="false"
            :action="axios.defaults.baseURL + '/compatibleData/uploadTemplate'"
            on-success="handleSuccess"
          >
            <div class="choose-file1">选择文件</div>
          </el-upload>
          <div class="grid-box">
            <el-table :data="fileList">
              <el-table-column
                property="fileName"
                label="文件名"
                width="150"
                :show-overflow-tooltip="true"
              ></el-table-column>
              <el-table-column
                property="fileSize"
                label="大小"
                width="200"
                :show-overflow-tooltip="true"
              ></el-table-column>
              <el-table-column property="address" label="操作">
                <span style="color: #002fa7; cursor: pointer" @click="handleDel"
                  >删除</span
                >
              </el-table-column>
            </el-table>
          </div>
          <div style="margin-top: 32px">
            <el-checkbox
              v-model="checked"
              @change="handleChangeSign"
            ></el-checkbox
            ><span style="color: #606266; margin-left: 10px"
              >提交上传表明您同意</span
            >
            <router-link
              :to="{ path: '/compatibilityChecklist' }"
              target="_blank"
              ><span style="color: #002fa7; cursor: pointer"
                >《兼容性清单使用声明》</span
              ></router-link
            >
          </div>
          <div class="butttons">
            <div class="button1" v-if="checked" @click="handleUpload">确定</div>
            <div
              class="button1"
              v-else
              style="background: #555; color: #fff; cursor: not-allowed"
            >
              确定
            </div>
            <div class="button2" @click="dialogVisible = false">取消</div>
          </div>
        </div>
      </div>
    </el-dialog>

    <el-dialog
      title="上传结果提示"
      :visible.sync="dialogVisible2"
      width="544px"
    >
      <div>
        <div style="padding: 24px 24px 0 2px">
          <div class="desc">
            <img src="../assets/images/success.png" alt="" /><span
              style="margin-left: 10px"
              >成功上传{{ successRows }}条数据</span
            >
          </div>
          <div class="desc" style="margin-top: 21px">
            <img src="../assets/images/error.png" alt="" /><span
              :title="failedData.join(',')"
              class="uploadDesc"
              style="margin-left: 10px"
            >
              上传失败{{ failedRows }}条数据<span v-if="failedData.length > 0"
                >,失败行数：{{ failedData.join("、") }}</span
              >
            </span>
          </div>
          <div
            v-if="repeatData.length > 0"
            style="margin-left: 34px; margin-top: 10px"
            :title="repeatData.join(',')"
            class="uploadDesc"
          >
            重复数据：{{ repeatData.join("、") }}
          </div>
          <div class="buttons">
            <div class="button1" @click="dialogVisible2 = false">确定</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {
  authenticationStatus,
  testOrganization,
  industryType,
} from "@/assets/js/publicData.js";
export default {
  data() {
    return {
      repeatRows: null,
      repeatData: [],
      failedData: [],
      failedRows: null,
      successRows: null,
      dialogVisible2: false,
      checked: false,
      fileList: [],
      nolist: false,
      dialogVisible: false,
      productName1: null,
      status: null,
      statusOptions: ["审核中", "已通过", "已驳回"],
      isactive: "true",
      title: "测评申请",
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
      placeholder: "请输入产品名称搜索内容",
      currentPage: 1,
      pageSize: 10,
      total: 0,
      inputValue: "",
      reviewStatus: [],
      productName: "",
      testOrgan: [],
      tableData: [],
    };
  },
  created() {
    this.initializingProcessFn();

    setTimeout(() => {
      if (
        this.$store.state.useName.roleName &&
        this.$store.state.useName.roleName.includes("OSV伙伴")
      ) {
        this.getTableList();
      }
    }, 1000);
  },
  methods: {
    headers() {
      const result = {
        "x-xsrf-token": this.getCookie("XSRF-TOKEN"),
        Authorizetion: localStorage.getItem("user"),
      };
      return result;
    },
    getCookie(name) {
      const patten = new RegExp(name + "=([^;]*)");
      const matches = document.cookie.match(patten);
      if (matches) {
        return matches[1];
      }
      return null;
    },
    handleChangeSign(val) {
      if (val) {
        this.axios
          .put("/user/signCompatibilityAgreement")
          .then(() => {})
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
      } else {
        this.axios
          .put("/user/cancelCompatibilityAgreement")
          .then(() => {})
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
      }
    },
    async downHandler(url) {
      const res = await this.axios.get(url, { responseType: "blob" });
      const blob = new Blob([res.data], { type: "application/octet-stream" });
      let imgBase64 = URL.createObjectURL(blob);
      const disposition = decodeURI(
        res.headers["content-disposition"]
          .replaceAll("+", "%20")
          .replaceAll("%2B", "+")
      );
      const fileName = disposition.split("filename=")[1];
      const a = document.createElement("a");
      a.href = imgBase64;
      a.download = fileName;
      document.body.appendChild(a);
      a.click();
      a.remove();
    },
    handleSearch() {
      this.currentPage = 1;
      this.getTableList();
    },
    handleDownload() {
      this.$confirm(
        "文件由外部用户上传，下载后建议安全扫描后谨慎打开",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
          beforeClose: (action, instance, done) => {
            if (action === "confirm") {
              this.downHandler(`/compatibleData/download`);
            }
            done();
          },
        }
      );
    },
    handleUpload() {
      const _this = this;
      this.axios
        .get("/compatibleData/upload", {
          params: {
            fileId: _this.fileList[0].fileId,
          },
        })
        .then((response) => {
          if (response.data.code === 200) {
            this.dialogVisible = false;
            this.currentPage = 1;
            this.getTableList();
            setTimeout(() => {
              this.dialogVisible2 = true;
            }, 1000);
            this.failedData = response.data.result.failedData;
            this.failedRows = response.data.result.failedRows;
            this.successRows = response.data.result.successRows;
            this.repeatData = response.data.result.repeatData;
            this.repeatRows = response.data.result.repeatRows;
          } else {
            this.$message.error(response.data.message);
          }
        });
    },
    handleDel() {
      this.fileList = [];
      this.checked = false;
    },
    handleBeforeUpload(data) {
      const type = data.name.split(".");
      const typeAfter = type[type.length - 1];
      if (typeAfter !== "xlsx" && typeAfter !== "xls") {
        this.$message({
          message: "请上传.xlsx .xls格式文件！",
          type: "warning",
        });
        return false;
      }
      if (this.fileList.length > 0) {
        this.$message({
          message: "文件只能上传一个",
          type: "warning",
        });
        return false;
      }
      if (data.size > 50 * 1024 * 1024) {
        this.$message({
          message: "文件大小不能超过50M!",
          type: "warning",
        });
        return false;
      }
    },
    handleSuccess(res) {
      if (res.code === 200) {
        this.fileList = [res.result];
      }
    },
    initializingProcessFn() {
      this.authenticationStatus = JSON.parse(
        JSON.stringify(authenticationStatus)
      );
      this.testOrganization = JSON.parse(JSON.stringify(testOrganization));
      this.industryType = JSON.parse(JSON.stringify(industryType));
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
    getTableList() {
      let params = {
        curPage: this.currentPage,
        pageSize: this.pageSize,
        productName: this.productName,
        statusStr: this.status,
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
        path: "/compatibleDetail",
        query: {
          dataId: row.dataId,
        },
      });
    },
  },
};
</script>
<style lang="less" scoped>
.uploadDesc{
  width: 450px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #000;
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
  width: 66x;
  height: 2px;
  background: #000;
  color: #fff;
  line-height: 24px;
  text-align: center;
  margin: 0 auto;
}
.choose-file1 {
  width: 66x;
  height: 2px;
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
