import request from '@/utils/api'

// 获取所有权限
export function getAllPermissions() {
  return request({
    url: '/permissions/all',
    method: 'get'
  })
}

// 获取树形结构权限列表
export function getPermissionTree() {
  return request({
    url: '/permissions/tree',
    method: 'get'
  })
}

// 根据用户ID获取权限列表
export function getPermissionsByUserId(userId) {
  return request({
    url: `/permissions/user/${userId}`,
    method: 'get'
  })
}

