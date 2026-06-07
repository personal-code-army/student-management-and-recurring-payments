"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
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
      <div className="flex flex-col flex-1 min-h-screen bg-[#000000] text-[#FFFFFF]">
        <header className="flex items-center gap-3 px-6 py-4 border-b border-[#FFFFFF]/10 bg-[#020203]/90 backdrop-blur sticky top-0 z-10">
          <SidebarTrigger className="text-[#FFFFFF]/70 hover:text-[#FFFFFF]" />
          <Separator orientation="vertical" className="h-5 bg-[#FFFFFF]/20" />
          <div className="flex-1">
            <h1 className="text-sm font-semibold text-[#FFFFFF] leading-none">Planos</h1>
            <p className="text-xs text-[#FFFFFF]/60 mt-0.5">Gerencie os planos disponíveis</p>
          </div>
          <Button
            onClick={abrirCriar}
            className="h-8 gap-1.5 bg-[#DD050A] text-[#FFFFFF] hover:bg-[#DD050A]/85 text-xs"
          >
            <Plus className="h-3.5 w-3.5" />
            Novo Plano
          </Button>
        </header>

        <main className="flex-1 p-6">
          <div className="mx-auto w-full max-w-7xl">
            {loading ? (
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {[1, 2, 3].map((i) => (
                  <div key={i} className="h-64 rounded-xl border border-[#FFFFFF]/10 bg-[#020203] animate-pulse" />
                ))}
              </div>
            ) : planos.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-24 text-center">
                <CircleDollarSign className="h-12 w-12 text-[#FFFFFF]/20 mb-4" />
                <p className="text-[#FFFFFF]/50 text-sm">Nenhum plano cadastrado.</p>
                <Button onClick={abrirCriar} className="mt-4 h-8 gap-1.5 bg-[#DD050A] text-[#FFFFFF] hover:bg-[#DD050A]/85 text-xs">
                  <Plus className="h-3.5 w-3.5" /> Criar primeiro plano
                </Button>
              </div>
            ) : (
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {planos.map((plano) => (
                  <Card
                    key={plano.id}
                    className="group relative overflow-hidden border-[#FFFFFF]/12 bg-[#020203] transition-all duration-300 hover:border-[#DD050A]/50 hover:-translate-y-1 hover:shadow-lg hover:shadow-[#DD050A]/10 min-h-[280px]"
                  >
                    <div className="pointer-events-none absolute inset-0 opacity-0 transition-opacity duration-300 group-hover:opacity-100">
                      <div className="absolute -top-24 left-1/2 h-48 w-48 -translate-x-1/2 rounded-full bg-[#DD050A]/12 blur-3xl" />
                      <div className="absolute -bottom-24 right-6 h-48 w-48 rounded-full bg-[#DD050A]/8 blur-3xl" />
                    </div>

                    <CardHeader className="pb-3 pt-6 px-6">
                      <div className="flex items-start justify-between">
                        <CardTitle className="text-lg text-[#FFFFFF]">{plano.name}</CardTitle>
                        <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                          <button
                            onClick={() => abrirEditar(plano)}
                            className="p-1.5 rounded-md text-[#FFFFFF]/50 hover:text-[#FFFFFF] hover:bg-[#FFFFFF]/10 transition-colors"
                            title="Editar"
                          >
                            <Pencil className="h-3.5 w-3.5" />
                          </button>
                          <button
                            onClick={() => setConfirmDeleteId(plano.id)}
                            className="p-1.5 rounded-md text-[#FFFFFF]/50 hover:text-[#DD050A] hover:bg-[#DD050A]/10 transition-colors"
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
                          <p className="text-3xl font-bold text-[#FFFFFF] group-hover:text-[#DD050A] transition-colors">
                            {formatCurrency(plano.monthlyAmount)}
                          </p>
                          <p className="text-xs text-[#FFFFFF]/50 uppercase tracking-wider mt-0.5">por mês</p>
                        </div>
                      </div>

                      {plano.frequency > 0 && (
                        <div className="flex items-center justify-center gap-2 text-xs text-[#FFFFFF]/50">
                          <Calendar className="h-3.5 w-3.5" />
                          <span>Vencimento a cada <span className="text-[#FFFFFF]/80 font-medium">{frequencyLabel(plano.frequency)}</span></span>
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
          <SheetContent className="bg-[#020203] border-l border-[#FFFFFF]/10 text-[#FFFFFF] w-full sm:max-w-md overflow-y-auto">
            <SheetHeader className="mb-6">
              <SheetTitle className="text-[#FFFFFF]">{editando ? "Editar Plano" : "Novo Plano"}</SheetTitle>
              <SheetDescription className="text-[#FFFFFF]/50">
                {editando ? "Altere os dados do plano." : "Preencha os dados para criar um novo plano."}
              </SheetDescription>
            </SheetHeader>

            <div className="space-y-4">
              <div className="space-y-1.5">
                <Label className="text-xs text-[#FFFFFF]/70">Nome *</Label>
                <Input
                  value={form.name}
                  onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
                  placeholder="Ex: Plano Mensal"
                  className="h-8 text-xs bg-[#000000] border-[#FFFFFF]/15 text-[#FFFFFF] placeholder:text-[#FFFFFF]/30 focus-visible:border-[#DD050A]/50"
                />
              </div>

              <div className="space-y-1.5">
                <Label className="text-xs text-[#FFFFFF]/70">Valor mensal (R$) *</Label>
                <Input
                  value={form.monthlyAmount}
                  onChange={(e) => setForm((f) => ({ ...f, monthlyAmount: e.target.value }))}
                  placeholder="150,00"
                  className="h-8 text-xs bg-[#000000] border-[#FFFFFF]/15 text-[#FFFFFF] placeholder:text-[#FFFFFF]/30 focus-visible:border-[#DD050A]/50"
                />
              </div>

              <div className="space-y-1.5">
                <Label className="text-xs text-[#FFFFFF]/70">Frequência (meses)</Label>
                <Input
                  type="number"
                  min={1}
                  value={form.frequency}
                  onChange={(e) => setForm((f) => ({ ...f, frequency: e.target.value }))}
                  placeholder="1"
                  className="h-8 text-xs bg-[#000000] border-[#FFFFFF]/15 text-[#FFFFFF] placeholder:text-[#FFFFFF]/30 focus-visible:border-[#DD050A]/50"
                />
                <p className="text-[10px] text-[#FFFFFF]/40">Quantidade de meses até o vencimento do pagamento.</p>
              </div>

              {formError && (
                <p className="text-xs text-red-400">{formError}</p>
              )}
            </div>

            <SheetFooter className="mt-8 flex gap-2">
              <Button
                variant="outline"
                onClick={() => setSheetOpen(false)}
                className="flex-1 h-8 text-xs border-[#FFFFFF]/15 text-[#FFFFFF]/70 bg-transparent hover:bg-[#FFFFFF]/5"
              >
                Cancelar
              </Button>
              <Button
                onClick={salvar}
                disabled={salvando}
                className="flex-1 h-8 text-xs bg-[#DD050A] text-[#FFFFFF] hover:bg-[#DD050A]/85"
              >
                {salvando ? "Salvando..." : editando ? "Salvar alterações" : "Criar plano"}
              </Button>
            </SheetFooter>
          </SheetContent>
        </Sheet>

        {/* Modal confirmação de exclusão */}
        {confirmDeleteId !== null && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
            <div className="bg-[#020203] border border-[#FFFFFF]/15 rounded-xl p-6 w-full max-w-sm mx-4 shadow-2xl">
              <h3 className="text-sm font-semibold text-[#FFFFFF] mb-1">Excluir plano</h3>
              <p className="text-xs text-[#FFFFFF]/60 mb-6">
                Tem certeza que deseja excluir este plano? Alunos vinculados a ele podem ser afetados.
              </p>
              <div className="flex gap-2">
                <Button
                  variant="outline"
                  onClick={() => setConfirmDeleteId(null)}
                  className="flex-1 h-8 text-xs border-[#FFFFFF]/15 text-[#FFFFFF]/70 bg-transparent hover:bg-[#FFFFFF]/5"
                >
                  Cancelar
                </Button>
                <Button
                  onClick={() => excluir(confirmDeleteId)}
                  disabled={excluindo}
                  className="flex-1 h-8 text-xs bg-[#DD050A] text-[#FFFFFF] hover:bg-[#DD050A]/85"
                >
                  {excluindo ? "Excluindo..." : "Excluir"}
                </Button>
              </div>
            </div>
          </div>
        )}
      </div>
    </SidebarProvider>
  )
}
