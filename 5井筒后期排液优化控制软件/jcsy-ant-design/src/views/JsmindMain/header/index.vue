<template>
  <div class="jsmind-header">
    <!-- <a-row type="flex" justify="start" align="middle">
      <a-icon style="margin-left:0px" type="windows" :theme="'filled'" :style="{ fontSize: '40px', color: '#08c' }"/>
      <a-button class="button-menu" type="link" @click="menu('file')">文件</a-button>
      <a-button class="button-menu" type="link" @click="menu('insert')">插入</a-button>
      <a-button class="button-menu" type="link" @click="menu('task')">任务</a-button>
      <a-button class="button-menu" type="link" @click="menu('view')">视图</a-button>
      <a-button class="button-menu" type="link" @click="menu('all')">全部</a-button>
      <a-button class="button-menu" type="link" @click="menu('associated')">关联</a-button>
    </a-row> -->
    <a-tabs type="card" :animated="false" :tabBarGutter="10">
      <a-button
        shape="circle"
        :icon="showToolBar ? 'up-circle' : 'down-circle'"
        slot="tabBarExtraContent"
        @click="ClickShowToolBar"
      />
      <a-tab-pane key="0" tab="开始">
        <StartMenu v-if="showToolBar"></StartMenu>
      </a-tab-pane>
      <a-tab-pane key="1" tab="文件"></a-tab-pane>
      <a-tab-pane key="2" tab="插入"><StartMenu></StartMenu></a-tab-pane>
      <!-- <a-tab-pane key="3" tab="任务"> Content of Tab Pane 3 </a-tab-pane>
      <a-tab-pane key="4" tab="视图"> Content of Tab Pane 3 </a-tab-pane>
      <a-tab-pane key="5" tab="全部"> Content of Tab Pane 3 </a-tab-pane>
      <a-tab-pane key="6" tab="关联"> Content of Tab Pane 3 </a-tab-pane> -->
    </a-tabs>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'

import {
  tagIconIcon,
  lineIcon,
  recycleBinIcon,
  lineColorIcon,
  fillColorIcon,
  shapeOfIcon,
  themeIcon,
  progressOfIcon,
  shrinkIcon,
  copyIcon,
  removeIcon,
  outputIcon,
  mountIcon,
  uninstallIcon,
  inputIcon,
  sameLevelIcon,
  childLevelIcon,
  tagGroupIcon,
  tagIcon,
  serveJsmindIcon,
  openIcon,
  closeIcon,
  returnIcon,
  saveMindMapIcon,
  saveTemplateIcon,
  jsmindInputIcon,
  jsmindOutputIcon,
  recentOperationIcon,
  lockIcon,
  unlockIcon,
  fullIcon,
  sideIcon,
  hspaceIcon,
  vspaceIcon,
  pspaceIcon,
  seeIcon,
  localFileIcon,
  renameIcon
} from '@/core/icons'
import StartMenu from './startMenu'

export default {
  components: {
    tagIconIcon,
    lineIcon,
    recycleBinIcon,
    lineColorIcon,
    fillColorIcon,
    shapeOfIcon,
    themeIcon,
    progressOfIcon,
    shrinkIcon,
    copyIcon,
    removeIcon,
    outputIcon,
    mountIcon,
    uninstallIcon,
    inputIcon,
    sameLevelIcon,
    childLevelIcon,
    tagGroupIcon,
    tagIcon,
    serveJsmindIcon,
    openIcon,
    closeIcon,
    returnIcon,
    saveMindMapIcon,
    saveTemplateIcon,
    jsmindInputIcon,
    jsmindOutputIcon,
    recentOperationIcon,
    lockIcon,
    unlockIcon,
    fullIcon,
    sideIcon,
    hspaceIcon,
    vspaceIcon,
    pspaceIcon,
    seeIcon,
    localFileIcon,
    renameIcon,
    StartMenu
  },
  data () {
    return {}
  },
  computed: {
    ...mapGetters(['showToolBar'])
  },
  methods: {
    ...mapActions(['ToggleShowToolbar']),
    ClickShowToolBar () {
      this.ToggleShowToolbar(!this.showToolBar)
    },
    menu () {}
  }
}
</script>

<style lang="less">
.jsmind-header {
  background: #424a58;
}

.jsmind-header .ant-tabs-bar {
  margin-bottom: 0px;
}

.jsmind-header > .ant-tabs-card > .ant-tabs-content > .ant-tabs-tabpane {
  background: white;
  padding: 0px;
}

.jsmind-header > .ant-tabs-card > .ant-tabs-bar {
  border-color: transparent;
}

.jsmind-header > .ant-tabs-card > .ant-tabs-bar .ant-tabs-tab {
  border-color: transparent;
  background: transparent;
  color: white;
  font-size: 18px;
  // background: yellow;
}

.jsmind-header > .ant-tabs-card > .ant-tabs-bar .ant-tabs-tab-active {
  border-color: white;
  background: white;
  color: black;
  font-size: 25px;
}

.jsmind-tool-bar {
  padding: 5px;
}

.jsmind-tool-bar {
  .jsmind-menu-item {
    text-align: center;
    display: inline-block;
    margin-left: 15px;
    cursor: pointer;
    :hover {
      background: rgb(219, 219, 219);
      border-radius: 3px;
    }
    p {
      text-align: center;
      margin-bottom: 0px;
    }
  }
  .jsmind-menu-group {
    display: inline-block;
    margin-left: 15px;
    cursor: pointer;
    :hover {
      background: rgb(219, 219, 219);
      border-radius: 3px;
    }
    p {
      text-align: center;
      margin-bottom: 0px;
    }
  }
}
</style>
