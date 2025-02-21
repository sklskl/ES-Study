import request from './request'

export function loginApi(data) {
  return request({
    url: 'users/login',
    method: 'get',
    params: data,
  })
}
