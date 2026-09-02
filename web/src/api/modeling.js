import request from '@/utils/api'

// 保存模型定义
export function saveModelDef(data) {
  return request({
    url: '/modeling/define',
    method: 'post',
    data
  })
}

// 获取模型定义详情
export function getModelDef(id) {
  return request({
    url: `/modeling/define/${id}`,
    method: 'get'
  })
}

// 获取模型列表
export function listModels() {
  return request({
    url: '/modeling/list',
    method: 'get'
  })
}

// 保存实体实例数据
export function saveInstance(data) {
  return request({
    url: '/modeling/instance',
    method: 'post',
    data
  })
}

// 获取实体实例列表
export function listInstances(params) {
  return request({
    url: '/modeling/instance/list',
    method: 'get',
    params
  })
}
