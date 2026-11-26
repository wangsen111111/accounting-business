<template>
  <div class="card-list" ref="content">
    <div style="margin-bottom:20px;" >
      <a-card :bordered="false" class="ant-pro-components-tag-select" >
        <a-row >
          <a-col :span="16">
            <a-form layout="inline" >
              <a-form-item :wrapper-Col="{ span: 8, offset: 4 }" label="项目名称：">
                <a-input v-model="paramMap.projectName" placeholder="请输入项目名称" />
              </a-form-item>
              <a-form-item label="负责人：">
                <a-input v-model="paramMap.createUser" placeholder="请输入负责人" />
              </a-form-item>
              <a-form-item label="起止时间：">
                <a-range-picker v-model="paramMap.startEndTime" valueFormat="YYYY-MM-DD">
                  <a-icon slot="suffixIcon" type="smile" />
                </a-range-picker>
              </a-form-item>
              <a-form-item label="进度范围：">
                <a-tooltip :trigger="['focus']" placement="topLeft" overlay-class-name="numeric-input">
                  <span v-if="paramMap.startProgressOf" slot="title" class="numeric-input-title">
                    {{ paramMap.startProgressOf !== '-' ? paramMap.startProgressOf : '-' }}
                  </span>
                  <template v-else slot="title">输入最小进度</template>
                  <a-input
                    :value="paramMap.startProgressOf"
                    :max-length="25"
                    style="width: 50px"
                    @change="onChangeStartProgressOf"/>
                </a-tooltip>
                -
                <a-tooltip :trigger="['focus']" placement="topLeft" overlay-class-name="numeric-input">
                  <span v-if="paramMap.endProgressOf" slot="title" class="numeric-input-title">
                    {{ paramMap.endProgressOf !== '-' ? paramMap.endProgressOf : '-' }}
                  </span>
                  <template v-else slot="title">输入最大进度</template>
                  <a-input
                    :value="paramMap.endProgressOf"
                    :max-length="25"
                    style="width: 50px"
                    @change="onChangeEndProgressOf"/>
                </a-tooltip>
              </a-form-item>
            </a-form>
          </a-col>
          <a-col :span="8" style="text-align: right;">
            <a-button type="primary" @click="reset()" style="margin-left:10px">重置</a-button>
            <a-button type="primary" @click="listPageProject()" style="margin-left:10px">查询</a-button>
            <a-button type="primary" @click="newBuildProject()" style="margin-left:10px">新建</a-button>
            <a-button type="primary" @click="downloadByIds()" :disabled="disabledDownload" style="margin-left:10px"><a-spin :spinning="loading">下载</a-spin>  </a-button>
            <a-button type="primary" @click="importProjects()" style="margin-left:10px">导入</a-button>
            <a-button type="primary" @click="deleteByIds()" style="margin-left:10px">删除</a-button>
            <a-button type="primary" @click="collectionProjectByIds()" style="margin-left:10px">收藏</a-button>
          </a-col>
        </a-row>
        <a-table
          style="margin-top:10px"
          :scroll="{ y: 'calc(100vh - 280px)' }"
          :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
          :columns="columns"
          :data-source="projectInfo"
          :pagination="pagination"
          @change="handleTableChange"
          class="components-table-demo-nested">
          <span slot="action" slot-scope="record" >
            <a @click="toDetail(record)">明细</a>
            <a-divider type="vertical" />
            <a @click="toEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a @click="toMindMap(record)">思维导图</a>
            <a-divider type="vertical" />
            <a @click="toPreview(record)">文件预览</a>
          </span>
        </a-table>
      </a-card>
    </div>

    <a-modal
      :footer="null"
      :width="'30%'"
      title="项目导入"
      :visible="importProjectVisible"
      :confirm-loading="confirmLoading"
      @ok="importProjectHandleOk"
      @cancel="importProjectHandleCancel">
      <a-form-model
        :model="importProjectForm"
        layout="horizontal"
        v-bind="{ labelCol: { span: 4 }, wrapperCol: { span: 14 } }">
        <a-card style="width: 100%;">
          <span>模式： </span>
          <a-radio-group name="radioGroup" :default-value="redioType" @change="radioGroupChange">
            <a-radio :value="0"> 外链 </a-radio>
            <a-radio :value="1"> 上传 </a-radio>
          </a-radio-group>
          <br/>
          <span>方式： </span>
          <a-radio-group name="radioGroup" v-model="saveType" >
            <a-radio :value="0"> 单思维导图 </a-radio>
            <a-radio :value="1"> 多思维导图 </a-radio>
          </a-radio-group>
          <div v-if="redioType">
            <a-upload-dragger
              name="file"
              :file-list="fileList"
              :multiple="true"
              :showUploadList="showUploadList"
              :before-upload="beforeUpload">
              <p class="ant-upload-drag-icon"> <a-icon type="inbox" /> </p>
              <p class="ant-upload-text"> 单击或拖动文件到此区域上载(zip压缩文件) </p>
            </a-upload-dragger>
            <a-button
              block
              type="primary"
              :disabled="fileList.length === 0"
              :loading="uploading"
              @click="handleUpload">
              {{ uploading ? '上传中' : '开始导入' }}
            </a-button>
          </div>
          <div v-else>
            <a-textarea
              style="background: #FFFFFF;font-color: #000000"
              v-model="projectPaths"
              placeholder="请输入本地文件路径或目录，多条换行输入..."
              :auto-size="{ minRows: 2, maxRows: 6 }" />
            <a-button block type="primary" @click="importChain"> 开始导入 </a-button>
          </div>
        </a-card>
      </a-form-model>
    </a-modal>

    <!-- 编辑表单 -->
    <a-modal
      :footer="null"
      :width="'30%'"
      title="项目编辑"
      :visible="editVisible"
      :confirm-loading="confirmLoading"
      @ok="editHandleOk"
      @cancel="editHandleCancel">
      <a-form-model
        ref="ruleForm"
        :rules="rules"
        :model="projectForm"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 14 }">
        <a-form-model-item label="项目名称" prop="projectName">
          <a-input v-model="projectForm.projectName" />
        </a-form-model-item>
        <a-form-model-item label="项目进度" prop="progressOf">
          <a-input v-model="projectForm.progressOf"/>
        </a-form-model-item>
        <a-form-model-item label="项目周期" prop="startStopTime">
          <a-range-picker valueFormat="YYYY-MM-DD" v-model="projectForm.startStopTime" :placeholder="['开始时间', '结束时间']" style="width: 100%" > </a-range-picker>
        </a-form-model-item>
        <a-form-model-item label="项目介绍" prop="note">
          <a-textarea
            style="background: #FFFFFF;font-color: #000000"
            v-model="projectForm.note"
            :auto-size="{ minRows: 5, maxRows: 5 }" />
        </a-form-model-item>
        <a-form-model-item :wrapper-col="{ span: 14, offset: 8 }">
          <a-button type="primary" @click="saveProject"> 保存 </a-button>
          <a-button style="margin-left: 10px;" @click="editHandleCancel"> 取消 </a-button>
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
import OperatingButton from '@/components/content/OperatingButton/index'
import { getProjectsByPage, importChain, importUpload, deleteProjectByIds, downloadByIds, update } from '@/api/project'
import { collection } from '@/api/collection'

