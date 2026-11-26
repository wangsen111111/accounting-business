import { axios } from '@/utils/serverRequest'

// 根据项目ID查询所有文件
export function getOperations (pageNo, pageSize, data) {
  return axios({
    url: `/operation/list/${pageNo}/${pageSize}`,
    method: 'post',
    data
  })
}
