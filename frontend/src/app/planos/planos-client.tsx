"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Sheet, SheetContent, SheetDescription, SheetFooter,
  SheetHeader, SheetTitle,
} from "@/components/ui/sheet"
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { AppSidebar } from "@/components/app-sidebar"
import { Separator } from "@/components/ui/separator"
import { CircleDollarSign, Pencil, Plus, Trash2, Calendar } from "lucide-react"
import { api } from "@/lib/api"
import { ApiErrorScreen } from "@/components/api-error-screen"
import { type ApiErrorInfo, getApiErrorInfo, resolveApiErrorContent } from "@/lib/api-errors"

interface Plano {
  id: number
  name: string
  monthlyAmount: number
  frequency: number
}

type ApiResponse<T> = { data: T }

interface FormState {
  name: string
  monthlyAmount: string
  frequency: string
}

const FORM_VAZIO: FormState = { name: "", monthlyAmount: "", frequency: "" }

function formatCurrency(value: number) {
  return value.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })
}

function parseCurrency(value: string): number {
  return parseFloat(value.replace(/\./g, "").replace(",", ".")) || 0
}

function frequencyLabel(frequency: number) {
  if (frequency === 1) return "1 mês"
  if (frequency === 12) return "anual"
  return `${frequency} meses`
}

