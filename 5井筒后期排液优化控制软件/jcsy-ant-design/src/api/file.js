import { axios } from '@/utils/serverRequest'

// 根据项目ID更新文件
export function update (file) {
  return axios({
    url: '/file/update',
    method: 'post',
    data: file
  })
}

// 获取文件类型
export function getFileTypes () {
  return axios({
    url: '/file/getFileTypes',
    method: 'get'
  })
}

// 根据条件获取全部文件
export function getFileByParam (data) {
  return axios({
    url: '/file/getFileByParam',
    method: 'post',
    data
  })
}

// 根据项目ID查询所有文件
export function GetFiles (projectId) {
  return axios({
    url: '/file/list/detail/' + projectId,
    method: 'get'
  })
}

// 根据文件ID删除文件和关联关系
export function DeleteFile (fileId) {
  return axios({
    url: '/file/delete/' + fileId,
    method: 'delete'
  })
}

// 获取所有标签
export async function getTags () {
  return axios({
    url: `/tag/list`,
    method: 'get'
  })
}

// list/{pageNum}/{pageCount} 分页获取文件信息
export async function getFilsByPage (pageNum, pageCount, data) {
  return axios({
    url: `/file/list/${pageNum}/${pageCount}`,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data
  })
}

// list/{pageNum}/{pageCount} 分页获取文件信息
export async function listByFileIds (data) {
  return axios({
    url: `/file/list/fileIds`,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data
  })
}

// 删除
export function deleteFileByIds (ids) {
  return axios({
    url: `/file/deleteFileByIds`,
    method: 'post',
    data: ids
  })
}

// download 根据文件集合下载
export function downloadFileByIds (ids) {
  return axios({
    url: `/file/downloadFileByIds`,
    method: 'post',
    responseType: 'blob',
    dataType: 'JSON',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data: ids
  })
}

// download 根据思维导图下载
export function downloadByMindMap (jsmind) {
  return axios({
    url: `/file/downloadByMindMap`,
    method: 'post',
    responseType: 'blob',
    dataType: 'JSON',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data: JSON.stringify(jsmind)
  })
}

// 修改文件标签editFileTag
export function editFileTag (tags, fileId) {
  return axios({
    url: `/file/editFileTag/${fileId}`,
    method: 'post',
    dataType: 'JSON',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data: tags
  })
}

// 根据id卸载文件uninstallFile
export function uninstallFile (fileId, nodeId) {
  return axios({
    url: `/file/uninstall/${fileId}/${nodeId}`,
    method: 'put'
  })
}

// 根据项目id和检索条件查询文件 getPageFileByProjectId

// list/{pageNum}/{pageCount} 分页获取文件信息 pageNum, pagesize, projectId, key
export async function getPageFileByProjectId (pageNo, pageSize, parmMap) {
  return axios({
    url: `/file/listPreview/${pageNo}/${pageSize}`,
    method: 'post',
    dataType: 'JSON',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data: parmMap
  })
}

// 文件上传
export async function uploadFile (data) {
  return axios({
    url: `/file/upload`,
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data
  })
}

// fileChain
export async function fileChain (data, projectId) {
  return axios({
    url: `/file/fileChain/${projectId}`,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data: data
  })
}

// fileChainMount文件外链挂载
export async function fileChainMount (data) {
  return axios({
    url: `/file/fileChain/mount`,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data: data
  })
}

// 统计文件类型数量
export async function countFileType (projectId) {
  const url = projectId == null ? `/file/countFileType` : `/file/countFileType/${projectId}`
  return axios({
    url: url,
    method: 'get'
  })
}

// 根据文件id获取文件信息
export function getFileByFileId (fileId) {
  return axios({
    url: '/file/getFileByFileId/' + fileId,
    method: 'get'
  })
}

export function mytest (data) {
  return axios({
    url: `/test/test`,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data: data
  })
}
