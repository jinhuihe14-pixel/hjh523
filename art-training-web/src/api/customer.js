import request from '@/utils/request'

export function getCustomerPage(params) {
  return request({
    url: '/customer/page',
    method: 'get',
    params
  })
}

export function getCustomerList(params) {
  return request({
    url: '/customer/list',
    method: 'get',
    params
  })
}

export function getCustomerDetail(id) {
  return request({
    url: `/customer/${id}`,
    method: 'get'
  })
}

export function getCustomerByNo(customerNo) {
  return request({
    url: `/customer/no/${customerNo}`,
    method: 'get'
  })
}

export function addCustomer(data) {
  return request({
    url: '/customer',
    method: 'post',
    data
  })
}

export function updateCustomer(data) {
  return request({
    url: '/customer',
    method: 'put',
    data
  })
}

export function deleteCustomer(id) {
  return request({
    url: `/customer/${id}`,
    method: 'delete'
  })
}

export function deleteBatchCustomer(ids) {
  return request({
    url: '/customer/batch',
    method: 'delete',
    data: ids
  })
}

export function updateCustomerStatus(id, status) {
  return request({
    url: '/customer/status',
    method: 'put',
    params: { id, status }
  })
}

export function getCustomerConsumePage(params) {
  return request({
    url: '/customer/consume/page',
    method: 'get',
    params
  })
}

export function getCustomerConsumeList(customerId) {
  return request({
    url: '/customer/consume/list',
    method: 'get',
    params: { customerId }
  })
}

export function addCustomerConsume(data) {
  return request({
    url: '/customer/consume',
    method: 'post',
    data
  })
}

export function getCustomerOperationLog(id) {
  return request({
    url: `/customer/operationLog/${id}`,
    method: 'get'
  })
}
