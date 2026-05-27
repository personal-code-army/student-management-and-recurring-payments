"use client"

import { useCallback, useEffect, useMemo, useState } from "react"
import {
  Card, CardContent, CardDescription, CardHeader, CardTitle,
} from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Table, TableBody, TableCaption, TableCell,
  TableHead, TableHeader, TableRow,
} from "@/components/ui/table"
import {
  Sheet, SheetClose, SheetContent, SheetDescription,
  SheetFooter, SheetHeader, SheetTitle,
} from "@/components/ui/sheet"
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { AppSidebar } from "@/components/app-sidebar"
import { Separator } from "@/components/ui/separator"
import {
  DollarSign, TrendingDown, CheckCircle, Plus, Pencil,
  Trash2, ChevronLeft, ChevronRight, Search, Filter,
  AlertCircle, Clock, QrCode, FileText, CreditCard,
} from "lucide-react"
import { api } from "@/lib/api"

// ─── Types ───────────────────────────────────────────────────────────────────

interface Payment {
  id: number
  description: string
  value: number
  paymentMethod: string
  dueDate: string
  issueDate: string | null
  status: string
  subscriptionId: number
}

interface FormState {
  description: string
  value: string
  paymentMethod: string
  dueDate: string
  issueDate: string
  status: string
  subscriptionId: string
}

type ApiResponse<T> = { data: T }

// ─── Constants ───────────────────────────────────────────────────────────────

const STATUS_OPTIONS = ["Todos", "A receber", "Pago", "Vencido"] as const
const METHOD_OPTIONS = ["PIX", "Cartão de Crédito", "Cartão de Débito", "Dinheiro", "Boleto"]
const POR_PAGINA = 10

const FORM_VAZIO: FormState = {
  description: "",
  value: "",
  paymentMethod: "",
  dueDate: "",
  issueDate: "",
  status: "A receber",
  subscriptionId: "",
}

const STATUS_STYLE: Record<string, string> = {
  "Pago":      "border-[#00FF00]/30 bg-[#00FF00]/10 text-[#00FF00]",
  "A receber": "border-[#F59E0B]/30 bg-[#F59E0B]/10 text-[#F59E0B]",
  "Vencido":   "border-[#DD050A]/30 bg-[#DD050A]/10 text-[#DD050A]",
}

const STATUS_ICON: Record<string, React.ReactNode> = {
  "Pago":      <CheckCircle className="h-3 w-3" />,
  "A receber": <Clock className="h-3 w-3" />,
  "Vencido":   <AlertCircle className="h-3 w-3" />,
}

const SELECT_CLASS =
  "h-8 rounded-md border border-[#FFFFFF]/15 bg-[#000000] px-2.5 text-xs text-[#FFFFFF] focus:border-[#DD050A]/50 focus:outline-none"

const FORM_SELECT_CLASS =
  "h-9 w-full rounded-md border border-[#FFFFFF]/15 bg-[#000000] px-3 text-sm text-[#FFFFFF] focus:border-[#DD050A]/50 focus:outline-none"

// ─── Helpers ─────────────────────────────────────────────────────────────────

function formatarData(iso: string | null): string {
  if (!iso) return "—"
  const normalized = iso.slice(0, 10)
  const [ano, mes, dia] = normalized.split("-")
  return `${dia}/${mes}/${ano}`
}

function formatarMoeda(value: number): string {
  return value.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })
}

function numerasDePagina(atual: number, total: number): (number | "…")[] {
  if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1)
  const pages: (number | "…")[] = [1]
  if (atual > 3) pages.push("…")
  for (let i = Math.max(2, atual - 1); i <= Math.min(total - 1, atual + 1); i++) pages.push(i)
  if (atual < total - 2) pages.push("…")
  pages.push(total)
  return pages
}

// ─── Component ───────────────────────────────────────────────────────────────

