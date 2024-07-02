<template>
  <div class="enterpriseSuccess">
    <div class="modelTitle">
      <div class="title">企业实名认证</div>
      <img
        :src="require('../../../assets/images/certificationSuccess.svg')"
        alt=""
      />
    </div>
    <el-form ref="form" :model="form" label-width="180px" label-position="left">
      <div class="modelTitle">基本信息</div>
      <el-form-item label="企业LOGO">
        <img :src="logo" alt="" style="width: 228px; height: 158px" />
      </el-form-item>
      <el-form-item label="工商注册国家/地区">
        <span>{{ form.region }}</span>
      </el-form-item>
      <el-form-item label="企业邮箱">
        <span>{{ form.companyMail }}</span>
      </el-form-item>
      <el-form-item label="营业执照">
        <img :src="code" alt="" style="width: 228px; height: 158px" />
      </el-form-item>
      <el-form-item label="企业名称">
        <span>{{ form.companyName }}</span>
      </el-form-item>
      <el-form-item label="统一社会信用代码/组织机构代码">
        <span>{{ form.creditCode }}</span>
      </el-form-item>
      <el-form-item label="营业执照地址">
        <span>{{ form.address }}</span>
      </el-form-item>
      <el-form-item label="企业法人代表">
        <span>{{ form.legalPerson }}</span>
      </el-form-item>
      <el-form-item label="注册资金">
        <span>{{ form.registrationCapital }}</span>
      </el-form-item>
      <el-form-item label="企业成立时间">
        <span>{{ form.registrationDate }}</span>
      </el-form-item>
      <el-form-item label="营业期限">
        <span>{{ form.expirationDate }}</span>
      </el-form-item>
      <div class="modelTitle">申请人信息</div>
      <el-form-item label="申请人姓名">
        <span>{{ useInfor.username }}</span>
      </el-form-item>
      <el-form-item label="申请人电话">
        <span>{{ useInfor.telephone }}</span>
      </el-form-item>
      <el-form-item label="申请人邮箱">
        <span>{{ useInfor.mail }}</span>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  data() {
    return {
      form: {},
      useInfor: {},
      code: "",
      logo: "",
    };
  },
  mounted() {
    this.getCurrentUser();
    this.getUsename();
  },
  methods: {
    getCurrentUser() {
      this.axios
        .get("/companies/company/currentUser")
        .then((res) => {
          this.form = res.data.result;
          this.getDownload(res.data.result.license, "license");
          this.getDownload(res.data.result.logo, "logo");
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    getUsename() {
      this.axios
        .get("/user/getUserInfo")
        .then((res) => {
          this.useInfor = res.data.result;
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    getDownload(id, type) {
      this.axios
        .get("/companies/imagePreview", {
          params: { fileId: id },
          responseType: "blob",
        })
        .then((res) => {
          if (type === "license") {
            this.code = window.URL.createObjectURL(res.data);
          } else {
            this.logo = window.URL.createObjectURL(res.data);
          }
        })
    },
  },
};
</script>
<style lang="less" scoped>
.enterpriseSuccess{
    padding: 48px;
    .modelTitle{
        display: flex;
        justify-content: flex-start;
        align-items: center;
        .title{
            font-size: 36px;
            margin-bottom: 24px;
            margin-right: 60px;
        }
    }
    .modelTitle{
        margin: 40px 0 20px 0;
        font-size: 16px;
        font-weight: bold;
    }
}
</style>
<style lang="less">
.enterpriseSuccess{
    .el-form-item{
        margin-bottom: 10px;
        
    }
    .el-form-item__label{
        color: #4f5661;
    }
}
</style>