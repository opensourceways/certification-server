<template>
  <div class="enterpriseAuditDetails">
    <div class="content">
      <div class="title">
        <span style="color: #555555" @click="goBackAll" class="cursor">openEuler软件兼容性测评服务</span>
        <i class="el-icon-arrow-right"></i>
        <span style="font-weight: bold">{{ contentTitle }}</span>
      </div>
      <div class="main">
        <el-form ref="form" :model="form" :rules="rules" label-width="150px" label-position="left">
          <div class="modeltitle">企业信息</div>
          <div class="modelmin">基本信息</div>
          <div class="model">
            <el-form-item label="营业执照">
              <img :src="code" alt style="width: 228px; height: 158px"/>
            </el-form-item>
            <el-form-item label="企业LOGO">
              <img :src="code" alt style="width: 228px; height: 104px"/>
            </el-form-item>
          </div>
          <div class="model">
            <el-form-item label="企业名称">
              <span>{{ form.companyName }}</span>
            </el-form-item>
            <el-form-item label="统一社区信用代码/组织机构代码" class="unify">
              <span>{{ form.creditCode }}</span>
            </el-form-item>
          </div>
          <div class="model">
            <el-form-item label="工商注册国家/地区">
              {{
                form.region
              }}
            </el-form-item>
            <el-form-item label="企业法人代表">{{ form.legalPerson }}</el-form-item>
          </div>
          <div class="model">
            <el-form-item label="注册资金">
              {{
                form.registrationCapital
              }}
            </el-form-item>
            <el-form-item label="营业期限">
              {{
                form.expirationDate
              }}
            </el-form-item>
          </div>
          <el-form-item label="企业成立时间">
            {{
              form.registrationDate
            }}
          </el-form-item>
          <el-form-item label="营业执照地址">{{ form.address }}</el-form-item>
          <div class="verification">
            <img :src="require('@assents/images/greenSuccess.png')" alt/>
            <span style="font-size: 14px; color: #5a9b83; margin: 0 10px">企业工商注册信息系统校验已通过</span>
            <el-popover
                placement="right"
                width="400"
                trigger="hover"
                content="系统校验方式通过法人姓名、企业名称、信用代码判断是否合法"
            >
              <img :src="require('assents/images/exclamation.png')" alt slot="reference"/>
            </el-popover>
          </div>
          <div class="modelmin">申请人信息</div>
          <div class="model">
            <el-form-item label="申请人姓名">
              {{
                userInfo.username
              }}
            </el-form-item>
            <el-form-item label="申请人邮箱">{{ userInfo.mail }}</el-form-item>
          </div>
          <el-form-item label="申请人电话">
            {{
              userInfo.telephone
            }}
          </el-form-item>
          <div class="modeltitle">审核</div>
          <el-form-item label="审核结果" v-if="type == 0" prop="radio">
            <el-radio-group v-model="form.radio">
              <el-radio :label="true">通过</el-radio>
              <el-radio :label="false">不通过</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审核结果" v-if="type == 1">
            <span>通过</span>
          </el-form-item>
          <el-form-item label="审核结果" v-if="type == 2">
            <span>不通过</span>
          </el-form-item>
          <el-form-item label="审核意见" class="comment" v-if="type == 1">
            <span class="approvalComment">{{ form.approvalComment }}</span>
          </el-form-item>
          <el-form-item label="审核意见" class="comment" v-if="type == 2">
            <span class="approvalComment">{{ form.approvalComment }}</span>
          </el-form-item>
          <el-form-item label="审核意见" v-if="type == 0" prop="textarea">
            <el-input
                type="textarea"
                placeholder="请输入内容"
                v-model="form.textarea"
                maxlength="1000"
                show-word-limit
            ></el-input>
          </el-form-item>
          <el-form-item label v-if="type == 0">
            <div class="operation">
              <div class="submit cursor" @click="submit('form')">提交</div>
              <div class="cancels cursor" @click="goBack">取消</div>
            </div>
          </el-form-item>
          <el-form-item label v-if="type != 0">
            <div class="operation">
              <div class="cancels cursor" @click="goBack">返回</div>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "enterpriseAuditDetails",
  comments: {},
  data() {
    return {
      form: {
        companyName: '',
        creditCode: '',
        region: '',
        legalPerson: '',
        registrationCapital: '',
        expirationDate: '',
        registrationDate: '',
        address: '',
        approvalComment: '',
        radio: true,
        textarea: '',
      },
      contentTitle: '企业审核',
      type: 0,
      uuid: '',
      userInfo: {},
      code: '',
      logo: '',
      rules: {
        radio: [{required: true, message: '请选择审核结果', trigger: 'blur'}],
        textarea: [
          {required: true, message: '请输入审核意见', trigger: 'blur'},
        ],
      },
    }
  },
  mounted() {
    this.uuid = this.$route.query.id
    this.getCompany();
    this.getUsename();
    this.getUse();
  },
  methods: {
    goBackAll() {
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
            this.$message.error(err?.response?.data?.message)
          })
    },
    getUse() {
      this.axios
          .get('/user/getUserInfo', {})
          .then((res) => {
            if (res.data.code === 200) {
              this.$store.commit('changeStatus', this.data.result)
            }
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message)
          })
    },
    getCompany() {
      this.axios
          .get('/companies/company', {
            params: {
              userUuid: this.uuid,
            },
          })
          .then((res) => {
            this.form = {
              companyName: res.data.result.companyName,
              creditCode: res.data.result.creditCode,
              region: res.data.result.region,
              legalPerson: res.data.result.legalPerson,
              registrationCapital: res.data.result.registrationCapital,
              expirationDate: res.data.result.expirationDate,
              registrationDate: res.data.result.registrationDate,
              address: res.data.result.address,
              approvalComment: res.data.result.approvalComment,
              radio: true,
              textarea: '',
            }
            this.type = res.data.result.status //后续验证
            this.getDownload(res.data.result.license, 'license')
            this.getDownload(res.data.result.logo, 'logo')
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message)
          })
    },
    getUsename() {
      this.axios
          .get('/users/user', {
            params: {
              uuid: this.uuid,
            },
          })
          .then((res) => {
            this.userInfo = res.data.result
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message)
          })
    },
    getDownload(id, type) {
      this.axios
          .get('/companies/imagePreview', {
            params: {
              fileId: id,
            },
            responseType: 'blob',
          })
          .then((res) => {
            if (type === 'license') {
              this.code = window.URL.createObjectURL(res.data)
            } else {
              this.logo = window.URL.createObjectURL(res.data)
            }
          })
    },
    submit(formName) {
      let params = {
        comment: this.form.textarea,
        result: this.form.radio,
        userUuid: this.uuid,
      }
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.axios
              .post('/companies/company/audit', params)
              .then((response) => {
                if (response.data.code === 200) {
                  this.$router.push({path: '/enterpriseAudit'})
                } else {
                  this.$message.error(response.data.result.errorMsg)
                }
              })
        } else {
          return false
        }
      })
    },
    goBack() {
      this.$router.push({path: '/enterpriseAudit'})
      this.form = {
        companyName: '',
        creditCode: '',
        region: '',
        legalPerson: '',
        registrationCapital: '',
        expirationDate: '',
        registrationDate: '',
        address: '',
        approvalComment: '',
        radio: true,
        textarea: '',
      }
    },
  },
}
</script>

<style lang="less" scoped>
.comment {
  width: 1000px;
!important;
}

.approvalComment {
  word-break: break-all;
  width: 1000px !important;
}

.enterpriseAuditDetails {
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
      padding: 70px 0;

      .modeltitle {
        font-size: 20px;
        margin-bottom: 24px;
      }

      .modelmin {
        font-size: 16px;
        margin-top: 24px;
        margin-bottom: 24px;
        font-weight: bold;
      }

      .model {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
      }

      .verification {
        display: flex;
        justify-content: flex-start;
        align-items: center;
      }
    }
  }
}
</style>
<style lang="less">
.enterpriseAuditDetails {
  .el-form-item {
    margin-bottom: 10px;
    width: 400px;
  }

  .unify {
    .el-form-item__label {
      line-height: 20px;
    }
  }

  .el-textarea {
    width: 400px;

    .el-textarea__inner {
      height: 126px;
    }
  }

  .operation {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 400px;
    text-align: center;
    line-height: 48px;
    margin-top: 40px;

    .cancels {
      width: 144px;
      height: 48px;
      background: #000000;
      color: #ffffff;
      margin-right: 23px;
    }
  }
}
</style>