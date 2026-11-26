<template>
  <div style="height: calc(100vh - 200px)">
    <!-- 文件列表 -->
    <!-- style="margin-top: 24px"  :pagination="{showSizeChanger: true, showQuickJumper: true, pageSize: 12, total: 50}"-->
    <a-card :bordered="false" v-show="isFileListOrCart">
      <a-list>
        <RecycleScroller
          v-infinite-scroll="handleInfiniteOnLoad"
          style="height: calc(100vh - 265px)"
          :items="data"
          :item-size="90"
          key-field="id"
          :infinite-scroll-disabled="busy"
          infinite-scroll-distance="calc(100vh - 265px)" >
          <a-list-item slot-scope="{ item }" style="border-bottom: 1px solid #e8e8e8;width:98%">
            <a-list-item-meta>
              <a-avatar
                slot="avatar"
                size="large"
                shape="square"
                :src="item.avatar"
                @click="toKnowledgeBaseDetail(item.avatar)" />
              <!-- @click="toKnowledgeBaseDetail(item.title)" -->
              <a
                slot="title"
                class="KnowledgeBaseName"
                @click="toKnowledgeBaseDetail(item.title)"
              >{{ item.title }}</a>
              <template slot="description">
                <a-row>
                  <a-col :span="12">{{ item.subtitle() }}</a-col>
                  <!-- <a-col :span="12" style="text-align: right;">
                    <a-icon
                      v-if="! item.isShow && routeName === 'KnowledgeBase'"
                      type="down"
                      @click="item.isShow = ! item.isShow"
                      style="margin-left:5px;margin-right:10px"
                    />
                    <a-icon
                      v-if="item.isShow && routeName === 'KnowledgeBase'"
                      type="up"
                      @click="item.isShow = ! item.isShow"
                      style="margin-left:5px;margin-right:10px"
                    />
                  </a-col>-->
                </a-row>
                <div v-if="routeName !== 'KnowledgeBase'">
                  <a-tag color="pink" :key="index" v-for="(tag, index) in item.tags">{{ tag }}</a-tag>
                </div>
                <!-- <Detail
                  v-if="item.isShow && routeName === 'KnowledgeBase' "
                  :item="item"
                  :isShow="item.isShow"
                  :tags="item.tags"
                ></Detail>-->
              </template>
            </a-list-item-meta>
            <div slot="actions" v-if="routeName !== 'KnowledgeBase'">
              <a-icon type="star" />
              <a-icon type="edit" style="margin-left:5px" />
            </div>
            <div slot="actions" v-if="routeName !== 'KnowledgeBase'">
              <a-dropdown>
                <a-menu slot="overlay">
                  <a-menu-item>
                    <a>编辑</a>
                  </a-menu-item>
                  <a-menu-item>
                    <a-popconfirm title="是否要删除此行？" @confirm="removeFile(item)">
                      <a>删除</a>
                    </a-popconfirm>
                    <!-- <a @click="deleteFile()">删除</a> -->
                  </a-menu-item>
                  <a-menu-item>
                    <a>收藏</a>
                  </a-menu-item>
                </a-menu>
                <a>
                  <a-icon type="small-dash" />
                </a>
              </a-dropdown>
            </div>
          </a-list-item>
        </RecycleScroller>
        <a-spin v-if="loading" class="demo-loading" />
      </a-list>
    </a-card>

    <!-- 文件卡片 -->
    <a-card :bordered="false" v-show="!isFileListOrCart">
      <a-list
        rowKey="id"
        :pagination="{showSizeChanger: true, showQuickJumper: true, pageSize: 12, total: searchData.length}"
        :grid="{gutter: 24, lg: 2, md: 1, sm: 1, xs: 1}"
        :dataSource="searchData">
        <a-list-item slot="renderItem" slot-scope="item">
          <a-card :hoverable="true" :bodyStyle="{padding:'12px'}">
            {{ item.title }}
            <a-card-meta>
              <!-- <a slot="title">{{ item.title }}</a>
              <a-avatar class="card-avatar" slot="avatar" :src="item.avatar" size="large" />
              <div class="meta-content" slot="description">
                {{ item.subtitle() }}
                <div class="text-overflow" :title="item.tags">
                  <a-tag color="pink" :key="index" v-for="(tag, index) in item.tags">{{ tag }}</a-tag>
                </div>
              </div> -->
            </a-card-meta>
            <template class="ant-card-actions" slot="actions">
              <a>编辑</a>
              <a>删除</a>
              <a>收藏</a>
              <a @click="test">...</a>
            </template>
          </a-card>
        </a-list-item>
      </a-list>
    </a-card>
  </div>
