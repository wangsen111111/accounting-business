<template>
  <div ref="templateDiv" style="overflow-x: hidden; overflow-y: auto;">
    <a-card :bordered="false" style="min-height: calc(100vh - 100px)">
      <span class="centered">
        <a-steps
          style="width:50vw;height:7vh;"
          class="centered"
          :current="current"
          @change="onChange"
        >
          <a-step title="新建项目" description />
          <a-step title="选择模板" description />
          <a-step title="上传文件" description />
        </a-steps>
      </span>
      <a-divider />
      <a-form :form="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 12 }">
        <div>
          <span>维度：&nbsp;&nbsp;</span>
          <a-radio-group v-model="dimensionValue">
            <a-radio-button
              v-for="(dimension, index) in dimensions"
              :key="index"
              :value="dimension.dimensionId"
            >{{ dimension.dimensionName }}</a-radio-button>
          </a-radio-group>
          <span style="margin-left:2vw">思维导图名称：</span>
          <a-input v-model="name" style="width: 10vw" />
          <div class="contentStyle">
            <a-card>
              <a-card
                style="width:550px;"
                :class="'' === selectName ? 'selectStyle': 'defaultStyle'"
                @click="selectTemplate('', 0)">
                <img
                  style="height:350px"
                  slot="cover"
                  alt="example"
                  src="../../../public/blankTemplate.png"
                />
                <a-card-meta title="空白模板" description style="text-align:center"></a-card-meta>
              </a-card>
            </a-card>
            <a-card
              style="margin:20px 0px"
              :bodyStyle="{
                'width':'99%',
                'overflow-x':'scroll',
                'white-space':'nowrap',
                'padding':'20px'
              }"
              v-if="jsMindSimpleData[dimensionValue] && jsMindSimpleData[dimensionValue].length > 0"
            >
              <a-card
                style="overflow:hidden;display:inline-block;margin-right:10px"
                :class="'template' === selectName && index === selectIndex ? 'selectStyle': 'defaultStyle'"
                v-for="(template, index) in jsMindSimpleData[dimensionValue]"
                :key="index"
                @click="selectTemplate('template', index)"
              >
                <JsMindSimple
                  style="display:flex;"
                  slot="cover"
                  v-if="show_jsMindMap"
                  :jsMindId="template.id"
                  :jsMindMapData="template"
                  :className="'jsmind_container_no_scroll_axis'"
                  :styleSet="{ width: '550px', height: '350px'}"
                ></JsMindSimple>
                <a-card-meta
                  :title="template.mindMapName"
                  description
                  style="text-align:center"
                ></a-card-meta>
              </a-card>
            </a-card>
            <a-card
              :bodyStyle="{
                'width':'99%',
                'overflow-x':'scroll',
                'white-space':'nowrap',
                'padding':'20px'
              }"
              v-if="jsMindSimpleProjects[dimensionValue] && jsMindSimpleProjects[dimensionValue].length > 0"
            >
              <a-card
                v-for="(project, index) in jsMindSimpleProjects[dimensionValue]"
                :key="index"
                style="overflow:hidden;display:inline-block;margin-right:10px"
                :class="'project' === selectName && index === selectIndex ? 'selectStyle': 'defaultStyle'"
                @click="selectTemplate('project', index)"
              >
                <JsMindSimple
                  style="display:flex"
                  slot="cover"
                  v-if="show_jsMindMap"
                  :jsMindId="project.id"
                  :jsMindMapData="project"
                  :className="'jsmind_container_no_scroll_axis'"
                  :styleSet="{ width: '550px', height: '350px'}"
                ></JsMindSimple>
                <a-card-meta :title="project.mindMapName" description style="text-align:center"></a-card-meta>
              </a-card>
            </a-card>
          </div>
        </div>
        <a-divider />
        <span style="100vw" class="centered">
          <a-button type="primary" @click="cancel">上一步</a-button>
          <a-button type="primary" style="margin-left:5%" @click="handleSubmit">下一步</a-button>
        </span>
      </a-form>
    </a-card>
    <div v-if="visible">
      <a-modal v-model="visible" title="思维导图详情" width="90">
        <template slot="footer">
          <a-button key="back" @click="handleCancel">Return</a-button>
        </template>
        <slot name="modelContents">
          <div style="height:30vw;">
            <JsMindSimple
              :styleSet="{ width: '100%', height: '100%' }"
              :jsMindId="selectJsMind.id"
              :jsMindMapData="selectJsMind"
            ></JsMindSimple>
          </div>
        </slot>
      </a-modal>
    </div>
  </div>
</template>

