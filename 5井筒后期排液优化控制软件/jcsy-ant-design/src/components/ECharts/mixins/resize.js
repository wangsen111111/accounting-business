import { debounce } from '@/utils/chart'

export default {
  data () {
    return {
      // sidebarElm: null
    }
  },
  mounted () {
    this.__resizeHandler = debounce(() => {
      if (this.chart) {
        this.chart.resize()
      }
    }, 300)
    window.addEventListener('resize', this.__resizeHandler)
  },
  beforeDestroy () {
    window.removeEventListener('resize', this.__resizeHandler)
  },
  methods: {
  }
}
