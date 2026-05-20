"use client"

import { useState, useMemo } from "react"
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
import { Separator } from "@/components/ui/separator"
import {
  Users, UserCheck, UserX, Clock, TrendingUp, Plus,
  Phone, Pencil, Trash2, ChevronLeft, ChevronRight, Search, Link2,
} from "lucide-react"
import { PaymentLinkSheet } from "@/components/payments/payment-link-sheet"

interface Aluno {
  id: number
  nome: string
  email: string
  telefone: string
  plano: string
  vencimento: string
  status: "Ativo" | "Inativo" | "Pendente"
  matricula: string
}

interface FormState {
  nome: string
  email: string
  telefone: string
  plano: string
  vencimento: string
  status: Aluno["status"]
  matricula: string
}

const DADOS_INICIAIS: Aluno[] = [
  { id: 1,  nome: "Felipe Souza",    email: "felipe.souza@email.com",  telefone: "11999990001", plano: "Mensal",     vencimento: "2026-06-01", status: "Ativo",    matricula: "2024-03-01" },
  { id: 2,  nome: "Ana Lima",        email: "ana.lima@email.com",       telefone: "11999990002", plano: "Trimestral", vencimento: "2026-06-15", status: "Pendente", matricula: "2024-01-15" },
  { id: 3,  nome: "Carlos Mendes",   email: "carlos.mendes@email.com",  telefone: "11999990003", plano: "Mensal",     vencimento: "2026-05-28", status: "Inativo",  matricula: "2023-06-10" },
  { id: 4,  nome: "Bruna Costa",     email: "bruna.costa@email.com",    telefone: "11999990004", plano: "Anual",      vencimento: "2027-01-10", status: "Ativo",    matricula: "2025-01-10" },
  { id: 5,  nome: "Marcos Rocha",    email: "marcos.rocha@email.com",   telefone: "11999990005", plano: "Mensal",     vencimento: "2026-05-15", status: "Pendente", matricula: "2023-09-20" },
  { id: 6,  nome: "Juliana Ferreira",email: "juliana.f@email.com",      telefone: "11999990006", plano: "Semestral",  vencimento: "2026-08-01", status: "Ativo",    matricula: "2025-02-01" },
  { id: 7,  nome: "Rafael Oliveira", email: "rafael.o@email.com",       telefone: "11999990007", plano: "Mensal",     vencimento: "2026-06-05", status: "Ativo",    matricula: "2025-03-05" },
  { id: 8,  nome: "Camila Santos",   email: "camila.s@email.com",       telefone: "11999990008", plano: "Trimestral", vencimento: "2026-07-20", status: "Ativo",    matricula: "2024-04-20" },
  { id: 9,  nome: "Lucas Martins",   email: "lucas.m@email.com",        telefone: "11999990009", plano: "Anual",      vencimento: "2026-12-12", status: "Ativo",    matricula: "2023-12-12" },
  { id: 10, nome: "Isabela Gomes",   email: "isabela.g@email.com",      telefone: "11999990010", plano: "Mensal",     vencimento: "2026-06-08", status: "Ativo",    matricula: "2025-05-08" },
  { id: 11, nome: "Pedro Alves",     email: "pedro.alves@email.com",    telefone: "11999990011", plano: "Trimestral", vencimento: "2026-07-01", status: "Ativo",    matricula: "2024-07-01" },
  { id: 12, nome: "Fernanda Lima",   email: "fernanda.l@email.com",     telefone: "11999990012", plano: "Semestral",  vencimento: "2026-09-15", status: "Ativo",    matricula: "2024-03-15" },
]

const PLANOS = ["Mensal", "Trimestral", "Semestral", "Anual"] as const
const STATUS_OPTIONS: Aluno["status"][] = ["Ativo", "Pendente", "Inativo"]
const POR_PAGINA = 10

const FORM_VAZIO: FormState = {
  nome: "", email: "", telefone: "",
  plano: "Mensal", vencimento: "", status: "Ativo", matricula: "",
}

const STATUS_STYLE: Record<Aluno["status"], string> = {
  Ativo:    "border-[#00FF00]/30 bg-[#00FF00]/10 text-[#00FF00]",
  Inativo:  "border-[#FFFFFF]/20 bg-[#FFFFFF]/8 text-[#FFFFFF]/50",
  Pendente: "border-[#DD050A]/40 bg-[#DD050A]/10 text-[#DD050A]",
}