export function RecebimentosClient() {
  const [payments, setPayments] = useState<Payment[]>([])
  const [busca, setBusca] = useState("")
  const [filtroStatus, setFiltroStatus] = useState<(typeof STATUS_OPTIONS)[number]>("Todos")
  const [pagina, setPagina] = useState(1)
  const [sheetAberto, setSheetAberto] = useState(false)
  const [modo, setModo] = useState<"criar" | "editar">("criar")
  const [editando, setEditando] = useState<Payment | null>(null)
  const [deletandoId, setDeletandoId] = useState<number | null>(null)
  const [form, setForm] = useState<FormState>(FORM_VAZIO)
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState<string | null>(null)

  // ── Fetch ──────────────────────────────────────────────────────────────────

  const carregarPayments = useCallback(async () => {
    try {
      setErro(null)
      setCarregando(true)
      const response = await api.get<ApiResponse<Payment[]>>("/api/payments")
      setPayments(Array.isArray(response.data?.data) ? response.data.data : [])
    } catch (err) {
      console.error("Erro ao carregar recebimentos", err)
      setErro("Não foi possível carregar os recebimentos.")
    } finally {
      setCarregando(false)
    }
  }, [])

  useEffect(() => {
    void carregarPayments()
  }, [carregarPayments])

  // ── Derived ────────────────────────────────────────────────────────────────

  const filtrados = useMemo(
    () =>
      payments
        .filter(p => {
          if (busca === "") return true
          const termo = busca.toLowerCase()
          return (
            p.description.toLowerCase().includes(termo) ||
            String(p.id).includes(termo) ||
            p.paymentMethod?.toLowerCase().includes(termo)
          )
        })
        .filter(p => (filtroStatus === "Todos" ? true : p.status === filtroStatus)),
    [payments, busca, filtroStatus]
  )

  const totalPaginas = Math.max(1, Math.ceil(filtrados.length / POR_PAGINA))
  const paginaAtual = Math.min(pagina, totalPaginas)
  const visiveis = filtrados.slice((paginaAtual - 1) * POR_PAGINA, paginaAtual * POR_PAGINA)

  const totalAReceber = payments
    .filter(p => p.status === "A receber")
    .reduce((acc, p) => acc + p.value, 0)

  const totalAtrasados = payments
    .filter(p => p.status === "Vencido")
    .reduce((acc, p) => acc + p.value, 0)

  const totalRecebidoMes = useMemo(() => {
    const now = new Date()
    return payments
      .filter(p => {
        if (p.status !== "Pago" || !p.issueDate) return false
        const d = new Date(p.issueDate)
        return d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear()
      })
      .reduce((acc, p) => acc + p.value, 0)
  }, [payments])

  function mudarFiltro(fn: () => void) {
    fn()
    setPagina(1)
  }

  // ── Sheet actions ──────────────────────────────────────────────────────────

  function abrirCriar() {
    setModo("criar")
    setForm(FORM_VAZIO)
    setEditando(null)
    setSheetAberto(true)
  }

  function abrirEditar(p: Payment) {
    setModo("editar")
    setForm({
      description: p.description,
      value: String(p.value),
      paymentMethod: p.paymentMethod ?? "",
      dueDate: p.dueDate?.slice(0, 10) ?? "",
      issueDate: p.issueDate?.slice(0, 10) ?? "",
      status: p.status,
      subscriptionId: String(p.subscriptionId),
    })
    setEditando(p)
    setSheetAberto(true)
  }

  async function salvar() {
    if (!form.description.trim() || !form.value || !form.dueDate) return
    try {
      const payload = {
        description: form.description.trim(),
        value: parseFloat(form.value.replace(",", ".")),
        paymentMethod: form.paymentMethod || null,
        dueDate: form.dueDate,
        issueDate: form.issueDate || null,
        status: form.status,
        subscriptionId: form.subscriptionId ? Number(form.subscriptionId) : null,
      }

      if (modo === "criar") {
        const response = await api.post<ApiResponse<Payment>>("/api/payments", payload)
        if (response.data?.data) {
          setPayments(prev => [...prev, response.data.data])
          setErro(null)
        }
      } else if (editando) {
        const response = await api.put<ApiResponse<Payment>>(`/api/payments/${editando.id}`, payload)
        if (response.data?.data) {
          setPayments(prev => prev.map(p => (p.id === editando.id ? response.data.data : p)))
          setErro(null)
        }
      }
      setSheetAberto(false)
    } catch (err) {
      console.error("Erro ao salvar recebimento", err)
      setErro("Não foi possível salvar o recebimento.")
    }
  }

  async function excluir(id: number) {
    try {
      await api.delete(`/api/payments/${id}`)
      setPayments(prev => prev.filter(p => p.id !== id))
      setErro(null)
    } catch (err) {
      console.error("Erro ao excluir recebimento", err)
      setErro("Não foi possível excluir o recebimento.")
    } finally {
      setDeletandoId(null)
    }
  }

  // ── Styles ─────────────────────────────────────────────────────────────────

  const labelClass = "text-xs font-medium text-[#FFFFFF]/80"
  const inputClass =
    "border-[#FFFFFF]/15 bg-[#000000] text-[#FFFFFF] placeholder:text-[#FFFFFF]/30 focus-visible:border-[#DD050A]/50"

  // ── Render ─────────────────────────────────────────────────────────────────

  return (
    <SidebarProvider>
      <AppSidebar />
      <div className="flex min-h-screen flex-1 flex-col bg-[#000000] text-[#FFFFFF]">

        {/* ── Header ── */}
        <header className="sticky top-0 z-10 flex items-center justify-between gap-3 border-b border-[#FFFFFF]/10 bg-[#020203]/90 px-4 py-3 backdrop-blur sm:px-6 sm:py-4">
          <div className="flex items-center gap-3">
            <SidebarTrigger className="text-[#FFFFFF]/70 hover:text-[#FFFFFF] md:hidden" />
            <Separator orientation="vertical" className="h-5 bg-[#FFFFFF]/20 md:hidden" />
            <div>
              <h1 className="text-sm font-semibold leading-none text-[#FFFFFF]">Recebimentos</h1>
              <p className="mt-0.5 text-xs text-[#FFFFFF]/60">Gerencie os pagamentos e cobranças</p>
            </div>
          </div>
          <button
            onClick={abrirCriar}
            className="flex items-center gap-2 rounded-lg border border-[#DD050A]/50 bg-[#DD050A]/15 px-3 py-2 text-xs font-medium text-[#FFFFFF] transition-colors hover:border-[#DD050A]/70 hover:bg-[#DD050A]/25"
          >
            <Plus className="h-3.5 w-3.5" />
            <span className="hidden sm:inline">Novo Recebimento</span>
          </button>
        </header>

        <main className="flex-1 space-y-5 p-4 sm:space-y-6 sm:p-6">

          {/* ── KPI Cards ── */}
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
            {[
              {
                label: "Total a Receber",
                valor: formatarMoeda(totalAReceber),
                sub: `${payments.filter(p => p.status === "A receber").length} cobranças pendentes`,
                subClass: "text-[#FFFFFF]/60",
                icon: DollarSign,
                accentColor: "group-hover:text-[#DD050A]",
                accentBg: "group-hover:bg-[#DD050A]/12",
              },
              {
                label: "Atrasados",
                valor: formatarMoeda(totalAtrasados),
                sub: `${payments.filter(p => p.status === "Vencido").length} cobranças vencidas`,
                subClass: "text-[#DD050A]",
                icon: TrendingDown,
                accentColor: "group-hover:text-[#DD050A] text-[#DD050A]",
                accentBg: "bg-[#DD050A]/12",
                valorClass: "text-[#DD050A]",
              },
              {
                label: "Recebido (Mês)",
                valor: formatarMoeda(totalRecebidoMes),
                sub: `${payments.filter(p => p.status === "Pago").length} pagamentos confirmados`,
                subClass: "text-[#00FF00]/80",
                icon: CheckCircle,
                accentColor: "group-hover:text-[#00AA00] text-[#00AA00]",
                accentBg: "group-hover:bg-[#00FF00]/12",
                valorClass: "text-[#00CC44]",
              },
            ].map(({ label, valor, sub, subClass, icon: Icon, accentColor, accentBg, valorClass }) => (
              <Card
                key={label}
                className="group border-[#FFFFFF]/12 bg-[#020203] transition-colors hover:border-[#DD050A]/50"
              >
                <CardHeader className="flex flex-row items-center justify-between px-4 pb-2 pt-4 sm:px-5">
                  <CardTitle className="text-xs font-medium uppercase tracking-wider text-[#FFFFFF]/65">
                    {label}
                  </CardTitle>
                  <span className={`flex h-7 w-7 items-center justify-center rounded-md bg-[#FFFFFF]/10 transition-colors ${accentBg}`}>
                    <Icon className={`h-4 w-4 text-[#FFFFFF] transition-colors ${accentColor}`} />
                  </span>
                </CardHeader>
                <CardContent className="px-4 pb-4 sm:px-5">
                  <p className={`text-xl font-bold sm:text-2xl ${valorClass ?? "text-[#FFFFFF]"}`}>
                    {valor}
                  </p>
                  <p className={`mt-1 text-xs ${subClass}`}>{sub}</p>
                </CardContent>
              </Card>
            ))}
          </div>

          {/* ── Table Card ── */}
          <Card className="border-[#FFFFFF]/12 bg-[#020203]">
            <CardHeader className="px-4 pb-3 pt-5 sm:px-5">
              <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                <div>
                  <CardTitle className="text-base text-[#FFFFFF]">Lista de Recebimentos</CardTitle>
                  <CardDescription className="text-xs text-[#FFFFFF]/60">
                    {filtrados.length} recebimento{filtrados.length !== 1 ? "s" : ""} encontrado{filtrados.length !== 1 ? "s" : ""}
                  </CardDescription>
                </div>

                {/* Filtros */}
                <div className="flex flex-wrap items-center gap-2">
                  <div className="relative">
                    <Search className="absolute left-2.5 top-1/2 h-3.5 w-3.5 -translate-y-1/2 text-[#FFFFFF]/40" />
                    <Input
                      type="search"
                      placeholder="Buscar por cliente ou ID..."
                      value={busca}
                      onChange={e => mudarFiltro(() => setBusca(e.target.value))}
                      className="h-8 w-full border-[#FFFFFF]/15 bg-[#000000] pl-8 text-xs text-[#FFFFFF] placeholder:text-[#FFFFFF]/35 focus-visible:border-[#DD050A]/50 sm:w-52"
                    />
                  </div>
                  <div className="flex items-center gap-1.5">
                    <Filter className="h-3.5 w-3.5 text-[#FFFFFF]/40" />
                    <select
                      value={filtroStatus}
                      onChange={e => mudarFiltro(() => setFiltroStatus(e.target.value as (typeof STATUS_OPTIONS)[number]))}
                      aria-label="Filtrar por status"
                      className={SELECT_CLASS}
                    >
                      {STATUS_OPTIONS.map(s => (
                        <option key={s} value={s}>{s}</option>
                      ))}
                    </select>
                  </div>
                </div>
              </div>
            </CardHeader>

            <CardContent className="px-4 pb-5 sm:px-5">
              {erro && (
                <div className="mb-4 rounded-md border border-[#DD050A]/40 bg-[#DD050A]/10 px-3 py-2 text-xs text-[#DD050A]">
                  {erro}
                </div>
              )}

              <Table className="min-w-[640px] table-fixed sm:min-w-0">
                <TableCaption className="sr-only">
                  Lista de recebimentos com informações de valor, cliente, datas e status
                </TableCaption>
                <TableHeader>
                  <TableRow className="border-[#FFFFFF]/10 hover:bg-transparent">
                    <TableHead scope="col" className="w-16 text-xs uppercase text-[#FFFFFF]/60">ID</TableHead>
                    <TableHead scope="col" className="text-xs uppercase text-[#FFFFFF]/60">Valor</TableHead>
                    <TableHead scope="col" className="text-xs uppercase text-[#FFFFFF]/60">Descrição</TableHead>
                    <TableHead scope="col" className="hidden text-xs uppercase text-[#FFFFFF]/60 sm:table-cell">Emissão</TableHead>
                    <TableHead scope="col" className="hidden text-xs uppercase text-[#FFFFFF]/60 md:table-cell">Vencimento</TableHead>
                    <TableHead scope="col" className="hidden text-xs uppercase text-[#FFFFFF]/60 lg:table-cell">Cobrança</TableHead>
                    <TableHead scope="col" className="text-xs uppercase text-[#FFFFFF]/60">Status</TableHead>
                    <TableHead scope="col" className="text-right text-xs uppercase text-[#FFFFFF]/60">Ações</TableHead>
                  </TableRow>
                </TableHeader>

                <TableBody>
                  {carregando ? (
                    Array.from({ length: 6 }).map((_, i) => (
                      <TableRow key={i} className="border-[#FFFFFF]/8">
                        {Array.from({ length: 8 }).map((__, j) => (
                          <TableCell key={j}>
                            <div className="h-3 animate-pulse rounded bg-[#FFFFFF]/10" />
                          </TableCell>
                        ))}
                      </TableRow>
                    ))
                  ) : visiveis.length === 0 ? (
                    <TableRow className="border-0 hover:bg-transparent">
                      <TableCell colSpan={8} className="py-12 text-center text-sm text-[#FFFFFF]/40">
                        Nenhum recebimento encontrado.
                      </TableCell>
                    </TableRow>
                  ) : (
                    visiveis.map(payment => (
                      <TableRow
                        key={payment.id}
                        className="border-[#FFFFFF]/8 transition-colors hover:bg-[#FFFFFF]/3"
                      >
                        {/* ID */}
                        <TableCell className="font-mono text-xs text-[#FFFFFF]/50">
                          #{payment.id}
                        </TableCell>

                        {/* Valor */}
                        <TableCell className="font-semibold text-[#FFFFFF]">
                          {formatarMoeda(payment.value)}
                        </TableCell>

                        {/* Descrição */}
                        <TableCell>
                          <span className="block max-w-[180px] truncate text-sm text-[#FFFFFF]/85" title={payment.description}>
                            {payment.description}
                          </span>
                        </TableCell>

                        {/* Emissão */}
                        <TableCell className="hidden text-xs text-[#FFFFFF]/65 sm:table-cell">
                          {formatarData(payment.issueDate)}
                        </TableCell>

                        {/* Vencimento */}
                        <TableCell className="hidden text-xs text-[#FFFFFF]/65 md:table-cell">
                          {formatarData(payment.dueDate)}
                        </TableCell>

                        {/* Cobrança / Método */}
                        <TableCell className="hidden lg:table-cell">
                          <span className="text-xs text-[#FFFFFF]/55">
                            {payment.paymentMethod ?? "—"}
                          </span>
                        </TableCell>

                        {/* Status */}
                        <TableCell>
                          <Badge
                            variant="outline"
                            className={`flex w-fit items-center gap-1 text-[10px] font-medium ${STATUS_STYLE[payment.status] ?? "border-[#FFFFFF]/20 bg-[#FFFFFF]/8 text-[#FFFFFF]/50"}`}
                          >
                            {STATUS_ICON[payment.status]}
                            {payment.status}
                          </Badge>
                        </TableCell>

                        {/* Ações */}
                        <TableCell className="text-right">
                          <div className="flex items-center justify-end gap-1">
                            <button
                              onClick={() => abrirEditar(payment)}
                              aria-label="Editar recebimento"
                              className="rounded-md p-1.5 text-[#FFFFFF]/40 transition-colors hover:bg-[#FFFFFF]/8 hover:text-[#FFFFFF]"
                            >
                              <Pencil className="h-3.5 w-3.5" />
                            </button>
                            {deletandoId === payment.id ? (
                              <div className="flex items-center gap-1">
                                <button
                                  onClick={() => excluir(payment.id)}
                                  className="rounded-md px-2 py-1 text-[10px] font-medium text-[#DD050A] transition-colors hover:bg-[#DD050A]/10"
                                >
                                  Confirmar
                                </button>
                                <button
                                  onClick={() => setDeletandoId(null)}
                                  className="rounded-md px-2 py-1 text-[10px] text-[#FFFFFF]/50 transition-colors hover:bg-[#FFFFFF]/5"
                                >
                                  Cancelar
                                </button>
                              </div>
                            ) : (
                              <button
                                onClick={() => setDeletandoId(payment.id)}
                                aria-label="Excluir recebimento"
                                className="rounded-md p-1.5 text-[#FFFFFF]/40 transition-colors hover:bg-[#DD050A]/10 hover:text-[#DD050A]"
                              >
                                <Trash2 className="h-3.5 w-3.5" />
                              </button>
                            )}
                          </div>
                        </TableCell>
                      </TableRow>
                    ))
                  )}
                </TableBody>
              </Table>

              {/* ── Paginação ── */}
              {totalPaginas > 1 && (
                <div className="mt-4 flex items-center justify-between">
                  <p className="text-xs text-[#FFFFFF]/50">
                    Mostrando {(paginaAtual - 1) * POR_PAGINA + 1}–
                    {Math.min(paginaAtual * POR_PAGINA, filtrados.length)} de {filtrados.length}
                  </p>
                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => setPagina(p => Math.max(1, p - 1))}
                      disabled={paginaAtual === 1}
                      className="rounded-md p-1.5 text-[#FFFFFF]/50 transition-colors hover:bg-[#FFFFFF]/8 hover:text-[#FFFFFF] disabled:opacity-30"
                      aria-label="Página anterior"
                    >
                      <ChevronLeft className="h-4 w-4" />
                    </button>
                    {numerasDePagina(paginaAtual, totalPaginas).map((p, i) =>
                      p === "…" ? (
                        <span key={`ellipsis-${i}`} className="px-1 text-xs text-[#FFFFFF]/30">…</span>
                      ) : (
                        <button
                          key={p}
                          onClick={() => setPagina(p)}
                          className={`min-w-[28px] rounded-md px-2 py-1 text-xs transition-colors ${
                            p === paginaAtual
                              ? "bg-[#DD050A] text-[#FFFFFF]"
                              : "text-[#FFFFFF]/60 hover:bg-[#FFFFFF]/8 hover:text-[#FFFFFF]"
                          }`}
                        >
                          {p}
                        </button>
                      )
                    )}
                    <button
                      onClick={() => setPagina(p => Math.min(totalPaginas, p + 1))}
                      disabled={paginaAtual === totalPaginas}
                      className="rounded-md p-1.5 text-[#FFFFFF]/50 transition-colors hover:bg-[#FFFFFF]/8 hover:text-[#FFFFFF] disabled:opacity-30"
                      aria-label="Próxima página"
                    >
                      <ChevronRight className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>
        </main>
      </div>

      {/* ── Sheet: Criar / Editar ── */}
      <Sheet open={sheetAberto} onOpenChange={setSheetAberto}>
        <SheetContent
          side="right"
          className="flex w-full flex-col gap-0 border-l border-[#FFFFFF]/10 bg-[#020203] p-0 text-[#FFFFFF] sm:max-w-md"
        >
          <SheetHeader className="border-b border-[#FFFFFF]/10 px-6 py-5">
            <SheetTitle className="text-base text-[#FFFFFF]">
              {modo === "criar" ? "Novo Recebimento" : "Editar Recebimento"}
            </SheetTitle>
            <SheetDescription className="text-xs text-[#FFFFFF]/55">
              {modo === "criar"
                ? "Preencha os dados para registrar um novo recebimento."
                : "Atualize os dados do recebimento selecionado."}
            </SheetDescription>
          </SheetHeader>

          <div className="flex-1 overflow-y-auto px-6 py-5">
            {erro && (
              <div className="mb-4 rounded-md border border-[#DD050A]/40 bg-[#DD050A]/10 px-3 py-2 text-xs text-[#DD050A]">
                {erro}
              </div>
            )}

            <div className="space-y-4">
              {/* Descrição */}
              <div className="flex flex-col gap-1.5">
                <Label htmlFor="descricao" className={labelClass}>Descrição *</Label>
                <Input
                  id="descricao"
                  value={form.description}
                  onChange={e => setForm(f => ({ ...f, description: e.target.value }))}
                  placeholder="Ex: Assinatura | Plano Mensal"
                  className={inputClass}
                />
              </div>

              {/* Valor + Método */}
              <div className="grid grid-cols-2 gap-4">
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="valor" className={labelClass}>Valor (R$) *</Label>
                  <Input
                    id="valor"
                    inputMode="decimal"
                    value={form.value}
                    onChange={e => setForm(f => ({ ...f, value: e.target.value }))}
                    placeholder="0,00"
                    className={inputClass}
                  />
                </div>
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="metodo" className={labelClass}>Método de Pagamento</Label>
                  <select
                    id="metodo"
                    value={form.paymentMethod}
                    onChange={e => setForm(f => ({ ...f, paymentMethod: e.target.value }))}
                    className={FORM_SELECT_CLASS}
                  >
                    <option value="">Selecionar...</option>
                    {METHOD_OPTIONS.map(m => (
                      <option key={m} value={m}>{m}</option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Datas */}
              <div className="grid grid-cols-2 gap-4">
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="vencimento" className={labelClass}>Vencimento *</Label>
                  <Input
                    id="vencimento"
                    type="date"
                    value={form.dueDate}
                    onChange={e => setForm(f => ({ ...f, dueDate: e.target.value }))}
                    className={`${inputClass} [color-scheme:dark]`}
                  />
                </div>
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="emissao" className={labelClass}>Data de Emissão</Label>
                  <Input
                    id="emissao"
                    type="date"
                    value={form.issueDate}
                    onChange={e => setForm(f => ({ ...f, issueDate: e.target.value }))}
                    className={`${inputClass} [color-scheme:dark]`}
                  />
                </div>
              </div>

              {/* Status + Assinatura */}
              <div className="grid grid-cols-2 gap-4">
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="status" className={labelClass}>Status</Label>
                  <select
                    id="status"
                    value={form.status}
                    onChange={e => setForm(f => ({ ...f, status: e.target.value }))}
                    className={FORM_SELECT_CLASS}
                  >
                    <option value="A receber">A receber</option>
                    <option value="Pago">Pago</option>
                    <option value="Vencido">Vencido</option>
                  </select>
                </div>
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="assinatura" className={labelClass}>ID da Assinatura</Label>
                  <Input
                    id="assinatura"
                    inputMode="numeric"
                    value={form.subscriptionId}
                    onChange={e => setForm(f => ({ ...f, subscriptionId: e.target.value }))}
                    placeholder="Ex: 1"
                    className={inputClass}
                  />
                </div>
              </div>
            </div>
          </div>

          <SheetFooter className="flex-row gap-2 border-t border-[#FFFFFF]/10 px-6 py-4">
            <SheetClose asChild>
              <button className="flex-1 rounded-lg border border-[#FFFFFF]/20 py-2 text-sm text-[#FFFFFF]/70 transition-colors hover:bg-[#FFFFFF]/5 hover:text-[#FFFFFF]">
                Cancelar
              </button>
            </SheetClose>
            <button
              onClick={salvar}
              disabled={!form.description.trim() || !form.value || !form.dueDate}
              className="flex-1 rounded-lg bg-[#DD050A] py-2 text-sm font-medium text-[#FFFFFF] transition-colors hover:bg-[#DD050A]/85 disabled:opacity-50"
            >
              {modo === "criar" ? "Registrar" : "Salvar"}
            </button>
          </SheetFooter>
        </SheetContent>
      </Sheet>
    </SidebarProvider>
  )
}
