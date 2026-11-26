import Vue from 'vue'
import router from './router'
import store from './store'

import NProgress from 'nprogress' // progress bar
import '@/components/NProgress/nprogress.less' // progress bar custom style
import notification from 'ant-design-vue/es/notification'
import { setDocumentTitle, domTitle } from '@/utils/domUtil'
import { ACCESS_TOKEN } from '@/store/mutation-types'
import { updateMindMap } from '@/api/mindMap'

NProgress.configure({ showSpinner: false }) // NProgress Configuration

const whiteList = ['login', 'register', 'registerResult'] // no redirect whitelist
const defaultRoutePath = '/dashboard/workplace'

router.beforeEach((to, from, next) => {
  // 遍历changeJsmindMap
  if (from.path === '/page/MindMap') {
    debugger
    var vm = new Vue()
    const changeJsmindMap = store.state.changeJsmindMap
    // var changeJsmindList = [] JSON.stringify(changeJsmindMap) == "{}"
    if (JSON.stringify(changeJsmindMap) === '{}' || changeJsmindMap === undefined) {
      next()
    } else {
      var changeJsmindList = []
      Object.keys(changeJsmindMap).forEach(key => {
        changeJsmindList.push(changeJsmindMap[key])
      })
      var count = 0
      changeJsmindList.forEach(jsmind => {
        vm.$confirm({
          title: '是否保存思维导图【' + jsmind.mindMapName + '】?',
          content: '确定保存，取消不保存',
          onOk () {
            // 调用子组件更新方法
            count++
            if (count === changeJsmindList.length) {
              next()
              // 清除全局变量中的数据
              store.state.changeJsmindMap = {}
              store.state.mountFileIds = {}
            }
            updateMindMap(jsmind).then(res => {
              res.success ? vm.$message.success(jsmind.mindMapName + '保存成功') : vm.$message.error(jsmind.mindMapName + '保存失败')
            }).catch(error => vm.$message.error(jsmind.mindMapName + '保存异常', error))
          },
          onCancel () {
            count++
            if (count === changeJsmindList.length) {
              next()
              // 清除全局变量中的数据
              store.state.changeJsmindMap = {}
              store.state.mountFileIds = {}
            }
          }
        })
      })
    }
  } else {
    NProgress.start() // start progress bar
    to.meta && (typeof to.meta.title !== 'undefined' && setDocumentTitle(`${to.meta.title} - ${domTitle}`))
    if (Vue.ls.get(ACCESS_TOKEN)) {
      /* has token */
      if (to.path === '/user/login') {
        next({ path: defaultRoutePath })
        NProgress.done()
      } else {
        if (store.getters.roles.length === 0) {
          store
            .dispatch('GetInfo')
            .then(res => {
              const roles = res.result && res.result.role
              store.dispatch('GenerateRoutes', { roles }).then(() => {
                // 根据roles权限生成可访问的路由表
                // 动态添加可访问路由表
                router.addRoutes(store.getters.addRouters)
                // 请求带有 redirect 重定向时，登录自动重定向到该地址
                const redirect = decodeURIComponent(from.query.redirect || to.path)
                if (to.path === redirect) {
                  // hack方法 确保addRoutes已完成 ,set the replace: true so the navigation will not leave a history record
                  next({ ...to, replace: true })
                } else {
                  // 跳转到目的路由
                  next({ path: redirect })
                }
              })
            })
            .catch(() => {
              notification.error({
                message: '错误',
                description: '请求用户信息失败，请重试'
              })
              store.dispatch('Logout').then(() => {
                next({ path: '/user/login', query: { redirect: to.fullPath } })
              })
            })
        } else {
          next()
        }
      }
    } else {
      if (whiteList.includes(to.name)) {
        // 在免登录白名单，直接进入
        next()
      } else {
        next({ path: '/user/login', query: { redirect: to.fullPath } })
        NProgress.done() // if current page is login will not trigger afterEach hook, so manually handle it
      }
    }
  }
})

router.afterEach(() => {
  NProgress.done() // finish progress bar
})
