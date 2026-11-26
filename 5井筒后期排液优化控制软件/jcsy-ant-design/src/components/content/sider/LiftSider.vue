<template>
  <div>
    <!-- <a-card class="left-menu-item">
      <dl>
        <dd>
          <homePageIcon :width="'13px'" :height="'13px'" alt="word" />
          <span style="margin-left:10px" :class="{yellow:flag == 'index'}" @click="toPage('index')">首页</span>
        </dd>
        <dd>
          <workSurfaceIcon :width="'13px'" :height="'13px'" alt="word" />
          <span style="margin-left:10px" :class="{yellow:flag == 'WorkSurface'}" @click="toPage('WorkSurface')">工作面板</span>
        </dd>
      </dl><hr />
      <dl>
        <dd>
          <docIcon :width="'13px'" :height="'13px'" alt="word" />
          <span style="margin-left:10px" :class="{yellow:flag == 'Doc'}" @click="toPage('Doc')">文档</span>
        </dd>
        <dd>
          <docIcon :width="'13px'" :height="'13px'" alt="word" />
          <span style="margin-left:10px" :class="{yellow:flag == 'Tag'}" @click="toPage('Tag')">标签</span>
        </dd>
        <dd>
          <knowledgeBaseIcon :width="'13px'" :height="'13px'" alt="word" />
          <span style="margin-left:10px" :class="{yellow:flag == 'KnowledgeBase'}" @click="toPage('KnowledgeBase')">知识库</span>
        </dd>
      </dl> <hr />
      <dl>
        <dd>
          <team-icon :width="'13px'" :height="'13px'" alt="word" />
          <span style="margin-left:10px" :class="{yellow:flag == 'Team'}" @click="toPage('Team')">团队</span>
        </dd>
        <dd>
          <collectionIcon :width="'13px'" :height="'13px'" alt="word" />
          <span style="margin-left:10px" :class="{yellow:flag == 'Collection'}" @click="toPage('Collection')">收藏</span>
        </dd>
        <dd>
          <focusOnIcon :width="'13px'" :height="'13px'" alt="word" />
          <span style="margin-left:10px" :class="{yellow:flag == 'FocusOn'}" @click="toPage('FocusOn')">关注</span>
        </dd>
        <dd>
          <recentBrowseicon :width="'13px'" :height="'13px'" alt="word" />
          <span style="margin-left:10px" :class="{yellow:flag == 'RecentBrowse'}" @click="toPage('RecentBrowse')">最近浏览</span>
        </dd>
      </dl><hr />
      <dl>
        <dd>
          <recycleBinIcon :width="'13px'" :height="'13px'" alt="word" />
          <span style="margin-left:10px" :class="{yellow:flag == 'RecycleBin'}" @click="toPage('RecycleBin')">回收站</span>
        </dd>
      </dl>
    </a-card> -->

    <a-card >
      <a-button type="primary" block> 标签检索 </a-button>
      <a-row :gutter="20">
        <a-col :span="12" v-for="(p, index) in tags" :key="index">
          <a-tag style="margin-top:20px" @click="toTagPage(index)">标签{{ index }}</a-tag>
        </a-col>
      </a-row>
      <br />
    </a-card>
  </div>
</template>

<script>
import { teamIcon, sharingCenterIcon, docIcon, knowledgeBaseIcon, homePageIcon, workSurfaceIcon, collectionIcon, focusOnIcon, recentBrowseicon, recycleBinIcon } from '@/core/icons'
var tags = []
for (var i = 0; i < 20; i++) {
  tags.push('标签' + i)
}
export default {
  data () {
    return {
      tags: tags
    }
  },
  computed: {
    flag () {
      return this.$route.name
    }
  },
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
  methods: {
    toTagPage (data) {
      this.$router.push({
        name: 'TagPage',
        params: {
          id: data
        }
      }) // path: `/page/TagPage/${data}`
    },
    toPage (data) {
      switch (data) {
        case 'index':
          this.$router.push({ name: 'index' })
          console.log(this.$route.name)
          break
        case 'WorkSurface':
          this.$router.push({ name: 'WorkSurface' })
          console.log(this.$route.name)
          break
        case 'Doc':
          this.$router.push({ name: 'Doc' })
          break
        case 'Tag':
          this.$router.push({ name: 'Tag' })
          break
        case 'KnowledgeBase':
          this.$router.push({ name: 'KnowledgeBase' })
          break
        case 'Team':
          this.$router.push({ name: 'Team' })
          break
        case 'FocusOn':
          this.$router.push({ name: 'FocusOn' })
          break
        case 'Collection':
          this.$router.push({ name: 'Collection' })
          break
        case 'RecentBrowse':
          this.$router.push({ name: 'RecentBrowse' })
          break
        case 'RecycleBin':
          this.$router.push({ name: 'RecycleBin' })
          break
        default:
      }
    }
  },
  created () {
    console.log(this.flag.name)
  },
  mounted () {}
}
</script>

<style scoped lang="less">
.post-click-color {
  cursor: pointer;
}
.yellow {
  color: #faad14;
}
.left-menu-item {
  & span {
    &:hover {
      color: rgb(32, 160, 255);
      cursor: pointer;
    }
  }
}

.left-tag-item {
  & span {
    &:hover {
      color: rgb(32, 160, 255);
      cursor: pointer;
    }
  }
}
</style>
