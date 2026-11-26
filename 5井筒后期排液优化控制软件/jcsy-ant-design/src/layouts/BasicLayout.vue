<!--
  BasicLayout：基础页面布局，包含了头部导航，侧边栏和通知栏：
-->
<template>
  <a-layout :class="['layout', device]">
    <!-- SideMenu  侧边栏-->
    <a-drawer
      v-if="isMobile()"
      placement="left"
      :wrapClassName="`drawer-sider ${navTheme}`"
      :closable="false"
      :visible="collapsed"
      @close="drawerClose">
      <side-menu
        mode="inline"
        :menus="menus"
        :theme="navTheme"
        :collapsed="false"
        :collapsible="true"
        @menuSelect="menuSelect">
      </side-menu>
    </a-drawer>

    <side-menu
      v-else-if="isSideMenu()"
      mode="inline"
      :menus="menus"
      :theme="navTheme"
      :collapsed="collapsed"
      :collapsible="true"
    ></side-menu>

    <a-layout :class="[layoutMode, `content-width-${contentWidth}`]" :style="{ paddingLeft: contentPaddingLeft, minHeight: '100vh' }">
      <!-- layout header  通知栏-->
      <global-header
        v-show="globalHeader"
        :mode="layoutMode"
        :menus="menus"
        :theme="navTheme"
        :collapsed="collapsed"
        :device="device"
        @toggle="toggle"/>
      <!-- <a-button type="link" class="but_type" style="right:200px;" @click.stop="()=> test()" >测试</a-button>
      <route-view />
      <div v-if="this.$router.history.current.name=='index'"></div> -->
      <!-- layout content -->
      <!-- 原来布局 -->
      <!-- <a-layout-content :style="{ height: '100%', margin: '24px 24px 0', paddingTop: fixedHeader ? '64px' : '0' }" v-if="!isShow">
        <multi-tab v-if="multiTab"></multi-tab>
        <a-layout >
          <a-layout-sider style="background:#f0f2f5">
            <RightSider></RightSider>
            <LiftSider></LiftSider>
          </a-layout-sider>

          <a-layout-content class="page-header-index-wide" style="margin-left:20px;">
            <route-view />
          </a-layout-content>

          <a-layout-sider style="background:#f0f2f5">
            <RightSider></RightSider>
          </a-layout-sider>
        </a-layout>
      </a-layout-content>
      <route-view v-else/> -->

      <route-view />
      <!-- layout footer -->
      <!-- <a-layout-footer>
        <global-footer />
      </a-layout-footer> -->

      <!-- Setting Drawer (show in development mode) -->
      <setting-drawer v-if="!production"></setting-drawer>
    </a-layout>
  </a-layout>

</template>

<script>
// import Buttons from '@/components/content/OperatingButton/Buttons'
import RightSider from '@/components/content/sider/RightSider'
import LiftSider from '@/components/content/sider/LiftSider'
import { triggerWindowResizeEvent } from '@/utils/util'
import { mapState, mapActions } from 'vuex'
import { mixin, mixinDevice } from '@/utils/mixin'
import config from '@/config/defaultSettings'

import RouteView from './RouteView'
import SideMenu from '@/components/Menu/SideMenu'
import TestMenu from '@/components/Menu/TestMenu'
import GlobalHeader from '@/components/GlobalHeader'
import GlobalFooter from '@/components/GlobalFooter'
import SettingDrawer from '@/components/SettingDrawer'
import { convertRoutes } from '@/utils/routeConvert'

import FileLisrOrCart from '@/components/content/FileLisrOrCart/index'
import Index from '@/views/page/Index'

export default {
  name: 'BasicLayout',
  mixins: [mixin, mixinDevice],
  components: {
    RightSider,
    LiftSider,
    RouteView,
    SideMenu,
    TestMenu,
    GlobalHeader,
    GlobalFooter,
    SettingDrawer,
    FileLisrOrCart,
    Index
  },
  data () {
    return {
      router: this.$router,
      production: config.production,
      collapsed: false,
      menus: []
    }
  },
  computed: {
    ...mapState({
      // 动态主路由
      mainMenu: state => state.permission.addRouters,
      globalHeader: state => state.app.globalHeader
    }),
    isShow () {
      const name = this.router.history.current.name
      if (name === 'index' || name !== 'WorkSurface') {
        return true
      } else {
        return false
      }
    },
    contentPaddingLeft () {
      if (!this.fixSidebar || this.isMobile()) {
        return '0'
      }
      if (this.sidebarOpened) {
        return '256px'
      }
      return '80px'
    }
  },
  watch: {
    sidebarOpened (val) {
      this.collapsed = !val
    }
  },
  created () {
    const routes = convertRoutes(this.mainMenu.find(item => item.path === '/'))
    this.menus = (routes && routes.children) || []
    this.collapsed = !this.sidebarOpened
  },
  mounted () {
    const userAgent = navigator.userAgent
    if (userAgent.indexOf('Edge') > -1) {
      this.$nextTick(() => {
        this.collapsed = !this.collapsed
        setTimeout(() => {
          this.collapsed = !this.collapsed
        }, 16)
      })
    }
  },
  methods: {
    ...mapActions(['setSidebar']),
    toggle () {
      this.collapsed = !this.collapsed
      this.setSidebar(!this.collapsed)
      triggerWindowResizeEvent()
    },
    paddingCalc () {
      let left = ''
      if (this.sidebarOpened) {
        left = this.isDesktop() ? '256px' : '80px'
      } else {
        left = (this.isMobile() && '0') || ((this.fixSidebar && '80px') || '0')
      }
      return left
    },
    menuSelect () {
    },
    drawerClose () {
      this.collapsed = false
    }
  }
}
</script>

<style lang="less">
/*
 * The following styles are auto-applied to elements with
 * transition="page-transition" when their visibility is toggled
 * by Vue.js.
 *
 * You can easily play with the page transition by editing
 * these styles.
 */

.page-transition-enter {
  opacity: 0;
}

.page-transition-leave-active {
  opacity: 0;
}

.page-transition-enter .page-transition-container,
.page-transition-leave-active .page-transition-container {
  -webkit-transform: scale(1.1);
  transform: scale(1.1);
}

.row-select-hover {
  &:hover {
    color: rgb(32, 160, 255);
    cursor: pointer;
  }
}

</style>
