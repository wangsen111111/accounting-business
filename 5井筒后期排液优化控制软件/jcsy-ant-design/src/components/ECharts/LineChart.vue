<template>
  <div :class="className" :id="id" :style="{ height: height, width: width }" />
</template>

<script>
import echarts from 'echarts'
import resize from './mixins/resize'
import 'echarts/theme/macarons'
export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    id: {
      type: String,
      default: 'linechart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    },
    chartData: {
      type: Object,
      default: null
    },
    isPercentage: {
      type: Boolean,
      default: false
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler () {
        this.initChart()
      }
    }
  },
  data () {
    return {
      chart: null
    }
  },
  mounted () {
    this.chart = echarts.init(document.getElementById(this.id))
    this.initChart()
    this.__resizeHandler()
  },
  beforeDestroy () {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart () {
      if (!this.chartData) {
        return
      }
      const vm = this
      this.chart.setOption({
        title: {
          text: this.chartData.title,
          x: 'center'
        },
        tooltip: {
          trigger: 'axis',
          formatter: function (params) {
            let result = (params[0].axisValue + '<br/><br/>')
            params.forEach(v => {
              if (v.data || v.data === 0) {
                if (vm.isPercentage) {
                  result += (v.marker + v.seriesName + ':' + v.data.toFixed(2) + '%' + '<br>')
                } else {
                  result += (v.marker + v.seriesName + ':' + v.data.toFixed(2) + '<br>')
                }
              }
            })
            return result
          }
        },
        legend: {
          data: this.chartData.legend,
          bottom: '0%'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '10%',
          containLabel: true
        },
        // brush: {
        //   toolbox: ['rect', 'polygon', 'lineX', 'lineY', 'keep', 'clear'],
        //   xAxisIndex: 0
        // },
        // toolbox: {
        //   feature: {
        //     magicType: {
        //       show: true,
        //       type: ['line', 'bar']
        //     },
        //     restore: {
        //       show: true
        //     },
        //     // dataView: {
        //     //   show: true
        //     // },
        //     saveAsImage: {
        //       show: true
        //     }
        //   }
        // },
        xAxis: {
          type: 'category',
          boundaryGap: true,
          // offset: 100,
          data: this.chartData.xAxis,
          axisLabel: {
            rotate: 30
          },
          axisPointer: {
            type: 'shadow'
          }
        },
        // xAxis: {
        //   type: 'category',
        //   boundaryGap: true,
        //   // offset: 100,
        //   data: this.chartData.xAxis,
        //   axisLabel: {
        //     rotate: 30
        //   },
        //   axisPointer: {
        //     type: 'shadow'
        //   }
        // },
        yAxis: [
          {
            type: 'value',
            // offset: '-620',
            name: this.chartData.yAxisName ? this.chartData.yAxisName[0] ? this.chartData.yAxisName[0] : '' : '',
            axisLabel: {
              show: true,
              interval: 'auto',
              formatter: this.isPercentage ? '{value} %' : '{value}'
            }
          },
          {
            type: 'value',
            show: this.chartData.yAxisName ? !!this.chartData.yAxisName[1] : false,
            name: this.chartData.yAxisName ? this.chartData.yAxisName[1] ? this.chartData.yAxisName[1] : '' : '',
            axisLabel: {
              show: true,
              interval: 'auto',
              formatter: '{value}'
            },
            splitLine: {
              show: false
            }
          }
        ],
        series: this.chartData.series
      })
    }
  }
}
</script>
