<template>
  <div class="card-list" ref="content" >
    <!-- 文件列表 -->   <!-- style="margin-top: 24px" -->
    <div v-if="isFileListOrCart">
      <a-card :bordered="false">
        <a-list :dataSource="dataSource" size="large" :pagination="{showSizeChanger: true, showQuickJumper: true, pageSize: 7, total: 50}">
          <a-list-item v-if="isFileListOrCart" slot="renderItem" slot-scope="item">
            <a-list-item-meta >
              <a-avatar slot="avatar" size="large" shape="square" :src="item.avatar"/>
              <a slot="title">{{ item.title }}</a>
              <template slot="description">
                <div> {{ item.subtitle() }}
                  <div class="text-overflow" :title="item.tags">
                    <a-tag color="pink" :key="index" v-for="(tag, index) in item.tags">  {{ tag }} </a-tag>
                  </div>
                </div>

              </template>
              <!-- <a slot="description">{{ item.subtitle() }}</a> -->
            </a-list-item-meta>

            <div slot="actions">
              <a-icon type="star" />
              <a-icon type="edit" style="margin-left:5px" />
              <!-- <a-icon type="thunderbolt" /> -->
            </div>

            <div slot="actions">
              <a-dropdown>
                <a-menu slot="overlay">
                  <a-menu-item><a>编辑</a></a-menu-item>
                  <a-menu-item><a>删除</a></a-menu-item>
                  <a-menu-item><a>收藏</a></a-menu-item>
                </a-menu>
                <a><a-icon type="small-dash" /></a>
              </a-dropdown>
            </div>
          </a-list-item>
        </a-list>
      </a-card>
    </div>

    <!-- 文件卡片 -->
    <div v-if="!isFileListOrCart" class="card-list" ref="content">
      <a-card :bordered="false">
        <a-list
          :pagination="{showSizeChanger: true, showQuickJumper: true, pageSize: 9, total: 50}"
          rowKey="id"
          :grid="{gutter: 24, lg: 3, md: 2, sm: 1, xs: 1}"
          :dataSource="dataSource"
        >
          <a-list-item slot="renderItem" slot-scope="item">
            <a-card :hoverable="true" :bodyStyle="{padding:'12px'}">
              <a-card-meta>
                <a slot="title">{{ item.title }}</a>
                <a-avatar class="card-avatar" slot="avatar" :src="item.avatar" size="large"/>
                <div class="meta-content" slot="description">
                  {{ item.subtitle() }}<br/>
                  <div class="text-overflow" :title="item.tags">
                    <a-tag color="pink" :key="index" v-for="(tag, index) in item.tags">  {{ tag }} </a-tag>
                  </div>
                </div>
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
      </a-card></div>
  </div>

</template>

<script>
const dataSource = []
const tags = ['标签1', '标签2', '标签3', '标签4', '标签5', '标签6', '标签7', '标签8', '标签9']
const operations = ['更新', '修改']
 // eslint-disable-next-line no-unused-vars
const filenames = ['大数据面试宝典', 'java面试宝典', '红楼梦', '西游记', '两个人，一些事', 'redis教程', 'mysql教程', 'vue教程']
  // eslint-disable-next-line no-unused-vars
const fileTypes = ['pdf', 'excel', 'word', 'picture', 'process']
var i
for (i = 0; i < 80; i++) {
  const fileType = fileTypes[Math.floor((Math.random() * fileTypes.length))]
  dataSource.push({
    title: filenames[Math.floor((Math.random() * filenames.length))] + '.' + fileType,
    avatar: '/svg/' + fileType + '.svg',
    time: '2018-07-26 22:44',
    path: 'zhouwei/bigdata',
    tags: tags,
    operation: operations[Math.floor((Math.random() * operations.length))],
    subtitle () {
        return this.path + ' ' + this.time + this.operation + '了文档'
    }

  })
}

export default {
  name: 'StandardList',
  props: {
    isFileListOrCart: { type: Boolean, default: false }
  },
  data () {
    return {
      dataSource,
      tags: tags,
      status: 'all'
      // isFileListOrCart: true
    }
  },
  methods: {
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
      console.log(this.isFileListOrCart)
    }
  },
  created () {

  }
}
</script>

<style lang="less" scoped>
@import "~@/components/index.less";

    .ant-avatar-lg {
        width: 48px;
        height: 48px;
        line-height: 48px;
    }

    .list-content-item {
        color: rgba(0, 0, 0, .45);
        display: inline-block;
        vertical-align: middle;
        font-size: 14px;
        margin-left: 40px;
        span {
            line-height: 20px;
        }
        p {
            margin-top: 4px;
            margin-bottom: 0;
            line-height: 22px;
        }
    }

    .card-list {
    /deep/ .ant-card-body:hover {
      .ant-card-meta-title>a {
        color: @primary-color;
      }
    }

    /deep/ .ant-card-meta-title {
      margin-bottom: 12px;

      &>a {
        display: inline-block;
        max-width: 100%;
        color: rgba(0,0,0,.85);
      }
    }

    /deep/ .meta-content {
      position: relative;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      height: 64px;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;

      margin-bottom: 1em;
    }
  }

  .card-avatar {
    width: 48px;
    height: 48px;
    border-radius: 48px;
  }

  .ant-card-actions {
    background: #f7f9fa;

    li {
      float: left;
      text-align: center;
      margin: 12px 0;
      color: rgba(0, 0, 0, 0.45);
      width: 50%;

      &:not(:last-child) {
        border-right: 1px solid #e8e8e8;
      }

      a {
        color: rgba(0, 0, 0, .45);
        line-height: 22px;
        display: inline-block;
        width: 100%;
        &:hover {
          color: @primary-color;
        }
      }
    }
  }

  .new-btn {
    background-color: #fff;
    border-radius: 2px;
    width: 100%;
    height: 188px;
  }
  .text-overflow{
    margin-top:15px;
    display: -webkit-box;/*作为弹性伸缩盒子模型显示*/
    -webkit-line-clamp: 1; /*显示的行数；如果要设置2行加...则设置为2*/
    overflow: hidden; /*超出的文本隐藏*/
    text-overflow: ellipsis; /* 溢出用省略号*/
    -webkit-box-orient: vertical;/*伸缩盒子的子元素排列：从上到下*/
  }
</style>
