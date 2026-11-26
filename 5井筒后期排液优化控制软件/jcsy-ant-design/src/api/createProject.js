import { axios } from '@/utils/serverRequest'

// 添加简易项目
export function AddProjectSimple (data) {
  return axios({
    url: '/project/addSimple',
    method: 'post',
    data
  })
}

// 修改简易项目
export function EditProjectSimple (data) {
  return axios({
    url: '/project/update',
    method: 'post',
    data
  })
}

// 获取全部的项目名称
export function GetProjectNames () {
  return axios({
    url: '/project/getProjectNames',
    method: 'get'
  })
}
