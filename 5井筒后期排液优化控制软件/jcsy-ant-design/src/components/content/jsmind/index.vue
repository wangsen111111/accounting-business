<template>
  <div>
    <div v-contextmenu:contextmenu :class="className" :style="styleSet" :id="jsMindId"></div>
    <v-contextmenu ref="contextmenu">
      <v-contextmenu-item v-if="!editable" @click="toggleEditable">开启编辑节点</v-contextmenu-item>
      <v-contextmenu-item v-if="editable" @click="toggleEditable">禁止编辑节点</v-contextmenu-item>
      <v-contextmenu-item @click="toggleNode">收起或展开子节点</v-contextmenu-item>
      <v-contextmenu-item @click="expandAllNode">展开全部节点</v-contextmenu-item>

      <v-contextmenu-item @click="updataNode">编辑</v-contextmenu-item>
      <v-contextmenu-item @click="removeNode">删除</v-contextmenu-item>
      <v-contextmenu-item>添加备注笔记</v-contextmenu-item>

      <v-contextmenu-submenu title="文件输出">
        <v-contextmenu-item>仅该节点</v-contextmenu-item>
        <v-contextmenu-item>子节点</v-contextmenu-item>
        <v-contextmenu-item>仅该节点及子节点</v-contextmenu-item>
      </v-contextmenu-submenu>

      <v-contextmenu-submenu title="文件卸载">
        <v-contextmenu-item>仅该节点</v-contextmenu-item>
        <v-contextmenu-item>子节点</v-contextmenu-item>
        <v-contextmenu-item>仅该节点及子节点</v-contextmenu-item>
      </v-contextmenu-submenu>

      <v-contextmenu-submenu title="插入">
        <v-contextmenu-item @click="showModel('addDownNode')">向下添加平行节点</v-contextmenu-item>
        <v-contextmenu-item @click="showModel('addUpNode')">向上添加平行节点</v-contextmenu-item>
        <v-contextmenu-item @click="showModel('addAfterNode')">添加子节点</v-contextmenu-item>
        <v-contextmenu-item>备注</v-contextmenu-item>
        <v-contextmenu-item>图片</v-contextmenu-item>
        <v-contextmenu-item>标签</v-contextmenu-item>
        <v-contextmenu-item></v-contextmenu-item>
      </v-contextmenu-submenu>

      <v-contextmenu-submenu title="样式">
        <v-contextmenu-item @click="showModel('changeFont')">设置字体大小</v-contextmenu-item>
        <v-contextmenu-submenu title="设置主题">
          <v-contextmenu-item
            v-for="(item,index) in themes"
            :key="index"
            @click="handleChangeTheme"
          >{{ item }}</v-contextmenu-item>
        </v-contextmenu-submenu>
        <v-contextmenu-submenu title="设置字体颜色">
          <Chrome v-model="fontColor" />
        </v-contextmenu-submenu>
      </v-contextmenu-submenu>
    </v-contextmenu>

    <a-modal
      :title="aModalTitle"
      :visible="visible"
      :confirm-loading="confirmLoading"
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <a-input v-model="inputValue" :placeholder="aModalPlaceholder" />
    </a-modal>
  </div>
</template>

<script>
import { Chrome } from 'vue-color'

// import 'jsmind/style/jsmind.css'
// import '@/assets/css/jsmind.css'
// import jsMind from 'jsmind'
// import Vue from 'vue'
import jsMind from '@/assets/jsmind/js/jsmind.js'

