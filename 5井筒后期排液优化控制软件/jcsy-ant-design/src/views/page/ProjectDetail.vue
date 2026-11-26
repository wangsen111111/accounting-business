<template>
  <div style="height: calc(100vh - 265px)">
    <a-row >
      <a-col :span="4" >
        <a-card style="height: calc(100vh - 65px)">
          <a-row v-if="projectBaseInfo">
            <h3>项目名称：</h3>
            <a-input :default-value="projectBaseInfo.projectName" disabled="disabled" />
          </a-row>
          <a-row v-if="projectBaseInfo">
            <h3>负责人:</h3>
            <a-input :default-value="projectBaseInfo.createUser" disabled="disabled" />
          </a-row>
          <a-row v-if="projectBaseInfo">
            <h3>项目周期：</h3>
            <a-range-picker v-model="projectBaseInfo.startEndTime" valueFormat="YYYY-MM-DD" style="width: 100%" disabled="disabled"> </a-range-picker>
          </a-row>
          <a-row v-if="projectBaseInfo">
            <h3>项目简介：</h3>
            <a-textarea
              style="background: #FFFFFF;font-color: #000000"
              :default-value="projectBaseInfo.note"
              disabled="disabled"
              :auto-size="{ minRows: 10, maxRows: 18 }" />
          </a-row>
          <a-row v-if="projectBaseInfo" style="margin-top:2px">
            <a-col :span="12" style="text-align: right;"><h3>项目进度:</h3></a-col><a-col :span="2"></a-col>
            <a-col :span="10"><a-input :default-value="projectBaseInfo.progressOf" disabled="disabled" style="text-align: center;" /></a-col>
          </a-row>
          <a-row v-if="projectBaseInfo" style="margin-top:2px">
            <a-col :span="12" style="text-align: right;"><h3>思维导图数:</h3></a-col><a-col :span="2"></a-col>
            <a-col :span="10"><a-input :default-value="projectBaseInfo.jsmindCount" disabled="disabled" style="text-align: center;" /></a-col>
          </a-row>
          <a-row v-if="projectBaseInfo" style="margin-top:2px">
            <a-col :span="12" style="text-align: right;"><h3>文件总数:</h3></a-col><a-col :span="2"></a-col>
            <a-col :span="10"><a-input :default-value="projectBaseInfo.fileCount" disabled="disabled" style="text-align: center;" /></a-col>
          </a-row>
          <a-row v-if="projectBaseInfo" style="margin-top:2px">
            <a-col :span="12" style="text-align: right;"><h3>已挂载文件数:</h3></a-col><a-col :span="2"></a-col>
            <a-col :span="10"><a-input :default-value="projectBaseInfo.mountOkCount" disabled="disabled" style="text-align: center;" /></a-col>
          </a-row>
          <a-row v-if="projectBaseInfo" style="margin-top:2px">
            <a-col :span="12" style="text-align: right;"><h3>未挂载文件数:</h3></a-col><a-col :span="2"></a-col>
            <a-col :span="10"><a-input :default-value="projectBaseInfo.mountNoCount" disabled="disabled" style="text-align: center;" /></a-col>
          </a-row>
          <a-row v-if="projectBaseInfo" style="margin-top:2px">
            <a-col :span="12" style="text-align: right;"><h3>标签标记文件数:</h3></a-col><a-col :span="2"></a-col>
            <a-col :span="10"><a-input :default-value="projectBaseInfo.tagFileCount" disabled="disabled" style="text-align: center;" /></a-col>
          </a-row>
        </a-card>
      </a-col>
      <a-col :span="20">
        <a-card
          style="width:100%; "
          :tab-list="tabListNoTitle"
          :active-tab-key="noTitleKey"
          @tabChange="key => onTabChange(key, 'noTitleKey')" >
          <div v-if="noTitleKey === 'artChartAnalysisicle'" style="height: calc(100vh - 170px)">
            <div style="height: 50%">
              <a-row :gutter="16">
                <a-col :span="12">
                  <PieChart v-if="wordCloudData" titile="标签统计" :pieChartData="wordCloudData" id="LabelStatistics" :styleCss="{ width: '100%', height: '390%', background:' #f1f2f6' }"></PieChart>
                </a-col>
                <a-col :span="12">
                  <PieChart v-if="countFileTypeData" titile="文件类型统计" :pieChartData="countFileTypeData" id="countFileType" :styleCss="{ width: '100%', height: '390%', background:' #f1f2f6' }"></PieChart>
                </a-col>
              </a-row>
            </div>
            <div style="height: 50%;margin-top:6px">
              <a-row :gutter="16">
                <a-col :span="12">
                  <WordCloud v-if="countNodeFileData" id="wordClould" :wordCloudData="countNodeFileData" height="390%"></WordCloud>
                </a-col>
                <a-col :span="12">
                  <Histogram v-if="HistogramOption" :option="HistogramOption" id="projectSchedule" :styleCss="{ width: '100%', height: '390%', background:' #f1f2f6' }"></Histogram>
                </a-col>
              </a-row>
            </div>

          </div>
          <div
            v-else-if="noTitleKey === 'mindMap'"
            v-infinite-scroll="handleInfiniteOnLoad"
            class="demo-infinite-container"
            :infinite-scroll-disabled="busy"
            :infinite-scroll-distance="10">
            <a-list
              :grid="{gutter: 12, lg: 2, md: 1, sm: 1, xs: 1}"
              :dataSource="mindMaps" >
              <a-list-item slot="renderItem" slot-scope="mindMap">
                <a-card :hoverable="true" :bodyStyle="{padding:'1px'}">
                  <JsMindMap :ref="mindMap.id" :jsMindId="mindMap.id" :jsMindMapData="mindMap" :styleCss="{ width: '100%', height: 'calc(100vh - 230px)'}"> </jsmindmap>
                  <template class="ant-card-actions" slot="actions">
                    <a @click="jsmindOperation(mindMap,'edit')">编辑</a>
                    <a @click="jsmindOperation(mindMap,'delete')">删除</a>
                    <a @click="jsmindOperation(mindMap,'collection')">收藏</a>
                    <a @click="jsmindOperation(mindMap,'dimension')">维度关联</a>
                  </template>
                </a-card>
              </a-list-item>
            </a-list>
          </div>
          <Doc v-else-if="noTitleKey === 'fileList'" :projectId="projectId" :styleCss="{ width: '100%', height: 'calc(100vh - 168px)'}"></Doc>
        </a-card>
      </a-col>
    </a-row>

  </div>
