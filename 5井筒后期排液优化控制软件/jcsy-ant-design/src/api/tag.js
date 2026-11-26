import { axios } from '@/utils/serverRequest'

// 获取所有标签
export async function getTags () {
  return axios({
    url: '/tag/list/',
    method: 'get'
  })
}

// 标签检索数据
export async function tagScreening () {
  return axios({
    url: '/tag/tagScreening/',
    method: 'get'
  })
}

// 获取热度标签及该标签下文件数
export async function getHotTagWithFileCount () {
  return axios({
    url: 'tag/getHotTagWithFileCount',
    method: 'get'
  })
}

// 多标签挂载
export async function multipleTagMount (data) {
  return axios({
    url: 'tag/multipleTagMount',
    method: 'post',
    data
  })
}

// 单标签挂载
export async function singleTagMount (data, model) {
  var url = model ? 'tag/singleTagMount' : 'tag/singleTagMount?model=' + model
  return axios({
    url: url,
    method: 'post',
    data
  })
}
