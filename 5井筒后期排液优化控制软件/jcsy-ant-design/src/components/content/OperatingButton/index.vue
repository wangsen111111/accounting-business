<template>
  <div>
    <a-card :bordered="false" class="ant-pro-components-tag-select">
      <a-row >
        <a-col :span="16">
          <a-select mode="tags" placeholder="类型筛选" style="width: 200px;margin-left:10px" @change="selectFileTypes">
            <a-select-option v-for="fileType in fileTypes" :key="fileType"> {{ fileType }} </a-select-option>
          </a-select>
          <a-input-search style="width: 200px;margin-left:10px" placeholder="请输入要查找的文件" enter-button @search="onSearch" />
        </a-col>
        <a-col :span="8" style="text-align: right;">
          <a-button v-if="!previewStatus && 'KnowledgeBase' != this.$route.name" type="primary" class="button" @click="showModal('openPreview')" >开启预览</a-button>
          <a-button v-if="previewStatus && 'KnowledgeBase' != this.$route.name" type="primary" class="button" @click="showModal('closePreview')" >关闭预览</a-button>
          <a-button type="primary" class="button" @click="showModal('newBuild')" >新建</a-button>
          <a-button type="primary" v-if="this.$route.name === 'KnowledgeBase'" class="button" @click="showModal('uploadDoc')" >上传文档</a-button>
          <a-button type="primary" class="button" @click="changeFileListOrCart" >
            <span v-if="isShow">文件卡片</span>
            <span v-if="!isShow">文件列表</span>
          </a-button>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script>
import NewKnowledgeBase from '@/components/content/from/NewKnowledgeBase'
import NewDoc from '@/components/content/from/NewDoc'
import UploadDoc from '@/components/content/from/UploadDoc'
export default {
  data () {
    return {
      data: [{ value: 2, text: 'dd' }],
      value: 0,
      // fileTypes: ['文档', 'word', 'excel', 'pdf', '图片', '工作流程类', '自定义知识库'],
      isShow: true,
      confirmLoading: false
    }
  },
  props: {
    previewStatus: { type: Boolean, default: false },
    fileTypes: {
      type: Array,
      default: null
    }
  },
  methods: {
    handleChange () {},
    onSearch (value) {
      console.log(value)
    },
    changeFileListOrCart () {
      this.isShow = !this.isShow
      this.$emit('isFileListOrCart', this.isShow)
    },
    showModal (type) {
      const menuType = this.$route.name
      switch (menuType) {
        case 'KnowledgeBase':
          if (type === 'newBuild') {
            this.$emit('isNewBuild', true)
          } else if (type === 'uploadDoc') {
            this.$emit('isUploadDoc', true)
          }
          break
        case 'Doc':
          this.$emit('isNewBuild', true)
          break
        case 'KnowledgeBaseDetail':
          if (type === 'openPreview') {
           this.$emit('openPreview', true)
          } else if (type === 'closePreview') {
            this.$emit('closePreview', false)
          }
          break
        case 'WorkSurface':
          if (type === 'newBuild') {
            this.$emit('createProject', true)
          } else if (type === 'uploadDoc') {
            this.$emit('isUploadDoc', true)
          }
          break
        default:
      }
      // console.log(this.$route.name)
    },
    selectFileTypes (value) {
      this.$emit('selectFileTypes', value)
    }
  },
  created () {},
  mounted () {},
  components: {
    NewKnowledgeBase,
    NewDoc,
    UploadDoc
  }
}
</script>

<style scoped lang="less">
.button{
  margin-left:10px
}
</style>
