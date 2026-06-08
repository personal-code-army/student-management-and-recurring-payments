"use client"

import { useEffect, useMemo, useState } from "react"
import { Badge } from "@/components/ui/badge"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table"
import { CircleAlert, CircleDollarSign, Clock, Users, CheckCircle, AlertCircle } from "lucide-react"
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { AppSidebar } from "@/components/app-sidebar"
import { api } from "@/lib/api"

// ─── Types ────────────────────────────────────────────────────────────────────

interface Payment {
  id: number
  subscriptionId: number
  description: string
  value: number
  paymentMethod: string | null
  dueDate: string
  issueDate: string | null
  status: string
}

interface Student {
  id: number
  name: string
  planId: number | null
  active: boolean
}

interface Subscription {
  id: number
  studentId: number
  planId: number
  status: string
}

interface Plan {
  id: number
  name: string
  monthlyAmount: number
  frequency: number
}

type ApiEnv<T> = { data: T }

// ─── Helpers ─────────────────────────────────────────────────────────────────

const brl = (n: number) =>
  n.toLocaleString("pt-BR", { style: "currency", currency: "BRL", maximumFractionDigits: 0 })

const fmtDate = (iso: string | null) => {
  if (!iso) return "—"
  const [y, m, d] = iso.slice(0, 10).split("-")
  return `${d}/${m}/${y}`
}

