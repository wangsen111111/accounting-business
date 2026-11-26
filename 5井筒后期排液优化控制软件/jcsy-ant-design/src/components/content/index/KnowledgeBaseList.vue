<template>
  <div >
    <div v-for="(item,index) in dataArr" :key="index" style="font-size:16px" class="text-overflow" :title="item.projectName + '('+item.fileCount+')'">
      {{ item.projectName }} {{ '('+item.fileCount+')' }}
    </div>
  </div>

</template>

<script>
// import { getProjectsAndFileCount } from '@/api/indexPage'
import { teamIcon, sharingCenterIcon, docIcon, knowledgeBaseIcon, homePageIcon, workSurfaceIcon, collectionIcon, focusOnIcon, recentBrowseicon, recycleBinIcon } from '@/core/icons'
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
    props: {
        dataArr: { type: Array,
            default: () => {
                return []
        } }
    },
    data () {
        return {
            projectAndFileCount: []
        }
    },
    methods: {
        changeMindMap (key) {

        }

    },
    created () {

    },
    mounted () {
          // getProjectsAndFileCount().then(res => {
          //   this.projectAndFileCount = res.data
          //   // res.data.forEach(item => this.projectAndFileCount.push(item.projectName + ' (' + item.fileCount + ')'))
          // }).catch(err => {
          //   console.log(err)
          // })
    }
}
</script>

<style scoped lang="less">
  .text-overflow{
    margin-top:0px;
    display: -webkit-box;/*作为弹性伸缩盒子模型显示*/
    -webkit-line-clamp: 1; /*显示的行数；如果要设置2行加...则设置为2*/
    overflow: hidden; /*超出的文本隐藏*/
    text-overflow: ellipsis; /* 溢出用省略号*/
    -webkit-box-orient: vertical;/*伸缩盒子的子元素排列：从上到下*/
  }
</style>
