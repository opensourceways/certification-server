<template>
    <div class="uncertifiedApplication">
        <div class="content">
            <div class="myCertified">
                <span>测评申请</span>
            </div>
            <div class="imgCertified">
                <img :src="required('@/assets/images/computer.svg')" alt="">
                <div class="uncertified">您还未进行过测评申请</div>
            </div>
        </div>
    </div>
</template>
<style lang="less" scoped>
.uncertifiedApplication{
    display: flex;
    justify-content: center;
    .content{
        width: 1416px;
        .myCertified{
            height: 120px;
            text-align: center;
            line-height: 120px;
            span{
                font-size: 36px;
            }
        }
        .imgCertified{
            height: 320px;
            background: #fff;
            display: flex;
            justify-content: center;
            align-items: center;
            flex-direction:column ;
            .uncertified{
                font-size: 14px;
                color: #555;
                margin-top: 20px;
            }
        }
    }
}
</style>