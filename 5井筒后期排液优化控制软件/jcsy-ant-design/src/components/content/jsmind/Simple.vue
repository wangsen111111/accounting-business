<template>
  <div class="jsmind_class" :style="styleSet">
    <div class="jsmind_container" :style="styleSet" :class="className" :id="jsMindId"></div>
  </div>
</template>

<script>
// import 'jsmind/style/jsmind.css'
// import '@/assets/css/jsmind.css'
// import jsMind from 'jsmind'
import jsMind from '@/assets/jsmind/js/jsmind.js'

// window.jsMind = jsMind
// require('jsmind/js/jsmind.draggable.js')
require('@/assets/jsmind/js/jsmind.draggable.js')
export default {
  components: {},
  data () {
    return {
      jm: null
    }
  },
  props: {
    jsMindId: {
      type: String,
      default: 'jsMindId'
    },
    jsMindName: {
      type: String,
      default: 'jsMindName'
    },
    data: {
      type: Array,
      default: () => {
        return []
      }
    },
    format: {
      type: String,
      default: 'node_array'
    },
    className: {
      type: String,
      default: 'jsmind_container'
    },
    styleSet: {
      type: Object,
      default: () => {
        return { width: '30.5vw', height: '20vw' }
      }
    }
  },
  mounted: function () {
    this.load_jsmind(this.jsMindId)
  },
  methods: {
    load_jsmind (id) {
      // var mind = {
      //   meta: {
      //     name: this.jsMindName,
      //     author: 'hizzgdev@163.com',
      //     version: '0.2'
      //   },
      //   format: this.format,
      //   data: this.data
      // }
      console.log(1234)
      var mind = this.data
      var options = {
        container: id,
        editable: false,
        theme: 'primary',
        layout: {
          hspace: 30, // 节点之间的水平间距
          vspace: 20, // 节点之间的垂直间距
          pspace: 12 // 节点与连接线之间的水平间距（用于容纳节点收缩/展开控制器）
        }
      }
      this.jm = jsMind.show(options, mind)
      this.jm.disable_edit()
    }
  }
}
</script>

<style scoped lang="less">
.jsmind_container {
  background: #f4f4f4;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
