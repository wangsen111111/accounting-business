import { axios } from '@/utils/serverRequest'

// 根据项目ID获取思维导图
export async function getNodeAttr () {
  return axios({
    url: `/nodeAttr/getNodeAttr`,
    method: 'get'
  })
}
