<template>
  <div>
    <a-card :bordered="false" style="min-height: calc(100vh - 100px);">
      <span class="centered">
        <a-steps
          style="width:50vw;height:7vh;display:flex;justify-content: center;align-items: center;"
          :current="current"
          @change="onChange"
        >
          <a-step title="新建项目" description />
          <a-step title="选择模板" description />
          <a-step title="上传文件" description />
        </a-steps>
      </span>
      <a-divider />
      <div style="width:100vw" class="centered">
        <a-form
          :form="form"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 12 }"
          @submit="handleSubmit"
          style="width:100vw"
        >
          <a-form-item label="项目名称">
            <a-input
              v-decorator="['project_name', { rules: [{ required: true, message: '请输入项目名称' }] }]"
              allow-clear
            />
          </a-form-item>
          <a-form-item label="项目权限">
            <a-select
              v-decorator="['power', { rules: [{ required: true, message: '请选择项目权限' }] }]"
              placeholder
              @change="handleSelectChange"
            >
              <a-select-option value="male">权限1</a-select-option>
              <a-select-option value="female">权限2</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="项目简介">
            <!-- introduction -->
            <a-textarea style="height:10vw" allow-clear />
            <!-- v-decorator="['note', { rules: [{ required: true, message: '请输入项目简介' }] }]" -->
            <!-- :auto-size="{ minRows: 8, maxRows: 15 }" -->
          </a-form-item>
          <a-divider />
          <span style="100vw" class="centered">
            <a-button type="primary" @click="cancel">取消</a-button>
            <a-button type="primary" style="margin-left:5%" html-type="submit">下一步</a-button>
          </span>
        </a-form>
      </div>
    </a-card>
  </div>
</template>

<script>
// import { AddProjectSimple, EditProjectSimple } from '@/api/createProject.js'
import { GetProjectNames } from '@/api/createProject.js'
export default {
  data () {
    return {
      current: 0,
      thisCurrent: 0,
      projectNames: [],
      formLayout: 'horizontal',
      form: this.$form.createForm(this, { name: 'coordinated' })
    }
  },
  created () {
    this.getParams()
    const this_ = this
    GetProjectNames().then((res) => {
      if (res.data && res.data.length > 0) {
        this_.projectNames = res.data
      }
    })
  },
  watch: {
    // $route: 'getParams'
  },
  methods: {
    onChange (current) {
      if (this.thisCurrent >= current) {
        this.current = current
      }
    },
    getParams: function () {
      this.params = this.$route.params
    },
    cancel () {
      if (this.params.routeName !== undefined) {
        this.$router.push({ name: this.params.routeName })
      } else {
        this.$router.push({ name: 'WorkSurface' })
      }
    },
    handleSubmit (e) {
      const this_ = this
      e.preventDefault()
      this.form.validateFields((err, values) => {
        if (!err) {
          const index = this_.projectNames.findIndex((pro) => pro === values.project_name)
          if (index > -1) {
            this_.$message.error('项目名不允许重复~')
          } else {
            // console.log('Received values of form: ', values)
            // this.$emit('next-step', values)
            this_.$router.replace({ name: 'SelectTemplate', params: { project: values } })
          }
        }
      })
    },
    handleSelectChange (value) {
      // console.log(value)
    }
  }
}
</script>
<style>
.centered {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
