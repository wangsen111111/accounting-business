<template>
  <div id="RadarMap" :style="{width: '400px', height: '338px'}"></div>
</template>

<script>
import echarts from 'echarts'
import resize from './mixins/resize'
export default {
    mixins: [resize],
    data () {
        return {

        }
    },
    mounted () {
        this.drawLine()
        this.__resizeHandler()
    },
    methods: {
            drawLine () {
        // 基于准备好的dom，初始化echarts实例
        // console.log(this.$echarts)
        const myChart = echarts.init(document.getElementById('RadarMap'))
        // 绘制图表
        var option = {
    title: {
        text: '基础雷达图'
    },
    tooltip: {},
    legend: {
        data: ['预算分配', '实际开销']
    },
    radar: {
        // shape: 'circle',
        name: {
            textStyle: {
                color: '#fff',
                backgroundColor: '#999',
                borderRadius: 3,
                padding: [3, 5]
            }
        },
        indicator: [
            { name: '销售', max: 6500 },
            { name: '管理', max: 16000 },
            { name: '信息技术', max: 30000 },
            { name: '客服', max: 38000 },
            { name: '研发', max: 52000 },
            { name: '市场', max: 25000 }
        ]
    },
    series: [{
        name: '预算 vs 开销',
        type: 'radar',
        // areaStyle: {normal: {}},
        data: [
            {
                value: [4300, 10000, 28000, 35000, 50000, 19000],
                name: '预算分配（Allocated Budget）'
            },
            {
                value: [5000, 14000, 28000, 31000, 42000, 21000],
                name: '实际开销（Actual Spending）'
            }
        ]
    }]
}
      myChart.setOption(option)
    }
    },
    created () {

    }
}
</script>

<style scoped lang="less">

</style>