</template>

<script>
// import contentmenu from 'v-contextmenu'
import 'v-contextmenu/dist/index.css'
// import Detail from '@/components/content/detail/index'
import infiniteScroll from 'vue-infinite-scroll'
import { RecycleScroller } from 'vue-virtual-scroller'
import 'vue-virtual-scroller/dist/vue-virtual-scroller.css'

export default {
  directives: { infiniteScroll },
  name: 'StandardList',
  components: {
    // contentmenu,
    // Detail,
    RecycleScroller
  },
  props: {
    isFileListOrCart: { type: Boolean, default: false },
    dataSource: {
      type: Array,
      default: null
    },
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
      routeName: null,
      searchData: null
      // dataSource: []
    }
  },
  beforeMount () {
    if (this.dataSource == null) {
      this.dataSource = []
      this.routeName = this.$route.name
      const tags = ['标签1', '标签2', '标签3', '标签4', '标签5', '标签6', '标签7', '标签8', '标签9']
      const operations = ['更新', '修改']
      // eslint-disable-next-line no-unused-vars
      let filenames = []
      // eslint-disable-next-line no-unused-vars
      let fileTypes = ['pdf', 'excel', 'word', 'picture', 'process', 'folder']
      // function uuid () {
      //   function S4 () {
      //     return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
      //   }
      //   return S4() + S4() + '-' + S4() + '-' + S4() + '-' + S4() + '-' + S4() + S4() + S4()
      // }

      if (this.$route.name === 'KnowledgeBase') {
        filenames = [
          '井场实验1',
          '井场实验2',
          '井场实验3',
          '井场实验4',
          '井场实验5',
          '井场实验6',
          '井场实验7',
          '井场实验8',
          '井场实验9',
          '井场实验10',
          '井场实验11',
          '井场实验12'
        ]
        fileTypes = ['folder']
      } else {
        filenames = ['三国演义', '红楼梦', '水府传', '西游记', '一些事', '斗罗大陆']
        fileTypes = ['pdf', 'xlsx', 'word']
      }

      for (var j = 0; j < 24; j++) {
        var fileType = fileTypes[Math.floor(Math.random() * fileTypes.length)]
        var KnowledgeBaseFileName = filenames[Math.floor(Math.random() * filenames.length)]
        var NotKnowledgeBaseFileName = filenames[Math.floor(Math.random() * filenames.length)] + '.' + fileType
        var tmpTitle = this.$route.name === 'KnowledgeBase' ? KnowledgeBaseFileName : NotKnowledgeBaseFileName
        if (fileType === 'xlsx' || fileType === 'xls') {
          fileType = 'excel'
        }
        // Math.floor 一个表示小于或等于指定数字的最大整数的数字
        // const fileType = fileTypes[Math.floor((Math.random() * fileTypes.length))]
        this.dataSource.push({
          isShow: true,
          id: j,
          title: tmpTitle,
          avatar: '/svg/' + fileType + '.svg',
          time: '2018-07-26 22:44',
          path: 'zhouwei/bigdata',
          tags: tags,
          operation: operations[Math.floor(Math.random() * operations.length)],
          subtitle () {
            return this.path + ' ' + this.time + this.operation + '了文档'
          }
        })
      }
    }
    let needData = this.dataSource
    if (this.selectFileTypeArray !== null && this.selectFileTypeArray.length !== 0) {
      needData = this.dataSource.filter((dataOne) => this.selectFileTypeArray.indexOf(dataOne.fileType) > -1)
    }
    this.searchData = needData
    const needArray = needData.slice(0, this.loadQuantity)
    this.data = needArray.map((item, index) => ({ ...item, index }))
  },
  mounted () {},
  methods: {
    toKnowledgeBaseDetail (name) {
      if (this.$route.name === 'KnowledgeBase') {
        this.$router.push({ name: 'KnowledgeBaseDetail', query: { name: name } })
      }
    },
    test () {
      var cHeight = document.getElementsByClassName('text-overflow')[1].clientHeight
      var sHeight = document.getElementsByClassName('text-overflow')[1].scrollHeight
      if (sHeight > cHeight) {
        console.log('已经溢出显示省略号')
      } else {
        console.log('没有溢出')
      }
    },
    getisFileListOrCart (data) {
      this.isFileListOrCart = data
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