const STATUS_MAP: Record<string, string> = {
  paid:        "Pago",
  PAID:        "Pago",
  pending:     "A receber",
  PENDING:     "A receber",
  overdue:     "Vencido",
  OVERDUE:     "Vencido",
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

// ─── Component ────────────────────────────────────────────────────────────────

export default function Dashboard() {
  const [payments,      setPayments]      = useState<Payment[]>([])
  const [students,      setStudents]      = useState<Student[]>([])
  const [subscriptions, setSubscriptions] = useState<Subscription[]>([])
  const [plans,         setPlans]         = useState<Plan[]>([])
  const [loading,       setLoading]       = useState(true)
  const [error,         setError]         = useState<string | null>(null)

  useEffect(() => {
    let active = true
    ;(async () => {
      try {
        setError(null)
        setLoading(true)
        const [payRes, stuRes, subRes, planRes] = await Promise.all([
          api.get<ApiEnv<Payment[]>>("/api/payments"),
          api.get<ApiEnv<Student[]>>("/api/students"),
          api.get<ApiEnv<Subscription[]>>("/api/subscriptions"),
          api.get<ApiEnv<Plan[]>>("/api/plans"),
        ])
        if (!active) return
        const rawPayments = Array.isArray(payRes.data?.data) ? payRes.data.data : []
        setPayments(rawPayments.map(p => ({ ...p, status: STATUS_MAP[p.status] ?? p.status })))
        setStudents(Array.isArray(stuRes.data?.data) ? stuRes.data.data : [])
        setSubscriptions(Array.isArray(subRes.data?.data) ? subRes.data.data : [])
        setPlans(Array.isArray(planRes.data?.data) ? planRes.data.data : [])
      } catch (e) {
        console.error("Erro ao carregar dashboard", e)
        if (active) setError("Não foi possível carregar os dados do dashboard.")
      } finally {
        if (active) setLoading(false)
      }
    })()
    return () => { active = false }
  }, [])

  // ── Lookup maps ───────────────────────────────────────────────────────────
  const subById     = useMemo(() => new Map(subscriptions.map(s => [s.id,  s])), [subscriptions])
  const studentById = useMemo(() => new Map(students.map(s     => [s.id,  s])), [students])
  const planById    = useMemo(() => new Map(plans.map(p        => [p.id,  p])), [plans])

  // ── KPIs ──────────────────────────────────────────────────────────────────
  const receitaTotal  = useMemo(() => payments.filter(p => p.status === "Pago").reduce((s, p) => s + p.value, 0), [payments])
  const totalAReceber = useMemo(() => payments.filter(p => p.status === "A receber").reduce((s, p) => s + p.value, 0), [payments])
  const qtdAtrasados  = useMemo(() => payments.filter(p => p.status === "Vencido").length, [payments])
  const clientesAtivos = useMemo(() => students.filter(s => s.active).length, [students])

  // ── Enriched rows ─────────────────────────────────────────────────────────
  const enrich = (p: Payment) => {
    const sub     = subById.get(p.subscriptionId)
    const student = sub ? studentById.get(sub.studentId) : undefined
    const plan    = sub ? planById.get(sub.planId)      : undefined
    return { ...p, alunoNome: student?.name ?? "—", planoNome: plan?.name ?? "—" }
  }

  const recentes = useMemo(() =>
    [...payments]
      .sort((a, b) => b.dueDate.localeCompare(a.dueDate))
      .slice(0, 8)
      .map(enrich),
    [payments, subById, studentById, planById]
  )

  const atrasados = useMemo(() =>
    [...payments]
      .filter(p => p.status === "Vencido")
      .sort((a, b) => a.dueDate.localeCompare(b.dueDate))
      .map(enrich),
    [payments, subById, studentById, planById]
  )

  // ── Skeleton ──────────────────────────────────────────────────────────────
  const Skeleton = ({ className }: { className?: string }) => (
    <div className={`animate-pulse rounded bg-zinc-200 dark:bg-[#FFFFFF]/10 ${className ?? ""}`} />
  )

  return (
    <SidebarProvider>
      <AppSidebar />
      <div className="flex min-h-screen flex-1 flex-col bg-zinc-50 text-zinc-900 dark:bg-[#000000] dark:text-[#FFFFFF]">

        {/* Header */}
        <header className="sticky top-0 z-20 flex h-14 items-center gap-3 border-b border-zinc-200 bg-white/90 px-6 backdrop-blur-sm dark:border-[#FFFFFF]/10 dark:bg-[#020203]/90">
          <SidebarTrigger className="text-zinc-600 hover:text-zinc-900 dark:text-[#FFFFFF]/60 dark:hover:text-[#FFFFFF]" />
          <div aria-hidden className="h-5 w-px shrink-0 self-center bg-zinc-200 dark:bg-[#FFFFFF]/15" />
          <div>
            <h1 className="text-sm font-semibold leading-none">Visão Geral</h1>
            <p className="mt-0.5 text-xs text-zinc-500 dark:text-[#FFFFFF]/45">Bem-vindo de volta</p>
          </div>
        </header>

        <main className="flex-1 space-y-6 p-6">

          {error && (
            <div className="rounded-lg border border-[#DD050A]/30 bg-[#DD050A]/10 px-4 py-3 text-sm text-[#DD050A]">
              {error}
            </div>
          )}

          {/* KPI Cards */}
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">

            {/* Receita Total */}
            <div className="rounded-xl border border-zinc-200 bg-white p-5 dark:border-[#FFFFFF]/10 dark:bg-[#020203]">
              <div className="mb-2 flex items-center justify-between">
                <span className="text-xs font-medium uppercase tracking-wide text-zinc-500 dark:text-[#FFFFFF]/45">Receita Total</span>
                <CircleDollarSign className="h-4 w-4 text-emerald-500" />
              </div>
              {loading ? (
                <>
                  <Skeleton className="mb-1 h-7 w-28" />
                  <Skeleton className="h-3 w-36" />
                </>
              ) : (
                <>
                  <p className="text-2xl font-bold">{brl(receitaTotal)}</p>
                  <p className="mt-1 text-xs text-zinc-500 dark:text-[#FFFFFF]/45">
                    {payments.filter(p => p.status === "Pago").length} pagamento(s) recebido(s)
                  </p>
                </>
              )}
            </div>

            {/* A Receber */}
            <div className="rounded-xl border border-zinc-200 bg-white p-5 dark:border-[#FFFFFF]/10 dark:bg-[#020203]">
              <div className="mb-2 flex items-center justify-between">
                <span className="text-xs font-medium uppercase tracking-wide text-zinc-500 dark:text-[#FFFFFF]/45">A Receber</span>
                <Clock className="h-4 w-4 text-amber-500" />
              </div>
              {loading ? (
                <>
                  <Skeleton className="mb-1 h-7 w-28" />
                  <Skeleton className="h-3 w-36" />
                </>
              ) : (
                <>
                  <p className="text-2xl font-bold">{brl(totalAReceber)}</p>
                  <p className="mt-1 text-xs text-zinc-500 dark:text-[#FFFFFF]/45">
                    {payments.filter(p => p.status === "A receber").length} pagamento(s) pendente(s)
                  </p>
                </>
              )}
            </div>

            {/* Atrasados */}
            <div className="rounded-xl border border-zinc-200 bg-white p-5 dark:border-[#FFFFFF]/10 dark:bg-[#020203]">
              <div className="mb-2 flex items-center justify-between">
                <span className="text-xs font-medium uppercase tracking-wide text-zinc-500 dark:text-[#FFFFFF]/45">Atrasados</span>
                <CircleAlert className="h-4 w-4 text-[#DD050A]" />
              </div>
              {loading ? (
                <>
                  <Skeleton className="mb-1 h-7 w-16" />
                  <Skeleton className="h-3 w-24" />
                </>
              ) : (
                <>
                  <p className="text-2xl font-bold">{qtdAtrasados}</p>
                  <p className={`mt-1 text-xs ${qtdAtrasados > 0 ? "text-[#DD050A]" : "text-zinc-500 dark:text-[#FFFFFF]/45"}`}>
                    {qtdAtrasados > 0 ? "Requer atenção" : "Em dia"}
                  </p>
                </>
              )}
            </div>

            {/* Clientes Ativos */}
            <div className="rounded-xl border border-zinc-200 bg-white p-5 dark:border-[#FFFFFF]/10 dark:bg-[#020203]">
              <div className="mb-2 flex items-center justify-between">
                <span className="text-xs font-medium uppercase tracking-wide text-zinc-500 dark:text-[#FFFFFF]/45">Clientes Ativos</span>
                <Users className="h-4 w-4 text-blue-500" />
              </div>
              {loading ? (
                <>
                  <Skeleton className="mb-1 h-7 w-16" />
                  <Skeleton className="h-3 w-24" />
                </>
              ) : (
                <>
                  <p className="text-2xl font-bold">{clientesAtivos}</p>
                  <p className="mt-1 text-xs text-zinc-500 dark:text-[#FFFFFF]/45">
                    de {students.length} aluno(s) cadastrado(s)
                  </p>
                </>
              )}
            </div>

          </div>

          {/* Tabela de Pagamentos */}
          <div className="rounded-xl border border-zinc-200 bg-white dark:border-[#FFFFFF]/10 dark:bg-[#020203]">
            <Tabs defaultValue="recentes">
              <div className="flex items-center justify-between border-b border-zinc-200 px-5 pt-4 pb-0 dark:border-[#FFFFFF]/10">
                <div className="pb-4">
                  <h2 className="text-sm font-semibold">Pagamentos</h2>
                  <p className="text-xs text-zinc-500 dark:text-[#FFFFFF]/45">Últimas movimentações</p>
                </div>
                <TabsList className="mb-4 border border-zinc-200 bg-zinc-50 dark:border-[#FFFFFF]/10 dark:bg-[#FFFFFF]/5">
                  <TabsTrigger value="recentes" className="text-xs">Recentes</TabsTrigger>
                  <TabsTrigger value="atrasados" className="text-xs">
                    Atrasados
                    {!loading && qtdAtrasados > 0 && (
                      <span className="ml-1.5 rounded-full bg-[#DD050A]/15 px-1.5 py-0.5 text-[10px] font-medium text-[#DD050A]">
                        {qtdAtrasados}
                      </span>
                    )}
                  </TabsTrigger>
                </TabsList>
              </div>

              {/* Tab: Recentes */}
              <TabsContent value="recentes" className="m-0">
                {loading ? (
                  <div className="space-y-3 p-5">
                    {[1, 2, 3, 4, 5].map(i => <Skeleton key={i} className="h-10 w-full" />)}
                  </div>
                ) : recentes.length === 0 ? (
                  <div className="flex flex-col items-center justify-center py-12 text-zinc-400 dark:text-[#FFFFFF]/30">
                    <CircleDollarSign className="mb-2 h-8 w-8" />
                    <p className="text-sm">Nenhum pagamento encontrado</p>
                  </div>
                ) : (
                  <div className="overflow-x-auto">
                    <Table>
                      <TableHeader>
                        <TableRow className="border-zinc-200 dark:border-[#FFFFFF]/10">
                          <TableHead className="text-xs font-medium text-zinc-500 dark:text-[#FFFFFF]/45">Aluno</TableHead>
                          <TableHead className="text-xs font-medium text-zinc-500 dark:text-[#FFFFFF]/45">Plano</TableHead>
                          <TableHead className="text-xs font-medium text-zinc-500 dark:text-[#FFFFFF]/45">Valor</TableHead>
                          <TableHead className="text-xs font-medium text-zinc-500 dark:text-[#FFFFFF]/45">Vencimento</TableHead>
                          <TableHead className="text-xs font-medium text-zinc-500 dark:text-[#FFFFFF]/45">Status</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {recentes.map(p => (
                          <TableRow key={p.id} className="border-zinc-100 dark:border-[#FFFFFF]/5">
                            <TableCell className="font-medium text-zinc-900 dark:text-[#FFFFFF]">{p.alunoNome}</TableCell>
                            <TableCell className="text-zinc-600 dark:text-[#FFFFFF]/60">{p.planoNome}</TableCell>
                            <TableCell className="text-zinc-900 dark:text-[#FFFFFF]">
                              {p.value.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}
                            </TableCell>
                            <TableCell className="text-zinc-600 dark:text-[#FFFFFF]/60">{fmtDate(p.dueDate)}</TableCell>
                            <TableCell>
                              <Badge variant="outline" className={`flex w-fit items-center gap-1 text-xs ${STATUS_STYLE[p.status] ?? ""}`}>
                                {STATUS_ICON[p.status]}
                                {p.status}
                              </Badge>
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </div>
                )}
              </TabsContent>

              {/* Tab: Atrasados */}
              <TabsContent value="atrasados" className="m-0">
                {loading ? (
                  <div className="space-y-3 p-5">
                    {[1, 2, 3].map(i => <Skeleton key={i} className="h-10 w-full" />)}
                  </div>
                ) : atrasados.length === 0 ? (
                  <div className="flex flex-col items-center justify-center py-12 text-zinc-400 dark:text-[#FFFFFF]/30">
                    <CheckCircle className="mb-2 h-8 w-8 text-emerald-500 opacity-60" />
                    <p className="text-sm">Nenhum pagamento atrasado</p>
                  </div>
                ) : (
                  <div className="overflow-x-auto">
                    <Table>
                      <TableHeader>
                        <TableRow className="border-zinc-200 dark:border-[#FFFFFF]/10">
                          <TableHead className="text-xs font-medium text-zinc-500 dark:text-[#FFFFFF]/45">Aluno</TableHead>
                          <TableHead className="text-xs font-medium text-zinc-500 dark:text-[#FFFFFF]/45">Plano</TableHead>
                          <TableHead className="text-xs font-medium text-zinc-500 dark:text-[#FFFFFF]/45">Valor</TableHead>
                          <TableHead className="text-xs font-medium text-zinc-500 dark:text-[#FFFFFF]/45">Vencimento</TableHead>
                          <TableHead className="text-xs font-medium text-zinc-500 dark:text-[#FFFFFF]/45">Status</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {atrasados.map(p => (
                          <TableRow key={p.id} className="border-zinc-100 dark:border-[#FFFFFF]/5">
                            <TableCell className="font-medium text-zinc-900 dark:text-[#FFFFFF]">{p.alunoNome}</TableCell>
                            <TableCell className="text-zinc-600 dark:text-[#FFFFFF]/60">{p.planoNome}</TableCell>
                            <TableCell className="text-zinc-900 dark:text-[#FFFFFF]">
                              {p.value.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}
                            </TableCell>
                            <TableCell className="text-zinc-600 dark:text-[#FFFFFF]/60">{fmtDate(p.dueDate)}</TableCell>
                            <TableCell>
                              <Badge variant="outline" className={`flex w-fit items-center gap-1 text-xs ${STATUS_STYLE[p.status] ?? ""}`}>
                                {STATUS_ICON[p.status]}
                                {p.status}
                              </Badge>
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </div>
                )}
              </TabsContent>

            </Tabs>
          </div>

        </main>
      </div>
    </SidebarProvider>
  )
}
