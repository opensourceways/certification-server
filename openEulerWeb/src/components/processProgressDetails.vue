<template>
  <div class="processProgressDetails">
    <div class="modelTitle" style="margin-top: 0px">测评信息</div>
    <el-form ref="form" :model="form" label-width="150px" label-position="left">
      <div class="model">
        <el-form-item label="企业名称">{{ form.companyName }}</el-form-item>
        <el-form-item label="产品名称">{{ form.productName }}</el-form-item>
      </div>
      <div class="model">
        <el-form-item label="产品版本">{{ form.productVersion }}</el-form-item>
        <el-form-item label="OS名称">{{ form.osName }}</el-form-item>
      </div>
      <el-form-item label="OS版本">{{ form.osVersion }}</el-form-item>
      <div class="model">
        <el-form-item label="算力平台">{{
          form.platforms ? form.platforms.join("、") : form.platforms
        }}</el-form-item>
        <el-form-item label="产品类型">{{ form.productType }}</el-form-item>
      </div>
      <div class="model">
        <el-form-item label="测试机构">{{
          form.testOrganization
        }}</el-form-item>
        <el-form-item label="申请日期">{{ form.applicationTime }}</el-form-item>
      </div>
      <div class="model">
        <el-form-item label="产品功能介绍">{{
          form.productFunctionDesc
        }}</el-form-item>
        <el-form-item label="使用场景介绍">{{
          form.usageScenesDesc
        }}</el-form-item>
      </div>
    </el-form>
    <div class="modelTitle" style="float: left" v-loading="tableLoading">
      审核记录
    </div>
    <img
      :src="require('@/assets/images/topArrowhead.png')"
      alt=""
      style="margin-top: 45px; margin-left: 20px"
      @click="isAuditRecords = false"
      v-if="isAuditRecords"
      class="cursor"
    />
    <img
      :src="require('@/assets/images/bottomArrowhead.png')"
      alt=""
      style="margin-top: 45px; margin-left: 20px"
      @click="isAuditRecords = true"
      v-if="!isAuditRecords"
      class="cursor"
    />
    <div class="clear"></div>
    <el-table :data="tableData" style="width: 100%" v-if="isAuditRecords">
      <el-table-column prop="nodeName" width="180">
        <template slot="header">
          <div style="display: flex; align-items: center">
            <span style="margin-right: 10px">审核节点</span>
            <el-draopdown @command="handleCommand">
              <img
                :src="nodeName === '' ? src : src1"
                alt=""
                style="margin-top: 8px"
                class="el-dropdown-link"
              />
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  v-for="(item, index) in filtersArr"
                  :key="index"
                  :command="item.value"
                  >{{ item.text }}</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-draopdown>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="handlerName" label="处理人" width="180">
      </el-table-column>
      <el-table-column prop="handlerResult" label="审核结果"> </el-table-column>
      <el-table-column prop="transferredComments" label="审核意见">
      </el-table-column>
      <el-table-column prop="handlerTime" label="完成时间"> </el-table-column>
    </el-table>
    <div
      style="display: flex; justify-content: center; margin-top: 20px"
      v-if="isAuditRecords"
    >
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total,sizes,prev,pager,next,jumper"
        :total="total"
      ></el-pagination>
    </div>
    <div
      class="modelTitle"
      v-if="
        processProgress === '测试阶段' ||
        processProgress === '报告初审' ||
        processProgress === '报告复审' ||
        processProgress === '证书初审' ||
        processProgress === '证书确认' ||
        processProgress === '证书签发'
      "
    >
      测试报告
    </div>
    <el-form
      :rules="rules"
      class="test-report"
      ref="forma"
      :model="forma"
      label-width="150px"
      label-position="left"
      v-if="
        (processProgress === '测试阶段' && processStatus === '待处理') ||
        (processProgress === '报告初审' && processStatus === '已驳回')
      "
    >
      <el-form-item
        label="上传测试报告"
        prop="testReport"
        class="upload"
        v-if="$store.state.useName.username === handlerName"
      >
        <el-upload
          class="upload-demo"
          :action="textAction"
          :headers="headers()"
          :data="testData"
          :file-list="fileList"
          :show-file-list="false"
          :on-success="onSuccess"
          :before-upload="beforeUpload"
          multiple
          accept=".pdf,.docx,.xlsx,.zip"
        >
          <div class="cancels" style="margin: 0 20px 0 0">上传</div>
          <span slot="tip" class="el-upload__tip"
            >上传的文件格式限制：pdf、xls、xlsx、doc、docx、zip，单个文件大小不超过50M，最多上传5个文档。</span
          >
        </el-upload>
        <div
          v-for="(item, index) in fileTextName"
          :key="index"
          class="fileList"
          v-show="fileTextName.length > 0"
        >
          <span
            @click="goDown(item.fileId)"
            style="currsor: pointer; color: #002fa7"
            >{{ item.fileName }}</span
          >
          <i
            class="el-icon-close"
            @click="goDelete(item.fileId)"
            style="cursor: pointer; margin-left: 15px"
          ></i>
        </div>
      </el-form-item>
    </el-form>
    <div
      v-if="
        (processProgress === '报告初审' ||
          processProgress === '报告复审' ||
          processProgress === '证书初审' ||
          processProgress === '证书确认') &&
        (processStatus === '待处理' ||
          (processProgress === '证书确认' && processStatus === '已驳回'))
      "
    >
      <span
        class="fileName cursor"
        v-for="(item, index) in fileTextName"
        :key="index"
        @click="goDown(item.fileId)"
        >{{ item.fileName }}</span
      >
    </div>
    <div v-if="processProgress === '证书签发'">
      <span
        class="fileName cursor"
        v-for="(item, index) in fileTextName"
        :key="index"
        @click="goDown(item.fileId)"
        >{{ item.fileName }}</span
      >
    </div>
    <div class="modelTitle" v-if="processStatus === '已驳回'">审核</div>
    <el-form
      ref="formb"
      :model="formb"
      label-width="150px"
      label-position="left"
      v-if="processStatus === '已驳回'"
    >
      <el-form-item label="审核结果">已驳回</el-form-item
      ><el-form-item label="审核意见">{{
        reviewComments
      }}</el-form-item></el-form
    >
    <div
      class="modelTitle"
      v-if="processProgress === '证书初审' && processStatus === '待处理'"
    >
      证书信息
    </div>
    <span
      class="certificatesInfor"
      v-if="processProgress === '证书初审' && processStatus === '待处理'"
      >证书信息正在初审中！</span
    >
    <div
      class="modelTitle"
      v-if="
        (processProgress === '证书确认' && processStatus === '待处理') ||
        (processProgress === '证书签发' && processStatus === '已驳回')
      "
    >
      证书信息确认
    </div>
    <el-form
      ref="formc"
      :model="formc"
      :rules="rules"
      label-width="150px"
      label-position="left"
      v-if="
        (processProgress === '证书确认' && processStatus === '待处理') ||
        (processProgress === '证书签发' && processStatus === '已驳回')
      "
    >
      <el-form-item label="证书类型">{{ formc.certificateType }}</el-form-item
      ><el-form-item label="证书权益" class="certificateInterests">{{
        formc.certificateInterests
      }}</el-form-item>
      <el-form-item label="有效期">{{ formc.validityPeriod }}</el-form-item
      ><el-form-item
        label="上传签名"
        class="upload"
        v-if="$store.state.useName.username === handlerName"
      >
        <el-upload
          class="upload-demo"
          drag
          :action="signAction"
          :headers="headers()"
          :data="testDatasign"
          :file-list="signList"
          :show-file-list="false"
          :on-success="onSuccess1"
          :before-upload="beforeUploadSign"
          accept=".png"
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">
            将文件拖到此处，或
            <em>点击上传</em>
          </div>
          <div class="el-upload__tip" slot="tip">
            上传的图片格式要求白底黑字、png，不超过10M
          </div>
        </el-upload>
        <div
          v-for="(item, index) in signList"
          :key="index"
          class="fileList"
          v-show="signList.length > 0"
        >
          <span @click="goDown(item.fileId)" class="cursor">{{
            item.fileName
          }}</span>
          <i class="el-icon-close cursor" @click="goDelete(item.fileId)"></i>
        </div>
      </el-form-item>
      <div class="model cursor" style="margin-bottom: 40px" @click="getPreview">
        <img :src="require('@/assets/images/certificates.png')" alt="" />
        <span class="certificates">证书预览</span>
      </div>
      <el-form-item label="审核结果" prop="radio">
        <el-radio-group v-model="formc.radio">
          <el-radio :label="1">通过</el-radio>
          <el-radio :label="2">驳回</el-radio></el-radio-group
        >
      </el-form-item>
      <el-form-item label="审核意见" prop="input">
        <el-input
          placeholder="请输入内容"
          type="textarea"
          v-model.trim="formc.input"
          maxlength="1000"
          show-word-limit
        ></el-input>
      </el-form-item>
    </el-form>
    <div
      class="modelTitle"
      v-if="processProgress === '证书签发' && processStatus === '待处理'"
    >
      证书信息
    </div>
    <el-form
      ref="formc"
      :model="formc"
      :rules="rules"
      label-width="150px"
      label-position="left"
      v-if="processProgress === '证书签发' && processStatus === '待处理'"
    >
      <el-form-item label="证书类型">{{ formc.certificateType }}</el-form-item
      ><el-form-item label="证书权益" class="certificateInterests">{{
        formc.certificateInterests
      }}</el-form-item>
      <el-form-item label="有效期">{{ formc.validityPeriod }}</el-form-item
      ><el-form-item label="上传签名" class="upload">
        <img :src="this.signImage" alt="" style="width: 74px; height: 26px" />
      </el-form-item>
      <div class="model cursor" style="margin-bottom: 40px" @click="getPreview">
        <img :src="require('@/assets/images/certificates.png')" alt="" />
        <span class="certificates">证书预览</span>
      </div>
    </el-form>
    <div
      class="modelTitle"
      v-if="processProgress === '证书签发' && processStatus === '通过'"
    >
      测评证书
    </div>
    <span
      class="fileName cursor"
      v-if="processProgress === '证书签发' && processStatus === '通过'"
      @click="goDown(certification.fileId)"
      >{{ certification.fileName }}</span
    >
    <div
      class="operation"
      v-if="processProgress === '方案审核' && processStatus === '已驳回'"
    >
      <div
        class="submit cursor"
        @click="sybmitFn"
        v-if="$store.state.useName.username === handlerName"
      >
        重新提交
      </div>
      <div class="cancels cursor" @click="goBack">返回</div>
    </div>
    <div
      class="operation"
      v-if="
        (processProgress === '证书确认' && processStatus === '待处理') ||
        (processProgress === '证书签发' && processStatus === '已驳回')
      "
    >
      <div
        class="submit cursor"
        @click="submitCe(formc.radio, formc.input)"
        v-if="$store.state.useName.username === handlerName"
      >
        确认
      </div>
      <div class="cancels cursor" @click="goBack">返回</div>
    </div>
    <div
      class="operation"
      v-if="
        (processProgress === '测试阶段' && processStatus === '待处理') ||
        (processProgress === '报告初审' && processStatus === '已驳回')
      "
    >
      <div
        class="submit cursor"
        @click="submitCe(1, '通过')"
        v-if="$store.state.useName.username === handlerName"
      >
        提交
      </div>
      <div class="cancels cursor" @click="goBack">返回</div>
    </div>
    <div
      class="cancels cursor"
      @click="goBack"
      v-if="
        (processProgress === '方案审核' ||
          processProgress === '报告初审' ||
          processProgress === '报告复审' ||
          processProgress === '证书初审') &&
        processStatus === '待处理'
      "
    >
      返回
    </div>
    <div
      class="back cursor"
      @click="goBack"
      v-if="
        (processProgress === '证书签发' && processStatus === '通过') ||
        (processProgress === '证书签发' && processStatus === '待处理')
      "
    >
      <span>返回</span>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    processList: {
      type: Array,
      value: [],
    },
    id: {
      type: String,
      value: "",
    },
    processProgress: {
      type: String,
      value: "",
    },
    processStatus: {
      type: String,
      value: "",
    },
    handlerName: {
      type: String,
      value: "",
    },
  },
  data() {
    return {
      flag: true,
      src: require("@/assets/images/screening.png"),
      src1: require("@/assets/images/screeingColor.png"),
      testReport: [],
      form: {},
      forma: {},
      formb: {},
      formc: { radio: 1 },
      tableData: [],
      fileList: [],
      reviewComments: "",
      testData: [],
      fileTextName: [],
      certification: "",
      testDatasign: {},
      signList: [],
      signImage: "",
      certificatesImg: "",
      code: "",
      currentPage: 1,
      pageSize: 10,
      total: 0,
      tableLoading: true,
      filtersArr: [
        { text: "全部", value: "" },
        { text: "测评申请", value: "测评申请" },
        { text: "方案审核", value: "方案审核" },
        { text: "测试阶段", value: "测试阶段" },
        { text: "报告初审", value: "报告初审" },
        { text: "报告复审", value: "报告复审" },
        { text: "证书初审", value: "证书初审" },
        { text: "证书确认", value: "证书确认" },
        { text: "证书签发", value: "证书签发" },
      ],
      nodeName: "",
      rules: {
        radio: [
          { required: true, message: "请选择审核结果", trigger: "change" },
        ],
        input: [{ required: true, message: "请输入审核意见", trigger: "blur" }],
        testReport: [
          { required: true, message: "请上传测试报告", trigger: "blur" },
        ],
      },
      isAuditRecords: false,
      textAction: this.axios.defaults.baseURL + "/software/upload",
      signAction: this.axios.defaults.baseURL + "/software/upload",
    };
  },
  mounted() {
    this.getfindById();
    this.getRecordsList();
    this.getAttachments("testReport");
    this.getAttachments("certificates");
    this.getAttachments("sign");
    this.getCertificateInfor();
    this.testData = {
      softwareId: this.id,
      fileTypeCode: 1,
      fileType: "testReport",
    };
    this.testDatasign = {
      softwareId: this.id,
      fileTypeCode: 2,
      fileType: "sign",
    };
  },
  methods: {
    headers() {
      const result = {
        "x-xsrf-token": this.getCookie("XSRF-TOKEN"),
        Authorization: localStorage.getItem("user"),
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
    downloadTestReport(fileId) {
      this.axios
        .get(`software/downloadAttachments?fileId=${fileId}`)
        .then(() => {
          this.$confirm(
            "文件由外部用户上传，下载后建议安全扫描后谨慎打开",
            "提示",
            {
              confirmButtonText: "确定",
              cancelButtonText: "取消",
              type: "warning",
              beforeClose: (action, instance, done) => {
                if (action === "confirm") {
                  this.downHandler(
                    `/software/downloadAttachments?fileId=${fileId}`
                  );
                }
                done();
              },
            }
          );
        });
    },
    beforeUpload(file) {
      if (this.fileTextName.length === 5) {
        this.$message.error("上传文件不能超过5个");
        return false;
      }
      const isLt2M = file.size / 1024 / 1024 < 50;
      if (!isLt2M) {
        this.$message.error("上传文件大小不能超过50MB");
        return false;
      } else if (file.size === 0) {
        this.$message.error("上传文件大小需超过0B");
        return false;
      }
    },
    beforeUploadSign(file) {
      if (this.signList.length === 1) {
        this.$message.error("上传文件不能超过1个");
        return false;
      }
      const isLt2M = file.size / 1024 / 1024 < 10;
      if (!isLt2M) {
        this.$message.error("上传文件大小不能超过10MB");
        return false;
      } else if (file.size === 0) {
        this.$message.error("上传文件大小需超过0B");
        return false;
      }
    },
    onSuccess(response) {
      if (response.code === 400) {
        this.$message({
          message: response.message,
          type: "error",
        });
      } else {
        this.$message({
          message: "上传成功",
          type: "success",
        });
      }
      this.getAttachments("testReport");
    },
    onSuccess1() {
      this.$message({
        message: "上传成功",
        type: "success",
      });
      this.getAttachments("sign");
    },
    handleCommand(command) {
      this.nodeName = command;
      this.currentPage = 1;
      this.isAuditRecords = false;
      this.getRecordsList();
      this.$forceUpdate();
    },
    getfindById() {
      this.axios
        .get("/software/findById", {
          params: {
            id: this.id,
          },
        })
        .then((res) => {
          this.form = res.data.result;
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    getRecordsList() {
      this.tableLoading = true;
      this.axios
        .get("/software/auditRecordsList", {
          params: {
            softwareId: this.id,
            curPage: this.currentPage,
            pageSize: this.pageSize,
            nodeName: this.nodeName,
          },
        })
        .then((res) => {
          this.tableData = res.data.result.records;
          if (res.data.result.records.length >= 2) {
            this.reviewComments = res.data.result.records
              ? res.data.result.records[1].transferredComments
              : "";
          }
          this.isAuditRecords = true;
          this.tableLoading = false;
          this.total = res.data.result.total;
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
          this.isAuditRecords = true;
          this.tableLoading = false;
        });
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getRecordsList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getRecordsList();
    },
    submitCe(type, commet) {
      if (this.flag) {
        this.flag = false;
        let params = {
          handlerResult: type,
          softwareId: this.id,
          transferredComments: commet,
          transferredUser: "",
        };
        this.axios.post("/software/processReview", params).then((response) => {
          if (response.data.code === 200) {
            this.$router.push("/");
          } else {
            this.$message.error(response?.data?.message);
          }
          this.flag = true;
        });
      }
    },
    getAttachments(type) {
      this.axios
        .get("/software/getAttachments", {
          params: {
            softwareId: this.id,
            fileType: type,
          },
        })
        .then((res) => {
          if (type === "testReport") {
            this.fileTextName = res.data.result;
            this.fileList = res.data.result;
          }
          if (type === "certificates") {
            this.certification = res.data.result ? res.data.result[0] : [];
          }
          if (type === "sign") {
            this.signList = res.data.result.length != 0 ? res.data.result : [];
            if (res.data.result.length != 0) {
              this.getImagePreview(this.signList[0].fileId);
            }
          }
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    getImagePreview(id) {
      this.axios
        .get("/software/imagePreview", {
          params: {
            fileId: id,
          },
          responseType: "blob",
        })
        .then((res) => {
          this.signImage = window.URL.createObjectURL(res.data);
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    getCertificateInfor() {
      this.axios
        .get("/software/certificateInfo", {
          params: {
            softwareId: this.id,
          },
        })
        .then((res) => {
          this.formc.certificateType = res.data.result
            ? res.data.result.certificateType
            : "";
          this.formc.certificateInterests = res.data.result
            ? res.data.result.certificateInterests
            : "";
          this.formc.validityPeriod = res.data.result
            ? res.data.result.validityPeriod
            : "";
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    goDown(id) {
      this.$confirm(
        "文件由外部用户上传，下载后建议安全扫描后谨慎打开",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
          beforeClose: (action, instance, done) => {
            if (action === "confirm") {
              this.downHandler(`/software/downloadAttachments?fileId=${id}`);
            }
            done();
          },
        }
      );
    },
    getPreview() {
      this.axios
        .get("/software/previewCertificate", {
          params: {
            softwareId: this.id,
          },
          responseType: "blob",
        })
        .then((res) => {
          this.certificatesImg = window.URL.createObjectURL(res.data);
          this.goCertificate();
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    goCertificate() {
      let test = window.open(this.certificatesImg);
      setTimeout(() => {
        test.document.title =
          this.form.productName + " " + this.form.productVersion + ".pdf";
      }, 100);
    },
    goDelete(id) {
      this.axios
        .delete("/software/deleteAttachments", {
          params: {
            fileId: id,
          },
        })
        .then(() => {
          this.getAttachments("testReport");
          this.getAttachments("sign");
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    sybmitFn() {
      this.$router.push({
        path: "/technicalCertification",
        query: {
          id: this.id,
        },
      });
    },
    goBack() {
      this.$router.push({
        path: "/",
      });
    },
  },
};
</script>
<style lang="less" scoped>
.processProgressDetails {
  background: #fff;
  padding: 40px 48px;
  .modelTitle {
    font-size: 20px;
    color: #000;
    margin-bottom: 24px;
    margin-top: 40px;
  }
  .model {
    display: flex;
    justify-content: flex-start;
    .certificates {
      color: #002fa7;
      margin-left: 8px;
      font-size: 16px;
    }
  }
  .fileList {
    display: inline-block !important;
    min-width: 300px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .cancels {
    width: 142px;
    height: 46px;
    border: 1px solid #000;
    line-height: 48px;
    text-align: center;
    margin-top: 20px;
  }
  .clear {
    clear: both;
  }
  .back {
    width: 142px;
    height: 46px;
    border: 1px solid #000;
    line-height: 48px;
    text-align: center;
    margin-top: 20px;
  }
  .fileName {
    color: #002fa7;
    font-size: 12px;
    margin-right: 20px;
  }
  .certificatesInfor {
    color: #4f5661;
    font-size: 14px;
  }
  .operation {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    width: 410px;
    line-height: 48px;
    margin-top: 40px;
    .cancels {
      width: 142px;
      height: 46px;
      border: 1px solid #000;
      margin-top: 0px;
      text-align: center;
    }
    .submit {
      width: 144px;
      height: 48px;
      border: 1px solid #000;
      color: #fff;
      margin-right: 23px;
      text-align: center;
    }
  }
}
</style>
<style lang="less">
.processProgressDetails {
  .el-form-item {
    width: 400px;
    margin-bottom: 16px;
  }
  .test-report .el-form-item__content {
    display: flex;
    flex-direction: column;
  }
  .certificateInterests {
    width: 450px !important;
  }
  .upload {
    width: 100%;
  }
  .el-textarea {
    width: 400px;
    .el-textarea__inner {
      height: 126px;
      border-radius: 0px;
    }
  }
}
</style>
