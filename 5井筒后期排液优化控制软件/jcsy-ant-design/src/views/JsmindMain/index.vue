<template>
  <div id="jsmind-main-layout">
    <a-layout>
      <a-layout-header :class="showToolBar ? 'have-tool-bar' : 'not-have-tool-bar'">
        <Header></Header>
      </a-layout-header>
      <a-layout-content style="margin-top: 10px">
        <!-- <Test></Test> -->
        zhongjian
        <p>1</p>
        <p>2</p>
        <p>3</p>
        <p>4</p>
        <p>5</p>
        <p>6</p>
        <p>dd</p>
        <p>dd</p>
        <p>dd</p>
      </a-layout-content>
      <a-layout-footer>Footer</a-layout-footer>
    </a-layout>
  </div>
</template>

<script>
import Test from './test'
import Header from './header/index'

export default {
  components: {
    Header,
    Test
  },
  computed: {
    showToolBar () {
      return this.$store.state.app.showToolBar
    }
  },
  data () {
    return {
    }
  }
}
</script>

<style lang="less">

#jsmind-main-layout {
  text-align: left;
  .have-tool-bar {
    padding: 0px;
    background: #fff;
    color: #fff;
    height: 145px;
  }

  .not-have-tool-bar {
    padding: 0px;
    background: #fff;
    color: #fff;
    height: 42px;
    .ant-tabs-card-content {
      height: 0px;
    }
  }

  .ant-layout-footer {
    background: #7dbcea;
    color: #fff;
    line-height: 1.5;
  }
}

#jsmind-main-layout > .ant-layout {
  margin-bottom: 48px;
}
#jsmind-main-layout > .ant-layout:last-child {
  margin: 0;
}
</style>
