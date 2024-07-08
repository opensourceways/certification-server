<template>
  <div>
    <div
        class="notice-container"
        v-if="showNotice&& !isEn"
        ref="noticeContainer"
    >
      <div class="notice-content">
        <div class="tip-content">
          <p class="tip-title">openEuler兼容性平台重视您的隐私</p>
          <span class="tip-text">我们在本网站上使用Cookie,包括第三方Cookie，以便网站正常运行和提升浏览体验。单击”接受全部“即表示您同意这些目的；单击”拒绝全部“即表示您拒绝非必要的Cookie;单击”管理Cookie“以选择接受或拒绝某些Cookie。需要了解更多信息或随时更改您的 Cookie 首选项，请参阅我们的</span>
          <span class="link" @click="goCookie">《openEuler兼容性平台Cookie政策》</span>。
        </div>
        <div class="right-btn">
          <div class="btn" @click="setupCookie(true)">全部接受</div>
          <div class="btn" @click="setupCookie(false)">全部拒绝</div>
          <div class="btn" @click="cookieVisible = true">管理Cookie</div>
        </div>
        <img
            class="close-notice"
            @click="closeNoticeZh"
            src="https://r.huaweistatic.com/s/ascendstatic/lst/tip/close.svg"
            alt=""
        />
      </div>
    </div>
    <!--中文弹窗-->
    <div class="cookie-dialog" v-if="cookieVisible" @mousewheel.prevent>
      <div class="dialog-box">
        <img
            src="https://r.huaweistatic.com/s/ascendstatic/lst/tip/close.svg"
            alt=""
            class="close-icon"
            @click="closeCookieVisible"
        />
        <p class="title">管理Cookie</p>
        <div>
          <div class="item-box">
            <span class="item-title">必要Cookie</span>
            <span class="item-text">始终启用</span>
          </div>
          <P class="item-desc">
            这些Cookie是网站正常工作所必需的，不能我们的系统中关闭。他们通常仅是为了响应您的服务请求而设置的，例如登录或填写表单。您可以将浏览器设置为阻止Cookie来拒绝这些Cookie，但网站的某些部分将无法正常工作。这些Cookie不存储任何个人身份信息。</P>
        </div>
        <div>
          <div class="item-box item-two">
            <span class="item-title">统计分析Cookie</span>
            <div class="switch-wrap" :class="{'switch-checked' : isChecked}" @click="isChecked = !isChecked">
              <div class="switch-handler"></div>
            </div>
          </div>
          <p class="item-desc">我们将根据您的同意使用和处理这些非必要的Cookie。这些Cookie允许我们获得摘要统计数据，例如，统计访问量和访问来源，便于我们改进我们的网站。</p>
        </div>
        <div class="dialog-btn">
          <div class="item-btn" @click="setupCookie(isChecked)">保存设置</div>
          <div class="item-btn" @click="isChecked = true; setupCookie(isChecked)">全部允许</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {mapMutations, mapState} from 'vuex'

export default {
  name: "cookieNotice",
  data() {
    return {
      isEn: false,
      acceptall: true,
      checked1: true,
      checked2: false,
      cookiepolicy: false,
      cookieVisible: false,
      isChecked: false,
    }
  },
  watch: {
    $route: {
      handler() {
        const state = this.isEn
            ? this.getCookie('agreed-cookiepolicy-en')
            : this.getCookie('agreed-cookiepolicy')
        this.setShowcookietips(!state)
      },
      immediate: true,
    }
  },
  mounted() {
    const state = this.isEn
        ? this.getCookie('agreed-cookiepolicy-en')
        : this.getCookie('agreed-cookiepolicy')
    this.setShowcookietips(!state)
    if (!state) {
      this.$router.onReady(() => {
        this.getPrivacyContentH()
      })
    } else {
      this.setCookieState(true)
    }
  },
  computed: {
    ...mapState('cookieCenter', ['showcookietips']),
    showNotice() {
      return this.showcookietips
    },
  },
  methods: {
    ...mapMutations('cookieCenter', [
      'setCookieState',
      'setPrivacyContentH',
      'setShowcookietips',
    ]),
    goCookie() {
      const routeData = this.$router.resolve({name: 'cookieProtocols'})
      window.open(routeData.href, '_blank')
    },
    //中文Cookie
    setupCookie(state) {
      this.setShowcookietips(false)
      this.cookieVisible = false
      const date = new Date()
      date.setMonth(date.getMonth() + 12)
      document.cookie = `agreed-cookiepolicy=${state ? '2' : '1'};path=/; expires=${date.toUTCString()}`
      this.setCookieState(true)
      this.setPrivacyContentH(0)
    },
    closeCookieVisible() {
      if (this.isChecked) {
        this.setupCookie(this.isChecked);
      } else {
        this.cookieVisible = false
      }
    },
    closeNoticeZh() {
      this.setShowcookietips(false)
    },
    getCookie(key) {
      const arr = document.cookie.split('; ')
      //eslint-disable-next-line guard-for-in
      for (const index in arr) {
        const resArr = arr[index].split('=')
        if (resArr[0] == key) {
          return resArr[1]
        }
      }
    },
    getPrivacyContentH() {
      this.$nextTick(() => {
        this.setPrivacyContentH(this.$refs.noticeContainer.offsetHeight)
      })
    },
  },
}
</script>

