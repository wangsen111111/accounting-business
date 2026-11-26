<template>
  <div>
    <div v-contextmenu:contextmenu class="jsmind_container" :style="styleCss" :id="jsMindId"></div>
    <v-contextmenu ref="contextmenu">
      <!-- <v-contextmenu-item @click="getDataRoot">测试</v-contextmenu-item> -->
      <v-contextmenu-item @click="copyNode">复制</v-contextmenu-item>
      <v-contextmenu-item @click="pasteNode">粘贴</v-contextmenu-item>

      <v-contextmenu-submenu title="主题">
        <v-contextmenu-item @click="addNode()">子主题</v-contextmenu-item>
        <v-contextmenu-item @click="addNodeBefore()">在指定节点前插入</v-contextmenu-item>
        <v-contextmenu-item @click="addNodeAfter()">在指定节点后插入</v-contextmenu-item>
        <v-contextmenu-item @click="removeNode()">删除</v-contextmenu-item>
      </v-contextmenu-submenu>

      <v-contextmenu-submenu title="收缩">
        <v-contextmenu-item @click="expandAllNode">展开全部节点</v-contextmenu-item>
        <!-- <v-contextmenu-item @click="collapseNode">收起子节点</v-contextmenu-item>
        <v-contextmenu-item @click="expandNode">展开子节点</v-contextmenu-item> -->
        <v-contextmenu-submenu title="展开至层级">
          <v-contextmenu-item @click="expandToDepth(1)">1级</v-contextmenu-item>
          <v-contextmenu-item @click="expandToDepth(2)">2级</v-contextmenu-item>
          <v-contextmenu-item @click="expandToDepth(3)">3级</v-contextmenu-item>
          <v-contextmenu-item @click="expandToDepth(5)">5级</v-contextmenu-item>
          <v-contextmenu-item @click="expandToDepth(7)">7级</v-contextmenu-item>
        </v-contextmenu-submenu>
      </v-contextmenu-submenu>
      <v-contextmenu-item @click="showDrawer">查看文件</v-contextmenu-item>

      <!-- <v-contextmenu-submenu title="任务">
        <v-contextmenu-submenu title="查看">
          <v-contextmenu-item @click="unMountFilesBychildrenNode">当前节点文件</v-contextmenu-item>
          <v-contextmenu-item @click="unMountFilesBychildrenNode">全部文件</v-contextmenu-item>
          <v-contextmenu-item @click="unMountFilesBychildrenNode">思维导图文件</v-contextmenu-item>
        </v-contextmenu-submenu>
        <v-contextmenu-submenu title="下载">
          <v-contextmenu-item @click="unMountFilesBychildrenNode">思维导图</v-contextmenu-item>
          <v-contextmenu-item @click="unMountFilesBychildrenNode">该节点</v-contextmenu-item>
          <v-contextmenu-item @click="unMountFilesBychildrenNode">该节点及子节点</v-contextmenu-item>
          <v-contextmenu-item @click="unMountFilesBychildrenNode">指定文件</v-contextmenu-item>
        </v-contextmenu-submenu>
        <v-contextmenu-submenu title="挂载">
          <v-contextmenu-item @click="unMountFilesBychildrenNode">外链</v-contextmenu-item>
          <v-contextmenu-item @click="unMountFilesBychildrenNode">上传</v-contextmenu-item>
          <v-contextmenu-item @click="unMountFilesBychildrenNode">已有文件</v-contextmenu-item>
        </v-contextmenu-submenu>
        <v-contextmenu-item @click="mountFiles">挂载</v-contextmenu-item>
      </v-contextmenu-submenu> -->

      <!--
      <v-contextmenu-item @click="showDrawer">查看文件</v-contextmenu-item>
      <v-contextmenu-item @click="mountFiles">挂载</v-contextmenu-item>
      <v-contextmenu-item v-if="!editable" @click="toggleEditable">开启编辑节点</v-contextmenu-item>
      <v-contextmenu-item v-if="editable" @click="toggleEditable">禁止编辑节点</v-contextmenu-item> -->
      <!-- <v-contextmenu-item @click="updataNode">编辑</v-contextmenu-item>
      <v-contextmenu-item @click="removeNode">删除</v-contextmenu-item> -->

      <!-- <v-contextmenu-submenu title="文件卸载">
        <v-contextmenu-item @click="unMountFilesByNode">该节点</v-contextmenu-item>
        <v-contextmenu-item @click="unMountFilesBychildrenNode">子节点</v-contextmenu-item>
        <v-contextmenu-item @click="unMountFilesByNode">该节点及子节点</v-contextmenu-item>
      </v-contextmenu-submenu> -->

      <!-- <v-contextmenu-submenu title="视图">
        <v-contextmenu-item @click="setFont()">设置字体样式</v-contextmenu-item>
        <v-contextmenu-item @click="showModel('changeFont')">设置字体大小</v-contextmenu-item>
        <v-contextmenu-submenu title="设置主题">
          <v-contextmenu-item v-for="(item,index) in themes" :key="index" @click="handleChangeTheme">{{ item }}</v-contextmenu-item>
        </v-contextmenu-submenu>
        <v-contextmenu-submenu title="设置字体颜色">
          <Chrome v-model="fontColor" />
        </v-contextmenu-submenu>
        <v-contextmenu-submenu title="设置背景颜色">
          <Chrome v-model="backgroundColor" />
        </v-contextmenu-submenu>
      </v-contextmenu-submenu> -->
    </v-contextmenu>

    <a-modal
      :title="aModalTitle"
      :visible="visible"
      :confirm-loading="confirmLoading"
      @ok="handleOk"
      @cancel="handleCancel">
      <a-input v-model="inputValue" :placeholder="aModalPlaceholder" />
    </a-modal>

    <a-drawer
      width="calc(50vw)"
      title="节点文件列表"
      placement="left"
      :closable="false"
      :visible="drawerVisible"
      @close="onClose">
      <FileList v-if="drawerVisible && nodeFileIds" :fileIds="nodeFileIds" :selectNode="selectNode"></FileList>
    </a-drawer>
  </div>
