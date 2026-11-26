<!--工作面板-->
<template>
  <a-layout-content :style="{ height: '100%', margin: '12px 12px 0'}">
    <a-layout >
      <a-layout-sider style="background:#f0f2f5">
        <a-card :style="{ width: '100%', height: 'calc(100vh - 78px)'}">
          <a-button type="primary" block> 快速访问 </a-button>
          <a-button
            block
            :title="item.collectionName"
            class="text-overflow"
            v-for="(item,index ) in collectionList "
            :key="index"
            style="margin-top:5px;text-align: left;"
            @click="toQuickPage(item)">
            <!-- 流程（脑图） -->
            <a-icon type="fork" v-if="item.collectionType === 'MIND_MAP'"/>
            <!-- 文件 -->
            <a-icon type="file" v-if="item.collectionType === 'FILE'"/>
            <!-- 项目 -->
            <a-icon type="folder-open" v-if="item.collectionType === 'PROJECT'" />
            <span style="marin-left:0px" >{{ item.collectionName }}</span>
          </a-button>
        </a-card>
      </a-layout-sider>

      <a-layout-content class="page-header-index-wide" style="margin-left:10px;">
        <div class="card-list" ref="content">
          <div style="margin-bottom:10px">
            <a-card :bordered="false" class="ant-pro-components-tag-select">
              <a-row >
                <a-col :span="19">
                  <a-form layout="inline" >
                    <a-form-item label="类型：">
                      <a-select style="width: 150px" v-model="paramMap.type" >
                        <a-select-option value="project"> 项目 </a-select-option>
                        <a-select-option value="jsmind"> 思维导图 </a-select-option>
                      </a-select>
                    </a-form-item>
                    <a-form-item label="操作人：">
                      <a-input v-model="paramMap.createUser" placeholder="请输入操作人" />
                    </a-form-item>
                    <a-form-item :wrapper-Col="{ span: 8, offset: 4 }" label="名称：">
                      <a-input v-model="paramMap.targetName" placeholder="请输入名称" />
                    </a-form-item>
                    <a-form-item label="操作时间：">
                      <a-range-picker v-model="paramMap.startEndTime" valueFormat="YYYY-MM-DD" style="width: 240px;">
                        <a-icon slot="suffixIcon" type="smile" />
                      </a-range-picker>
                    </a-form-item>
                  </a-form>
                </a-col>
                <a-col :span="5" style="text-align: right;">
                  <a-button type="primary" @click="reset()" >重置</a-button>
                  <a-button type="primary" @click="listPage()" style="margin-left:5px">查询</a-button>
                  <a-button type="primary" @click="createProject()" style="margin-left:5px">新建项目</a-button>
                </a-col>
              </a-row>
            </a-card>
          </div>

          <a-card :bordered="false" >
            <div
              v-infinite-scroll="handleInfiniteOnLoad"
              class="demo-infinite-container"
              :infinite-scroll-disabled="busy"
              :infinite-scroll-distance="10">
              <a-list
                :grid="{gutter: 24, lg: 3, md: 1, sm: 1, xs: 1}"
                :dataSource="operationList">
                <a-list-item slot="renderItem" slot-scope="item">
                  <a-card :hoverable="true" :bodyStyle="{padding:'12px'}">
                    <a-card-meta>
                      <a slot="title" @click="toTargetPage(item)">{{ item.targetName }}</a>
                      <!-- process knowledge_base -->
                      <a-avatar @click="toTargetPage(item)" class="card-avatar" slot="avatar" size="large" :src="item.operationTable === 'jsmind' ? '/svg/process.svg' : '/svg/knowledge_base.svg'" />
                      <div class="meta-content" slot="description">{{ item.operationDate }} / {{ item.createUser }} / {{ item.operationDescription }} </div>
                    </a-card-meta>
                  </a-card>
                </a-list-item>
              </a-list>
            </div>
          </a-card>
        </div>
      </a-layout-content>
    </a-layout>
  </a-layout-content>