<script>
import { GetDimensions, GetJsMinds } from '@/api/selectTemplate'
import JsMindSimple from '@/components/content/jsmind/jsMindSimple.vue'
export default {
  name: 'SelectTemplate',
  components: { JsMindSimple },
  data () {
    return {
      current: 1,
      thisCurrent: 1,
      params: {},
      formLayout: 'horizontal',
      form: this.$form.createForm(this, { name: 'coordinated' }),
      dimensionValue: 1,
      selectIndex: 0,
      selectName: '',
      dimensions: [],
      projects: [],
      jsMindProjects: {},
      jsMindSimpleProjects: {},
      jsMindData: {},
      jsMindSimpleData: {},
      show_jsMindMap: true,
      visible: false,
      selectJsMind: {},
      name: '',
      clickJsMind: {}
    }
  },
  created () {
    const this_ = this
    this.getParams(() => {
        GetDimensions().then((res) => {
          this_.dimensions = res.data
          this_.dimensions.forEach((item) => {
            this_.$set(this_.jsMindData, item.dimensionId, [])
            this_.$set(this_.jsMindSimpleData, item.dimensionId, [])
            this_.$set(this_.jsMindProjects, item.dimensionId, [])
            this_.$set(this_.jsMindSimpleProjects, item.dimensionId, [])
          })
          this_.dimensionValue = this_.dimensions[0].dimensionId
          this_.init()
        })
    })
  },
  methods: {
    onChange (current) {
      if (this.thisCurrent >= current) {
        this.cancel()
      }
    },
    getParams (callBack) {
      this.params = this.$route.params
      if (this.params.project) {
        if (callBack && Object.prototype.toString.call(callBack) === '[object Function]') {
          callBack()
        }
      } else {
        this.$destroy('SelectTemplate')
        this.$router.push({ name: 'CreateProject', params: this.params })
      }
    },
    init () {
      this.show_jsMindMap = false

      GetJsMinds().then((res) => {
        if (res.data) {
          const this_ = this
          res.data.forEach((jsMind) => {
            if (jsMind.dimensionId !== undefined) {
              if (jsMind.isTemplate) {
                this_.$set(this_.jsMindData[jsMind.dimensionId], this_.jsMindData[jsMind.dimensionId].length, jsMind)
                const simpleJsMind = JSON.parse(JSON.stringify(jsMind))
                simpleJsMind.id += 'simple'
                const simpleData = []
                for (let i = 0; i < 5; i++) {
                  const child = simpleJsMind.data.data.children
                  if (child.length <= i) {
                    break
                  }
                  const one = child[i]
                  if (one.children !== undefined) {
                    one.children = []
                  }
                  simpleData.push(one)
                }
                simpleJsMind.data.data.children = simpleData
                this_.$set(
                  this_.jsMindSimpleData[jsMind.dimensionId],
                  this_.jsMindSimpleData[jsMind.dimensionId].length,
                  simpleJsMind
                )
              } else {
                // jsMindSimpleProjects
                this_.$set(
                  this_.jsMindProjects[jsMind.dimensionId],
                  this_.jsMindProjects[jsMind.dimensionId].length,
                  jsMind
                )
                const simpleJsMindObject = JSON.parse(JSON.stringify(jsMind))
                simpleJsMindObject.id += 'simple'
                const simpleDataObject = []
                for (let j = 0; j < 5; j++) {
                  const childObject = simpleJsMindObject.data.data.children
                  if (!childObject || childObject.length <= j) {
                    break
                  }
                  const oneObject = childObject[j]
                  if (oneObject.children !== undefined) {
                    oneObject.children = []
                  }
                  simpleDataObject.push(oneObject)
                }
                simpleJsMindObject.data.data.children = simpleDataObject
                this_.$set(
                  this_.jsMindSimpleProjects[jsMind.dimensionId],
                  this_.jsMindSimpleProjects[jsMind.dimensionId].length,
                  simpleJsMindObject
                )
              }
            }
          })
        }
      })
    },
    cancel () {
      this.$router.push({ name: 'CreateProject', params: this.params })
    },
    handleSubmit (e) {
      if (this.name !== '') {
        if (this.clickJsMind.id === undefined) {
          const mindData = {
            id: 'root',
            topic: this.name,
            children: []
          }
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
          const mindMap = {
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
          this.clickJsMind = mindMap
        }
        const jsMindCopy = JSON.parse(JSON.stringify(this.clickJsMind))
        const processData = jsMindCopy.data.data
        if (processData) {
          if (jsMindCopy.format === 'node_tree') {
            this.recursionFileIds(processData)
          } else if (jsMindCopy.format === 'node_array') {
            const this_ = this
            processData.forEach(dataOne => {
              this_.recursionFileIds(dataOne)
            })
          }
        }
        jsMindCopy.mindMapName = this.name
        jsMindCopy.dimensionId = this.dimensionValue

        this.params.jsMind = jsMindCopy
        this.$router.push({ name: 'UploadFiles', params: this.params })
      } else {
        this.$message.error('请输入思维导图名称~')
      }
    },
    recursionFileIds (processData) {
      if (processData.fileIds !== undefined) {
        delete processData.fileIds
      }
      if (processData.children) {
        const this_ = this
        processData.children.forEach(dataOne => {
          this_.recursionFileIds(dataOne)
        })
      }
    },
    selectTemplate (name, index) {
      if (name === 'template') {
        this.clickJsMind = this.jsMindData[this.dimensionValue][index]
      } else if (name === 'project') {
        this.clickJsMind = this.jsMindProjects[this.dimensionValue][index]
      } else {
        this.clickJsMind = {}
      }
      if (name === this.selectName && index === this.selectIndex) {
        if (name === 'template') {
          this.selectJsMind = this.jsMindData[this.dimensionValue][this.selectIndex]
        } else if (name === 'project') {
          this.selectJsMind = this.jsMindProjects[this.dimensionValue][this.selectIndex]
        } else {
          this.selectJsMind = {}
        }
        this.visible = true
      } else {
        this.selectName = name
        this.selectIndex = index
      }
      if (this.selectJsMind.id === undefined) {
        this.visible = false
      }
    },
    handleCancel (e) {
      this.visible = false
    }
  },
  watch: {
    // 监测路由变化，只要变化了就调用路由参数方法将数据存储本组件即可
    // $route: 'getParams',
    jsMindData () {
      this.$nextTick(() => {
        this.show_jsMindMap = true
      })
    },
    dimensionValue () {
      this.show_jsMindMap = false
      this.selectIndex = 0
      this.$nextTick(() => {
        this.show_jsMindMap = true
      })
    }
  }
}
</script>
<style>
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
