<!--团队-->
<template>
  <div class="card-list" ref="content" >
    <operating-button @isFileListOrCart="getIsFileListOrCart"></operating-button>
    <FileLisrOrCart :isFileListOrCart="isFileListOrCart"></FileLisrOrCart>
  </div>
</template>

<script>
import OperatingButton from '@/components/content/OperatingButton/index'
import FileLisrOrCart from '@/components/content/FileLisrOrCart/index'
export default {
    data () {
        return {
          isFileListOrCart: true
        }
    },
    methods: {
      getIsFileListOrCart (data) {
        // 发送请求初始化新建文档表单所需数据
        this.isFileListOrCart = data
      }
    },
    created () {

    },
    mounted () {

    },
    components: {
      OperatingButton,
      FileLisrOrCart
  }
}
</script>

<style scoped lang="less">

</style>
