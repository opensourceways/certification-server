<template>
  <div class="certificationDetailsHuawei">
    <div class="content">
      <div class="title">
      <span style="color: #555555" @click="goBack" class="cursor"
      >openEuler软件兼容性测评服务</span>
        <i class="el-icon-arrow-right"></i>
        <span style="font-weight: bold">{{ contentTitle }}</span>
      </div>
      <ProcessProgress :titleList="titleList"></ProcessProgress>
      <ProcessProgressDetailsHuawei
          :sign="sign"
          :certificates="certificates"
          :softwareId="softwareId"
          :node="node"
          :handlerName="handlerName"
          :titleList="titleList"
          :forms="form"
          :testReport="testReport"
          @getNode="getNode"
          @getRecords="getauditRecordsList"
      ></ProcessProgressDetailsHuawei>
    </div>
  </div>
</template>

<script>
import ProcessProgress from '@/components/processProgress.vue'
import ProcessProgressDetailsHuawei from '@/components/processProgressDetailsHuawei.vue'

export default {
  name: "certificationDetailsHuawei",
  comments: {
    ProcessProgress,
    ProcessProgressDetailsHuawei,
  },

  data() {
    return {
      handlerName: "",
      sign: {},
      certificates: [],
      contentTitle: "测评审核",
      //节点数据
      titleList: [],
      //测评信息
      form: {},
      //审核记录
      records: [],
      //测试报告
      testReport: [],
      softwareId: 0,
      node: "",
    };
  },
  created() {
    this.softwareId = this.$route.query.softwareId;
    this.getNode();
    this.getCertification();
    this.getAttachments();
    this.getUse();
  },
  methods: {
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
    //获取节点数据
    getNode() {
      this.axios
          .get(`software/node?softwareId=${this.softwareId}`, {})
          .then((res) => {
            this.titleList = res.data.result
            this.titleList.forEach((item) => {
              if (item.handlerResult === "待处理") {
                this.node = item.nodeName;
                this.handlerName = item.handlerName;
                return;
              }
            });
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
    //获取测评信息
    getCertification() {
      this.axios
          .get(`software/findById?id=${this.softwareId}`, {})
          .then((res) => {
            this.form = res.data.result;
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
    //获取审核记录
    getauditRecordsList() {
      this.axios
          .get(
              `software/auditRecordsList?softwareId=${this.softwareId}&nodeName=&curPage=1&pageSize=10`,
              {}
          )
          .then((res) => {
            this.records = res.data.result.records;
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
    //获取测试报告名称
    getAttachments() {
      this.axios
          .get(
              `software/getAttachments?softwareId=${this.softwareId}&fileType=testReport`,
              {}
          )
          .then((res) => {
            this.testReport = res.data.result;
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
      this.axios
          .get(
              `software/getAttachments?softwareId=${this.softwareId}&fileType=certificates`,
              {}
          )
          .then((res) => {
            this.certificates = res.data.result;
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
  },
};
</script>

<style lang="less" scoped>
.certificationDetailsHuawei {
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
  }
}
</style>