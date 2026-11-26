<template>
  <div >
    <a-row style="height: 100%">
      <a-col :span="3">
        <div style="background-color: #ececec;  padding: 20px 20px 20px 20px;height: calc(100vh - 64px)">
          <a-row :gutter="16">
            <a-col >
              <a-card title="项目库" :bordered="false" style="height:calc(33vh - 45px)">
                <div style="height:calc(17vh);">
                  <happy-scroll size="5" resize >
                    <div class="con">
                      <div
                        v-for="(item,index) in projectAndFileCount"
                        :key="index"
                        style="font-size:16px"
                        class="text-overflow"
                        :title="item.projectName + '('+item.fileCount+')'">
                        {{ item.projectName }} {{ '('+item.fileCount+')' }}
                      </div>
                    </div>
                  </happy-scroll>
                </div>
              </a-card>
            </a-col>
            <a-col style="background-color: #ececec; margin-top:20px;">
              <a-card title="课题成员" :bordered="false" style="height:calc(33vh - 45px)">
                <div style="height:calc(17vh);">
                  <happy-scroll size="5" resize>
                    <Members></Members>
                  </happy-scroll>
                </div>
              </a-card>
            </a-col>
            <a-col style="background-color: #ececec; margin-top:20px;">
              <a-card title="关注标签" :bordered="false" style="height:calc(33vh - 45px);">
                <div style="height:calc(17vh);">
                  <happy-scroll size="5" resize>
                    <HotTag></HotTag>
                  </happy-scroll>
                </div>
              </a-card>
            </a-col>
          </a-row>
        </div>
      </a-col>
      <a-col :span="12" >
        <div style="background-color: #ececec; padding: 20px 0px; ">
          <a-spin :spinning="spinning" >
            <a-tabs v-model="currDimension" @change="changeMindMap" tab-position="bottom">
              <a-tab-pane key="-1">
                <span slot="tab"> <a-icon type="android" /> 空维度 </span>
                <div style="width: calc(50wh - 0px); height: calc(100vh - 165px);">
                  <JsMindMap v-if="jsMindMapData && (currDimension == '-1')" :jsMindMapData="jsMindMapData" :jsMindId="jsMindMapData.id" :styleCss="{ width: '100%', height: 'calc(100vh - 165px)'}"></JsMindMap>
                </div>
              </a-tab-pane>
              <a-tab-pane :key="dimension.dimensionId" v-for="dimension in dimensions" >
                <span slot="tab">
                  <a-icon type="apple" />{{ dimension.dimensionName }}</span>
                <div style="width: calc(50wh - 0px); height: calc(100vh - 165px);">
                  <JsMindMap v-if="jsMindMapData && (currDimension == dimension.dimensionId)" :jsMindMapData="jsMindMapData" :jsMindId="jsMindMapData.id" :styleCss="{ width: '100%', height: 'calc(100vh - 165px)'}"></JsMindMap>
                </div>
              </a-tab-pane>
            </a-tabs>
          </a-spin>
        </div>
      </a-col>
      <a-col :span="5" >
        <div style="background-color: #ececec;padding: 20px 20px;">
          <div >
            <WordCloud v-if="wordCloudData" id="wordClould" :wordCloudData="wordCloudData" height="calc(50vh - 65px)"></WordCloud>
          </div>
          <div style="margin-top:20px;">
            <PieChart v-if="wordCloudData" :pieChartData="wordCloudData" id="All_Knowledge_base_statistics" :styleCss="{ width: '100%', height: 'calc(50vh - 65px)', background:' #f1f2f6' }"></PieChart>
          </div>
        </div>
      </a-col>
      <a-col :span="4" style="height: 100%">
        <div style="background-color: #ececec; padding: 20px 20px 20px 0px;">
          <Histogram v-if="HistogramOption" :option="HistogramOption" id="projectSchedule" :styleCss="{width: '100%', height: 'calc(100vh - 110px)', background:' #f1f2f6'}"></Histogram>
        </div>
      </a-col>

    </a-row>
  </div>
</template>

<script>
import { getHotTagWithFileCount, getProjectsAndFileCount } from '@/api/indexPage'
import JsMindMap from '@/components/content/jsmind/jsmind'
import Members from '@/components/content/index/Members'
import HotTag from '@/components/content/index/HotTag'
import WordCloud from '@/components/ECharts/WordCloud'
import RadarMap from '@/components/ECharts/RadarMap'
import Histogram from '@/components/ECharts/Histogram'
import PieChart from '@/components/ECharts/PieChart'
import TableList from '@/components/content/index/tableList'
import { Chrome } from 'vue-color'

// import 'jsmind/style/jsmind.css'
// import jsMind from 'jsmind'
// import jsMind from '@/assets/jsmind/js/jsmind.js'
import Vue from 'vue'