const SELECT_CLASS = "h-8 rounded-md border border-[#FFFFFF]/15 bg-[#000000] px-2.5 text-xs text-[#FFFFFF] focus:border-[#DD050A]/50 focus:outline-none"
const FORM_SELECT_CLASS = "h-9 w-full rounded-md border border-[#FFFFFF]/15 bg-[#000000] px-3 text-sm text-[#FFFFFF] focus:border-[#DD050A]/50 focus:outline-none"

function formatarData(iso: string): string {
  if (!iso) return ""
  const [ano, mes, dia] = iso.split("-")
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
  const [alunos, setAlunos]           = useState<Aluno[]>(DADOS_INICIAIS)
  const [busca, setBusca]             = useState("")
  const [filtroStatus, setFiltroStatus] = useState("Todos")
  const [filtroPlano, setFiltroPlano]   = useState("Todos")
  const [pagina, setPagina]           = useState(1)
  const [sheetAberto, setSheetAberto] = useState(false)
  const [modo, setModo]               = useState<"criar" | "editar">("criar")
  const [editando, setEditando]       = useState<Aluno | null>(null)
  const [deletandoId, setDeletandoId] = useState<number | null>(null)
  const [form, setForm]               = useState<FormState>(FORM_VAZIO)
  const [linkSheetAluno, setLinkSheetAluno] = useState<Aluno | null>(null)

  const filtrados = useMemo(() => alunos
    .filter(a => busca === "" || a.nome.toLowerCase().includes(busca.toLowerCase()) || a.email.toLowerCase().includes(busca.toLowerCase()))
    .filter(a => filtroStatus === "Todos" || a.status === filtroStatus)
    .filter(a => filtroPlano === "Todos" || a.plano === filtroPlano),
  [alunos, busca, filtroStatus, filtroPlano])

  const totalPaginas = Math.max(1, Math.ceil(filtrados.length / POR_PAGINA))
  const paginaAtual  = Math.min(pagina, totalPaginas)
  const visiveis     = filtrados.slice((paginaAtual - 1) * POR_PAGINA, paginaAtual * POR_PAGINA)

  const ativos    = alunos.filter(a => a.status === "Ativo").length
  const inativos  = alunos.filter(a => a.status === "Inativo").length
  const pendentes = alunos.filter(a => a.status === "Pendente").length

  function mudarFiltro(fn: () => void) { fn(); setPagina(1) }

  function abrirCriar() {
    setModo("criar"); setForm(FORM_VAZIO); setEditando(null); setSheetAberto(true)
  }

  function abrirEditar(a: Aluno) {
    setModo("editar")
    setForm({ nome: a.nome, email: a.email, telefone: a.telefone, plano: a.plano, vencimento: a.vencimento, status: a.status, matricula: a.matricula })
    setEditando(a)
    setSheetAberto(true)
  }

  function salvar() {
    if (!form.nome.trim() || !form.email.trim() || !form.telefone.trim() || !form.vencimento || !form.matricula) return
    if (modo === "criar") {
      setAlunos(prev => [...prev, { ...form, id: Date.now() }])
    } else if (editando) {
      setAlunos(prev => prev.map(a => a.id === editando.id ? { ...a, ...form } : a))
    }
    setSheetAberto(false)
  }

  function excluir(id: number) { setAlunos(prev => prev.filter(a => a.id !== id)); setDeletandoId(null) }

  const labelClass = "text-xs font-medium text-[#FFFFFF]/80"
  const inputClass = "border-[#FFFFFF]/15 bg-[#000000] text-[#FFFFFF] placeholder:text-[#FFFFFF]/30 focus-visible:border-[#DD050A]/50"

  return (
    <SidebarProvider>
      <AppSidebar />
      <div className="flex min-h-screen flex-1 flex-col bg-[#000000] text-[#FFFFFF]">

        {/* Header */}
        <header className="sticky top-0 z-10 flex items-center justify-between gap-3 border-b border-[#FFFFFF]/10 bg-[#020203]/90 px-4 py-3 backdrop-blur sm:px-6 sm:py-4">
          <div className="flex items-center gap-3">
            <SidebarTrigger className="text-[#FFFFFF]/70 hover:text-[#FFFFFF] md:hidden" />
            <Separator orientation="vertical" className="h-5 bg-[#FFFFFF]/20 md:hidden" />
            <div>
              <h1 className="text-sm font-semibold leading-none text-[#FFFFFF]">Alunos</h1>
              <p className="mt-0.5 text-xs text-[#FFFFFF]/60">Gerencie os alunos cadastrados</p>
            </div>
          </div>
          <button
            onClick={abrirCriar}
            className="flex items-center gap-2 rounded-lg border border-[#DD050A]/50 bg-[#DD050A]/15 px-3 py-2 text-xs font-medium text-[#FFFFFF] transition-colors hover:border-[#DD050A]/70 hover:bg-[#DD050A]/25"
          >
            <Plus className="h-3.5 w-3.5" />
            <span className="hidden sm:inline">Novo Aluno</span>
          </button>
        </header>

        <main className="flex-1 space-y-5 p-4 sm:space-y-6 sm:p-6">

          {/* KPI Cards */}
          <div className="grid grid-cols-2 gap-4 xl:grid-cols-4">
            {[
              { label: "Total de Alunos", valor: alunos.length, sub: "+2 este mês", subClass: "text-[#DD050A]", icon: Users, IconExtra: TrendingUp },
              { label: "Ativos",          valor: ativos,        sub: `${alunos.length > 0 ? Math.round((ativos / alunos.length) * 100) : 0}% do total`, subClass: "text-[#FFFFFF]/60", icon: UserCheck },
              { label: "Pendentes",       valor: pendentes,     sub: "Requer atenção", subClass: "text-[#DD050A]", icon: Clock },
              { label: "Inativos",        valor: inativos,      sub: "Sem plano ativo", subClass: "text-[#FFFFFF]/60", icon: UserX },
            ].map(({ label, valor, sub, subClass, icon: Icon, IconExtra }) => (
              <Card key={label} className="group border-[#FFFFFF]/12 bg-[#020203] transition-colors hover:border-[#DD050A]/50">
                <CardHeader className="flex flex-row items-center justify-between px-4 pb-2 pt-4 sm:px-5">
                  <CardTitle className="text-xs font-medium uppercase tracking-wider text-[#FFFFFF]/65">{label}</CardTitle>
                  <span className="flex h-7 w-7 items-center justify-center rounded-md bg-[#FFFFFF]/10 transition-colors group-hover:bg-[#DD050A]/12">
                    <Icon className="h-4 w-4 text-[#FFFFFF] transition-colors group-hover:text-[#DD050A]" />
                  </span>
                </CardHeader>
                <CardContent className="px-4 pb-4 sm:px-5">
                  <p className="text-xl font-bold text-[#FFFFFF] sm:text-2xl">{valor}</p>
                  <p className={`mt-1 flex items-center gap-1 text-xs ${subClass}`}>
                    {IconExtra && <IconExtra className="h-3 w-3" />}{sub}
                  </p>
                </CardContent>
              </Card>
            ))}
          </div>

          {/* Tabela */}
          <Card className="border-[#FFFFFF]/12 bg-[#020203]">
            <CardHeader className="px-4 pb-3 pt-5 sm:px-5">
              <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                <div>
                  <CardTitle className="text-base text-[#FFFFFF]">Lista de Alunos</CardTitle>
                  <CardDescription className="text-xs text-[#FFFFFF]/60">
                    {filtrados.length} aluno{filtrados.length !== 1 ? "s" : ""} encontrado{filtrados.length !== 1 ? "s" : ""}
                  </CardDescription>
                </div>

                {/* Filtros */}
                <div className="flex flex-wrap items-center gap-2">
                  <div className="relative">
                    <Search className="absolute left-2.5 top-1/2 h-3.5 w-3.5 -translate-y-1/2 text-[#FFFFFF]/40" />
                    <Input
                      type="search"
                      placeholder="Buscar aluno..."
                      value={busca}
                      onChange={e => mudarFiltro(() => setBusca(e.target.value))}
                      className="h-8 w-full border-[#FFFFFF]/15 bg-[#000000] pl-8 text-xs text-[#FFFFFF] placeholder:text-[#FFFFFF]/35 focus-visible:border-[#DD050A]/50 sm:w-44"
                    />
                  </div>
                  <select
                    value={filtroStatus}
                    onChange={e => mudarFiltro(() => setFiltroStatus(e.target.value))}
                    aria-label="Filtrar por status"
                    className={SELECT_CLASS}
                  >
                    <option value="Todos">Todos os status</option>
                    {STATUS_OPTIONS.map(s => <option key={s} value={s}>{s}</option>)}
                  </select>
                  <select
                    value={filtroPlano}
                    onChange={e => mudarFiltro(() => setFiltroPlano(e.target.value))}
                    aria-label="Filtrar por plano"
                    className={SELECT_CLASS}
                  >
                    <option value="Todos">Todos os planos</option>
                    {PLANOS.map(p => <option key={p} value={p}>{p}</option>)}
                  </select>
                </div>
              </div>
            </CardHeader>

            <CardContent className="px-4 pb-5 sm:px-5">
              <Table className="min-w-120 table-fixed sm:min-w-0">
                <TableCaption className="sr-only">
                  Lista de alunos cadastrados com informações de contato, plano e status
                </TableCaption>
                <TableHeader>
                  <TableRow className="border-[#FFFFFF]/10 hover:bg-transparent">
                    <TableHead scope="col" className="text-xs uppercase text-[#FFFFFF]/60">Aluno</TableHead>
                    <TableHead scope="col" className="hidden text-xs uppercase text-[#FFFFFF]/60 md:table-cell">Contato</TableHead>
                    <TableHead scope="col" className="hidden text-xs uppercase text-[#FFFFFF]/60 sm:table-cell">Plano</TableHead>
                    <TableHead scope="col" className="hidden text-xs uppercase text-[#FFFFFF]/60 lg:table-cell">Vencimento</TableHead>
                    <TableHead scope="col" className="text-xs uppercase text-[#FFFFFF]/60">Status</TableHead>
                    <TableHead scope="col" className="text-right text-xs uppercase text-[#FFFFFF]/60">Ações</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {visiveis.length === 0 ? (
                    <TableRow className="hover:bg-transparent">
                      <TableCell colSpan={6} className="py-12 text-center text-sm text-[#FFFFFF]/40">
                        Nenhum aluno encontrado com os filtros selecionados.
                      </TableCell>
                    </TableRow>
                  ) : visiveis.map(aluno => {
                    const telDigits = (aluno.telefone || "").replace(/\D/g, "")
                    return (
                    <TableRow
                      key={aluno.id}
                      className={`border-[#FFFFFF]/10 transition-colors ${deletandoId === aluno.id ? "bg-[#DD050A]/8" : "hover:bg-[#FFFFFF]/5"}`}
                    >
                      <TableCell>
                        <p className="font-medium text-[#FFFFFF]">{aluno.nome}</p>
                        <p className="mt-0.5 text-xs text-[#FFFFFF]/50 md:hidden">{aluno.email}</p>
                        <p className="mt-0.5 text-xs text-[#FFFFFF]/40">desde {formatarData(aluno.matricula)}</p>
                      </TableCell>
                      <TableCell className="hidden md:table-cell">
                        <p className="text-sm text-[#FFFFFF]/80">{aluno.email}</p>
                        <p className="mt-0.5 text-xs text-[#FFFFFF]/50">{aluno.telefone}</p>
                      </TableCell>
                      <TableCell className="hidden text-[#FFFFFF]/70 sm:table-cell">{aluno.plano}</TableCell>
                      <TableCell className="hidden text-[#FFFFFF]/70 lg:table-cell">{formatarData(aluno.vencimento)}</TableCell>
                      <TableCell>
                        <Badge variant="outline" className={STATUS_STYLE[aluno.status]}>{aluno.status}</Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        {deletandoId === aluno.id ? (
                          <div className="flex items-center justify-end gap-1.5">
                            <span className="hidden text-xs text-[#FFFFFF]/60 sm:inline">Excluir?</span>
                            <button
                              onClick={() => excluir(aluno.id)}
                              className="rounded border border-[#DD050A]/60 bg-[#DD050A]/20 px-2.5 py-1 text-xs font-medium text-[#DD050A] transition-colors hover:bg-[#DD050A]/35"
                            >
                              Sim
                            </button>
                            <button
                              onClick={() => setDeletandoId(null)}
                              className="rounded border border-[#FFFFFF]/20 bg-transparent px-2.5 py-1 text-xs font-medium text-[#FFFFFF]/60 transition-colors hover:bg-[#FFFFFF]/8"
                            >
                              Não
                            </button>
                          </div>
                        ) : (
                          <div className="flex items-center justify-end gap-1.5">
                            <a
                              href={`https://wa.me/55${telDigits}`}
                              target="_blank"
                              rel="noreferrer"
                              aria-label={`Contatar ${aluno.nome} via WhatsApp (abre nova aba)`}
                              className="inline-flex h-7 w-7 items-center justify-center rounded-md border border-[#FFFFFF]/15 text-[#FFFFFF]/60 transition-colors hover:border-[#DD050A]/40 hover:bg-[#DD050A]/10 hover:text-[#FFFFFF]"
                            >
                              <Phone className="h-3.5 w-3.5" />
                            </a>
                            <button
                              onClick={() => setLinkSheetAluno(aluno)}
                              aria-label={`Gerar link de pagamento para ${aluno.nome}`}
                              title="Gerar link de pagamento"
                              className="inline-flex h-7 w-7 items-center justify-center rounded-md border border-[#FFFFFF]/15 text-[#FFFFFF]/60 transition-colors hover:border-[#DD050A]/40 hover:bg-[#DD050A]/10 hover:text-[#FFFFFF]"
                            >
                              <Link2 className="h-3.5 w-3.5" />
                            </button>
                            <button
                              onClick={() => abrirEditar(aluno)}
                              aria-label={`Editar ${aluno.nome}`}
                              className="inline-flex h-7 w-7 items-center justify-center rounded-md border border-[#FFFFFF]/15 text-[#FFFFFF]/60 transition-colors hover:border-[#DD050A]/40 hover:bg-[#DD050A]/10 hover:text-[#FFFFFF]"
                            >
                              <Pencil className="h-3.5 w-3.5" />
                            </button>
                            <button
                              onClick={() => setDeletandoId(aluno.id)}
                              aria-label={`Excluir ${aluno.nome}`}
                              className="inline-flex h-7 w-7 items-center justify-center rounded-md border border-[#FFFFFF]/15 text-[#FFFFFF]/60 transition-colors hover:border-[#DD050A]/40 hover:bg-[#DD050A]/10 hover:text-[#DD050A]"
                            >
                              <Trash2 className="h-3.5 w-3.5" />
                            </button>
                          </div>
                        )}
                      </TableCell>
                    </TableRow>
                  )})}
                </TableBody>
              </Table>

              {/* Paginação */}
              <div className="mt-4 flex items-center justify-between border-t border-[#FFFFFF]/10 pt-4">
                <p className="text-xs text-[#FFFFFF]/50">
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
                      className="inline-flex h-7 w-7 items-center justify-center rounded-md border border-[#FFFFFF]/15 text-[#FFFFFF]/60 transition-colors hover:border-[#FFFFFF]/30 hover:text-[#FFFFFF] disabled:pointer-events-none disabled:opacity-30"
                    >
                      <ChevronLeft className="h-3.5 w-3.5" />
                    </button>
                    {numerasDePagina(paginaAtual, totalPaginas).map((num, idx) =>
                      num === "…" ? (
                        <span key={`ellipsis-${idx}`} className="px-1 text-xs text-[#FFFFFF]/40">…</span>
                      ) : (
                        <button
                          key={num}
                          onClick={() => setPagina(num)}
                          aria-label={`Página ${num}`}
                          aria-current={num === paginaAtual ? "page" : undefined}
                          className={`inline-flex h-7 w-7 items-center justify-center rounded-md text-xs font-medium transition-colors ${
                            num === paginaAtual
                              ? "bg-[#DD050A] text-[#FFFFFF]"
                              : "border border-[#FFFFFF]/15 text-[#FFFFFF]/60 hover:border-[#FFFFFF]/30 hover:text-[#FFFFFF]"
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
                      className="inline-flex h-7 w-7 items-center justify-center rounded-md border border-[#FFFFFF]/15 text-[#FFFFFF]/60 transition-colors hover:border-[#FFFFFF]/30 hover:text-[#FFFFFF] disabled:pointer-events-none disabled:opacity-30"
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
          className="flex w-full flex-col border-l border-[#FFFFFF]/10 bg-[#020203] text-[#FFFFFF] sm:max-w-md"
        >
          <SheetHeader className="border-b border-[#FFFFFF]/10 px-6 pb-4 pt-6">
            <SheetTitle className="text-base text-[#FFFFFF]">
              {modo === "criar" ? "Novo Aluno" : "Editar Aluno"}
            </SheetTitle>
            <SheetDescription className="text-xs text-[#FFFFFF]/55">
              {modo === "criar"
                ? "Preencha os dados para cadastrar um novo aluno."
                : "Atualize as informações do aluno."}
            </SheetDescription>
          </SheetHeader>

          <div className="flex-1 overflow-y-auto px-6 py-5">
            <div className="flex flex-col gap-5">

              <div className="flex flex-col gap-1.5">
                <Label htmlFor="nome" className={labelClass}>Nome completo</Label>
                <Input id="nome" value={form.nome} onChange={e => setForm(f => ({ ...f, nome: e.target.value }))}
                  placeholder="Ex: João Silva" className={inputClass} />
              </div>

              <div className="flex flex-col gap-1.5">
                <Label htmlFor="email" className={labelClass}>E-mail</Label>
                <Input id="email" type="email" value={form.email} onChange={e => setForm(f => ({ ...f, email: e.target.value }))}
                  placeholder="joao@email.com" className={inputClass} />
              </div>

              <div className="flex flex-col gap-1.5">
                <Label htmlFor="telefone" className={labelClass}>Telefone / WhatsApp</Label>
                <Input id="telefone" type="tel" value={form.telefone} onChange={e => setForm(f => ({ ...f, telefone: e.target.value }))}
                  placeholder="11999990000" className={inputClass} />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="plano" className={labelClass}>Plano</Label>
                  <select id="plano" value={form.plano} onChange={e => setForm(f => ({ ...f, plano: e.target.value }))}
                    className={FORM_SELECT_CLASS}>
                    {PLANOS.map(p => <option key={p} value={p}>{p}</option>)}
                  </select>
                </div>
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="status" className={labelClass}>Status</Label>
                  <select id="status" value={form.status} onChange={e => setForm(f => ({ ...f, status: e.target.value as Aluno["status"] }))}
                    className={FORM_SELECT_CLASS}>
                    {STATUS_OPTIONS.map(s => <option key={s} value={s}>{s}</option>)}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="matricula" className={labelClass}>Data de matrícula</Label>
                  <Input id="matricula" type="date" value={form.matricula} onChange={e => setForm(f => ({ ...f, matricula: e.target.value }))}
                    className={`${inputClass} [color-scheme:dark]`} />
                </div>
                <div className="flex flex-col gap-1.5">
                  <Label htmlFor="vencimento" className={labelClass}>Vencimento</Label>
                  <Input id="vencimento" type="date" value={form.vencimento} onChange={e => setForm(f => ({ ...f, vencimento: e.target.value }))}
                    className={`${inputClass} [color-scheme:dark]`} />
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
              className="flex-1 rounded-lg bg-[#DD050A] py-2 text-sm font-medium text-[#FFFFFF] transition-colors hover:bg-[#DD050A]/85 disabled:opacity-50"
              disabled={!form.nome.trim() || !form.email.trim() || !form.telefone.trim() || !form.vencimento || !form.matricula}
            >
              {modo === "criar" ? "Cadastrar" : "Salvar"}
            </button>
          </SheetFooter>
        </SheetContent>
      </Sheet>

      <PaymentLinkSheet
        open={linkSheetAluno !== null}
        onOpenChange={(open) => { if (!open) setLinkSheetAluno(null) }}
        studentName={linkSheetAluno?.nome ?? ""}
      />
    </SidebarProvider>
  )
}
