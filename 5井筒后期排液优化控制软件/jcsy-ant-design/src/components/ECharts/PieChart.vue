<template>
  <div :id="echartId" :style="styleCss"></div>
</template>

<script>
import echarts from 'echarts'
import resize from './mixins/resize'
export default {
    mixins: [resize],
    props: {
        titile: { type: String, default: '知识库' },
        id: { type: String, default: 'echart' },
        styleCss: { type: Object,
            default: () => {
                return { width: '100%', height: '280px' }
        } },
        pieChartData: { type: Array,
            default: () => {
                return []
        } }
    },
    data () {
        return {
            chart: null,
            echartId: 'PieChart' + this.id
        }
    },
    mounted () {
        const legendData = this.pieChartData.map(item => item.name)
        this.chart = echarts.init(document.getElementById(this.echartId))
        this.drawLine(legendData)
        this.__resizeHandler()
    },
    methods: {
        drawLine (legendData) {
            // 基于准备好的dom，初始化echarts实例
            // console.log(this.$echarts)
            // const myChart = echarts.init(document.getElementById(this.echartId))
            // 绘制图表
            var option = {
                title: {
                    text: this.titile,
                    left: 'center'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: '{a} <br/>{b} : {c} ({d}%)'
                },
                legend: {
                    orient: 'vertical',
                    left: 'left',
                    data: legendData
                },
                series: [
                    {
                        name: '访问来源',
                        type: 'pie',
                        radius: '55%',
                        center: ['50%', '60%'],
                        data: this.pieChartData,

                        emphasis: {
                            itemStyle: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }
                ]
            }
            this.chart.setOption(option)
        }
    },
    created () {

    }
}
</script>

<style scoped lang="less">
.box{
   width:100%;
  //  height: 500px;
   background: #f1f2f6;
}
</style>