<style scoped lang="less">
.notice-container {
  width: 100%;
  position: fixed;
  bottom: 0;
  z-index: 1997;
  padding: 16px 0;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 0 8px 0 rgba(18, 20, 23, 0.10);
  backdrop-filter: blur(5px);

  .notice-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 1416px;
    margin: 0 auto;
    position: relative;

    .tip-content {
      .tip-title {
        font-size: 14px;
        color: #000;
        line-height: 22px;
        font-weight: 500;
        margin-bottom: 2px;
      }

      .tip-text {
        font-size: 12px;
        color: rgba(0, 0, 0, 0.80);
        line-height: 18px;
      }

      .link {
        font-size: 12px;
        color: #2e53fa;
        line-height: 18px;

        &:hover {
          text-decoration: underline;
        }
      }
    }

    .right-btn {
      display: flex;
      flex-shrink: 0;
      margin-left: 56px;

      .btn {
        border: 1px solid #202329;
        border-radius: 28px;
        padding: 4px 15px;
        font-size: 14px;
        color: #000;
        line-height: 22px;
        margin-left: 16px;
        cursor: pointer;
        transition: all .2s cubic-bezier(.2, 0, 0, 1);

        &:hover {
          border: 1px solid #373b42;
          color: #fff;
          background-color: #373b42;
        }
      }
    }

    .close-notice {
      position: absolute;
      top: -8px;
      right: 0;
      cursor: pointer;
      transition: all 250ms cubic-bezier(0, 0, 0, 1);

      &:hover {
        transform: rotate(180deg);
      }
    }
  }
}

.notice-container-en {
  position: fixed;
  bottom: 0;
  z-index: 1997;
  width: 100%;
  padding: 16px 0;
  background: #e6e8ef;
  box-shadow: 0 0 8px 0 rgba(18, 20, 23, 0.10);

  .notice-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 1416px;
    margin: 0 auto;

    .tip-content {
      .link {
        color: #022ea7;

        &:hover {
          text-decoration: underline;
        }
      }
    }

    .btn-container {
      display: flex;
      align-items: center;
      flex-shrink: 0;

      .accept-btn {
        font-size: 14px;
        color: #000000;
        letter-spacing: 0;
        line-height: 22px;
        cursor: pointer;
      }

      .cookiesetting {
        font-size: 14px;
        color: #000000;
        line-height: 22px;
        margin-left: 32px;
        cursor: pointer;
      }
    }
  }
}

.cookie-dialog {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  z-index: 1999;

  .dialog-box {
    width: 930px;
    padding: 32px;
    border-radius: 16px;
    box-sizing: border-box;
    position: relative;
    top: calc(50% - 242px);
    left: calc(50% - 465px);
    background-color: #fff;

    .close-icon {
      position: absolute;
      width: 24px;
      top: 12px;
      right: 12px;
      cursor: pointer;
    }

    .title {
      font-size: 24px;
      color: #000;
      text-align: center;
      line-height: 32px;
      font-weight: 500;
    }

    .item-box {
      margin-top: 24px;
      display: flex;
      align-items: center;

      .item-title {
        font-size: 20px;
        color: #000;
        line-height: 28px;
        font-weight: 500;
      }

      .item-text {
        font-size: 14px;
        color: rgba(0, 0, 0, 0.60);
        line-height: 22px;
        margin-left: 24px;
      }

      .switch-warp {
        background-color: #e0e2e6;
        border-radius: 16px;
        width: 40px;
        height: 24px;
        transition: background-color .2s cubic-bezier(.2, 0, 0, 1);
        margin-left: 24px;
        cursor: pointer;
        position: relative;

        .switch-handler {
          background-color: #fff;
          border-radius: 50%;
          width: 16px;
          height: 16px;
          position: absolute;
          left: 4px;
          top: 50%;
          transform: translateY(-50%);
          transition: left .2s cubic-bezier(.2, 0, 0, 1);
        }
      }

      .switch-checked {
        background-color: #121212;

        .switch-handler {
          left: 20px;
        }
      }
    }

    .item-desc {
      font-size: 16px;
      color: rgba(0, 0, 0, 0.80);
      line-height: 24px;
      margin-top: 12px;
    }

    .dialog-btn {
      display: flex;
      justify-content: center;
      margin-top: 40px;

      .item-btn {
        border: 1px solid #202329;
        border-radius: 28px;
        padding: 7px 23px;
        font-size: 16px;
        color: #000;
        line-height: 24px;
        cursor: pointer;
        transition: all .2s cubic-bezier(.2, 0, 0, 1);

        &:hover {
          border: 1px solid #373b42;
          color: #fff;
          background-color: #373b42;
        }

        &:last-of-type {
          margin-left: 16px;
        }
      }
    }
  }
}

