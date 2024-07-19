<template>
  <div class="processProgress">
    <div v-for="(item, index) in titleList" :key="index" class="main">
      <div class="processNumber">
        <div
          class="number"
          :class="{
            'number-red': item.handlerResult === '已驳回',
            'number-blue': item.handlerResult === '待处理',
            'number-pass': item.handlerResult === '通过',
          }"
        >
          <div
            class="inline inline-red"
            :class="{
              'number-red': item.handlerResult === '已驳回',
              'inline-blue': item.handlerResult === '待处理',
              'inline-pass': item.handlerResult === '通过',
            }"
          >
            {{ index + 1 }}
          </div>
        </div>
      </div>
      <div class="processName">
        <div class="nameModel">
          <span
            class="name"
            :class="{
              'name-red': item.handlerResult === '已驳回',
              'name-blue': item.handlerResult === '待处理',
              'name-pass': item.handlerResult === '通过',
            }"
            >{{ item.nodeName }}</span
          >
          <div
            class="line"
            :class="{ 'line-blue': item.handlerResult === '通过' }"
            v-show="item.nodeName != '证书签发'"
          ></div>
        </div>
        <div class="details">
          <div class="content">{{ item.handlerName }}</div>
          <div class="content">{{ item.handlerTime }}</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    titleList: {
      type: Array,
      value: [],
    },
  },
};
</script>
<style lang="less" scoped>
.processProgress {
  background: #fff;
  height: 120px;
  margin-bottom: 24px;
  padding-left: 50px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  .main {
    display: flex;
    justify-content: space-between;
    .processNumber {
      width: 27px;
      padding-top: 25px;
      margin-right: 10px;
      .number {
        width: 27px;
        height: 27px;
        background: #f5f7fb;
        border: 1px solid #6d7278;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        color: #6d7278;
        .inline {
          width: 20px;
          height: 20px;
          background: #f5f7fb;
          border-radius: 50%;
          text-align: center;
          line-height: 20px;
          color: #6d7278;
        }
      }
      .number-blue {
        border: 2px solid #002fa7;
        .inline-blue {
          background: #002fa7;
          color: white;
        }
      }
      .number-red {
        border: 2px solid #e42121;
        .inline-red {
          background: #e42121;
          color: white;
        }
      }
      .number-pass {
        border: 1px solid #002fa7;
        .inline-pass {
          color: #002fa7;
        }
      }
    }
    .processName {
      width: 126px;
      padding-top: 30px;
      .nameModel {
        display: flex;
        justify-content: space-between;
        align-items: center;
        .name {
          font-size: 14px;
          color: #6d7278;
        }
        .name-blue {
          color: #002fa7;
        }
        .name-red {
          font-weight: 400;
          color: #e42121;
        }
        .name-pass {
          font-weight: 400;
          color: #002fa7;
        }
        .line {
          width: 64px;
          height: 1px;
          background: #6d7278;
        }
        .line-blue {
          background: #002fa7;
        }
      }
      .details {
        margin-top: 10px;
        .content {
          font-size: 12px;
          color: #555;
        }
      }
    }
  }
}
</style>
