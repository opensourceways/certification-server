<template>
  <div
    :class="
      cnNameTitle === 'openEuler兼容性测评服务'
        ? 'pictureMain'
        : 'pictureMain newPicture'
    "
  >
    <div class="mainTitle">
      <div class="enName">{{ enName }}</div>
      <div class="cnName">
        <div class="cnNameTitle">{{ cnNameTitle }}</div>
        <div
          class="cnNameContent"
          v-if="
            cnNameTitle === 'openEuler兼容性测评服务' &&
            processName === '申请技术测评' &&
            personRole === '伙伴'
          "
        >
          查看<span @click="goCertificationDetails" class="cursor"
            >测评流程</span
          >
        </div>
      </div>
      <div
        class="processRedirection cursor"
        @click="goRedirection"
        v-if="processName && personRole === '伙伴'"
      >
        <span style="margin-left: 20px">{{ processName }}</span>
        <div class="Arrowhead">
          <i class="el-icon-right"></i>
        </div>
      </div>
      <div
        class="signed"
        v-if="
          cnNameTitle === 'openEuler兼容性测评服务' &&
          processName === '申请技术测评' &&
          personRole === '伙伴'
        "
      >
        <div class="top">
          您还可以签署openEuler社区贡献者许可协议（CLA）参与到社区贡献中
        </div>
      </div>
      <el-dialog
        title="提示"
        :visible.sync="centerDialogVisible"
        width="550px"
        center
        top="40vh"
      >
        <span>申请兼容性测评服务需要完成企业实名认证</span>
        <div class="button cursor" @click="goPerson">
          <span style="margin-left: 20px">企业实名认证</span>
          <div class="Arrowhead">
            <i class="el-icon-right"></i>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    enName: {
      type: String,
      value: "CERTIFICATION SERVICE",
    },
    cnNameTitle: {
      type: String,
      value: "openEuler兼容性测评服务",
    },
    processName: {
      type: String,
      value: "申请技术测评",
    },
    personRole: {
      type: String,
      value: "伙伴",
    },
  },
  data() {
    return {
      centerDialogVisible: false,
    };
  },
  methods: {
    goHere() {
      window.open("https://www.openeuler.org/zh/community/contribution");
    },
    goCertificationDetails() {
      window.open("https://gitee.com/openeuler/technical-certification");
    },
    goRedirection() {
      if (this.processName === "申请技术测评") {
        this.axios
          .get("/user/isNeedCompanyAuthentication")
          .then((res) => {
            if (res.data.result) {
              this.$router.push("/technicalCertification");
            } else {
              this.centerDialogVisible = true;
            }
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
      }
      if (this.processName === "审批配置") {
        return;
      }
    },
    goPerson() {
      this.$router.push({
        path: "/accountCenter",
        query: {
          checkedModel: "企业信息",
        },
      });
    },
  },
};
</script>
<style lang="less" scoped>
.pictureMain {
  height: 380px;
  background: url("../assets/images/openEuler_banner.png") center no-repeat;
  display: flex;
  justify-content: center;
  .mainTitle {
    width: 1416px;
    height: 380px;
    position: relative;
    .enName {
      font-size: 60px;
      opacity: 0.1;
      position: absolute;
      top: 32px;
      left: 0;
    }
    .cnName {
      position: absolute;
      top: 78px;
      left: 0;
      .cnNameTitle {
        font-size: 54px;
        float: left;
      }
      .cnNameContent {
        font-size: 16px;
        color: #555;
        float: left;
        line-height: 96px;
        margin-left: 20px;
        span {
          color: #002fa7;
          cursor: pointer;
        }
      }
    }
    .processRedirection {
      width: 218px;
      height: 48px;
      background: #002fa7;
      position: absolute;
      top: 170px;
      left: 0;
      color: #fff;
      display: flex;
      justify-content: space-around;
      align-items: center;
      cursor: pointer;
      .Arrowhead {
        width: 24px;
        height: 24px;
        text-align: center;
        line-height: 24px;
      }
    }
    .signed {
      position: absolute;
      top: 250px;
      left: 0;
      font-size: 16px;
      .bottom {
        margin-top: 6px;
        color: #555;
        span {
          color: #002fa7;
        }
      }
    }
    .button {
      width: 192px;
      height: 48px;
      background: #000;
      color: #fff;
      margin-top: 46px;
      display: flex;
      justify-content: space-around;
      align-items: center;
      cursor: pointer;
      .Arrowhead {
        width: 24px;
        height: 24px;
        border: 1px dashed #fff;
        text-align: center;
        line-height: 24px;
      }
    }
  }
}
.newPicture {
  background-image: url("../assets/images/openEuler_new.png");
}
</style>
<style lang="less">
.pictureMain {
  .el-dialog__wrapper {
    .el-dialog__header {
      border-bottom: 1px dashed;
    }
    .el-dialog__body{
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }

  }
}
</style>
