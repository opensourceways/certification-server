<template>
  <div class="titleInput">
    <div class="title">{{ title }}</div>
    <el-input
      maxlength="64"
      v-model="input"
      :placeholder="placeholder"
      prefix-icon="el-icon-search"
      @change="goChange"
    ></el-input>
  </div>
</template>
<script>
export default {
  props: {
    title: {
      type: String,
      value: "",
    },
    placeholder: {
      type: String,
      value: "",
    },
  },
  data() {
    return {
      input: "",
    };
  },
  methods: {
    goChange(value) {
      this.$emit("inputChange", value);
    },
  },
};
</script>
<style lang="less" scoped>
.titleInput{
  margin-bottom:40px ;
  .title{
    height: 136px;
    font-size: 36px;
    line-height: 136px;
    text-align: center;
  }
}
</style>
<style lang="less">
.titleInput{
    .el-input{
        .el-input__inner{
            border-radius: 0px;
            height: 54px;
            line-height: 54px;
        }
        .el-input__icon{
            font-size: 20px;
            line-height: 56px;
        }
    }
    .el-input--prefix .el-input__inner{
        padding-left: 38px;
    }
}
</style>