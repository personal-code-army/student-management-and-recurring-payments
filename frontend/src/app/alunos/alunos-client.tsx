"use client"

import { useCallback, useEffect, useMemo, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
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
import {
  Users, UserCheck, UserX, Star, TrendingUp, Plus,
  Phone, Pencil, Trash2, ChevronLeft, ChevronRight, Search,
} from "lucide-react"
import { api } from "@/lib/api"
import { formatCpf, formatPhone, isValidEmail, normalizeCpf, normalizePhone } from "@/lib/validators"
import { ApiErrorScreen } from "@/components/api-error-screen"
import { type ApiErrorInfo, getApiErrorInfo, resolveApiErrorContent } from "@/lib/api-errors"

interface Aluno {
  id: number
  name: string
  cpf: string
  birthDate: string
  age?: number | null
  phone?: string | null
  email?: string | null
  address?: string | null
  planId?: number | null
  active: boolean
}

interface Plano {
  id: number
  name: string
  monthlyAmount?: number | null
  frequency?: number | null
}

interface FormState {
  name: string
  cpf: string
  birthDate: string
  phone: string
  email: string
  address: string
  planId: string
  active: boolean
}

interface Plano {
  id: number
  name: string
}

type ApiResponse<T> = {
  data: T
}

const STATUS_OPTIONS = ["Todos", "Ativo", "Inativo"] as const
const POR_PAGINA = 10

const FORM_VAZIO: FormState = {
  name: "",
  cpf: "",
  birthDate: "",
  phone: "",
  email: "",
  address: "",
  planId: "",
  active: true,
}

const STATUS_STYLE: Record<"Ativo" | "Inativo", string> = {
  Ativo: "border-emerald-500/30 bg-emerald-500/10 text-emerald-600 dark:border-[#00FF00]/30 dark:bg-[#00FF00]/10 dark:text-[#00FF00]",
  Inativo: "border-zinc-300 bg-zinc-100 text-zinc-500 dark:border-[#FFFFFF]/20 dark:bg-[#FFFFFF]/8 dark:text-[#FFFFFF]/50",
}

const SELECT_CLASS = "h-8 rounded-md border border-zinc-300 bg-white px-2.5 text-xs text-zinc-900 focus:border-[#DD050A]/50 focus:outline-none dark:border-[#FFFFFF]/15 dark:bg-[#000000] dark:text-[#FFFFFF]"
const FORM_SELECT_CLASS = "h-9 w-full rounded-md border border-zinc-300 bg-white px-3 text-sm text-zinc-900 focus:border-[#DD050A]/50 focus:outline-none dark:border-[#FFFFFF]/15 dark:bg-[#000000] dark:text-[#FFFFFF]"

function normalizeIsoDate(value: string): string {
  if (!value) return ""
  return value.slice(0, 10)
}

function formatarData(iso: string): string {
  const normalized = normalizeIsoDate(iso)
  if (!normalized) return ""
  const [ano, mes, dia] = normalized.split("-")
  return `${dia}/${mes}/${ano}`
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

export function AlunosClient() {
  const [alunos, setAlunos] = useState<Aluno[]>([])
  const [planos, setPlanos] = useState<Plano[]>([])
  const [busca, setBusca] = useState("")
  const [buscaDigitada, setBuscaDigitada] = useState("")
  const [filtroStatus, setFiltroStatus] = useState<(typeof STATUS_OPTIONS)[number]>("Todos")
  const [pagina, setPagina] = useState(1)
  const [sheetAberto, setSheetAberto] = useState(false)
  const [modo, setModo] = useState<"criar" | "editar">("criar")
  const [editando, setEditando] = useState<Aluno | null>(null)
  const [deletandoId, setDeletandoId] = useState<number | null>(null)
  const [form, setForm] = useState<FormState>(FORM_VAZIO)
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState<string | null>(null)
  const [planos, setPlanos] = useState<Plano[]>([])
  const [apiError, setApiError] = useState<ApiErrorInfo | null>(null)

  const carregarAlunos = useCallback(async () => {
    try {
      setErro(null)
      setApiError(null)
      setCarregando(true)
      const response = await api.get<ApiResponse<Aluno[]>>("/api/students")
      setAlunos(Array.isArray(response.data?.data) ? response.data.data : [])
    } catch (err) {
      console.error("Erro ao carregar alunos", err)
      const info = getApiErrorInfo(err)
      setApiError(info ?? { status: 500, message: "Nao foi possivel carregar os alunos." })
    } finally {
      setCarregando(false)
    }
  }, [])

  const carregarPlanos = useCallback(async () => {
    try {
      const response = await api.get<ApiResponse<Plano[]>>("/api/plans")
      setPlanos(Array.isArray(response.data?.data) ? response.data.data : [])
    } catch (err) {
      console.error("Erro ao carregar planos", err)
    }
  }, [])

  useEffect(() => {
    void carregarAlunos()
    void carregarPlanos()
  }, [carregarAlunos, carregarPlanos])

  useEffect(() => {
    api.get<ApiResponse<Plano[]>>("/api/plans")
      .then(res => setPlanos(Array.isArray(res.data?.data) ? res.data.data : []))
      .catch(err => console.error("Erro ao carregar planos", err))
  }, [])

  const planoPorId = useMemo(() => {
    const map = new Map<number, Plano>()
    planos.forEach(p => map.set(p.id, p))
    return map
  }, [planos])

  const filtrados = useMemo(() => alunos
    .filter(a => {
      if (busca === "") return true
      const termo = busca.toLowerCase()
      return (
        a.name.toLowerCase().includes(termo) ||
        a.email?.toLowerCase().includes(termo) ||
        a.cpf?.toLowerCase().includes(termo)
      )
    })
    .filter(a => {
      if (filtroStatus === "Todos") return true
      return filtroStatus === "Ativo" ? a.active : !a.active
    }),
    [alunos, busca, filtroStatus])

  const totalPaginas = Math.max(1, Math.ceil(filtrados.length / POR_PAGINA))
  const paginaAtual = Math.min(pagina, totalPaginas)
  const visiveis = filtrados.slice((paginaAtual - 1) * POR_PAGINA, paginaAtual * POR_PAGINA)

  const ativos = alunos.filter(a => a.active).length
  const inativos = alunos.filter(a => !a.active).length
  const planoPopular = useMemo(() => {
    const contagem = new Map<number, number>()
    for (const a of alunos) {
      if (a.planId != null) contagem.set(a.planId, (contagem.get(a.planId) ?? 0) + 1)
    }
    let melhorId: number | null = null
    let melhorQtd = 0
    for (const [id, qtd] of contagem) {
      if (qtd > melhorQtd) { melhorQtd = qtd; melhorId = id }
    }
    if (melhorId == null) return null
    return { nome: planoPorId.get(melhorId)?.name ?? `#${melhorId}`, qtd: melhorQtd }
  }, [alunos, planoPorId])
  const emailValido = useMemo(() => isValidEmail(form.email), [form.email])
  const emailInvalido = form.email.trim() !== "" && !emailValido

  function mudarFiltro(fn: () => void) { fn(); setPagina(1) }
  function aplicarBusca() {
    mudarFiltro(() => setBusca(buscaDigitada.trim()))
    void carregarAlunos()
  }

  function abrirCriar() {
    setModo("criar"); setForm(FORM_VAZIO); setEditando(null); setSheetAberto(true)
  }

  function abrirEditar(a: Aluno) {
    setModo("editar")
    setForm({
      name: a.name,
      cpf: formatCpf(a.cpf),
      birthDate: normalizeIsoDate(a.birthDate ?? ""),
      phone: formatPhone(a.phone ?? ""),
      email: a.email ?? "",
      address: a.address ?? "",
      planId: a.planId != null ? String(a.planId) : "",
      active: a.active,
    })
    setEditando(a)
    setSheetAberto(true)
  }

  async function salvar() {
    if (!form.name.trim() || !form.cpf.trim() || !form.birthDate || !emailValido) return
    try {
      const cpfDigits = normalizeCpf(form.cpf)
      if (cpfDigits.length !== 11) {
        setErro("CPF invalido.")
        return
      }

      const planIdValue = form.planId.trim()
      if (planIdValue && !/^\d+$/.test(planIdValue)) {
        setErro("Plano invalido.")
        return
      }

      const planId = planIdValue ? Number(planIdValue) : null
      const payload = {
        name: form.name.trim(),
        cpf: cpfDigits,
        birthDate: form.birthDate,
        phone: normalizePhone(form.phone) || null,
        email: form.email?.trim() || null,
        address: form.address?.trim() || null,
        planId,
        active: form.active,
      }

      if (modo === "criar") {
        const response = await api.post<ApiResponse<Aluno>>("/api/students", payload)
        if (response.data?.data) {
          setAlunos(prev => [...prev, response.data.data])
          setErro(null)
        }
      } else if (editando) {
        const response = await api.put<ApiResponse<Aluno>>(`/api/students/${editando.id}`, payload)
        if (response.data?.data) {
          setAlunos(prev => prev.map(a => a.id === editando.id ? response.data.data : a))
          setErro(null)
        }
      }
      setSheetAberto(false)
    } catch (err) {
      console.error("Erro ao salvar aluno", err)
      const info = getApiErrorInfo(err)
      const { description, detail } = resolveApiErrorContent(info)
      setErro(detail ?? description)
    }
  }

  async function excluir(id: number) {
    try {
      await api.delete(`/api/students/${id}`)
      setAlunos(prev => prev.filter(a => a.id !== id))
      setErro(null)
    } catch (err) {
      console.error("Erro ao excluir aluno", err)
      const info = getApiErrorInfo(err)
      const { description, detail } = resolveApiErrorContent(info)
      setErro(detail ?? description)
    } finally {
      setDeletandoId(null)
    }
  }

  const labelClass = "text-xs font-medium text-zinc-700 dark:text-[#FFFFFF]/80"
  const inputClass = "border-zinc-300 bg-white text-zinc-900 placeholder:text-zinc-400 focus-visible:border-[#DD050A]/50 dark:border-[#FFFFFF]/15 dark:bg-[#000000] dark:text-[#FFFFFF] dark:placeholder:text-[#FFFFFF]/30"

  return (
    <SidebarProvider>
      <AppSidebar />
      <div className="flex min-h-screen flex-1 flex-col bg-white text-zinc-900 dark:bg-[#000000] dark:text-[#FFFFFF]">

        {/* Header */}
        <header className="sticky top-0 z-10 flex items-center justify-between gap-3 border-b border-zinc-200 bg-white/90 px-4 py-3 backdrop-blur sm:px-6 sm:py-4 dark:border-[#FFFFFF]/10 dark:bg-[#020203]/90">
          <div className="flex items-center gap-3">
            <SidebarTrigger className="text-zinc-600 hover:text-zinc-900 dark:text-[#FFFFFF]/70 dark:hover:text-[#FFFFFF]" />
            <div
              aria-hidden
              className="shrink-0 self-center bg-zinc-300 dark:bg-[#FFFFFF]/45"
              style={{ width: "1px", height: "20px" }}
            />
            <div>
              <h1 className="text-sm font-semibold leading-none text-zinc-900 dark:text-[#FFFFFF]">Alunos</h1>
              <p className="mt-0.5 text-xs text-zinc-500 dark:text-[#FFFFFF]/60">Gerencie os alunos cadastrados</p>
            </div>
          </div>
          <button
            onClick={abrirCriar}
            className="flex items-center gap-2 rounded-lg border border-[#DD050A]/50 bg-[#DD050A]/15 px-3 py-2 text-xs font-medium text-[#DD050A] transition-colors hover:border-[#DD050A]/70 hover:bg-[#DD050A]/25 dark:text-[#FFFFFF]"
          >
            <Plus className="h-3.5 w-3.5" />
            <span className="hidden sm:inline">Novo Aluno</span>
          </button>
        </header>

        <main className="flex-1 space-y-5 p-4 sm:space-y-6 sm:p-6">
          <ApiErrorScreen
            error={apiError}
            onRetry={carregarAlunos}
            onClose={() => setApiError(null)}
          />
          {/* KPI Cards */}
          <div className="grid grid-cols-2 gap-4 xl:grid-cols-4">
            {[
              { label: "Total de Alunos", valor: alunos.length, sub: "Base cadastrada", subClass: "text-[#DD050A]", icon: Users, IconExtra: TrendingUp },
              { label: "Ativos", valor: ativos, sub: `${alunos.length > 0 ? Math.round((ativos / alunos.length) * 100) : 0}% do total`, subClass: "text-zinc-500 dark:text-[#FFFFFF]/60", icon: UserCheck },
              { label: "Plano popular", valor: planoPopular?.nome ?? "—", sub: planoPopular ? `${planoPopular.qtd} aluno${planoPopular.qtd !== 1 ? "s" : ""}` : "Nenhum vinculado", subClass: "text-[#DD050A]", icon: Star },
              { label: "Inativos", valor: inativos, sub: "Sem plano ativo", subClass: "text-zinc-500 dark:text-[#FFFFFF]/60", icon: UserX },
            ].map(({ label, valor, sub, subClass, icon: Icon, IconExtra }) => (
              <Card key={label} className="group border-zinc-200 bg-white transition-colors hover:border-[#DD050A]/50 dark:border-[#FFFFFF]/12 dark:bg-[#020203]">
                <CardHeader className="flex flex-row items-center justify-between px-4 pb-2 pt-4 sm:px-5">
                  <CardTitle className="text-xs font-medium uppercase tracking-wider text-zinc-600 dark:text-[#FFFFFF]/65">{label}</CardTitle>
                  <span className="flex h-7 w-7 items-center justify-center rounded-md bg-zinc-100 transition-colors group-hover:bg-[#DD050A]/12 dark:bg-[#FFFFFF]/10">
                    <Icon className="h-4 w-4 text-zinc-900 transition-colors group-hover:text-[#DD050A] dark:text-[#FFFFFF]" />
                  </span>
                </CardHeader>
                <CardContent className="px-4 pb-4 sm:px-5">
                  <p className="text-xl font-bold text-zinc-900 sm:text-2xl dark:text-[#FFFFFF]">{valor}</p>
                  <p className={`mt-1 flex items-center gap-1 text-xs ${subClass}`}>
                    {IconExtra && <IconExtra className="h-3 w-3" />}{sub}
                  </p>
                </CardContent>
              </Card>
            ))}
          </div>

          {/* Tabela */}
          <Card className="border-zinc-200 bg-white dark:border-[#FFFFFF]/12 dark:bg-[#020203]">
            <CardHeader className="px-4 pb-3 pt-5 sm:px-5">
              <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                <div>
                  <CardTitle className="text-base text-zinc-900 dark:text-[#FFFFFF]">Lista de Alunos</CardTitle>
                  <CardDescription className="text-xs text-zinc-500 dark:text-[#FFFFFF]/60">
                    {filtrados.length} aluno{filtrados.length !== 1 ? "s" : ""} encontrado{filtrados.length !== 1 ? "s" : ""}
                  </CardDescription>
                </div>

                {/* Filtros */}
                <div className="flex flex-wrap items-center gap-2">
                  <div className="relative">
                    <Search className="absolute left-2.5 top-1/2 h-3.5 w-3.5 -translate-y-1/2 text-zinc-400 dark:text-[#FFFFFF]/40" />
                    <Input
                      type="search"
                      placeholder="Buscar por nome, e-mail ou CPF..."
                      value={busca}
                      onChange={e => mudarFiltro(() => setBusca(e.target.value))}
                      className="h-8 w-full border-zinc-300 bg-white pl-8 text-xs text-zinc-900 placeholder:text-zinc-400 focus-visible:border-[#DD050A]/50 sm:w-44 dark:border-[#FFFFFF]/15 dark:bg-[#000000] dark:text-[#FFFFFF] dark:placeholder:text-[#FFFFFF]/35"
                      value={buscaDigitada}
                      onChange={e => setBuscaDigitada(e.target.value)}
                      onKeyDown={e => {
                        if (e.key === "Enter") {
                          e.preventDefault()
                          aplicarBusca()
                        }
                      }}
                      className="h-8 w-full border-[#FFFFFF]/15 bg-[#000000] pl-8 text-xs text-[#FFFFFF] placeholder:text-[#FFFFFF]/35 focus-visible:border-[#DD050A]/50 sm:w-44"
                    />
                  </div>
                  <button
                    type="button"
                    onClick={aplicarBusca}
                    className="h-8 rounded-md border border-[#DD050A]/40 bg-[#DD050A]/15 px-3 text-xs font-medium text-[#FFFFFF] transition-colors hover:border-[#DD050A]/70 hover:bg-[#DD050A]/25"
                  >
                    Pesquisar
                  </button>
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
            </CardHeader>

            <CardContent className="px-4 pb-5 sm:px-5">
              {erro && (
                <div className="mb-4 rounded-md border border-[#DD050A]/40 bg-[#DD050A]/10 px-3 py-2 text-xs text-[#DD050A]">
                  {erro}
                </div>
              )}
              <Table className="min-w-120 table-fixed sm:min-w-0">
                <TableCaption className="sr-only">
                  Lista de alunos cadastrados com informações de contato, plano e status
                </TableCaption>
                <TableHeader>
                  <TableRow className="border-zinc-200 hover:bg-transparent dark:border-[#FFFFFF]/10">
                    <TableHead scope="col" className="text-xs uppercase text-zinc-500 dark:text-[#FFFFFF]/60">Aluno</TableHead>
                    <TableHead scope="col" className="hidden text-xs uppercase text-zinc-500 md:table-cell dark:text-[#FFFFFF]/60">Contato</TableHead>
                    <TableHead scope="col" className="hidden text-xs uppercase text-zinc-500 sm:table-cell dark:text-[#FFFFFF]/60">CPF</TableHead>
                    <TableHead scope="col" className="hidden text-xs uppercase text-zinc-500 lg:table-cell dark:text-[#FFFFFF]/60">Nascimento</TableHead>
                    <TableHead scope="col" className="hidden text-xs uppercase text-zinc-500 md:table-cell dark:text-[#FFFFFF]/60">Plano</TableHead>
                    <TableHead scope="col" className="text-xs uppercase text-zinc-500 dark:text-[#FFFFFF]/60">Status</TableHead>
                    <TableHead scope="col" className="text-right text-xs uppercase text-zinc-500 dark:text-[#FFFFFF]/60">Ações</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {carregando ? (
                    <TableRow className="hover:bg-transparent">
                      <TableCell colSpan={7} className="py-12 text-center text-sm text-zinc-400 dark:text-[#FFFFFF]/40">
                        Carregando alunos...
                      </TableCell>
                    </TableRow>
                  ) : visiveis.length === 0 ? (
                    <TableRow className="hover:bg-transparent">
                      <TableCell colSpan={7} className="py-12 text-center text-sm text-zinc-400 dark:text-[#FFFFFF]/40">
                        Nenhum aluno encontrado com os filtros selecionados.
                      </TableCell>
                    </TableRow>
                  ) : visiveis.map(aluno => {
                    const telDigits = normalizePhone(aluno.phone ?? "")
                    return (
                      <TableRow
                        key={aluno.id}
                        className={`border-zinc-200 transition-colors dark:border-[#FFFFFF]/10 ${deletandoId === aluno.id ? "bg-[#DD050A]/8" : "hover:bg-zinc-50 dark:hover:bg-[#FFFFFF]/5"}`}
                      >
                        <TableCell>
                          <p className="font-medium text-zinc-900 dark:text-[#FFFFFF]">{aluno.name}</p>
                          <p className="mt-0.5 text-xs text-zinc-500 md:hidden dark:text-[#FFFFFF]/50">{aluno.email ?? "-"}</p>
                          {aluno.age != null && (
                            <p className="mt-0.5 text-xs text-zinc-400 dark:text-[#FFFFFF]/40">idade {aluno.age} anos</p>
                          )}
                        </TableCell>
                        <TableCell className="hidden md:table-cell">
                          <p className="text-sm text-zinc-700 dark:text-[#FFFFFF]/80">{aluno.email ?? "-"}</p>
                          <p className="mt-0.5 text-xs text-zinc-500 dark:text-[#FFFFFF]/50">{aluno.phone ?? "-"}</p>
                        </TableCell>
                        <TableCell className="hidden text-zinc-600 sm:table-cell dark:text-[#FFFFFF]/70">{aluno.cpf}</TableCell>
                        <TableCell className="hidden text-zinc-600 lg:table-cell dark:text-[#FFFFFF]/70">{formatarData(aluno.birthDate)}</TableCell>
                        <TableCell className="hidden text-zinc-600 md:table-cell dark:text-[#FFFFFF]/70">
                          {aluno.planId != null ? (planoPorId.get(aluno.planId)?.name ?? `#${aluno.planId}`) : "-"}
                        </TableCell>
                        <TableCell>
                          <Badge variant="outline" className={STATUS_STYLE[aluno.active ? "Ativo" : "Inativo"]}>
                            {aluno.active ? "Ativo" : "Inativo"}
                          </Badge>
                        </TableCell>
                        <TableCell className="text-right">
                          {deletandoId === aluno.id ? (
                            <div className="flex items-center justify-end gap-1.5">
                              <span className="hidden text-xs text-zinc-500 sm:inline dark:text-[#FFFFFF]/60">Excluir?</span>
                              <button
                                onClick={() => excluir(aluno.id)}
                                className="rounded border border-[#DD050A]/60 bg-[#DD050A]/20 px-2.5 py-1 text-xs font-medium text-[#DD050A] transition-colors hover:bg-[#DD050A]/35"
                              >
                                Sim
                              </button>
                              <button
                                onClick={() => setDeletandoId(null)}
                                className="rounded border border-zinc-300 bg-transparent px-2.5 py-1 text-xs font-medium text-zinc-600 transition-colors hover:bg-zinc-100 dark:border-[#FFFFFF]/20 dark:text-[#FFFFFF]/60 dark:hover:bg-[#FFFFFF]/8"
                              >
                                Não
                              </button>
                            </div>
                          ) : (
                            <div className="flex items-center justify-end gap-1.5">
                              {telDigits.length > 0 ? (
                                <a
                                  href={`https://wa.me/55${telDigits}`}
                                  target="_blank"
                                  rel="noreferrer"
                                  aria-label={`Contatar ${aluno.name} via WhatsApp (abre nova aba)`}
                                  className="inline-flex h-7 w-7 items-center justify-center rounded-md border border-zinc-300 text-zinc-600 transition-colors hover:border-[#DD050A]/40 hover:bg-[#DD050A]/10 hover:text-zinc-900 dark:border-[#FFFFFF]/15 dark:text-[#FFFFFF]/60 dark:hover:text-[#FFFFFF]"
                                >
                                  <Phone className="h-3.5 w-3.5" />
                                </a>
                              ) : (
                                <span
                                  aria-label={`Contato WhatsApp indisponivel para ${aluno.name}`}
                                  aria-disabled="true"
                                  className="inline-flex h-7 w-7 items-center justify-center rounded-md border border-zinc-200 text-zinc-300 cursor-not-allowed dark:border-[#FFFFFF]/10 dark:text-[#FFFFFF]/30"
                                >
                                  <Phone className="h-3.5 w-3.5" />
                                </span>
                              )}
                              <button
                                onClick={() => abrirEditar(aluno)}
                                aria-label={`Editar ${aluno.name}`}
                                className="inline-flex h-7 w-7 items-center justify-center rounded-md border border-zinc-300 text-zinc-600 transition-colors hover:border-[#DD050A]/40 hover:bg-[#DD050A]/10 hover:text-zinc-900 dark:border-[#FFFFFF]/15 dark:text-[#FFFFFF]/60 dark:hover:text-[#FFFFFF]"
                              >
                                <Pencil className="h-3.5 w-3.5" />
                              </button>
                              <button
                                onClick={() => setDeletandoId(aluno.id)}
                                aria-label={`Excluir ${aluno.name}`}
                                className="inline-flex h-7 w-7 items-center justify-center rounded-md border border-zinc-300 text-zinc-600 transition-colors hover:border-[#DD050A]/40 hover:bg-[#DD050A]/10 hover:text-[#DD050A] dark:border-[#FFFFFF]/15 dark:text-[#FFFFFF]/60"
                              >
                                <Trash2 className="h-3.5 w-3.5" />
                              </button>
                            </div>
                          )}
                        </TableCell>
                      </TableRow>
                    )
                  })}
                </TableBody>
              </Table>

              {/* Paginação */}
              <div className="mt-4 flex items-center justify-between border-t border-zinc-200 pt-4 dark:border-[#FFFFFF]/10">
                <p className="text-xs text-zinc-500 dark:text-[#FFFFFF]/50">
                  {filtrados.length === 0
                    ? "Nenhum resultado"
                    : `${(paginaAtual - 1) * POR_PAGINA + 1}–${Math.min(paginaAtual * POR_PAGINA, filtrados.length)} de ${filtrados.length} aluno${filtrados.length !== 1 ? "s" : ""}`
                  }
                </p>
                {totalPaginas > 1 && (
                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => setPagina(p => Math.max(1, p - 1))}
                      disabled={paginaAtual === 1}
                      aria-label="Página anterior"
                      className="inline-flex h-7 w-7 items-center justify-center rounded-md border border-zinc-300 text-zinc-600 transition-colors hover:border-zinc-400 hover:text-zinc-900 disabled:pointer-events-none disabled:opacity-30 dark:border-[#FFFFFF]/15 dark:text-[#FFFFFF]/60 dark:hover:border-[#FFFFFF]/30 dark:hover:text-[#FFFFFF]"
                    >
                      <ChevronLeft className="h-3.5 w-3.5" />
                    </button>
                    {numerasDePagina(paginaAtual, totalPaginas).map((num, idx) =>
                      num === "…" ? (
                        <span key={`ellipsis-${idx}`} className="px-1 text-xs text-zinc-400 dark:text-[#FFFFFF]/40">…</span>
                      ) : (
                        <button
                          key={num}
                          onClick={() => setPagina(num)}
                          aria-label={`Página ${num}`}
                          aria-current={num === paginaAtual ? "page" : undefined}
                          className={`inline-flex h-7 w-7 items-center justify-center rounded-md text-xs font-medium transition-colors ${num === paginaAtual
                              ? "bg-[#DD050A] text-[#FFFFFF]"
                              : "border border-zinc-300 text-zinc-600 hover:border-zinc-400 hover:text-zinc-900 dark:border-[#FFFFFF]/15 dark:text-[#FFFFFF]/60 dark:hover:border-[#FFFFFF]/30 dark:hover:text-[#FFFFFF]"
                            }`}
                        >
                          {num}
                        </button>
                      )
                    )}
                    <button
                      onClick={() => setPagina(p => Math.min(totalPaginas, p + 1))}
                      disabled={paginaAtual === totalPaginas}
                      aria-label="Próxima página"
                      className="inline-flex h-7 w-7 items-center justify-center rounded-md border border-zinc-300 text-zinc-600 transition-colors hover:border-zinc-400 hover:text-zinc-900 disabled:pointer-events-none disabled:opacity-30 dark:border-[#FFFFFF]/15 dark:text-[#FFFFFF]/60 dark:hover:border-[#FFFFFF]/30 dark:hover:text-[#FFFFFF]"
                    >
                      <ChevronRight className="h-3.5 w-3.5" />
                    </button>
                  </div>
                )}
              </div>
            </CardContent>
          </Card>
        </main>
      </div>

      {/* Sheet — Criar / Editar Aluno */}
      <Sheet open={sheetAberto} onOpenChange={setSheetAberto}>
        <SheetContent
          side="right"
          className="flex w-full flex-col border-l border-zinc-200 bg-white text-zinc-900 sm:max-w-md dark:border-[#FFFFFF]/10 dark:bg-[#020203] dark:text-[#FFFFFF]"
        >
          <SheetHeader className="border-b border-zinc-200 px-6 pb-4 pt-6 dark:border-[#FFFFFF]/10">
            <SheetTitle className="text-base text-zinc-900 dark:text-[#FFFFFF]">
              {modo === "criar" ? "Novo Aluno" : "Editar Aluno"}
            </SheetTitle>
            <SheetDescription className="text-xs text-zinc-500 dark:text-[#FFFFFF]/55">
              {modo === "criar"
                ? "Preencha os dados para cadastrar um novo aluno."
                : "Atualize as informações do aluno."}
            </SheetDescription>
          </SheetHeader>

          <div className="flex-1 overflow-y-auto px-6 py-5">
            <div className="flex flex-col gap-5">

              <div className="flex flex-col gap-1.5">
                <Label htmlFor="nome" className={labelClass}>Nome completo</Label>
                <Input id="nome" value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))}
                  placeholder="Ex: João Silva" className={inputClass} />
              </div>

              <div className="flex flex-col gap-1.5">
                <Label htmlFor="cpf" className={labelClass}>CPF</Label>
                <Input
                  id="cpf"
                  inputMode="numeric"
                  value={form.cpf}
                  onChange={e => setForm(f => ({ ...f, cpf: formatCpf(e.target.value) }))}
                  placeholder="000.000.000-00"
                  className={inputClass}
                />
              </div>

              <div className="flex flex-col gap-1.5">
                <Label htmlFor="nascimento" className={labelClass}>Data de nascimento</Label>
                <Input id="nascimento" type="date" value={form.birthDate} onChange={e => setForm(f => ({ ...f, birthDate: e.target.value }))}
                  className={`${inputClass} [color-scheme:light] dark:[color-scheme:dark]`} />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="telefone" className={labelClass}>Telefone / WhatsApp</Label>
                  <Input
                    id="telefone"
                    type="tel"
                    inputMode="numeric"
                    value={form.phone}
                    onChange={e => setForm(f => ({ ...f, phone: formatPhone(e.target.value) }))}
                    placeholder="(11) 99999-0000"
                    className={inputClass}
                  />
                </div>
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="email" className={labelClass}>E-mail</Label>
                  <Input
                    id="email"
                    type="email"
                    value={form.email}
                    onChange={e => setForm(f => ({ ...f, email: e.target.value }))}
                    placeholder="joao@email.com"
                    className={`${inputClass}${emailInvalido ? " border-[#DD050A]/60 focus-visible:border-[#DD050A]" : ""}`}
                    aria-invalid={emailInvalido}
                    aria-describedby={emailInvalido ? "email-error" : undefined}
                  />
                  {emailInvalido && (
                    <p id="email-error" className="text-xs text-[#DD050A]">E-mail invalido.</p>
                  )}
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="endereco" className={labelClass}>Endereco</Label>
                  <Input id="endereco" value={form.address} onChange={e => setForm(f => ({ ...f, address: e.target.value }))}
                    placeholder="Rua, numero, bairro" className={inputClass} />
                </div>
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="plano" className={labelClass}>Plano</Label>
                  <select
                    id="plano"
                    value={form.planId}
                    onChange={e => setForm(f => ({ ...f, planId: e.target.value }))}
                    className={FORM_SELECT_CLASS}
                  >
                    <option value="">Sem plano</option>
                    {planos.map(p => (
                      <option key={p.id} value={String(p.id)}>{p.name}</option>
                    ))}
                  </select>
                </div>
              <div className="flex flex-col gap-1.5">
                <Label htmlFor="plano" className={labelClass}>Plano</Label>
                <select
                  id="plano"
                  value={form.planId}
                  onChange={e => setForm(f => ({ ...f, planId: e.target.value }))}
                  className={FORM_SELECT_CLASS}
                >
                  <option value="">Sem plano</option>
                  {planos.map(plano => (
                    <option key={plano.id} value={plano.id}>
                      {plano.name}
                    </option>
                  ))}
                </select>
              </div>

              <div className="flex flex-col gap-1.5">
                <Label htmlFor="ativo" className={labelClass}>Status</Label>
                <select
                  id="ativo"
                  value={form.active ? "true" : "false"}
                  onChange={e => setForm(f => ({ ...f, active: e.target.value === "true" }))}
                  className={FORM_SELECT_CLASS}
                >
                  <option value="true">Ativo</option>
                  <option value="false">Inativo</option>
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
              className="flex-1 rounded-lg bg-[#DD050A] py-2 text-sm font-medium text-[#FFFFFF] transition-colors hover:bg-[#DD050A]/85 disabled:opacity-50"
              disabled={!form.name.trim() || !form.cpf.trim() || !form.birthDate || !emailValido}
            >
              {modo === "criar" ? "Cadastrar" : "Salvar"}
            </button>
          </SheetFooter>
        </SheetContent>
      </Sheet>
    </SidebarProvider>
  )
}
