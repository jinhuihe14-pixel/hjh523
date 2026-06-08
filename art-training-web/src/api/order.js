import request from '@/utils/request'

export function getOrderPage(params) {
  return request({
    url: '/order/page',
    method: 'get',
    params
  })
}

export function getOrderDetail(id) {
  return request({
    url: `/order/${id}`,
    method: 'get'
  })
}

export function getOrderByNo(orderNo) {
  return request({
    url: `/order/no/${orderNo}`,
    method: 'get'
  })
}

export function getOrderItems(orderId) {
  return request({
    url: '/order/item/list',
    method: 'get',
    params: { orderId }
  })
}

export function addOrder(data) {
  return request({
    url: '/order',
    method: 'post',
    data
  })
}

export function updateOrder(data) {
  return request({
    url: '/order',
    method: 'put',
    data
  })
}

export function deleteOrder(id) {
  return request({
    url: `/order/${id}`,
    method: 'delete'
  })
}

export function deleteBatchOrder(ids) {
  return request({
    url: '/order/batch',
    method: 'delete',
    data: ids
  })
}

export function updateOrderStatus(id, status, remark) {
  return request({
    url: '/order/status',
    method: 'put',
    params: { id, status, remark }
  })
}

export function reviewOrder(id, remark, pass) {
  return request({
    url: '/order/review',
    method: 'put',
    params: { id, remark, pass }
  })
}

export function setOrderUrgent(id, urgent) {
  return request({
    url: '/order/urgent',
    method: 'put',
    params: { id, urgent }
  })
}

export function updateOrderPayment(id, receivedAmount, paymentStatus) {
  return request({
    url: '/order/payment',
    method: 'put',
    params: { id, receivedAmount, paymentStatus }
  })
}

export function getOrderByCustomer(customerId) {
  return request({
    url: '/order/customer/list',
    method: 'get',
    params: { customerId }
  })
}

export function getOrderOperationLog(id) {
  return request({
    url: `/order/operationLog/${id}`,
    method: 'get'
  })
}
