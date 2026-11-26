import { axios } from '@/utils/serverRequest'

const baseUrl = ''

// 获取项目及该项目下文件数
export async function getProjectsAndFileCount () {
  return axios({
    url: baseUrl + 'project/getProjectsAndFileCount',
    method: 'get'
  })
}

// 获取热度标签及该标签下文件数
export async function getHotTagWithFileCount () {
  return axios({
    url: baseUrl + 'tag/getHotTagWithFileCount',
    method: 'get'
  })
}

// 获取项目进度
// export function getProjectProgressOf () {
//   return axios({
//     url: baseUrl + 'project/getProjectProgressOf',
//     method: 'get'
//   })
// }
