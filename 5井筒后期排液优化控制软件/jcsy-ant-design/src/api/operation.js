import { axios } from '@/utils/serverRequest'

// 获取最近操作思维导图
// export function getOperationJsminds () {
//   return axios({
//     url: `/operation/list/jsmind`,
//     method: 'get'
//   })
// }

// 根据项目ID查询所有文件
export function getOperations (pageNo, pageSize, data) {
    return axios({
      url: `/operation/list/${pageNo}/${pageSize}`,
      method: 'post',
      data
    })
  }
