import { describe, expect, it } from "vitest"
import { formatCpf, isValidBirthDate, isValidCpf, isValidEmail, normalizeCpf } from "../validators"

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

  it("valida CPF com digitos verificadores corretos", () => {
    expect(isValidCpf("529.982.247-25")).toBe(true)
    expect(isValidCpf("52998224725")).toBe(true)
  })

  it("rejeita CPF com digitos verificadores incorretos", () => {
    expect(isValidCpf("123.456.789-00")).toBe(false)
  })

  it("rejeita CPF com todos os digitos iguais", () => {
    expect(isValidCpf("111.111.111-11")).toBe(false)
    expect(isValidCpf("000.000.000-00")).toBe(false)
  })

  it("rejeita CPF com tamanho incorreto", () => {
    expect(isValidCpf("123.456")).toBe(false)
  })
})

describe("validacao de data de nascimento", () => {
  it("considera vazio como valido", () => {
    expect(isValidBirthDate("")).toBe(true)
  })

  it("aceita data passada valida", () => {
    expect(isValidBirthDate("1990-06-15")).toBe(true)
  })

  it("rejeita data futura", () => {
    expect(isValidBirthDate("2099-01-01")).toBe(false)
  })

  it("rejeita data de hoje", () => {
    const hoje = new Date().toISOString().slice(0, 10)
    expect(isValidBirthDate(hoje)).toBe(false)
  })

  it("rejeita data com mais de 120 anos", () => {
    expect(isValidBirthDate("1900-01-01")).toBe(false)
  })

  it("rejeita string invalida", () => {
    expect(isValidBirthDate("nao-e-data")).toBe(false)
  })
})
