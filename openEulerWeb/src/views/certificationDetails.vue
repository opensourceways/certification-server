<template>
  <div class="certificationDetails">
    <div class="content">
      <div class="title">
        <span style="color:#555555" @click="goBack" class="cursor">openEuler软件兼容性测评服务</span>
        <i class="el-icon-arrow-right"></i>
        <span style="font-weight: bold">{{ contentTitle }}</span>
      </div>
      <ProcessProgress :titleList="processList"></ProcessProgress>
      <ProcessProgressDetails
          :handlerName="handlerName"
          :processList="processList"
          :id="uuid"
          :processProgress="processProgress"
          :processStatus="processStatus"
      ></ProcessProgressDetails>
    </div>
  </div>
</template>

<script>
import ProcessProgress from '@/components/processProgress.vue'
import ProcessProgressDetails from '@/components/processProgressDetails.vue'

export default {
  name: "certificationDetails",
  comments: {
    ProcessProgress,
    ProcessProgressDetails,
  },
  data() {
    return {
      handlerName: '',
      contentTitle: '测评详情',
      uuid: '',
      processList: [],
      processProgress: '',
      processStatus: '',
    }
  },
  created() {
    this.uuid = this.$route.query.id
  },
  mounted() {
    this.getUsename()
    this.getNode()
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
    getUsename() {
      this.axios
          .get('/user/getUserInfo', {})
          .then((res) => {
            this.useName = res.data.result
            this.$store.commit('changeStatus', this.useName)
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message)
          })
    },
    getNode() {
      this.axios
          .get('/software/node', {
            params: {
              softwareId: this.uuid,
            },
          })
          .then((res) => {
            this.processList = res.data.result
            this.processList.forEach((item) => {
              if (item.handlerResult === '已驳回') {
                this.processProgress = item.nodeName
                this.processStatus = item.handlerResult
                return
              }
              if (item.handlerResult === '待处理') {
                this.processProgress = item.nodeName
                this.processStatus = item.handlerResult
                this.handlerName = item.handlerName
                return
              }
              if (item.handlerResult === '通过' && item.nodeName === '证书签发') {
                this.processProgress = '证书签发'
                this.processStatus = '通过'
                return
              }

            })
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message)
          })
    },
  },
}
</script>
<style lang="less" scoped>
.certificationDetails {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;

  .content {
    width: 1416px;
    line-height: 96px;
    font-size: 12px;

    i {
      margin: 0 4px;
    }
  }
}
</style>
<style lang="less">
.certificationDetails {
}
</style>