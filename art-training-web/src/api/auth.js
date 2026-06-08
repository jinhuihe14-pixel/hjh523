import request from '@/utils/request'

export function login(data) {
  return request({
    url: 'http://localhost:8082/auth-center/api/auth/login',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: 'http://localhost:8082/auth-center/api/auth/logout',
    method: 'post'
  })
}

export function getUserInfo() {
  return request({
    url: 'http://localhost:8082/auth-center/api/auth/info',
    method: 'get'
  })
}
