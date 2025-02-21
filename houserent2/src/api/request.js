import axios from 'axios'
import store from '@/store'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api', // 这里是后端接口地址示例
  timeout: 5000,
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = store.state.user.token
    if (token) {
      // 如果有 token ，在请求头中携带
      config.headers['Authorization'] = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    // 可根据后端自定义的响应格式进行处理
    const res = response.data
    // 例如后端约定 code !== 200 为错误
    if (res.code !== 200) {
      // 做一些提示或处理
      return Promise.reject(res.message || 'Error')
    }
    return res
  },
  (error) => {
    return Promise.reject(error)
  }
)

export default service
