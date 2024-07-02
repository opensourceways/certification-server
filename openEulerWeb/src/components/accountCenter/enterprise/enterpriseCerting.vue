<template>
    <div class="enterpriseCerting">
        <img :src="require('../../../assets/images/application.svg')" alt="">
        <div class="title">我们正在审核您的企业实名认证申请</div>
        <div>审核时间为1~3个工作日，请您耐心等待，结果将以短信的方式通知您</div>
        <div>申请时间：{{applyTime}}</div>
    </div>
</template>
<script>
export default {
    data(){
        return{
            applyTime:'',
        }
    },
    mounted(){
        this.getCurrentUser()
    },
    methods:{
        getCurrentUser(){
            this.axios.get('/companies/company/currentUser').then((res)=>{
                this.applyTime=res.data.result.applyTime
            }).catch((err)=>{
                this.$message.error(err?.response?.data?.message)
            })
        }
    }
}
</script>
<style lang="less" scoped>
.enterpriseCerting{
    padding: 48px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    .title{
        font-size: 32px;
        margin-bottom: 16px;
    }
}
</style>