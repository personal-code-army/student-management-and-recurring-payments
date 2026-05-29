import axios from "axios"

export type ApiErrorInfo = {
  code?: string
  status?: number
  message?: string
}

type ApiErrorCopy = {
  title: string
  description: string
  detail?: string
}

const DEFAULT_COPY: ApiErrorCopy = {
  title: "Erro inesperado",
  description: "Falha na conexao com o servidor.",
}

const CODE_COPY: Record<string, ApiErrorCopy> = {
  AUTH_INVALID_CREDENTIALS: {
    title: "Credenciais invalidas",
    description: "Verifique seu e-mail e senha e tente novamente.",
  },
  VALIDATION_ERROR: {
    title: "Dados invalidos",
    description: "Revise os campos e tente novamente.",
  },
  ENTITY_NOT_FOUND: {
    title: "Nao encontrado",
    description: "Nao foi possivel localizar o recurso solicitado.",
  },
  EMAIL_ALREADY_EXISTS: {
    title: "E-mail ja cadastrado",
    description: "Use outro e-mail para continuar.",
  },
  CPF_ALREADY_REGISTERED: {
    title: "CPF ja cadastrado",
    description: "Use outro CPF para continuar.",
  },
  SUBSCRIPTION_ALREADY_EXISTS: {
    title: "Assinatura ja existente",
    description: "O aluno ja possui uma assinatura ativa.",
  },
  SUBSCRIPTION_PENDING_PAYMENT: {
    title: "Pagamento pendente",
    description: "Existem pagamentos pendentes ou vencidos para este aluno.",
  },
  NOT_FOUND: {
    title: "Nao encontrado",
    description: "Nao foi possivel localizar o recurso solicitado.",
  },
  CONFLICT: {
    title: "Conflito de dados",
    description: "Ja existe um registro com essas informacoes.",
  },
  BAD_REQUEST: {
    title: "Requisicao invalida",
    description: "Revise os dados enviados e tente novamente.",
  },
  UNAUTHORIZED: {
    title: "Nao autorizado",
    description: "Faca login para continuar.",
  },
  INTERNAL_SERVER_ERROR: {
    title: "Erro interno",
    description: "Ocorreu um erro no servidor. Tente novamente mais tarde.",
  },
}

const STATUS_COPY: Record<number, ApiErrorCopy> = {
  400: CODE_COPY.BAD_REQUEST,
  401: CODE_COPY.UNAUTHORIZED,
  404: CODE_COPY.NOT_FOUND,
  409: CODE_COPY.CONFLICT,
  500: CODE_COPY.INTERNAL_SERVER_ERROR,
}

export function getApiErrorInfo(error: unknown): ApiErrorInfo | null {
  if (!axios.isAxiosError(error)) {
    return null
  }

  const status = error.response?.status
  const data = error.response?.data
  const dataObject =
    typeof data === "object" && data !== null
      ? (data as { code?: unknown; error?: unknown; message?: unknown })
      : undefined

  const code = typeof dataObject?.code === "string" ? dataObject.code : undefined
  const message =
    typeof data === "string"
      ? data
      : typeof dataObject?.error === "string"
        ? dataObject.error
        : typeof dataObject?.message === "string"
          ? dataObject.message
          : error.message

  return { code, status, message }
}

export function resolveApiErrorContent(info?: ApiErrorInfo | null): ApiErrorCopy {
  if (!info) {
    return DEFAULT_COPY
  }

  const base =
    (info.code && CODE_COPY[info.code]) ||
    (info.status && STATUS_COPY[info.status]) ||
    DEFAULT_COPY

  const detail = info.message?.trim() ? info.message : undefined
  return { ...base, detail }
}
