<template>
  <div class="adminApproval">
    <PictureMain
        :enName="enName"
        :cnMameTitle="cnNameTitle"
        :processName="processName"
    ></PictureMain>
    <div class="tabs">
      <div
          :class="tabValue === '审批配置' ? 'tab active' : 'tab'"
          @click="tabChange('审批配置')"
          class="cursor"
      >
        审批配置
      </div>
    </div>
    <ApprovalConfiguration></ApprovalConfiguration>
  </div>
</template>

<script>
import PictureMain from "@/components/pictureMain.vue"
import ApprovalConfiguration from "@/components/approvalConfiguration.vue"

export default {
  name: "adminApproval",
  components: {ApprovalConfiguration},
  comments: {
    PictureMain,
    ApprovalConfiguration,
  },
  data() {
    return {
      enName: "Approval Configuration",
      cnNameTitle: "审批配置",
      processName: "新增审批路径",
      tabValue: "审批配置",
    };
  },
  mounted() {
    this.getUsername();
  },
  methods: {
    tabChange(value) {
      this.tabValue = value;
    },
    getUsername() {
      this.axios
          .get("/user/getUserInfo", {})
          .then((res) => {
            let userName = res.data.result;
            this.$store.commit("changeStatus", userName);
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
  },
};
</script>

<style lang="less" scoped>
.adminApproval {
  .tabs {
    height: 48px;
    background: #fdfefe;
    box-shadow: 0px 1px 4px 0px #555555;
    display: flex;
    justify-content: center;
    align-items: center;

    .tab {
      height: 48px;
      width: 80px;
      color: #555555;
      text-align: center;
      line-height: 48px;
      margin-right: 40px;
      cursor: pointer;
    }

    .active {
      color: #002fa7;
      border-bottom: 1px solid #002fa7;
    }
  }
}
</style>