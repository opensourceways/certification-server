<template>
  <div class="redirectJump" v-loading="redirectJump"></div>
</template>

<script>
export default {
  created() {
    this.token = this.$route.query.token;
    window.localStorage.setItem("user", this.token)
    this.redirectJump = false;
    this.getUsename();
    setTimeout(() => {
      this.exitsLogin();
    }, 7200000);
  },
  data() {
    return {
      redirectJump: true,
      token: "",
    };
  },
  methods: {
    getUsename() {
      this.axios
          .get("/user/getUserInfo", {})
          .then((res) => {
            if (
                res.data.code === 500 &&
                res.data.message === "You haven't signed the privacy protocol yet"
            ) {
              this.$message.error(
                  "您没有签署隐私政策协议，请先签署隐私政策协议后重试！"
              );
              setTimeout(() => {
                this.$router.push({
                  path: "/priacyPolicy",
                  query: {toSign: "true"},
                });
              }, 2000);
              return;
            }
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
          });
    },
    exitsLogin() {
      this.axios
          .get("/auth/logout")
          .then((response) => {
            if (response.data.code === 200) {
              window.localStorage.removeItem("user");
              this.$store.commit("changeStatus", {});
              window.location.href = response.data.result;
            }
          })
    },
  },
};
</script>

<style lang="less">
.redirectJump {
  width: 100%;
  height: 100%;
}
</style>