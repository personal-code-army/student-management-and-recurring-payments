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

export function isValidEmail(value: string): boolean {
  const trimmed = value.trim()
  if (!trimmed) return true
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmed)
}
