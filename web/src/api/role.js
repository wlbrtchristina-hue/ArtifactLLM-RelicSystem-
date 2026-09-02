import request from '@/utils/api'

// 分页查询角色列表
export function getRolePage(params) {
  return request({
    url: '/roles',
    method: 'get',
    params: {
      current: params.current || 1,
      size: params.size || 10,
      name: params.name,
      status: params.status
    }
  })
}

// 获取所有角色
export function getAllRoles() {
  return request({
    url: '/roles/all',
    method: 'get'
  })
}

// 添加角色
export function addRole(data) {
  return request({
    url: '/roles',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(id, data) {
  return request({
    url: `/roles/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/roles/${id}`,
    method: 'delete'
  })
}

// 根据用户ID获取角色列表
export function getRolesByUserId(userId) {
  return request({
    url: `/roles/user/${userId}`,
    method: 'get'
  })
}

// 为用户分配角色
export function assignRolesToUser(userId, roleIds) {
  return request({
    url: `/roles/user/${userId}`,
    method: 'post',
    data: roleIds
  })
}

