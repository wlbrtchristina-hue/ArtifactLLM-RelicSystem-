import request from '@/utils/api'

// 用户注册
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 用户登录
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 获取验证码
export function getCaptcha() {
  return request({
    url: '/user/captcha',
    method: 'get'
  })
}

// 发送邮箱验证码
export function sendEmailCode(email) {
  return request({
    url: '/user/email/code',
    method: 'post',
    params: { email }
  })
}

// 获取当前用户信息
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 用户登出
export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}
