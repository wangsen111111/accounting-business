<template>
  <div style="height: calc(100vh - 200px)">
    <!-- 文件列表 -->
    <a-card :bordered="false">
      <a-list>
        <RecycleScroller
          v-if="operationList"
          v-infinite-scroll="handleInfiniteOnLoad"
          style="height: calc(100vh - 265px)"
          :items="operationList"
          :item-size="90"
          key-field="key"
          :infinite-scroll-disabled="busy"
          infinite-scroll-distance="calc(100vh - 265px)" >
          <a-list-item slot-scope="{ item }" style="border-bottom: 1px solid #e8e8e8;width:98%">
            <a-list-item-meta>
              <a-avatar
                slot="avatar"
                size="large"
                shape="square"
                src="/svg/pdf.svg"
                @click="toKnowledgeBaseDetail('/svg/pdf.svg')" />
              <a
                slot="title"
                class="KnowledgeBaseName"
                @click="toKnowledgeBaseDetail(item.operationDescription)"
              >{{ item.operationDescription }}</a>
              <template slot="description">
                <a-row> <a-col :span="12">{{ item.operationDescription }}</a-col> </a-row>
              </template>
            </a-list-item-meta>
            <div slot="actions" >
              <a-icon type="star" />
              <a-icon type="edit" style="margin-left:5px" />
            </div>
            <div slot="actions" >
              <a-dropdown>
                <a-menu slot="overlay">
                  <a-menu-item> <a>编辑</a> </a-menu-item>
                  <a-menu-item>
                    <a-popconfirm title="是否要删除此行？" @confirm="removeFile(item)"> <a>删除</a> </a-popconfirm>
                  </a-menu-item>
                  <a-menu-item> <a>收藏</a> </a-menu-item>
                </a-menu>
                <a> <a-icon type="small-dash" /> </a>
              </a-dropdown>
            </div>
          </a-list-item>
        </RecycleScroller>
        <a-spin v-if="loading" class="demo-loading" />
      </a-list>
    </a-card>
  </div>
</template>

<script>
import 'v-contextmenu/dist/index.css'
import infiniteScroll from 'vue-infinite-scroll'
import { RecycleScroller } from 'vue-virtual-scroller'
import 'vue-virtual-scroller/dist/vue-virtual-scroller.css'
import { getOperations } from '@/api/workSurface'

export default {
  directives: { infiniteScroll },
  name: 'StandardList',
  components: { RecycleScroller },
  props: {
    selectFileTypeArray: {
      type: Array,
      default: null
    }
  },
  data () {
    return {
      data: [],
      loading: false,
      busy: false,
      loadQuantity: 20,
      searchData: null,
      dataSource: null,
      operationList: null
    }
  },
  beforeMount () {
    getOperations(1, 10).then(res => {
      this.operationList = res.data.data
    })
  },
  mounted () {

  },
  methods: {
    toKnowledgeBaseDetail (name) {
      if (this.$route.name === 'KnowledgeBase') {
        this.$router.push({ name: 'KnowledgeBaseDetail', query: { name: name } })
      }
    },
    handleInfiniteOnLoad () {
      const needData = this.searchData === null ? this.dataSource : this.searchData
      if (needData.length !== null && needData.length !== 0) {
        const data = this.data
        this.loading = true
        if (data.length >= needData.length) {
          this.$message.warning('已经到底部了')
          this.busy = true
          this.loading = false
          return
        }
        this.data = data
          .concat(needData.slice(data.length, data.length + this.loadQuantity))
          .map((item, index) => ({ ...item, index }))
        this.loading = false
      }
    },
    removeFile (data) {
      this.$emit('removeFile', data)
    }
  }
}
</script>

<style lang="less" scoped>
@import '~@/components/index.less';
.text-overflow {
  margin-top: 15px;
  display: -webkit-box; /*作为弹性伸缩盒子模型显示*/
  -webkit-line-clamp: 1; /*显示的行数；如果要设置2行加...则设置为2*/
  overflow: hidden; /*超出的文本隐藏*/
  text-overflow: ellipsis; /* 溢出用省略号*/
  -webkit-box-orient: vertical; /*伸缩盒子的子元素排列：从上到下*/
}
.KnowledgeBaseName:hover {
  background-color: red;
}
.demo-loading {
  position: absolute;
  bottom: 40px;
  width: 100%;
  text-align: center;
}
</style>
