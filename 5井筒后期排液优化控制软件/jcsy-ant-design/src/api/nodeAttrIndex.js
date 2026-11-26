import { axios } from '@/utils/serverRequest'

export function save (data) {
  return axios({
    url: `/nodeAttrIndex/save`,
    method: 'post',
    data
  })
}

export function update (data) {
    return axios({
        url: `/nodeAttrIndex/update`,
        method: 'post',
        data
    })
}

export function remove (data) {
return axios({
    url: `/nodeAttrIndex/remove`,
    method: 'post',
    data
})
}
