import { axios } from '@/utils/serverRequest'

// collection收藏
export function collection (data) {
    return axios({
      url: `/collection/insert`,
      method: 'post',
      data: data
    })
  }

 export async function collectionList () {
  return axios({
    url: `/collection/list`,
    method: 'get'
  })
}
