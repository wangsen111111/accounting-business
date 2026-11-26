import { axios } from '@/utils/serverRequest'

export function associated (data) {
  return axios({
    url: `/jsmind/associated`,
    method: 'post',
    data
  })
}

// 获取拷贝节点
export function getCopyNodeByNodeId (nodeId, jsmind) {
  return axios({
    url: `/jsmind/${nodeId}/getCopyNodeByNodeId`,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data: jsmind
  })
}

// 获取拷贝节点
export function pasteNode (data) {
  return axios({
    url: `/jsmind/pasteNode`,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data
  })
}

// 获取思维导图关联数据
export function getAssociated (jsmindId) {
  const url = jsmindId ? `/jsmind/getAssociated?jsmindId=${jsmindId}` : `/jsmind/getAssociated`
  return axios({
    url: url,
    method: 'get'
  })
}

// 根据项目ID获取思维导图
export function getAllMindMap () {
  return axios({
    url: `/jsmind/list`,
    method: 'get'
  })
}

// 根据项目ID获取思维导图
export function getMindMapByProjectId (projectId) {
  return axios({
    url: `/jsmind/list/${projectId}`,
    method: 'get'
  })
}

// 根据维度ID获取思维导图
export function getMindMapByDimensionId (dimensionId, projectId) {
  return axios({
    url: `/jsmind/getMindMapByDimensionId/${dimensionId}/${projectId}`,
    method: 'get'
  })
}

// 根据思维导图ID获取思维导图
export function getMindMapById (jsmindId) {
  return axios({
    url: `/jsmind/getMindMapById/${jsmindId}`,
    method: 'get'
  })
}

// 根据项目ID删除思维导图
export function removeMindMapByid (mindMapId) {
  return axios({
    url: `/jsmind/remove/${mindMapId}`,
    method: 'get'
  })
}

// 添加思维导图
export function addMindMap (data) {
  return axios({
    url: '/jsmind/add',
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data: data
  })
}

// 更新思维导图
export function updateMindMap (data) {
  return axios({
    url: '/jsmind/update',
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data: data
  })
}

// 更新思维导图
export function saveMindMapToTemplate (data, name) {
  return axios({
    url: '/jsmind/add/template?name=' + name,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data: data
  })
}

// uninstallFile文件卸载，根据文件id和思维导图卸载
export function uninstallFile (jsmindId, nodeId, fileId) {
  return axios({
    url: `/jsmind/uninstallFile/${jsmindId}/${nodeId}/${fileId}`,
    method: 'get'
  })
}

// 获取所有项目
export function getProjects () {
  return axios({
    url: '/project/list',
    method: 'get'
  })
}

// 根据维度查询思维导图
export function getJsmindBydimensionId (id) {
  return axios({
    url: `/jsmind/getJsmindBydimensionId/${id}`,
    method: 'get'
  })
}

// 根据维度查询思维导图
export function countNodeFile (projectId) {
  const url = projectId == null ? `/jsmind/countNodeFile` : `/jsmind/countNodeFile/${projectId}`
  return axios({
    url: url,
    method: 'get'
  })
}

// countTag
export function countTag (projectId) {
  return axios({
    url: `/jsmind/countTag/${projectId}`,
    method: 'get'
  })
}

// 思维导图导图
export async function inputFile (data) {
  return axios({
    url: `/jsmind/import`,
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data
  })
}

// getTemplate 获取模板
export function getTemplate () {
  return axios({
    url: `/jsmind/getTemplate`,
    method: 'get'
  })
}

// getOperationJsminds 获取最近操作思维导图
export function getOperationJsminds () {
  return axios({
    url: `/jsmind/getOperationJsminds`,
    method: 'get'
  })
}

// 校验思维导图名称是否存在
export function checkMindMapName (name) {
  return axios({
    url: `/jsmind/checkMindMapName` + '?name=' + name,
    method: 'get'
  })
}
