<template>
  <div class="enterpriseIndex">
    <div class="title">企业实名认证</div>
    <el-form
      ref="form"
      :model="form"
      :rules="rules"
      label-width="150px"
      label-position="left"
    >
      <div class="modelTitle">基本信息</div>
      <el-form-item label="企业LOGO" prop="logo">
        <el-upload
          class="upload-demo"
          drag
          :action="logoAction"
          :file-list="fileList"
          :show-filee-list="false"
          :http-request="customRequestLogo"
          :before-upload="beforeUpload"
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">
            将文件拖到此处，或
            <em>点击上传</em>
          </div>
          <div class="el-upload__tip" slot="tip">
            上传的图片格式要求jpg、jpeg、bmp、png，不超过10M
          </div>
        </el-upload>
        <div class="fileList" v-show="form.logoName">
          <span
            @click="goDown(form.logo)"
            style="color: #002fa7"
            class="cursor"
            >{{ form.logoName }}</span
          >
          <i
            class="el-icon-close cursor"
            @click="goDelete('logoName', 'logo')"
          ></i>
        </div>
      </el-form-item>
      <el-form-item label="工商注册国家/地区" prop="region">
        <el-select v-model="form.region" placeholder="请选择" filterable>
          <el-option
            v-for="item in countryOptions"
            :key="item"
            :label="item"
            :value="item"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="企业邮箱" prop="companyMail">
        <el-input v-model="form.companyMail" placeholder="请输入企业邮箱地址">
        </el-input>
      </el-form-item>
      <el-form-item label="上传营业执照" required prop="license">
        <el-upload
          class="upload-demo licensure"
          drag
          :action="textAction"
          :file-list="fileList1"
          :show-filee-list="false"
          :http-request="customRequest"
          :before-upload="beforeUpload1"
        >
          <img
            src="require('../../../assets/images/licensure.png')"
            alt=""
            style="margin-top: 30px"
          />
          <div class="el-upload__text">
            将文件拖到此处，或
            <em>点击上传</em>
          </div>
          <div class="el-upload__tip" slot="tip">
            上传的图片格式要求jpg、jpeg、bmp、png，不超过10M
          </div>
        </el-upload>
        <div class="fileList" v-if="form.licenseName">
          <span
            @click="goDown(form.license)"
            style="color: #002fa7"
            class="cursor"
            >{{ form.licenseName }}</span
          >
          <i
            class="el-icon-close cursor"
            @click="goDelete('licenseName', 'license')"
          ></i>
        </div>
      </el-form-item>
      <div class="modelTitle">营业执照自动识别结果</div>
      <el-form-item label="企业名称" prop="companyName">
        <el-input
          maxlength="64"
          v-model="form.companyName"
          placeholder="请输入企业名称"
        >
        </el-input>
      </el-form-item>
      <el-form-item
        label="统一社会信用代码/组织机构代码"
        prop="creditCode"
        class="creditCode"
      >
        <template #label>
          <span
            ><span>统一社会信用代码/</span
            ><span>&nbsp;&nbsp;组织机构代码</span></span
          >
        </template>
        <el-input
          maxlength="32"
          v-model="form.creditCode"
          placeholder="请输入企业证件号"
        >
        </el-input>
      </el-form-item>
      <el-form-item label="营业执照地址" prop="address">
        <template #label>
          <span>&nbsp;&nbsp;营业执照地址</span>
        </template>
        <el-input
          maxlength="1000"
          v-model="form.address"
          placeholder="请输入营业执照地址"
          type="textarea"
          show-word-limit
        >
        </el-input>
      </el-form-item>
      <el-form-item label="企业法人代表" prop="legalPerson">
        <el-input
          maxlength="50"
          v-model="form.legalPerson"
          placeholder="请输入法人名称"
        >
        </el-input>
      </el-form-item>
      <el-form-item label="注册资金">
        <template #label>
          <span>&nbsp;&nbsp;注册资金</span>
        </template>
        <el-input
          maxlength="32"
          v-model="form.registrationCapital"
          placeholder="请输入注册资金(元)"
        >
        </el-input>
      </el-form-item>
      <el-form-item label="企业成立时间">
        <template #label>
          <span>&nbsp;&nbsp;企业成立时间</span>
        </template>
        <el-date-picker
          v-model="form.registrationDate"
          type="date"
          placeholder="选择日期"
          format="yyyy 年 MM 月 dd 日"
          value-format="yyyy-MM-dd"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="营业期限">
        <template #label>
          <span>&nbsp;&nbsp;营业期限</span>
        </template>
        <el-date-picker
          v-model="form.expirationDate"
          type="date"
          placeholder="选择日期"
          format="yyyy 年 MM 月 dd 日"
          value-format="yyyy-MM-dd"
          @change="expiraChange"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <div class="operation">
          <div class="submit cursor" @click="submitFn('form')">提交</div>
          <div class="cancels" @click="cancleFn">取消</div>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  data() {
    var validatePass = (rule, value, callback) => {
      if (this.checkEmail(value)) {
        callback();
      } else {
        callback(new Error("请输入正确的邮箱"));
      }
    };
    return {
      form: {
        companyName: "",
        creditCode: "",
        address: "",
        legalPerson: "",
        registrationCapital: "",
        registrationDate: null,
        expirationDate: null,
        region: "",
        companyMail: "",
        license: "",
        licenseName: "",
        log: "",
        logName: "",
      },
      logoAction: "/companies/uploadLogo",
      textAction: "/companies/uploadLicense",
      countryOptions: [],
      fileList: [],
      fileList1: [],
      useInfor: {},
      rules: {
        logo: [{ required: true, message: "请上传企业LOGO", trigger: "blur" }],
        region: [
          { required: true, message: "请选择国家/地区", trigger: "change" },
        ],
        companyMail: [
          { required: true, message: "请输入企业邮箱", trigger: "blur" },
          { validator: validatePass, trigger: "blur" },
        ],
        license: [
          { required: true, message: "请上传营业执照", trigger: "blur" },
        ],
        companyName: [
          { required: true, message: "请输入企业名称", trigger: "blur" },
        ],
        creditCode: [
          {
            required: true,
            message: "请输入统一社会信用代码/组织机构代码",
            trigger: "blur",
          },
        ],
        legalPerson: [
          { required: true, message: "请输入企业法人", trigger: "blur" },
        ],
      },
    };
  },
  mounted() {
    this.getCountries();
    this.getUsename();
    this.getCurrentUser();
  },
  methods: {
    customRequestLogo(options) {
      const formData = new FormData();
      formData.append("file", options.file);
      this.axios
        .post(this.logoAction, formData, {
          headers: this.headers,
        })
        .then((response) => {
          this.handleSuccessLogo(response.data);
        });
    },
    customRequest(options) {
      const formData = new FormData();
      formData.append("file", options.file);
      this.axios
        .post(this.textAction, formData, {
          headers: this.headers,
        })
        .then((response) => {
          this.handleSuccess(response.data);
        });
    },
    headers() {
      return {
        "x-xsrf-token": this.getCookie("XSRF-TOKEN"),
        Authorization: localStorage.getItem("user"),
      };
    },
    getCookie(name) {
      const pattern = new RegExp((name += "=([^;]*)"));
      const matches = document.cookie.match(pattern);
      if (matches) {
        return matches[1];
      }
      return null;
    },
    validateField(type) {
      this.$refs.form.validateField(type);
    },
    checkEmail(emailStr) {
      let reg = /^.+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/;
      if (reg.test(emailStr)) {
        return true;
      } else {
        return false;
      }
    },
    beforeUpload(file) {
      if (this.form.logoName) {
        this.$message.error("上传文件不能超过1个");
        return false;
      }
      const isLt2M = file.size / 1024 / 1024 < 10;
      if (
        file.type !== "imagge/jpeg" &&
        file.type !== "imagge/jpg" &&
        file.type !== "imagge/bmp" &&
        file.type !== "imagge/png"
      ) {
        this.$message.error("请上传正确的文件格式");
        return false;
      } else if (!isLt2M) {
        this.$message.error("上传文件大小不能超过10MB");
        return false;
      } else if (file.size === 0) {
        this.$message.error("上传文件大小需超过0B");
        return false;
      } else {
        return true;
      }
    },
    beforeUpload1(file) {
      if (this.form.licenseName) {
        this.$message.error("上传文件不能超过1个");
        return false;
      }
      const isLt2M = file.size / 1024 / 1024 < 10;
      if (
        file.type !== "imagge/jpeg" &&
        file.type !== "imagge/jpg" &&
        file.type !== "imagge/bmp" &&
        file.type !== "imagge/png"
      ) {
        this.$message.error("请上传正确的文件格式");
        return false;
      } else if (!isLt2M) {
        this.$message.error("上传文件大小不能超过10MB");
        return false;
      } else if (file.size === 0) {
        this.$message.error("上传文件大小需超过0B");
        return false;
      } else {
        return true;
      }
    },
    getCurrentUser() {
      this.axios
        .get("/companies/company/currentUser")
        .then((res) => {
          if (res.data.result.status != null) {
            this.form = res.data.result;
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
          this.useInfor = res.data.result;
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    getCountries() {
      this.axios
        .get("/countries")
        .then((res) => {
          this.countryOptions = res.data.result;
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    handleSuccess(response) {
      this.form.license = response.result.fileInfo.fileId;
      this.form.licenseName = response.result.fileInfo.fileName;
      if (response.result.licenseInfo) {
        let newobject = JSON.parse(JSON.stringify(response.result.licenseInfo));
        this.form.companyName = newobject.companyName;
        this.form.creditCode = newobject.creditCode;
        this.form.address = newobject.address;
        this.form.legalPerson = newobject.legalPerson;
        this.form.registrationCapital = newobject.registrationCapital;
        this.form.registrationDate = newobject.registrationDate;
        this.form.expirationDate = newobject.expirationDate;
      }
      setTimeout(() => {
        this.$refs.form.validateField("license");
        this.$refs.form.validateField("companyName");
        this.$refs.form.validateField("creditCode");
        this.$refs.form.validateField("address");
        this.$refs.form.validateField("legalPerson");
        this.$refs.form.validateField("registrationCapital");
      }, 10);
      this.$forceUpdate();
    },
    handleSuccessLogo(response) {
      this.form.logo = response.result.fileId;
      this.form.logoName = response.result.fileName;
      this.form = JSON.parse(JSON.stringify(this.form));
      setTimeout(() => {
        this.$refs.form.validateField("logo");
      }, 10);
      this.$forceUpdate();
    },
    expiraChange(value) {
      this.form.expirationDate = value;
      if (value <= this.form.registrationDate) {
        this.$message.error("营业期限需晚于企业成立时间");
        this.form.expirationDate = "";
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
              this.downHandler(`/software/downloadAttachment?fileId=${id}`);
            }
            done();
          },
        }
      );
    },
    goDelete(name, id) {
      this.form[name] = "";
      this.form[id] = "";
      if (id === "logo") {
        this.fileList = [];
      } else {
        this.fileList1 = [];
      }
      this.$message({ message: "删除成功", type: "success" });
      this.$forceUpdate();
    },
    submitFn(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.axios.post("/companies", this.form).then((response) => {
            if (response.data.code == 200) {
              this.$message({ message: "提交成功", type: "success" });
              this.$emit("handleChange", "测评中");
            } else {
              this.$message.error(response.data.message);
            }
          });
        } else {
          return false;
        }
      });
    },
    camcleFn() {
      this.$emit("handleChange", "开始");
    },
  },
};
</script>
<style lang="less" scoped>
.enterpriseIndex {
  padding: 48px;
  .title {
    font-size: 36px;
  }
  .content {
    font-size: 14px;
    color: #555;
    margin: 24px 0 40px 0;
  }
  .button {
    width: 192px;
    height: 48px;
    background: #000;
    color: #000;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    .Arrowhead {
      width: 24px;
      height: 24px;
      border: 1px dashed #fff;
      text-align: center;
      line-height: 24px;
      margin-left: 8px;
    }
  }
  .fiileList {
    width: 300px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .modelTitle {
    margin: 40px 0 20px 0;
    font-size: 16px;
    font-weight: bold;
  }
  .operation {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    width: 400px;
    text-align: center;
    line-height: 48px;
    .cancels {
      width: 144px;
      height: 46px;
      border: 1px solid #000;
      cursor: pointer;
    }
    .notAllow {
      width: 144px;
      height: 48px;
      background: #000;
      color: #fff;
      margin-right: 23px;
      cursor: pointer;
    }
  }
}
</style>
<style lang="less">
a:-webkit-any-link {
  text-decoration: none;
}
.enterpriseIndex {
  .el-input {
    width: 400px;
    height: 36px;
    line-height: 36px;
    .el-input__inner {
      height: 36px;
      line-height: 36px;
      border-radius: 0px;
    }
  }
  .el-textarea {
    width: 400px;
  }
  .el-textarea__inner {
    width: 400px;
    height: 126px;
  }
  .el-upload {
    width: 240px;
    height: 134px;
    .el-upload-dragger {
      width: 240px;
      height: 134px;
      .el-icon-upload {
        margin: 24px 0 12px 0;
      }
    }
  }
  .el-date-editor.el-input {
    width: 400px;
  }
  .licensure {
    .el-upload {
      width: 286px;
      height: 200px;
      .el-upload-dragger {
        width: 286px;
        height: 200px;
      }
    }
  }
}
.creditCode .el-form-item__label {
  line-height: 22px;
}
</style>
