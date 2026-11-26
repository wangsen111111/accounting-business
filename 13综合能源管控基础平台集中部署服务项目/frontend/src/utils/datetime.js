/**
 * 时间格式化工具（不依赖第三方库，避免时区/locale 差异）
 */

const pad = (n) => String(n).padStart(2, '0')

/** 2026-11-10 15:31:07 */
export function formatDateTime(date) {
  const d = date instanceof Date ? date : new Date(date)
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(
    d.getMinutes()
  )}:${pad(d.getSeconds())}`
}

/** 15:31:07 */
export function formatTime(date) {
  const d = date instanceof Date ? date : new Date(date)
  return `${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}
