<template>
  <div class="processProgressDetailsHuawei">
    <div class="modelTitle" style="margin-top: 0px">测评信息</div>
    <el-form
      ref="form"
      :model="forms"
      label-width="150px"
      label-position="left"
    >
      <div class="model">
        <el-form-item label="企业名称">{{ forms.companyName }}</el-form-item>
        <el-form-item label="产品名称">{{ forms.productName }}</el-form-item>
      </div>
      <div class="model">
        <el-form-item label="产品版本">{{ forms.productVersion }}</el-form-item>
        <el-form-item label="OS名称">{{ forms.osName }}</el-form-item>
      </div>
      <div class="model">
        <el-form-item label="OS版本">{{ forms.osVersion }}</el-form-item>
      </div>
      <div class="model">
        <el-form-item label="算力平台">
          <span
            style="display: inline-block"
            v-for="item in forms.platforms"
            :key="item"
            >{{ item }}</span
          >
        </el-form-item>
        <el-form-item label="产品类型">{{ forms.productType }}</el-form-item>
      </div>
      <div class="model">
        <el-form-item label="测试机构">{{
          forms.testOrganization
        }}</el-form-item>
        <el-form-item label="申请日期">{{
          forms.applicationTime
        }}</el-form-item>
      </div>
      <div class="model">
        <el-form-item label="产品功能介绍">{{
          forms.productFunctionDesc
        }}</el-form-item>
        <el-form-item label="使用场景介绍">{{
          forms.usageScenesDesc
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
    <el-pagination
      class="pagination"
      v-if="isAuditRecords"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[10, 20, 30, 40]"
      :page-size="pageSize"
      layout="total,sizes,prev,pager,next,jumper"
      :total="total"
    ></el-pagination>
    <div
      class="modelTitle"
      v-if="
        node === '测试阶段' ||
        node === '报告初审' ||
        node === '' ||
        node === '报告复审' ||
        node === '证书初审' ||
        node === '证书确认' ||
        node === '证书签发'
      "
    >
      测试报告
    </div>
    <span class="certificatesInfor" v-if="node === '测试阶段'"
      >正在进行测试阶段，待伙伴上传测试报告</span
    >
    <span
      class="fileName"
      v-if="
        node === '报告初审' ||
        node === '' ||
        node === '报告复审' ||
        node === '证书初审' ||
        node === '证书确认' ||
        node === '证书签发'
      "
    >
      <span
        class="cursor"
        v-for="item in testReport"
        :key="item.fileId"
        @click="downloadTestReport(item.fileId)"
        >{{ item.fileName }}&nbsp;&nbsp;</span
      >
    </span>
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
        transferredComments
      }}</el-form-item></el-form
    >
    <div class="modelTitle" v-if="node === '证书确认'">证书信息</div>
    <el-form
      ref="form"
      v-if="node === '证书确认'"
      :model="information"
      label-width="150px"
      label-position="left"
    >
      <el-form-item label="企业名称">{{
        information.companyName
      }}</el-form-item>
      <el-form-item label="产品名称">{{
        information.productName
      }}</el-form-item>
      <el-form-item label="产品版本">{{
        information.productVersion
      }}</el-form-item>
      <el-form-item label="OS名称">{{ information.osName }}</el-form-item>
      <el-form-item label="OS版本">{{ information.osVersion }}</el-form-item>
      <el-form-item label="算力平台">
        <span v-for="item in information.platforms" :key="item">{{
          item
        }}</span>
      </el-form-item>
    </el-form>
    <div class="modelTitle" v-if="node === '证书签发'">证书信息</div>
    <el-form
      ref="form"
      v-if="node === '证书签发'"
      :model="information"
      label-width="150px"
      label-position="left"
    >
      <div class="model">
        <el-form-item label="企业名称">{{
          information.companyName
        }}</el-form-item>
        <el-form-item label="证书类型">{{
          information.certificateType
        }}</el-form-item>
      </div>
      <div class="model">
        <el-form-item label="产品名称">{{
          information.productName
        }}</el-form-item>
        <el-form-item label="证书权益">{{
          certificateInfo.certificateInterests
        }}</el-form-item>
      </div>
      <div class="model">
        <el-form-item label="产品版本">{{
          information.productVersion
        }}</el-form-item>
        <el-form-item label="有效期">{{
          certificateInfo.validityPeriod
        }}</el-form-item>
      </div>
      <div class="model">
        <el-form-item label="os名称">{{ information.osName }}</el-form-item>
        <el-form-item label="上传签名"
          ><img :src="this.signImage" alt="" class="signImage" />
        </el-form-item>
      </div>
      <div class="model">
        <el-form-item label="os版本">{{ information.osVersion }}</el-form-item>
        <el-form-item label="算力平台">
          <span
            style="display: inline-block"
            v-for="item in information.platforms"
            :key="item"
            >{{ item }}</span
          >
        </el-form-item>
      </div>
    </el-form>
    <div
      class="model cursor"
      style="margin-bottom: 40px"
      @click="goCertificateImg"
      v-if="node === '证书签发'"
    >
      <img :src="require('@/assets/images/certificates.png')" alt="" />
      <span class="certificates">证书预览</span>
    </div>
    <div
      class="modelTitle"
      v-if="
        (node === '方案审核' ||
          node === '报告初审' ||
          node === '报告复审' ||
          node === '证书签发') &&
        $store.state.useName.username == handlerName
      "
    >
      审核
    </div>
    <el-form
      :rules="rules"
      ref="form2"
      v-if="
        (node === '方案审核' ||
          node === '报告初审' ||
          node === '报告复审' ||
          node === '证书签发') &&
        $store.state.useName.username == handlerName
      "
      :model="form"
      label-width="150px"
      label-position="left"
    >
      <el-form-item label="审核结果" prop="handlerResult">
        <el-radio-group v-model="form.handlerResult">
          <el-radio :label="1">通过</el-radio>
          <el-radio :label="2">驳回</el-radio>
          <el-radio :label="3" @change="handleResult">转审</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item
        class="de"
        label="审核意见"
        prop="transferredComments"
        v-if="form.handlerResult != 3"
      >
        <el-input
          placeholder="请输入内容"
          type="textarea"
          v-model="form.transferredComments"
          maxlength="1000"
          show-word-limit
          @input="transferredCommentsChange"
        ></el-input>
      </el-form-item>
      <el-form-item
        class="de"
        label="转审人员"
        prop="transferredUser"
        v-if="form.handlerResult == 3"
      >
        <el-select v-model="form.transferredUser" placeholder="请选择"
          ><el-option
            v-for="item in options"
            :key="item.uuid"
            :label="item.username"
            :value="item.uuid"
          ></el-option
        ></el-select> </el-form-item
      ><el-form-item label>
        <div class="operation">
          <div class="submit cursor" @click="processReciew">确定</div>
          <div class="cancels cursor" @click="$router.back(-1)">取消</div>
        </div>
      </el-form-item></el-form
    >
    <div class="modelTitle" v-if="node === '证书初审'">证书信息确认</div>
    <el-form
      :rules="rules"
      ref="form"
      v-if="node === '证书初审'"
      :model="information"
      label-width="150px"
      label-position="left"
    >
      <el-form-item label="企业名称">
        <span class="cerInfor">{{
          information.companyName
        }}</span></el-form-item
      >
      <el-form-item label="产品名称"
        ><span class="cerInfor">{{
          information.productName
        }}</span></el-form-item
      >
      <el-form-item label="产品版本" prop="productVersion"
        ><el-input
          v-model="information.productVersion"
          placeholder="请输入内容"
          type="text"
          maxlength="64"
        ></el-input
      ></el-form-item>
      <el-form-item label="OS名称" prop="osName"
        ><el-select
          v-model="information.osName"
          placeholder="请选择"
          @change="getOsVersion"
          ><el-option
            v-for="item in allOs"
            :key="item.value"
            :label="item.osName"
            :value="item.osName"
          ></el-option></el-select
      ></el-form-item>
      <el-form-item label="OS版本" prop="osVersion"
        ><el-select v-model="information.osVersion" placeholder="请选择"
          ><el-option
            v-for="item in osVersions"
            :key="item"
            :label="item"
            :value="item"
          ></el-option></el-select
      ></el-form-item>
      <el-form-item label="算力平台" required>
        <div
          class="flat"
          v-for="(item, index) in information.hashratePlatformList"
          :key="index"
        >
          <el-select
            class="select"
            v-model="item.platformName"
            placeholder="请选择算力平台"
            @change="(name) => changeFlatform(name, index)"
            ><el-option
              v-for="(item, index) in computingPlatform"
              :key="index"
              :label="item.platformName"
              :value="item.platformName"
            ></el-option
          ></el-select>
          <el-select
            class="select"
            v-model="item.serverProvider"
            placeholder="选择硬件厂家"
            @change="(name) => changeServerProvider(name, index)"
            ><el-options
              v-for="item in providerAndServerType[index]"
              :key="item.serverProvider"
              :label="item.serverProvider"
              :value="item.serverProvider"
            ></el-options
          ></el-select>
          <el-select
            multiple
            class="select select1"
            v-model="item.serverTypes"
            placeholder="选择硬件型号"
            collapse-tags
            ><el-options
              v-for="item in serverType[index]"
              :key="item"
              :label="item"
              :value="item"
            ></el-options
          ></el-select>
        </div>
      </el-form-item>
      <el-form-item label>
        <div class="model cursor" @click="goCertificateImg">
          <img
            style="width: 24px; height: 24px"
            :src="require('@/assets/images/certificates.png')"
            alt=""
          />
          <span class="certificates">证书预览</span>
        </div>
      </el-form-item>
      <el-form-item label>
        <div
          class="operation"
          v-if="$store.state.useName.username === handlerName"
        >
          <div class="submit cursor" @click="sybmitFn">确认</div>
          <div class="cancels cursor" @click="$router.back(-1)">取消</div>
        </div>
      </el-form-item>
    </el-form>
    <div class="modelTitle" v-if="node === ''">测评证书</div>
    <span class="fileName" v-if="node === ''"
      ><span
        class="cursor"
        v-for="item in certificates"
        :key="item.fileName"
        @click="downloadCertificates(item.fileId)"
        >{{ item.fileName }}</span
      ></span
    >
    <div
      class="model cursor"
      style="margin-bottom: 40px"
      @click="goCertificateImg"
      v-if="node === '证书确认'"
    >
      <img :src="require('@/assets/images/certificates.png')" alt="" />
      <span class="certificates">证书预览</span>
    </div>
    <div
      class="cancels cancels2 cursor"
      v-if="
        node === '测试阶段' ||
        node === '证书确认' ||
        node === '' ||
        node === '测评申请' ||
        $store.state.useName.username != handlerName
      "
      @click="$router.back(-1)"
    >
      返回
    </div>
  </div>