</template>

<script>
import Histogram from '@/components/ECharts/Histogram'
import PieChart from '@/components/ECharts/PieChart'
import JsMindMap from '@/components/content/jsmind/jsmind'
import Doc from '@/components/content/project/Doc'
import WordCloud from '@/components/ECharts/WordCloud'
import { getMindMapByProjectId, countNodeFile, countTag, removeMindMapByid } from '@/api/mindMap'
import { collection } from '@/api/collection'
import { getHotTagWithFileCount, getProjectBaseInfo } from '@/api/project'
import { countFileType } from '@/api/file'
import infiniteScroll from 'vue-infinite-scroll'
export default {
    directives: { infiniteScroll },
    components: { Doc, JsMindMap, PieChart, WordCloud, Histogram },
    data () {
        return {
          HistogramOption: null,
          projectBaseInfo: null,
          project: null,
          busy: false,
          countNodeFileData: null,
          countFileTypeData: null,
          wordCloudData: null,
          projectId: null,
          mindMaps: [],
          value: '',
          noTitleKey: 'artChartAnalysisicle',
          tabListNoTitle: [
              { key: 'artChartAnalysisicle', tab: '图表分析' },
              { key: 'mindMap', tab: '思维导图' },
              { key: 'fileList', tab: '项目文件' }
          ]
        }
    },
    methods: {
      getMindMapByProjectIdUtil () {
        getMindMapByProjectId(this.projectId).then(res => {
          if (res.success) {
            this.mindMaps = res.data
            if (this.mindMaps != null && this.mindMaps.length > 0) {
            }
          }
        })
      },
      onTabChange (key, type) {
        this[type] = key
        if (key === 'artChartAnalysisicle') {
          this.getIconAnalysis()
        } else if (key === 'mindMap') {
          this.getMindMapByProjectIdUtil()
        }
      },
      handleInfiniteOnLoad () {
        const data = this.mindMaps
        if (data.length > 14) {
          this.$message.warning('Infinite List loaded all')
          this.busy = true
        }
      },
      jsmindOperation (jsmind, type) {
        if (type === 'collection') {
          var dataArr = []
          dataArr.push({ 'collectionType': 1, 'collectionName': jsmind.mindMapName, 'targetId': jsmind.id })
          collection({ 'collectionScs': dataArr }).then(res => {
            res.success ? this.$message.success('收藏成功') : this.$message.error('收藏失败')
          }).catch(error => { this.$message.error('收藏异常', error) })
        }
        if (type === 'edit') {
          this.$router.push({ name: 'MindMap', query: { projectId: jsmind.projectId, jsmindId: jsmind.id } })
        }
        if (type === 'delete') {
          removeMindMapByid(jsmind.id).then(res => {
            res.success ? this.$message.success('删除成功') : this.$message.error('删除失败')
          }).catch(error => this.$message.error('删除异常', error))
        }
      },
      getIconAnalysis () {
        // 获取热度名和数量
        getHotTagWithFileCount(this.projectId).then(res => {
          if (res.success) { this.wordCloudData = res.data }
        })
        // 获取文件类型数量
        countFileType(this.projectId).then(res => {
          if (res.success) { this.countFileTypeData = res.data }
        })
        // 获取文件类型数量
        countNodeFile(this.projectId).then(res => {
          if (res.success) { this.countNodeFileData = res.data }
        })
        // 获取项目基本信息
        getProjectBaseInfo(this.projectId).then(res => {
          if (res.success) { this.projectBaseInfo = res.data }
        })
        // 获取思维导图标签统计数量
        countTag(this.projectId).then(res => {
          const tagNames = res.data.tagNames
          const mindMaps = res.data.data.map(item => { return item.name })
          const data = res.data.data.map(item => {
            var data = {
                        name: item.name,
                        type: 'bar',
                        stack: '总量',
                        label: {
                            show: true,
                            position: 'insideRight'
                        },
                        data: item.data
                    }
            return data
          })
          this.HistogramOption = {
            tooltip: {
                trigger: 'axis',
                axisPointer: { // 坐标轴指示器，坐标轴触发有效
                    type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            legend: {
                data: mindMaps // 思维导图名称
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: {
                type: 'value'
            },
            yAxis: {
                type: 'category',
                data: tagNames // 标签名称
            },
            series: data
          }
        })
      }
    },
    created () {

    },
    mounted () {
      this.projectId = this.$route.query.projectId
      this.getIconAnalysis()
    }
}
</script>

<style scoped lang="less">
.demo-infinite-container {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  overflow: auto;
  padding: 8px 24px;
  height: calc(100vh - 170px);
}
</style>
