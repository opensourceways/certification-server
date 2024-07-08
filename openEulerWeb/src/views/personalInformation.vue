<template>
  <div style="width: 100%">
    <div class="banner">
      <span class="privacy">个人信息收集清单</span>
    </div>
    <div class="main">
      <p class="text">
        openEuler兼容性平台（以下简称我们）会严格遵从国家的法律法规要求，将合法合规融入产品设计和业务流程中，并采用业界先进的安全技术保证数据安全。同时我们非常重视您的个人信息和隐私保护，致力于捍卫每个人的隐私权利。</p>
      <p class="text">目前本应用涉及场景如下：</p>
      <p class="title">1.登录</p>
      <p class="text">收集目的：openEuler兼容性测评服务、生成“产品”兼容性报告和证书等</p>
      <p class="text">个人信息类型：帐号信息</p>
      <p class="text">收集的方式：第三方帐号共享</p>
      <p class="text">个人信息字段：Uniportal账号信息（帐号名、帐号ID）、<strong>手机号、邮件地址；</strong></p>

      <p class="title">2.基本资料维护</p>
      <p class="text">收集目的：个人通过网站提交个人数据，以便平台进行通知</p>
      <p class="text">个人信息类型：您主动提交的数据</p>
      <p class="text">收集的方式：用户输入</p>
      <p class="text">个人信息字段：用户名、所在地区（市区）；</p>

      <p class="title">3.企业实名认证</p>
      <p class="text">收集目的：申请加入openEuler兼容性平台，以便于我们进行资质审核</p>
      <p class="text">个人信息类型：您主动提交的数据</p>
      <p class="text">收集的方式：用户输入</p>
      <p class="text">个人信息字段：企业名称、企业徽标、工商注册国家/地区、法人代表、营业执照、统一社会信用代码、企业邮箱、注册资金、企业成立时间、营业期限、营业执照地址；</p>

      <p class="title">4.申请技术测评</p>
      <p class="text">收集目的：为了进行“产品”openEuler兼容性测评</p>
      <p class="text">个人信息类型：您主动提交的数据</p>
      <p class="text">收集的方式：用户输入</p>
      <p class="text">个人信息字段：“产品”名称、“产品”版本、产品类型、测试机构、操作系统类型、操作系统版本号、“产品”功能介绍、使用场景介绍、测试所用芯片型号、测试所用的服务器型号；</p>

      <p class="title">5.生成“产品”兼容性报告和证书</p>
      <p class="text">收集目的：为了生成“产品”openEuler兼容性报告和证书</p>
      <p class="text">个人信息类型：您主动提交的数据</p>
      <p class="text">收集的方式：用户输入</p>
      <p class="text">个人信息字段：企业徽标、“产品”名称、“产品”版本、操作系统类型、操作系统版本号、测试所用芯片的型号、测试所用的服务器型号、<strong>项目负责人姓名和签名</strong></p>
    </div>
  </div>
</template>

<script>
export default {}
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
    color: #0836AA;
  }
}

</style>