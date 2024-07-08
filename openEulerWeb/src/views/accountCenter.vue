<template>
  <div class="accountCenter">
    <div class="content">
      <div class="title">
        <span style="color: #555555" @click="goBack" class="cursor">openEuler软件兼容性测评服务</span>
        <i class="el-icon-arrow-right"></i>
        <span style="font-weight: bold">账号中心</span>
      </div>
      <div class="main">
        <PersonalInformation></PersonalInformation>
      </div>
    </div>
  </div>
</template>

<script>
import PersonalInformation from '@/components/accountCenter/personInformation.vue'


export default {
  name: "accountCenter",
  components: {PersonalInformation},
  comments: {
    PersonalInformation,
  },
  data() {
    return {}
  },
  mounted() {
    this.getUsename()
  },
  methods: {
    getUsename() {
      this.axios
          .get('/user/getUserInfo', {})
          .then((res) => {
            let useName = res.data.result
            this.$store.commit('changeStatus', useName)
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message)
          })
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
            this.$message.error(err?.response?.data?.message)
          })
    },
  },
}
</script>
<style lang="less" scoped>
.accountCenter {
  display: flex;
  justify-content: center;

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

    .main {
      display: flex;
      justify-content: space-between;
    }
  }
}
</style>