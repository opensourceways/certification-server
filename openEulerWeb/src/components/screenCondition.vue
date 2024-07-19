<template>
  <div class="approvalConfiguration">
    <div class="testOrganization">
      <div class="typeClass">
        <span class="left-title" v-if="textTitle === '申请人'">{{
          textTitle
        }}</span>
        <span class="left-title" v-else>{{ textTitle }}</span>
        <div class="right-type">
          <ul :class="open ? '' : 'ulClass'">
            <li
              @click="goClilkAll()"
              :class="all ? 'active' : ''"
              class="cursor"
            >
              <p>全部</p>
            </li>
            <li
              v-for="(item, index) in textList"
              :key="index"
              :class="item.active ? 'active' : ''"
            >
              <p @click="goClilk(item)" class="cursor">{{ item.name }}</p>
            </li>
          </ul>
        </div>
      </div>
      <div
        class="open-more cursor"
        @click="open = true"
        v-if="!open && (textTitle === '产品类型' || textTitle === '测试机构')"
      >
        <span>更多</span>
        <img :src="require('@/assets/images/bottomArrowhead.png')" alt="" />
      </div>
      <div
        class="open-more cursor"
        @click="open = false"
        v-else-if="open && (textTitle === '产品类型' || textTitle === '测试机构')"
      >
        <span>收起</span>
        <img :src="require('@/assets/images/topArrowhead.png')" alt="" />
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    textList: {
      type: Array,
      value: [],
    },
    textTitle: {
      type: String,
      value: "",
    },
  },
  data() {
    return {
      open: false,
      arr: [],
      all: true,
    };
  },
  methods: {
    goClilkAll() {
      if (this.all) {
        this.arr = [];
      } else {
        this.arr = [];
        this.all = true;
        this.textList.forEach((item) => {
          item.active = false;
        });
      }
      this.goParameters();
    },
    goClilk(item) {
      if (item.active) {
        for (var i = 0; i < this.arr.length; i++) {
            if(this.arr[i]===item.name){
                this.arr.splice(i,1)
            }
        }
        if(this.arr.length===0){
            this.all=true
        }
      }else{
        this.all=false
        this.arr.push(item.name)
      }
      item.active=!item.active
      this.goParameters()
    },
    goParameters(){
        this.$emit('handleChange',this.arr)
    }
  },
};
</script>
<style lang="less" scoped>
.approvalConfiguration{
    .testOrganization{
        background: #fff;
        position: relative;
        .typeClass{
            display: flex;
            margin: 0 24px;
            padding-bottom: 10px;

            .left-title{
                margin: 18px 30px 16px 16px;
                position: relative;
                color: #000;
                font-size: 14px;
                font-weight: bold;
                width: 56px;
            }
            .right-type{
                flex: 1;
                margin-right: 76px;
                display: flex;
                .ulClass{
                    max-height: 46px;
                    overflow: hidden;
                }
                ul{
                    display: flex;
                    padding-right: 18px;
                    flex-wrap:wrap ;
                    list-style: none;
                    margin: 0 0 7px 0;
                    li{
                        list-style: none;
                        margin-right: 17px;
                        font-size: 14px;
                        color: #555;
                        height: 32px;
                        margin-bottom: 10px;
                        p{
                            cursor: pointer;
                            display: inline-block;
                            padding: 4px 12px;
                            border: 1px solid transparent;
                        }
                    }
                    .active{
                        p{
                            color: #002fa7;
                            display: inline-block;
                            border: 1px solid #002fa7;
                            background-image: url('@/assets/images/triangulationRight.svg');
                            background-position: right bottom;
                            background-repeat: no-repeat;
                        }
                    }
                }
            }
        }
        .open-more{
            position: absolute;
            top: 17px;
            right: 24px;
            font-size: 14px;
            color: #022ea7;
            cursor: pointer;
            img{
                width: 16px;
                height: 16px;
                margin: 0 0 -3px 8px;
            }
        }
    }
}
</style>