export default {
  data () {
    return {
      projectForm: {
        projectId: null,
        projectName: null,
        progressOf: null,
        note: null,
        startTime: null,
        endTime: null,
        startStopTime: null
      },
      rules: {
        projectName: [
          { required: true, message: '请输入项目名称', trigger: 'change' },
          { max: 20, message: '项目名称最长20', trigger: 'change' }
        ],
        startStopTime: { required: true, message: '请输入项目周期', trigger: 'change' },
        progressOf: [
          { required: true, message: '请输入项目进度', trigger: 'change' },
          { validator: this.handleConfirmProgressOf, trigger: 'change' }
        ],
        note: [
          { required: true, message: '请输入项目简介', trigger: 'change' },
          { max: 1000, message: '简介最长1000字符', trigger: 'change' }
        ]
      },
      editVisible: false,
      disabledDownload: false,
      loading: false,
      fileList: [],
      showUploadList: true,
      uploading: false,
      redioType: 0,
      saveType: 0,
      projectPaths: '',
      importProjectForm: {},
      importProjectVisible: false,
      tags: [],
      currFileId: null,
      currFileTags: [],
      tagIsShow: false,
      projectInfo: null,
      confirmLoading: false,
      columns: [
        { title: '项目名称', dataIndex: 'projectName', slots: { title: 'customTitle' }, scopedSlots: { customRender: 'name' } },
        { title: '项目进度', dataIndex: 'progressOf' },
        { title: '项目周期', dataIndex: 'startStopTime' },
        { title: '负责人', dataIndex: 'createUser' },
        { title: '思维导图数', dataIndex: 'jsmindCount' },
        { title: '文件数', dataIndex: 'fileCount' },
        { title: '创建时间', dataIndex: 'createTime' },
        { title: '操作', key: 'action', scopedSlots: { customRender: 'action' }, width: '280px' }
      ],
      selectedRowKeys: [],
      selectedRowData: null,
      pagination: {
        pageNo: 1,
        pageSize: 10, // 默认每页显示数量
        showSizeChanger: true, // 显示可改变每页数量
        pageSizeOptions: ['10', '20', '50', '100'], // 每页数量选项
        showTotal: total => `共 ${this.pagination.total} 条数据`, // 显示总数
        onShowSizeChange: this.onShowSizeChange, // 改变每页数量时更新显示
        onChange: this.changePage, // 点击页码事件
        total: 0 // 总条数
        // size: 'small' // 迷你版
       },
      paramMap: {
         startProgressOf: null,
         endProgressOf: null,
         createUser: '',
         projectName: '',
         startEndTime: []
      }

    }
  },
  components: {
    OperatingButton
  },
  mounted () {
    // 获取分页数据
    this.listPage()
  },
  methods: {
    importChain () {
      importChain(JSON.stringify({ 'projectPaths': this.projectPaths }), this.saveType).then(res => {
        if (res.success) {
           this.$message.success('导入成功')
            this.importProjectVisible = false
            this.listPage()
        } else {
          this.$message.error('导入失败')
        }
      })
    },
    importProjectHandleCancel (e) {
      this.importProjectVisible = false
    },
    importProjectHandleOk (e) {
      this.importProjectVisible = false
    },
    importProjects () {
      this.importProjectVisible = true
    },
    // 时间选择
    handlePanelChange (value) {
      if (value.lenth === 0) {
        this.projectForm.startTime = null
        this.projectForm.endTime = null
      } else if (value.lenth === 2) {
        this.projectForm.startTime = value[0]
        this.projectForm.endTime = value[0]
      }
    },
        // 单选框改变事件
    radioGroupChange (obj) {
      this.redioType = obj.target.value
    },
    beforeUpload (file) {
      this.fileList = [...this.fileList, file]
      return false
    },
    saveProject () {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          this.projectForm.startTime = this.projectForm.startStopTime[0]
          this.projectForm.endTime = this.projectForm.startStopTime[1]
          // 提交数据修改项目
          update(this.projectForm).then(res => {
            // res.success ? this.$message.success('修改成功') : this.$message.error('修改失败')
            if (res.success) {
              this.$message.success('修改成功')
                          // 刷新列表
              this.loading = true
              getProjectsByPage(this.pagination.pageNo, this.pagination.pageSize, this.paramMap).then((res) => {
                this.projectInfo = res.data.data
                this.pagination.total = res.data.total
                this.loading = false
                this.importProjectVisible = false
                this.editVisible = false
              })
            } else { this.$message.error('修改失败') }
          })
        } else {
          return false
        }
      })
    },
    handleConfirmProgressOf  (rule, value, callback) {
      var regPos = /^\d+(\.\d+)?$/ // 非负浮点数
      var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/ // 负浮点数
      if (!(regPos.test(value) || regNeg.test(value))) {
        // eslint-disable-next-line standard/no-callback-literal
        callback('请输入数值！')
      }
      if (!(Number(value) >= 0 && Number(value) <= 100)) {
        // eslint-disable-next-line standard/no-callback-literal
        callback('请输入0-100！')
      }
      // Note: 必须总是返回一个 callback，否则 validateFieldsAndScroll 无法响应
      callback()
    },
    // 编辑项目
    toEdit (project) {
      //  this.projectForm = Object.assign(this.projectForm, project) // 对象属性拷贝，该方法返回第一个参数的引用
      if ((project.startStopTime !== null && project.startStopTime !== '')) {
        const dataList = project.startStopTime.split('至')
        this.projectForm.startStopTime = dataList
      }
      this.projectForm.progressOf = project.progressOf ? Number(project.progressOf) : ''
      this.projectForm.projectName = project.projectName
      this.projectForm.note = project.note
      this.projectForm.startTime = project.startTime
      this.projectForm.endTime = project.endTime
      this.projectForm.projectId = project.projectId
      this.editVisible = true
    },
    editHandleOk () {
      this.editVisible = false
    },
    editHandleCancel () {
      this.editVisible = false
    },
    // 新建项目
    newBuildProject () {
      this.$router.push({ name: 'CreateProject', params: { routeName: 'project' } })
    },
    // 删除
    deleteByIds () {
      deleteProjectByIds(JSON.stringify({ 'ids': this.selectedRowKeys })).then(res => {
        // 刷新页面
        this.listPageProject()
      })
    },
    // 下载
    downloadByIds () {
      if (this.selectedRowData.length === 0) {
        this.$message.warning('请选择下载文件')
      } else {
        this.loading = true
        this.disabledDownload = true
        this.selectedRowData.forEach(project => {
          // downloadByIds(JSON.stringify({ 'ids': this.selectedRowKeys })).then(res => {
          downloadByIds(project.projectId).then(res => {
            this.loading = false
            this.disabledDownload = false
            // var fileName = this.selectedRowKeys.length === 1 ? this.selectedRowData[0].projectName + '.zip' : '项目压缩包.zip'
            var fileName = project.projectName + '.zip'
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
        })
      }
    },
    // 文件上传
    handleUpload () {
      this.showUploadList = true
      const { fileList } = this
      const formData = new FormData()

      fileList.forEach(file => {
        formData.append('files', file)
      })
      this.uploading = true

      importUpload(formData, this.saveType).then(res => {
        if (res.data.ok != null && res.data.ok.length > 0) {

        }
        const success = `完成上传，【${res.data.ok}】上传成功，【${res.data.no}】已存在`
        if (res.success) {
          this.$message.success(success)
          // 刷新页面
          this.listPageProject()
        }
      }).catch(error => {
          this.$message.error('上传异常', error)
      }).finally(o => {
        this.fileList = []
        this.uploading = false
        this.showUploadList = false
      })
    },
    // 收藏
    collectionProjectByIds () {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择收藏项目')
      } else {
        var dataArr = []
        this.selectedRowData.forEach(item => {
          var data = { 'collectionType': 2, 'collectionName': item.projectName, 'targetId': item.projectId }
          dataArr.push(data)
        })
        collection(JSON.stringify({ 'collectionScs': dataArr })).then(res => {
          (res.success) ? this.$message.success('收藏成功') : this.$message.error('收藏失败')
        }).catch(error => {
          this.$message.error('收藏异常', error)
        })
      }
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
    // 获取分页数据
    listPage () {
        this.loading = true
        getProjectsByPage(this.pagination.pageNo, this.pagination.pageSize, this.paramMap).then((res) => {
          this.projectInfo = res.data.data
          this.pagination.total = res.data.total
          this.loading = false
          this.importProjectVisible = false
        })
    },
    listPageProject () {
      this.listPage()
    },
    reset () {
      this.paramMap = {
         startProgressOf: null,
         endProgressOf: null,
         createUser: '',
         projectName: '',
         startEndTime: []
      }
     this.listPage()
    },
    onChangeStartProgressOf (e) {
      const { value } = e.target
      const reg = /^-?[0-9]*(\.[0-9]*)?$/
      if ((!isNaN(value) && reg.test(value)) || value === '' || value === '-') {
        this.paramMap.startProgressOf = value
      }
    },
    onChangeEndProgressOf (e) {
      const { value } = e.target
      const reg = /^-?[0-9]*(\.[0-9]*)?$/
      if ((!isNaN(value) && reg.test(value)) || value === '' || value === '-') {
        this.paramMap.endProgressOf = value
      }
    },
    handleTableChange (pagination, filters, sorter) {
      console.log(pagination)
    },
    // 跳转项目明细页面
    toDetail (project) {
      this.$router.push({ name: 'ProjectDetail', query: { projectId: project.projectId } })
    },
    // 跳转思维导图页面
    toMindMap (data) {
      this.$router.push({ name: 'MindMap', query: { projectId: data.projectId } })
    },
    // 跳转预览页面
    toPreview (data) {
      this.$router.push({ name: 'FilePreviewAndNotes', query: { projectId: data.projectId } })
    },
    // 进入标签编辑弹框
    toTagEdit (record) {
      // 获取当前文件标签列表
      this.currFileId = record.fileId
      this.currFileTags = record.tags
      this.tagIsShow = true
    },
    // 复选框选中记录数组
    onSelectChange (selectedRowKeys, row) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedRowData = row
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
