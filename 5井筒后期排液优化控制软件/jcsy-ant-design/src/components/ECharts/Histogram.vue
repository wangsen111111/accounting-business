<template>
  <div :id="echartId" :style="styleCss"></div>

</template>

<script>
import echarts from 'echarts'
import resize from './mixins/resize'
export default {
    mixins: [resize],
    data () {
        return {
            chart: null,
            echartId: 'Histogram' + this.id
        }
    },
    props: {
        id: { type: String, default: 'echart' },
        styleCss: { type: Object,
            default: () => {
                return { width: '100%', height: '280px' }
        } },
        option: { type: Object,
            default: () => {
                return {}
        } }
    },
    mounted () {
        this.chart = echarts.init(document.getElementById(this.echartId))
        this.drawLine()
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
        drawLine () {
            this.chart.setOption(this.option)
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
  .home{
    margin: 0 auto;
    width: calc(100% - 80px)
  }
}
</style>
