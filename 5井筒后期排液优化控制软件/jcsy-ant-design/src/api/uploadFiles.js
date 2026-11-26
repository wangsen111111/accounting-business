import { axios } from '@/utils/serverRequest'

// 查询所有标签
export function GetTags () {
  return axios({
    url: '/tag/list',
    method: 'get'
  })
}

// 添加新标签
export function AddTags (tag) {
  return axios({
    url: '/tag/insert',
    method: 'post',
    data: tag
  })
}

// 添加文件标签关联关系
export function AddFileTagRelations (fileTagRelations) {
  return axios({
    url: '/file-tag-relation/insert',
    method: 'post',
    data: fileTagRelations
  })
}

// 添加思维导图
export function AddJsMind (jsMind) {
  return axios({
    url: '/jsmind/add',
    method: 'post',
    data: jsMind
  })
}
