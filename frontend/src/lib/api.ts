import axios from "axios"

export const BASE_URL =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080"

export const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
})

export type GeneratePaymentLinkResponse = {
  checkoutUrl: string
  expirationDate: string
  externalReference: string
}

export async function generateMercadoPagoLink(
  subscriptionId: number,
): Promise<GeneratePaymentLinkResponse> {
  const { data } = await api.post<{ data: GeneratePaymentLinkResponse }>(
    "/api/payments/mercadopago-link",
    { subscriptionId },
  )
  return data.data
}
