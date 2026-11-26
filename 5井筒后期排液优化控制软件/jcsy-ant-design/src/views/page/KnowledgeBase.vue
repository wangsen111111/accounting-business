<template>
  <div class="card-list" ref="content" >
    <a-modal
      :width="'30%'"
      title="新建知识库"
      :visible="newBuildVisible"
      :confirm-loading="confirmLoading"
      @ok="newBuildHandleOk"
      @cancel="newBuildHandleCancel">
      <a-form-model
        :model="newBuildForm"
        layout="horizontal"
        v-bind="{ labelCol: { span: 4 }, wrapperCol: { span: 14 } }">
        <a-form-model-item label="选择模板">
          <a-select v-model="newBuildForm.template" :placeholder="'请选择模板'">
            <a-select-option
              :key="index"
              v-for="(item,index) in responseData.templates"
              :value="item"
            >{{ item }}</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="权限分配">
          <a-select v-model="newBuildForm.permission" placeholder="请选择权限">
            <a-select-option
              :key="index"
              v-for="(item,index) in responseData.permissions"
              :value="item"
            >{{ item }}</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="知识库名称">
          <a-input v-model="newBuildForm.name" placeholder="请填写知识库名称" />
        </a-form-model-item>
        <a-form-model-item label="知识库简介">
          <a-input v-model="newBuildForm.introduction" type="textarea" placeholder="请填写知识库简介" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>

    <a-modal
      :width="'50%'"
      title="文档上传"
      :visible="uploadDocVisible"
      :confirm-loading="confirmLoading"
      @ok="uploadDocHandleOk"
      @cancel="uploadDocHandleCancel">
      <a-row>
        <a-col :span="12">
          <a-form-model layout="horizontal" :model="uploadDocFrom" v-bind="{ labelCol: { span: 4 }, wrapperCol: { span: 14 } }">
            <a-form-model-item label="关联知识库">
              <a-select
                v-model="uploadDocFrom.knowledgeBaseNames"
                mode="tags"
                style="width: 100%"
                placeholder="请选择知识库"
                @change="change">
                <a-select-option v-for="(item) in responseData.treeData" :key="item.title">{{ item.title }}</a-select-option>
              </a-select>
            </a-form-model-item>
            <a-form-model-item label="权限分配">
              <a-select v-model="uploadDocFrom.permission" placeholder="请选择权限">
                <a-select-option
                  :key="index"
                  v-for="(item,index) in responseData.permissions"
                  :value="item">{{ item }}</a-select-option>
              </a-select>
            </a-form-model-item>
            <a-form-model-item label="文档名称">
              <a-input v-model="uploadDocFrom.name" placeholder="请填写知识库名称" />
            </a-form-model-item>
            <a-form-model-item label="文档简介">
              <a-input v-model="uploadDocFrom.introduction" type="textarea" placeholder="请填写文档简介" />
            </a-form-model-item>
            <a-form-model-item label="文档标签">
              <a-select v-model="uploadDocFrom.tags" mode="tags" style="width: 100%" placeholder="请选择文档标签">
                <a-select-option v-for="(item,index) in responseData.tags" :key="index.toString()">{{ item }}</a-select-option>
              </a-select>
            </a-form-model-item>
            <a-form-model-item label="文档标签">
              <a-upload-dragger
                name="file"
                :multiple="true"
                action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
                @change="handleChange" >
                <p class="ant-upload-drag-icon"> <a-icon type="inbox" /> </p>
                <p class="ant-upload-text"> 文档上传 </p>
                <!-- <p class="ant-upload-hint"> 222 </p> -->
              </a-upload-dragger>
            </a-form-model-item>

          </a-form-model>
        </a-col>
        <a-col :span="12">
          <div class="page-header-index-wide">
            <a-select style="width: 100%" @change="SelectOperationKnowledgeBase" placeholder="请选择要操作的知识库">
              <a-select-option :key="index" v-for="(item,index) in displaknowledgeBaseNames" :value="item">{{ item }}</a-select-option>
            </a-select>
            <a-card :bordered="false">
              <a-tree :treeData="displaTreeData" >
                <template slot="custom" slot-scope="item">
                  <span>{{ item.title }}</span>
                  <a-popconfirm ok-text="Yes" cancel-text="No" @confirm="confirm" @cancel="cancel" >
                    <div slot="title" slot-scope="">
                      <span >输入新名字</span>
                      <a-input v-model="newTreeTitle">{{ item.title }}</a-input>
                    </div>
                    <a-button type="link" class="but_type" style="right:200px;" @click.stop="()=> append(item)" >新增</a-button>
                  </a-popconfirm>
                  <a-popconfirm ok-text="Yes" cancel-text="No" @confirm="confirm" @cancel="cancel" >
                    <div slot="title" slot-scope="">
                      <span >输入新名字</span>
                      <a-input v-model="newTreeTitle">{{ item.title }}</a-input>
                    </div>
                    <a-button v-if="item.pid" type="link" class="but_type" style="right:120px;" @click.stop="()=> edit(item)" >编辑</a-button>
                  </a-popconfirm>
                  <a-button v-if="item.pid" type="link" class="but_type" @click="(e)=> remove(item)">删除</a-button>
                </template>
              </a-tree>
            </a-card>
          </div>
        </a-col>
      </a-row>
    </a-modal>

    <operating-button @isFileListOrCart="getIsFileListOrCart" @isNewBuild="getIsNewBuild" @isUploadDoc="getIsUploadDoc"></operating-button>
    <FileLisrOrCart :isFileListOrCart="isFileListOrCart"></FileLisrOrCart>

  </div>

