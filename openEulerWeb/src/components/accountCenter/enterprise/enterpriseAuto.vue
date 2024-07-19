<template>
    <div class="enterpriseAuto">
        <div class="title">企业信息</div>
        <div class="content">伙伴申请技术测评服务需完善企业信息</div>
        <div class="button cursor" @click="goPerson">
            <span style="margin-left:20px">企业实名认证</span>
            <div class="Arrowhead">
                <i class="el-icon-right"></i>
            </div>
        </div>
    </div>
</template>
<script>
export default {
    methods:{
        goPerson(){
            this.$emit('handleChange','未完善')
        }
    }
}
</script>
<style lang="less" scoped>
.enterpriseAuto{
    padding: 48px;
    .title{
        font-size: 36px;
        margin-bottom: 24px;
    }
    .content{
        font-size: 14px;
        color: #555555;
    }
    .button{
        width: 192px;
        height: 48px;
        background: #000000;
        color: #ffffff;
        margin-top: 46px;
        display: flex;
        justify-content: space-around;
        align-items: center;
        cursor: pointer;
        .Arrowhead{
            width: 24px;
            height: 24px;
            border: 1px dashed #ffffff;
            text-align: center;
            line-height: 24px;
        }
    }
}
</style>