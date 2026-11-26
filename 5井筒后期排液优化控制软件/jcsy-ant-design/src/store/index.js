import Vue from 'vue'
import Vuex from 'vuex'

import app from './modules/app'
import user from './modules/user'

// default router permission control
import permission from './modules/permission'

// dynamic router permission control (Experimental)
// import permission from './modules/async-router'
import getters from './getters'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    app,
    user,
    permission
  },
  state: {
    count: 0,
    show: ''
  },
  // state: sessionStorage.getItem('state') ? JSON.parse(sessionStorage.getItem('state')) : {
  //   count: 0,
  //   show: ''
  // },
  mutations: {
    increment: (state) => { state.count++ },
    decrement: (state) => { state.count-- },
    saveChangeJsmindMap: (state, jsmindData) => {
      if (!state.changeJsmindMap) { state.changeJsmindMap = {} }
      Object.keys(jsmindData).forEach(key => {
        if (key === 'id') { state.changeJsmindMap[jsmindData[key]] = jsmindData }
      })
    },
    clearJsmindMap: (state) => { state.changeJsmindMap = {} },
    deleteAttrJsmindMap: (state, attrName) => {
      try {
        delete state.changeJsmindMap[attrName]
      } catch (error) {

      }
    },
    mountFileIds: (state, fileIds) => {
      if (!state.mountFileIds) { state.mountFileIds = {} }
      state.mountFileIds[fileIds[0]] = fileIds[1]
     },
    isSave: (state) => { state.isSave = true },
    openJsminds: (state, openJsminds) => { state.openJsminds = openJsminds },
    copyNode: (state, copyNode) => { state.copyNode = copyNode },
    mountSelectFiles: (state, mountSelectFiles) => { state.mountSelectFiles = mountSelectFiles },
    isMountFiles: (state, isMountFiles) => { state.isMountFiles = isMountFiles }
  },
  actions: {

  },
  getters
})

// export default new new Vuex.Store({
//   state: {
//   count: 0,
//   show: ''
//   },
//   getters: {
//     counts: (state) => {
//     return state.count
//     }
//   },
//   mutations: {
//     increment: (state) => {
//     state.count++
//     },
//     decrement: (state) => {
//     state.count--
//     },
//     changTxt: (state, v) => {
//     state.show = v
//     }
//   }
//  })()
