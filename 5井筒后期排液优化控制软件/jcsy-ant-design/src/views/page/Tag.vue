<template>
  <div>
    <a-card :bordered="false">
      <div>
        <a-popconfirm
          placement="bottom"
          ok-text="Yes"
          cancel-text="No"
          @confirm="add(selectKey, addTitle)"
        >
          <template slot="title">
            <span>输入新标签</span>
            <a-input v-model="addTitle"></a-input>
          </template>
          <a-button type="primary">添加</a-button>
        </a-popconfirm>
        <template v-if="selectKey !== undefined">
          <a-popconfirm
            placement="bottom"
            ok-text="Yes"
            cancel-text="No"
            @confirm="edit(editTag, editTitle)"
          >
            <template slot="title">
              <span>修改标签</span>
              <a-input v-model="editTitle"></a-input>
            </template>
            <a-button type="primary" @click="editBefore(selectKey)" ghost>修改</a-button>
          </a-popconfirm>
          <a-popconfirm
            placement="bottom"
            ok-text="Yes"
            cancel-text="No"
            @confirm="remove(selectKey)"
          >
            <template slot="title">
              <span>确定删除？</span>
            </template>
            <a-button type="danger" ghost>删除</a-button>
          </a-popconfirm>
        </template>
      </div>
      <a-tree
        class="draggable-tree"
        :default-expanded-keys="expandedKeys"
        draggable
        :treeData="data"
        @select="select"
      />
      <!-- @dragenter="onDragEnter"
        @drop="onDrop" -->
    </a-card>
  </div>
</template>

<script>
const x = 3
const y = 2
const z = 1
const data = []

const generateData = (_level, _preKey, _tns) => {
  let preKey = _preKey || ''
  if (preKey !== '') {
    preKey += '-'
  }
  const tns = _tns || data

  const children = []
  for (let i = 0; i < x; i++) {
    const key = `${preKey}${i}`
    tns.push({ title: key, key })
    if (i < y) {
      children.push(key)
    }
  }
  if (_level < 0) {
    return tns
  }
  const level = _level - 1
  children.forEach((key, index) => {
    tns[index].children = []
    return generateData(level, key, tns[index].children)
  })
}
generateData(z)
export default {
  data () {
    return {
      data,
      expandedKeys: ['0-0', '0-0-0', '0-0-0-0'],
      selectKey: undefined,
      addTitle: null,
      editTitle: null,
      editTag: null,
      selectTag: null
      // stop: false
    }
  },
  methods: {
    onDragEnter (info) {
      console.log(info)
      // expandedKeys 需要受控时设置
      // this.expandedKeys = info.expandedKeys
    },
    onDrop (info) {
      const dropKey = info.node.eventKey
      const dragKey = info.dragNode.eventKey
      const dropPos = info.node.pos.split('-')
      const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1])
      const loop = (data, key, callback) => {
        data.forEach((item, index, arr) => {
          if (item.key === key) {
            return callback(item, index, arr)
          }
          if (item.children) {
            return loop(item.children, key, callback)
          }
        })
      }
      const data = [...this.data]

      // Find dragObject
      let dragObj
      loop(data, dragKey, (item, index, arr) => {
        arr.splice(index, 1)
        dragObj = item
      })
      if (!info.dropToGap) {
        // Drop on the content
        loop(data, dropKey, (item) => {
          item.children = item.children || []
          // where to insert 示例添加到尾部，可以是随意位置
          item.children.push(dragObj)
        })
      } else if (
        (info.node.children || []).length > 0 && // Has children
        info.node.expanded && // Is expanded
        dropPosition === 1 // On the bottom gap
      ) {
        loop(data, dropKey, (item) => {
          item.children = item.children || []
          // where to insert 示例添加到尾部，可以是随意位置
          item.children.unshift(dragObj)
        })
      } else {
        let ar
        let i
        loop(data, dropKey, (item, index, arr) => {
          ar = arr
          i = index
        })
        if (dropPosition === -1) {
          ar.splice(i, 0, dragObj)
        } else {
          ar.splice(i + 1, 0, dragObj)
        }
      }
      this.data = data
    },
    select (keys) {
      this.selectKey = keys[0]
    },
    add (key, addTitle) {
      if (key === undefined) {
        this.$set(this.data, this.data.length, {
          key: '-' + this.data.length,
          title: addTitle
        })
      } else {
        this.getTagByKey(this.data, key)
        const parentTag = this.selectTag
        if (!parentTag.children) {
          this.$set(parentTag, 'children', [])
        }
        this.$set(parentTag.children, parentTag.children.length, {
          key: parentTag.key + '-' + parentTag.children.length,
          title: addTitle
        })
      }
    },
    editBefore (key) {
      this.getTagByKey(this.data, key)
      const editTag = this.selectTag
      this.editTag = editTag
      this.editTitle = editTag.title
    },
    edit (editTag, editTitle) {
      editTag.title = editTitle
    },
    remove (key) {
      this.getTagByKey(data, key, true)
    },
    getTagByKey (data, key, remove, last) {
      let result = null
      if (data instanceof Array) {
        for (const object of data) {
          result = this.getTagByKey(object, key, remove, data)
          if (result === -1) {
            return -1
          }
        }
      } else {
        if (data.key === key) {
          if (remove) {
            const lastIndex = last.findIndex((obj) => obj.key === data.key)
            this.$delete(last, lastIndex)
          }
          this.selectTag = data
          return -1
        } else {
          if (data.children !== undefined) {
            result = this.getTagByKey(data.children, key, remove, last)
            if (result === -1) {
              return -1
            }
          }
        }
      }
    }
  }
}
</script>
