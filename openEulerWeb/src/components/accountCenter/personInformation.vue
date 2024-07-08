<template>
  <div class="personInformation">
    <div class="personAvatar">
      <div class="avatar">
        <img :src="require('@/assets/images/img_photos_default.svg')" alt="" />
        <span class="account">
          账号：
          <span>{{ useInfor["username"] }}</span>
        </span>
        <div class="line"></div>
      </div>
      <div class="navigatVertical">
        <div
          :class="checkedModel === '个人资料' ? 'model active' : 'model'"
          @click="checkedModel = '个人资料'"
          class="cursor"
        >
          <img :src="require('@/assets/images/user.png')" alt="" />
          <span>个人资料</span>
        </div>
        <div
          :class="checkedModel === '企业信息' ? 'model active' : 'model'"
          @click="checkedModel = '企业信息'"
          class="cursor"
          v-if="roles === 'user'"
        >
          <img :src="require('@/assets/images/enterprise.png')" alt="" />
          <span>企业信息</span>
        </div>
      </div>
    </div>
    <PersonMain
      v-if="checkedModel === '个人资料'"
      @changeName="getUsename()"
    ></PersonMain>
    <EnterpriseMain v-else-if="checkedModel === '企业信息'"></EnterpriseMain>
  </div>
</template>
<script>
import PersonMain from "./person/personMain.vue";
import EnterpriseMain from "./enterprise/enterpriseMain.vue";

export default {
  components: {
    PersonMain,
    EnterpriseMain,
  },
  data() {
    return {
      checkedModel: "个人资料",
      useInfor: {},
      roles: "",
    };
  },
  mounted() {
    this.getUsename();
    if (this.$route.query.checkedModel) {
      this.checkedModel = this.$route.query.checkedModel;
    }
  },
  methods: {
    getUsename() {
      this.axios
        .get("/user/getUserInfo")
        .then((res) => {
          this.useInfor = res.data.result;
          if(res.data.result.roles.indexOf('user'!='-1')){
            this.roles='user'
          }
        })
        .catch((err) => {
          this.$message.error(err?.response?.data?.message);
        });
    },
  },
};
</script>
<style lang="less" scoped>
.personInformatiion{
  width: 1416px;
  min-height: calc(100vh - 260px);
  display: flex;
  justify-content: space-between;
  margin-bottom: 40px;
  .personAvatar{
    width: 336px;
    background: #fff;
    .avatar{
      width: 336px;
      background: #fff;
      .account{
        color: #555;
        font-size: 14px;
        margin: 24px 0 16px 0;
        span{
          color: #000;
        }
      }
      .line{
        width: 288px;
        height: 1px;
        background: #e3e6ed;
      }
    }
    .navigatVertical{
      padding: 0 26px;
      .model{
        width: 256px;
        height: 72px;
        display: flex;
        justify-content: flex-start;
        align-items: center;
        padding: 0 16px;
        img{
          border: 1px dashed;
          width: 22px;
          height: 22px;
          margin-right: 16px;
          
        }
        span{
          font-size: 16px;
          color: #555;
          line-height: 72px;
        }
      }
      .active{
        border-left: 2px solid #002fa7;
        font-weight: bold;
      }
    }
  }
}
</style>