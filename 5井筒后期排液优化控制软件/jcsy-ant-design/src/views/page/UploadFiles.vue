<template>
  <div>
    <a-card :bordered="false" style="min-height: calc(100vh - 100px)">
      <span class="centered">
        <a-steps
          style="width:50vw;height:7vh;display:flex;justify-content: center;align-items: center;"
          :current="current"
          @change="onChange"
        >
          <a-step title="新建项目" description />
          <a-step title="选择模板" description />
          <a-step title="上传文件" description />
        </a-steps>
      </span>
      <a-divider />
      <a-row type="flex" :gutter="[16, { xs: 8, sm: 8, md: 8, lg: 8 }]">
        <a-col :span="6">
          <span>导入方式：&nbsp;&nbsp;</span>
          <a-radio-group v-model="value">
            <a-radio-button :value="1">外链</a-radio-button>
            <a-radio-button :value="2">上传</a-radio-button>
          </a-radio-group>
          <a-divider />
          <span v-if="value === 1">
            <a-input v-model="externalLink" placeholder="请输入本地文件夹路径~" style="width: 70%" />
            <a-button
              type="primary"
              style="width: 30%;"
              @click="externalLinkUpload(externalLink)"
            >外链上传</a-button>
          </span>
          <a-upload
            v-else
            :file-list="fileList"
            :before-upload="beforeUpload"
            :customRequest="customRequest"
            directory
          >
            <a-button style="width: 100%">
              <a-icon type="upload" />上传文件夹
            </a-button>
          </a-upload>
          <div
            v-if="show"
            style="height: 30vw;border:0.05vw solid black;margin-top:1vh;overflow-x: auto !important;"
          >
            <a-tree
              class="draggable-tree"
              :treeData="fileTreeData"
              v-model="selectedFileTreeKeys"
              :checkable="true"
              @check="check"
              :default-expanded-keys="defaultKeys"
            />
          </div>
        </a-col>
        <a-col :span="18">
          <div style="height:30vw;margin-top:0vw;">
            <a-row :gutter="16">
              <a-col :span="1">
                <span style="width:100%"></span>
              </a-col>
              <a-col :span="6">
                <a-button type="primary" class="tag-description">已有标签库</a-button>
                <div class="tag-box" style="overflow-x: auto !important;">
                  <a-tree
                    class="tag-tree"
                    :treeData="tagTreeData"
                    v-model="selectedTagTreeKeys"
                    :checkable="true"
                    :defaultExpandAll="true"
                  />
                </div>
              </a-col>
              <a-col :span="6">
                <a-button type="primary" class="tag-description">输入新的标签</a-button>
                <div class="tag-box">
                  <a-textarea
                    v-model="textareaValue"
                    placeholder
                    style="height:26.8vw;min-height:5vw;max-height:26.8vw"
                  />
                </div>
              </a-col>
              <a-col :span="4">
                <div style="text-align:center" class="centered">
                  <a-button
                    type="primary"
                    style="width:8vw;margin-top:6vw;"
                    class="text-Omit"
                    @click="addTag"
                    title="添加标签至已选择文件"
                  >添加标签至已选择文件</a-button>
                </div>
                <div style="text-align:center" class="centered">
                  <a-button
                    type="danger"
                    style="width:8vw;margin-top:1vw;"
                    class="text-Omit"
                    @click="removeTag"
                    title="删除所选标签"
                  >删除所选标签</a-button>
                </div>
              </a-col>
              <a-col :span="6">
                <a-button type="primary" class="tag-description">目标文件已有标签</a-button>
                <div class="tag-box" style="overflow-x: auto !important;">
                  <a-tree
                    class="tag-chosen-tree"
                    :treeData="chosenTagTreeData"
                    v-model="selectedChosenTagTreeKeys"
                    :checkable="true"
                    :defaultExpandAll="true"
                  />
                </div>
              </a-col>
            </a-row>
          </div>
          <div style="margin-top:3vw;margin-left:5vw">
            <a-button type="primary" style="margin-left:15%;padding:0 4vw" @click="cancel">上一步</a-button>
            <a-button
              type="primary"
              style="margin-left:5%;padding:0 2vw"
              @click="folderGenerateMindMap"
            >以现有结构自动生成思维导图</a-button>
            <a-button
              type="primary"
              style="margin-left:5%;padding:0 4vw"
              @click="handleSubmit()"
            >下一步</a-button>
          </div>
        </a-col>
      </a-row>
    </a-card>
    <div class="example" v-if="spin">
      <a-spin />
    </div>
  </div>
