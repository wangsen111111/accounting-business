import axios from 'axios'

/**
 * 统一的 axios 实例。
 * 后端返回 { code, message, data }，这里在拦截器里直接解包 data，
 * 业务代码拿到的就是纯数据对象。
 */
const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 15000
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('gfecmp_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && typeof body.code === 'number') {
      if (body.code === 0) return body.data
      return Promise.reject(new Error(body.message || '接口返回异常'))
    }
    return body
  },
  (error) => {
    const message =
      error.code === 'ECONNABORTED'
        ? '请求超时，请确认后端服务已启动（默认 8080 端口）'
        : error.message || '网络异常'
    return Promise.reject(new Error(message))
  }
)

export default http
