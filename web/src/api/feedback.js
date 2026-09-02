import request from '@/utils/api'

// 提交反馈
export function submitFeedback(data) {
  return request({
    url: '/feedback/submit',
    method: 'post',
    data
  })
}

// 获取反馈列表（用户）
export function getUserFeedback(params) {
  return request({
    url: `/feedback/user/${params.userId}`,
    method: 'get',
    params: {
      pageNum: params.pageNum || 1,
      pageSize: params.pageSize || 10
    }
  })
}
