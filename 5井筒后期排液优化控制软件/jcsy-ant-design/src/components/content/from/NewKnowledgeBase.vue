<template>
  <a-modal
    :width="'50%'"
    title="新建知识库"
    :visible="visible"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-form-model layout="horizontal" :model="form" v-bind="formItemLayout">
      <a-form-model-item label="选择模板">
        <a-select v-model="form.template" :placeholder="'请选择模板'">
          <a-select-option
            :key="index"
            v-for="(item,index) in echo.templates"
            :value="item"
          >{{ item }}</a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="权限分配">
        <a-select v-model="form.permission" placeholder="请选择权限">
          <a-select-option
            :key="index"
            v-for="(item,index) in echo.permissions"
            :value="item"
          >{{ item }}</a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="知识库名称">
        <a-input v-model="form.name" placeholder="请填写知识库名称" />
      </a-form-model-item>
      <a-form-model-item label="知识库简介">
        <a-input v-model="form.introduction" type="textarea" placeholder="请填写知识库简介" />
      </a-form-model-item>
    </a-form-model>
  </a-modal>
</template>
<script>
export default {
  data () {
    return {
      visible: false,
      confirmLoading: false,
      form: {
        template: undefined, // 模板
        permission: undefined, // 权限分配
        introduction: '', // 简介
        name: '' // 名称
      },
      echo: {
        templates: ['模板1', '模板2', '模板3', '模板4'],
        permissions: ['仅自己', '全部', '可读', '可写']
      }
    }
  },
  props: {
   isNewBuild: { type: Boolean, default: false }
  },
  watch: {
    isNewBuild (oldval, newval) {
      console.log('oldval', oldval)
      console.log('newval', newval)
      this.visible = newval
    }
  },
  methods: {
    handleOk (e) {
      this.confirmLoading = true
      setTimeout(() => {
        this.visible = false
        this.confirmLoading = false
      }, 200)
    },
    handleCancel (e) {
      this.visible = false
    }
  },
  computed: {
    formItemLayout () {
      const { layout } = this.form
      return layout === 'horizontal' ? { labelCol: { span: 4 }, wrapperCol: { span: 14 } } : {}
    },
    buttonItemLayout () {
      const { layout } = this.form
      return layout === 'horizontal'
        ? {
            wrapperCol: { span: 14, offset: 4 }
          }
        : {}
    }
  },
  created () {

  },
  mounted () {

  },
  updated () {

  },
  activated () {
    console.log('新建知识库=>', this.isNewBuild)
    this.visible = this.isNewBuild
  },
  deactivated () {
    alert('组件停用')
  }
}
</script>
