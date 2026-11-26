<template>
  <div>
    <a-row >
      <a-col :span="4">
        <div style="background-color: #ececec;height:280px; padding: 20px ">
          <div >
            <h1>项目简介：</h1>
            <a-textarea
              style="background: #FFFFFF;font-color: #000000"
              v-model="value"
              placeholder=""
              disabled="disabled"
              :auto-size="{ minRows: 2, maxRows: 6 }" />
          </div>
          <div style="margin-top:10px">
            <h1>标签：</h1>
            <div class="text-overflow" :title="item.tags">
              <a-tag color="pink" :key="index" v-for="(tag, index) in item.tags">  {{ tag }} </a-tag>
            </div>
          </div>
        </div>
      </a-col>
      <a-col :span="6">
        <!-- 饼图 -->
        <PieChart :id="item.id" :styleCss="{ width: '100%', height: '280px', padding: '20px', background: '#ececec'}"></PieChart>
      </a-col>
      <a-col :span="6">
        <!-- 直方图 -->
        <Histogram :option="HistogramOption" :id="item.id" :styleCss="{width: '100%', height: '280px', padding: '20px', background: '#ececec'}"></Histogram>
      </a-col>
      <a-col :span="8">
        <!-- 脑图 -->
        <SimpleMindMap :id="item.id" ></SimpleMindMap>
        <!-- <TreeChart :id="item.id"></TreeChart> -->
      </a-col>
    </a-row>
  </div>

</template>

<script>
// import RadarMap from '@/components/ECharts/RadarMap'
// TreeChart
import TreeChart from '@/components/ECharts/TreeChart'
import Histogram from '@/components/ECharts/Histogram'
import PieChart from '@/components/ECharts/PieChart'
import SimpleMindMap from '@/components/content/jsmind/SimpleMindMap'
export default {
    data () {
        return {
          value: '项目概况是指在介绍或论述某个项目时,首先综合性地简要介绍项目的基本情况。比如一个招商项目,它比一般项目概况的内容较全面,包括项目建设内容、建设规模、投资总额、市场前景、经济效益、社会效益、地理位置、交通条件、气候环境、人文环境、优惠政策等内容。',
          HistogramOption: {
                title: {
                    text: '井场试验-n'
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    }
                },
                legend: {
                    data: ['文件数']
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'value',
                    boundaryGap: [0, 0.01]
                },
                yAxis: {
                    type: 'category',
                    data: ['阶段1', '阶段2', '阶段3', '阶段4', '阶段5']
                },
                series: [
                    {
                        name: '文件数',
                        type: 'bar',
                        data: [19, 24, 30, 15, 41, 87]
                    }
                ]
            }
       }
    },
    components: {
      Histogram,
      PieChart,
      SimpleMindMap,
      TreeChart
    },
    props: {
      item: { type: Object,
            default: () => {
                return { }
        } },
      tags: { type: Array, default: null }
    },
    methods: {

    },
    created () {

    },
    mounted () {

    }
}
</script>

<style scoped lang="less">
  .text-overflow{
    margin-top:0px;
    display: -webkit-box;/*作为弹性伸缩盒子模型显示*/
    -webkit-line-clamp: 2; /*显示的行数；如果要设置2行加...则设置为2*/
    overflow: hidden; /*超出的文本隐藏*/
    text-overflow: ellipsis; /* 溢出用省略号*/
    -webkit-box-orient: vertical;/*伸缩盒子的子元素排列：从上到下*/
  }
</style>