import contentmenu from 'v-contextmenu'
import 'v-contextmenu/dist/index.css'

import { getDimensions } from '@/api/dimension'
import { getJsmindBydimensionId } from '@/api/mindMap'
// window.jsMind = jsMind
// require('jsmind/js/jsmind.draggable.js')
require('@/assets/jsmind/js/jsmind.draggable.js')
Vue.use(contentmenu)
export default {
  components: {
    Chrome,
    TableList,
    Histogram,
    PieChart,
    RadarMap,
    WordCloud,
    HotTag,
    Members,
    JsMindMap
  },
  watch: {
    fontColor (newVlaue, oldValue) {
      this.jm.set_node_color(this.jm.get_selected_node().id, null, newVlaue.hex)
    }
  },
  computed: {
    currDimension2 () {
      console.log(this.currDimension)
      return false
    },
    spinning () {
      if (this.jsMindMapData == null) {
        return true
      } else {
        return false
      }
    }
  },
  data () {
    return {
      tagAndFileCount: [],
      // spinning: false,
      currDimension: '-1',
      dimensions: [],
      jsMindMapData: null,
      isShowMindMap: false,
      tabPosition: 'top',
      wordCloudData: null,
      projectAndFileCount: null,
      HistogramOption: null
    }
  },
  mounted: function () {
    // 获取维度
    getDimensions().then(res => {
      this.dimensions = res.data
    })
    // 获取热度名和数量
    getHotTagWithFileCount().then(res => {
      this.wordCloudData = res.data
    }).catch(err => {
      console.log(err)
    })
    // 获取项目和文件数
    getProjectsAndFileCount().then(res => {
        this.projectAndFileCount = res.data
        const yAxisDateArr = res.data.map(item => item.projectName)
        const seriesDataArr = res.data.map(item => item.progressOf)
        // this.HistogramOption.yAxis.data = yAxisDateArr
        // this.HistogramOption.series.data = seriesDataArr
          this.HistogramOption = {
              title: {
                  text: '进度'
              },
              tooltip: {
                  trigger: 'axis',
                  axisPointer: {
                      type: 'shadow'
                  }
              },
              legend: {
                  data: ['项目']
              },
              grid: {
                  left: '3%',
                  right: '4%',
                  bottom: '3%',
                  containLabel: true
              },
              xAxis: {
                  type: 'value',
                  max: 100, // 设置最大值
                  min: 0,
                  boundaryGap: [0, 0.01]
              },
              yAxis: {
                  type: 'category',
                  axisLabel: {
                    interval: 0,
                    rotate: 90
                  },
                  data: yAxisDateArr
              },
              series: [
                  {
                      name: '项目',
                      type: 'bar',
                      data: seriesDataArr
                  }
              ]
          }
      })
    // 获取思维导图
    this.jsMindMapData = null
    getJsmindBydimensionId(this.currDimension).then(res => {
        if (res.success) {
          this.jsMindMapData = res.data
        }
    })
    getHotTagWithFileCount().then(res => {
            res.data.forEach(item => this.tagAndFileCount.push(item.name + ' (' + item.value + ')'))
          }).catch(err => { console.log(err) })
  },
  methods: {
    changeMindMap (key) {
      // 请求数据
      this.jsMindMapData = null
      getJsmindBydimensionId(this.currDimension).then(res => {
        if (res.success) { this.jsMindMapData = res.data }
      })
    }

  }
}
</script>

<style scoped>
.card-container {
  background: #f5f5f5;
  overflow: hidden;
  padding: 24px;
}
.card-container > .ant-tabs-card > .ant-tabs-content {
  height: 120px;
  margin-top: -16px;
}

.card-container > .ant-tabs-card > .ant-tabs-content > .ant-tabs-tabpane {
  background: #fff;
  padding: 16px;
}

.card-container > .ant-tabs-card > .ant-tabs-bar {
  border-color: #fff;
}

.card-container > .ant-tabs-card > .ant-tabs-bar .ant-tabs-tab {
  border-color: transparent;
  background: transparent;
}

.card-container > .ant-tabs-card > .ant-tabs-bar .ant-tabs-tab-active {
  border-color: #fff;
  background: #fff;
}
.text-overflow{
  margin-top:0px;
  display: -webkit-box;/*作为弹性伸缩盒子模型显示*/
  -webkit-line-clamp: 1; /*显示的行数；如果要设置2行加...则设置为2*/
  overflow: hidden; /*超出的文本隐藏*/
  text-overflow: ellipsis; /* 溢出用省略号*/
  -webkit-box-orient: vertical;/*伸缩盒子的子元素排列：从上到下*/
}
</style>
