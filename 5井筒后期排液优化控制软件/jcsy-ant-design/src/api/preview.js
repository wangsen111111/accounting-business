import { axios } from '@/utils/serverRequest'

// word文件预览
export function getHtmlByWord (data) {
  return axios({
    url: '/preview/wordToHtml',
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data
    // responseType: 'blob'
  })
}

// 获取外链文件的服务路径
export function getServiceUrl (data) {
  return axios({
    url: `/preview/serviceUrl`,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data
  })
}

// 获取ppt服务路径
export function getPPTServiceUrl (data) {
  return axios({
    url: `/preview/getPPTServiceUrl`,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data
  })
}
