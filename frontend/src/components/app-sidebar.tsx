"use client"

import { useEffect, useState } from "react"
import { LayoutDashboard, DollarSign, Users, Box, Settings, LogOut, Moon } from "lucide-react"
import { usePathname, useRouter } from "next/navigation"
import { useTheme } from "next-themes"
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar"
import { api } from "@/lib/api"

const navItems = [
  { title: "Dashboard", url: "/dashboard", icon: LayoutDashboard },
  { title: "Recebimentos", url: "/recebimentos", icon: DollarSign },
  { title: "Alunos", url: "/alunos", icon: Users },
  { title: "Planos", url: "/planos", icon: Box },
]

interface UsuarioLogado {
  name: string
  email: string
}

type ApiResponse<T> = { data: T }


export function AppSidebar() {
  const router = useRouter()
  const pathname = usePathname()
  const { theme, setTheme } = useTheme()
  const [usuario, setUsuario] = useState<UsuarioLogado | null>(null)

  useEffect(() => {
    api.get<ApiResponse<UsuarioLogado>>("/api/auth/me")
      .then(res => setUsuario(res.data?.data ?? null))
      .catch(err => console.error("Erro ao carregar usuario", err))
  }, [])

  const handleLogout = () => {
    localStorage.removeItem("auth_token")
    router.replace("/login")
  }

  const isActive = (url: string) => pathname === url || pathname?.startsWith(`${url}/`) || false
  const inicial = usuario?.name?.trim().charAt(0).toUpperCase() ?? "?"

  return (
    <Sidebar className="[--sidebar:#FFFFFF] [--sidebar-foreground:#18181B] [--sidebar-border:#E4E4E7] border-r border-zinc-200 text-zinc-900 dark:[--sidebar:#020203] dark:[--sidebar-foreground:#FFFFFF] dark:[--sidebar-border:#2A2A2A] dark:border-[#2A2A2A] dark:text-[#FFFFFF]">
      <SidebarHeader className="relative overflow-hidden border-b border-zinc-200 px-4 py-5 dark:border-[#FFFFFF]/10">
        <div className="pointer-events-none absolute -right-10 -top-10 h-24 w-24 rounded-full bg-[#DD050A]/20 blur-2xl" />
        <div className="relative flex items-center gap-3">
          <div className="flex h-10 w-10 shrink-0 items-center justify-center overflow-hidden rounded-full bg-black shadow-[0_0_14px_rgba(221,5,10,0.35)]">
            <img src="/logo.png" alt="Gelo Team" className="h-9 w-9 object-contain" />
          </div>
          <div>
            <p className="text-sm font-black uppercase italic leading-none tracking-wide text-zinc-900 dark:text-[#FFFFFF]">Gelo Team</p>
            <p className="mt-1 text-[10px] tracking-[0.28em] text-[#DD050A]/90">GESTAO DE ALUNOS</p>
          </div>
        </div>
      </SidebarHeader>

      <SidebarContent className="px-2 py-3">
        <SidebarGroup>
          <SidebarGroupLabel className="mb-2 px-3 text-[10px] tracking-[0.25em] text-zinc-500 dark:text-[#FFFFFF]/45">NAVEGACAO</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {navItems.map((item) => {
                const ativo = isActive(item.url)
                return (
                  <SidebarMenuItem key={item.title}>
                    <SidebarMenuButton
                      asChild
                      className={`group relative overflow-hidden rounded-md transition-all duration-200 ${
                        ativo
                          ? "bg-[#DD050A]/14 text-zinc-900 dark:text-[#FFFFFF]"
                          : "text-zinc-600 hover:bg-zinc-100 hover:text-zinc-900 dark:text-[#FFFFFF]/70 dark:hover:bg-[#FFFFFF]/5 dark:hover:text-[#FFFFFF]"
                      }`}
                    >
                      <a href={item.url} className="flex items-center gap-3 px-3 py-2.5" aria-current={ativo ? "page" : undefined}>
                        <span
                          className={`absolute inset-y-1.5 left-0 w-1 rounded-r-full bg-[#DD050A] transition-opacity duration-200 ${
                            ativo ? "opacity-100" : "opacity-0"
                          }`}
                        />
                        <item.icon className={`h-4 w-4 transition-colors ${ativo ? "text-[#DD050A]" : "group-hover:text-zinc-900 dark:group-hover:text-[#FFFFFF]"}`} />
                        <span className={`text-sm ${ativo ? "font-semibold" : ""}`}>{item.title}</span>
                      </a>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                )
              })}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter className="border-t border-zinc-200 px-2 py-3 dark:border-[#FFFFFF]/10">
        {/* Card de usuario */}
        <div className="mb-2 flex items-center gap-3 rounded-lg border border-zinc-200 bg-zinc-50 px-3 py-2.5 dark:border-[#FFFFFF]/10 dark:bg-[#FFFFFF]/[0.03]">
          <div className="relative">
            <div className="flex h-9 w-9 items-center justify-center rounded-full border border-[#DD050A]/40 bg-[#DD050A]/15 text-sm font-bold text-[#DD050A]">
              {inicial}
            </div>
            <span className="absolute -bottom-0.5 -right-0.5 h-2.5 w-2.5 rounded-full border-2 border-white bg-emerald-500 dark:border-[#020203]" />
          </div>
          <div className="min-w-0 flex-1">
            <p className="truncate text-sm font-medium text-zinc-900 dark:text-[#FFFFFF]">{usuario?.name ?? "Carregando..."}</p>
            <p className="truncate text-xs text-zinc-500 dark:text-[#FFFFFF]/50">{usuario?.email ?? "—"}</p>
          </div>
        </div>

        <SidebarMenu>
          <SidebarMenuItem>
            <div
              role="button"
              tabIndex={0}
              onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
              onKeyDown={e => e.key === "Enter" && setTheme(theme === "dark" ? "light" : "dark")}
              className="flex w-full cursor-pointer items-center justify-between rounded-md px-3 py-2.5 text-zinc-600 transition-all hover:bg-zinc-100 hover:text-zinc-900 dark:text-[#FFFFFF]/70 dark:hover:bg-[#FFFFFF]/5 dark:hover:text-[#FFFFFF]"
            >
              <div className="flex items-center gap-3">
                <Moon className="h-4 w-4" />
                <span className="text-sm">Modo Escuro</span>
              </div>
              {/* Toggle switch */}
              <div
                role="switch"
                aria-checked={theme === "dark"}
                className={`relative h-5 w-9 shrink-0 rounded-full transition-colors ${
                  theme === "dark" ? "bg-[#DD050A]" : "bg-zinc-300 dark:bg-[#FFFFFF]/25"
                }`}
              >
                <span
                  className={`absolute top-0.5 h-4 w-4 rounded-full bg-white shadow-sm transition-transform ${
                    theme === "dark" ? "translate-x-[18px]" : "translate-x-0.5"
                  }`}
                />
              </div>
            </div>
          </SidebarMenuItem>
          <SidebarMenuItem>
            <SidebarMenuButton asChild className="rounded-md text-zinc-600 transition-all hover:bg-zinc-100 hover:text-zinc-900 dark:text-[#FFFFFF]/70 dark:hover:bg-[#FFFFFF]/5 dark:hover:text-[#FFFFFF]">
              <a href="/configuracoes" className="flex items-center gap-3 px-3 py-2.5">
                <Settings className="h-4 w-4" />
                <span className="text-sm">Configurações</span>
              </a>
            </SidebarMenuButton>
          </SidebarMenuItem>
          <SidebarMenuItem>
            <SidebarMenuButton
              type="button"
              onClick={handleLogout}
              className="w-full rounded-md text-zinc-600 transition-all hover:bg-[#DD050A]/12 hover:text-[#DD050A] cursor-pointer dark:text-[#FFFFFF]/70"
            >
              <div className="flex items-center gap-3 px-3 py-2.5">
                <LogOut className="h-4 w-4" />
                <span className="text-sm">Sair</span>
              </div>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarFooter>
    </Sidebar>
  )
}