</template>

<script>
import FileList from '@/components/content/file/FileList'
import { Chrome } from 'vue-color'

import jsMind from '@/assets/jsmind/js/jsmind.js'
// import * as jsMind from '@/assets/jsmind/js/jsmind.js'
// import Vue from 'vue'  nodeAttrIndex
import { save, update, remove } from '@/api/nodeAttrIndex'

// 引用jsmind依赖中的css
// import 'jsmind/style/jsmind.css'
// 引用自己的css
// import '@/assets/css/jsmind.css'
import '@/assets/jsmind/style/jsmind.css'

import contentmenu from 'v-contextmenu'
import 'v-contextmenu/dist/index.css'
// Vue.use(contentmenu)

import { updateMindMap, getCopyNodeByNodeId, pasteNode } from '@/api/mindMap'
import Vue from 'vue'
// window.jsMind = jsMind
// require('jsmind/js/jsmind.draggable.js')
require('@/assets/jsmind/js/jsmind.draggable.js')
export default {
  components: { Chrome, contentmenu, FileList },
  props: ['jsMindId', 'jsMindMapData', 'fileIds', 'styleCss'],
  data () {
    return {
      copyNodeData: null,
      selectNode: null,
      nodeFileIds: null,
      drawerVisible: false,
      radarLoading: true,
      fontColor: '#ff0000',
      backgroundColor: '#ff0000',
      jm: null,
      editable: true,
      currSelectedNodeid: null,
      visible: false,
      confirmLoading: false,
      inputValue: null,
      type: null,
      aModalTitle: null,
      aModalPlaceholder: null,
      themes: ['default', 'primary', 'warning', 'danger', 'success', 'info', 'greensea', 'nephrite', 'belizeholle', 'wisteria', 'asphalt', 'orange', 'pumpkin', 'pomegranate', 'clouds', 'asbestos']
    }
  },
  watch: {
    currSelectNode: {
        handler (newVal) {
            // console.log('选中节点改变=》', newVal)
            // 返回给父组件当前节点的选中状态 this.jm.get_selected_node()
            var selectNode = { 'jm': this.jm, 'get_selected_node': newVal }
            this.$emit('isSelectNode', newVal, selectNode)
        },
        immediate: true
    },
    fontColor (newVlaue, oldValue) {
      this.jm.set_node_color(this.jm.get_selected_node().id, null, newVlaue.hex)
    },
    backgroundColor (newVlaue, oldValue) {
      this.jm.set_node_color(this.jm.get_selected_node().id, newVlaue.hex, null)
    },
    currSelectJsmindId (newVlaue, oldValue) {
      if (newVlaue === this.jsMindId) {
        console.log('父组件值变化', newVlaue, oldValue, this.jsMindId)
      }
    },
    jsmindData: {
      deep: true,
      handler (newVlaue, oldValue) {
          if (oldValue && this.$route.name === 'MindMap') {
            // 在缓存中记录变化的数据
            this.$store.commit('saveChangeJsmindMap', newVlaue)
          }
      }

    }
  },
  computed: {
    currSelectNode: function () {
      if (this.jm != null) {
        return this.jm.get_selected_node()
      } else {
        return null
      }
    },
    // 计算属性的 获取传输到后台的数据
    jsmindData: function () {
      if (!this.jm) { return null }
      var responseData = this.jsMindMapData
      responseData['data'] = JSON.parse(JSON.stringify(this.jm.get_data('node_tree')))
      responseData['data']['view'] = JSON.parse(JSON.stringify(this.jm.options.view))
      responseData['data']['layout'] = JSON.parse(JSON.stringify(this.jm.options.layout))
      responseData['theme'] = this.jm.options.theme
      responseData['support_html'] = this.jm.options.support_html
      responseData['mode'] = this.jm.options.mode
      responseData['editable'] = this.jm.options.editable
      return responseData
    },
    changeData: function () {
      if (!this.jm) { return null }
      return this.jm.get_data('node_tree').data
    }
  },
  mounted: function () {
    this.load_jsmind()
    // this.$store.commit('mountFileIds', this.getFileIds())
    // 判断传入的思维导图是否是模板
    if (this.jsmindData.isTemplate) {
      this.jm.view.setZoomByProportion(0.7)
      this.expandToDepth(1)
      // 更换样式
       var jsmindDiv = document.getElementById(this.jsMindId)
       jsmindDiv.className = 'jsmind_container_no_scroll_axis'
    }
    // 重置容器滑动条
    // this.reLoadContainer()
  },
  methods: {
    // -----------------复制粘贴开始-----------------------------
    pasteNode () {
        if (!this.jm.get_selected_node()) {
          this.$message.warning('请选择粘贴节点')
          return
        }
                // 获取copyNode数据
        var copyNode = this.$store.state.copyNode
        if (!copyNode) {
          this.$message.warning('请选择复制节点')
          return
        }

        var data = {}
        this.jsmindData.data = JSON.parse(JSON.stringify(this.jm.get_data('node_tree')))
        data.jsmind = this.jsmindData
        data.copyNode = copyNode
        data.copyNodeId = this.jm.get_selected_node().id

        pasteNode(data).then(res => {
          if (res.success) {
            // 重新渲染视图
            this.changeContainer(res.data)
            // 保存思维导图
            this.savaMindMap()
          }
        })
    },
    copyNode () {
        if (!this.jm.get_selected_node()) {
          this.$message.warning('请选择复制节点')
          return
        }
        // var copyNodeData = this.jm.get_selected_node()
        // this.buildNodeId(copyNodeData)
        // // 重新生成id
        // this.copyNodeData = this.jm.get_selected_node()
      var nodeId = this.jm.get_selected_node().id
      this.jsmindData.data = JSON.parse(JSON.stringify(this.jm.get_data('node_tree')))
      // var data = JSON.parse(JSON.stringify(this.jm.get_data('node_tree')))
      getCopyNodeByNodeId(nodeId, this.jsmindData).then(res => {
        if (res.success) {
          // 将数据放入全局变量中（vuex）
          this.$store.commit('copyNode', res.data)
        }
      })
    },
    buildNodeId (node) {
      node.id = 'new_' + jsMind.util.uuid.newid()
      if (node.children !== undefined && node.children.length > 0) {
        node.children.forEach(item => { return this.buildNodeId(item) })
      }
    },
    // -----------------复制粘贴结束-----------------------------
    getJsmindData () {
      var responseData = this.jsMindMapData
      responseData['data'] = JSON.parse(JSON.stringify(this.jm.get_data('node_tree')))
      responseData['data']['view'] = JSON.parse(JSON.stringify(this.jm.options.view))
      responseData['data']['layout'] = JSON.parse(JSON.stringify(this.jm.options.layout))
      responseData['theme'] = this.jm.options.theme
      responseData['support_html'] = this.jm.options.support_html
      responseData['mode'] = this.jm.options.mode
      responseData['editable'] = this.jm.options.editable
      return responseData
    },
    // -------------------图标开始------------------------------------
    parseToDOM (str) {
      var div = document.createElement('div')
      if (typeof str === 'string') { div.innerHTML = str }
      return div.childNodes[0]
    },
    insertIcon (img, nodeAttrId) {
      // 更新选中节点
        if (!this.jm.get_selected_node()) {
          this.$message.warning('请选择节点')
          return
        }
        var data = { 'attrId': nodeAttrId, 'jsmindId': this.jsMindId, 'nodeId': this.jm.get_selected_node().id }
        var newImgDom = this.parseToDOM(img)
        var newSrcStr = newImgDom.getAttribute('src')
        var newType = newSrcStr.split('/')[2]
        const oldImgs = this.jm.get_selected_node_imgs()

        // 插入的情况
         if (oldImgs == null || oldImgs.length === 0) {
          var editTopic = this.jm.get_selected_node_edit_topic()
          save(data).then(res => {
            if (res.success) {
              this.jm.update_node(this.jm.get_selected_node().id, img + editTopic)
              this.savaMindMap()
            }
          })
         } else {
            oldImgs.forEach(oldImg => {
              var imgDom = this.parseToDOM(oldImg)
              var oldSrcStr = imgDom.getAttribute('src')
              // 判断当前图标插入还是替换
              var oldType = oldSrcStr.split('/')[2]
              if (newType === oldType) {
                // 替换原来图标
                update(data).then(res => {
                  if (res.success) { this.jm.replace_node_img(this.jm.get_selected_node().id, oldImg, img) }
                })
              }
            })
            var oldTypes = oldImgs.map(oldImg => this.parseToDOM(oldImg).getAttribute('src').split('/')[2])
            // 添加新类型图标
            if (oldTypes.indexOf(newType) === -1) {
              const editTopic = this.jm.get_selected_node_edit_topic()
              save(data).then(res => {
                if (res.success) {
                  this.jm.update_node(this.jm.get_selected_node().id, img + editTopic)
                  this.savaMindMap()
                }
              })
            }
         }
    },
    deleteIcon (type) {
      const oldImgs = this.jm.get_selected_node_imgs()
      oldImgs.forEach(oldImg => {
        var imgDom = this.parseToDOM(oldImg)
        var oldSrcStr = imgDom.getAttribute('src')
        // 判断当前图标插入还是替换
        var oldType = oldSrcStr.split('/')[2]
        if (type === oldType) {
          // 替换原来图标
          var data = { 'jsmindId': this.jsMindId, 'nodeId': this.jm.get_selected_node().id }
          remove(data).then(res => {
            this.jm.replace_node_img(this.jm.get_selected_node().id, oldImg, '')
            this.savaMindMap()
          })
        }
      })
    },
    // -------------------图标结束------------------------------------
    // -------------------获取当前节点开始-------------------------------
    getCurrNode () {
      if (this.jm.get_selected_node() == null) { return null }
      var responseData = this.jsMindMapData
      var rootNode = JSON.parse(JSON.stringify(this.jm.get_data('node_tree')))
      var currNodeList = this.getCurrNodeData(rootNode.data, this.jm.get_selected_node().id, [])
      rootNode.data = currNodeList[0]
      responseData['data'] = rootNode
      responseData['data']['view'] = JSON.parse(JSON.stringify(this.jm.options.view))
      responseData['data']['layout'] = JSON.parse(JSON.stringify(this.jm.options.layout))
      responseData['theme'] = this.jm.options.theme
      responseData['support_html'] = this.jm.options.support_html
      responseData['mode'] = this.jm.options.mode
      responseData['editable'] = this.jm.options.editable
      return responseData
    },
    getCurrNodeData (node, currNodeId, currNodeList) {
      if (node.id === currNodeId) { currNodeList.push(node) }
      if (node.id !== currNodeId && node.children !== undefined && node.children.length > 0) {
        node.children.forEach(item => { return this.getCurrNodeData(item, currNodeId, currNodeList) })
      }
      return currNodeList
    },

    // -------------------获取当前节点结束-------------------------------
    // 判断当前是否选择节点并提示
    isSelectNode () {
        if (!this.jm.get_selected_node()) {
          this.$message.warning('请选择节点')
          return false
        }
        return true
    },
    // ---------------导入导出开始-------------------------------
    outputFile () {
        var mindData = this.jm.get_data() // 获取的元数据
        var mindName = mindData.meta.name
        var mindStr = jsMind.util.json.json2string(this.getJsmindData())
        jsMind.util.file.save(mindStr, 'text/jsmind', mindName + '.jm')
    },
    // ---------------导入导出结束-------------------------------
    // ---------------重置容器开始-------------------------------
    changeContainer (currSelectJsmind) {
      var jsmindDiv = document.getElementById(this.jsMindId)
      // 删除原来节点下数据
      var firstElementChild = jsmindDiv.firstElementChild
      jsmindDiv.removeChild(firstElementChild)
      // 重新渲染原来节点数据
      this.loadJsmind(currSelectJsmind)
    },
    // 重新加载容器，使样式居中
    reLoadContainer () {
            var jsmindDiv = document.getElementById(this.jsMindId)
      // 删除原来节点下数据
      var firstElementChild = jsmindDiv.firstElementChild
      jsmindDiv.removeChild(firstElementChild)
      // 重新渲染原来节点数据
      this.loadJsmind(this.jsmindData)
    },
    // ---------------重置容器结束-------------------------------
    // --------------------样式修改开始------------------------------
    // 修改字体颜色
    changeFontColor (hex) {
      if (!this.jm.get_selected_node()) {
        this.$message.warning('请选择节点')
        return
      }
      this.jm.set_node_color(this.jm.get_selected_node().id, null, hex)
    },
    // 修改字体大小
    changeFontSize (fontSize) {
      var selectedNode = this.jm.get_selected_node()
      if (!this.isSelectNode()) { return }
      this.jm.set_node_font_style(selectedNode.id, fontSize)
    },
    // 切换粗字体
    toggleFontThickness () {
      if (!this.isSelectNode()) { return }
      var selectedNode = this.jm.get_selected_node()
      // bold 粗  normal 细
      var fontWeight = selectedNode.data['font-weight']
      if (fontWeight === undefined || fontWeight === 'normal') {
        this.jm.set_node_font_style(selectedNode.id, selectedNode.data['font-size'], 'bold')
      } else {
        this.jm.set_node_font_style(selectedNode.id, selectedNode.data['font-size'], 'normal')
      }
    },
    // 斜体切换
    toggleFontItalic () {
      if (!this.isSelectNode()) { return }
      var selectedNode = this.jm.get_selected_node()
      var fontStyle = selectedNode.data['font-style']
      if (fontStyle === undefined || fontStyle === 'normal') {
        this.jm.set_node_font_styleOnly(selectedNode.id, 'italic')
      } else {
        this.jm.set_node_font_styleOnly(selectedNode.id, 'normal')
      }
    },
    // 字体样式 修改字体样式
    changeFamily (fontFamily) {
      // if (!this.isSelectNode()) { return }
        // this.jm.mind.selected.data['font-family'] = fontFamily
        // // 刷新视图
        // this.jm.view.reset_node_custom_style(this.jm.get_selected_node())
      if (!this.isSelectNode()) { return }
      var selectedNode = this.jm.get_selected_node()
       this.jm.set_node_font_family(selectedNode.id, fontFamily)
    },
    // 中划线切换 text-decoration:line-through;
    toggleLineThrough () {
      if (!this.isSelectNode()) { return }
      this.toggleTextDecoration('text-decoration', 'line-through')
    },
    // 下划线切换 text-decoration：underline
    toggleUnderline () {
      if (!this.isSelectNode()) { return }
      this.toggleTextDecoration('text-decoration', 'underline')
    },
    toggleTextDecoration (styleKey, styleVelue) {
      var textDecoration = this.jm.mind.selected.data[styleKey]
      if (textDecoration === undefined || textDecoration === '') {
        this.jm.mind.selected.data[styleKey] = styleVelue
        // 刷新视图
        this.jm.view.reset_node_custom_style(this.jm.get_selected_node())
      } else {
        this.jm.mind.selected.data['text-decoration'] = ''
        this.jm.view.reset_node_custom_style(this.jm.get_selected_node())
      }
    },
    // --------------------样式修改结束------------------------------

    reset () { this.jm._reset() },

    // --------------------详图修改开始------------------------------
    // 收缩
    toggleNode () {
      if (!this.isSelectNode()) { return }
      this.jm.toggle_node(this.jm.get_selected_node().id)
    },
    // 收起子节点 jm.collapse_node(node|nodeid)
    collapseNode () {
      if (!this.isSelectNode()) { return }
      this.jm.collapse_node(this.jm.get_selected_node().id)
    },
    // 展开全部节点
    expandAllNode () { this.jm.expand_all() },
    // 展开子节点 jm.expand_node(node|nodeid)
    expandNode () {
      if (!this.isSelectNode()) { return }
      this.jm.expand_node(this.jm.get_selected_node().id)
    },
    expandToDepth (depth) { this.jm.expand_to_depth(depth) },
    // --------------------详图修改开始------------------------------

    // --------------------节点编辑开始------------------------------
    toggleEditable () {
      this.editable = !this.editable
      this.jm.options.editable = this.editable
    },
    toggleIsEditable (editable) { this.jm.options.editable = editable },
    // --------------------节点编辑结束------------------------------

    // --------------------模式修改开始------------------------------
    toggleMode (mode) { this.jm.options.mode = mode },
    // --------------------模式修改结束------------------------------

    // --------------------主题开始------------------------------
        // 删除节点
    removeNode () {
      if (!this.isSelectNode()) { return }
      this.jm.remove_node(this.jm.get_selected_node().id)
    },
    // 添加子主题
    addNode () {
      if (!this.isSelectNode()) { return }
      this.jm.add_node(this.jm.get_selected_node(), jsMind.util.uuid.newid(), '子主题')
    },
    // 添加同级主题 - 后
    addNodeAfter () {
      if (!this.isSelectNode()) { return }
      this.jm.insert_node_after(this.jm.get_selected_node(), jsMind.util.uuid.newid(), '同级主题-后')
    },
    // 添加同级主题 - 前
    addNodeBefore () {
      if (!this.isSelectNode()) { return }
      this.jm.insert_node_before(this.jm.get_selected_node(), jsMind.util.uuid.newid(), '同级主题-前')
    },

    showModel (type) {
      var selectedNode = this.jm.get_selected_node() // as parent of new node
      console.log(this.jm.options.editable)
      if (!selectedNode) { this.$message.error('请选择一个节点'); return }
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
    handleOk (e) {
      if (this.inputValue === '' || this.inputValue == null) {
        this.$message.error('输入节点名不能为空')
      } else {
        var selectedNode = this.jm.get_selected_node()
        if (this.type === 'addAfterNode') { this.jm.add_node(selectedNode, jsMind.util.uuid.newid(), this.inputValue) }
        if (this.type === 'addUpNode') { this.jm.insert_node_before(selectedNode, jsMind.util.uuid.newid(), this.inputValue) }
        if (this.type === 'addDownNode') { this.jm.add_node(selectedNode.parent.id, jsMind.util.uuid.newid(), this.inputValue) }
        if (this.type === 'changeFont') { this.jm.set_node_font_style(selectedNode.id, this.inputValue) }
      }
      this.visible = false
      this.confirmLoading = false
    },
    handleCancel (e) {
      console.log('Clicked cancel button')
      this.visible = false
    },
    // --------------------主题结束------------------------------

    // --------------------对象格式开始------------------------------
    // 修改主题
    changeTheme (theme) { this.jm.set_theme(theme) },
    // 背景修改
    changeBackgroundColor (backgroundColor) { this.jm.set_node_color(this.jm.get_selected_node().id, backgroundColor, null) },
    // --------------------对象格式结束------------------------------

    utilChangeMountStatus () {
      // 修改全局变量当前jsmind的挂载文件数据
      this.$store.commit('mountFileIds', this.getFileIds())
      // 发送消息修改父页面数据
      this.$emit('changeMountStatus')
    },
    onClose () {
      this.nodeFileIds = null
      this.drawerVisible = false
    },
    setFont () {
      this.jm.set_node_font_style(this.jm.get_selected_node().id, 33, 23, 'oblique')
    },
    loadJsmind (jsMindMapData) {
      var options = {
        container: jsMindMapData.id,
        editable: jsMindMapData.editable,
        theme: jsMindMapData.theme,
        mode: jsMindMapData.mode, // 显示模式
        view: jsMindMapData.data.view,
        layout: jsMindMapData.data.layout
      }
      // this.jm = jsMind.show(options, this.jsMindMapData.data)
      this.jm = jsMind.show(options, jsMindMapData.data)
      this.jm.view.add_event(this.jm, 'contextmenu', this.contextmenuHandle)
    },
    load_jsmind () {
      var options = { container: this.jsMindId }
      if (this.jsMindMapData.editable !== undefined) { options['editable'] = this.jsMindMapData.editable }
      if (this.jsMindMapData.theme !== undefined) { options['theme'] = this.jsMindMapData.theme }
      if (this.jsMindMapData.mode !== undefined) { options['mode'] = this.jsMindMapData.mode }
      if (this.jsMindMapData.data.view !== undefined) { options['view'] = this.jsMindMapData.data.view }
      if (this.jsMindMapData.data.layout !== undefined) { options['layout'] = this.jsMindMapData.data.layout }
debugger
      this.jm = jsMind.show(options, this.jsMindMapData.data)
      this.jm.view.add_event(this.jm, 'contextmenu', this.contextmenuHandle)
    },
    contextmenuHandle (e) {
      var element = e.target || event.srcElement
      var nodeid = this.jm.view.get_binded_nodeid(element)
      this.currSelectedNodeid = nodeid
    },
    showDrawer () {
      // 获取当前节点文件列表数据
      if (!this.jm.get_selected_node()) {
        this.$message.warning('请选择节点')
      } else {
        if (!this.jm.get_selected_node().data.fileIds) {
          this.nodeFileIds = []
        } else {
          // this.selectNode = this.jm.get_selected_node()
          this.nodeFileIds = this.jm.get_selected_node().data.fileIds
          this.selectNode = { 'jm': this.jm, 'get_selected_node': this.jm.get_selected_node() }
        }
        this.drawerVisible = true
      }
    },
    // -----------------任务开始-----------------------------

    // 查看选中节点ids
    viewSelectNodeFileIds () {},
    unMountFilesByMindMap () {
      this.forEachNode([this.jm.get_root()])
      // 重置容器数据
      this.changeContainer(this.jsmindData)
    },
    // 卸载当前节点及其子节点
    unMountFilesBychildrenNode () {
      if (!this.jm.get_selected_node()) {
        this.$message.warning('请选择节点')
        return
      }
      var nodes = []
      nodes.push(this.jm.get_selected_node())
      this.forEachNode(nodes)
    },
    // 遍历传入的节点
    forEachNode (nodes) {
      nodes.forEach(node => {
        // 卸载当前节点文件
        this.unMountFilesSpecifiedNodes(node)
        if (node.children !== undefined) {
          this.forEachNode(node.children)
        }
      })
    },
    // 卸载指定节点文件
    unMountFilesSpecifiedNodes (node) {
      this.unMountNode(node)
    },
    // 卸载当前节点文件
    unMountFilesByNode () {
      this.unMountNode(this.jm.mind.selected)
    },
    // 卸载节点文件，保留图标的方法
    unMountNode (selectNode) {
      selectNode.data['fileIds'] = []
      var topic = selectNode.topic
      var fileCountStr = topic.match(/[(][0-9]*[)]/, '')
      var iconHtml = topic.match(/<[^>]+>/g, '')
      if (iconHtml != null && iconHtml.length > 0) {
          for (let index = 0; index < iconHtml.length; index++) {
              topic = topic.replace(iconHtml[index], '')
          }
      }
      var prefix = topic.replace(iconHtml, '')
      if (fileCountStr != null) {
        prefix = prefix.replace(fileCountStr, '')
        this.jm.update_node(selectNode.id, prefix)
      } else {
        this.jm.update_node(selectNode.id, selectNode.topic)
      }
      if (this.$route.name === 'MindMap') { this.utilChangeMountStatus() }
    },
    // 标签挂载
    tagMount (tagName, fileIds) {
      if (!this.jm.get_selected_node()) {
        this.$message.warning('请选择节点')
        return
      }
      // 长出子节点
      var uuid = jsMind.util.uuid.newid()
      this.jm.add_node(this.jm.get_selected_node(), uuid, tagName + '(' + fileIds.length + ')')
      // 获取子节点
      var selectNode = this.jm.mind.selected
      var childrens = selectNode.children.filter(item => item.id === uuid)
      // 挂载文件
      this.mountWithNodeAndFileIds(childrens[0], fileIds)
    },
    // 文件挂载
    mountFiles () {
      try {
        var filterArr = this.jm.mind.selected.data['fileIds'].filter(item => !(this.fileIds.includes(item)))
        this.jm.mind.selected.data['fileIds'] = filterArr.concat(this.fileIds)
      } catch (err) {
        this.jm.mind.selected.data['fileIds'] = this.fileIds
      }
      // 修改tuopic文件数
      // var topic = this.jm.mind.selected.topic.replace(/[(][0-9]*[)]/, '')
      // topic = topic + `(${this.jm.mind.selected.data['fileIds'].length})`
      var fileCountStr = this.jm.mind.selected.topic.match(/[(][0-9]*[)]/, '')
      if (fileCountStr != null) {
         const prefix = this.jm.mind.selected.topic.replace(fileCountStr, '')
         this.jm.update_node(this.jm.get_selected_node().id, prefix)
      } else {
        this.jm.update_node(this.jm.get_selected_node().id, this.jm.mind.selected.topic)
      }
      if (this.$route.name === 'MindMap') { this.utilChangeMountStatus() }
    },

    // 挂载已有选择文件
    mountExistSelectFiles (fileIds) {
      this.mountWithNodeAndFileIds(this.jm.mind.selected, fileIds)
      // if (this.jm.mind.selected.data['fileIds'] === undefined) {
      //    this.jm.mind.selected.data['fileIds'] = fileIds
      // } else {
      //   var filterArr = this.jm.mind.selected.data['fileIds'].filter(item => !(fileIds.includes(item)))
      //   this.jm.mind.selected.data['fileIds'] = filterArr.concat(fileIds)
      // }
      // var fileCountStr = this.jm.mind.selected.topic.match(/[(][0-9]*[)]/, '')
      // if (fileCountStr != null) {
      //    const prefix = this.jm.mind.selected.topic.replace(fileCountStr, '')
      //    this.jm.update_node(this.jm.get_selected_node().id, prefix)
      // } else {
      //   this.jm.update_node(this.jm.get_selected_node().id, this.jm.mind.selected.topic)
      // }
      if (this.$route.name === 'MindMap') { this.utilChangeMountStatus() }
    },
    // 跟指定节点挂载文件
    mountWithNodeAndFileIds (node, fileIds) {
      if (node.data['fileIds'] === undefined) {
         node.data['fileIds'] = fileIds
      } else {
        var filterArr = node.data['fileIds'].filter(item => !(fileIds.includes(item)))
        node.data['fileIds'] = filterArr.concat(fileIds)
      }
      this.jm.update_node(node.id, node.topic)
      // var fileCountStr = node.topic.match(/[(][0-9]*[)]/, '')
      // if (fileCountStr != null) {
      //    const prefix = node.topic.replace(fileCountStr, '')
      //    this.jm.update_node(node.id, prefix)
      // } else {
      //   this.jm.update_node(node.id, node.topic)
      // }
    },
    // -----------------任务结束-----------------------------

    // 保存思维导图
    savaMindMap () {
      this.jsmindData.data = JSON.parse(JSON.stringify(this.jm.get_data('node_tree')))
      updateMindMap(this.jsmindData).then(res => {
        // res.success ? this.$message.success('保存成功') : this.$message.error('保存失败')
        if (res.success) {
          this.$message.success('保存成功')
          // 清楚全局变量中的数据
          this.$store.commit('deleteAttrJsmindMap', this.jsMindId)
          // 修改浏览器中打开思维导图变量
          const openJsminds = Vue.ls.get('openJsminds', null)
          if (openJsminds != null) {
            const newOpenJsminds = openJsminds.map(item => {
              if (item.id === this.jsmindData.id) {
                return this.jsmindData
              }
              return item
            })
            Vue.ls.set('openJsminds', newOpenJsminds, 1000 * 60 * 60 * 24)
          }
        } else {
          this.$message.error('保存失败')
        }
      })// .catch(error => this.$message.error('保存异常', error))
    },
    // 缩放
    zoom (value) { this.jm.view.setZoomByProportion(value) },
    // 放大
    zoomIn () { this.jm.view.zoomIn() },
    // 缩小
    zoomOut () { this.jm.view.zoomOut() },
    updataNode () { this.jm.begin_edit(this.jm.get_selected_node().id) },

    setAModal (aModalTitle, aModalPlaceholder) {
      this.aModalTitle = aModalTitle
      this.aModalPlaceholder = aModalPlaceholder
    },

    handleChangeTheme (vm, event) {
        this.jm.set_theme(`${vm.$slots.default[0].text}`)
    },
    handleChangeColer (value) {
      this.jm.set_node_color(this.jm.get_selected_node().id, null, value)
    },
    saveMountFileIds () {
      this.$store.commit('mountFileIds', this.getFileIds())
    },
    // 获取选中思维导图节点文件Id集合
    getSelectNodeFileIds () {
      var selectNode = this.jm.get_selected_node()
      if (!selectNode) {
        this.$message.warning('请选择节点')
      }
      if (selectNode) {
        if (selectNode.data.fileIds !== undefined) {
          return selectNode.data.fileIds
        } else {
          return []
        }
      }
    },
    // 获取思维导图所有挂载文件id集合
    getMindMapFileIds (jsmind) {
      // var obj = this.jsmindData.data.data
      var obj = jsmind.data.data
      var fileIds = []
      this.recursion([obj], fileIds) // 获取思维导图所有节点下的文件列表集合
      fileIds = fileIds.flatMap((x) => x) // 扁平化
      // return [this.jsMindId, fileIds]
      return fileIds
    },
    // 递归获取id
    recursion (objList, fileIds) {
      if (objList != null && objList.length > 0) {
        objList.forEach(obj => {
            if (obj.fileIds !== undefined) {
              fileIds.push(obj.fileIds)
            }
            if (obj.children !== undefined) {
              this.recursion(obj.children, fileIds)
            }
        })
      }
    },

    // --------------------测试方法------------------
    test () {
      this.jm.update_node(this.jm.get_selected_node().id, '测试')
      console.log('options=>', this.jm.options)
      this.jm.options.layout.pspace = 120
      // this.jm.view.reset_theme()
      console.log(this.jm.get_selected_node())
      console.log(this.jm.mind.selected.data)
      console.log(this.jm.get_selected_node().data)
      console.log(this.jm.mind.selected.data['background-color'])
      this.jm.mind.selected.data['fileIds'] = [1, 2, 3]

      this.jm.mind.selected.data['background-color'] = 'red'
      console.log('获取view=》', this.jm.mind.selected._data.view)
    },
    getDataTest () {
      console.log('node_tree=》', JSON.stringify(this.jm.get_data('node_tree')))
      console.log('传输数据=》', JSON.stringify(this.jm.get_data('node_tree').data))
    },
    // 获取数据
    getData () {

      // 获取元数据 : 使用 jm.get_meta() 方法可获取当前思维导图的元数据。
      // 获取数据 : 使用 jm.get_data(data_format) 方法可获取当前思维导图的指定格式的数据文本。
      // console.log('元数据=》', this.jm.get_meta())

      // 获取选中节点
      // var nodeElement = this.jm.get_selected_node()._data.view.element
      // console.log('nodeElement=》', nodeElement)
      // var testData = { 'name': 'zhouwei', 'age': 18 }

      // nodeElement.setAttribute('name', testData)
      // console.log('nodeElement=》', JSON.parse(nodeElement.getAttribute(name)))

      // console.log('node=》', this.jm.get_selected_node())
      // console.log('data=》', this.jm.get_selected_node().data)
      // console.log('name=》', nodeElement.getAttribute('name'))
      // console.log('node_tree=》', JSON.stringify(this.jm.get_data('node_tree')))
      // console.log('传输数据=》', JSON.stringify(this.jm.get_data('node_tree').data))

      // console.log('主题=》', this.jm.options.theme)

      // console.log('是否支持节点里的HTML元素=》', this.jm.options.support_html)
      // console.log('是否启用编辑=》', this.jm.options.mode)
      // console.log('是否启用编辑=》', this.jm.options.editable)

      // this.jm.mind.selected.data['fileIds'] = [1, 2, 3]
      // // 重写底层方法
      // console.log('底层方法=》', this.jm.shortcut.handles.editnode)
      // console.log('获取底层数据=>', this.jm.prototype.n)
    },
    resize_jsmind () {
        this.jm.view.resize()
    },
    // 获取数据
    getDataRoot () {
      // get_node_data
      var nodeId = this.jm.get_selected_node().id
      this.jsmindData.data = JSON.parse(JSON.stringify(this.jm.get_data('node_tree')))
      // var data = JSON.parse(JSON.stringify(this.jm.get_data('node_tree')))
      getCopyNodeByNodeId(nodeId, this.jsmindData).then(res => {
        if (res.success) {

        }
      })

      // console.log('node_tree=》', JSON.stringify(this.jm.get_data('node_tree')))
      //  console.log('get_node_data=》', JSON.stringify(this.jm.get_node_data('node_tree_copynode')))
      // console.log('node=>', this.jm.get_node(this.jm.get_selected_node().id))
      // console.log('node=》', this.jm.get_selected_node())
      // console.log('node=》', JSON.stringify(this.jm.get_selected_node()))
      // selected  <img src="/assets/priority1.16284e9a.svg">
      //       var jsmindDiv = document.getElementById(this.jsMindId)
      // // 删除原来节点下数据
      // var firstElementChild = jsmindDiv.firstElementChild

      // var selected = document.getElementsByClassName('selected')
      // var img = document.createElement('img')
      // img.src = '/assets/priority1.16284e9a.svg'
      // selected[0].appendChild(img)
      // var jsmnode = document.querySelectorAll("jmnode[class='selected']")
      // console.log(firstElementChild, jsmnode, selected)

      // 重新渲染原来节点数据
      // this.changeFamily()
      // console.log('get_data获取的数据=》', this.jm.get_data('node_tree'))
      // console.log('传过来色数据=》', this.jsMindMapData)
      // console.log('子组件组织的数据=》', this.jsmindData)
      // this.resize_jsmind()
      // console.log('jm=>', this.jm.layout.layout())
      // console.log('视图=》', this.jm.view)
      // this.jm.view.actualZoom = 2
      // this.jm.view.show(true)
      // this.changeContainer(this.jsmindData)
      // this.jm.view.setZoomByProportion(0.5)
      // this.jm.mind.selected.data['fileIds'] = filterArr.concat(this.fileIds)
      // console.log('当前选中节点=》', this.jm.get_selected_node())
      // console.log('元数据=》', this.jm.get_meta())

      // var nodeElement = this.jm.get_selected_node()._data.view.element
      // var testData = { 'name': 'zhouwei', 'age': 18 }
      // nodeElement.setAttribute('name', testData)

      //  console.log('node=》', this.jm.get_selected_node())
      //  console.log('name=》', nodeElement.getAttribute('name').age)
      //  console.log('node_tree=》', this.jm.get_data('node_tree'))
    }
    // mytest () {
    //   this.$emit('getMindMapDate', this.jsmindData)
    // }
  }
}
</script>

<style scoped lang="less">
.jsmind_container {
  // width: calc(50wh - 0px);
  // height: calc(100vh - 165px);
  border: solid 1px #ccc;
  background: #f4f4f4;
  // display: flex;
  //   justify-content: center;
  // align-items: center;
}
.box{
   width:100%;
   background: #f1f2f6;
  .home{
    margin: 0 auto;
    width: calc(100% - 80px)
  }
}
.jsmind_container_no_scroll_axis {
  background: #f4f4f4;
  display: flex;
  justify-content: center;
  align-items: center;
}

.jsmind_container_no_scroll_axis > .jsmind-inner::-webkit-scrollbar {
  display: none !important; /* Chrome Safari */
}

.jsmind_container_no_scroll_axis > .jsmind-inner {
  scrollbar-width: none !important; /* firefox */
  -ms-overflow-style: none !important; /* IE 10+ */
  overflow-x: hidden !important;
  overflow-y: hidden !important;
}
</style>
