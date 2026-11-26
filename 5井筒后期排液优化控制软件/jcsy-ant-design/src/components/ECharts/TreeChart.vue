<template>
  <div :id="echartId" :style="styleCss"></div>
</template>

<script>
import echarts from 'echarts'
import resize from './mixins/resize'
// import { axios } from '@/utils/request'
export default {
    mixins: [resize],
    props: {
        id: { type: String, default: 'TreeChart' },
        styleCss: { type: Object,
            default: () => {
                return { width: '100%', height: '280px' }
        } }
    },
    data () {
        return {
            chart: null,
            echartId: 'PieChart' + this.id,
            treeData: {
                'name': 'flare',
                'children': [
                    {
                        'name': 'analytics',
                        'children': [
                            {
                                'name': 'cluster',
                                'children': [
                                    {
                                        'name': 'AgglomerativeCluster',
                                        'value': 3938
                                    },
                                    {
                                        'name': 'CommunityStructure',
                                        'value': 3812
                                    },
                                    {
                                        'name': 'HierarchicalCluster',
                                        'value': 6714
                                    },
                                    {
                                        'name': 'MergeEdge',
                                        'value': 743
                                    }
                                ]
                            },
                            {
                                'name': 'graph',
                                'children': [
                                    {
                                        'name': 'BetweennessCentrality',
                                        'value': 3534
                                    },
                                    {
                                        'name': 'LinkDistance',
                                        'value': 5731
                                    },
                                    {
                                        'name': 'MaxFlowMinCut',
                                        'value': 7840
                                    },
                                    {
                                        'name': 'ShortestPaths',
                                        'value': 5914
                                    },
                                    {
                                        'name': 'SpanningTree',
                                        'value': 3416
                                    }
                                ]
                            },
                            {
                                'name': 'optimization',
                                'children': [
                                    {
                                        'name': 'AspectRatioBanker',
                                        'value': 7074
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        }
    },
    mounted () {
        this.chart = echarts.init(document.getElementById(this.echartId))
        this.drawLine()
        this.__resizeHandler()
    },
    methods: {
        drawLine () {
            // 绘制图表
            this.chart.showLoading()
            this.$axios.get('http:/127.0.0.1:8000/public/json/echartTreeData.json').then(data => {
                this.chart.hideLoading()

                echarts.util.each(this.treeData.children, function (datum, index) {
                    index % 2 === 0 && (datum.collapsed = true)
                })

                this.chart.setOption({
                    tooltip: {
                        trigger: 'item',
                        triggerOn: 'mousemove'
                    },
                    series: [
                        {
                            type: 'tree',

                            data: [this.treeData],

                            top: '1%',
                            left: '7%',
                            bottom: '1%',
                            right: '20%',

                            symbolSize: 7,

                            label: {
                                position: 'left',
                                verticalAlign: 'middle',
                                align: 'right',
                                fontSize: 9
                            },

                            leaves: {
                                label: {
                                    position: 'right',
                                    verticalAlign: 'middle',
                                    align: 'left'
                                }
                            },

                            expandAndCollapse: true,
                            animationDuration: 550,
                            animationDurationUpdate: 750
                        }
                    ]
                })
            })
            // axios({
            //     url: 'https://echarts-www.cdn.bcebos.com/examples/data/asset/data/flare.json',
            //     method: 'get',
            //     headers: {
            //     'Content-Type': 'application/json;charset=UTF-8'
            //     }
            // }).then(data => {

            // })
            // axios.get('https://echarts-www.cdn.bcebos.com/examples' + '/data/asset/data/flare.json', function (data) {

            // })
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
