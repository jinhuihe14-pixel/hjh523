export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

export function formatMoney(value, decimals = 2) {
  if (value === null || value === undefined || isNaN(value)) return '0.00'
  return Number(value).toFixed(decimals)
}

export function deepClone(obj) {
  return JSON.parse(JSON.stringify(obj))
}

export function generateId() {
  return Date.now().toString(36) + Math.random().toString(36).substr(2)
}
