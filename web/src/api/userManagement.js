import request from '@/utils/api'

// 分页查询用户列表
export function getUserPage(params) {
  return request({
    url: '/users',
    method: 'get',
    params: {
      current: params.current || 1,
      size: params.size || 10,
      username: params.username
    }
  })
}

// 根据ID获取用户
export function getUserById(id) {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

// 更新用户
export function updateUser(id, data) {
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

// 批量删除用户
export function batchDeleteUsers(ids) {
  return request({
    url: '/users/batch',
    method: 'delete',
    data: ids
  })
}

// 切换用户状态
export function toggleUserStatus(id, status) {
  return request({
    url: `/users/${id}/status`,
    method: 'put',
    params: { status }
  })
}