</template>

<script>
import { GetTags, AddTags, AddFileTagRelations, AddJsMind } from '@/api/uploadFiles'
import { uploadFile, fileChain } from '@/api/file'
import { AddProjectSimple } from '@/api/createProject.js'
import { PostsJsMinds } from '@/api/selectTemplate'
export default {
  data () {
    return {
      current: 2,
      thisCurrent: 2,
      params: {},
      value: 1,
      loading: false,
      imageUrl: '',
      fileList: [],
      fileTreeData: [],
      show: true,
      selectedFileTreeKeys: [],
      defaultKeys: [],
      textareaValue: '',

      tagTreeDataInitial: [],
      tagTreeData: [],
      chosenTagTreeData: [],
      selectedTagTreeKeys: [],
      selectedChosenTagTreeKeys: [],
      files: [],
      uploadFiles: [],
      externalLink: '',
      projectId: -1,
      spin: false,
      fileTagRelationList: [],
      cc: {
        configTemplateBasicInfo: {
          NPV: 0.1,
          PROD: 0.2,
          PIR: 0.3,
          UTC: 0.4,
          keepExisting: 1,
          keepCommited: 0,
          keepAsset: 1,
          ROCE: -999,
          YSUDC: 7,
          hierarchy: '资产层面优化',
          templateName: 'aabb',
          versionSchemeCode: '2020hclA',
          remarks: 'aabb'
        },
        constraintConfigList: [
          {
            yearSequence: 0,
            capex: 1,
            product: -999,
            netProfit: -999,
            unitCost: -999,
            unitCompletionCost: -999
          },
          {
            yearSequence: 1,
            capex: -999,
            product: 2,
            netProfit: -999,
            unitCost: -999,
            unitCompletionCost: -999
          },
          {
            yearSequence: 2,
            capex: -999,
            product: -999,
            netProfit: 3,
            unitCost: -999,
            unitCompletionCost: -999
          },
          {
            yearSequence: 3,
            capex: -999,
            product: -999,
            netProfit: -999,
            unitCost: 4,
            unitCompletionCost: -999
          },
          {
            yearSequence: 4,
            capex: -999,
            product: -999,
            netProfit: -999,
            unitCost: -999,
            unitCompletionCost: 5
          },
          {
            yearSequence: 5,
            capex: -999,
            product: -999,
            netProfit: -999,
            unitCost: -999,
            unitCompletionCost: -999
          }
        ],
        logicList: [
          {
            subjectProjectCode: 'AGT.TAS.B3D.ENT.NFA000EXT',
            objectProjectCode: 'AGT.TAS.B3D.ENT.NFD000NEW',
            predicate: 'depend'
          },
          {
            subjectProjectCode: 'ANG.AGG.ENT.ENT.NFA000EXT',
            objectProjectCode: 'ANG.GSN.B21.ENT.NFA000EXT',
            predicate: 'exclude'
          },
          {
            subjectProjectCode: 'AUS.KSL.MIS.CHK.INF000NEW',
            objectProjectCode: 'AUS.KSL.MIS.CHK.NFA000EXT',
            predicate: 'depend'
          }
        ],
        requiredList: [
          {
            keepProjectCode: 'AGT.TAS.B3D.ENT.NFA000EXT'
          },
          {
            keepProjectCode: 'AGT.TAS.B3D.ENT.NFD000NEW'
          },
          {
            keepProjectCode: 'AGT.TAS.B3E.ENT.NFA000EXT'
          },
          {
            keepProjectCode: 'AGT.TAS.B3E.ENT.NFD000NEW'
          },
          {
            keepProjectCode: 'AGT.TAS.B7E.ENT.EXP000EXP'
          },
          {
            keepProjectCode: 'AGT.TAS.B7E.ENT.NFA000EXT'
          },
          {
            keepProjectCode: 'AGT.TAS.B7E.ENT.NFD000NEW'
          }
        ],
        portfolioScreenTemps: [
          {
            keepProjectCode: 'AGT.TAS.B3D.ENT.NFA000EXT'
          },
          {
            keepProjectCode: 'AGT.TAS.B3D.ENT.NFD000NEW'
          },
          {
            keepProjectCode: 'AGT.TAS.B3E.ENT.NFA000EXT'
          },
          {
            keepProjectCode: 'AGT.TAS.B3E.ENT.NFD000NEW'
          },
          {
            keepProjectCode: 'AGT.TAS.B7E.ENT.EXP000EXP'
          },
          {
            keepProjectCode: 'AGT.TAS.B7E.ENT.NFA000EXT'
          },
          {
            keepProjectCode: 'AGT.TAS.B7E.ENT.NFD000NEW'
          },
          {
            keepProjectCode: 'ANG.AGG.ENT.ENT.NFA000EXT'
          },
          {
            keepProjectCode: 'ANG.GSN.B21.ENT.NFA000EXT'
          },
          {
            keepProjectCode: 'AUS.KSL.MIS.CHK.INF000NEW'
          },
          {
            keepProjectCode: 'AUS.KSL.MIS.CHK.NFA000EXT'
          },
          {
            keepProjectCode: 'AUS.KSL.MIS.OBO.INF000NEW'
          },
          {
            keepProjectCode: 'AUS.KSL.MIS.OBO.NFA000EXT'
          },
          {
            keepProjectCode: 'BRA.PGB.S11.LUL.NFA000EXT'
          },
          {
            keepProjectCode: 'CAN.BSA.BBN.BEA.DEC000COM'
          },
          {
            keepProjectCode: 'CAN.BSA.BBN.BLH.NFA000EXT'
          },
          {
            keepProjectCode: 'CAN.BSA.BBN.BUC.DEC000EXT'
          },
          {
            keepProjectCode: 'CAN.BSA.BBN.NOC.NFA000EXT'
          },
          {
            keepProjectCode: 'CAN.BSA.CLA.CLA.INF001NEW'
          },
          {
            keepProjectCode: 'CAN.BSA.CLA.CLA.INF002NEW'
          }
        ]
      }
    }
  },
  components: { GetTags },
  watch: {
    fileTreeData () {
      this.$nextTick(() => {
        this.show = true
      })
    }
  },
  created () {
    const this_ = this
    this.getParams(() => {
      GetTags().then((res) => {
        if (res.data.length > 0) {
          res.data.forEach((tag) => {
            this_.tagTreeDataInitial.push({
              key: tag.tagId,
              title: tag.tagName
            })
          })
        }
        this_.tagTreeData = JSON.parse(JSON.stringify(this_.tagTreeDataInitial))
      })
    })
  },
  methods: {
    onChange (current) {
      if (this.thisCurrent >= current) {
        if (current === 0) {
          this.$router.push({ name: 'CreateProject', params: this.params })
        } else if (current === 1) {
          this.cancel()
        }
      }
    },
    getParams: function (callBack) {
      this.params = this.$route.params
      if (this.params.project) {
        if (this.params.jsMind) {
          if (callBack && Object.prototype.toString.call(callBack) === '[object Function]') {
            callBack()
          }
        } else {
          this.$destroy('UploadFiles')
          this.cancel()
        }
      } else {
        this.$destroy('UploadFiles')
        this.$router.push({ name: 'CreateProject', params: this.params })
      }
    },
    cancel () {
      this.$router.push({ name: 'SelectTemplate', params: this.params })
    },
    addProject (callBack) {
      const this_ = this
      if (this.params.project !== undefined && this.projectId === -1) {
        AddProjectSimple(this_.params.project).then((project) => {
          this_.projectId = project.data.projectId
          if (callBack) {
            callBack()
          }
        })
      } else {
        if (callBack) {
          callBack()
        }
      }
    },
    handleSubmit (callBack) {
      if (this.params.project !== undefined) {
        const this_ = this
        this.spin = true
        this.addProject(() => {
          this_.handleSubmitMethod(callBack)
        })
      }
    },
    handleSubmitMethod (callBack) {
      const this_ = this
      if (this_.params.jsMind !== undefined) {
        this_.params.jsMind.projectId = this_.projectId
        PostsJsMinds(this_.params.jsMind).then((jsMind) => {
          if (this_.uploadFiles && this_.uploadFiles.length > 0) {
            let flag = false
            if (callBack) {
              flag = true
            }
            if (this_.uploadFiles[0].fileId !== undefined) {
              this_.fileTagRelationList = []
              this_.tagsRecursiveProcessing(this_.fileTreeData[0], this_.uploadFiles, '', flag)
              console.log(this_.fileTagRelationList)
              AddFileTagRelations(this_.fileTagRelationList).then((res) => {
                if (callBack) {
                  callBack()
                } else {
                  this_.spin = false
                  this_.$router.push({ name: 'MindMap', query: { projectId: this_.projectId } })
                }
              })
            } else {
              const formData = new FormData()
              formData.append('projectId', this_.projectId)
              this_.uploadFiles.forEach((file) => {
                formData.append('files', file)
              })
              this_.uploading = true
              uploadFile(formData)
                .then((res) => {
                  if (res.success) {
                    const successFileInfo = res.data.success
                    this_.fileTagRelationList = []
                    this_.tagsRecursiveProcessing(this_.fileTreeData[0], successFileInfo, '', flag)
                    console.log(this_.fileTagRelationList)
                    AddFileTagRelations(this_.fileTagRelationList).then((res) => {
                      if (callBack) {
                        callBack()
                      } else {
                        this_.spin = false
                        this_.$router.push({ name: 'MindMap', query: { projectId: this_.projectId } })
                        this_.$message.success('上传完成')
                      }
                    })
                  }
                })
                .catch((error) => {
                  this_.$message.error('上传异常', error)
                })
            }
          } else {
            if (callBack) {
              this_.$message.error('请上传文件~')
            } else {
              this_.spin = false
              this_.$router.push({ name: 'MindMap', query: { projectId: this_.projectId } })
            }
          }
        })
      }
    },
    tagsRecursiveProcessing (files, fileInfos, filePath, flag) {
      if (files.children !== undefined && files.children.length > 0) {
        if (filePath === '') {
          filePath += files.title
        } else {
          filePath += '/' + files.title
        }
        const this_ = this
        files.children.forEach((file) => {
          if (file.tags.length > 0 || flag) {
            this_.tagsProcessing(file, filePath, fileInfos)
          }
          this.tagsRecursiveProcessing(file, fileInfos, filePath, flag) //  + '/' + file.title
        })
      }
      // else {
      //   console.log(this.fileTagRelationList)
      //   AddFileTagRelations(this.fileTagRelationList).then((res) => {})
      // }
    },
    tagsProcessing (file, filePath, fileInfos) {
      filePath += '/' + file.title
      for (let i = 0; i < fileInfos.length; i++) {
        if (fileInfos[i].filePath.indexOf(filePath) > -1) {
          file.fileId = fileInfos[i].fileId
          if (file.tags.length > 0) {
            const fileTagRelation = []
            file.tags.forEach((tag) => {
              fileTagRelation.push({
                fileId: fileInfos[i].fileId,
                tagId: tag.key
              })
            })
            this.fileTagRelationList = this.fileTagRelationList.concat(fileTagRelation)
          }
          break
        }
      }
    },
    // 树状列表
    beforeUpload (file, fileList) {
      // this.spin = true
      if (
        (file.uid !== undefined && file.uid === fileList[0].uid) ||
        (file.fileId !== undefined && file.fileId === fileList[0].fileId)
      ) {
        this.fileTreeData = []
        this.uploadFiles = fileList
      }
      this.show = false
      const pathArray =
        file.webkitRelativePath === undefined ? file.filePath.split('/') : file.webkitRelativePath.split('/')
      let findIndex = -1
      for (let i = 0; i < this.fileTreeData.length; i++) {
        if (this.fileTreeData[i].key1 === pathArray[0]) {
          findIndex = i
          break
        }
      }
      if (findIndex === -1) {
        // 如果没有该文件路径的根目录 则创建
        findIndex = this.fileTreeData.length
        this.$set(this.fileTreeData, findIndex, {
          key1: pathArray[0],
          key: findIndex,
          title: pathArray[0],
          children: [],
          tags: []
        })
      }
      this.buildFileTreePosition(pathArray, 1, this.fileTreeData[findIndex], [findIndex]) // 建立文件数位置
      // if (
      //   (file.uid !== undefined && file.uid === fileList[fileList.length - 1].uid) ||
      //   (file.fileId !== undefined && file.fileId === fileList[fileList.length - 1].fileId)
      // ) {
      // }
    },
    buildFileTreePosition (pathArray, index, fileTrees, indexArray) {
      const path = pathArray[index]
      let findFileTree = null
      let keyIndex = -1
      for (const fileTree of fileTrees.children) {
        keyIndex++
        if (fileTree.key1 === path) {
          findFileTree = fileTree
          break
        }
      }
      if (findFileTree === null) {
        keyIndex = fileTrees.children.length
        let needKey = ''
        for (let i = 0; i < indexArray.length; i++) {
          needKey += '/' + indexArray[i]
        }
        findFileTree = {
          key1: pathArray[index],
          key: needKey.substring(1) + '/' + keyIndex,
          title: pathArray[index],
          children: [],
          tags: []
        }
        if (findFileTree.key.split('/').length <= 2) {
          this.defaultKeys.push(findFileTree.key)
        }
        this.$set(fileTrees.children, keyIndex, findFileTree)
      }
      if (index < pathArray.length - 1) {
        indexArray.push(keyIndex)
        index++
        this.buildFileTreePosition(pathArray, index, findFileTree, indexArray)
      }
    },
    customRequest (uploadObject) {
      // 插件的上传拦截，等点击下一步的时候开始上传
      // console.log(uploadObject)
    },
    check () {
      this.files = []
      let tags = []
      for (const keys of this.selectedFileTreeKeys) {
        if (keys === 0) {
          continue
        }
        const key = keys.split('/')
        let file = null
        for (let i = 0; i < key.length; i++) {
          if (i === 0) {
            file = this.fileTreeData[key[i]]
          } else {
            file = file.children[key[i]]
          }
        }
        if (file.children.length === 0) {
          this.files.push(file)
          if (tags.length === 0) {
            tags = file.tags
          } else {
            tags = tags.filter((tag) => file.tags.findIndex((fileTag) => tag.key === fileTag.key) > -1)
          }
        }
      }
      this.chosenTagTreeData = JSON.parse(JSON.stringify(tags))
      if (tags.length === 0) {
        this.tagTreeData = JSON.parse(JSON.stringify(this.tagTreeDataInitial))
      } else {
        this.tagTreeData = this.tagTreeDataInitial.filter(
          (tagTree) => tags.findIndex((tag) => tag.key === tagTree.key) === -1
        )
      }
    },

    // 穿梭框
    addTag () {
      if (this.files.length > 0) {
        // const needFiles = this.files.filter(file => file.children.length === 0)
        const this_ = this
        this.pendingBefore(() => {
          this_.pending(this_.tagTreeData, this_.chosenTagTreeData, this_.selectedTagTreeKeys, (addTags) => {
            this_.files.forEach((file) => {
              if (file.children.length === 0) {
                addTags.forEach((addTag) => {
                  const index = file.tags.findIndex((tag) => tag.key === addTag.key)
                  if (index === -1) {
                    file.tags.push(addTag)
                  }
                })
                file.tags.sort((a, b) => {
                  return a.key - b.key
                })
              }
            })
          })
        })
      } else {
        this.$message.error('请选择文件')
      }
    },
    pendingBefore (callBack) {
      const this_ = this
      if (this.textareaValue !== '') {
        AddTags({ tagName: this.textareaValue }).then((res) => {
          const newTag = {
            key: res.data.tagId,
            title: res.data.tagName
          }
          this_.tagTreeDataInitial.push(newTag)
          this.tagTreeData.push(newTag)
          this.selectedTagTreeKeys.push(res.data.tagId)
          if (callBack) {
            callBack()
          }
        })
        this.textareaValue = ''
      } else {
        if (callBack) {
          callBack()
        }
      }
    },
    removeTag () {
      this.pending(this.chosenTagTreeData, this.tagTreeData, this.selectedChosenTagTreeKeys, (removeTags) => {
        this.files.forEach((file) => {
          removeTags.forEach((removeTag) => {
            const index = file.tags.findIndex((tag) => tag.key === removeTag.key)
            if (index !== -1) {
              file.tags.splice(index, 1)
            }
          })
        })
      })
    },
    pending (array1, array2, keys, callBack) {
      let indexs = []
      const tags = []
      keys.forEach((key) => {
        for (let i = 0; i < array1.length; i++) {
          if (array1[i].key === key) {
            indexs.push(i)
            tags.push(array1[i])
            array2.push(array1[i])
          }
        }
      })
      if (callBack) {
        callBack(tags)
      }
      array2 = array2.sort((a, b) => {
        return a.key - b.key
      })
      indexs = indexs.sort((a, b) => {
        return b - a
      })
      for (let i = 0; i < indexs.length; i++) {
        array1.splice(indexs[i], 1)
      }
      this.removeArrayByArray(keys, JSON.parse(JSON.stringify(keys)))
    },
    removeArrayByArray (array1, array2) {
      for (let i = array2.length - 1; i >= 0; i--) {
        this.$delete(array1, i)
      }
    },
    externalLinkUpload (externalLink) {
      if (externalLink) {
        const this_ = this
        this.spin = true
        this.addProject(() => {
          fileChain({ paths: externalLink }, this.projectId).then((res) => {
            if (res.data) {
              const index = externalLink.lastIndexOf('\\')
              res.data.sort((a, b) => {
                return a.filePath.match(RegExp(/\\/g)).length - b.filePath.match(RegExp(/\\/g)).length
              })
              res.data.forEach((file) => {
                file.filePath = file.filePath.substring(index + 1).replace(/\\/g, '/')
                this_.beforeUpload(file, res.data)
              })
            }
            this.spin = false
          })
        })
      } else {
        this.$message.error('请输入本地文件夹路径~')
      }
    },
    folderGenerateMindMap () {
      const this_ = this
      this.spin = true
      this.handleSubmit(() => {
        const mindData = JSON.parse(JSON.stringify(this_.fileTreeData[0]))
        this_.getMindDataByFileTreeData(mindData)
        const date = new Date()
        const user = '管理员'
        var mind = {
          /* 元数据，定义思维导图的名称、作者、版本等信息 */
          meta: {
            name: 'jsMind-' + date.getTime(),
            author: user,
            version: '0.1'
          },
          /* 数据格式声明 */
          format: 'node_tree',
          /* 数据内容 */
          data: mindData
        }
        const jsMind = {
          projectId: this.projectId,
          dimensionId: this.params.jsMind.dimensionId,
          mindMapName: mindData.topic,
          format: 'node_tree',
          data: mind,
          theme: 'primary',
          mode: '',
          support_html: false,
          editable: true,
          createTime: date,
          updateTime: date,
          createUser: user,
          updateUser: user,
          isTemplate: false
        }
        AddJsMind(jsMind).then((res) => {
          this.spin = false
          this_.$router.push({ name: 'MindMap', query: { projectId: this_.projectId } })
        })
      })
    },
    getMindDataByFileTreeData (files, parent) {
      if (files.children !== undefined) {
        files.id = files.key
        files.topic = files.title
        for (const field in files) {
          if (field !== 'id' && field !== 'topic' && field !== 'children' && field !== 'fileId') {
            delete files[field]
          }
        }
        files.fileIds = []
        files.childrenIndex = []
        files.expanded = 'true'
        if (files.children.length > 0) {
          files.children.forEach((nextFiles) => {
            this.getMindDataByFileTreeData(nextFiles, files)
          })
          if (files.fileIds && files.fileIds.length > 0) {
            files.topic += '(' + files.fileIds.length + ')'
            files.childrenIndex.sort((a, b) => {
              return b - a
            })
            files.childrenIndex.forEach((index) => {
              files.children.splice(index, 1)
            })
          }
        } else {
          if (parent) {
            parent.fileIds.push(files.fileId)
            const index = parent.children.findIndex((par) => par.id === files.id)
            parent.childrenIndex.push(index)
          }
          // delete files.fileId
        }
      }
    }
  }
}
</script>
<style>
.tag-description {
  height: 3vw;
  background-color: #ececec;
  text-align: center;
  line-height: 3vw;
  width: 100%;
}
.tag-box {
  height: 27vw;
  border: 0.05vw solid black;
}
.ant-upload {
  width: 100%;
}
.example {
  text-align: center;
  line-height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  width: 100vw;
  height: 100vh;
  position: absolute;
  z-index: 10;
  top: 0;
  left: 0;
}
.text-Omit {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.centered {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
