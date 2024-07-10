<template>
  <div style="width: 100%">
    <div class="banner">
      <span class="legal">COOKIE PROTOCOLS</span>
      <span class="privacy">openEuler兼容性平台Cookie政策</span>
    </div>
    <div class="main">
      <p class="title">（一）Cookies</p>
      <p class="text-first">Cookie是由Web服务器创建并存储在计算机或移动设备上的文本文件。Cookie的内容只能由创建Cookie的服务器检索或读取。Cookie对于您正在使用的浏览器或移动应用程序是唯一的。它们通常包含标识符、站点名称、数字和字符。</p>
      <p class="text">Cookie服务于不同的目的，例如帮助我们了解网站如何被使用，让您高效地浏览页面，记住您的偏好并总体上改善您的浏览体验。</p>
      <p class="text">我们可能会同时使用会话Cookie（该Cookie将在您关闭网络浏览器后失效）和永久Cookie（根据适用的法律法规，该Cookie将在您的计算机或移动设备上保留六个月，或者直到您撤回同意或删除它们为止）。</p>
      <p class="text">我们使用一种类型的Cookie：</p>
      <p class="text bold">(1) 严格必要的Cookie: 用于登录和验证。当您使用Uniportal帐号登录网站时，Cookie将确保您对该网站的访问尽可能顺利；</p>
      <p class="text">a). uuid</p>
      <p class="text">来源：openEuler兼容性平台</p>
      <p class="text">到期时间：会话</p>
      <p class="text">目的：维护openEuler兼容性平台用户登录会话信息。</p>
      <p class="text">本服务无广告类型的Cookie。</p>
      <p class="title">（二）其他类似的技术</p>
      <p class="text-first">除Cookie之外，我们可能会使用其他技术来自动收集信息。</p>
      <p class="text">浏览器网络存储：我们可能会使用浏览器网络存储（包括通过HTML5），也称为本地存储对象，从而达到与Cookie类似的目的。浏览器Web存储可以存储比Cookie更多的数据量。您的网络浏览器可能提供清除浏览器网络存储的功能。</p>
      <p class="title">（三）请勿追踪（Do Not Track）</p>
      <p class="text-first">很多网络浏览器均提供 Do Not Track 功能，该功能可向网站发布 Do Not Track 请求。目前，主要互联网标准组织尚未设立相关政策来规定网站应如何处理此类请求。</p>
      <p class="text">如果您启用了“请勿追踪”或您的浏览器可能提供的其他类似功能，高校人才发展社区将不会更改本声明中所述的收集和使用您数据的方式。但是，我们保留对您的“请勿追踪”请求做出回应并停止收集您的数据而不另行通知的权利。</p>
      <p class="title">（四）您的选择</p>
      <p class="text-first">大多数浏览器允许您删除或拒绝Cookie。为此，请按照浏览器设置中的说明进行操作。默认情况下，许多浏览器接受Cookie，直到您更改设置为止。</p>
      <p class="text">如果您不接受我们的Cookie，您在使用本网站时可能会遇到一些不便。例如，我们可能无法识别您的计算机或移动设备，并且您可能需要在每次访问网站时登录。</p>
      <p class="title">（五）联系我们</p>
      <p class="text-first">如果您有更多关于Cookie的问题，您可以通过contact@openeuler.io和我们取的联系。</p>
    </div>
  </div>
</template>

<script>
export default {};
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