import contentmenu from 'v-contextmenu'
import 'v-contextmenu/dist/index.css'
// window.jsMind = jsMind
require('@/assets/jsmind/js/jsmind.draggable.js')
// Vue.use(contentmenu)
export default {
  components: {
    Chrome,
    contentmenu
  },
  watch: {
    fontColor (newVlaue, oldValue) {
      this.jm.set_node_color(this.jm.get_selected_node().id, null, newVlaue.hex)
    }
  },
  data () {
    return {
      radarLoading: true,
      fontColor: '#ff0000',
      jm: null,
      editable: true,
      currSelectedNodeid: null,
      visible: false,
      confirmLoading: false,
      inputValue: null,
      type: null,
      aModalTitle: null,
      aModalPlaceholder: null,
      themes: [ 'default', 'primary', 'warning', 'danger', 'success', 'info', 'greensea', 'nephrite', 'belizeholle', 'wisteria', 'asphalt', 'orange', 'pumpkin', 'pomegranate', 'clouds', 'asbestos' ]
    }
  },
  props: {
    jsMindId: {
      type: String,
      default: 'jsMindId'
    },
    jsMindName: {
      type: String,
      default: 'jsMindName'
    },
    data: {
      type: Array,
      default: () => {
        return []
      }
    },
    format: {
      type: String,
      default: 'node_array'
    },
    styleSet: {
      type: Object,
      default: () => {
        return { width: '30.5vw', height: '20vw' }
      }
    },
    className: {
      type: String,
      default: 'jsmind_container'
    }
  },
  mounted: function () {
    this.load_jsmind(this.jsMindId)
  },
  methods: {
    handleOk (e) {
      if (this.inputValue === '' || this.inputValue == null) {
        this.$message.error('输入节点名不能为空')
      } else {
        var selectedNode = this.jm.get_selected_node()
        if (this.type === 'addAfterNode') {
          this.jm.add_node(selectedNode, jsMind.util.uuid.newid(), this.inputValue)
        }
        if (this.type === 'addUpNode') {
          this.jm.insert_node_before(selectedNode, jsMind.util.uuid.newid(), this.inputValue)
        }
        if (this.type === 'addDownNode') {
          this.jm.add_node(selectedNode.parent.id, jsMind.util.uuid.newid(), this.inputValue)
        }
        if (this.type === 'changeFont') {
          this.jm.set_node_font_style(selectedNode.id, this.inputValue)
        }
      }
      this.visible = false
      this.confirmLoading = false
    },
    handleCancel (e) {
      console.log('Clicked cancel button')
      this.visible = false
    },
    load_jsmind (id) {
      var mind = {
        meta: {
          name: this.jsMindName,
          author: 'hizzgdev@163.com',
          version: '0.2'
        },
        format: this.format,
        data: this.data
      }
      var options = {
        container: id,
        editable: true,
        theme: 'primary',
        layout: {
          hspace: 30, // 节点之间的水平间距
          vspace: 20, // 节点之间的垂直间距
          pspace: 12 // 节点与连接线之间的水平间距（用于容纳节点收缩/展开控制器）
        }
      }
      this.jm = jsMind.show(options, mind)
      // jm.set_readonly(true);
      // var mind_data = jm.get_data();
      // alert(mind_data);
      // this.jm.add_node('sub2', 'sub2344', 'new node', { 'background-color': 'red' })
      // this.jm.set_node_color('sub21', 'green', '#ccc')
      this.jm.view.add_event(this.jm, 'contextmenu', this.contextmenuHandle)
    },
    contextmenuHandle (e) {
      var element = e.target || event.srcElement
      var nodeid = this.jm.view.get_binded_nodeid(element)
      this.currSelectedNodeid = nodeid
    },
    updataNode () {
      this.jm.begin_edit(this.jm.get_selected_node().id)
      // this.jm.update_node(this.getSelectedNodeid(), 'topic')
      // this.visible = true
    },
    removeNode () {
      if (!this.jm.get_selected_node().id) {
        this.$message.error('请选择一个节点')
      } else {
        this.jm.remove_node(this.jm.get_selected_node().id)
        this.$message.success('删除成功')
      }
    },
    expandAllNode () {
      this.jm.expand_all()
    },
    toggleEditable () {
      this.editable = !this.editable
      this.jm.options.editable = this.editable
    },
    toggleNode () {
      this.jm.toggle_node(this.jm.get_selected_node().id)
    },
    setAModal (aModalTitle, aModalPlaceholder) {
      this.aModalTitle = aModalTitle
      this.aModalPlaceholder = aModalPlaceholder
    },
    showModel (type) {
      var selectedNode = this.jm.get_selected_node() // as parent of new node
      console.log(this.jm.options.editable)
      if (!selectedNode) {
        this.$message.error('请选择一个节点')
        return
      }
      this.visible = true
      this.type = type
      this.inputValue = ''
      switch (type) {
        case 'addAfterNode':
          this.setAModal('添加子节点', '请输入节点名称')
          break
        case 'addUpNode':
          this.setAModal('向上添加平行节点', '请输入节点名称')
          break
        case 'addDownNode':
          this.setAModal('向下添加平行节点', '请输入节点名称')
          break
        case 'changeFont':
          this.setAModal('设置节点字体大小', '请输入字体大小数值，单位为像素')
          break
        default:
      }
    },
    handleChangeTheme (vm, event) {
      this.jm.set_theme(`${vm.$slots.default[0].text}`)
      // alert(`「${vm.$slots.default[0].text}」被点击啦！`)
    },
    handleChangeColer (value) {
      this.jm.set_node_color(this.jm.get_selected_node().id, null, value)
    }
  }
}
</script>

<style scoped lang="less">
.jsmind_container {
  /* width: 766.4583px;
  height: 543.66px; */
  width: 30vw;
  height: 20vw;
  // border: solid 1px #ccc;
  // background: #f4f4f4;
}
// .box {
//   width: 100%;
//   background: #f1f2f6;
//   .home {
//     margin: 0 auto;
//     width: calc(100% - 80px);
//   }
// }
</style>
