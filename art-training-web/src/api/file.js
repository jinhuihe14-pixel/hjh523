import request from '@/utils/request'

export function uploadFile(files) {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  return request({
    url: '/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function deleteFile(url) {
  return request({
    url: '/file',
    method: 'delete',
    params: { url }
  })
}
