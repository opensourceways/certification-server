<template>
  <div id="app" @click="useChage=false">
    <div class="navigat">
      <div class="content">
        <img :src="require('./assets/images/logo.svg')" alt @click="goBack" class="cursor" style="width: 133px"/>
        <div class="use">
          <span class="usename" @click.stop="useChage=!useChage" v-if="useName">{{ useName }}</span>
          <div class="personalCenter" v-if="useChage && useName">
            <span @click="goRouter('accountCenter')" class="cursor">账号中心</span>
            <div class="line"></div>
            <span @click="exitsLogin" class="cursor">退出登录</span>
          </div>
        </div>
      </div>
    </div>
    <router-view v-if="loading"></router-view>
    <div class="bottomLogo">
      <img :src="require('@/assets/images/footerLogo.svg')" alt/>
      <div class="content">
        <div class="policy">
          <router-link to="/priacyPolicy" target="_blank">
            <span class="text">隐私政策</span>
          </router-link>
          <div class="line"></div>
          <router-link to="/legalNotice" target="_blank">
            <span class="text">法律声明</span>
          </router-link>
          <div class="line"></div>
          <router-link to="/cookieProtocols" target="_blank">
            <span class="text">Cookie协议</span>
          </router-link>
        </div>
        <div class="copyright">版权所有 @ 2022 openEuler 保留一切权利</div>
      </div>
    </div>
    <cookieNotice></cookieNotice>
  </div>
</template>

<script>
import cookieNotice from './views/cookieNotice.vue'

export default {
  components: {
    cookieNotice
  },
  data() {
    return {
      useChage: false,
      loading: false
    }
  },
  computed: {
    useName() {
      return this.$store.state.useName.username
    },
  },
  mounted() {
    this.loading = false
    this.axios.get('/user/getUsrInfo').finally(() => {
      this.loading = true
    })
  },
  methods: {
    goPush(type) {
      this.$router.push('/' + type)
    },
    goRouter(data) {
      if (data === 'accountCenter') {
        this.$router.push('/accountCenter')
      } else if (data === 'login') {
        this.$router.push('/')
      } else if (data === 'registration') {
        this.$router.push('/useRegistration')
      } else {
        this.$router.push(data)
      }
    },
    exitsLogin() {
      this.axios
          .get('/auth/logout')
          .then((response) => {
            if (response.data.code === 200) {
              window.localStorage.removeItem('user')
              this.$store.commit('changeStatus', {})
              this.useChage = !this.useChage
              window.location.href = response.data.result
            }
          })
    },
    goBack() {
      this.axios
          .get('/user/getUserInfo', {})
          .then((res) => {
            if (res.data.result.roles.indexOf('china_region') != -1) {
              this.$router.push('/enterpriseAudit')
            } else if (res.data.result.roles.indexOf('admin') != -1) {
              this.$router.push('/adminApproval')
            } else {
              this.$router.push('/')
            }
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message || err?.data?.message)
          })
    },
  },
}
</script>

<style lang="less">
.cursor {
  cursor: pointer;
}

#app {
  .navigat {
    height: 80px;
    background: #ffffff;
    display: flex;
    justify-content: center;

    .content {
      width: 1416px;
      height: 80px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .use {
        height: 80px;
        font-size: 14px;
        position: relative;
        font-weight: 400;

        .usname {
          line-height: 80px;
          color: #002fa7;
          cursor: pointer;
        }

        .personalCenter {
          width: 120px;
          height: 80px;
          position: absolute;
          top: 81px;
          right: 0px;
          box-shadow: 0px 2px 8px 0px rgba(0, 0, 0, 0.08);
          background: #ffffff;
          padding: 10px 0;
          display: flex;
          flex-direction: column;
          justify-content: space-around;
          align-items: center;
          z-index: 100;

          .line {
            width: 80px;
            height: 1px;
            background: #dddddd;
          }
        }
      }
    }
  }

  .bottomLogo {
    position: relative;
    margin-top: 104px;
    height: 104px;
    background: #ffffff;
    display: flex;
    justify-content: center;
    align-items: center;

    img {
      position: absolute;
      left: 20%;
    }

    .content {
      .policy {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-bottom: 20px;

        .text {
          font-size: 14px;
          font-family: FZLTXIHJW, FZLTXIHJW-Regular;
          font-weight: 400;
          cursor: pointer;
          color: #000000;
        }

        .line {
          width: 1px;
          height: 20px;
          background: #4f5661;
          margin: 0 15px;
        }
      }

      .copyright {
        opacity: 0.7;
        font-size: 12px;
        font-family: FZLTXIHJW, FZLTXIHJW-Regular;
        font-weight: 400;
      }
    }
  }
}
</style>
