import { axios } from '@/utils/serverRequest'

// 获取所有维度
export function getDimensions () {
    return axios({
      url: `/dimension/list`,
      method: 'get'
    })
  }
