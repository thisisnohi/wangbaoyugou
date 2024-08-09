pptx.vue<template>
  <div class="layout-container">
    <div class="layout-container-form">
      <iframe
          src='https://view.officeapps.live.com/op/view.aspx?src=https%3A%2F%2Fwaibaoyugou.com%2Foffice%2F20240809.docx'
          width='1000px' height='800px' frameborder='1'>
      </iframe>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";

export default defineComponent({
  name: "officeView",
  components: {},

  setup() {
    const loading = ref(false);
    return {
      loading,
    };
  },
});
</script>

<style lang="scss">
.layout-container {
  height: 100%;
  margin: 0 0 0 10px;
  width: calc(100% - 10px);
}

.el-table tr.error {
  background: #f5a623;
}
</style>
