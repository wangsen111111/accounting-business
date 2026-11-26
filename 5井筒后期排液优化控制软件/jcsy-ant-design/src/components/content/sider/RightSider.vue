<template>
  <a-card >
    <a-button type="primary" block> 快速访问 </a-button>
    <a-button
      block
      v-for="(obj,index ) in HotLinks "
      :key="index"
      style="margin-top:10px"
      @click="toQuickPage()">
      <!-- <hot-icon :width="'15px'" :height="'15px'" alt="word"></hot-icon> -->
      <a-icon type="wifi" />
      <span style="marin-left:10px" >链接1</span>
    </a-button>
  </a-card>
</template>

<script>
import { hotIcon } from '@/core/icons'
export default {
    data () {
        return {
          HotLinks: ['链接', '链接', '链接', '链接', '链接', '链接', '链接', '链接', '链接', '链接', '链接', '链接']
        }
    },
    methods: {
      toQuickPage () {
        this.$router.push({ name: 'QuickPage' })
      }
    },
    components: {
      hotIcon
    },
    created () {

    },
    mounted () {

    }
}
</script>

<style scoped lang="less">

</style>
