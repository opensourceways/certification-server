<template>
  <div class="home" style="min-height: 1040px">
    <PictureMain
        :enName="enName"
        :cnNameTitle="cnNameTitle"
        :processName="processName"
        :personRole="personRole"
    ></PictureMain>
    <UncertifiedApplication
        v-if="!certificaBoole &&
          $store.state.useName.roleNames &&
          !$store.state.useName.roleNames.includes('OSV伙伴') &&
          $store.state.useName.roleNames &&
          !$store.state.useName.roleNames.includes('欧拉社区旗舰店')
         "
        v-loading="dataLoading"
    ></UncertifiedApplication>
    <CertificationApplication
        v-else-if="personRole === '伙伴'"
        v-loading="dataLoading"
    ></CertificationApplication>
    <CertificationApplicationHuawei
        v-else-if="personRole === '华为'"
    ></CertificationApplicationHuawei>
  </div>
</template>

<script>
import PictureMain from "@/components/pictureMain.vue"
import UncertifiedApplication from "@/components/uncertifiedApplication.vue"
import CertificationApplication from "@/components/certificationApplication.vue"
import CertificationApplicationHuawei from "@/components/certificationApplicationHuawei.vue"

export default {
  name: "HomeView",
  comments: {
    PictureMain,
    UncertifiedApplication,
    CertificationApplication,
    CertificationApplicationHuawei,
  },
  data() {
    return {
      enName: "CERTIFICATION SERVICE",
      cnNameTitle: "openEuler兼容性测评服务",
      processName: "申请技术测评",
      certificaBoole: false,
      personRole: "",
      useName: "",
      dataLoading: true,
    };
  },
  created() {
    this.getUsename();
  },
  methods: {
    getUsename() {
      this.axios
          .get("/user/getUserInfo", {})
          .then((res) => {
            if (res.data.code === 401) {
              this.getLogin();
            } else {
              this.useName = res.data.result;
              this.$store.commit("changeStatus", this.useName);
              if (this.useName.roles.indexOf("user") != "-1") {
                this.personRole = "伙伴";
              } else {
                this.personRole = "华为";
              }
              this.getTableList();
            }
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
    getLogin() {
      this.axios
          .get("/auth/login", {})
          .then((response) => {
            if (response.data.code === 200) {
              window.location.href = response.data.result;
            }
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
    async getTableList() {
      let params = {
        pageNum: 1,
        pageSize: 10,
        productName: "",
        productType: [],
        selectMyApplication: [],
        status: [],
        testOrganization: [],
      };
      let url = "/software/softwareList";
      if (this.personRole === "华为") {
        url = "/software/reviewSoftwareList";
      } else {
        url = "/software/softwareList";
      }
      this.axios
          .post(url, params)
          .then((response) => {
            if (response.data.code === 200) {
              if (response.data.result.list.length > 0) {
                this.certificaBoole = true;
              } else {
                this.certificaBoole = false;
              }
              this.dataLoading = false;
            } else {
              this.$message.error(response.data.message);
              this.dataLoading = false;
            }
          })
    },
  },
};
</script>
