<template>
  <div class="enterpriseMain">
    <EnterpriseIndex
      v-if="enterType == '未完善'"
      @handleChange="handleChanging"
    ></EnterpriseIndex>
    <EnterpriseCerting v-else-if="enterType == '测评中'"></EnterpriseCerting>
    <EnterpriseFail
      v-else-if="enterType == '测评失败'"
      @handleChange="handleChangDel"
    ></EnterpriseFail>
    <EnterpriseSuccess v-else-if="enterType == '测评成功'"></EnterpriseSuccess>
    <EnterpriseAuto
      v-if="enterType == '开始'"
      @handleChange="handleChange"
    ></EnterpriseAuto>
    <EnterpriseWhite
      v-if="enterType == '空白'"
      @handleChange="handleChange"
    ></EnterpriseWhite>
  </div>
</template>
<script>
import EnterpriseIndex from "./enterpriseIndex.vue";
import EnterpriseCerting from "./enterpriseCerting.vue";
import EnterpriseFail from "./enterpriseFail.vue";
import EnterpriseSuccess from "./enterpriseSuccess.vue";
import EnterpriseAuto from "./enterpriseAuto.vue";
import EnterpriseWhite from "./enterpriseWhite.vue";
export default {
  name: "enterpriseMain",
  components: {
    EnterpriseIndex,
    EnterpriseCerting,
    EnterpriseFail,
    EnterpriseSuccess,
    EnterpriseAuto,
    EnterpriseWhite,
  },
  mounted() {
    this.getCurrentUser();
  },
  data() {
    return {
      enterType: "空白",
    };
  },
  methods: {
    getCurrentUser() {
      this.axios
        .get("/companies/company/currentUser")
        .then((res) => {
          if (res.data.result.status == null) {
            this.enterType = "开始";
          } else if (res.data.result.status == 0) {
            this.enterType = "测评中";
          } else if (res.data.result.status == 1) {
            this.enterType = "测评成功";
          } else if (res.data.result.status == 2) {
            this.enterType = "测评失败";
          } else {
            this.enterType = "空白";
          }
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
    handleChange(type) {
      this.enterType = type;
    },
    handleChanging(type) {
      this.enterType = type;
    },
    handleChangDel(type) {
      this.enterType = type;
    },
  },
};
</script>
<style lang="less" scoped>
.enterpriseMain {
  width: 1056px;
  background: #fff;
}
</style>
