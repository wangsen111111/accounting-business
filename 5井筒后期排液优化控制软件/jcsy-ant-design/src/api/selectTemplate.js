import { axios } from '@/utils/serverRequest'

// 根据项目ID查询所有文件
export function GetDimensions () {
  return axios({
    url: '/dimension/list/',
    method: 'get'
  })
}

// 得到所有思维导图模板
export function GetMindMapTemplate () {
  return axios({
    url: '/mind-map-template/list/',
    method: 'get'
  })
}

// 得到所有的项目
export function GetProjects () {
  return axios({
    url: '/project/list/mindMap/sameLayer',
    method: 'get'
  })
}

// 得到所有的思维导图
export function GetJsMinds () {
  return axios({
    url: '/jsmind/list',
    method: 'get'
  })
}

// 添加思维导图
export function PostsJsMinds (data) {
  return axios({
    url: '/jsmind/add',
    method: 'post',
    data
  })
}
