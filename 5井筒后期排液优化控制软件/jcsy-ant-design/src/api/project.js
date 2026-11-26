import { axios } from '@/utils/serverRequest'

// 获取所有项目
export function getProjects () {
  return axios({
    url: '/project/list',
    method: 'get'
  })
}

// 获取项目明细
export function getProjectDetail (projectId) {
  return axios({
    url: `/project/getProjectDetail/${projectId}`,
    method: 'get'
  })
}

// 分页获取所有项目
export async function getProjectsByPage (pageNum, pageCount, data) {
  return axios({
    url: `/project/listPage/${pageNum}/${pageCount}`,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data
  })
}

// importChain
export async function importChain (data, saveType) {
  return axios({
    url: `/project/import/chain/${saveType}`,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data
  })
}

// upload
export async function importUpload (data, saveType) {
  return axios({
    url: `/project/import/upload/${saveType}`,
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data
  })
}

// deleteProjectByIds
export async function deleteProjectByIds (data) {
  return axios({
    url: `/project/deleteProjectByIds`,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data
  })
}

// download下载
export function downloadByIds (projectId) {
  return axios({
    url: `/project/downloadByIds/${projectId}`,
    method: 'post',
    responseType: 'blob',
    dataType: 'JSON',
    headers: { 'Content-Type': 'application/json;charset=utf-8' }
    // data: ids
  })
}

// 获取热度标签及该标签下文件数
export async function getHotTagWithFileCount (id) {
  return axios({
    url: '/tag/getHotTagWithFileCount?projectId=' + id,
    method: 'get'
  })
}

// getProjectBaseInfo
export async function getProjectBaseInfo (projectId) {
  return axios({
    url: `/project/getProjectBaseInfo/${projectId}`,
    method: 'get'
  })
}

// update更新项目
export async function update (data) {
  return axios({
    url: `/project/update`,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data
  })
}
