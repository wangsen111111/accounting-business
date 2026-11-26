<template>
  <div>
    <a-row :gutter="16">
      <a-col :span="6">
        <a-button type="primary" block> 文件列表 </a-button>
        <a-card style="width: 100%;height:calc(100vh - 97px)">
          <a-row>
            <a-col :span="4"><a-button type="link" disabled><span style="font-size:18px;color:	#000000"> 项目： </span> </a-button></a-col>
            <a-col :span="20">
              <a-select
                show-search
                placeholder="切换项目"
                option-filter-prop="children"
                @change="changeSelectProject"
                v-model="selectProject"
                style="width: 100%">
                <a-select-option v-for="project in projects" :key="project.projectNmae" :value="project.projectId"> {{ project.projectName }} </a-select-option>
              </a-select>
            </a-col>
          </a-row>
          <a-row style="margin-top:10px">
            <a-col :span="4"><a-button type="link" disabled> <span style="font-size:18px;color:	#000000"> 搜索： </span> </a-button></a-col>
            <a-col :span="16">
              <a-input-search placeholder="文件检索" enter-button @search="onSearch" v-model="parmkey" style="width:100%" />
            </a-col>
            <a-col :span="4" style="text-align: right;">
              <a-button @click="reset()">重置</a-button>
            </a-col>
          </a-row>
          <a-list
            style="margin-top:10px"
            size="small"
            bordered
            :data-source="dbFileInfo"
            item-layout="vertical"
            :pagination="pagination">
            <a-list-item slot="renderItem" slot-scope="item">
              <a @click="preview(item)" :class="item.fileId == fileNote.fileId ? 'clickstyle' : ''">{{ item.fileName }}</a>
            </a-list-item>
          </a-list>
        </a-card>
      </a-col>
      <a-col :span="12">
        <!-- <div style="width: calc(50vw - 20px);height:calc(100vh - 97px);float:left;margin-left:10px;margin-right:10px"> -->
        <a-button type="primary" block> 预览窗口 </a-button>
        <!-- <a-card style="width: 100%;height:calc(100vh - 97px)"></a-card> -->
        <a-card class="view-container" style="width: 100%;height:calc(100vh - 152px)">
          <!-- word预览 -->
          <div v-if="['docx','DOCX', 'doc', 'DOC ', 'txt'].includes(previewType)" style="padding:12px">
            <a-spin :spinning="spinning" :delay="delay" tip="Loading...">
              <div v-html="wordInfo"> </div>
            </a-spin>
          </div>
          <!-- pdf预览 -->
          <iframe
            v-if="previewType.toLowerCase() === 'pdf'"
            type="application/pdf"
            :src="serviceUrl"
            style="width:100%;height: calc(100vh - 180px)"
          ></iframe>
          <!-- 图片 -->
          <img :src="serviceUrl" style="width:100%;height: calc(100vh - 210px)" v-if="previewType === 'jpg' || previewType === 'png'"/>
          <!-- ppt -->
          <div v-if="['ppt', 'pptx', 'PPT', 'PPTX'].includes(previewType)">
            <a-spin :spinning="serviceUrl == null" :delay="delay" tip="Loading...">
              <img :src="url" style="width:100%" v-for="(url,index) in serviceUrl" :key="index"/>
            </a-spin>
          </div>
          <!-- excel -->
          <div v-if="['xlsx', 'xls', 'XLSX', 'XLS'].includes(previewType)" style="padding:12px">
            <a-spin :spinning="spinning" :delay="delay" tip="Loading...">
              <div v-html="wordInfo">
              </div>
            </a-spin>
          </div>
        </a-card>
        <a-card :bodyStyle="{padding:'0px'}">
          <a-row>
            <a-col :span="1"></a-col>
            <a-col :span="18"> <a-slider style="margin-left:5px" :marks="marks" v-model="progressOf" @afterChange="progressOfAfterChange"/> </a-col>
            <a-col :span="4" style="text-align: right;"><a-rate v-model="score" allow-half @change="scoreChange" /></a-col>
            <a-col :span="1"></a-col>
          </a-row>
        </a-card>
      </a-col>
      <a-col :span="6">
        <div style="width: calc(25vw - 0px);">
          <a-button type="primary" block> 笔记 </a-button>
          <a-form layout="inline" >
            <a-form-item label="笔记选择：">
              <a-select style="width: 150px" v-model="selectFileNote" >
                <a-select-option key="0" > 添加新笔记 </a-select-option>
                <a-select-option v-for="item in fileNote" :key="item.id" :value="item.id" :title="item.createUser + item.createTime"> {{ item.createUser }} - {{ item.createTime }} </a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="关键字：">
              <a-input placeholder="关键字" v-model="keyword" style="width:100px"/>
              <a-button type="primary" @click="addKeyWord(keyword)">添加 </a-button>
            </a-form-item>
          </a-form>
          <a-card style="width: 100%;height:calc(100vh - 136px)">
            <a-card :bodyStyle="{ 'padding':'0px' }">
              <a-tag closable v-for="(item,index) in currSelectFileNote.keywords" :key="index" @close="closeTag(item)">{{ item }}</a-tag>
            </a-card>
            <a-textarea
              style="background: #FFFFFF;font-color: #000000;margin-top:10px"
              v-model="currSelectFileNote.content"
              placeholder=""
              :auto-size="{ minRows: 30, maxRows: 35 }" />
            <a-row>
              <a-col :span="12"><a-button type="primary" block style="margin-top:10px" @click="updataNote"> 提交笔记 </a-button></a-col>
              <a-col :span="12"><a-button type="primary" block style="margin-top:10px" @click="removeNote" v-if="selectFileNote !== '0'"> 删除笔记 </a-button></a-col>
            </a-row>
          </a-card>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import infiniteScroll from 'vue-infinite-scroll'
