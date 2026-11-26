<template>
  <div :class="className" :style="styleSet" :id="jsMindId"></div>
</template>

<script>
// import 'jsmind/style/jsmind.css'
// import '@/assets/css/jsmind.css'
import jsMind from '@/assets/jsmind/js/jsmind.js'

require('@/assets/jsmind/js/jsmind.draggable.js')
// require('jsmind/js/jsmind.draggable.js')
export default {
  data () {
    return {}
  },
  components: {
  },
  props: {
    jsMindId: {
      type: String,
      default: 'jsMindId'
    },
    jsMindMapData: {
      type: Object,
      default: () => {
        return []
      }
    },
    styleSet: {
      type: Object,
      default: () => {
        return { width: '30.5vw', height: '20vw' }
      }
    },
    className: {
      type: String,
      default: 'jsmind_container'
    }
  },
  mounted: function () {
    this.load_jsmind()
  },
  methods: {
    load_jsmind () {
      var options = {
        container: this.jsMindId,
        editable: this.jsMindMapData.editable,
        theme: this.jsMindMapData.theme,
        view: {
          engine: 'canvas', // 思维导图各节点之间线条的绘制引擎
          hmargin: 10, // 思维导图距容器外框的最小水平距离
          vmargin: 10, // 思维导图距容器外框的最小垂直距离
          line_width: 2, // 思维导图线条的粗细
          line_color: '#555' // 思维导图线条的颜色
        }
      }
      this.jm = jsMind.show(options, this.jsMindMapData.data)
      this.jm.disable_edit()
    }
  }
}
</script>

<style lang="less">
.jsmind_container,
.jsmind_container_no_scroll_axis {
  background: #f4f4f4;
  display: flex;
  justify-content: center;
  align-items: center;
}

.jsmind_container_no_scroll_axis > .jsmind-inner::-webkit-scrollbar {
  display: none !important; /* Chrome Safari */
}

.jsmind_container_no_scroll_axis > .jsmind-inner {
  scrollbar-width: none !important; /* firefox */
  -ms-overflow-style: none !important; /* IE 10+ */
  overflow-x: hidden !important;
  overflow-y: hidden !important;
}
</style>
