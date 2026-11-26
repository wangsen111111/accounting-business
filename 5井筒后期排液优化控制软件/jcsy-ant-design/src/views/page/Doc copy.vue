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
    <div >
      <a-card :bordered="false" class="ant-pro-components-tag-select" >
        <a-row >
          <a-col :span="19">
            <a-form layout="inline" >
              <a-form-item label="所属项目：">
                <a-select style="width: 130px" v-model="paramMap.projectId" >
                  <a-select-option v-for="project in projects" :key="String(project.projectId)" :value="project.projectId"> {{ project.projectName }} </a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item :wrapper-Col="{ span: 8, offset: 4 }" label="文件名称：">
                <a-input v-model="paramMap.fileName" placeholder="请输入文件名称" />
              </a-form-item>
              <a-form-item label="标签：">
                <a-select
                  mode="tags"
                  v-model="paramMap.tags"
                  placeholder="标签选择"
                  style="width: 200px;" >
                  <a-select-option v-for="tag in tags" :key="String(tag.tagId)"> {{ tag.tagName }} </a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="上传时间：">
                <a-range-picker v-model="paramMap.startEndTime" valueFormat="YYYY-MM-DD" style="width: 200px;">
                  <a-icon slot="suffixIcon" type="smile" />
                </a-range-picker>
              </a-form-item>
              <a-form-item label="管理模式：" >
                <a-radio-group v-model="paramMap.managementType">
                  <a-radio-button value="1"> 上传 </a-radio-button>
                  <a-radio-button value="0"> 外链 </a-radio-button>
                </a-radio-group>
              </a-form-item>
              <a-form-item label="笔记：" >
                <a-radio-group v-model="paramMap.isHasNote">
                  <a-radio-button value="有"> 有 </a-radio-button>
                  <a-radio-button value="无"> 无 </a-radio-button>
                </a-radio-group>
              </a-form-item>
              <!-- <a-form-item label="是否挂载：" >
                <a-radio-group v-model="paramMap.isMount">
                  <a-radio-button value="1"> 是 </a-radio-button>
                  <a-radio-button value="0"> 否 </a-radio-button>
                </a-radio-group>
              </a-form-item> -->

            </a-form>
          </a-col>
          <a-col :span="5" style="text-align: right;">
            <a-button type="primary" @click="reset()" >重置</a-button>
            <a-button type="primary" @click="conditionListPageFile()" style="margin-left:5px">查询</a-button>
            <a-button type="primary" @click="downloadFileByIds()" style="margin-left:5px">下载</a-button>
            <a-button type="primary" @click="deleteFileByIds()" style="margin-left:5px">删除</a-button>
            <a-button type="primary" @click="collectionFileByIds()" style="margin-left:5px">收藏</a-button>
          </a-col>
        </a-row>
        <a-table
          style="margin-top:10px"
          :scroll="{ y: 'calc(100vh - 280px)' }"
          :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
          :columns="columns"
          :data-source="fileInfo"
          :pagination="pagination"
          @change="handleTableChange"
          class="components-table-demo-nested">
          <a slot="operation" slot-scope="text">{{ text }}</a>
          <span slot="boolvalue" slot-scope="text">{{ text | BoolFilter }}</span>
          <span slot="customTitle"><a-icon type="smile-o" /> Name</span>
          <span slot="tags" slot-scope="tagList">
            <a-tag
              v-for="tag in tagList"
              :key="tag"
              :color="tag === 'loser' ? 'volcano' : tag.length > 5 ? 'geekblue' : 'green'">
              {{ tag.toUpperCase() }}
            </a-tag>
          </span>
          <span slot="action" slot-scope="record" >
            <a @click="toPreview(record)">预览</a>
            <a-divider type="vertical" />
            <a @click="toTagEdit(record)">标签</a>
            <a-divider type="vertical" />
            <a @click="exportNote(record)">导出笔记</a>
          </span>
          <a-table
            slot-scope="fileData"
            slot="expandedRowRender"
            :columns="innerColumns"
            :data-source="fileData.infos"
            :pagination="false" >
            <span slot="status" > <a-badge status="success" />Finished </span>
            <span slot="operation" class="table-operation" slot-scope="text,record">
              <span slot="action" >
                <a @click="toMindMap(record.jsmindId)">跳转思维导图</a>
                <a-divider type="vertical" />
                <a @click="uninstallFile(record.jsmindId,record.key ,fileData.fileId)"> 卸载</a>
              </span>
            </span>
          </a-table>
        </a-table>
      </a-card>
    </div>
    <!-- style="height: calc(100vh - 168px)"
    :scroll="{ y: 'calc(100vh - 346px)' }"
    -->

  </div>
</template>

