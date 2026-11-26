<!--思维导图操作页-->
<template>
  <div>
    <a-col :span="6">
      <a-tabs default-active-key="fileType" @change="changeTabl" tab-position="bottom">
        <a-tab-pane key="fileType">
          <span slot="tab"> <a-icon type="apple" /> 已有文件 </span>
          <a-card style="width: 100%;height:calc(100vh - 133px)">
            <a-row>
              <a-col :span="20"><a-input-search placeholder="文件检索" enter-button @search="onSearch" v-model="parmMap.key" /></a-col>
              <a-col :span="4"><a-button @click="reset()">重置</a-button></a-col>
            </a-row>
            <!-- v-if="$store.state.mountFileIds" -->
            <a-table
              :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
              :scroll="{ y: 'calc(100vh - 333px)' }"
              :columns="columns"
              :data-source="dbFileInfo"
              :pagination="pagination" >
              <!-- <span slot="boolvalue" slot-scope="text">{{ text | BoolFilter }}</span> -->
              <span slot="boolvalue" slot-scope="text">{{ isMount(text) }}</span>
            </a-table>
          </a-card>
        </a-tab-pane>
        <a-tab-pane key="import">
          <span slot="tab"> <a-icon type="android" />  新导入文件 </span>
          <a-card style="width: 100%;height:calc(100vh - 133px)">
            <span>挂载模式： </span>
            <a-radio-group name="radioGroup" :default-value="redioType" @change="radioGroupChange">
              <a-radio :value="0"> 外链 </a-radio>
              <a-radio :value="1"> 上传 </a-radio>
            </a-radio-group>
            <div v-if="redioType">
              <a-upload-dragger
                styleBody="height:20px"
                name="file"
                :file-list="fileList"
                :multiple="true"
                :showUploadList="showUploadList"
                :before-upload="beforeUpload">
                <p class="ant-upload-drag-icon"> <a-icon type="inbox" /> </p>
                <p class="ant-upload-text"> 单击或拖动文件到此区域 </p>
              </a-upload-dragger>
              <a-button
                block
                type="primary"
                :disabled="fileList.length === 0"
                :loading="uploading"
                @click="handleUpload">
                {{ uploading ? '上传中' : '开始上传' }}
              </a-button>

              <div>
                <a-radio-group @change="radioGroupChangeFile" :default-value="redioFileType" button-style="solid">
                  <a-radio-button style="width: 143px;" value="successFileInfo">成功上传文件 </a-radio-button>
                  <a-radio-button style="width: 143px;" value="existFileInfo">重复上传文件 </a-radio-button>
                  <a-radio-button style="width: 143px;" value="failFileInfo">上传失败文件 </a-radio-button>
                </a-radio-group>
              </div>

              <a-table
                v-if="redioFileType === 'successFileInfo'"
                :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
                :columns="columns"
                :data-source="successFileInfo"
                :pagination="upPagination" >
                <span slot="boolvalue" slot-scope="text">{{ isMount(text) }}</span>
              </a-table>

              <a-table
                v-if="redioFileType === 'existFileInfo'"
                :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
                :columns="[ { title: '重复上传文件', dataIndex: 'fileName' } ]"
                :data-source="existFileInfo"
                :pagination="upPagination" >
                <span slot="boolvalue" slot-scope="text">{{ isMount(text) }}</span>
              </a-table>

              <a-table
                v-if="redioFileType === 'failFileInfo'"
                :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
                :columns="[ { title: '上传失败文件', dataIndex: 'fileName' } ]"
                :data-source="failFileInfo"
                :pagination="upPagination" >
                <span slot="boolvalue" slot-scope="text">{{ isMount(text) }}</span>
              </a-table>
            </div>
            <div v-else>
              <a-textarea
                style="background: #FFFFFF;font-color: #000000"
                v-model="value"
                placeholder="请输入本地文件路径或目录，多条换行输入..."
                :auto-size="{ minRows: 7, maxRows: 8 }" />
              <a-button :disabled="isDisable" block type="primary" @click="fileChain"> 开始外链 </a-button>
              <a-table
                :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
                :columns="[ { title: '外链文件', dataIndex: 'fileName' } ]"
                :data-source="chainFileInfo"
                :pagination="upPagination" />
            </div>
          </a-card>
        </a-tab-pane>
        <a-tab-pane key="tag">
          <span slot="tab"> <a-icon type="android" /> 标签 </span>
          <a-card style="width: 100%;height:calc(100vh - 133px)">
            <div>
              挂载模式：
              <a-radio-group name="radioGroup" v-model="mountModel">
                <a-radio :value="0"> 多标签 </a-radio>
                <a-radio :value="1"> 单标签 </a-radio>
              </a-radio-group><br>
            </div>

            <div v-if="mountModel === 1">
              标签逻辑：
              <a-radio-group name="radioGroup" v-model="logicalModel" >
                <a-radio :value="0"> 交集 </a-radio>
                <a-radio :value="1"> 并集 </a-radio>
              </a-radio-group>
            </div>

            <!--  v-if="$store.state.mountFileIds" -->
            <a-table
              :row-selection="{ onChange: onSelectChangeTag }"
              :scroll="{ y: 'calc(50vh)' }"
              :columns="tagColumns"
              :data-source="tagWithCount"
              :pagination="pagination" >
              <span slot="boolvalue" slot-scope="text">{{ isMount(text) }}</span>
            </a-table>
            <a-input placeholder="请输入节点名称" v-if="mountModel === 1"/>
            <a-button type="primary" style="width:100%" @click="tagMount">挂载</a-button>
          </a-card>
        </a-tab-pane>
      </a-tabs>
    </a-col>

    <a-col :span="18">
      <a-row>
        <a-col :span="12">
          <a-select
            v-if="projects !== null"
            show-search
            placeholder="切换项目"
            option-filter-prop="children"
            v-model="selectProjectId"
            @change="changeSelectProject"
            style="width: 300px">
            <a-select-option v-for="project in projects" :key="project.projectName" :value="project.projectId"> {{ project.projectName }} </a-select-option>
          </a-select>
        </a-col>
        <a-col :span="12" style="text-align:right">
          <a-icon type="zoom-in" :style="{ fontSize: '20px', color: '#08c' }" @click="zoomIn" />
          <a-icon type="zoom-out" :style="{ fontSize: '20px', color: '#08c' }" style="margin-left:20px" @click="zoomOut"/>
          <!-- <a-button type="primary" style="margin-left:20px"> 思维导图 </a-button>
          <a-button type="primary" style="margin-left:1px"> 树结构 </a-button> -->
          <a-button type="primary" style="margin-left:1px" @click="savaMindMap"> 保存 </a-button>
        </a-col>
      </a-row>
      <!-- v-model="activeKey" 可以替换 :default-active-key="mindMapActiveKey"  @tabClick="tabClickMindMap"-->
      <a-row>
        <a-tabs
          v-model="activeKey"
          tab-position="bottom"
          type="editable-card"
          @change="changeMindMap"
          @edit="onEdit">
          <a-tab-pane v-for="mindMap in mindMaps" :key="mindMap.id" :tab="mindMap.mindMapName" >
            <JsMindMap
              :ref="mindMap.id"
              @changeMountStatus="()=>{selectedRowKeys = []}"
              @isSelectNode="isSelectNode"
              :jsMindId="mindMap.id"
              :jsMindMapData="mindMap"
              :fileIds="selectedRowKeys"
              :styleCss="{ width: 'calc(50wh - 0px)', height: 'calc(100vh - 165px)'}"></jsmindmap>
          </a-tab-pane>
        </a-tabs>
      </a-row>
    </a-col>
    <a-modal
      width="100%"
      centered
      title="添加思维导图"
      :visible="visible"
      @ok="anddJsmindhandleOk"
      @cancel="handleCancel">
      <!-- <a-input v-model="inputValue" placeholder="请输入思维导图名称" /> -->
      <a-card :bordered="false" style="min-height: calc(100vh - 100px)">
        <a-form-model
          ref="ruleForm"
          :rules="rules"
          :model="addJsmindForm"
          v-bind="{}">
          <a-row>
            <a-col :span="6">
              <a-form-model-item label="" >
                <span>维度：</span>
                <a-radio-group v-model="addJsmindForm.dimensionId" @change="onChangeRadio" >
                  <a-radio-button value="-1"> 空维度 </a-radio-button>
                  <a-radio-button
                    v-for="(dimension) in dimensions"
                    :key="dimension.dimensionId"
                    :value="dimension.dimensionId">
                    {{ dimension.dimensionName }}
                  </a-radio-button>
                </a-radio-group>
              </a-form-model-item>
            </a-col>
            <a-col :span="2">
              <a-form-model-item label="" prop="isEmpty">
                <span>空模板：</span>
                <a-switch checked-children="是" un-checked-children="否" v-model="addJsmindForm.isEmpty" @change="changeSwitch" default-checked />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="" prop="mindMapName">
                <span>思维导图名称：</span>
                <a-input v-model="addJsmindForm.mindMapName" placeholder="请输入思维导图名称" style="width: 10vw" />
              </a-form-model-item>
            </a-col>

          </a-row>

          <a-form-model-item label="" prop="jsmind">
            <div class="contentStyle">
              <a-card v-if="switchStatus">
                <a-card
                  style="width:550px"
                  :class="!selectJsmindId ? 'selectStyle': 'defaultStyle'"
                  @click="selectTemplate(null)">
                  <img
                    style="height:250px"
                    slot="cover"
                    alt="example"
                    src="https://gw.alipayobjects.com/zos/rmsportal/JiqGstEfoWAOHiTxclqi.png"/>
                  <a-card-meta title="空白模板" description style="text-align:center"></a-card-meta>
                </a-card>
              </a-card>
              <div v-if="!switchStatus">
                <!-- 模板 -->
                <a-card :bodyStyle="{ 'width':'99%', 'overflow-x':'scroll', 'white-space':'nowrap', 'padding':'20px' }" >
                  <h1>模板:</h1>
                  <a-card
                    style="overflow:hidden;display:inline-block;margin-right:10px"
                    :class="template.id === selectJsmindId ? 'selectStyle': 'defaultStyle'"
                    v-for="(template) in filterMindMapsTemplate"
                    :key="template.id"
                    @click="selectTemplate(template)">
                    <JsMindSimple
                      :ref="template.id"
                      style="display:flex;"
                      slot="cover"
                      :jsMindId="template.id"
                      :jsMindMapData="template"
                      :className="'jsmind_container_no_scroll_axis'"
                      :styleSet="{ width: '550px', height: '350px'}">
                    </JsMindSimple>
                    <a-card-meta
                      :title="template.mindMapName"
                      description
                      style="text-align:center">
                    </a-card-meta>
                  </a-card>
                </a-card>

                <!-- 已有项目作为模板 -->
                <a-card :bodyStyle="{ 'width':'99%', 'overflow-x':'scroll', 'white-space':'nowrap', 'padding':'20px' }" >
                  <h1>已有项目作为模板:</h1>
                  <a-card
                    style="overflow:hidden;display:inline-block;margin-right:10px"
                    :class="template.id === selectJsmindId ? 'selectStyle': 'defaultStyle'"
                    v-for="(template) in filterMindMapsProjectTemplate"
                    :key="template.id"
                    @click="selectTemplate(template)">
                    <JsMindSimple
                      :ref="template.id"
                      style="display:flex;"
                      slot="cover"
                      :jsMindId="template.id"
                      :jsMindMapData="template"
                      :className="'jsmind_container_no_scroll_axis'"
                      :styleSet="{ width: '550px', height: '350px'}">
                    </JsMindSimple>
                    <a-card-meta
                      :title="template.mindMapName"
                      description
                      style="text-align:center"
                    ></a-card-meta>
                  </a-card>
                </a-card>
              </div>
            </div>
          </a-form-model-item>

          <a-divider />
          <span style="100vw" class="centered">
            <a-button type="primary" @click="handleCancel">取消</a-button>
            <a-button type="primary" style="margin-left:5%" @click="anddJsmindhandleOk">确定</a-button>
          </span>
        </a-form-model>
      </a-card>
    </a-modal>
  </div>
