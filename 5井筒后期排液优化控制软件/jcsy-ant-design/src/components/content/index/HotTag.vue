<template>
  <div>
    <div v-for="item in tagAndFileCount" :key="item">
      <span style="margin-left:10px;font-size:16px" >{{ item }} </span>
    </div>
  </div>

</template>

<script>
import infiniteScroll from 'vue-infinite-scroll'
import { getHotTagWithFileCount } from '@/api/indexPage'
import { teamIcon, sharingCenterIcon, docIcon, knowledgeBaseIcon, homePageIcon, workSurfaceIcon, collectionIcon, focusOnIcon, recentBrowseicon, recycleBinIcon } from '@/core/icons'
export default {
  directives: { infiniteScroll },
    components: {
        teamIcon,
        sharingCenterIcon,
        docIcon,
        knowledgeBaseIcon,
        homePageIcon,
        workSurfaceIcon,
        collectionIcon,
        focusOnIcon,
        recentBrowseicon,
        recycleBinIcon
    },
    data () {
        return {
          busy: false,
           tagAndFileCount: []
        }
    },
    methods: {
            handleInfiniteOnLoad () {
        const data = this.tagAndFileCount
        if (data.length > 5) {
          this.$message.warning('Infinite List loaded all')
          this.busy = true
        }
      }

    },
    created () {

    },
    mounted () {
          getHotTagWithFileCount().then(res => {
            res.data.forEach(item => this.tagAndFileCount.push(item.name + ' (' + item.value + ')'))
          }).catch(err => { console.log(err) })
    }
}
</script>

<style scoped lang="less">
// div{
//  margin-top: 7px;
//  margin-left: 10px;
// }
</style>
