import { api } from "@/lib/api"

// ─── Types ────────────────────────────────────────────────────────────────────

export interface Payment {
  id: number
  subscriptionId: number
  description: string
  value: number
  paymentMethod: string | null
  dueDate: string        // "YYYY-MM-DD"
  issueDate: string | null
  status: string         // "A receber" | "Pago" | "Vencido"
}

export interface PaymentRequest {
  subscriptionId: number | null
  description: string
  value: number
  paymentMethod: string | null
  dueDate: string
  issueDate: string | null
  status: string
}

/**
 * Subscription retornada por GET /api/subscriptions
 * Usada apenas para popular o select do formulário.
 */
export interface Subscription {
  id: number
  studentId: number
  planId: number
  startDate: string
  status: string         // "Ativo" | "Inativo" | "Cancelado"
  paymentMethod: string | null
}

type ApiResponse<T> = { data: T }

// ─── Service ──────────────────────────────────────────────────────────────────

export const paymentService = {
  /** GET /api/payments — todos os pagamentos */
  async findAll(): Promise<Payment[]> {
    const res = await api.get<ApiResponse<Payment[]>>("/api/payments")
    return Array.isArray(res.data?.data) ? res.data.data : []
  },

  /** GET /api/payments?subscriptionId={id} */
  async findBySubscription(subscriptionId: number): Promise<Payment[]> {
    const res = await api.get<ApiResponse<Payment[]>>("/api/payments", {
      params: { subscriptionId },
    })
    return Array.isArray(res.data?.data) ? res.data.data : []
  },

  /** GET /api/payments?studentName={name} */
  async findByStudentName(studentName: string): Promise<Payment[]> {
    const res = await api.get<ApiResponse<Payment[]>>("/api/payments", {
      params: { studentName },
    })
    return Array.isArray(res.data?.data) ? res.data.data : []
  },

  /** GET /api/payments/{id} */
  async findById(id: number): Promise<Payment> {
    const res = await api.get<ApiResponse<Payment>>(`/api/payments/${id}`)
    const payment = res.data?.data
    if (!payment) throw new Error(`Payment ${id} not found`)
    return payment
  },

  /** POST /api/payments */
  async create(request: PaymentRequest): Promise<Payment> {
    const res = await api.post<ApiResponse<Payment>>("/api/payments", request)
    const payment = res.data?.data
    if (!payment) throw new Error("Failed to create payment")
    return payment
  },

  /**
   * PUT /api/payments/{id}
   * O backend só atualiza: description, value, paymentMethod,
   * dueDate, issueDate, status — o subscriptionId não muda.
   */
  async update(id: number, request: PaymentRequest): Promise<Payment> {
    const res = await api.put<ApiResponse<Payment>>(`/api/payments/${id}`, request)
    const payment = res.data?.data
    if (!payment) throw new Error("Failed to update payment")
    return payment
  },

  /** DELETE /api/payments/{id} */
  async remove(id: number): Promise<void> {
    await api.delete(`/api/payments/${id}`)
  },
}

// ─── Subscription service (auxiliar para o formulário) ────────────────────────

export const subscriptionService = {
  /**
   * GET /api/subscriptions
   * Busca todas as assinaturas para popular o select do form.
   * Retorna apenas as ATIVAS para evitar o erro de FK.
   */
  async findAllActive(): Promise<Subscription[]> {
    const res = await api.get<ApiResponse<Subscription[]>>("/api/subscriptions")
    const all = Array.isArray(res.data?.data) ? res.data.data : []
    return all.filter(s => s.status === "Ativo")
  },
}