</template>

<script>
import JsMindSimple from '@/components/content/jsmind/jsMindSimple.vue' // jsmind
import { getDimensions } from '@/api/dimension' // PostsJsMinds GetJsMinds
import SelectTemplate from '@/components/content/jsmind/SelectTemplate'
import JsMindMap from '@/components/content/jsmind/jsmind'
import { getPageFileByProjectId, uploadFile, fileChain } from '@/api/file'
import { getMindMapByProjectId, removeMindMapByid, getMindMapByDimensionId, addMindMap } from '@/api/mindMap'
import { getProjects } from '@/api/project'
import { getHotTagWithFileCount, multipleTagMount, singleTagMount } from '@/api/tag'
import store from '@/store/index'

export default {
  components: { JsMindMap, SelectTemplate, JsMindSimple },
  data () {
    return {
      mountModel: 0, // 挂载模式
      logicalModel: 0, // 逻辑模式
      currSelectNode: null,
      tagWithCount: null,
      switchStatus: true,
      selectJsmindId: 0,
      clickJsMind: null,
      mindMapsTemplate: [],
      selectName: '',
      addJsmindForm: {
        projectId: null,
        mindMapName: '',
        dimensionId: '-1',
        jsmind: {},
        isEmpty: true
      },
       rules: {
        mindMapName: [
          { required: true, message: '请输入思维导图名称', trigger: 'change' },
          { max: 20, message: '思维导图名称最长20', trigger: 'change' }
        ],
        jsmind: { required: true, message: '请选择模板', trigger: 'change' }
      },
      dimensionValue: '',
      dimensions: [],
      currSelectJsmindId: null,
      mountFileIds: [],
      spinning: false,
      drawerVisible: false,
      selectProjectId: null,
      projects: null,
      inputValue: '',
      visible: false,
      index: 0,
      mindMaps: [],
      showUploadList: true,
      value: '',
      fileList: [],
      uploading: false,
      redioType: 1, // 单选框默认为上传
      redioFileType: 'successFileInfo',
      projectId: this.$route.query.projectId,
      parmMap: { 'projectId': null, 'fileName': null },
      activeKey: null,
      oldActiveKey: null,
      newTabIndex: 0,
      tagColumns: [
        { title: '标签', dataIndex: 'name', width: '250px' },
        { title: '文件数量', dataIndex: 'value', width: '100px' }
      ],
      columns: [
        { title: '已上传文件', dataIndex: 'fileName', width: '250px' },
        { title: '是否挂载', dataIndex: 'fileId', scopedSlots: { customRender: 'boolvalue' }, width: '100px' }
      ],
      dbFileInfo: [],
      successFileInfo: [],
      failFileInfo: [],
      existFileInfo: [],
      chainFileInfo: [],
      selectedRowKeys: [], // Check here to configure the default column
      loading: false,
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
       },
      upPagination: {
        defaultPageSize: 15,
        showTotal: total => `上传 ${total} 个文件`,
        showSizeChanger: false,
        onShowSizeChange: (current, pageSize) => (this.pageSize = pageSize)
      }
    }
  },
  watch: {
    computedMountFileIds (newVlaue, oldValue) {

    }
  },
  computed: {
    filterMindMapsTemplate: function () {
      if (this.mindMapsTemplate) {
        const filterMindMapsTemplate = this.mindMapsTemplate.filter(item => item.isTemplate)
        return filterMindMapsTemplate
      }
      return []
    },
    filterMindMapsProjectTemplate () {
      if (this.mindMapsTemplate) {
        const filterMindMapsTemplate = this.mindMapsTemplate.filter(item => !(item.isTemplate))
        return filterMindMapsTemplate
      }
      return []
    },
    computedMountFileIds: function () {
      return this.activeKey
    },
    indexArr: function () {
      return this.mindMaps.map((item, index) => {
        return { 'id': item.id, 'index': index }
      })
    },
    isDisable: function () {
      if (this.value === null || this.value === '') { return true } else { return false }
    },
		isMount () {
        return function (fileId) {		// 对计算属性进行传参
          if (store.state.mountFileIds !== undefined) {
            var fileIds = store.state.mountFileIds[this.activeKey]
            if (fileIds !== undefined && fileIds !== null) {
              if (fileIds.length > 0) {
                if (fileIds.indexOf(String(fileId)) > -1) { return '已挂载' } else { return '未挂载' }
              } else { return '未挂载' }
            }
          }
        }
			}
  },
  beforeMount () {

  },
  mounted () {
    if (this.$route.query.projectId) { // 项目页跳转过来
      if (this.$route.query.jsmindId) {
        this.activeKey = this.$route.query.jsmindId
      }
      this.selectProjectId = Number(this.$route.query.projectId)
    }
    this.parmMap['projectId'] = this.selectProjectId
    // this.getMindMapByProjectIdUtil()

    new Promise((resolve, reject) => {
      getMindMapByProjectId(this.projectId).then(res => {
        if (res.success) {
          this.mindMaps = res.data
          if (this.mindMaps != null && this.mindMaps.length > 0) {
            if (!this.activeKey) {
              this.activeKey = this.mindMaps[0].id
            }
          }
          resolve()
        }
      })
    }).then(() => {
      this.getPageFile()
      getProjects().then(res => { this.projects = res.success ? res.data : [] })
    })
  },
  beforeDestroy () {
  },
  destroyed () {
  },
  methods: {
    // 是否选中节点
    isSelectNode (data, selectNodeAndJm) {
      this.currSelectNode = data
      // this.selectNodeAndJm = selectNodeAndJm
    },
    // 挂载
    tagMount () {
      console.log(this.selectedRowKeys)
      if (!this.currSelectNode) { this.$message.warning('请选择节点') }
      // this.$refs[this.activeKey][0].tagMount(item.tagName, item.fileIds)
      // 获取当前选中的标签
    },
    changeSwitch (boo) {
      if (boo) {
        this.selectTemplate(null)
        this.addJsmindForm.jsmind = {}
      }
      this.switchStatus = boo
    },
    selectTemplate (jsmind) {
      if (jsmind) {
        this.selectJsmindId = jsmind.id
        this.addJsmindForm.jsmind = jsmind
        // this.$refs[jsmind.id][0].getJsmindData()
      } else {
        this.selectJsmindId = null
        this.addJsmindForm.jsmind = {}
      }
    },
    onChangeRadio (e) {
      this.addJsmindForm.jsmind = null
      const dimensionId = e.target.value
      getMindMapByDimensionId(dimensionId, this.projectId).then(res => {
        if (res.success) { this.mindMapsTemplate = res.data }
      })
      // console.log(`checked = ${dimensionId}`)
      // 获取改维度下的思维导图和项目
    },
    // changeMountStatus () {
    //   this.selectedRowKeys = []
    // },
    getMindMapByProjectIdUtil () {
      getMindMapByProjectId(this.projectId).then(res => {
        if (res.success) {
          this.mindMaps = res.data
          if (this.mindMaps != null && this.mindMaps.length > 0) {
            this.activeKey = this.mindMaps[0].id
          }
        }
      })
    },
    // 监听子组件消息
    isSaveOk (data) { this.spinning = false },
    // 下拉框值改变事件
    changeSelectProject (data) {
      this.projectId = data
      // 更加项目id刷新思维导图
      this.getMindMapByProjectIdUtil()
    },
    // 单选框改变事件
    radioGroupChange (obj) {
      this.redioType = obj.target.value
    },
    // 文件列表显示
    radioGroupChangeFile (obj) {
      this.redioFileType = obj.target.value
    },
    beforeUpload (file) {
      this.fileList = [...this.fileList, file]
      return false
    },
    // 文件外链
    fileChain () {
      fileChain(JSON.stringify({ 'paths': this.value }), this.projectId).then(res => {
        res.data.map(item => {
          item.fileName = item.fileName + '.' + item.fileType
          return item
        })
        this.chainFileInfo = res.data
      })
    },
    // 文件上传
    handleUpload () {
      this.showUploadList = true
      const { fileList } = this
      const formData = new FormData()
      formData.append('projectId', this.projectId)

      fileList.forEach(file => {
        formData.append('files', file)
      })
      this.uploading = true

      uploadFile(formData).then(res => {
        if (res.success) {
          this.successFileInfo = res.data.success
          this.failFileInfo = res.data.fail
          this.existFileInfo = res.data.exist
          this.$message.success('上传完成')
        }
      }).catch(error => {
          this.$message.error('上传异常', error)
      }).finally(o => {
        this.fileList = []
        this.uploading = false
        this.showUploadList = false
      })
    },
    // 封装发送分页文件的数据
    getPageFile () {
      getPageFileByProjectId(this.pagination.pageNo, this.pagination.pageSize, this.parmMap).then((res) => {
        this.dbFileInfo = res.data.data.map(item => {
          item['key'] = item['fileId']
          // item['fileName'] = item['fileName'] + '.' + item['fileType']
          return item
       })
      this.pagination.total = res.data.total
      })
    },
    reset () {
      this.parmMap['fileName'] = null
      this.getPageFile()
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
      this.pagination.pageNo = 1
      this.getPageFile()
    },
    // 标签选择值改变事件
    onSelectChangeTag (selectedRowKeys, row) {
      alert(this.mountModel)
      var tagNames = row.map(item => { return item.name })
      // 判断是多标签挂载还是单标签挂载
      if (this.mountModel === 0) {
        multipleTagMount({ 'tagNames': tagNames }).then(res => {
          console.log(res.data)
        })
      } else {
        singleTagMount({ 'tagNames': tagNames }, this.logicalModel).then(res => {
          console.log(res.data)
        })
      }
    },
    // 复选框值改变事件
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    tagHandleChange (value) {
      this.currFileTags = value
    },
    // 切换卡片，组织index
    changeTabl (key) {
      if (key === 'tag') {
        getHotTagWithFileCount().then(res => {
          if (res.success) { this.tagWithCount = res.data }
        })
      }
    },
    changeMindMap (key) {
      // console.log(this.activeKey)
      // this.$refs[this.activeKey][0].savaMindMap()
      this.currSelectJsmindId = key
      // this.savaMindMap()
      // this.$refs[key][0].saveMountFileIds()
    },
    // 切换前 vm.$refs[vm.activeKey][0].savaMindMap()
    tabClickMindMap (key) {

    },
    callback (key) {
      console.log(key)
    },
    onEdit (targetKey, action) {
      this[action](targetKey)
    },
    // 添加思维导图
    anddJsmindhandleOk (e) {
        // 校验是否选择模板
        if (!this.addJsmindForm.jsmind) {
          this.$message.warning('请选择模板')
        }
        this.$refs.ruleForm.validate(valid => {
          if (valid) { // 校验通过
          // this.addJsmindForm.projectId = this.projectId
          var jsmind
          if (this.addJsmindForm.isEmpty) {
            const data = { 'meta': { 'name': this.addJsmindForm.mindMapName, '': '', 'version': '' }, 'format': 'node_tree', 'data': { 'id': 'root', 'topic': this.addJsmindForm.mindMapName, 'expanded': true } }
            jsmind = { 'projectId': this.projectId, 'dimensionId': this.addJsmindForm.dimensionId, 'data': data, 'mindMapName': this.addJsmindForm.mindMapName }
          } else {
            this.addJsmindForm.jsmind['projectId'] = this.projectId
            this.addJsmindForm.jsmind['dimensionId'] = this.dimensionId
            jsmind = JSON.parse(JSON.stringify(this.addJsmindForm.jsmind))
            jsmind.data.meta.name = this.addJsmindForm.mindMapName
            jsmind.data.data.topic = this.addJsmindForm.mindMapName
            jsmind.mindMapName = this.addJsmindForm.mindMapName
          }
            addMindMap(JSON.stringify(jsmind)).then(res => {
              if (res.success) {
                this.activeKey = res.data.id
                this.mindMaps.push(res.data)
                this.visible = false
                this.confirmLoading = false
                this.switchStatus = true
                this.selectTemplate(null)
                this.addJsmindForm = {
                  projectId: null,
                  mindMapName: '',
                  dimensionId: '-1',
                  jsmind: {},
                  isEmpty: true
                }
              }
        })
          } else {
            console.log('error submit!!')
            return false
          }
      })
    },
    handleCancel (e) {
      this.visible = false
    },
    // 添加tab标签页面（添加思维导图）
    add () {
        getDimensions().then((res) => {
          this.dimensions = res.data
           this.visible = true
        })
    },
    // 删除标签页（删除思维导图） targetKey为当前选中的key
    remove (targetKey) {
      // 弹框提示是否删除当前思维导图
      const vm = this
      this.$confirm({
        title: '是否删除当前思维导图?',
        content: '确定删除，取消不删除',
        onOk () {
          const mindMaps = vm.mindMaps.filter(pane => pane.id !== targetKey)
          vm.mindMaps = mindMaps
          removeMindMapByid(targetKey).then(res => {
            if (res.success) {
              vm.mindMaps.length > 0 ? vm.activeKey = vm.mindMaps[0].id : this.activeKey = ''
              vm.$message.success('删除成功')
            } else {
              vm.$message.error('删除失败')
            }
          }).catch(error => vm.$message.error('删除异常', error))
        },
        onCancel () {
          vm.$message.success('取消删除')
        }
      })
    },
    // 保存思维导图
    savaMindMap () {
      // 调用子组件更新方法
      this.$refs[this.activeKey][0].savaMindMap()
    },
    // 放大
    zoomIn () { this.$refs[this.activeKey][0].zoomIn() },
    zoomOut () { this.$refs[this.activeKey][0].zoomOut() },
    // 将选中文件数据发送给子组件
    mountFiles () { return this.selectedRowKeys }
  }
}
</script>

<style scoped lang="less">
.divcss5{ border-left:1px solid #00F}
.selectStyle {
  border: 0.1vw solid red;
}
.defaultStyle {
  border: 0.1vw solid white;
}
.contentStyle {
  margin-left: 2.4vw;
  padding-top: 1.5vw;
}
.centered {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
