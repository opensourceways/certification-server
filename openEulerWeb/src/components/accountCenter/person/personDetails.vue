<template>
  <div class="personDetails" v-if="!informationNew">
    <div class="title">
      个人基本信息
      <span class="delInfo cursor" @click="centerDialogVisible = true"
        >注销个人信息</span
      >
    </div>
    <div class="personContent">
      <div class="account">
        <div class="name">
          用户名 &nbsp;&nbsp;
          <span>{{ useInfor.username }}</span>
        </div>
        <div class="name" style="margin-right: 240px">
          手机号码
          <span>{{ useInfor.telephone }}</span>
        </div>
        <span
          class="management cursor"
          @click="changeInfor"
          v-if="roles != 'noUser'"
        >
          修改
        </span>
      </div>
      <div class="account" style="margin-top: 24px">
        <div class="name" style="display: flex">
          <div style="width: 70px">个人邮箱</div>
          <div style="width: 90%; word-break: break-all; margin-left: 17px">
            {{ useInfor.mail }}
          </div>
        </div>
        <div class="name" style="margin-right: 240px">
          所在地区
          <span>{{ useInfor.province }} {{ useInfor.city }}</span>
        </div>
        <span class="management" v-if="roles != 'noUser'">
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </span>
      </div>
      <div class="account" style="margin-top: 24px" v-if="roles === 'noUser'">
        <div class="name">
          用户角色
          <span>{{ userRole }}</span>
        </div>
        <span class="management" v-if="roles != 'noUser'">
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </span>
        <span class="management" v-if="roles != 'noUser'">
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </span>
      </div>
    </div>
    <el-dialog
      title="注销个人信息"
      :visible.sync="centerDialogVisible"
      width="30%"
      center
    >
      <span
        >如果您不再使用openEuler兼容性平台，可以注销服务。注销成功后，账号内的所有数据将被全部删除并且无法恢复。</span
      >
      <span slot="footer" class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleDelInfo">确 定</el-button>
      </span>
    </el-dialog>
  </div>
  <div v-else class="personDetails">
    <div class="title">个人基本信息</div>
    <el-form ref="form" :model="form" label-position="150px">
      <el-form-item
        label="用户名"
        prop="username"
        :rules="[{ required: true, message: '请输入用户名', trigger: 'blur' }]"
      >
        <el-input
          v-model="form.username"
          placeholder="请输入用户名"
          maxlength="16"
        ></el-input>
      </el-form-item>
      <el-form-item
        label="手机号码"
        prop="telephone"
        :rules="[{ required: true, message: '手机号码不能为空' }]"
      >
        <span v-if="useInfor.telephone">{{ form.telephone }}</span>
        <el-input
          v-model="form.telephone"
          placeholder="请输入手机号"
          maxlength="16"
          v-else
        ></el-input>
      </el-form-item>
      <el-form-item
        label="邮箱地址"
        prop="mail"
        :rules="[
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          {
            type: 'email',
            message: '请输入正确的邮箱地址',
            trigger: ['blur', 'change'],
          },
        ]"
      >
        <span v-if="useInfor.mail">{{ form.mail }}</span>
        <el-input
          v-model="form.mail"
          placeholder="请输入邮箱地址"
          maxlength="50"
          v-else
        ></el-input>
      </el-form-item>
      <el-form-item label="所在地区" required>
        <div
          style="display: flex; justify-content: space-between; width: 400px"
        >
          <el-form-item
            prop="province"
            :rules="[{ required: true, message: '不能为空' }]"
          >
            <el-select
              v-model="form.province"
              placeholder="请选择"
              @change="provinceChange"
            >
              <el-options
                v-for="item in provinceOptions"
                :key="item"
                :label="item"
                :value="item"
              ></el-options>
            </el-select>
          </el-form-item>
          <el-form-item
            prop="city"
            :rules="[{ required: true, message: '不能为空' }]"
          >
            <el-select
              v-model="form.city"
              placeholder="请选择"
              v-if="
                form.province != '香港特别行政区' &&
                form.province != '澳门特别行政区'
              "
            >
              <el-options
                v-for="item in cityOptions"
                :key="item"
                :label="item"
                :value="item"
              ></el-options>
            </el-select>
          </el-form-item>
        </div>
      </el-form-item>
      <el-form-item label>
        <div class="operation">
          <div class="submit cursor" @click="submitFn()">提交</div>
          <div class="cancels cursor" @click="cancleFn">取消</div>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  mounted() {
    this.getUsename();
    this.getAllProvince();
  },
  data() {
    return {
      informationNew: false,
      form: {},
      provinceOptions: [],
      cityOptions: [],
      useInfor: {},
      roles: "",
      userRole: "",
      centerDialogVisible: false,
    };
  },
  methods: {
    handleDelInfo() {
      this.axios
        .delete("/user/deregisterUser")
        .then((res) => {
          if (res.data.code === 200) {
            this.$message({
              message: "注销成功！将在15个工作日内注销个人信息。",
              type: "success",
            });
          } else {
            this.$message({
              message: res.data.message,
              type: "error",
            });
          }
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        })
        .finally(() => {
          this.centerDialogVisible = true;
        });
    },
    getUsename() {
      this.axios
        .get("/user/getUserInfo")
        .then((res) => {
          this.useInfor = res.data.result;
          if (res.data.result.roles.indexOf("user") === -1) {
            this.roles = "noUser";
          }
          this.$store.commit("changeStatus", this.useInfor);
          this.userRole = res.data.result.roleName.join(" ");
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    changeInfor() {
      this.informationNew = true;
      this.form = JSON.parse(JSON.stringify(this.useInfor || {}));
    },
    canleFn() {
      this.informationNew = false;
      this.form = {};
    },
    getAllProvince() {
      this.axios.get("/user/getAllProvince").then((res) => {
        this.provinceOptions = res.data.result;
      });
    },
    provinceChange(data) {
      this.form.data.city = "";
      this.axios
        .get("/user/getCityByProvince", {
          params: {
            province: data,
          },
        })
        .then((res) => {
          this.cityOptions = res.data.result;
        });
    },
    submitFn() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.axios
            .post("/user/modifyUserInfo", this.form)
            .then((response) => {
              if (response.data.code === 200) {
                this.$emit("changeName", "");
                this.informationNew = false;
                this.getUsename();
                this.$message({ message: "提交成功", type: "success" });
              } else {
                this.$message.error(response.data.message);
              }
            });
        }
      });
    },
  },
};
</script>
<style lang="less" scoped>
.personDetails {
  padding: 48px;
  .title {
    font-size: 36px;
    margin-bottom: 24px;
    .delInfo {
      font-size: 14px;
      color: #002fa7;
      cursor: pointer;
    }
  }
  .personContent {
    .Incomplete {
      font-size: 14px;
      color: #555;
    }
    .buttton {
      width: 144px;
      height: 48px;
      background: #000;
      color: #fff;
      margin-top: 40px;
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
    .account {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 14px;
      .name {
        width: 328px;
        color: #555;
        span {
          color: #000;
          margin-left: 20px;
        }
      }
      .management {
        color: #0002fa;
        cursor: pointer;
      }
    }
  }
}
</style>
<style lang="less">
.personDetails {
  .el-inpput {
    width: 400px;
    height: 36px;
    line-height: 36px;
    .el-input_inner {
      height: 36px;
      line-height: 36px;
      border-radius: 0px;
    }
  }
  .el-select {
    .el-input {
      width: 192px;
      height: 36px;
      line-height: 36px;
    }
    .el-input_inner {
      height: 36px;
      line-height: 36px;
      border-radius: 0px;
    }
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
    }
    .submit {
      width: 144px;
      height: 48px;
      background: #000;
      color: #fff;
      margin-right: 23px;
    }
  }
}
</style>