<script>
import OperatingButton from '@/components/content/OperatingButton/index'
import { getFilsByPage, downloadFileByIds, deleteFileByIds, getTags, editFileTag } from '@/api/file'
import { collection } from '@/api/collection'
import { getProjects } from '@/api/project'
import { uninstallFile, getMindMapById } from '@/api/mindMap'
import { exportNoteByFile } from '@/api/fileNote'
export default {
  data () {
    return {
      paramMap: {
        isHasNote: null,
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
        { title: '文件类型', dataIndex: 'fileType' },
        { title: '所属项目数', dataIndex: 'projectNumber' },
        { title: '标签', dataIndex: 'tags', scopedSlots: { customRender: 'tags' } },
        { title: '管理模式', dataIndex: 'managementType' },
        { title: '笔记', dataIndex: 'isHasNote' },
        // { title: '是否挂载', dataIndex: 'isMount' },
        // { title: '是否挂载', dataIndex: 'mountLoc', scopedSlots: { customRender: 'boolvalue' } },
        { title: '上传时间', dataIndex: 'uploadTime' },
        { title: '操作', key: 'action', scopedSlots: { customRender: 'action' } }
      ],
      innerColumns: [
        { title: '所属项目', dataIndex: 'projectName' },
        { title: '思维导图', dataIndex: 'mindMapName' },
        { title: '所在节点', dataIndex: 'nodeName' },
        { title: '操作', dataIndex: 'operation', key: 'operation', scopedSlots: { customRender: 'operation' } }
      ],
      selectedRowKeys: [],
      selectedRowData: null,
      fileTypes: ['文档', 'word', 'excel', 'pdf', '图片', '工作流程类', '自定义知识库'],
      pagination: {
        pageNo: 1,
        pageSize: 20, // 默认每页显示数量
        showSizeChanger: true, // 显示可改变每页数量
        pageSizeOptions: ['20', '50', '100'], // 每页数量选项
        showTotal: total => `共 ${this.pagination.total} 条数据`, // 显示总数
        onShowSizeChange: this.onShowSizeChange, // 改变每页数量时更新显示
        onChange: this.changePage, // 点击页码事件
        total: 0 // 总条数
        // size: 'small' // 迷你版
       }
    }
  },
   filters: {
    BoolFilter (value) {
      console.log(value)
      var jsminds = []
      value.forEach(item => { jsminds = jsminds.concat(item.jsmindIds) })
      if (jsminds.length > 0) {
        return '已挂载'
      } else {
         return '未挂载'
      }
    }
  },
  components: {
    OperatingButton
  },
  mounted () {
    // 获取分页数据
    this.listPage()
    // 获取后台所有标签
    getTags().then(res => {
      if (res.success) { this.tags = res.data }
    }).catch(error => { this.$message.error('获取标签异常', error) })
    // 获取所有项目
    getProjects().then(res => {
      if (res.success) {
        this.projects = res.data
      }
    })
  },
  methods: {
    listPage () {
      getFilsByPage(this.pagination.pageNo, this.pagination.pageSize, this.paramMap).then((res) => {
        this.fileInfo = res.data.data
        this.pagination.total = res.data.total
      })
    },
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
      const vm = this
      new Promise(function (resolve, reject) {
          editFileTag(vm.currFileTags, vm.currFileId).then(res => { vm.listPage() })
          getTags().then(res => { if (res.success) { vm.tags = res.data } }).catch(error => { vm.$message.error('获取标签异常', error) })
          resolve()
      }).then(() => {
        vm.tagIsShow = false
        vm.$message.success('标签修改成功')
      })
    },
    tagHandleCancel (e) {
      this.tagIsShow = false
    },
    onSearch (value) {
      console.log(value)
    },
    handleChange (value) {
      console.log(`selected ${value}`)
    },
    toPreview (file) {
      this.$router.push({ name: 'FilePreviewAndNotes', query: { fileId: file.fileId } })
    },
    // 重置
    reset () {
      this.paramMap = {
        isMount: null,
        managementType: null,
        tags: [],
        fileName: '',
        projectId: null,
        startEndTime: []
      }
    },
    toMindMap (jsmindId) {
      // 获取项目id
      getMindMapById(jsmindId).then(res => {
        if (res.success) {
          this.$router.push({ name: 'MindMap', query: { projectId: res.data.projectId, jsmindId: res.data.id } })
        } else {
          this.$message.error('获取项目id失败')
        }
      })
    },
    // 条件查询
    conditionListPageFile () {
      this.pagination.pageNo = 1
      this.listPage()
    },
    exportNote (file) {
      exportNoteByFile(file).then(res => {
          var fileName = file.fileName + '-文件笔记.doc'
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
    },
    // 进入标签编辑弹框
    toTagEdit (record) {
      console.log(record)
      // 获取当前文件标签列表
      this.currFileId = record.fileId
      this.currFileTags = record.tags
      this.tagIsShow = true
    },
    start () {
      this.loading = true
      setTimeout(() => {
        this.loading = false
        this.selectedRowKeys = []
      }, 1000)
    },
    // 复选框选中记录数组
    onSelectChange (selectedRowKeys, row) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedRowData = row
      console.log(row)
    },
    // 卸载文件
    uninstallFile (jsmindId, nodeId, fileId) {
      console.log(nodeId, fileId)
      uninstallFile(jsmindId, nodeId, fileId).then(res => {
        res.success ? this.$message.success('卸载成功') : this.$message.error('卸载失败')
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
      deleteFileByIds({ 'ids': this.selectedRowKeys }).then(res => {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请选择收删除文件')
        } else {
            if (res.success) {
              this.$message.success('删除成功')
              // 刷新数据
              this.listPage()
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
        // JSON.stringify({ 'collectionScs': dataArr })
        collection({ 'collectionScs': dataArr }).then(res => {
          (res.success) ? this.$message.success('收藏成功') : this.$message.error('收藏失败')
        }).catch(error => {
          this.$message.error('收藏异常', error)
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