@media screen and (min-width: 768px) and (max-width: 1440px) {
  .notice-container {
    .notice-content {
      width: 1080px;

      .close-notice {
        position: absolute;
        top: -6px;
        right: 0;
        cursor: pointer;
        transition: all 250ms cubic-bezier(0, 0, 0, 1);

        &:hover {
          transform: rotate(180deg);
        }
      }
    }

    .link:hover {
      text-decoration: underline;
    }
  }

  .notice-container-en {
    .notice-content {
      width: 1080px;

      .tip-content {
        max-width: 700px;
      }
    }
  }
}

@media screen and (min-width: 768px) and (max-width: 1112px) {
  .notice-container {
    .notice-content {
      width: calc(100% - 32px);
    }
  }

  .notice-container-en {
    .notice-content {
      width: calc(100% - 32px);
    }
  }
}

@media screen and  (max-width: 767px) {
  .notice-container {
    background: #fff;
    box-shadow: 0 0 8px 0 rgba(18, 20, 23, 0.10);
    border-top-left-radius: 8px;
    border-top-right-radius: 8px;
    padding: 16px 24px;
    box-sizing: border-box;

    .notice-content {
      width: 100%;
      display: block;

      .tip-content {
        .tip-title {
          text-align: center;
        }

        .tip-text {
          font-size: 12px;
          color: rgba(0, 0, 0, 0.80);
          line-height: 18px;
          display: inline-block;
        }

        .link:hover {
          text-decoration: underline;
        }
      }

      .right-btn {
        margin-left: 0;
        display: block;

        .btn {
          margin-left: 0;
          margin-top: 12px;
          border: 1px solid #202329;
          border-radius: 28px;
          font-size: 14px;
          color: #000;
          text-align: center;
          line-height: 18px;
        }
      }

      .close-notice {
        transition: all 250ms cubic-bezier(0, 0, 0, 1);
        width: 16px;
        right: -12px;
        top: -9px;

        &:hover {
          transform: rotate(180deg);
        }
      }
    }
  }

  .notice-container-en {
    height: auto;
    padding: 0;

    .notice-content {
      height: auto;
      padding: 12px 16px;
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      width: 100%;
      box-sizing: border-box;

      .tip-content {
        font-size: 12px;
        color: #303030;
        text-align: left;
        line-height: 18px;

        .link {
          display: inline-block;
        }
      }

      .btn-container {
        margin-top: 12px;

        .accept-btn {
          font-size: 12px;
          color: #000000;
          letter-spacing: 0;
          text-align: center;
          line-height: 18px;
        }

        .cookiesetting {
          font-size: 12px;
          color: #000000;
          letter-spacing: 0;
          line-height: 18px;
        }
      }
    }
  }

  .cookie-dialog {
    .dialog-box {
      width: 100%;
      height: auto;
      padding: 16px 24px;
      border-radius: 8px 8px 0 0;
      top: 100%;
      left: 0;
      bottom: 0;
      transform: translateY(-100%);

      .close-icon {
        display: none;
      }

      .title {
        font-size: 18px;
        line-height: 26px;
      }

      .item-box {
        margin-top: 12px;
        justify-content: space-between;

        .item-title {
          font-size: 14px;
          line-height: 22px;
        }

        .item-text {
          font-size: 12px;
          line-height: 18px;
        }
      }

      .item-two {
        margin-top: 16px;
      }

      .item-desc {
        font-size: 12px;
        color: rgba(0, 0, 0, 1);
        line-height: 18px;
        margin-top: 8px;
      }

      .dialog-btn {
        margin-top: 12px;

        item-btn {
          border: none;
          border-radius: 0;
          padding: 8px 0;
          width: 140px;
          text-align: center;

          &:hover {
            border: none;
            color: #000;
            background-color: #fff;
          }

          &:first-of-type {
            margin-right: 16px;
          }

          &:last-of-type {
            margin-left: 16px;
            position: relative;

            &::before {
              content: '';
              position: absolute;
              width: 1px;
              height: 24px;
              top: 8px;
              left: -16px;
              background-color: #e3e3e3;
            }
          }
        }
      }
    }
  }
}

@media screen and (min-width: 1201px) and (max-width: 1440px) {
  .notice-container {
    padding: 12px 0;

    .notice-content {
      width: calc(100% - 114px);
    }
  }
}
</style>