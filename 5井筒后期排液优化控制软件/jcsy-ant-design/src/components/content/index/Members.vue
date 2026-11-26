<template>
  <div >
    <!-- <div>
      <h1 style="font-size:24px">课题成员</h1>
    </div> -->
    <div
      class="demo-infinite-container"
      infinite-scroll-disabled="false"
      :infinite-scroll-distance="10">
      <div v-for="item in names" :key="item">
        <workSurfaceIcon :width="'16px'" :height="'16px'" alt="word" />
        <span style="margin-left:10px;font-size:16px" >{{ item }} </span>
      </div>
    </div>

  </div>

</template>

<script>
import { teamIcon, sharingCenterIcon, docIcon, knowledgeBaseIcon, homePageIcon, workSurfaceIcon, collectionIcon, focusOnIcon, recentBrowseicon, recycleBinIcon } from '@/core/icons'
var names = []
for (var i = 0; i < 5; i++) {
    names.push('成员' + i + '(16)')
}
export default {
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
            names: names
        }
    },
    methods: {
        changeMindMap (key) {

        }

    },
    created () {

    },
    mounted () {

    }
}
</script>

<style scoped lang="less">
// div{
//  margin-top: 7px;
//  margin-left: 10px;
// }
</style>
