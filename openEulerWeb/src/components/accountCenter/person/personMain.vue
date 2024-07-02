<template>
  <div class="personMain">
    <PersonDetails @changeName="changeName"></PersonDetails>
  </div>
</template>
<script>
import PersonDetails from "./personDetails.vue";
export default {
  name: "personMain",
  components: {
    PersonDetails,
  },
  methods: {
    changeName() {
      this.$emit("changeName", "");
    },
  },
};
</script>
<style lang="less" scoped>
.personMain {
  width: 1056px;
  background: #fff;
}
</style>
