"use client"

import { useEffect, useRef, useState } from "react"
import axios from "axios"
import { Check, Copy, ExternalLink, Loader2 } from "lucide-react"

import {
  Sheet,
  SheetClose,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  generateMercadoPagoLink,
  type GeneratePaymentLinkResponse,
} from "@/lib/api"

interface PaymentLinkSheetProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  studentName: string
  defaultSubscriptionId?: string
}

const labelClass = "text-xs font-medium text-[#FFFFFF]/80"
const inputClass =
  "border-[#FFFFFF]/15 bg-[#000000] text-[#FFFFFF] placeholder:text-[#FFFFFF]/30 focus-visible:border-[#DD050A]/50"

function formatExpiration(iso: string | undefined): string {
  if (!iso) return ""
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return iso
  return d.toLocaleString("pt-BR", { dateStyle: "short", timeStyle: "short" })
}

export function PaymentLinkSheet({
  open,
  onOpenChange,
  studentName,
  defaultSubscriptionId = "",
}: PaymentLinkSheetProps) {
  const [subscriptionId, setSubscriptionId] = useState(defaultSubscriptionId)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [result, setResult] = useState<GeneratePaymentLinkResponse | null>(null)
  const [copied, setCopied] = useState(false)
  const requestIdRef = useRef(0)

  useEffect(() => {
    if (open) {
      requestIdRef.current += 1
      setSubscriptionId(defaultSubscriptionId)
      setResult(null)
      setError(null)
      setCopied(false)
      setLoading(false)
    }
  }, [open, defaultSubscriptionId])

  async function handleGenerate() {
    const id = Number(subscriptionId)
    if (!Number.isInteger(id) || id <= 0) {
      setError("Informe um ID de assinatura válido")
      return
    }
    const myRequestId = ++requestIdRef.current
    setLoading(true)
    setError(null)
    try {
      const data = await generateMercadoPagoLink(id)
      if (myRequestId !== requestIdRef.current) return
      setResult(data)
    } catch (err) {
      if (myRequestId !== requestIdRef.current) return
      if (axios.isAxiosError(err)) {
        const status = err.response?.status
        if (status === 404) {
          setError("Assinatura não encontrada")
        } else if (status === 400) {
          setError(err.response?.data?.error ?? "Dados inválidos")
        } else if (status === 401) {
          setError("Sessão expirada — faça login novamente")
        } else {
          setError("Erro ao gerar link. Tente novamente.")
        }
      } else {
        setError("Erro ao gerar link. Tente novamente.")
      }
    } finally {
      if (myRequestId === requestIdRef.current) setLoading(false)
    }
  }

  async function handleCopy() {
    if (!result?.checkoutUrl) return
    try {
      await navigator.clipboard.writeText(result.checkoutUrl)
      setCopied(true)
      window.setTimeout(() => setCopied(false), 2000)
    } catch {
      setError("Não foi possível copiar para a área de transferência")
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent
        side="right"
        className="flex w-full flex-col border-l border-[#FFFFFF]/10 bg-[#020203] text-[#FFFFFF] sm:max-w-md"
      >
        <SheetHeader className="border-b border-[#FFFFFF]/10 px-6 pb-4 pt-6">
          <SheetTitle className="text-base text-[#FFFFFF]">
            Gerar link de pagamento
          </SheetTitle>
          <SheetDescription className="text-xs text-[#FFFFFF]/55">
            Mercado Pago Checkout Pro · {studentName}
          </SheetDescription>
        </SheetHeader>

        <div className="flex-1 overflow-y-auto px-6 py-5">
          <div className="flex flex-col gap-5">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="subscriptionId" className={labelClass}>
                ID da assinatura
              </Label>
              <Input
                id="subscriptionId"
                type="number"
                inputMode="numeric"
                min={1}
                value={subscriptionId}
                onChange={(e) => setSubscriptionId(e.target.value)}
                placeholder="Ex: 1"
                disabled={loading}
                className={inputClass}
              />
              <p className="text-[11px] text-[#FFFFFF]/45">
                Use o ID da assinatura cadastrada deste aluno no backend.
              </p>
            </div>

            {error && (
              <div className="rounded-md border border-[#DD050A]/40 bg-[#DD050A]/10 px-3 py-2 text-xs text-[#DD050A]">
                {error}
              </div>
            )}

            {result && (
              <div className="flex flex-col gap-3 rounded-md border border-[#00FF00]/20 bg-[#00FF00]/5 p-3">
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="checkoutUrl" className={labelClass}>
                    Link de pagamento
                  </Label>
                  <Input
                    id="checkoutUrl"
                    readOnly
                    value={result.checkoutUrl}
                    className={`${inputClass} font-mono text-[11px]`}
                    onFocus={(e) => e.currentTarget.select()}
                  />
                </div>
                <p className="text-[11px] text-[#FFFFFF]/60">
                  Validade: {formatExpiration(result.expirationDate)}
                </p>
                <div className="flex gap-2">
                  <button
                    type="button"
                    onClick={handleCopy}
                    className="inline-flex flex-1 items-center justify-center gap-2 rounded-md border border-[#FFFFFF]/20 bg-[#FFFFFF]/5 px-3 py-2 text-xs font-medium text-[#FFFFFF] transition-colors hover:border-[#DD050A]/40 hover:bg-[#DD050A]/10"
                  >
                    {copied ? (
                      <>
                        <Check className="h-3.5 w-3.5" />
                        Copiado
                      </>
                    ) : (
                      <>
                        <Copy className="h-3.5 w-3.5" />
                        Copiar
                      </>
                    )}
                  </button>
                  <a
                    href={result.checkoutUrl}
                    target="_blank"
                    rel="noreferrer noopener"
                    className="inline-flex items-center justify-center gap-2 rounded-md border border-[#FFFFFF]/20 bg-[#FFFFFF]/5 px-3 py-2 text-xs font-medium text-[#FFFFFF] transition-colors hover:border-[#DD050A]/40 hover:bg-[#DD050A]/10"
                  >
                    <ExternalLink className="h-3.5 w-3.5" />
                    Abrir
                  </a>
                </div>
              </div>
            )}
          </div>
        </div>

        <SheetFooter className="flex-row gap-2 border-t border-[#FFFFFF]/10 px-6 py-4">
          <SheetClose asChild>
            <button
              type="button"
              className="flex-1 rounded-lg border border-[#FFFFFF]/20 py-2 text-sm text-[#FFFFFF]/70 transition-colors hover:bg-[#FFFFFF]/5 hover:text-[#FFFFFF]"
            >
              Fechar
            </button>
          </SheetClose>
          <button
            type="button"
            onClick={handleGenerate}
            disabled={loading || !subscriptionId}
            className="flex-1 inline-flex items-center justify-center gap-2 rounded-lg bg-[#DD050A] py-2 text-sm font-medium text-[#FFFFFF] transition-colors hover:bg-[#DD050A]/85 disabled:opacity-50"
          >
            {loading && <Loader2 className="h-3.5 w-3.5 animate-spin" />}
            {result ? "Gerar outro" : "Gerar link"}
          </button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  )
}