</template>
<script>
export default {
  props: [
    "handlerName",
    "certificates",
    "forms",
    "titleList",
    "node",
    "softwareId",
    "testReport",
  ],
  data() {
    return {
      src: require("@/assets/images/screening.png"),
      src1: require("@/assets/images/screeingColor.png"),
      command: "",
      transferredComments: "",
      processStaus: "",
      formb: {},
      isAuditRecords: true,
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 1,
      rules: {
        productVersion: [
          { required: true, message: "请输入产品版本", trigger: "blur" },
        ],
        osName: [
          { required: true, message: "请选择Os名称", trigger: "change" },
        ],
        osVersion: [
          { required: true, message: "请选择Os版本", trigger: "change" },
        ],
        hashratePlatformList: {
          platformName: [{ required: true, trigger: "change" }],
          serverProvider: [{ required: true, trigger: "change" }],
          serverTypes: [{ required: true, trigger: "change" }],
        },
        handlerResult: [
          { required: true, message: "请选择审核结果", trigger: "change" },
        ],
        transferredComments: [
          { required: true, message: "请输入审核意见", trigger: "blur" },
        ],
        transferredUser: [
          { required: true, message: "请选择转审人员", trigger: "blur" },
        ],
      },
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
      certificate: [],
      certificatesImg: "",
      signImage: "",
      sign: [],
      certificateInfo: {},
      serverTypes: [],
      providerAndServerTypes: [],
      computingPlatform: [],
      osVersions: [],
      allOs: "",
      information: {
        companyName: "",
        hashratePlatformList: [
          {
            platformName: "",
            serverProvider: "",
            serverTypes: [],
          },
        ],
        osName: "",
        osVersion: "",
        productFunctionDesc: "",
        productName: "",
        productType: "",
        productVersion: "",
        testOrganization: "",
        usageScenesDesc: "",
      },
      form: {
        handlerResult: 1,
        softwareId: 21,
        transferredComments: "",
        transferredUser: "",
      },
      processProgress: "方案审核",
      options: [],
      dialogTableVisible: false,
    };
  },
  created() {
    this.getInformation();
    this.getauditRecordsList();
    this.getCertificateInfor();
    setTimeout(() => {
      if (this.node === "证书初审") {
        this.getAllOs();
        this.getComputingPlatform();
      }
      if (
        this.node === "方案审核" ||
        this.node === "报告初审" ||
        this.node === "报告复审" ||
        this.node === "证书签发"
      ) {
        this.getTransferredUserList();
      }
      if (this.node === "证书签发") {
        this.getSign();
      }
    }, 1000);
    this.form.softwareId = this.softwareId;
  },
  methods: {
    handleResult() {},
    transferredCommentsChange(value) {
      this.form.transferredComments = value
        .split(" ")
        .join("")
        .split("\n")
        .join("");
    },
    handleCommand(command1) {
      this.command = command1;
      this.nodeName = command1;
      this.currentPage = 1;
      this.isAuditRecords = false;
      this.getauditRecordsList();
      this.$forceUpdate();
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getauditRecordsList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getauditRecordsList();
    },
    getauditRecordsList() {
      this.tableLoading = true;
      this.axios
        .get("/software/auditRecordsList", {
          params: {
            softwareId: this.softwareId,
            curPage: this.currentPage,
            pageSize: this.pageSize,
            nodeName: this.nodeName,
          },
        })
        .then((res) => {
          this.tableData = res.data.result.records;
          this.total = res.data.result.total;
          if (
            this.tableData[1] &&
            this.tableData[1].handlerResult == "已驳回"
          ) {
            this.processStaus = "已驳回";
            this.transferredComments = this.tableData[1].transferredComments;
          }
          this.isAuditRecords = true;
          this.tableLoading = false;
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
          this.isAuditRecords = true;
          this.tableLoading = false;
        });
    },
    goCertificateImg() {
      if (
        this.$store.state.useName.username == this.handlerName &&
        this.node == "证书初审"
      ) {
        let flag = true;
        this.information.hashratePlatformList.forEach((item) => {
          if (item.serverTypes.length == 0) {
            flag = false;
          }
        });
        this.$refs.form.validate((valid) => {
          if (valid && flag) {
            let params = {
              softwareId: this.softwareId,
              hashratePlatformList: this.information.hashratePlatformList,
              id: this.information.id,
              osName: this.information.osName,
              osVersion: this.information.osVersion,
              productVersion: this.information.productVersion,
            };
            this.axios
              .post("/software/previewCertificateConfirmInfo", params, {
                responseType: "blob",
              })
              .then((res) => {
                this.certificatesImg = window.URL.createObjectURL(res.data);
                let test = window.open(this.certificatesImg);
                setTimeout(() => {
                  test.document.title =
                    this.information.productName +
                    " " +
                    this.information.productVersion +
                    ".pdf";
                }, 100);
              })
              .catch((err) => {
                this.$message.error(err);
              });
          } else {
            this.$message({ message: "请填写完整证书信息！", type: "warning" });
            return false;
          }
        });
      } else {
        this.getCertificate();
      }
    },
    changeSelect() {
      this.computingPlatform.forEach((item) => {
        this.information.hashratePlatformList.forEach((item2, index) => {
          if (item.platformName == item2.platformName) {
            this.providerAndServerTypes[index] = item.providerAndServerTypes;
            this.providerAndServerTypes[index].forEach((item3) => {
              if (item3.serverProvider == item2.serverProvider) {
                this.serverTypes[index] = item3.serverTypes;
              }
            });
          }
        });
      });
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
    downLoadCertificates() {
      this.axios
        .get(
          `software/downloadAttachments?fileId=${this.certificateInfo.fileId}`
        )
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
                    `/software/downloadAttachments?fileId=${this.certificateInfo.fileId}`
                  );
                }
                done();
              },
            }
          );
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    getCertificate() {
      this.axios
        .get(`/software/previewCertificate?softwareId=${this.softwareId}`, {
          responseType: "blob",
        })
        .then((res) => {
          this.certificatesImg = window.URL.createObjectURL(res.data);
          let test = window.open(this.certificatesImg);
          setTimeout(() => {
            test.document.title =
              this.forms.productName + " " + this.forms.productVersion + ".pdf";
          }, 100);
        });
    },
    getSign() {
      this.axios
        .get(
          `/software/getAttachments?softwareId=${this.softwareId}&fileType=sign`
        )
        .then((res) => {
          this.sign = res.data.result;
          this.axios
            .get("/software/imagePreview", {
              params: {
                fileId: this.sign[0].fileId,
              },
              responseType: "blob",
            })
            .then((res) => {
              this.signImage = window.URL.createObjectURL(res.data);
            })
            .catch((err) => {
              this.$message.error(err);
            });
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    getCertificateInfo() {
      this.axios
        .get("/software/certificateInfo", {
          params: {
            softwareId: this.softwareId,
          },
        })
        .then((res) => {
          this.certificateInfo = res.data.result;
        })
        .catch((err) => {
          this.$message.error(err);
        });
    },
    downloadCertificates(fileId) {
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
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
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
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    sybmitFn() {
      let flag = true;
      this.information.hashratePlatformList.forEach((item) => {
        if (item.serverTypes.length == 0) {
          flag = false;
        }
      });
      this.information.hashratePlatformList.forEach((item1, index1) => {
        this.information.hashratePlatformList.forEach((item2, index2) => {
          if (
            item1.platformName == item2.platformName &&
            item1.serverProvider == item2.serverProvider &&
            index1 != index2
          ) {
            flag = false;
          }
        });
      });
      this.$refs.form.validate((valid) => {
        if (valid && flag) {
          let params = {
            hashratePlatformList: this.information.hashratePlatformList,
            id: this.information.id,
            osName: this.information.osName,
            osVersion: this.information.osVersion,
            productVersion: this.information.productVersion,
          };
          this.axios
            .post("/software/update", params)
            .then(() => {
              this.$message({
                message: "证书信息已确认",
                type: "success",
              });
              this.$router.back(-1);
            })
            .catch((err) => {
              this.$message.error(err?.response?.data?.message);
            });
        } else {
          this.$message({
            message: "信息不完整或算力平台重复！",
            type: "warning",
          });
          return false;
        }
      });
    },
    changeServerProvider(name, index) {
      let flag = true;
      this.providerAndServerTypes[0].forEach((item) => {
        if (item.serverProvider == name && flag) {
          this.serverTypes[index] = item.serverTypes;
          this.information.hashratePlatformList[index].serverTypes = [
            this.serverTypes[index][0],
          ];
          flag = false;
        }
      });
    },
    changeFlatform(name, index) {
      this.computingPlatform.forEach((item) => {
        if (item.platformName == name) {
          this.providerAndServerTypes[index] = item.providerAndServerTypes;
          this.information.hashratePlatformList[index].serverProvider =
            this.providerAndServerTypes[index][0].serverProvider;
          this.serverTypes[index] =
            this.providerAndServerTypes[index][0].serverTypes;
          this.information.hashratePlatformList[index].serverTypes =
            this.providerAndServerTypes[index][0].serverTypes[0];
        }
      });
    },
    getComputingPlatform() {
      this.axios
        .get("software/findAllComputingPlatform")
        .then((res) => {
          this.computingPlatform = res.data.result;
          this.changeSelect();
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    getOsVersion(osName) {
      this.allOs.forEach((item) => {
        if (item.osName == osName) {
          this.osVersions = item.osVersion;
          this.information.osVersion = this.osVersions[0];
        }
      });
    },
    getInformation() {
      this.axios
        .get("software/findById", {
          params: {
            id: this.softwareId,
          },
        })
        .then((res) => {
          this.information = res.data.result;
          this.changeSelect();
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    getAllOs() {
      this.axios
        .get("software/findAllOs")
        .then((res) => {
          this.allOs = res.data.result;
          this.allOs.forEach((item) => {
            if (item.osName == this.information.osName) {
              this.osVersions = item.osVersion;
            }
          });
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    processReciew() {
      if (this.form.handlerResult == "3") {
        let params = {
          handlerResult: this.form.handlerResult,
          softwareId: this.form.softwareId,
          transferredComments: "转审",
          transferredUser: this.form.transferredUser,
        };
        this.$refs.form2.validate((valid) => {
          if (valid) {
            this.axios.post("/software/processReview", params).then(() => {
              this.$message({
                message: "审核提交成功！",
                type: "success",
              });
              this.$router.back("-1");
            });
          } else {
            this.$message({
              message: "请选择转审人员",
              type: "warning",
            });
          }
        });
      } else {
        this.$refs.form2.validate((valid) => {
          if (valid) {
            this.axios.post("/software/processReview", this.form).then(() => {
              this.$message({
                message: "审核提交成功！",
                type: "success",
              });
              this.$router.back("-1");
            });
          } else {
            this.$message({
              message: "请填写审核意见",
              type: "warning",
            });
          }
        });
      }
    },
    getTransferredUserList() {
      this.form.softwareId = this.$props.softwareId;
      this.axios
        .get(`software/transferredUserList?softwareId=${this.form.softwareId}`)
        .then((res) => {
          this.options = res.data.result;
        });
    },
  },
};
</script>
<style lang="less" scoped>
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}
.cancels2 {
  margin-top: 40px;
}
.signImage {
  width: 93px;
  height: 36px;
}
.computPlatform {
  margin-bottom: 10px;
  .el-input {
    width: 192px;
    margin-right: 16px;
  }
}
.processProgressDetailsHuawei {
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
    align-items: flex-start;
    .certificates {
      color: #002fa7;
      margin-left: 8px;
      font-size: 16px;
    }
  }
  .cancels {
    width: 142px;
    height: 46px;
    border: 1px solid #000;
    margin-right: 16px;
    line-height: 48px;
    text-align: center;
  }
  .fileName {
    color: #002fa7;
    font-size: 12px;
  }
  .certificatesInfor {
    color: #4f5661;
    font-size: 14px;
  }
  .cerInfor {
    color: #000;
    font-size: 14px;
  }
  .operation {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    width: 410px;
    text-align: center;
    line-height: 48px;
    margin-top: 40px;
    .cancels {
      width: 142px;
      height: 46px;
      border: 1px solid #000;
    }
    .submit {
      width: 144px;
      height: 48px;
      background: #000;
      color: #fff;
      margin-right: 23px;
    }
  }
  .download {
    position: absolute;
    right: -100px;
    top: 0;
    width: 48px;
    height: 48px;
    opacity: 0.6;
    background: #fff;
    border-radius: 50%;
    text-align: center;
    line-height: 48px;
    font-size: 30px;
  }
  .shutdown {
    position: absolute;
    right: -188px;
    top: 0;
    width: 48px;
    height: 48px;
    opacity: 0.6;
    background: #fff;
    border-radius: 50%;
    text-align: center;
    line-height: 48px;
    font-size: 30px;
  }
}
</style>
<style lang="less">
.el-pagination__editor.el-input {
  width: 50px !important;
}
.processProgressDetailsHuawei {
  .el-form-item {
    width: 400px;
    margin-bottom: 16px;
  }
  .upload {
    width: 100%;
  }
  .el-input {
    width: 400px;
    .el-input__inner {
      height: 36px;
      border-radius: 0px;
      line-height: 36px;
    }
  }
  .el-textarea {
    width: 400px;
    .el-textarea__inner {
      height: 126px;
      border-radius: 0px;
    }
  }
}
.flat {
  display: flex;
  justify-content: space-between;
  width: 800px;
}
.select {
  margin-bottom: 10px;
  .el-input {
    width: 192px;
    margin-right: 16px;
  }
}
.select1 {
  margin-bottom: 10px;
  .el-input {
    width: 230px;
    margin-right: 16px;
  }
}
.de .el-form-item__error {
  display: none;
}
.de .el-input__inner {
  border: 1px solid #dcdfe6 !important;
}
.de .el-textarea__inner {
  border: 1px solid #dcdfe6 !important;
}
</style>
