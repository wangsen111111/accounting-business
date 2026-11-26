<template>
  <div style="height: calc(100vh - 800px)">
    <operating-button :previewStatus="previewStatus" @openPreview="getOpenPreview" @closePreview="getClosePreview"></operating-button>
    <div style="height: calc(100vh - 800px)">
      <a-row>
        <a-col :span="leftSpan">
          <!-- 文件卡片 -->
          <a-card :bordered="false" >
            <a-breadcrumb style="margin-bottom:10px">
              <a-breadcrumb-item href="">
                <span style="margin-left:10px;font-size:18px"> <knowledgeBaseIcon :width="'20px'" :height="'14px'" alt="word" />{{ this.$route.query.name }} ：</span>
              </a-breadcrumb-item>
            </a-breadcrumb>
            <a-list
              :pagination="{showSizeChanger: true, showQuickJumper: true, pageSize: pageSize, total: 50}"
              :grid="{gutter: 24, lg: rowNum, md: 2, sm: 1, xs: 1}"
              :dataSource="dataSource">
              <a-list-item slot="renderItem" slot-scope="item">
                <a-card :hoverable="true" :bodyStyle="{padding:'12px'}">
                  <a-card-meta>
                    <a @click="preview(item.title)" slot="title">{{ item.title }}</a>
                    <a-avatar class="card-avatar" slot="avatar" :src="item.avatar" size="large"/>
                    <div class="meta-content" slot="description">
                      {{ item.subtitle() }}<br/>
                      <div class="text-overflow" :title="item.tags">
                        <a-tag color="pink" :key="index" v-for="(tag, index) in item.tags">  {{ tag }} </a-tag>
                      </div>
                    </div>
                  </a-card-meta>
                  <template class="ant-card-actions" slot="actions">
                    <a @click="preview(item.title)">预览</a>
                    <a>下载</a>
                    <a>收藏</a>
                    <a-dropdown>
                      <a-menu slot="overlay">
                        <a-menu-item><a>预览</a></a-menu-item>
                        <a-menu-item><a>下载</a></a-menu-item>
                        <a-menu-item><a>删除</a></a-menu-item>
                        <a-menu-item><a>收藏</a></a-menu-item>
                        <a-menu-item><a>分享</a></a-menu-item>
                        <a-menu-item><a>添加标签</a></a-menu-item>
                      </a-menu>
                      <a><a-icon type="small-dash" /></a>
                    </a-dropdown>
                  </template>
                </a-card>
              </a-list-item>
            </a-list>
          </a-card>
        </a-col>
        <a-col v-if="rightSpan != 0" :span="rightSpan">
          <a-card class="view-container" style="margin-left:10px">
            <!-- word预览 -->
            <div v-if="previewType === 'word'" v-html="wordInfo" style="padding:12px"></div>
            <!-- pdf预览 -->
            <iframe
              v-if="previewType.toLowerCase() === 'pdf'"
              type="application/pdf"
              :src="pdfUrl"
              style="width:100%;height: calc(100vh - 280px)"
            ></iframe>
            <!-- excel预览 -->
            <a-tabs v-if="previewType.toLowerCase() === 'xls' || previewType.toLowerCase() === 'xlsx'" default-active-key="jsMindId" @change="changeSheet" tab-position="bottom">
              <a-tab-pane v-for="(item,index) in sheetList" :key="index">
                <span slot="tab" >
                  <a-icon type="apple" />
                  {{ item }}
                </span>
                <a-table
                  style="width:100%;height: calc(100vh - 280px)"
                  :columns="tableHeader"
                  :data-source="tableData">
                </a-table>
              </a-tab-pane>
            </a-tabs>
          </a-card>
        </a-col>
      </a-row>
    </div>

  </div>

</template>

<script>
import contentmenu from 'v-contextmenu'
import 'v-contextmenu/dist/index.css'
import Detail from '@/components/content/detail/index'
import OperatingButton from '@/components/content/OperatingButton/index'
import { knowledgeBaseIcon } from '@/core/icons'
import { downloadFile } from '@/api/knowledgeBase'
// import { getHtmlByWord, downloadFile } from '@/api/knowledgeBase'
import XLSX from 'xlsx'

