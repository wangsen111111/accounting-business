<template>
  <div>
    <a-row type="flex" justify="start" class="jsmind-tool-bar">
      <div class="jsmind-menu-item" @click="nodeLocked = !nodeLocked">
        <component
          ref="toolBarSvgLock"
          :is="nodeLocked ? 'lockIcon' : 'unlockIcon'"
          :width="'30px'"
          :height="'30px'"
          alt="禁用/启用"
        ></component>
        <p>
          <a>{{ nodeLocked ? '启用' : '禁用' }}</a>
        </p>
        <!-- <p>节点编辑</p> -->
        <a-divider style="margin: 8px 0px"></a-divider>
        <div style="text-align: center; margin-top: 0px">节点编辑</div>
      </div>
      <div class="jsmind-menu-item" @click="bothSide = !bothSide">
        <component
          ref="toolBarSvgSide"
          :is="bothSide ? 'fullIcon' : 'sideIcon'"
          :width="'30px'"
          :height="'30px'"
          alt="两侧/单侧"
        ></component>
        <p>
          <a>{{ bothSide ? '两侧' : '单侧' }}</a>
        </p>
        <!-- <p>模式</p> -->
        <a-divider style="margin: 8px 0px"></a-divider>
        <div style="text-align: center; margin-top: 0px">模式</div>
      </div>
      <div style="display: contents" v-for="group in menuItemList" :key="group.groupName">
        <a-divider type="vertical" style="height: 90px; margin-left: 15px"></a-divider>
        <div>
          <div class="jsmind-menu-group" v-for="item in group.menuList" :key="item.menuItem.title">
            <p class="iconsvg">
              <component
                ref="toolBarSvg"
                :is="item.menuItem.icon"
                :width="'30px'"
                :height="'30px'"
                :alt="item.menuItem.title"
              ></component>
            </p>
            <p v-if="item.menuItem.children.length > 0">
              <a-dropdown :trigger="['click']">
                <a> {{ item.menuItem.title }} <a-icon type="down" /> </a>
                <a-menu slot="overlay">
                  <a-menu-item v-for="m in item.menuItem.children" :key="m.title">
                    <a @click="ClickMenuItem(m.title)">{{ m.title }}</a>
                  </a-menu-item>
                </a-menu>
              </a-dropdown>
            </p>
            <p v-if="item.menuItem.children.length === 0">
              <a>{{ item.menuItem.title }}</a>
            </p>
          </div>
          <a-divider style="margin: 8px 0px"></a-divider>
          <div style="text-align: center; margin-top: 0px">{{ group.groupName }}</div>
        </div>
      </div>
    </a-row>
  </div>
</template>

<script>
import {
  lockIcon,
  unlockIcon,
  fullIcon,
  sideIcon,
  seeIcon,
  outputIcon,
  mountIcon,
  uninstallIcon,
  childLevelIcon,
  removeIcon,
  tagIcon
} from '@/core/icons'
import menuMixins from './menuMixins'
const menuList = [
  {
    groupName: '任务',
    menuItem: {
      title: '查看',
      icon: 'seeIcon',
      children: [
        {
          title: '查看当前节点文件',
          msg: ''
        },
        {
          title: '查看全部文件',
          msg: ''
        },
        {
          title: '查看该思维导图全部文件',
          msg: ''
        }
      ]
    }
  },
  {
    groupName: '任务',
    menuItem: {
      title: '下载',
      icon: 'outputIcon',
      children: [
        {
          title: '思维导图',
          msg: ''
        },
        {
          title: '选中节点及文件(导图)',
          msg: ''
        },
        {
          title: '选中节点文件(仅文件)',
          msg: ''
        },
        {
          title: '指定文件',
          msg: ''
        }
      ]
    }
  },
  {
    groupName: '任务',
    menuItem: {
      title: '挂载',
      icon: 'mountIcon',
      children: [
        {
          title: '外链',
          msg: ''
        },
        {
          title: '上传',
          msg: ''
        },
        {
          title: '已有文件',
          msg: ''
        }
      ]
    }
  },
  {
    groupName: '任务',
    menuItem: {
      title: '卸载',
      icon: 'uninstallIcon',
      children: [
        {
          title: '卸载指定文件',
          msg: ''
        },
        {
          title: '卸载选择节点所有文件',
          msg: ''
        },
        {
          title: '卸载选择节点及子节点所有文件',
          msg: ''
        },
        {
          title: '卸载所有文件',
          msg: ''
        }
      ]
    }
  },
  {
    groupName: '插入',
    menuItem: {
      title: '节点',
      icon: 'childLevelIcon',
      children: [
        {
          title: '插入子节点',
          msg: ''
        },
        {
          title: '在选中节点前插入节点',
          msg: ''
        },
        {
          title: '在选中节点后插入节点',
          msg: ''
        }
      ]
    }
  },
  {
    groupName: '插入',
    menuItem: {
      title: '删除',
      icon: 'removeIcon',
      children: []
    }
  },
  {
    groupName: '标记',
    menuItem: {
      title: '标签',
      icon: 'tagIcon',
      children: [
        {
          title: '标签',
          msg: ''
        },
        {
          title: '项目',
          msg: ''
        },
        {
          title: '标签组',
          msg: ''
        }
      ]
    }
  }
]
export default {
  mixins: [menuMixins],
  components: {
    lockIcon,
    unlockIcon,
    fullIcon,
    sideIcon,
    seeIcon,
    outputIcon,
    mountIcon,
    uninstallIcon,
    childLevelIcon,
    removeIcon,
    tagIcon
  },
  data () {
    return {
      menuList
    }
  },
  computed: {
    menuItemList () {
      const groupList = [...new Set(this.menuList.map((a) => a.groupName))]
      const res = []
      groupList.forEach((g) => {
        res.push({
          groupName: g,
          menuList: this.menuList.filter((a) => {
            if (a.groupName === g) return a
          })
        })
      })
      return res
    }
  },
  mounted () {
    this.SetDefColor()
  },
  watch: {
    bothSide () {
      this.$nextTick(() => {
        this.SetDefColor()
      })
    }
  },
  methods: {
    ClickMenuItem (value) {
      console.log(this.$refs.toolBarSvg)
    }
  }
}
</script>

<style lang="less">
</style>
