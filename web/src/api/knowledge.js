import request from '@/utils/api'

export function getRelicGraph(relicName) {
  return request({
    url: '/knowledge/neo4j/graph',
    method: 'get',
    params: { relicName }
  })
}
