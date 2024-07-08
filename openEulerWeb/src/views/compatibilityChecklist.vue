<template>
  <div style="width: 100%">
    <div class="banner">
      <span class="legal">USAGE STATEMENT</span>
      <span class="privacy">兼容性清单使用说明</span>
    </div>
    <div class="main">
      <p class="top text">
        &nbsp;&nbsp;&nbsp;&nbsp;感谢您对openEuler社区（以下简称”社区“）（以下简称”项目“）的支持。在您填写和上传兼容您的感谢您对openEuler商业发行版的软、硬件解决方案清单（以下简称”兼容清单“）之前，请务必仔细阅读本声明。点击”我已阅读并同意“，即表明您同意接受本声明的以下所有条款。如果您不同意本声明，则您不得上传兼容性清单。
      </p>
      <p class="top text">
        &nbsp;&nbsp;&nbsp;&nbsp;本声明不会改变您将上传的兼容性清单用于任何其他目的的权利。
      </p>
      <p class="text">
        1、根据本声明，您特此授予”社区“和"社区"用户永久的、非独占的、全球性的、免费的、免授权费的、不可撤销的许可，以复制、使用、公开展示和分发您的兼容性清单。
      </p>
      <p class="text">
        2、您上传的兼容性清单需要遵照社区提供的数据模板要求，该数据模板由openEuler社区负责维护。
      </p>
      <p class="text">
        3、您承诺，您在法律上有授予上述许可的权利。如果您的雇主或第三方对您上传的兼容性清单享有权利，您承诺该雇主或第三方已授权您上传该兼容性清单。
      </p>
      <p class="text">
        4、您需保证所提供的兼容性清单的真实性和准确性，社区仅”按原样“展示您上传的兼容性清单，不附带任何明示或默示担保及条件（包括但不限于任何权利担保、非侵权保证，适销性保证、适用性保证或条件）。
      </p>
      <p class="text">
        5、OSV伙伴上传的兼容性清单社区不得用于除上述用途描述外的其他用途。
      </p>
      <p class="text">
        6、如需要对您的兼容性清单作出任何更改，您应当通知”社区“。
      </p>
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    goHere() {
      window.open(
          "https://www.openatom.org/#/privacyPolicy/0.15906735691316976"
      );
    },
  },
};
</script>

<style lang="less" scoped>
.banner {
  width: 1900px;
  background: url("../assets/images/openEuler_banner.png") center;
  height: 380px;
  position: relative;
  margin: 0 auto;

  .legal {
    position: absolute;
    top: 136px;
    left: 251px;
    font-size: 60px;
    font-family: HuaweiSansMedium, HuaweiSansMedium-Regular;
    font-weight: 400;
    text-align: left;
    color: rgba(0, 47, 167, 0.07);
    line-height: 60px;
  }

  .privacy {
    position: absolute;
    top: 166px;
    left: 251px;
    font-size: 54px;
    font-family: FZLTXIHJW, FZLTXIHJW-Regular;
    font-weight: 400;
    text-align: left;
    color: #000000;
    line-height: 76px;
  }
}

.main {
  width: 880px;
  margin: 40px auto;
  font-family: FZLTHJW FZLTHJW-Regular;
  padding-bottom: 96px;
  text-align: justify;

  .top {
    margin-top: 40px;
    font-size: 14px;
    line-height: 22px;
  }

  .title {
    margin-top: 40px;

    font-size: 18px;
    font-weight: bold;
  }

  .text-first {
    font-size: 14px;
    margin-top: 16px;
    color: #555555;
    line-height: 22px;
  }

  .text {
    font-size: 14px;
    margin-top: 16px;
    color: #555555;
    line-height: 22px;
  }

  .bold {
    font-weight: bold;
    color: #000000;
  }

  .left {
    margin-left: 16px;
  }

  .blue {
    color: #0836aa;
  }
}
</style>