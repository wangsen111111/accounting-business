<template>
  <div>
    <a-modal
      :width="'50%'"
      title="新建知识库"
      :visible="visible"
      :confirm-loading="confirmLoading"
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <a-row>
        <a-col :span="12">
          <a-form-model :layout="form.layout" :model="form" v-bind="formItemLayout">
            <a-form-model-item label="关联知识库">
              <a-select
                v-model="form.knowledgeBaseNames"
                mode="tags"
                style="width: 100%"
                placeholder="请选择知识库"
                @change="change">
                <a-select-option v-for="(item) in echo.treeData" :key="item.title">{{ item.title }}</a-select-option>
              </a-select>
            </a-form-model-item>
            <a-form-model-item label="权限分配">
              <a-select v-model="form.permission" placeholder="请选择权限">
                <a-select-option
                  :key="index"
                  v-for="(item,index) in echo.permissions"
                  :value="item">{{ item }}</a-select-option>
              </a-select>
            </a-form-model-item>
            <a-form-model-item label="知识库名称">
              <a-input v-model="form.name" placeholder="请填写知识库名称" />
            </a-form-model-item>
            <a-form-model-item label="知识库简介">
              <a-input v-model="form.introduction" type="textarea" placeholder="请填写知识库简介" />
            </a-form-model-item>
            <a-form-model-item label="文档标签">
              <a-select v-model="form.tags" mode="tags" style="width: 100%" placeholder="请选择文档标签">
                <a-select-option v-for="(item,index) in echo.tags" :key="index.toString()">{{ item }}</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-form-model>
        </a-col>
        <a-col :span="12">
          <!-- <Tree></Tree> -->
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
                    <a-button type="link" class="but_type" style="right:200px;" @click="()=> append(item)" >新增</a-button>
                  </a-popconfirm>
                  <a-popconfirm ok-text="Yes" cancel-text="No" @confirm="confirm" @cancel="cancel" >
                    <div slot="title" slot-scope="">
                      <span >输入新名字</span>
                      <a-input v-model="newTreeTitle">{{ item.title }}</a-input>
                    </div>
                    <a-button v-if="item.pid" type="link" class="but_type" style="right:120px;" @click="()=> edit(item)" >编辑</a-button>
                  </a-popconfirm>
                  <a-button v-if="item.pid" type="link" class="but_type" @click="(e)=> remove(item)">删除</a-button>
                </template>
              </a-tree>
            </a-card>
          </div>
        </a-col>
      </a-row>
      <a-row style="text-align:right">
        <a-button type="primary"> 取消 </a-button>
        <a-button style="margin-left:10px" type="primary"> 确定 </a-button>
      </a-row>
    </a-modal></div>
</template>
<script>
const names = ['模板1', '模板2', '模板3', '模板4']
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
      },
      {
        id: '6',
        pid: '0',
        title: nodeNames[0] + ':0-0-1',
        key: '0-0-1',
        scopedSlots: { title: 'custom' },
        children: [
          { id: '7', pid: '1', title: nodeNames[1] + '0-0-1-1', key: '0-0-1-1', scopedSlots: { title: 'custom' } },
          { id: '8', pid: '1', title: nodeNames[1] + '0-0-1-2', key: '0-0-1-2', scopedSlots: { title: 'custom' } }
        ]
      },
      {
        id: '9',
        pid: '0',
        title: nodeNames[0] + '0-0-2',
        key: nodeNames[0] + '0-0-2',
        scopedSlots: { title: 'custom' }
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
  },
  {
    id: '10',
    pid: null,
    title: names[2],
    key: '0-2',
    scopedSlots: { title: 'custom' }
  },
  {
    id: '11',
    pid: null,
    title: names[3],
    key: '0-3',
    scopedSlots: { title: 'custom' }
  }
]
export default {
  data () {
    return {
      form: {
        layout: 'horizontal',
        knowledgeBaseNames: [], // 模板
        permission: undefined, // 权限分配
        introduction: '', // 简介
        name: '', // 名称
        tags: [], // 文档标签
        knowledgeBaseIds: []
      },
      echo: {
        treeData: treeData,
        permissions: ['仅自己', '全部', '可读', '可写'],
        tags: ['标签1', '标签2', '标签3', '标签4', '标签5', '标签6', '标签7', '标签8', '标签9']
      },
      displaTreeData: [], // 知识库下拉框选中右侧树要展示的数据
      displaknowledgeBaseNames: [],
      buttonType: '',
      newTreeTitle: '',
      currentTreeData: ''
    }
  },
  computed: {
    formItemLayout () {
      const { layout } = this.form
      return layout === 'horizontal' ? { labelCol: { span: 4 }, wrapperCol: { span: 14 } } : {}
    },
    buttonItemLayout () {
      const { layout } = this.form
      return layout === 'horizontal' ? { wrapperCol: { span: 14, offset: 4 } } : {}
    }
  },
  methods: {
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
         this.$message.success('修改成功')
          break
        default:
      }
    },
    cancel (e) {
      console.log(e)
      this.$message.success('取消操作')
    },
    // 递归查找
    searchOption (option, arr, type = 'delect') {
      console.log(option, arr)
      for (let s = 0; s < arr.length; s++) {
        console.log(arr[s].key, option.key)
        if (arr[s].key === option.key) {
          if (type === 'delect') {
            arr.splice(s, 1)
          } else {
          // 这是模拟数据编辑数据
            this.$set(arr, s, {
              title: '12121212',
              key: '12121212',
              scopedSlots: { title: 'custom' }
            })
          }
          break
        } else if (arr[s].children && arr[s].children.length > 0) { // 递归条件
          this.searchOption(option, arr[s].children)
        } else {
          continue
        }
      }
    },
    append (data) {
      this.buttonType = 'append'
      this.newTreeTitle = ''
      console.log(data)
      this.currentTreeData = data
    },
    remove (data) {
      this.buttonType = 'remove'
        // 先请求后端接口，删除成功后执行
      this.searchOption(data, this.echo.treeData)
      // data = {}
    },
    edit (data) {
      this.buttonType = 'edit'
        // 先请求后端接口，编辑成功后执行
        this.newTreeTitle = data.title
        this.currentTreeData = data
        console.log(data)
         console.log(data.title)
      // this.searchOption(data, this.treeData, 'edit')
    },
    change (value) {
      console.log(value)
      this.displaknowledgeBaseNames = []
       this.displaknowledgeBaseNames = this.echo.treeData.filter(item => {
        if (value.indexOf(item.title) !== -1) return item
      }).map(item => item.title)
    },
    SelectOperationKnowledgeBase (value) {
      this.displaTreeData = []
       // 添加选中的一级节点
      this.displaTreeData = this.echo.treeData.filter(item => {
        if (value.indexOf(item.title) !== -1) return item
      })
    }
  }
}

</script>
<style lang="less" scoped>
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
</style>