</template>

<script>
// import { getTemplates, getTreeData } from '@/api/knowledgeBase'
import OperatingButton from '@/components/content/OperatingButton/index'
import FileLisrOrCart from '@/components/content/FileLisrOrCart/index'

const names = ['知识库1', '知识库2', '知识库3', '知识库4']
const nodeNames = ['目录1', '目录2', '目录3', '目录4']
const treeData = [
  {
    id: '0',
    pid: null,
    title: names[0],
    key: '0-0',
    scopedSlots: { title: 'custom' },
    children: [
      {
        id: '1',
        pid: '0',
        title: nodeNames[0] + ':0-0-0',
        key: '0-0-0',
        scopedSlots: { title: 'custom' },
        children: [
          { id: '2', pid: '1', title: '0-0-0-0', key: '0-0-0-0', scopedSlots: { title: 'custom' }, children: [ { id: '5', pid: '2', title: '0-0-0-3', key: '0-0-0-3', scopedSlots: { title: 'custom' } } ] },
          { id: '3', pid: '1', title: '0-0-0-1', key: '0-0-0-1', scopedSlots: { title: 'custom' } },
          { id: '4', pid: '1', title: '0-0-0-2', key: '0-0-0-2', scopedSlots: { title: 'custom' } }
        ]
      }
    ]
  },
  {
    id: '101',
    pid: null,
    title: names[1],
    key: '0-1',
    scopedSlots: { title: 'custom' },
    children: [
      { id: '21', pid: '101', title: '0-1-0-0', key: '0-1-0-0', scopedSlots: { title: 'custom' } },
      { id: '22', pid: '101', title: '0-1-0-1', key: '0-1-0-1', scopedSlots: { title: 'custom' } },
      { id: '23', pid: '101', title: '0-1-0-2', key: '0-1-0-2', scopedSlots: { title: 'custom' } }
    ]
  }
]
export default {
  data () {
    return {
      isFileListOrCart: true,
      confirmLoading: false,
      uploadDocVisible: false,
      displaTreeData: [], // 知识库下拉框选中右侧树要展示的数据
      displaknowledgeBaseNames: [],
      buttonType: '',
      newTreeTitle: '',
      currentTreeData: '',
      uploadDocFrom: {
        knowledgeBaseNames: [], // 模板
        permission: undefined, // 权限分配
        introduction: '', // 简介
        name: '', // 名称
        tags: [], // 文档标签
        knowledgeBaseIds: []
      },
      newBuildVisible: false,
      newBuildForm: {
        template: undefined, // 模板
        permission: undefined, // 权限分配
        introduction: '', // 简介
        name: '' // 名称
      },
      responseData: {
        treeData: treeData,
        templates: ['知识库1', '知识库2', '知识库3', '知识库4'],
        permissions: ['仅自己', '全部', '可读', '可写'],
        tags: ['标签1', '标签2', '标签3', '标签4', '标签5', '标签6', '标签7', '标签8', '标签9']
      }
    }
  },
  components: {
    OperatingButton,
    FileLisrOrCart
  },
  watch: {
      displaTreeData (newValue, oldValue) { // 其中 newValue 是监听改变后的值，oldValue 是改变之前的值
          console.log('改变前的数据', oldValue)
          console.log('改变后的数据', newValue) // 可以在此处执行对应的语句或函数
      }
  },
  methods: {
    handleChange (info) {
      const status = info.file.status
      if (status !== 'uploading') {
        console.log(info.file, info.fileList)
      }
      if (status === 'done') {
        this.$message.success(`${info.file.name} file uploaded successfully.`)
      } else if (status === 'error') {
        this.$message.error(`${info.file.name} file upload failed.`)
      }
    },
    uploadDocHandleOk (e) {
      // 提交表单数据
      // 关闭弹框，提示响应是否成功
      this.confirmLoading = true
      setTimeout(() => {
        this.uploadDocVisible = false
        this.confirmLoading = false
      }, 200)
    },
    uploadDocHandleCancel (e) {
      this.uploadDocVisible = false
    },

    newBuildHandleOk (e) {
      this.newBuildVisible = false
      // this.confirmLoading = true
      // setTimeout(() => {
      //   this.newBuildVisible = false
      //   this.confirmLoading = false
      // }, 200)
    },
    newBuildHandleCancel (e) {
      this.newBuildVisible = false
    },
    getIsFileListOrCart (data) {
      // 发送请求初始化新建文档表单所需数据
      this.isFileListOrCart = data
    },
    getIsNewBuild (isNewBuild) {
      // getTemplates().then(res => {
      //           this.responseData.templates = res.data
      //       }).catch(err => {
      //           this.$message.error(`获取模板数据失败: ${err.message}`)
      //       })
      this.newBuildVisible = isNewBuild
    },
    getIsUploadDoc (isUploadDoc) {
      // getTreeData().then(res => {
      //           this.responseData.treeData = res.data
      //       }).catch(err => {
      //           this.$message.error(`获取模板数据失败: ${err.message}`)
      //       })
      // 发送请求初始化上传文档表单所需数据
      this.uploadDocVisible = isUploadDoc
    },
    // 文档上传相关逻辑
    uuid () {
      function S4 () {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
      }
    return (S4() + S4() + '-' + S4() + '-' + S4() + '-' + S4() + '-' + S4() + S4() + S4())
    },
    confirm (e) {
       switch (this.buttonType) {
        case 'append':
          // 模拟添加
          const data = this.currentTreeData
          const newChild = {
            id: this.uuid,
            pid: data.id,
            title: this.newTreeTitle,
            key: this.newTreeTitle,
            scopedSlots: { title: 'custom' },
            children: [] }
          if (!data.children) {
            this.$set(data, 'children', [])
          }
          data.children.push(newChild)
          this.$message.success('添加成功')
          break
       case 'edit':
        this.currentTreeData.title = this.newTreeTitle
         this.searchOption(this.currentTreeData, this.displaTreeData, 'edit')
         this.$message.success('修改成功')
          break
        default:
      }
    },
    cancel (e) {
      this.$message.success('取消操作')
    },
    // 递归查找
    searchOption (option, arr, type) { // type = 'delect'
      for (let s = 0; s < arr.length; s++) {
        if (arr[s].key === option.key) {
          if (type === 'remove') {
            arr.splice(s, 1)
          } else if (type === 'edit') {
            console.log(option)
            // option.title = '测试'
          // 这是模拟数据编辑数据
            this.$set(arr, s, option)
            console.log(this.displaTreeData)
          }
          break
        } else if (arr[s].children && arr[s].children.length > 0) { // 递归条件
          this.searchOption(option, arr[s].children, type)
        } else {
          continue
        }
      }
    },
    append (data) {
      this.buttonType = 'append'
      this.newTreeTitle = ''
      this.currentTreeData = data
    },
    remove (data) {
      this.buttonType = 'remove'
        // 先请求后端接口，删除成功后执行
      // this.searchOption(data, this.responseData.treeData)
      this.searchOption(data, this.displaTreeData, 'remove')
      // data = {}
    },
    edit (data) {
      this.buttonType = 'edit'
        // 先请求后端接口，编辑成功后执行
        this.newTreeTitle = data.title
        this.currentTreeData = data
    },
    change (value) {
      this.displaknowledgeBaseNames = []
       this.displaknowledgeBaseNames = this.responseData.treeData.filter(item => {
        if (value.indexOf(item.title) !== -1) return item
      }).map(item => item.title)
    },
    SelectOperationKnowledgeBase (value) {
      this.displaTreeData = []
       // 添加选中的一级节点
      this.displaTreeData = this.responseData.treeData.filter(item => {
        if (value.indexOf(item.title) !== -1) return item
      })
    }
  },
  computed: {}
}
</script>

<style lang="less" scoped>
@import "~@/components/index.less";
.ant-tree-title {
  width: 100%;
}
.title {
  float: left;
}
.ant-card-body {
  :global {
    .ant-tree {
      line-height: 3;
      li {
        position: relative;
      }
    }
  }
}
.ant-card-body .but_type {
  float: right;
  position: absolute;
  right: 40px;
}
.ads{
    position:fixed;
    top:200px;
}
</style>
