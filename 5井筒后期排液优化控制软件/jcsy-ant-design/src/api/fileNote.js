import { axios } from '@/utils/serverRequest'

// 根据文件id获取文件笔记
export async function getFileNoteByFileId (fileId) {
  return axios({
    url: `/note/list/${fileId}`,
    method: 'get'
  })
}

// 根据笔记id获更新笔记
export async function updataFileNoteById (fileNote) {
  return axios({
    url: `/note/update`,
    method: 'post',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data: fileNote
  })
}
// removeFileNoteById
export async function removeFileNoteById (id) {
  return axios({
    url: `/note/removeFileNoteById/${id}`,
    method: 'get'
  })
}

// 导出文件笔记
export async function exportNoteByFile (file) {
  return axios({
    url: `/note/exportNoteByFile`,
    method: 'post',
    responseType: 'blob',
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
    data: file
  })
}
