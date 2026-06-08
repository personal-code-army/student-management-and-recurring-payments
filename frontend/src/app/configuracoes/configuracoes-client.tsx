"use client"

import { useEffect, useState } from "react"
import { api } from "@/lib/api"
import { Input } from "@/components/ui/input"
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetFooter,
} from "@/components/ui/sheet"
import { Pencil, Trash2, Plus, Eye, EyeOff, User, Lock, Users } from "lucide-react"

interface UserData {
  id: number
  name: string
  email: string
  cellphoneNumber: string
  companyId: number
  role: string
}

type ApiEnvelope<T> = { data: T }

const labelClass = "block text-xs font-medium text-zinc-600 dark:text-[#FFFFFF]/80 mb-1"
const inputClass =
  "h-9 w-full border-zinc-300 bg-white text-zinc-900 placeholder:text-zinc-400 focus-visible:border-[#DD050A]/50 dark:border-[#FFFFFF]/15 dark:bg-[#000000] dark:text-[#FFFFFF] dark:placeholder:text-[#FFFFFF]/35"
const cardClass =
  "rounded-xl border border-zinc-200 bg-white p-6 dark:border-[#FFFFFF]/10 dark:bg-[#020203]"
const sectionTitleClass = "text-sm font-semibold text-zinc-900 dark:text-[#FFFFFF]"
const btnPrimary =
  "rounded-md border border-[#DD050A]/50 bg-[#DD050A]/15 px-3 py-1.5 text-xs font-medium text-[#DD050A] transition-colors hover:border-[#DD050A]/70 hover:bg-[#DD050A]/25 disabled:opacity-50 dark:text-[#FFFFFF]"
const btnSecondary =
  "rounded-md border border-zinc-300 px-4 py-2 text-xs font-medium text-zinc-600 transition-colors hover:bg-zinc-50 dark:border-[#FFFFFF]/20 dark:text-[#FFFFFF]/60 dark:hover:bg-[#FFFFFF]/5"

const ROLE_LABEL: Record<string, string> = { ADMIN: "Admin", USER: "Usuário" }