import { RecycleScroller } from 'vue-virtual-scroller'
import 'vue-virtual-scroller/dist/vue-virtual-scroller.css'
import { getPageFileByProjectId, getFileByFileId, update } from '@/api/file'
import { getFileNoteByFileId, updataFileNoteById, removeFileNoteById } from '@/api/fileNote'
import { downloadFile } from '@/api/knowledgeBase'
import { getHtmlByWord, getServiceUrl, getPPTServiceUrl } from '@/api/preview'
import { getProjects } from '@/api/project'
import XLSX from 'xlsx'
export default {
  directives: { infiniteScroll },
  name: 'CardList',
  watch: {
    selectFileNote (newVlaue, oldValue) {
      if (newVlaue === '0') {
        this.currSelectFileNote = { id: null, fileId: this.currSelectFile.fileId, content: '' }
      } else {
        this.currSelectFileNote = this.fileNote.filter(item => item.id === Number(newVlaue))[0]
      }
    },
    currSelectFileNote: {
      handler (newVal) {
      },
      deep: true
    }
  },
  data () {
    return {
      keyword: '',
      selectFileNote: '0',
      currSelectFileNote: { id: null, fileId: this.$route.query.fileId, content: '' },
      currSelectFile: null,
      progressOf: 0, // 进度
      score: 0, // 评分
      marks: { 0: '0%', 50: '50%', 100: { style: { color: 'red' }, label: <strong>100%</strong> } },
      selectProject: null,
      delay: 500,
      spinning: false,
      wordInfo: null,
      note: '',
      fileNote: [],
      serviceUrl: null,
      previewType: 'pdf',
      projects: null,
      parmMap: {},
      loading: false,
      busy: false,
      parmkey: this.$route.query.fileName,
      dbFileInfo: [],
      selectedRowKeys: [],
      pagination: {
        pageNo: 1,
        defaultPageSize: 15,
        pageSize: 15, // 默认每页显示数量
        showTotal: total => `共 ${this.pagination.total} 数据`, // 显示总数
        // onShowSizeChange: (current, pageSize) => {(this.pageSize = pageSize)}, // 改变每页数量时更新显示
        onShowSizeChange: this.onShowSizeChange, // 改变每页数量时更新显示
        onChange: this.changePage, // 点击页码事件
        total: 0, // 总条数
        size: 'small'
       }
    }
  },
  components: {
    RecycleScroller,
    XLSX
  },
  computed: {},
  mounted () {
    if (this.$route.query.fileId) { // 文档面跳转过来
      // 查询当前文件信息
      getFileByFileId(this.$route.query.fileId).then(res => {
        if (res.success) {
          var file = res.data
          this.preview(file)
          this.parmkey = file.fileName
          this.parmMap = { 'fileName': file.fileName }
          this.pagination.pageNo = 1
          this.getPageFile()
        }
      })
    } else if (this.$route.query.projectId) { // 项目页跳转过来
      this.selectProject = Number(this.$route.query.projectId)
      this.parmMap = { 'projectId': Number(this.$route.query.projectId) }
    }
    this.getPageFile()
    getProjects().then(res => { this.projects = res.success ? res.data : [] })
  },
  methods: {
    // 修改后台文件评分
    scoreChange (value) {
      this.currSelectFile['score'] = value
      update(this.currSelectFile)
    },
    // 修改后台文件进度
    progressOfAfterChange (value) {
      this.currSelectFile['progressOf'] = value
      update(this.currSelectFile)
     },

    // 封装发送分页文件的数据
    getPageFile () {
      getPageFileByProjectId(this.pagination.pageNo, this.pagination.pageSize, this.parmMap).then((res) => {
        if (res.success) {
          this.dbFileInfo = res.data.data
          this.pagination.total = res.data.total
        }
      })
    },
        // 分页获取数据
    changePage (pageNo, pageSize) {
      this.pagination.pageNo = pageNo
      this.pagination.pageSize = pageSize
      this.getPageFile()
    },
        // 每页显示数据改变时候获取分页数据
    onShowSizeChange (pageNo, pageSize) {
      this.pagination.pageNo = pageNo
      this.pagination.pageSize = pageSize
      this.getPageFile()
    },
    // 文件检索框
    onSearch (value) {
      this.parmMap['fileName'] = value
      // 将当前页码调整为1
      this.pagination.pageNo = 1
      this.getPageFile()
    },
        // 下拉框值改变事件
    changeSelectProject (projectId) {
      this.parmMap['projectId'] = projectId
      this.getPageFile()
    },
    // 复选框值改变事件
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    reset () {
      this.parmMap['fileName'] = null
      this.parmkey = ''
      this.getPageFile()
    },
    closeTag (tagName) {
      this.currSelectFileNote.keywords = this.currSelectFileNote.keywords.filter(item => {
        if (item !== tagName) { return item }
       })
    },
    addKeyWord (keyword) {
      if (keyword === '') {
        this.$message.success('关键字不能为空')
        return
      }
      this.keyword = ''
      if (this.currSelectFileNote.keywords) {
        this.currSelectFileNote.keywords.push(keyword)
      } else {
        this.currSelectFileNote.keywords = [keyword]
      }
    },
    removeNote () {
      var fileNoteId = this.currSelectFileNote.id
      removeFileNoteById(fileNoteId).then(res => {
        res.success ? this.$message.success('删除成功') : this.$message.success('删除失败')
        getFileNoteByFileId(this.currSelectFile.fileId).then(res => {
          if (res.success) {
            this.fileNote = res.data
            this.selectFileNote = '0'
            this.currSelectFileNote = { id: null, fileId: null, content: '' }
          }
        })
      })
    },
    updataNote () {
      new Promise((resolve, reject) => {
          updataFileNoteById(JSON.stringify(this.currSelectFileNote)).then(res => {
            resolve(res)
          })
      }).then(res => {
          if (res.success) {
            if (this.currSelectFileNote.id) {
              res.success ? this.$message.success('更新成功') : this.$message.success('更新失败')
            } else {
              res.success ? this.$message.success('添加成功') : this.$message.success('添加失败')
            }
            getFileNoteByFileId(this.currSelectFile.fileId).then(resp => {
              if (resp.success) {
                this.fileNote = resp.data
                this.selectFileNote = res.data.id
              }
            })
          }
      })
    },
    preview (file) {
      // 修改url地址参数
      const newUrl = '/#' + this.$route.path + `?fileId=${file.fileId}`
      window.history.replaceState('', '', newUrl)
      // 记录当前选中文件
      this.currSelectFile = file
      // 获取文件笔记
      // 获取评分信息
      this.progressOf = file.progressOf ? file.progressOf : 0
      this.score = file.score ? file.score : 0
      // ,fileName, fileId

      // this.fileNote.fileId = file.fileId
      getFileNoteByFileId(file.fileId).then(res => {
        if (res.success) { this.fileNote = res.data }
      })
      // getHtmlByWord().then(res => { this.wordInfo = res })
      // const split = file.fileName.split('.') // split[split.length - 1]
      this.previewType = file.fileType
      if (['pdf', 'PDF', 'jpg', 'JPG', 'png', 'PNG'].includes(this.previewType)) {
        // 判断本地模式还是服务模式
        if (file.managementType === '外链') {
          // 外链使用绝对路径
          getServiceUrl(JSON.stringify({ 'param': file.filePath })).then(res => {
            res.success ? this.serviceUrl = res.data : this.$message.error(res.message)
          })
        } else if (file.managementType === '上传') {
          // 上传使用服务器路径
          this.serviceUrl = file.accessPath
        }
      } else if (['ppt', 'pptx', 'PPT', 'PPTX'].includes(this.previewType)) {
        this.serviceUrl = null
        getPPTServiceUrl(JSON.stringify({ 'param': file.filePath })).then(res => {
          res.success ? this.serviceUrl = res.data : this.$message.error(res.message)
        })
      } else if (['docx', 'doc', 'xlsx', 'xls', 'txt'].includes(this.previewType)) {
        this.spinning = true
        getHtmlByWord({ 'param': file.filePath }).then(res => {
          if (res.success) {
            this.wordInfo = res.data
          } else {
            this.wordInfo = ''
            this.$message.error(res.message)
          }
          this.spinning = false
          })
      } else {
        this.$message.warning('该文件暂不支持预览')
      }
    },
    previewExcel (fileNumber, SheetIndex) {
      const vm = this
      downloadFile(fileNumber).then(res => {
        const content = res
        const blob = new Blob([content])
        this.currentBlob = blob
        const files = new window.File([blob], fileNumber)
        var reader = new FileReader()
        reader.onload = function (e) {
          var data = new Uint8Array(e.target.result)
          var workbook = XLSX.read(data, { type: 'array' })
          /* DO SOMETHING WITH workbook HERE */
          vm.sheetList = workbook.SheetNames
          // const firstSheetName = workbook.SheetNames[0]
          const SheetName = workbook.SheetNames[SheetIndex]
          const worksheet = workbook.Sheets[SheetName]
          const header = vm.getHeaderRow(worksheet)
          const results = XLSX.utils.sheet_to_json(worksheet)
          // 生成表格数据
          vm.generateData({ header, results })
        }
        reader.readAsArrayBuffer(files)
      })
      // 打印数据
    }
  }
}
</script>

<style lang="less" scoped>
  .clickstyle {
      background: #4E7CA0;
      color: #ffffff;
  }
  .view-container {
    height:calc(100vh - 220px);
    width: 100%;
    overflow: auto;
}
.display-line {
  display: inline-block;
  margin-left: 10px;
}
</style>
