const getters = {
  device: state => state.app.device,
  theme: state => state.app.theme,
  color: state => state.app.color,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  nickname: state => state.user.name,
  welcome: state => state.user.welcome,
  roles: state => state.user.roles,
  userInfo: state => state.user.info,
  addRouters: state => state.permission.addRouters,
  multiTab: state => state.app.multiTab,
  lang: state => state.i18n.lang,
  counts: (state) => state.count, // 测试声明全局变量
  changeJsmindMap: (state) => state.changeJsmindMap,
  mountFileIds: (state) => state.mountFileIds,
  isSave: (state) => state.isSave,
  openJsminds: (state) => state.openJsminds,
  copyNode: (state) => state.copyNode,
  globalHeader: (state) => state.app.globalHeader,
  showToolBar: (state) => state.app.showToolBar,
  isMountFiles: (state) => state.isMountFiles // 判断是否挂载
}

export default getters
