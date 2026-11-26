import { axios } from '@/utils/serverRequest'
import { axiosNet } from '@/utils/NetServerRequest'

// 新建知识库表单所需数据，查询模板
export function getTemplates () {
  return axios({
    url: '/getTemplates',
    method: 'get'
  })
}

// 上传文档表达所需数据
export function getTreeData () {
    return axios({
      url: '/getTreeData',
      method: 'get'
    //   data: parameter
    })
  }

// 下载重大任务文件
export function downloadFile (filenumber) {
  const data = {
    filenumber
  }
  return axiosNet({
    url: '/Jxxx/DownloadFile',
    method: 'get',
    params: data,
    responseType: 'blob'
  })
}
