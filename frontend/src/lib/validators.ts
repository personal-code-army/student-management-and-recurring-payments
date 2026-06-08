export function normalizeCpf(value: string): string {
  return value.replace(/\D/g, "").slice(0, 11)
}

export function formatCpf(value: string): string {
  const digits = normalizeCpf(value)
  if (!digits) return ""
  if (digits.length <= 3) return digits
  if (digits.length <= 6) return `${digits.slice(0, 3)}.${digits.slice(3)}`
  if (digits.length <= 9) return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6)}`
  return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6, 9)}-${digits.slice(9)}`
}

export function normalizePhone(value: string): string {
  return value.replace(/\D/g, "").slice(0, 11)
}

export function formatPhone(value: string): string {
  const digits = normalizePhone(value)
  if (!digits) return ""
  if (digits.length <= 2) return `(${digits}`
  if (digits.length <= 6) return `(${digits.slice(0, 2)}) ${digits.slice(2)}`
  if (digits.length <= 10) return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`
  return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7)}`
}

export function isValidCpf(value: string): boolean {
  const digits = value.replace(/\D/g, "")
  if (digits.length !== 11) return false
  if (/^(\d)\1{10}$/.test(digits)) return false

  let sum = 0
  for (let i = 0; i < 9; i++) sum += parseInt(digits[i]) * (10 - i)
  let first = 11 - (sum % 11)
  if (first >= 10) first = 0
  if (first !== parseInt(digits[9])) return false

  sum = 0
  for (let i = 0; i < 10; i++) sum += parseInt(digits[i]) * (11 - i)
  let second = 11 - (sum % 11)
  if (second >= 10) second = 0
  return second === parseInt(digits[10])
}

export function isValidEmail(value: string): boolean {
  const trimmed = value.trim()
  if (!trimmed) return true
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmed)
}

export function isValidBirthDate(value: string): boolean {
  if (!value) return true
  const date = new Date(value + "T00:00:00")
  if (isNaN(date.getTime())) return false
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  if (date >= today) return false
  const minDate = new Date(today)
  minDate.setFullYear(today.getFullYear() - 120)
  return date >= minDate
}
