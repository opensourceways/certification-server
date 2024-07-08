<template>
  <div class="newApproval">
    <div class="content">
      <div class="title">
        <span style="color: #555555">审批配置</span>
        <i class="el-icon-arrow-right"></i>
        <span style="font-weight: bold">{{ contentTitle }}</span>
      </div>
      <div class="contentTitle">{{ contentTitle }}</div>
      <div class="main">
        <el-form ref="form" :model="form" label-width="150px" label-position="left">
          <el-form-item label="审批路径" required>
            <el-select v-model="form.value" placeholder="请选择创新中心">
              <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="方案审核人员" required>
            <el-select v-model="form.value" placeholder="请选择">
              <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="报告初审人员" required>
            <el-select v-model="form.value" placeholder="请选择">
              <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="报告复审人员" required>
            <el-select v-model="form.value" placeholder="请选择">
              <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="证书初审人员" required>
            <el-select v-model="form.value" placeholder="请选择">
              <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="证书复审人员" required>
            <el-select v-model="form.value" placeholder="请选择">
              <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label>
            <div class="operation">
              <div class="cancels cursor" @click="informationNew=false">取消</div>
              <div class="submit cursor" @click="informationNew=false">提交</div>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "newApproval",
  comments: {},
  data() {
    return {
      form: {},
      options: [],
      contentTitle: '新增审批路径',
    }
  },
  mounted() {
    this.getUsename()
  },
  methods: {
    getUsename() {
      this.axios
          .get('/user/getUserInfo', {})
          .then((res) => {
            if (res.data.code === 200) {
              this.$store.commit('changeStatus', res.data.result)
            }
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message)
          })
    }
  }
}
</script>

<style lang="less" scoped>
.newApproval {
  display: flex;
  justify-content: center;

  .content {
    width: 1416px;
    line-height: 96px;
    font-size: 12px;

    i {
      margin: 0 4px;
    }

    .contentTitle {
      font-size: 36px;
      text-align: center;
      margin-bottom: 40px;
    }

    .main {
      height: 628px;
      background: #ffffff;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
}
</style>
<style lang="less">
.newApproval {
  .el-input {
    width: 400px;

    .el-input__inner {
      height: 36px;
      line-height: 36px;
      border-radius: 0;
    }
  }

  .operation {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 400px;
    text-align: center;
    line-height: 48px;

    .cancels {
      width: 144px;
      height: 46px;
      border: 1px solid #000000;
      margin-right: 23px;
    }

    .submit {
      width: 144px;
      height: 48px;
      background: #000000;
      color: #ffffff;
    }
  }
}
</style>