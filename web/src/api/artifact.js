import request from '@/utils/api'

// 获取文物列表
export function getArtifacts(params) {
  return request({
    url: '/relics',
    method: 'get',
    params: {
      pageNum: params.pageNum || 1,
      pageSize: params.pageSize || 20,
      era: params.era,
      material: params.material,
      typeName: params.type, // 前端字段 'type' 映射到 后端 'typeName'
      discoverySite: params.location // 前端字段 'location' 映射到 后端 'discoverySite'
    }
  })
}

// 获取文物详情
export function getArtifactDetail(id) {
  return request({
    url: `/relics/${id}`,
    method: 'get'
  })
}

export function getRelicEras() {
  return request({
    url: '/relics/eras',
    method: 'get'
  })
}

export function getRelicMaterials() {
  return request({
    url: '/relics/materials',
    method: 'get'
  })
}

export function getRelicTypes() {
  return request({
    url: '/relics/types',
    method: 'get'
  })
}

export function getRelicDiscoverySites() {
  return request({
    url: '/relics/discovery-sites',
    method: 'get'
  })
}

export function searchRelics(params) {
  return request({
    url: '/relics/search',
    method: 'get',
    params: {
      q: params.q || '',
      semantic: params.semantic === undefined ? true : params.semantic,
      related: params.related === undefined ? false : params.related,
      pageNum: params.pageNum || 1,
      pageSize: params.pageSize || 10
    }
  })
}
