<template>
  <div class="page-header-index-wide">
    <a-card :bordered="false">
      <a-tree :treeData="treeData" >
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
            <a-button type="link" class="but_type" style="right:120px;" @click="()=> edit(item)" >编辑</a-button>
          </a-popconfirm>
          <a-button type="link" class="but_type" @click="(e)=> remove(item)">删除</a-button>
        </template>
      </a-tree>
    </a-card>
  </div>
</template>

<script>
const treeData = [
  {
    title: '0-0',
    key: '0-0',
    scopedSlots: { title: 'custom' },
    children: [
      {
        title: '0-0-0',
        key: '0-0-0',
        scopedSlots: { title: 'custom' },
        children: [
          { title: '0-0-0-0', key: '0-0-0-0', scopedSlots: { title: 'custom' } },
          { title: '0-0-0-1', key: '0-0-0-1', scopedSlots: { title: 'custom' } },
          { title: '0-0-0-2', key: '0-0-0-2', scopedSlots: { title: 'custom' } }
        ]
      },
      {
        title: '0-0-1',
        key: '0-0-1',
        scopedSlots: { title: 'custom' },
        children: [
          { title: '0-0-1-0', key: '0-0-1-0', scopedSlots: { title: 'custom' } },
          { title: '0-0-1-1', key: '0-0-1-1', scopedSlots: { title: 'custom' } },
          { title: '0-0-1-2', key: '0-0-1-2', scopedSlots: { title: 'custom' } }
        ]
      },
      {
        title: '0-0-2',
        key: '0-0-2',
        scopedSlots: { title: 'custom' }
      }
    ]
  },
  {
    title: '0-1',
    key: '0-1',
    scopedSlots: { title: 'custom' },
    children: [
      { title: '0-1-0-0', key: '0-1-0-0', scopedSlots: { title: 'custom' } },
      { title: '0-1-0-1', key: '0-1-0-1', scopedSlots: { title: 'custom' } },
      { title: '0-1-0-2', key: '0-1-0-2', scopedSlots: { title: 'custom' } }
    ]
  },
  {
    title: '0-2',
    key: '0-2',
    scopedSlots: { title: 'custom' }
  }
]

let i
const names = ['模板1', '模板2', '模板3', '模板4']
// const nodeNames = ['目录1', '目录2', '目录3', '目录4']
for (i = 0; i < 4; i++) {
  treeData.push(
      {
        title: names[i],
        key: names[i],
        scopedSlots: { title: 'custom' },
        children: [
          {
            title: i + '目录2-1',
            key: '2-1',
            scopedSlots: { title: 'custom' },
            children: [
              { title: '目录3-1:2-1',
                key: '3-1:2-1',
                children: [
                  { title: '目录4-2:2-1', key: '4-2:2-1', scopedSlots: { title: 'custom' }, children: [] },
                  { title: '目录4-2:2-1', key: '4-2:2-1', scopedSlots: { title: 'custom' }, children: [] }
              ] },
              { title: '目录3-2:2-1', key: '3-2:2-1', scopedSlots: { title: 'custom' }, children: [] }
            ]
          },
          {
            title: i + '目录2-2',
            key: '2-2',
            scopedSlots: { title: 'custom' },
            children: [
              { title: '目录3-1:2-2', key: '3-1:2-2', scopedSlots: { title: 'custom' }, children: [] },
              { title: '目录3-2:2-2', key: '3-2:2-2', scopedSlots: { title: 'custom' }, children: [] }
            ]
          }
        ]
  }
  )
}

export default {
  data () {
    return {
      treeData,
      buttonType: '',
      newTreeTitle: '',
      currentTreeData: ''
    }
  },
  methods: {
    confirm (e) {
       switch (this.buttonType) {
        case 'append':
              // 模拟添加
          const data = this.currentTreeData
          const newChild = { title: this.newTreeTitle,
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
      this.searchOption(data, this.treeData)
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
