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
  AlertCircle, Clock, Loader2, BadgeCheck,
} from "lucide-react"
import {
  paymentService, subscriptionService,
  type Payment, type PaymentRequest, type Subscription,
} from "./payment.service"
import { api } from "@/lib/api"

interface StudentLite { id: number; name: string }
interface PlanLite    { id: number; name: string }

// ─── Types ───────────────────────────────────────────────────────────────────

interface FormState {
  description: string
  value: string
  paymentMethod: string
  dueDate: string
  issueDate: string
  status: string
  subscriptionId: string
}

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
  "Pago":      "border-green-500/30 bg-green-500/10 text-green-600 dark:border-[#00FF00]/30 dark:bg-[#00FF00]/10 dark:text-[#00FF00]",
  "A receber": "border-amber-500/30 bg-amber-500/10 text-amber-600 dark:border-[#F59E0B]/30 dark:bg-[#F59E0B]/10 dark:text-[#F59E0B]",
  "Vencido":   "border-red-500/30 bg-red-500/10 text-red-600 dark:border-[#DD050A]/30 dark:bg-[#DD050A]/10 dark:text-[#DD050A]",
}

const STATUS_ICON: Record<string, React.ReactNode> = {
  "Pago":      <CheckCircle className="h-3 w-3" />,
  "A receber": <Clock className="h-3 w-3" />,
  "Vencido":   <AlertCircle className="h-3 w-3" />,
}

const SELECT_CLASS =
  "h-8 rounded-md border border-zinc-300 bg-white px-2.5 text-xs text-zinc-900 focus:border-[#DD050A]/50 focus:outline-none dark:border-[#FFFFFF]/15 dark:bg-[#000000] dark:text-[#FFFFFF]"

const FORM_SELECT_CLASS =
  "h-9 w-full rounded-md border border-zinc-300 bg-white px-3 text-sm text-zinc-900 focus:border-[#DD050A]/50 focus:outline-none disabled:opacity-50 dark:border-[#FFFFFF]/15 dark:bg-[#000000] dark:text-[#FFFFFF]"

// ─── Helpers ─────────────────────────────────────────────────────────────────

