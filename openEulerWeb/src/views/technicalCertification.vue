<template>
  <div class="technicalCertification">
    <div class="content">
      <div class="title">
      <span style="color: #555555" @click="goBack" class="cursor"
      >openEuler软件兼容性测评服务</span>
        <i class="el-icon-arrow-right"></i>
        <span style="font-weight: bold">{{ contentTitle }}</span>
      </div>
      <div class="contentTitle">{{ contentTitle }}</div>
      <div class="main">
        <el-form
            :model="ruleForm"
            :rules="rules"
            ref="ruleForm"
            label-width="150px"
            label-position="left"
        >
          <el-form-item label="企业名称">
            <span>{{ ruleForm.companyName }}</span>
          </el-form-item>
          <el-form-item label="产品名称" prop="productName">
            <el-input
                v-model="ruleForm.productName"
                placeholder="请输入"
                maxlength="64"
            ></el-input>
          </el-form-item>
          <el-form-item label="产品功能介绍" prop="productFunctionDesc">
            <el-input
                v-model="ruleForm.productFunctionDesc"
                placeholder="请输入产品功能介绍"
                type="textarea"
                show-word-limit
                maxlength="1000"
            ></el-input>
          </el-form-item>
          <el-form-item label="使用场景介绍" prop="usageScenesDesc">
            <el-input
                v-model="ruleForm.usageScenesDesc"
                placeholder="请输入使用场景介绍"
                type="textarea"
                show-word-limit
                maxlength="1000"
            ></el-input>
          </el-form-item>
          <el-form-item label="产品版本" prop="productVersion">
            <el-input
                v-model="ruleForm.productVersion"
                placeholder="请输入"
                maxlength="64"
            ></el-input>
          </el-form-item>
          <el-form-item label="OS名称" prop="osName">
            <el-select
                v-model="ruleForm.osName"
                placeholder="请选择"
                @change="osNameChange"
            >
              <el-option
                  v-for="item in osNameOptions"
                  :key="item.osName"
                  :label="item.osName"
                  :value="item.osName"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="OS版本" prop="osVersion">
            <el-select v-model="ruleForm.osVersion" placeholder="请选择">
              <el-option
                  v-for="item in osVersionOptions"
                  :key="item"
                  :label="item"
                  :value="item"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="算力平台" required prop="hashratePlatformList">
            <div
                class="computPlatform"
                v-for="(item, index) in ruleForm.hashratePlatformList"
                :key="index"
            >
              <el-select
                  v-model="ruleForm.hashratePlatformList[index].platformName"
                  placeholder="选择算力平台"
                  @change="(val) => platformChange(val, index)"
              >
                <el-option
                    v-for="item in platformOptions"
                    :key="item.platformName"
                    :label="item.platformName"
                    :value="item.platformName"
                ></el-option>
              </el-select>
              <el-select
                  v-model="ruleForm.hashratePlatformList[index].serverProvider"
                  placeholder="选择硬件厂家"
                  @change="(val) => manufacturerChange(val, index)"
              >
                <el-option
                    v-for="item in manufactureOptions"
                    :key="item.serverProvider"
                    :label="item.serverProvider"
                    :value="item.serverProvider"
                ></el-option>
              </el-select>
              <el-select
                  v-model="ruleForm.hashratePlatformList[index].serverTypes"
                  placeholder="选择硬件型号"
                  multiple
              >
                <el-option
                    v-for="item in modeloptions"
                    :key="item"
                    :label="item"
                    :value="item"
                ></el-option>
              </el-select>
              <span
                  class="cursor"
                  style="color: #002fa7; margin-right: 10px"
                  v-if="index === ruleForm.hashratePlatformList.length - 1"
                  @click="addFn"
              >添加</span>
              <span
                  class="cursor"
                  style="color: #002fa7"
                  v-if="ruleForm.hashratePlatformList.length > 1"
                  @click="deleteFn(index)"
              >删除</span>
            </div>
          </el-form-item>
          <el-form-item label="产品类型" prop="productType">
            <el-cascader
                v-model="ruleForm.productType"
                :options="productOptions"
                :props="{
              value: 'productType',
              label: 'productType',
              children: 'children',
            }"
            ></el-cascader>
          </el-form-item>
          <el-form-item label="测试机构" prop="testOrganization">
            <el-select v-model="ruleForm.testOrganization" placeholder="请选择">
              <el-option
                  v-for="item in innovationOptions"
                  :key="item"
                  :label="item"
                  :value="item"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="ruleForm.checked" @change="handleChangeSign">
              我已阅读并同意签署
              <router-link to="/compatibilityProtocol" target="_blank">
              <span style="color: #002fa7; text-decoration: none !important"
              >《技术测评协议》</span>
              </router-link>
            </el-checkbox>
          </el-form-item>
          <el-form-item>
            <div class="operation">
              <div
                  class="submit cursor"
                  @click="submitFn('ruleForm')"
                  v-if="ruleForm.checked"
              >
                提交
              </div>
              <div class="notAllow" v-else>提交</div>
              <div class="cancels cursor" @click="resetForm('ruleForm')">
                取消
              </div>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "technicalCertification",
  components: {},
  data() {
    return {
      contentTitle: "申请技术测评",
      ruleForm: {
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
        checked: false,
      },
      rules: {
        productName: [
          {required: true, message: "请输入产品名称", trigger: "blur"},
        ],
        productFunctionDesc: [
          {required: true, message: "请输入产品功能介绍", trigger: "blur"},
        ],
        usageScenesDesc: [
          {required: true, message: "请输入使用场景介绍", trigger: "blur"},
        ],
        productVersion: [
          {required: true, message: "请输入产品版本", trigger: "blur"},
        ],
        osName: [
          {required: true, message: "请选择OS名称", trigger: "change"},
        ],
        osVersion: [
          {required: true, message: "请选择OS版本", trigger: "change"},
        ],
        hashratePlatformList: [
          {required: true, message: "请选择算力平台", trigger: "change"},
        ],
        productType: [
          {required: true, message: "请选择产品类型", trigger: "change"},
        ],
        testOrganization: [
          {required: true, message: "请选择测试机构", trigger: "change"},
        ],
      },
      options: [],
      companyCity: "",
      osNameOptions: [],
      osVersionOptions: [],
      productOptions: [],
      innovationOptions: [],
      platformOptions: [],
      manufactureOptions: [],
      modeloptions: [],
      uuid: "",
    };
  },
  mounted() {
    this.uuid = this.$route.query ? this.$route.query.id : "";
    if (this.uuid) {
      this.getFindByid();
    }
    this.getUsename();
    this.getCompany();
    this.getOsFn();
    this.getPlatform();
    this.getProduct();
    this.getInnovation();
  },
  methods: {
    handleChangeSign(val) {
      if (val) {
        this.axios
            .put("/user/signTechnicalAgreement")
            .then(() => {
            })
            .catch((err) => {
              this.$message.error(err?.response?.data?.message);
            });
      }
    },
    goBack() {
      this.axios
          .get('/user/getUserInfo', {})
          .then((res) => {
            if (res.data.result.roles.indexOf('china_region') !== -1) {
              this.$router.push('/enterpriseAudit')
            } else if (res.data.result.roles.indexOf('admin') !== -1) {
              this.$router.push('/adminApproval')
            } else {
              this.$router.push('/')
            }
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
    getUsename() {
      this.axios
          .get("/user/getUserInfo")
          .then((res) => {
            let userName = res.data.result;
            this.$store.commit("changeStatus", userName);
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
    getCompany() {
      this.axios
          .get("/companies/company/getCurrentUserCompanyName", {})
          .then((res) => {
            this.ruleForm.companyName = res.data.result;
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
    addFn() {
      this.ruleForm.hashratePlatformList.push({
        platformName: "",
        serverProvider: "",
        serverTypes: [],
      });
    },
    deleteFn(index) {
      this.ruleForm.hashratePlatformList.splice(index, 1);
    },
    getOsFn() {
      this.axios
          .get("/software/findAllOs", {})
          .then((res) => {
            this.osNameOptions = res.data.result;
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
    getPlatform() {
      this.axios
          .get("/software/findAllComputingPlatform", {})
          .then((res) => {
            this.platformOptions = res.data.result;
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
    getProduct() {
      this.axios
          .get("/software/findAllProduct", {})
          .then((res) => {
            this.productOptions = res.data.result;
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
    getInnovation() {
      this.axios
          .get("/software/findAllInnovationCenter", {})
          .then((res) => {
            this.innovationOptions = res.data.result;
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
    getFindByid() {
      this.axios
          .get("/software/findById", {
            params: {
              id: this.uuid,
            },
          })
          .then((res) => {
            this.ruleForm = res.data.result;
            this.ruleForm.productType = this.ruleForm.productType.split("/");
            this.osNameChange(this.ruleForm.osName);
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
    osNameChange(value) {
      this.osNameOptions.forEach((item) => {
        if (item.osName === value) {
          this.osVersionOptions = item.osVersion;
        }
      });
    },
    platformChange(value, index) {
      this.manufacturerOptions = [];
      this.ruleForm.hashratePlatformList[index].serverProvider = "";
      this.ruleForm.hashratePlatformList[index].serverTypes = [];
      this.platformOptions.forEach((item) => {
        if (item.platformName === value) {
          this.manufacturerOptions = item.providerAndServerTypes;
        }
      });
    },
    manufacturerChange(value, index) {
      this.modeloptions = [];
      this.ruleForm.hashratePlatformList[index].serverTypes = [];
      this.manufacturerOptions.forEach((item) => {
        if (item.serverProvider === value) {
          this.modeloptions = item.serverTypes;
        }
      });
    },
    submitFn(forName) {
      let flag = true;
      this.ruleForm.hashratePlatformList.forEach((item) => {
        if (item.serverTypes.length === 0) {
          flag = false;
        }
      });
      this.ruleForm.hashratePlatformList.forEach((item1, index1) => {
        this.ruleForm.hashratePlatformList.forEach((item2, index2) => {
          if (
              item1.platformName === item2.platformName &&
              item1.serverProvider === item2.serverProvider &&
              index1 != index2
          ) {
            flag = false;
          }
        });
      });
      this.$refs[forName].validate((valid) => {
        if (valid && flag) {
          let params = this.ruleForm;
          params.productType = params.productType.join("/");
          this.axios
              .post("/software/register", params)
              .then((response) => {
                if (response.data.code === 200) {
                  this.$message({
                    message: "提交成功！",
                    type: "success",
                  });
                  this.$router.push("/");
                } else {
                  this.$message.error(response.data.message);
                }
              })
        } else {
          this.$message({
            message: "信息不完整或算力平台重复！",
            type: "warning",
          });
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
      this.$router.push("/");
    },
  },
};
</script>

<style lang="less" scoped>
.technicalCertification {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;

  .content {
    width: 1416px;

    .title {
      height: 96px;
      line-height: 96px;
      font-size: 12px;

      i {
        margin: 0 4px;
      }
    }

    .contentTitle {
      font-size: 36px;
      text-align: center;
      margin-bottom: 40px;
    }

    .main {
      background: #ffffff;
      display: flex;
      justify-content: center;
      align-items: center;
      width: 410px;
      text-align: center;
      line-height: 48px;
      margin-top: 40px;

      .cancels {
        width: 144px;
        height: 46px;
        border: 1px solid #000000;
      }

      .notAllow {
        width: 144px;
        height: 48px;
        background: #555555;
        color: #ffffff;
        margin-right: 23px;
        cursor: not-allowed;
      }

      .submit {
        width: 144px;
        height: 48px;
        background: #000000;
        color: #ffffff;
        margin-right: 23px;
      }
    }
  }
}
</style>
<style lang="less">
.el-form-item__content {
  word-break: break-all;
}

.technicalCertification {
  .el-input {
    width: 400px;
  }

  .el-textarea {
    width: 126px;

    .el-textarea__inner {
      height: 126px;
    }
  }

  .computPlatform {
    margin-bottom: 10px;

    .el-input {
      width: 192px;
      margin-right: 16px;
    }
  }
}

.Computing {
  width: 192px;
  margin-right: 16px;
}
</style>