</template>

<script>

import 'v-contextmenu/dist/index.css'
import infiniteScroll from 'vue-infinite-scroll'
import { RecycleScroller } from 'vue-virtual-scroller'
import 'vue-virtual-scroller/dist/vue-virtual-scroller.css'

import OperatingButton from '@/components/content/OperatingButton/index'
import FileLisrOrCart from '@/components/content/FileLisrOrCart/index'
import { getOperations } from '@/api/workSurface'
import { collectionList } from '@/api/collection'
import { getMindMapById } from '@/api/mindMap'

import { knowledgeBaseIcon, processIcon } from '@/core/icons'

export default {
  directives: { infiniteScroll },
  data () {
    return {
      busy: false,
      paramMap: {
        type: '',
        targetName: '',
        createUser: '',
        startEndTime: []
      },
      operationList: [],
      isFileListOrCart: true,
      collectionList: []
    }
  },
  methods: {
        // 重置
    reset () {
      this.paramMap = { type: '', targetName: '', createUser: '', startEndTime: [] }
      this.listPage()
    },
        // 条件查询
    listPage () {
      getOperations(1, 100, this.paramMap).then(res => {
          this.operationList = res.data.data
      })
    },
    handleInfiniteOnLoad () {

    },
    // 快速访问
    toQuickPage (collection) {
      if (collection.collectionType === 'PROJECT') {
        // 项目明细页面
        this.$router.push({ name: 'ProjectDetail', query: { projectId: collection.targetId } })
      } else if (collection.collectionType === 'MIND_MAP') {
        // 思维导图操作页面
        // 获取该思维导图所属项目id
        getMindMapById(collection.targetId).then(res => {
          if (res.success) {
            this.$router.push({ name: 'MindMap', query: { projectId: res.data.projectId, jsmindId: res.data.id } })
          }
        })
      } else if (collection.collectionType === 'FILE') {
        // 文件预览
        this.$router.push({ name: 'FilePreviewAndNotes', query: { fileId: collection.targetId } })
      }
    },
    // 快捷跳转到指定页面
    toTargetPage (item) {
      if (item.operationTable === 'jsmind') {
        // 获取项目id
        getMindMapById(item.targetId).then(res => {
          if (res.success) {
            this.$router.push({ name: 'MindMap', query: { projectId: res.data.projectId, jsmindId: res.data.id } })
          }
        })
      } else if (item.operationTable === 'project') {
        this.$router.push({ name: 'ProjectDetail', query: { projectId: item.targetId } })
      }
    },
    getIsFileListOrCart (data) {
      // 发送请求初始化新建文档表单所需数据
      this.isFileListOrCart = data
    },
    createProject () {
      this.$router.push({ name: 'CreateProject', params: { routeName: 'WorkSurface' } }) // CreateProjectStep
    }
  },
  created () {},
  mounted () {
    this.listPage()
    collectionList().then(res => {
      this.collectionList = res.data
    })
  },
  components: {
    OperatingButton,
    FileLisrOrCart,
    RecycleScroller,
    knowledgeBaseIcon,
    processIcon
  }
}
</script>

<style scoped lang="less">
.demo-infinite-container {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  overflow: auto;
  padding: 8px 24px;
  height: calc(100vh - 225px)
}
.demo-loading-container {
  position: absolute;
  bottom: 40px;
  width: 100%;
  text-align: center;
}
  .text-overflow{
    margin-top:15px;
    display: -webkit-box;/*作为弹性伸缩盒子模型显示*/
    -webkit-line-clamp: 1; /*显示的行数；如果要设置2行加...则设置为2*/
    overflow: hidden; /*超出的文本隐藏*/
    text-overflow: ellipsis; /* 溢出用省略号*/
    -webkit-box-orient: vertical;/*伸缩盒子的子元素排列：从上到下*/
  }
</style>