function formatarData(iso: string | null): string {
  if (!iso) return "—"
  const [ano, mes, dia] = iso.slice(0, 10).split("-")
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

function parseCurrency(raw: string): number {
  const str = raw.trim()
  if (str.includes(",") && str.includes(".")) {
    // pt-BR thousands+decimal: "1.234,56" → dot=thousands, comma=decimal
    return parseFloat(str.replace(/\./g, "").replace(",", "."))
  }
  if (str.includes(",")) {
    // comma-only: "1234,56" → comma=decimal
    return parseFloat(str.replace(",", "."))
  }
  if (str.includes(".")) {
    // dot-only: check digits after last dot to decide role
    // ≤2 digits → decimal ("1234.56"); >2 digits → thousands ("1.234")
    const afterDot = str.split(".").pop() ?? ""
    return afterDot.length <= 2
      ? parseFloat(str)
      : parseFloat(str.replace(/\./g, ""))
  }
  return parseFloat(str)
}

function formToRequest(form: FormState): PaymentRequest {
  return {
    description:    form.description.trim(),
    value:          parseCurrency(form.value),
    paymentMethod:  form.paymentMethod || null,
    dueDate:        form.dueDate,
    issueDate:      form.issueDate || null,
    status:         form.status,
    subscriptionId: form.subscriptionId ? Number(form.subscriptionId) : null,
  }
}

// ─── Component ───────────────────────────────────────────────────────────────

export function RecebimentosClient() {
  const [payments, setPayments]             = useState<Payment[]>([])
  const [subscriptions, setSubscriptions]   = useState<Subscription[]>([])
  const [subLookup, setSubLookup]           = useState<Subscription[]>([])
  const [students, setStudents]             = useState<StudentLite[]>([])
  const [plans, setPlans]                   = useState<PlanLite[]>([])
  const [busca, setBusca]                   = useState("")
  const [filtroStatus, setFiltroStatus]     = useState<(typeof STATUS_OPTIONS)[number]>("Todos")
  const [pagina, setPagina]                 = useState(1)
  const [sheetAberto, setSheetAberto]       = useState(false)
  const [modo, setModo]                     = useState<"criar" | "editar">("criar")
  const [editando, setEditando]             = useState<Payment | null>(null)
  const [deletandoId, setDeletandoId]       = useState<number | null>(null)
  const [recebendoId, setRecebendoId]       = useState<number | null>(null)
  const [form, setForm]                     = useState<FormState>(FORM_VAZIO)
  const [carregando, setCarregando]         = useState(true)
  const [carregandoSubs, setCarregandoSubs] = useState(false)
  const [salvando, setSalvando]             = useState(false)
  const [erro, setErro]                     = useState<string | null>(null)

  // ── Fetch payments ─────────────────────────────────────────────────────────

  const carregarPayments = useCallback(async () => {
    try {
      setErro(null)
      setCarregando(true)
      const data = await paymentService.findAll()
      setPayments(data)
    } catch (err) {
      console.error("Erro ao carregar recebimentos", err)
      setErro("Não foi possível carregar os recebimentos.")
    } finally {
      setCarregando(false)
    }
  }, [])

  useEffect(() => { void carregarPayments() }, [carregarPayments])

  // ── Fetch de referência para lookup na tabela (mount) ─────────────────────

  useEffect(() => {
    Promise.all([
      api.get<{ data: Subscription[] }>("/api/subscriptions"),
      api.get<{ data: StudentLite[] }>("/api/students"),
    ])
      .then(([subRes, stuRes]) => {
        setSubLookup(Array.isArray(subRes.data?.data) ? subRes.data.data : [])
        setStudents(Array.isArray(stuRes.data?.data) ? stuRes.data.data : [])
      })
      .catch(err => console.error("Erro ao carregar dados de referência", err))
  }, [])

  // ── Fetch subscriptions ao abrir sheet de criação ──────────────────────────

  const carregarSubscriptions = useCallback(async () => {
    if (subscriptions.length > 0) return
    try {
      setCarregandoSubs(true)
      const [subs, stuRes, planRes] = await Promise.all([
        subscriptionService.findAllActive(),
        api.get<{ data: StudentLite[] }>("/api/students"),
        api.get<{ data: PlanLite[] }>("/api/plans"),
      ])
      setSubscriptions(subs)
      setStudents(Array.isArray(stuRes.data?.data) ? stuRes.data.data : [])
      setPlans(Array.isArray(planRes.data?.data) ? planRes.data.data : [])
    } catch (err) {
      console.error("Erro ao carregar assinaturas", err)
    } finally {
      setCarregandoSubs(false)
    }
  }, [subscriptions.length])

  // ── Lookup maps para o select de assinatura ───────────────────────────────

  const studentById = useMemo(() => new Map(students.map(s => [s.id, s.name])), [students])
  const planById    = useMemo(() => new Map(plans.map(p => [p.id, p.name])), [plans])
  const subById     = useMemo(() => new Map(subLookup.map(s => [s.id, s.studentId])), [subLookup])

  // ── Derived state ──────────────────────────────────────────────────────────

  const filtrados = useMemo(
    () =>
      payments
        .filter(p => {
          if (busca === "") return true
          const termo = busca.toLowerCase()
          const studentName = studentById.get(subById.get(p.subscriptionId) ?? -1) ?? ""
          return (
            p.description.toLowerCase().includes(termo) ||
            String(p.id).includes(termo) ||
            (p.paymentMethod ?? "").toLowerCase().includes(termo) ||
            studentName.toLowerCase().includes(termo)
          )
        })
        .filter(p => filtroStatus === "Todos" ? true : p.status === filtroStatus)
        .sort((a, b) => {
          if (!a.issueDate && !b.issueDate) return 0
          if (!a.issueDate) return 1
          if (!b.issueDate) return -1
          return b.issueDate.localeCompare(a.issueDate)
        }),
    [payments, busca, filtroStatus, studentById, subById]
  )

  const totalPaginas = Math.max(1, Math.ceil(filtrados.length / POR_PAGINA))
  const paginaAtual  = Math.min(pagina, totalPaginas)
  const visiveis     = filtrados.slice((paginaAtual - 1) * POR_PAGINA, paginaAtual * POR_PAGINA)

  const totalAReceber    = payments.filter(p => p.status === "A receber").reduce((a, p) => a + p.value, 0)
  const totalAtrasados   = payments.filter(p => p.status === "Vencido").reduce((a, p) => a + p.value, 0)
  const pagosDoMes = useMemo(
    () => payments.filter(p => p.status === "Pago"),
    [payments]
  )

  const totalRecebidoMes = useMemo(
    () => pagosDoMes.reduce((a, p) => a + p.value, 0),
    [pagosDoMes]
  )

  function mudarFiltro(fn: () => void) { fn(); setPagina(1) }

  // ── Sheet actions ──────────────────────────────────────────────────────────

  function abrirCriar() {
    setModo("criar")
    setForm(FORM_VAZIO)
    setEditando(null)
    setErro(null)
    setSheetAberto(true)
    void carregarSubscriptions()
  }

  function abrirEditar(p: Payment) {
    setModo("editar")
    setForm({
      description:    p.description,
      value:          String(p.value),
      paymentMethod:  p.paymentMethod ?? "",
      dueDate:        p.dueDate?.slice(0, 10) ?? "",
      issueDate:      p.issueDate?.slice(0, 10) ?? "",
      status:         p.status,
      subscriptionId: p.subscriptionId != null ? String(p.subscriptionId) : "",
    })
    setEditando(p)
    setErro(null)
    setSheetAberto(true)
    // Editar não precisa re-selecionar assinatura — campo fica readonly
  }

  async function salvar() {
    if (!form.description.trim() || !form.value || !form.dueDate) return
    if (!form.paymentMethod) {
      setErro("Selecione o método de pagamento.")
      return
    }
    if (modo === "criar" && !form.subscriptionId) {
      setErro("Selecione uma assinatura ativa para vincular o recebimento.")
      return
    }
    try {
      setSalvando(true)
      setErro(null)
      const request = formToRequest(form)

      if (isNaN(request.value)) {
        setErro("Valor inválido. Use o formato numérico correto (ex: 150,00).")
        setSalvando(false)
        return
      }

      if (modo === "criar") {
        const created = await paymentService.create(request)
        setPayments(prev => [created, ...prev])
      } else if (editando) {
        const updated = await paymentService.update(editando.id, request)
        setPayments(prev => prev.map(p => p.id === editando.id ? updated : p))
      }

      setSheetAberto(false)
    } catch (err: unknown) {
      console.error("Erro ao salvar recebimento", err)
      const msg = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message
      setErro(msg ?? "Não foi possível salvar o recebimento.")
    } finally {
      setSalvando(false)
    }
  }

  async function excluir(id: number) {
    try {
      await paymentService.remove(id)
      setPayments(prev => prev.filter(p => p.id !== id))
      setErro(null)
    } catch (err) {
      console.error("Erro ao excluir recebimento", err)
      setErro("Não foi possível excluir o recebimento.")
    } finally {
      setDeletandoId(null)
    }
  }

  async function receberPagamento(payment: Payment) {
    try {
      setRecebendoId(payment.id)
      const request: PaymentRequest = {
        subscriptionId: payment.subscriptionId,
        description:    payment.description,
        value:          payment.value,
        paymentMethod:  payment.paymentMethod,
        dueDate:        payment.dueDate,
        issueDate:      payment.issueDate,
        status:         "Pago",
      }
      const updated = await paymentService.update(payment.id, request)
      setPayments(prev => prev.map(p => p.id === payment.id ? updated : p))
    } catch (err) {
      console.error("Erro ao registrar recebimento", err)
      setErro("Não foi possível registrar o recebimento.")
    } finally {
      setRecebendoId(null)
    }
  }

  // ── Styles ─────────────────────────────────────────────────────────────────

  const labelClass = "text-xs font-medium text-zinc-600 dark:text-[#FFFFFF]/80"
  const inputClass =
    "border-zinc-300 bg-white text-zinc-900 placeholder:text-zinc-400 focus-visible:border-[#DD050A]/50 dark:border-[#FFFFFF]/15 dark:bg-[#000000] dark:text-[#FFFFFF] dark:placeholder:text-[#FFFFFF]/30"

  const canSave =
    form.description.trim() !== "" &&
    form.value !== "" &&
    form.dueDate !== "" &&
    form.paymentMethod !== "" &&
    (modo === "editar" || form.subscriptionId !== "") &&
    !salvando

  // ── Render ─────────────────────────────────────────────────────────────────

  return (
    <SidebarProvider>
      <AppSidebar />
      <div className="flex min-h-screen flex-1 flex-col bg-zinc-50 text-zinc-900 dark:bg-[#000000] dark:text-[#FFFFFF]">

        {/* ── Header ── */}
        <header className="sticky top-0 z-10 flex items-center justify-between gap-3 border-b border-zinc-200 bg-white/90 px-4 py-3 backdrop-blur sm:px-6 sm:py-4 dark:border-[#FFFFFF]/10 dark:bg-[#020203]/90">
          <div className="flex items-center gap-3">
            <SidebarTrigger className="text-zinc-500 hover:text-zinc-900 md:hidden dark:text-[#FFFFFF]/70 dark:hover:text-[#FFFFFF]" />
            <Separator orientation="vertical" className="h-5 bg-zinc-200 md:hidden dark:bg-[#FFFFFF]/20" />
            <div>
              <h1 className="text-sm font-semibold leading-none text-zinc-900 dark:text-[#FFFFFF]">Recebimentos</h1>
              <p className="mt-0.5 text-xs text-zinc-500 dark:text-[#FFFFFF]/60">Gerencie os pagamentos e cobranças</p>
            </div>
          </div>
          <button
            onClick={abrirCriar}
            className="flex items-center gap-2 rounded-lg border border-[#DD050A]/50 bg-[#DD050A]/15 px-3 py-2 text-xs font-medium text-[#DD050A] transition-colors hover:border-[#DD050A]/70 hover:bg-[#DD050A]/25 dark:text-[#FFFFFF]"
          >
            <Plus className="h-3.5 w-3.5" />
            <span className="hidden sm:inline">Novo Recebimento</span>
          </button>
        </header>

        <main className="flex-1 space-y-5 p-4 sm:space-y-6 sm:p-6">

          {/* ── KPI Cards ── */}
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
            {([
              {
                label: "Total a Receber",
                valor: formatarMoeda(totalAReceber),
                sub: `${payments.filter(p => p.status === "A receber").length} cobranças pendentes`,
                subClass: "text-zinc-500 dark:text-[#FFFFFF]/60",
                icon: DollarSign,
                accentColor: "group-hover:text-[#DD050A]",
                accentBg: "group-hover:bg-[#DD050A]/12",
              },
              {
                label: "Atrasados",
                valor: formatarMoeda(totalAtrasados),
                sub: `${payments.filter(p => p.status === "Vencido").length} cobranças vencidas`,
                subClass: "text-red-500 dark:text-[#DD050A]",
                icon: TrendingDown,
                accentColor: "text-[#DD050A]",
                accentBg: "bg-[#DD050A]/12",
                valorClass: "text-red-500 dark:text-[#DD050A]",
              },
              {
                label: "Total Recebido",
                valor: formatarMoeda(totalRecebidoMes),
                sub: `${pagosDoMes.length} pagamento${pagosDoMes.length !== 1 ? "s" : ""} confirmado${pagosDoMes.length !== 1 ? "s" : ""}`,
                subClass: "text-green-600 dark:text-[#00FF00]/80",
                icon: CheckCircle,
                accentColor: "text-green-600 group-hover:text-green-500 dark:text-[#00AA00] dark:group-hover:text-[#00CC44]",
                accentBg: "group-hover:bg-green-500/12 dark:group-hover:bg-[#00FF00]/12",
                valorClass: "text-green-600 dark:text-[#00CC44]",
              },
            ] as const).map(({ label, valor, sub, subClass, icon: Icon, accentColor, accentBg, valorClass }) => (
              <Card key={label} className="group border-zinc-200 bg-white transition-colors hover:border-[#DD050A]/50 dark:border-[#FFFFFF]/12 dark:bg-[#020203]">
                <CardHeader className="flex flex-row items-center justify-between px-4 pb-2 pt-4 sm:px-5">
                  <CardTitle className="text-xs font-medium uppercase tracking-wider text-zinc-500 dark:text-[#FFFFFF]/65">
                    {label}
                  </CardTitle>
                  <span className={`flex h-7 w-7 items-center justify-center rounded-md bg-[#FFFFFF]/10 transition-colors ${accentBg}`}>
                    <Icon className={`h-4 w-4 text-[#FFFFFF] transition-colors ${accentColor}`} />
                  </span>
                </CardHeader>
                <CardContent className="px-4 pb-4 sm:px-5">
                  <p className={`text-xl font-bold sm:text-2xl ${(valorClass as string | undefined) ?? "text-zinc-900 dark:text-[#FFFFFF]"}`}>
                    {valor}
                  </p>
                  <p className={`mt-1 text-xs ${subClass}`}>{sub}</p>
                </CardContent>
              </Card>
            ))}
          </div>

          {/* ── Table Card ── */}
          <Card className="border-zinc-200 bg-white dark:border-[#FFFFFF]/12 dark:bg-[#020203]">
            <CardHeader className="px-4 pb-3 pt-5 sm:px-5">
              <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                <div>
                  <CardTitle className="text-base text-zinc-900 dark:text-[#FFFFFF]">Lista de Recebimentos</CardTitle>
                  <CardDescription className="text-xs text-zinc-500 dark:text-[#FFFFFF]/60">
                    {filtrados.length} recebimento{filtrados.length !== 1 ? "s" : ""} encontrado{filtrados.length !== 1 ? "s" : ""}
                  </CardDescription>
                </div>
                <div className="flex flex-wrap items-center gap-2">
                  <div className="relative">
                    <Search className="absolute left-2.5 top-1/2 h-3.5 w-3.5 -translate-y-1/2 text-zinc-400 dark:text-[#FFFFFF]/40" />
                    <Input
                      type="search"
                      placeholder="Buscar por descrição ou ID..."
                      value={busca}
                      onChange={e => mudarFiltro(() => setBusca(e.target.value))}
                      className="h-8 w-full border-zinc-300 bg-white pl-8 text-xs text-zinc-900 placeholder:text-zinc-400 focus-visible:border-[#DD050A]/50 sm:w-52 dark:border-[#FFFFFF]/15 dark:bg-[#000000] dark:text-[#FFFFFF] dark:placeholder:text-[#FFFFFF]/35"
                    />
                  </div>
                  <div className="flex items-center gap-1.5">
                    <Filter className="h-3.5 w-3.5 text-zinc-400 dark:text-[#FFFFFF]/40" />
                    <select
                      value={filtroStatus}
                      onChange={e => mudarFiltro(() => setFiltroStatus(e.target.value as (typeof STATUS_OPTIONS)[number]))}
                      aria-label="Filtrar por status"
                      className={SELECT_CLASS}
                    >
                      {STATUS_OPTIONS.map(s => <option key={s} value={s}>{s}</option>)}
                    </select>
                  </div>
                </div>
              </div>
            </CardHeader>

            <CardContent className="px-4 pb-5 sm:px-5">
              {erro && !sheetAberto && (
                <div className="mb-4 rounded-md border border-[#DD050A]/40 bg-[#DD050A]/10 px-3 py-2 text-xs text-[#DD050A]">
                  {erro}
                </div>
              )}

              <Table className="min-w-[640px] table-fixed sm:min-w-0">
                <TableCaption className="sr-only">
                  Lista de recebimentos com informações de valor, descrição, datas e status
                </TableCaption>
                <TableHeader>
                  <TableRow className="border-zinc-200 hover:bg-transparent dark:border-[#FFFFFF]/10">
                    <TableHead scope="col" className="w-16 text-xs uppercase text-zinc-500 dark:text-[#FFFFFF]/60">ID</TableHead>
                    <TableHead scope="col" className="text-xs uppercase text-zinc-500 dark:text-[#FFFFFF]/60">Valor</TableHead>
                    <TableHead scope="col" className="text-xs uppercase text-zinc-500 dark:text-[#FFFFFF]/60">Descrição</TableHead>
                    <TableHead scope="col" className="hidden text-xs uppercase text-zinc-500 md:table-cell dark:text-[#FFFFFF]/60">Aluno</TableHead>
                    <TableHead scope="col" className="hidden text-xs uppercase text-zinc-500 sm:table-cell dark:text-[#FFFFFF]/60">Emissão</TableHead>
                    <TableHead scope="col" className="hidden text-xs uppercase text-zinc-500 lg:table-cell dark:text-[#FFFFFF]/60">Vencimento</TableHead>
                    <TableHead scope="col" className="hidden text-xs uppercase text-zinc-500 xl:table-cell dark:text-[#FFFFFF]/60">Método</TableHead>
                    <TableHead scope="col" className="text-xs uppercase text-zinc-500 dark:text-[#FFFFFF]/60">Status</TableHead>
                    <TableHead scope="col" className="text-right text-xs uppercase text-zinc-500 dark:text-[#FFFFFF]/60">Ações</TableHead>
                  </TableRow>
                </TableHeader>

                <TableBody>
                  {carregando ? (
                    Array.from({ length: 6 }).map((_, i) => (
                      <TableRow key={i} className="border-[#FFFFFF]/8">
                        {Array.from({ length: 9 }).map((__, j) => (
                          <TableCell key={j}>
                            <div className="h-3 animate-pulse rounded bg-[#FFFFFF]/10" />
                          </TableCell>
                        ))}
                      </TableRow>
                    ))
                  ) : visiveis.length === 0 ? (
                    <TableRow className="border-0 hover:bg-transparent">
                      <TableCell colSpan={9} className="py-12 text-center text-sm text-[#FFFFFF]/40">
                        Nenhum recebimento encontrado.
                      </TableCell>
                    </TableRow>
                  ) : (
                    visiveis.map(payment => (
                      <TableRow key={payment.id} className="border-zinc-100 transition-colors hover:bg-zinc-50 dark:border-[#FFFFFF]/8 dark:hover:bg-[#FFFFFF]/3">
                        <TableCell className="font-mono text-xs text-zinc-400 dark:text-[#FFFFFF]/50">#{payment.id}</TableCell>
                        <TableCell className="font-semibold text-zinc-900 dark:text-[#FFFFFF]">{formatarMoeda(payment.value)}</TableCell>
                        <TableCell>
                          <span className="block max-w-[180px] truncate text-sm text-zinc-700 dark:text-[#FFFFFF]/85" title={payment.description}>
                            {payment.description}
                          </span>
                        </TableCell>
                        <TableCell className="hidden text-xs text-zinc-700 md:table-cell dark:text-[#FFFFFF]/80">
                          {studentById.get(subById.get(payment.subscriptionId) ?? -1) ?? "—"}
                        </TableCell>
                        <TableCell className="hidden text-xs text-zinc-500 sm:table-cell dark:text-[#FFFFFF]/65">
                          {formatarData(payment.issueDate)}
                        </TableCell>
                        <TableCell className="hidden text-xs text-zinc-500 lg:table-cell dark:text-[#FFFFFF]/65">
                          {formatarData(payment.dueDate)}
                        </TableCell>
                        <TableCell className="hidden xl:table-cell">
                          <span className="text-xs text-zinc-500 dark:text-[#FFFFFF]/55">{payment.paymentMethod ?? "—"}</span>
                        </TableCell>
                        <TableCell>
                          <Badge
                            variant="outline"
                            className={`flex w-fit items-center gap-1 text-[10px] font-medium ${
                              STATUS_STYLE[payment.status] ?? "border-[#FFFFFF]/20 bg-[#FFFFFF]/8 text-[#FFFFFF]/50"
                            }`}
                          >
                            {STATUS_ICON[payment.status]}
                            {payment.status}
                          </Badge>
                        </TableCell>
                        <TableCell className="text-right">
                          <div className="flex items-center justify-end gap-1">
                            {payment.status !== "Pago" && (
                              <button
                                onClick={() => receberPagamento(payment)}
                                disabled={recebendoId === payment.id}
                                aria-label="Marcar como pago"
                                title="Marcar como pago"
                                className="rounded-md p-1.5 text-zinc-400 transition-colors hover:bg-green-500/10 hover:text-green-600 disabled:opacity-50 dark:text-[#FFFFFF]/40 dark:hover:bg-[#00FF00]/10 dark:hover:text-[#00FF00]"
                              >
                                {recebendoId === payment.id
                                  ? <Loader2 className="h-3.5 w-3.5 animate-spin" />
                                  : <BadgeCheck className="h-3.5 w-3.5" />
                                }
                              </button>
                            )}
                            <button
                              onClick={() => abrirEditar(payment)}
                              aria-label="Editar recebimento"
                              className="rounded-md p-1.5 text-zinc-400 transition-colors hover:bg-zinc-100 hover:text-zinc-700 dark:text-[#FFFFFF]/40 dark:hover:bg-[#FFFFFF]/8 dark:hover:text-[#FFFFFF]"
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
                                  className="rounded-md px-2 py-1 text-[10px] text-zinc-500 transition-colors hover:bg-zinc-100 dark:text-[#FFFFFF]/50 dark:hover:bg-[#FFFFFF]/5"
                                >
                                  Cancelar
                                </button>
                              </div>
                            ) : (
                              <button
                                onClick={() => setDeletandoId(payment.id)}
                                aria-label="Excluir recebimento"
                                className="rounded-md p-1.5 text-zinc-400 transition-colors hover:bg-[#DD050A]/10 hover:text-[#DD050A] dark:text-[#FFFFFF]/40"
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
                  <p className="text-xs text-zinc-500 dark:text-[#FFFFFF]/50">
                    Mostrando {(paginaAtual - 1) * POR_PAGINA + 1}–{Math.min(paginaAtual * POR_PAGINA, filtrados.length)} de {filtrados.length}
                  </p>
                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => setPagina(p => Math.max(1, p - 1))}
                      disabled={paginaAtual === 1}
                      className="rounded-md p-1.5 text-zinc-500 transition-colors hover:bg-zinc-100 hover:text-zinc-900 disabled:opacity-30 dark:text-[#FFFFFF]/50 dark:hover:bg-[#FFFFFF]/8 dark:hover:text-[#FFFFFF]"
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
                              : "text-zinc-500 hover:bg-zinc-100 hover:text-zinc-900 dark:text-[#FFFFFF]/60 dark:hover:bg-[#FFFFFF]/8 dark:hover:text-[#FFFFFF]"
                          }`}
                        >
                          {p}
                        </button>
                      )
                    )}
                    <button
                      onClick={() => setPagina(p => Math.min(totalPaginas, p + 1))}
                      disabled={paginaAtual === totalPaginas}
                      className="rounded-md p-1.5 text-zinc-500 transition-colors hover:bg-zinc-100 hover:text-zinc-900 disabled:opacity-30 dark:text-[#FFFFFF]/50 dark:hover:bg-[#FFFFFF]/8 dark:hover:text-[#FFFFFF]"
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
      <Sheet open={sheetAberto} onOpenChange={open => { setSheetAberto(open); if (!open) setErro(null) }}>
        <SheetContent
          side="right"
          className="flex w-full flex-col gap-0 border-l border-zinc-200 bg-white p-0 text-zinc-900 sm:max-w-md dark:border-[#FFFFFF]/10 dark:bg-[#020203] dark:text-[#FFFFFF]"
        >
          <SheetHeader className="border-b border-zinc-200 px-6 py-5 dark:border-[#FFFFFF]/10">
            <SheetTitle className="text-base text-zinc-900 dark:text-[#FFFFFF]">
              {modo === "criar" ? "Novo Recebimento" : "Editar Recebimento"}
            </SheetTitle>
            <SheetDescription className="text-xs text-zinc-500 dark:text-[#FFFFFF]/55">
              {modo === "criar"
                ? "Preencha os dados para registrar um novo recebimento."
                : "Atualize os dados do recebimento selecionado."}
            </SheetDescription>
          </SheetHeader>

          <div className="flex-1 overflow-y-auto px-6 py-5">
            {erro && sheetAberto && (
              <div className="mb-4 rounded-md border border-[#DD050A]/40 bg-[#DD050A]/10 px-3 py-2 text-xs text-[#DD050A]">
                {erro}
              </div>
            )}

            <div className="space-y-4">

              {/* ── Assinatura (select real) — só aparece na criação ── */}
              {modo === "criar" && (
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="assinatura" className={labelClass}>
                    Assinatura *
                    {carregandoSubs && (
                      <Loader2 className="ml-1.5 inline h-3 w-3 animate-spin text-[#FFFFFF]/40" />
                    )}
                  </Label>
                  <select
                    id="assinatura"
                    value={form.subscriptionId}
                    onChange={e => setForm(f => ({ ...f, subscriptionId: e.target.value }))}
                    disabled={carregandoSubs}
                    className={FORM_SELECT_CLASS}
                  >
                    <option value="">
                      {carregandoSubs
                        ? "Carregando assinaturas..."
                        : subscriptions.length === 0
                          ? "Nenhuma assinatura ativa encontrada"
                          : "Selecionar assinatura..."}
                    </option>
                    {subscriptions.map(s => (
                      <option key={s.id} value={String(s.id)}>
                        {studentById.get(s.studentId) ?? `Aluno #${s.studentId}`} · {planById.get(s.planId) ?? `Plano #${s.planId}`}
                      </option>
                    ))}
                  </select>
                  {subscriptions.length === 0 && !carregandoSubs && (
                    <p className="text-[10px] text-[#FFFFFF]/40">
                      Crie uma assinatura ativa antes de registrar um pagamento manualmente.
                    </p>
                  )}
                </div>
              )}

              {/* ── Na edição mostramos o ID como readonly ── */}
              {modo === "editar" && (
                <div className="flex flex-col gap-1.5">
                  <Label className={labelClass}>Assinatura</Label>
                  <div className="flex h-9 items-center rounded-md border border-[#FFFFFF]/10 bg-[#FFFFFF]/5 px-3 text-sm text-[#FFFFFF]/50">
                    #{form.subscriptionId}
                    <span className="ml-2 text-xs text-[#FFFFFF]/35">(não pode ser alterada)</span>
                  </div>
                </div>
              )}

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
                  <Label htmlFor="metodo" className={labelClass}>Método de Pagamento *</Label>
                  <select
                    id="metodo"
                    aria-required="true"
                    value={form.paymentMethod}
                    onChange={e => setForm(f => ({ ...f, paymentMethod: e.target.value }))}
                    className={FORM_SELECT_CLASS}
                  >
                    <option value="">Selecionar...</option>
                    {METHOD_OPTIONS.map(m => <option key={m} value={m}>{m}</option>)}
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
                    className={`${inputClass} dark:[color-scheme:dark]`}
                  />
                </div>
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="emissao" className={labelClass}>Data de Emissão</Label>
                  <Input
                    id="emissao"
                    type="date"
                    value={form.issueDate}
                    onChange={e => setForm(f => ({ ...f, issueDate: e.target.value }))}
                    className={`${inputClass} dark:[color-scheme:dark]`}
                  />
                </div>
              </div>

              {/* Status */}
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

            </div>
          </div>

          <SheetFooter className="flex-row gap-2 border-t border-zinc-200 px-6 py-4 dark:border-[#FFFFFF]/10">
            <SheetClose asChild>
              <button className="flex-1 rounded-lg border border-zinc-300 py-2 text-sm text-zinc-600 transition-colors hover:bg-zinc-100 hover:text-zinc-900 dark:border-[#FFFFFF]/20 dark:text-[#FFFFFF]/70 dark:hover:bg-[#FFFFFF]/5 dark:hover:text-[#FFFFFF]">
                Cancelar
              </button>
            </SheetClose>
            <button
              onClick={salvar}
              disabled={!canSave}
              className="flex flex-1 items-center justify-center gap-2 rounded-lg bg-[#DD050A] py-2 text-sm font-medium text-[#FFFFFF] transition-colors hover:bg-[#DD050A]/85 disabled:opacity-50"
            >
              {salvando && <Loader2 className="h-3.5 w-3.5 animate-spin" />}
              {salvando ? "Salvando..." : modo === "criar" ? "Registrar" : "Salvar"}
            </button>
          </SheetFooter>
        </SheetContent>
      </Sheet>
    </SidebarProvider>
  )
}
