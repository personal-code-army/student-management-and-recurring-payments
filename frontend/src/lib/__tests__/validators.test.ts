import { describe, expect, it } from "vitest"
import { formatCpf, isValidEmail, normalizeCpf } from "../validators"

describe("validacoes de alunos", () => {
  it("considera e-mail valido quando vazio ou bem formatado", () => {
    expect(isValidEmail("")).toBe(true)
    expect(isValidEmail("joao@email.com")).toBe(true)
  })

  it("marca e-mail invalido quando falta @ ou dominio", () => {
    expect(isValidEmail("joaoemail.com")).toBe(false)
    expect(isValidEmail("joao@")).toBe(false)
  })

  it("normaliza CPF removendo caracteres e limitando 11 digitos", () => {
    expect(normalizeCpf("123.456.789-00")).toBe("12345678900")
    expect(normalizeCpf("12345678900123")).toBe("12345678900")
  })

  it("formata CPF completo com mascara", () => {
    expect(formatCpf("12345678900")).toBe("123.456.789-00")
  })
})
