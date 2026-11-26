<template>
  <div style="overflow: auto;font-size:14px;height:280px;width:100%;background-color: #ececec;">
    <svg
      xmlns="http://www.w3.org/2000/svg"
      :id="id"
      version="1.1"
      height="600"
      width="1000"
    >
      <!-- style="border:1px solid #C0C0C0" -->
    </svg>
  </div>
</template>

<script>
const treeData = [
        {
            title: '知识库1',
            children: [
                {
                    title: '目录1'
                },
                {
                    title: '目录2',
                    children: [
                        { title: '目录2.1' },
                        { title: '目录2.2' },
                        {
                            title: '目录2.3',
                            children: [
                                { title: '目录2.3.1' },
                                { title: '目录2.3.2' },
                                { title: '目录2.3.3' }
                            ]
                        },
                        { title: '目录2.4' }
                        // { title: '目录2.5' }
                    ]
                }
                // ,{
                //     title: '1.2',
                //     children: [
                //         { title: '1.2.1', borderColor: 'red', fontColor: 'red' },
                //         { title: '1.2.2', bgColor: 'black', fontColor: 'white' }
                //     ]
                // }
            ]
        }
    ]
export default {
    data () {
        return {
            treeData: treeData,
            // 字体大小、节点左右间隔大小、节点内部padding、节点上下margin、默认字体颜色、默认边框颜色、
            baseStyle: { nodeFontS: 14, interval: 40, padding: 4, marginY: 6, fontColor: '#626262', borderColor: '#55aaee', lineColor: '#55aaee', svgW: 0, lastNodeN: 0 }
        }
    },
    props: {
        id: { type: String, default: 'svgContainer' },
        basePro: { type: Object,
            default: () => {
                return { nodeFontS: 14, interval: 25, padding: 4, marginY: 6, fontColor: '#626262', borderColor: '#55aaee', lineColor: '#55aaee' }
        } }
    },
    mounted () {
        const svg = document.getElementById(this.id)
        // 树状结构数据重构
        this.reBuildData(this.treeData, null)
        // 构建svg
        this.buildSvg(this.treeData, svg)
        // 设置svg宽高
        this.setSvgContainerSize(this.baseStyle.svgW + 30, this.baseStyle.lastNodeN * this.getNodeH() + 30, svg)
    },
    methods: {
        getNodeH () {
            return this.baseStyle.nodeFontS + this.baseStyle.padding * 2 + this.baseStyle.marginY * 2
        },
        reBuildData (d, parent) { // 树状结构数据重构
            d.forEach((v, i) => {
                v.parent = parent
                if (v.children && v.children.length > 0) {
                    this.reBuildData(v.children, v)
                    this.parentY(v, d.length, i)
                } else {
                    this.baseStyle.lastNodeN = this.baseStyle.lastNodeN + 1
                    v.y = this.baseStyle.lastNodeN
                    this.parentY(v, d.length, i)
                }
            })
        },

     parentY (lastNode, len, i) { // 计算节点的y坐标
        const parent = lastNode.parent
        if (len === (i + 1) && !!parent) {
            const s = parent.children[0].y
            parent.y = s + (lastNode.y - s) / 2
        }
    },

     setSvgContainerSize (w, h, svg) { // 设置svg的宽和高
        svg.setAttribute('width', w)
        svg.setAttribute('height', h)
    },

     buildSvg (data, svg) { // 生成脑图svg
        data.forEach((v) => {
            this.buildNode(v, svg)
            if (!!v.children && v.children.length > 0) {
                this.buildSvg(v.children, svg)
            }
        })
    },

     buildNode (node, svg) { // 构建脑图的每个节点
        const gTag = document.createElementNS('http://www.w3.org/2000/svg', 'g')
        const textTag = document.createElementNS('http://www.w3.org/2000/svg', 'text')
        const text = document.createTextNode(node.title)
        textTag.appendChild(text)

        gTag.appendChild(textTag)
        svg.appendChild(gTag)

        textTag.setAttribute('fill', node.fontColor ? node.fontColor : this.baseStyle.fontColor)
        textTag.setAttribute('font-size', this.baseStyle.nodeFontS)
        const parentx = node.parent ? node.parent.x : 0
        const parentw = node.parent ? node.parent.w : 0
        const intervalNow = node.parent ? this.baseStyle.interval : 0

        const textH = Math.ceil(textTag.getBBox().height)
        node.w = Math.ceil(textTag.getBBox().width) + this.baseStyle.padding * 2
        node.x = parentx + parentw + intervalNow + 6
        gTag.setAttribute('transform', `translate(${node.x + this.baseStyle.padding},${node.y * (this.getNodeH()) + textH / 2 - 5})`)
        if ((node.w + node.x) > this.baseStyle.svgW) {
            this.baseStyle.svgW = node.w + node.x
        }
        gTag.insertBefore(this.drawBorder(node), textTag)
        this.drawLine(node, svg)
    },

     drawBorder (node) { // 画节点的边框
        const fan = document.createElementNS('http://www.w3.org/2000/svg', 'path')
        fan.setAttribute('d', this.getBorderD(node))
        fan.setAttribute('stroke', node.borderColor ? node.borderColor : this.baseStyle.borderColor)
        fan.setAttribute('stroke-width', '1')
        fan.setAttribute('fill', node.bgColor ? node.bgColor : 'none')
        return fan
    },
     getBorderD (node) { // 获得节点的d
        return `M0,-17h${node.w - 8}a5,5,0,0,1,5,5v13a5,5,0,0,1,-5,5h-${node.w - 8}a5,5,0,0,1,-5,-5v-13a5,5,0,0,1,5,-5z`
    },

     drawLine (node, svg) { // 为当前节点连线父级节点
        if (!node.parent) {
            return
        }
        const fan = document.createElementNS('http://www.w3.org/2000/svg', 'path')
        svg.appendChild(fan)
        fan.setAttribute('d', this.getD(node))
        fan.setAttribute('stroke', this.baseStyle.lineColor)
        fan.setAttribute('stroke-width', '1')
        fan.setAttribute('fill', 'none')
    },
     getD (node) { // 获得节点的 d线
        const parent = node.parent
        if (parent.y !== node.y) {
            return `M${parent.x + parent.w} ${parent.y * this.getNodeH()} C${parent.x + parent.w + 15} ${parent.y * this.getNodeH()} ${node.x - 15} ${node.y * this.getNodeH()} ${node.x} ${node.y * this.getNodeH()}`
        } else {
            return `M${parent.x + parent.w} ${parent.y * this.getNodeH()} L${node.x} ${node.y * this.getNodeH()}`
        }
    }

    }
}
</script>

<style scoped lang="less">
body {font-family: 微软雅黑;font-size: 22px;}
</style>