export default {
  name: 'StandardList',
  components: {
    contentmenu,
    Detail,
    OperatingButton,
    knowledgeBaseIcon,
    XLSX
  },
  props: {
    isFileListOrCart: { type: Boolean, default: false }
  },
  data () {
    return {
      leftSpan: 24,
      rightSpan: 0,
      rowNum: 3,
      pageSize: 12,
      dataSource: [],
      wordInfo: null,
      openPreview: null,
      closePreview: null,
      previewStatus: false,
      previewType: null,
      pdfUrl: null,
      sheetList: [],
      currentSheet: null,
      tableData: [],
      tableHeader: []
    }
  },
  mounted () {
    const tags = ['标签1', '标签2', '标签3', '标签4', '标签5', '标签6', '标签7', '标签8', '标签9']
    const operations = ['更新', '修改']
    // eslint-disable-next-line no-unused-vars
    let filenames = []
      // eslint-disable-next-line no-unused-vars
    let fileTypes = ['pdf', 'excel', 'word', 'picture', 'process', 'folder']
    function uuid () {
          function S4 () { return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1) }
          return (S4() + S4() + '-' + S4() + '-' + S4() + '-' + S4() + '-' + S4() + S4() + S4())
        }

    if (this.$route.name === 'KnowledgeBase') {
      filenames = ['井场实验1', '井场实验2', '井场实验3', '井场实验4', '井场实验5', '井场实验6']
      fileTypes = ['folder']
    } else {
      filenames = ['三国演义', '红楼梦', '水府传', '西游记', '一些事', '斗罗大陆']
      fileTypes = ['pdf', 'xlsx', 'word']
    }
    for (var j = 0; j < 50; j++) {
      var fileType = fileTypes[Math.floor((Math.random() * fileTypes.length))]
      var KnowledgeBaseFileName = filenames[Math.floor((Math.random() * filenames.length))]
      var NotKnowledgeBaseFileName = filenames[Math.floor((Math.random() * filenames.length))] + '.' + fileType
      var tmpTitle = this.$route.name === 'KnowledgeBase' ? KnowledgeBaseFileName : NotKnowledgeBaseFileName
      if (fileType.toLowerCase() === 'xls' || fileType.toLowerCase() === 'xlsx') {
        fileType = 'excel'
      }
      this.dataSource.push({
        isShow: true,
        id: uuid(),
        title: tmpTitle,
        avatar: '/svg/' + fileType + '.svg',
        time: '2018-07-26 22:44',
        path: 'zhouwei/bigdata',
        tags: tags,
        operation: operations[Math.floor((Math.random() * operations.length))],
        subtitle () { return this.path + ' ' + this.time + this.operation + '了文档' }
      })
    }
    // this.previewExcel('052D型驱逐舰zbzt20200716163402753中国城市经纬度.xlsx', 1)
    // this.previewExcel('052D型驱逐舰zbzt20200716163402753中国城市经纬度.xlsx', 0)
  },
  methods: {
    uuid () {
          function S4 () { return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1) }
          return (S4() + S4() + '-' + S4() + '-' + S4() + '-' + S4() + '-' + S4() + S4() + S4())
        },
    preview (fileName) {
      // 判断是否开启预览窗口,若未打开（false），则打开
      if (!this.previewStatus) {
        this.getOpenPreview()
        this.previewStatus = !this.previewStatus
      }
      // getHtmlByWord().then(res => { this.wordInfo = res })
      this.previewType = fileName.split('.')[1]
      if (this.previewType === 'pdf' || this.previewType === 'PDF') {
        this.previewPDF()
      } else if (this.previewType === 'word') {
        this.previewWord()
      } else if (this.previewType === 'xlsx' || this.previewType === 'excel') {
        this.previewExcel('052D型驱逐舰zbzt20200716163402753中国城市经纬度.xlsx', 0)
      }
    },
    previewWord () {
      // getHtmlByWord().then(res => { this.wordInfo = res })
    },
    previewPDF () {
      this.pdfUrl = '/pdf/testPdf.pdf'
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
          console.log('sheetList=>', vm.sheetList)
          // const firstSheetName = workbook.SheetNames[0]
          const SheetName = workbook.SheetNames[SheetIndex]
          const worksheet = workbook.Sheets[SheetName]
          // if (vm.currentSheet) {
          //   worksheet = workbook.Sheets[vm.currentSheet]
          // } else {
          //   vm.currentSheet = SheetName
          // }
          // const json = XLSX.utils.sheet_to_json(worksheet);
          // const html = XLSX.utils.sheet_to_html(worksheet);
          // const formulae = XLSX.utils.sheet_to_formulae(worksheet);
          const header = vm.getHeaderRow(worksheet)
          const results = XLSX.utils.sheet_to_json(worksheet)
          // 生成表格数据
          vm.generateData({ header, results })
        }
        reader.readAsArrayBuffer(files)
      })
      // 打印数据
    },
    // 选择sheet页
    changeSheet (sheetIndex) {
      this.currentSheet = sheetIndex
      this.previewExcel('052D型驱逐舰zbzt20200716163402753中国城市经纬度.xlsx', sheetIndex)
    },
    // 生成表格数据
    generateData ({ header, results }) {
      // this.tableHeader = header
      // this.tableData = results
      const vm = this
      const tmpHeader = []
      header.forEach(function (headerItem) {
        tmpHeader.push({
           key: vm.uuid(),
          title: headerItem,
          dataIndex: headerItem,
          sorter: true,
          width: '20%',
          scopedSlots: { customRender: 'name' }
        })
      })
      this.tableHeader = tmpHeader

      const tmpResults = []
      results.forEach(function (item) {
        item.key = vm.uuid()
        tmpResults.push(item)
      })

      this.tableData = tmpResults
      console.log('tableHeader=>', header)
      console.log('tableData=>', results)
    },
    // 获取表头数据
    getHeaderRow (sheet) {
      const headers = []
      const range = XLSX.utils.decode_range(sheet['!ref'])
      console.log('range=>', range)
      let C
      const R = range.s.r
      let EmptyHeader = 0
      /* start in the first row */
      for (C = range.s.c; C <= range.e.c; ++C) {
        /* walk every column in the range */
        const cell = sheet[XLSX.utils.encode_cell({ c: C, r: R })]
        /* find the cell in the first row */
        let hdr = '__EMPTY_' + C // <-- replace with your desired default

        if (cell && cell.t) hdr = XLSX.utils.format_cell(cell)
        else {
          if (EmptyHeader === 0) {
            hdr = '__EMPTY'
            EmptyHeader++
          } else {
            hdr = '__EMPTY_' + EmptyHeader++
          }
        }
        headers.push(hdr)
      }
      return headers
    },
    getOpenPreview (openPreview) {
      this.previewStatus = openPreview
      this.leftSpan = 12
      this.rightSpan = 12
      this.rowNum = 2
      this.pageSize = 6
      this.wordInfo = ''
    },
    getClosePreview (closePreview) {
      this.previewStatus = closePreview
      this.leftSpan = 24
      this.rightSpan = 0
      this.rowNum = 3
      this.pageSize = 12
    },
    getisFileListOrCart (data) {
      this.isFileListOrCart = data
    }
  },
  created () {

  }
}
</script>

<style lang="less" scoped>
@import "~@/components/index.less";
  .text-overflow{
    margin-top:15px;
    display: -webkit-box;/*作为弹性伸缩盒子模型显示*/
    -webkit-line-clamp: 1; /*显示的行数；如果要设置2行加...则设置为2*/
    overflow: hidden; /*超出的文本隐藏*/
    text-overflow: ellipsis; /* 溢出用省略号*/
    -webkit-box-orient: vertical;/*伸缩盒子的子元素排列：从上到下*/
  }
  .menu:hover{
    background-color: red;
  }
  .view-container {
    height:calc(100vh - 220px);
    width: 100%;
    overflow: auto;
}
</style>
