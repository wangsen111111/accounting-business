<template>
  <div class="card-list" ref="content">
    <a-modal
      :width="'30%'"
      title="标签编辑"
      :visible="tagIsShow"
      :confirm-loading="confirmLoading"
      @ok="tagHandleOk"
      @cancel="tagHandleCancel">
      <a-form-model
        layout="inline"
        v-bind="{ labelCol: { span: 7 }, wrapperCol: { span: 14 } }">
        <span v-for="tag in currFileTags" :key="tag"><a-tag color="#f50" >{{ tag }}<a-icon type="close" /> </a-tag> </span>
        <hr/>
        <a-row >
          <a-col :span="20">
            <a-select
              mode="tags"
              v-model="currFileTags"
              placeholder="标签选择"
              style="width: 90%;"
              @change="tagHandleChange" >
              <a-select-option v-for="tag in tags" :key="String(tag.tagName)"> {{ tag.tagName }} </a-select-option>
            </a-select>
          </a-col>
        </a-row>
      </a-form-model>
    </a-modal>

    <div style="margin-bottom:20px" >
      <a-card :bordered="false" class="ant-pro-components-tag-select" :bodyStyle="{padding:'0px'}">
        <a-button type="primary" @click="unMountFiles()" >卸载</a-button>
      </a-card>
    </div>

    <a-table
      :scroll="{ y: 'calc(100vh - 346px)' }"
      :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
      :columns="columns"
      :data-source="fileInfo"
      @change="handleTableChange"
      class="components-table-demo-nested">
      <a slot="operation" slot-scope="text">{{ text }}</a>
      <span slot="customTitle"><a-icon type="smile-o" /> Name</span>
      <span slot="tags" slot-scope="tagList">
        <a-tag
          v-for="(tag,index) in tagList"
          :key="index"
          :color="tag === 'loser' ? 'volcano' : tag.length > 5 ? 'geekblue' : 'green'">
          {{ tag.toUpperCase() }}
        </a-tag>
      </span>
      <span slot="action" slot-scope="record" >
        <a @click="toPreview(record)">预览</a>
        <a-divider type="vertical" />
        <a @click="toTagEdit(record)">标签</a>
      </span>
    </a-table>
  </div>
</template>