export function ConfiguracoesClient() {
  const [me, setMe] = useState<UserData | null>(null)
  const [allUsers, setAllUsers] = useState<UserData[]>([])
  const [loading, setLoading] = useState(true)

  // Profile sheet
  const [profileSheet, setProfileSheet] = useState(false)
  const [profileForm, setProfileForm] = useState({ name: "", email: "", cellphoneNumber: "" })
  const [savingProfile, setSavingProfile] = useState(false)

  // Password form
  const [pwForm, setPwForm] = useState({ currentPassword: "", newPassword: "", confirm: "" })
  const [savingPw, setSavingPw] = useState(false)
  const [showPw, setShowPw] = useState({ current: false, next: false, confirm: false })

  // User CRUD sheet
  const [userSheet, setUserSheet] = useState(false)
  const [userSheetMode, setUserSheetMode] = useState<"create" | "edit">("create")
  const [selectedUser, setSelectedUser] = useState<UserData | null>(null)
  const [userForm, setUserForm] = useState({ name: "", email: "", cellphoneNumber: "", password: "", cpf: "" })
  const [savingUser, setSavingUser] = useState(false)

  // Delete modal
  const [deleteModal, setDeleteModal] = useState(false)
  const [deletingUser, setDeletingUser] = useState(false)

  // Toast
  const [toast, setToast] = useState<{ msg: string; ok: boolean } | null>(null)

  const showToast = (msg: string, ok = true) => {
    setToast({ msg, ok })
    setTimeout(() => setToast(null), 3500)
  }

  useEffect(() => {
    Promise.all([
      api.get<ApiEnvelope<UserData>>("/api/auth/me"),
      api.get<ApiEnvelope<UserData[]>>("/api/users"),
    ])
      .then(([meRes, usersRes]) => {
        setMe(meRes.data.data)
        setAllUsers(usersRes.data.data ?? [])
      })
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  // ── Profile ──────────────────────────────────────────────────────────────
  const openProfileSheet = () => {
    if (!me) return
    setProfileForm({ name: me.name, email: me.email, cellphoneNumber: me.cellphoneNumber ?? "" })
    setProfileSheet(true)
  }

  const saveProfile = async () => {
    if (!me) return
    setSavingProfile(true)
    try {
      const res = await api.put<ApiEnvelope<UserData>>(`/api/users/${me.id}`, {
        name: profileForm.name,
        email: profileForm.email,
        cellphoneNumber: profileForm.cellphoneNumber,
        companyId: me.companyId,
      })
      setMe(res.data.data)
      setProfileSheet(false)
      showToast("Perfil atualizado com sucesso")
    } catch {
      showToast("Erro ao atualizar perfil", false)
    } finally {
      setSavingProfile(false)
    }
  }

  // ── Password ─────────────────────────────────────────────────────────────
  const savePassword = async () => {
    if (pwForm.newPassword !== pwForm.confirm) {
      showToast("As senhas não coincidem", false)
      return
    }
    setSavingPw(true)
    try {
      await api.patch("/api/users/change-password", {
        currentPassword: pwForm.currentPassword,
        newPassword: pwForm.newPassword,
      })
      setPwForm({ currentPassword: "", newPassword: "", confirm: "" })
      showToast("Senha alterada com sucesso")
    } catch {
      showToast("Erro ao alterar senha. Verifique a senha atual", false)
    } finally {
      setSavingPw(false)
    }
  }

  // ── Users CRUD ────────────────────────────────────────────────────────────
  const openCreateUser = () => {
    setUserSheetMode("create")
    setSelectedUser(null)
    setUserForm({ name: "", email: "", cellphoneNumber: "", password: "", cpf: "" })
    setUserSheet(true)
  }

  const openEditUser = (user: UserData) => {
    setUserSheetMode("edit")
    setSelectedUser(user)
    setUserForm({ name: user.name, email: user.email, cellphoneNumber: user.cellphoneNumber ?? "", password: "", cpf: "" })
    setUserSheet(true)
  }

  const saveUser = async () => {
    setSavingUser(true)
    try {
      if (userSheetMode === "create") {
        const res = await api.post<ApiEnvelope<UserData>>("/api/auth/register", {
          name: userForm.name,
          email: userForm.email,
          password: userForm.password,
          cpf: userForm.cpf,
          cellphoneNumber: userForm.cellphoneNumber,
          companyId: me?.companyId,
        })
        setAllUsers(prev => [...prev, res.data.data])
        showToast("Usuário criado com sucesso")
      } else {
        const res = await api.put<ApiEnvelope<UserData>>(`/api/users/${selectedUser!.id}`, {
          name: userForm.name,
          email: userForm.email,
          cellphoneNumber: userForm.cellphoneNumber,
          companyId: selectedUser!.companyId,
        })
        setAllUsers(prev => prev.map(u => u.id === selectedUser!.id ? res.data.data : u))
        showToast("Usuário atualizado com sucesso")
      }
      setUserSheet(false)
    } catch {
      showToast("Erro ao salvar usuário", false)
    } finally {
      setSavingUser(false)
    }
  }

  const openDeleteUser = (user: UserData) => {
    setSelectedUser(user)
    setDeleteModal(true)
  }

  const deleteUser = async () => {
    if (!selectedUser) return
    setDeletingUser(true)
    try {
      await api.delete(`/api/users/${selectedUser.id}`)
      setAllUsers(prev => prev.filter(u => u.id !== selectedUser.id))
      setDeleteModal(false)
      showToast("Usuário excluído")
    } catch {
      showToast("Erro ao excluir usuário", false)
    } finally {
      setDeletingUser(false)
    }
  }

  // ── Render ────────────────────────────────────────────────────────────────
  return (
    <div className="min-h-screen bg-zinc-50 text-zinc-900 dark:bg-[#000000] dark:text-[#FFFFFF]">
      {/* Header */}
      <header className="sticky top-0 z-20 flex h-14 items-center border-b border-zinc-200 bg-white/90 px-6 backdrop-blur-sm dark:border-[#FFFFFF]/10 dark:bg-[#020203]/90">
        <div>
          <h1 className="text-base font-semibold text-zinc-900 dark:text-[#FFFFFF]">Configurações</h1>
          <p className="text-xs text-zinc-500 dark:text-[#FFFFFF]/45">Gerencie o seu perfil e usuários</p>
        </div>
      </header>

      <main className="mx-auto max-w-3xl space-y-6 p-6">

        {/* ── Minha Conta ────────────────────────────────────────────────── */}
        <section className={cardClass}>
          <div className="mb-4 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <User className="h-4 w-4 text-[#DD050A]" />
              <h2 className={sectionTitleClass}>Minha Conta</h2>
            </div>
            {!loading && me && (
              <button onClick={openProfileSheet} className={`${btnPrimary} flex items-center gap-1.5`}>
                <Pencil className="h-3 w-3" />
                Editar Perfil
              </button>
            )}
          </div>

          {loading ? (
            <div className="space-y-3">
              {[1, 2, 3].map(i => (
                <div key={i} className="h-5 w-2/3 animate-pulse rounded bg-zinc-200 dark:bg-[#FFFFFF]/10" />
              ))}
            </div>
          ) : me ? (
            <div className="flex items-start gap-4">
              <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-full border border-[#DD050A]/40 bg-[#DD050A]/15 text-xl font-bold text-[#DD050A]">
                {me.name?.charAt(0).toUpperCase()}
              </div>
              <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                <div>
                  <p className="text-xs text-zinc-500 dark:text-[#FFFFFF]/45">Nome</p>
                  <p className="text-sm font-medium">{me.name}</p>
                </div>
                <div>
                  <p className="text-xs text-zinc-500 dark:text-[#FFFFFF]/45">E-mail</p>
                  <p className="text-sm font-medium">{me.email}</p>
                </div>
                <div>
                  <p className="text-xs text-zinc-500 dark:text-[#FFFFFF]/45">Telefone</p>
                  <p className="text-sm font-medium">{me.cellphoneNumber || "—"}</p>
                </div>
                <div>
                  <p className="text-xs text-zinc-500 dark:text-[#FFFFFF]/45">Papel</p>
                  <p className="text-sm font-medium">{ROLE_LABEL[me.role] ?? me.role}</p>
                </div>
              </div>
            </div>
          ) : (
            <p className="text-sm text-zinc-500 dark:text-[#FFFFFF]/40">Backend não conectado</p>
          )}
        </section>

        {/* ── Alterar Senha ──────────────────────────────────────────────── */}
        <section className={cardClass}>
          <div className="mb-4 flex items-center gap-2">
            <Lock className="h-4 w-4 text-[#DD050A]" />
            <h2 className={sectionTitleClass}>Alterar Senha</h2>
          </div>
          <div className="space-y-4">
            <div>
              <label className={labelClass}>Senha Atual</label>
              <div className="relative">
                <Input
                  type={showPw.current ? "text" : "password"}
                  placeholder="••••••••"
                  value={pwForm.currentPassword}
                  onChange={e => setPwForm(f => ({ ...f, currentPassword: e.target.value }))}
                  className={inputClass}
                />
                <button
                  type="button"
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-zinc-400 dark:text-[#FFFFFF]/40"
                  onClick={() => setShowPw(s => ({ ...s, current: !s.current }))}
                >
                  {showPw.current ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
            </div>
            <div>
              <label className={labelClass}>Nova Senha</label>
              <div className="relative">
                <Input
                  type={showPw.next ? "text" : "password"}
                  placeholder="••••••••"
                  value={pwForm.newPassword}
                  onChange={e => setPwForm(f => ({ ...f, newPassword: e.target.value }))}
                  className={inputClass}
                />
                <button
                  type="button"
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-zinc-400 dark:text-[#FFFFFF]/40"
                  onClick={() => setShowPw(s => ({ ...s, next: !s.next }))}
                >
                  {showPw.next ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
            </div>
            <div>
              <label className={labelClass}>Confirmar Nova Senha</label>
              <div className="relative">
                <Input
                  type={showPw.confirm ? "text" : "password"}
                  placeholder="••••••••"
                  value={pwForm.confirm}
                  onChange={e => setPwForm(f => ({ ...f, confirm: e.target.value }))}
                  className={inputClass}
                />
                <button
                  type="button"
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-zinc-400 dark:text-[#FFFFFF]/40"
                  onClick={() => setShowPw(s => ({ ...s, confirm: !s.confirm }))}
                >
                  {showPw.confirm ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
            </div>
            <div className="flex justify-end">
              <button
                onClick={savePassword}
                disabled={savingPw || !pwForm.currentPassword || !pwForm.newPassword || !pwForm.confirm}
                className={`${btnPrimary} px-4 py-2`}
              >
                {savingPw ? "Salvando..." : "Alterar Senha"}
              </button>
            </div>
          </div>
        </section>

        {/* ── Usuários ───────────────────────────────────────────────────── */}
        <section className={cardClass}>
          <div className="mb-4 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Users className="h-4 w-4 text-[#DD050A]" />
              <h2 className={sectionTitleClass}>Usuários</h2>
            </div>
            <button onClick={openCreateUser} className={`${btnPrimary} flex items-center gap-1.5`}>
              <Plus className="h-3 w-3" />
              Novo Usuário
            </button>
          </div>

          {loading ? (
            <div className="space-y-3">
              {[1, 2, 3].map(i => (
                <div key={i} className="h-10 animate-pulse rounded bg-zinc-200 dark:bg-[#FFFFFF]/10" />
              ))}
            </div>
          ) : allUsers.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-10 text-zinc-400 dark:text-[#FFFFFF]/30">
              <Users className="mb-2 h-8 w-8" />
              <p className="text-sm">Nenhum usuário cadastrado</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-zinc-200 dark:border-[#FFFFFF]/10">
                    {["Nome", "E-mail", "Telefone", "Papel", ""].map(h => (
                      <th key={h} className="pb-2 pr-4 text-left text-xs font-medium text-zinc-500 dark:text-[#FFFFFF]/45">
                        {h}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {allUsers.map(u => (
                    <tr key={u.id} className="border-b border-zinc-100 last:border-0 dark:border-[#FFFFFF]/5">
                      <td className="py-3 pr-4 font-medium text-zinc-900 dark:text-[#FFFFFF]">{u.name}</td>
                      <td className="py-3 pr-4 text-zinc-600 dark:text-[#FFFFFF]/60">{u.email}</td>
                      <td className="py-3 pr-4 text-zinc-600 dark:text-[#FFFFFF]/60">{u.cellphoneNumber || "—"}</td>
                      <td className="py-3 pr-4">
                        <span className="rounded-full border border-zinc-200 bg-zinc-100 px-2 py-0.5 text-xs text-zinc-600 dark:border-[#FFFFFF]/15 dark:bg-[#FFFFFF]/10 dark:text-[#FFFFFF]/70">
                          {ROLE_LABEL[u.role] ?? u.role}
                        </span>
                      </td>
                      <td className="py-3">
                        <div className="flex items-center justify-end gap-2">
                          <button
                            onClick={() => openEditUser(u)}
                            className="rounded p-1.5 text-zinc-400 transition-colors hover:bg-zinc-100 hover:text-zinc-700 dark:text-[#FFFFFF]/40 dark:hover:bg-[#FFFFFF]/10 dark:hover:text-[#FFFFFF]"
                          >
                            <Pencil className="h-3.5 w-3.5" />
                          </button>
                          <button
                            onClick={() => openDeleteUser(u)}
                            className="rounded p-1.5 text-zinc-400 transition-colors hover:bg-red-50 hover:text-[#DD050A] dark:text-[#FFFFFF]/40 dark:hover:bg-[#DD050A]/10 dark:hover:text-[#DD050A]"
                          >
                            <Trash2 className="h-3.5 w-3.5" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </main>

      {/* ── Profile Edit Sheet ────────────────────────────────────────────── */}
      <Sheet open={profileSheet} onOpenChange={setProfileSheet}>
        <SheetContent className="border-zinc-200 bg-white dark:border-[#FFFFFF]/10 dark:bg-[#020203]">
          <SheetHeader>
            <SheetTitle className="text-zinc-900 dark:text-[#FFFFFF]">Editar Perfil</SheetTitle>
          </SheetHeader>
          <div className="mt-6 space-y-4">
            <div>
              <label className={labelClass}>Nome</label>
              <Input
                value={profileForm.name}
                onChange={e => setProfileForm(f => ({ ...f, name: e.target.value }))}
                className={inputClass}
                placeholder="Nome Completo"
              />
            </div>
            <div>
              <label className={labelClass}>E-mail</label>
              <Input
                type="email"
                value={profileForm.email}
                onChange={e => setProfileForm(f => ({ ...f, email: e.target.value }))}
                className={inputClass}
                placeholder="nome@email.com"
              />
            </div>
            <div>
              <label className={labelClass}>Telefone</label>
              <Input
                value={profileForm.cellphoneNumber}
                onChange={e => setProfileForm(f => ({ ...f, cellphoneNumber: e.target.value }))}
                className={inputClass}
                placeholder="(11) 99999-9999"
              />
            </div>
          </div>
          <SheetFooter className="mt-6 flex gap-2">
            <button onClick={() => setProfileSheet(false)} className={`flex-1 ${btnSecondary}`}>
              Cancelar
            </button>
            <button onClick={saveProfile} disabled={savingProfile} className={`flex-1 ${btnPrimary} py-2`}>
              {savingProfile ? "Salvando..." : "Salvar"}
            </button>
          </SheetFooter>
        </SheetContent>
      </Sheet>

      {/* ── User Create / Edit Sheet ──────────────────────────────────────── */}
      <Sheet open={userSheet} onOpenChange={setUserSheet}>
        <SheetContent className="border-zinc-200 bg-white dark:border-[#FFFFFF]/10 dark:bg-[#020203]">
          <SheetHeader>
            <SheetTitle className="text-zinc-900 dark:text-[#FFFFFF]">
              {userSheetMode === "create" ? "Novo Usuário" : "Editar Usuário"}
            </SheetTitle>
          </SheetHeader>
          <div className="mt-6 space-y-4">
            <div>
              <label className={labelClass}>Nome</label>
              <Input
                value={userForm.name}
                onChange={e => setUserForm(f => ({ ...f, name: e.target.value }))}
                className={inputClass}
                placeholder="Nome Completo"
              />
            </div>
            <div>
              <label className={labelClass}>E-mail</label>
              <Input
                type="email"
                value={userForm.email}
                onChange={e => setUserForm(f => ({ ...f, email: e.target.value }))}
                className={inputClass}
                placeholder="nome@email.com"
              />
            </div>
            <div>
              <label className={labelClass}>Telefone</label>
              <Input
                value={userForm.cellphoneNumber}
                onChange={e => setUserForm(f => ({ ...f, cellphoneNumber: e.target.value }))}
                className={inputClass}
                placeholder="(11) 99999-9999"
              />
            </div>
            {userSheetMode === "create" && (
              <>
                <div>
                  <label className={labelClass}>CPF</label>
                  <Input
                    value={userForm.cpf}
                    onChange={e => setUserForm(f => ({ ...f, cpf: e.target.value }))}
                    className={inputClass}
                    placeholder="000.000.000-00"
                  />
                </div>
                <div>
                  <label className={labelClass}>Senha</label>
                  <Input
                    type="password"
                    value={userForm.password}
                    onChange={e => setUserForm(f => ({ ...f, password: e.target.value }))}
                    className={inputClass}
                    placeholder="••••••••"
                  />
                </div>
              </>
            )}
          </div>
          <SheetFooter className="mt-6 flex gap-2">
            <button onClick={() => setUserSheet(false)} className={`flex-1 ${btnSecondary}`}>
              Cancelar
            </button>
            <button onClick={saveUser} disabled={savingUser} className={`flex-1 ${btnPrimary} py-2`}>
              {savingUser ? "Salvando..." : "Salvar"}
            </button>
          </SheetFooter>
        </SheetContent>
      </Sheet>

      {/* ── Delete Confirmation Modal ─────────────────────────────────────── */}
      {deleteModal && selectedUser && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
          <div className="mx-4 w-full max-w-sm rounded-xl border border-zinc-200 bg-white p-6 dark:border-[#FFFFFF]/10 dark:bg-[#020203]">
            <div className="mb-4 flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-[#DD050A]/10">
                <Trash2 className="h-5 w-5 text-[#DD050A]" />
              </div>
              <div>
                <h3 className="text-sm font-semibold text-zinc-900 dark:text-[#FFFFFF]">Excluir Usuário</h3>
                <p className="text-xs text-zinc-500 dark:text-[#FFFFFF]/45">Esta ação não pode ser desfeita</p>
              </div>
            </div>
            <p className="mb-6 text-sm text-zinc-600 dark:text-[#FFFFFF]/60">
              Tem certeza que deseja excluir{" "}
              <span className="font-medium text-zinc-900 dark:text-[#FFFFFF]">{selectedUser.name}</span>?
            </p>
            <div className="flex gap-3">
              <button onClick={() => setDeleteModal(false)} className={`flex-1 ${btnSecondary}`}>
                Cancelar
              </button>
              <button onClick={deleteUser} disabled={deletingUser} className={`flex-1 ${btnPrimary} py-2`}>
                {deletingUser ? "Excluindo..." : "Excluir"}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ── Toast ─────────────────────────────────────────────────────────── */}
      {toast && (
        <div
          className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 rounded-lg border px-4 py-3 text-sm shadow-lg ${
            toast.ok
              ? "border-green-500/30 bg-green-500/10 text-green-700 dark:text-green-400"
              : "border-[#DD050A]/30 bg-[#DD050A]/10 text-[#DD050A]"
          }`}
        >
          {toast.msg}
        </div>
      )}
    </div>
  )
}
