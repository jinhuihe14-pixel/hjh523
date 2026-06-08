import request from '@/utils/request'

export function getContractPage(params) {
  return request({
    url: '/contract/page',
    method: 'get',
    params
  })
}

export function getContractDetail(id) {
  return request({
    url: `/contract/${id}`,
    method: 'get'
  })
}

export function getContractByNo(contractNo) {
  return request({
    url: `/contract/no/${contractNo}`,
    method: 'get'
  })
}

export function addContract(data) {
  return request({
    url: '/contract',
    method: 'post',
    data
  })
}

export function updateContract(data) {
  return request({
    url: '/contract',
    method: 'put',
    data
  })
}

export function deleteContract(id) {
  return request({
    url: `/contract/${id}`,
    method: 'delete'
  })
}

export function deleteBatchContract(ids) {
  return request({
    url: '/contract/batch',
    method: 'delete',
    data: ids
  })
}

export function updateContractStatus(id, status) {
  return request({
    url: '/contract/status',
    method: 'put',
    params: { id, status }
  })
}

export function getContractByCustomer(customerId) {
  return request({
    url: '/contract/customer/list',
    method: 'get',
    params: { customerId }
  })
}

export function getExpiringContract(days) {
  return request({
    url: '/contract/expiring/soon',
    method: 'get',
    params: { days }
  })
}

export function getContractOperationLog(id) {
  return request({
    url: `/contract/operationLog/${id}`,
    method: 'get'
  })
}