<script>
import OperatingButton from '@/components/content/OperatingButton/index'
// listByFileIds
import { getFilsByPage, downloadFileByIds, deleteFileByIds, getTags, editFileTag, listByFileIds } from '@/api/file'
import { collection } from '@/api/collection'
export default {
  props: ['fileIds', 'selectNode'],
  data () {
    return {
      paramMap: {
        isMount: null,
        managementType: null,
        tags: [],
        fileName: '',
        projectId: null,
        startEndTime: []
      },
      tags: [],
      projects: [],
      currFileId: null,
      currFileTags: [],
      tagIsShow: false,
      fileInfo: null,
      confirmLoading: false,
      columns: [
        { title: '文件名称', dataIndex: 'fileName', slots: { title: 'customTitle' }, scopedSlots: { customRender: 'name' } },
        { title: '标签', dataIndex: 'tags', scopedSlots: { customRender: 'tags' }, width: '300px' },
        { title: '操作', key: 'action', scopedSlots: { customRender: 'action' } }
      ],
      selectedRowKeys: [],
      selectedRowData: null,
      fileTypes: ['文档', 'word', 'excel', 'pdf', '图片', '工作流程类', '自定义知识库']
    }
  },
  components: {
    OperatingButton
  },
  mounted () {
    // 获取分页数据
    // this.paramMap['projectId'] = this.projectId
    // this.listPage()
    listByFileIds(JSON.stringify({ 'fileIds': this.fileIds })).then(res => {
      this.fileInfo = res.data
    })
    // 获取后台所有标签
    getTags().then(res => {
      if (res.success) { this.tags = res.data }
    }).catch(error => { this.$message.error('获取标签异常', error) })
  },
  methods: {
    handleTableChange (pagination, filters, sorter) {
      console.log(pagination)
    },
    handleSubmit (e) {
      console.log(this.formInline)
    },
    tagHandleChange (value) {
      this.currFileTags = value
    },
    tagHandleOk (e) {
      editFileTag(this.currFileTags, this.currFileId).then(res => {
          listByFileIds(JSON.stringify({ 'fileIds': this.fileIds })).then(res => {
            this.fileInfo = res.data
          })
      })
    getTags().then(res => {
      if (res.success) { this.tags = res.data }
    }).catch(error => { this.$message.error('获取标签异常', error) })
      this.tagIsShow = false
      this.$message.success('标签修改成功')
    },
    tagHandleCancel (e) {
      this.tagIsShow = false
    },
    toPreview (file) {
      this.$router.push({ name: 'FilePreviewAndNotes', query: { fileId: file.fileId } })
    },
    // 进入标签编辑弹框
    toTagEdit (record) {
      console.log(record)
      // 获取当前文件标签列表
      this.currFileId = record.fileId
      this.currFileTags = record.tags
      this.tagIsShow = true
    },
    // 复选框选中记录数组
    onSelectChange (selectedRowKeys, row) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedRowData = row
    },
    // 卸载文件
    unMountFiles () {
      if (this.selectedRowKeys == null || this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择文件'); return
      }
      const jm = this.selectNode['jm']
      const jmMindSelected = this.selectNode['get_selected_node']
      console.log(jmMindSelected)
      debugger
      jmMindSelected.data['fileIds'] = jmMindSelected.data['fileIds'].filter(item => !(this.selectedRowKeys.includes(item)))
      var topic = jmMindSelected.topic
      var iconHtml = topic.match(/<[^>]+>/g, '')
      if (iconHtml != null && iconHtml.length > 0) {
          for (let index = 0; index < iconHtml.length; index++) {
              topic = topic.replace(iconHtml[index], '')
          }
      }
      var fileCountStr = topic.match(/[(][0-9]*[)]/, '')
      var prefix = topic.replace(iconHtml, '')
      if (fileCountStr != null) {
        prefix = topic.replace(fileCountStr, '')
        jm.update_node(jmMindSelected.id, prefix)
      } else {
        jm.update_node(jmMindSelected.id, jmMindSelected.topic)
      }
      // 文件列表刷新
      listByFileIds(JSON.stringify({ 'fileIds': jmMindSelected.data['fileIds'] })).then(res => {
        this.fileInfo = res.data
      })
    },
    // 分页获取数据
    changePage (pageNo, pageSize) {
      this.pagination.pageNo = pageNo
      this.pagination.pageSize = pageSize
      this.listPage()
    },
    // 每页显示数据改变时候获取分页数据
    onShowSizeChange (pageNo, pageSize) {
      this.pagination.pageNo = pageNo
      this.pagination.pageSize = pageSize
      this.listPage()
    },
    // 下载
    downloadFileByIds () {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择下载文件')
      } else {
        downloadFileByIds(JSON.stringify({ 'ids': this.selectedRowKeys })).then(res => {
          var fileName = ''
          if (this.selectedRowKeys.length === 1) {
            fileName = this.selectedRowData[0].fileName + '.' + this.selectedRowData[0].fileType
          } else {
            fileName = new Date().toLocaleString() + '.zip'
          }
          const content = res
          const blob = new Blob([content])
          if ('download' in document.createElement('a')) {
          // 非IE下载
            const elink = document.createElement('a')
            elink.download = fileName
            elink.style.display = 'none'
            elink.href = URL.createObjectURL(blob)
            document.body.appendChild(elink)
            elink.click()
            URL.revokeObjectURL(elink.href) // 释放URL 对象
            document.body.removeChild(elink)
          } else {
          // IE10+下载
            navigator.msSaveBlob(blob, fileName)
          }
        })
      }
    },
    // 删除
    deleteFileByIds () {
      deleteFileByIds(JSON.stringify({ 'ids': this.selectedRowKeys })).then(res => {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请选择收删除文件')
        } else {
            if (res.success) {
              this.$message.success('删除成功')
              // 刷新数据
              getFilsByPage(1, 5).then((res) => {
                this.fileInfo = res.data
              })
            } else {
              this.$message.error('删除失败')
            }
          }
        }).catch(error => {
          this.$message.error('删除异常', error)
        })
    },
    // 收藏
    collectionFileByIds () {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择收藏文件')
      } else {
        var dataArr = []
        this.selectedRowData.forEach(item => {
          var data = { 'collectionType': 0, 'collectionName': item.fileName, 'targetId': item.fileId }
          dataArr.push(data)
        })
        collection(JSON.stringify({ 'collectionScs': dataArr })).then(res => {
          (res.success) ? this.$message.success('收藏成功') : this.$message.error('收藏失败')
        }).catch(error => {
          console.log(error)
          this.$message.error('收藏异常')
        })
      }
    }
  },
  computed: {
    hasSelected () {
      return this.selectedRowKeys.length > 0
    }
  }
}
</script>

<style lang="less" scoped>
@import '~@/components/index.less';
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
.ads {
  position: fixed;
  top: 200px;
}
</style>
