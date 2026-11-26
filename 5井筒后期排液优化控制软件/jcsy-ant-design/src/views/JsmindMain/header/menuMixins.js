export default {
    data () {
      return {
        nodeLocked: false, // 是否锁定节点
        bothSide: true// 两侧模式
      }
    },
    mounted () {
    },
    computed: {
      toolBarSvg () {
        return this.$refs.toolBarSvg
      },
      sysColor () {
        return this.$store.state.app.color
      }
    },
    beforeDestroy () {
    },
    watch: {
      sysColor () {
        this.SetDefColor()
      }
    },
    methods: {
      SetDefColor () {
        this.$refs.toolBarSvg.forEach((toolbar) => {
          toolbar.$el.children.forEach((path) => {
            path.style.fill = this.sysColor
          })
        })
        this.$refs.toolBarSvgSide.$el.children[0].style.fill = this.sysColor
      }
    }
  }