export function PlanosClient() {
  const [planos, setPlanos] = useState<Plano[]>([])
  const [loading, setLoading] = useState(true)
  const [apiError, setApiError] = useState<ApiErrorInfo | null>(null)

  const [sheetOpen, setSheetOpen] = useState(false)
  const [editando, setEditando] = useState<Plano | null>(null)
  const [form, setForm] = useState<FormState>(FORM_VAZIO)
  const [salvando, setSalvando] = useState(false)
  const [formError, setFormError] = useState("")

  const [confirmDeleteId, setConfirmDeleteId] = useState<number | null>(null)
  const [excluindo, setExcluindo] = useState(false)

  async function carregarPlanos() {
    try {
      setLoading(true)
      const res = await api.get<ApiResponse<Plano[]>>("/api/plans")
      setPlanos(res.data.data)
      setApiError(null)
    } catch (err) {
      setApiError(getApiErrorInfo(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { carregarPlanos() }, [])

  function abrirCriar() {
    setEditando(null)
    setForm(FORM_VAZIO)
    setFormError("")
    setSheetOpen(true)
  }

  function abrirEditar(plano: Plano) {
    setEditando(plano)
    setForm({
      name: plano.name,
      monthlyAmount: plano.monthlyAmount.toLocaleString("pt-BR", { minimumFractionDigits: 2 }),
      frequency: String(plano.frequency),
    })
    setFormError("")
    setSheetOpen(true)
  }

  async function salvar() {
    if (!form.name.trim()) { setFormError("Nome é obrigatório."); return }
    const valor = parseCurrency(form.monthlyAmount)
    if (!valor || valor <= 0) { setFormError("Informe um valor válido."); return }
    const freq = parseInt(form.frequency) || 1
    if (freq < 1) { setFormError("Frequência deve ser pelo menos 1."); return }

    setSalvando(true)
    setFormError("")
    try {
      const payload = { name: form.name.trim(), monthlyAmount: valor, frequency: freq }
      if (editando) {
        await api.put(`/api/plans/${editando.id}`, payload)
      } else {
        await api.post("/api/plans", payload)
      }
      setSheetOpen(false)
      await carregarPlanos()
    } catch (err) {
      const info = getApiErrorInfo(err)
      const { description } = resolveApiErrorContent(info)
      setFormError(description)
    } finally {
      setSalvando(false)
    }
  }

  async function excluir(id: number) {
    setExcluindo(true)
    try {
      await api.delete(`/api/plans/${id}`)
      setConfirmDeleteId(null)
      await carregarPlanos()
    } catch (err) {
      const info = getApiErrorInfo(err)
      const { description } = resolveApiErrorContent(info)
      setFormError(description)
    } finally {
      setExcluindo(false)
    }
  }

  if (apiError) {
    return <ApiErrorScreen error={apiError} onRetry={carregarPlanos} />
  }

  return (
    <SidebarProvider>
      <AppSidebar />
      <div className="flex flex-col flex-1 min-h-screen bg-zinc-50 text-zinc-900 dark:bg-[#000000] dark:text-[#FFFFFF]">
        <header className="flex items-center gap-3 px-6 py-4 border-b border-zinc-200 bg-white/90 backdrop-blur sticky top-0 z-10 dark:border-[#FFFFFF]/10 dark:bg-[#020203]/90">
          <SidebarTrigger className="text-zinc-500 hover:text-zinc-900 dark:text-[#FFFFFF]/70 dark:hover:text-[#FFFFFF]" />
          <Separator orientation="vertical" className="h-5 bg-zinc-200 dark:bg-[#FFFFFF]/20" />
          <div className="flex-1">
            <h1 className="text-sm font-semibold leading-none text-zinc-900 dark:text-[#FFFFFF]">Planos</h1>
            <p className="text-xs text-zinc-500 mt-0.5 dark:text-[#FFFFFF]/60">Gerencie os planos disponíveis</p>
          </div>
          <button
            onClick={abrirCriar}
            className="flex items-center gap-2 rounded-lg border border-[#DD050A]/50 bg-[#DD050A]/15 px-3 py-2 text-xs font-medium text-[#DD050A] transition-colors hover:border-[#DD050A]/70 hover:bg-[#DD050A]/25 dark:text-[#FFFFFF]"
          >
            <Plus className="h-3.5 w-3.5" />
            Novo Plano
          </button>
        </header>

        <main className="flex-1 p-6">
          <div className="mx-auto w-full max-w-7xl">
            {loading ? (
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {[1, 2, 3].map((i) => (
                  <div key={i} className="h-64 rounded-xl border border-zinc-200 bg-white animate-pulse dark:border-[#FFFFFF]/10 dark:bg-[#020203]" />
                ))}
              </div>
            ) : planos.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-24 text-center">
                <CircleDollarSign className="h-12 w-12 text-zinc-300 mb-4 dark:text-[#FFFFFF]/20" />
                <p className="text-zinc-500 text-sm dark:text-[#FFFFFF]/50">Nenhum plano cadastrado.</p>
                <button
                  onClick={abrirCriar}
                  className="mt-4 flex items-center gap-2 rounded-lg border border-[#DD050A]/50 bg-[#DD050A]/15 px-3 py-2 text-xs font-medium text-[#DD050A] transition-colors hover:border-[#DD050A]/70 hover:bg-[#DD050A]/25 dark:text-[#FFFFFF]"
                >
                  <Plus className="h-3.5 w-3.5" /> Criar primeiro plano
                </button>
              </div>
            ) : (
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {planos.map((plano) => (
                  <Card
                    key={plano.id}
                    className="group relative overflow-hidden border-zinc-200 bg-white transition-all duration-300 hover:border-[#DD050A]/50 hover:-translate-y-1 hover:shadow-lg hover:shadow-[#DD050A]/10 min-h-[280px] dark:border-[#FFFFFF]/12 dark:bg-[#020203]"
                  >
                    <div className="pointer-events-none absolute inset-0 opacity-0 transition-opacity duration-300 group-hover:opacity-100">
                      <div className="absolute -top-24 left-1/2 h-48 w-48 -translate-x-1/2 rounded-full bg-[#DD050A]/12 blur-3xl" />
                      <div className="absolute -bottom-24 right-6 h-48 w-48 rounded-full bg-[#DD050A]/8 blur-3xl" />
                    </div>

                    <CardHeader className="pb-3 pt-6 px-6">
                      <div className="flex items-start justify-between">
                        <CardTitle className="text-lg text-zinc-900 dark:text-[#FFFFFF]">{plano.name}</CardTitle>
                        <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                          <button
                            onClick={() => abrirEditar(plano)}
                            className="p-1.5 rounded-md text-zinc-400 hover:text-zinc-700 hover:bg-zinc-100 transition-colors dark:text-[#FFFFFF]/50 dark:hover:text-[#FFFFFF] dark:hover:bg-[#FFFFFF]/10"
                            title="Editar"
                          >
                            <Pencil className="h-3.5 w-3.5" />
                          </button>
                          <button
                            onClick={() => setConfirmDeleteId(plano.id)}
                            className="p-1.5 rounded-md text-zinc-400 hover:text-[#DD050A] hover:bg-[#DD050A]/10 transition-colors dark:text-[#FFFFFF]/50"
                            title="Excluir"
                          >
                            <Trash2 className="h-3.5 w-3.5" />
                          </button>
                        </div>
                      </div>
                    </CardHeader>

                    <CardContent className="px-6 pb-8 space-y-6">
                      <div className="flex flex-col items-center gap-3 text-center">
                        <span className="flex items-center justify-center w-10 h-10 rounded-md bg-[#DD050A]/12">
                          <CircleDollarSign className="h-5 w-5 text-[#DD050A]" />
                        </span>
                        <div>
                          <p className="text-3xl font-bold text-zinc-900 group-hover:text-[#DD050A] transition-colors dark:text-[#FFFFFF]">
                            {formatCurrency(plano.monthlyAmount)}
                          </p>
                        </div>
                      </div>

                      {plano.frequency > 0 && (
                        <div className="flex items-center justify-center gap-2 text-xs text-zinc-500 dark:text-[#FFFFFF]/50">
                          <Calendar className="h-3.5 w-3.5" />
                          <span>Vencimento a cada <span className="text-zinc-700 font-medium dark:text-[#FFFFFF]/80">{frequencyLabel(plano.frequency)}</span></span>
                        </div>
                      )}
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </div>
        </main>

        {/* Sheet criar / editar */}
        <Sheet open={sheetOpen} onOpenChange={setSheetOpen}>
          <SheetContent
            side="right"
            className="flex w-full flex-col gap-0 border-l border-zinc-200 bg-white p-0 text-zinc-900 sm:max-w-md dark:border-[#FFFFFF]/10 dark:bg-[#020203] dark:text-[#FFFFFF]"
          >
            <SheetHeader className="border-b border-zinc-200 px-6 py-5 dark:border-[#FFFFFF]/10">
              <SheetTitle className="text-base text-zinc-900 dark:text-[#FFFFFF]">
                {editando ? "Editar Plano" : "Novo Plano"}
              </SheetTitle>
              <SheetDescription className="text-xs text-zinc-500 dark:text-[#FFFFFF]/55">
                {editando ? "Altere os dados do plano." : "Preencha os dados para criar um novo plano."}
              </SheetDescription>
            </SheetHeader>

            <div className="flex-1 overflow-y-auto px-6 py-5">
              {formError && (
                <div className="mb-4 rounded-md border border-[#DD050A]/40 bg-[#DD050A]/10 px-3 py-2 text-xs text-[#DD050A]">
                  {formError}
                </div>
              )}

              <div className="space-y-5">
                <div className="flex flex-col gap-1.5">
                  <Label className="text-xs font-medium text-zinc-600 dark:text-[#FFFFFF]/80">Nome *</Label>
                  <Input
                    value={form.name}
                    onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
                    placeholder="Ex: Plano Mensal"
                    className="border-zinc-300 bg-white text-zinc-900 placeholder:text-zinc-400 focus-visible:border-[#DD050A]/50 dark:border-[#FFFFFF]/15 dark:bg-[#000000] dark:text-[#FFFFFF] dark:placeholder:text-[#FFFFFF]/30"
                  />
                </div>

                <div className="flex flex-col gap-1.5">
                  <Label className="text-xs font-medium text-zinc-600 dark:text-[#FFFFFF]/80">Valor mensal (R$) *</Label>
                  <Input
                    value={form.monthlyAmount}
                    onChange={(e) => setForm((f) => ({ ...f, monthlyAmount: e.target.value }))}
                    placeholder="150,00"
                    className="border-zinc-300 bg-white text-zinc-900 placeholder:text-zinc-400 focus-visible:border-[#DD050A]/50 dark:border-[#FFFFFF]/15 dark:bg-[#000000] dark:text-[#FFFFFF] dark:placeholder:text-[#FFFFFF]/30"
                  />
                </div>

                <div className="flex flex-col gap-1.5">
                  <Label className="text-xs font-medium text-zinc-600 dark:text-[#FFFFFF]/80">Frequência (meses)</Label>
                  <Input
                    type="number"
                    min={1}
                    value={form.frequency}
                    onChange={(e) => setForm((f) => ({ ...f, frequency: e.target.value }))}
                    placeholder="1"
                    className="border-zinc-300 bg-white text-zinc-900 placeholder:text-zinc-400 focus-visible:border-[#DD050A]/50 dark:border-[#FFFFFF]/15 dark:bg-[#000000] dark:text-[#FFFFFF] dark:placeholder:text-[#FFFFFF]/30"
                  />
                  <p className="text-[10px] text-zinc-400 dark:text-[#FFFFFF]/40">
                    Quantidade de meses até o vencimento do pagamento.
                  </p>
                </div>
              </div>
            </div>

            <SheetFooter className="border-t border-zinc-200 px-6 py-4 dark:border-[#FFFFFF]/10">
              <div className="flex w-full gap-3">
                <button
                  onClick={() => setSheetOpen(false)}
                  className="flex-1 h-9 rounded-lg border border-zinc-300 text-xs text-zinc-600 hover:bg-zinc-100 transition-colors dark:border-[#FFFFFF]/15 dark:text-[#FFFFFF]/70 dark:hover:bg-[#FFFFFF]/5"
                >
                  Cancelar
                </button>
                <button
                  onClick={salvar}
                  disabled={salvando}
                  className="flex-1 h-9 rounded-lg text-xs bg-[#DD050A] text-[#FFFFFF] hover:bg-[#DD050A]/85 transition-colors disabled:opacity-50"
                >
                  {salvando ? "Salvando..." : editando ? "Salvar alterações" : "Criar plano"}
                </button>
              </div>
            </SheetFooter>
          </SheetContent>
        </Sheet>

        {/* Modal confirmação de exclusão */}
        {confirmDeleteId !== null && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
            <div className="bg-white border border-zinc-200 rounded-xl p-6 w-full max-w-sm mx-4 shadow-2xl dark:bg-[#020203] dark:border-[#FFFFFF]/15">
              <h3 className="text-sm font-semibold text-zinc-900 mb-1 dark:text-[#FFFFFF]">Excluir plano</h3>
              <p className="text-xs text-zinc-500 mb-6 dark:text-[#FFFFFF]/60">
                Tem certeza que deseja excluir este plano? Alunos vinculados a ele podem ser afetados.
              </p>
              <div className="flex gap-2">
                <button
                  onClick={() => setConfirmDeleteId(null)}
                  className="flex-1 h-9 rounded-lg border border-zinc-300 text-xs text-zinc-600 hover:bg-zinc-100 transition-colors dark:border-[#FFFFFF]/15 dark:text-[#FFFFFF]/70 dark:hover:bg-[#FFFFFF]/5"
                >
                  Cancelar
                </button>
                <button
                  onClick={() => excluir(confirmDeleteId)}
                  disabled={excluindo}
                  className="flex-1 h-9 rounded-lg text-xs bg-[#DD050A] text-[#FFFFFF] hover:bg-[#DD050A]/85 transition-colors disabled:opacity-50"
                >
                  {excluindo ? "Excluindo..." : "Excluir"}
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </SidebarProvider>
  )
}
