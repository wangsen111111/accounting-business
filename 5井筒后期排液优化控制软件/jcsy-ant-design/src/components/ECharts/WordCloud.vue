<template>
  <div class="box">
    <div ref="wordClould" :id="echartId" :style="{width: '%100', height: this.height}"></div>
  </div>
</template>
<script>
import resize from './mixins/resize'
 import echarts from 'echarts'
 import 'echarts-wordcloud/dist/echarts-wordcloud'
 import 'echarts-wordcloud/dist/echarts-wordcloud.min'
export default {
  name: 'Home',
  mixins: [resize],
  data () {
    return {
        chart: null,
        echartId: 'WordClould' + this.id
    }
  },
  props: {
      id: { type: String, default: 'wordClould' },
      height: { type: String, default: '396px' },
      wordCloudData: { type: Array,
        default: () => {
        return []
      } }
  },
    watch: {
          wordCloudData: {
              // deep: true,  // 确认是否深入监听。（一般监听时是不能监听到对象属性值的变化的，数组的值变化可以听到。
              immediate: true, // 确认是否以当前的初始值执行handler的函数
              handler (nval, oval) {
                  console.log('nval=>', nval)
                  console.log('oval=>', oval)
                  console.log(this)
                  console.log('监听')
              }
          }
      },
  mounted () {
        this.chart = echarts.init(document.getElementById(this.echartId))
        // this.chart = echarts.init(this.$refs.chart2)
        this.initCharts()
        this.__resizeHandler()
  },
  beforeUpdate () {
      // alert(this.wordCloudData)
  },
  methods: {
    // this.$emit('update.wordCloudData',newValue),
    initCharts () {
        // const myChart2 = echarts.init(this.$refs.chart2)
        this.chart.setOption({
                        title: {
                            text: ''
                        },
                        tooltip: {},
                        series: [{
                            type: 'wordCloud', // 类型为字符云
                                shape: 'smooth', // 平滑
                                gridSize: 8, // 网格尺寸
                                size: ['50%', '50%'],
                                // sizeRange : [ 50, 100 ],
                                rotationRange: [-45, 0, 45, 90], // 旋转范围
                                textStyle: {
                                    normal: {
                                        fontFamily: '微软雅黑',
                                        color: function () {
                                            return 'rgb(' +
                                                Math.round(Math.random() * 255) +
                                        ', ' + Math.round(Math.random() * 255) +
                                        ', ' + Math.round(Math.random() * 255) + ')'
                                            }
                                        },
                                    emphasis: {
                                        shadowBlur: 5, // 阴影距离
                                        shadowColor: '#333' // 阴影颜色
                                    }
                                },
                                left: 'center',
                                top: 'center',
                                right: null,
                                bottom: null,
                                width: '100%',
                                height: '100%',
                                data: this.wordCloudData
                                // [
                                //     { 'name': '专业类', 'value': '756' },
                                //     { 'name': '科技科', 'value': '701' },
                                //     { 'name': '油藏类', 'value': '622' },
                                //     { 'name': '报表类', 'value': '579' },
                                //     { 'name': '学术类', 'value': '525' },
                                //     { 'name': '最近浏览', 'value': '479' },
                                //     { 'name': '团队', 'value': '443' },
                                //     { 'name': '收藏', 'value': '386' },
                                //     { 'name': '关注标签', 'value': '345' },
                                //     { 'name': '气藏类', 'value': '327' },
                                //     { 'name': '岩石裂缝', 'value': '298' },
                                //     { 'name': '油气运移', 'value': '256' },
                                //     { 'name': 'Casio', 'value': '213' },
                                //     { 'name': '波霸奶茶少冰七分糖', 'value': '188' },
                                //     { 'name': 'Nginx', 'value': '178' },
                                //     { 'name': '可口可乐', 'value': '171' },
                                //     { 'name': '哈哈哈哈哈', 'value': '136' },
                                //     { 'name': 'taishuaile', 'value': '114' },
                                //     { 'name': '请叫我算术嘉', 'value': '98' },
                                //     { 'name': '清风抽纸', 'value': '75' },
                                //     { 'name': 'python', 'value': '46' },
                                //     { 'name': 'NJUPT', 'value': '38' },
                                //     { 'name': 'ArithmeticJia', 'value': '26' }]
                                }]
                            })
    }
  },
  components: {

  }
}
</script>
<style  lang="less" >
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
