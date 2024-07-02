<template>
    <div class="enterpriseFail">
        <img :src="require('../../../assets/images/fail.svg')" alt="">
        <div class="title">企业实名认证申请失败</div>
        <div class="failedMessage">失败原因：{{approvalComment}}</div>
        <div class="submit cursor" @click="goPerson">重新提交</div>
    </div>
</template>
<script>
export default {
    data(){
        return{
            approvalComment:'',
        }
    },
    mounted(){
        this.getCurrentUser()
    },
    methods:{
        getCurrentUser(){
            this.axios.get('/companies/company/currentUser').then((res)=>{
                this.approvalComment=res.data.result.approvalComment
            }).catch((err)=>{
                this.$message.error(err?.response?.data?.message)
            })
        },
        goPerson(){
            this.$emit('handleChange','未完善')
        }
    }
}
</script>
<style lang="less" scoped>
.failedMessage{
    width: 676px !important;
    word-break: break-all;
    text-align: center;
}
.enterpriseFail{
    margin-top: 95px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    .title{
        font-size: 32px;
        margin-bottom: 16px;
    }
    .submit{
        width: 144px;
        height: 48px;
        background: #000;
        color: #fff;
        margin-top: 40px;
        text-align: center;
        line-height: 48px;
        cursor: pointer;
    }
}
</style>