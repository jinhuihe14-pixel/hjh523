import request from '@/utils/request'

export function getReportOverview(params) {
  return request({
    url: '/report/overview',
    method: 'get',
    params
  })
}

export function getReportByCustomer(params) {
  return request({
    url: '/report/byCustomer',
    method: 'get',
    params
  })
}

export function getReportByType(params) {
  return request({
    url: '/report/byType',
    method: 'get',
    params
  })
}

export function exportOrderReport(params) {
  return request({
    url: '/report/export/order',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function getOperationLogPage(params) {
  return request({
    url: '/report/operationLog/page',
    method: 'get',
    params
  })
}
