<template>
  <div style="margin-bottom:20px" >
    <a-card :bordered="false" class="ant-pro-components-tag-select">
      <a-row >
        <a-col :span="16">
          <a-select
            mode="tags"
            placeholder="类型筛选"
            style="width: 200px;margin-left:10px"
            @change="handleChange" >
            <a-select-option v-for="fileType in fileTypes" :key="fileType"> {{ fileType }} </a-select-option>
          </a-select>
          <a-input-search style="width: 200px;margin-left:10px" placeholder="input search text" enter-button @search="onSearch" />
        </a-col>
        <a-col :span="8" style="text-align: right;">
          <a-button type="primary" @click="showModal('newBuild')" >新建</a-button>
          <a-modal
            :width="'50%'"
            :title="title"
            :visible="visible"
            :confirm-loading="confirmLoading"
            @ok="handleOk"
            @cancel="handleCancel" >
            <div v-if="title=='新建知识库'"><NewKnowledgeBase/></div>
            <div v-if="title=='新建文档'"><NewDoc/></div>
            <div v-if="title=='上传文档'"><UploadDoc/></div>
          </a-modal>
          <a-button type="primary" v-if="this.$route.name === 'KnowledgeBase'" style="margin-left:10px" @click="showModal('uploadDoc')" >上传文档</a-button>
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
      title: '',
      data: [{ value: 2, text: 'dd' }],
      value: 0,
      fileTypes: ['文档', 'word', 'excel', 'pdf', '图片', '工作流程类', '自定义知识库'],
      isShow: true,
      ModalText: 'Content of the modal',
      visible: false,
      confirmLoading: false
    }
  },
  methods: {
    handleChange () {},
    onSearch (value) {
      console.log(value)
    },
    changeState () {
      this.isShow = !this.isShow
      this.$emit('isShowMsg', this.isShow)
    },
    showModal (type) {
      const menuType = this.$route.name
      switch (menuType) {
        case 'KnowledgeBase':
          if (type === 'newBuild') {
            this.title = '新建知识库'
          } else if (type === 'uploadDoc') {
            this.title = '上传文档'
          }

          break
        case 'Doc':
          this.title = '新建文档'
          break
        default:
      }
      this.visible = true
      console.log(this.$route.name)
    },
    handleOk (e) {
      this.flag = false
      this.ModalText = 'The modal will be closed after two seconds'
      this.confirmLoading = true
      setTimeout(() => {
        this.visible = false
        this.confirmLoading = false
      }, 2000)
    },
    handleCancel (e) {
      this.flag = false
      console.log('Clicked cancel button')
      this.visible = false
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
</style>
