import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 120000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加token到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data

    // 如果响应码不是200，则判断为错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')

      // 401: 未登录或token过期
      if (res.code === 401) {
        // 清除本地存储
        localStorage.removeItem('token')
        localStorage.removeItem('isLoggedIn')
        localStorage.removeItem('userInfo')
        // 跳转到登录页
        window.location.href = '/login'
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    const message = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(message)
    
    // 处理401未授权错误
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
    }
    
    return Promise.reject(error)
  }
)

export default request

