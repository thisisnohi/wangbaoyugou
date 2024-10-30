<template>
  <div class="layout-container">
    <div class="layout-container-form">
      <vue-office-docx
          :src="docx"
          style="height: 100vh;width: 100%"
          @rendered="rendered"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";
//引入VueOfficeDocx组件
import VueOfficeDocx from '@vue-office/docx'
//引入相关样式
import '@vue-office/docx/lib/index.css'

export default defineComponent({
  name: "officeView",
  components: {
    VueOfficeDocx
  },
  setup() {
    const loading = ref(false);
    const docx = ref('')
    docx.value = 'http://106.15.194.231/office/20240809.docx'
    const rendered = () => {
      console.log("渲染完成")
    };

    return {
      loading,
      docx,
      rendered,
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
