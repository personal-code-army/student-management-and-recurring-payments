"use client"

import { LayoutDashboard, DollarSign, Users, Box, Settings, LogOut } from "lucide-react"
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


const navItems = [
  { title: "Dashboard", url: "/dashboard", icon: LayoutDashboard },
  { title: "Recebimentos", url: "/dashboard/recebimentos", icon: DollarSign },
  { title: "Alunos", url: "/dashboard/alunos", icon: Users },
  { title: "Planos", url: "/dashboard/planos", icon: Box },
]

const bottomItems = [
  { title: "Configurações", url: "/dashboard/configuracoes", icon: Settings },
]

export function AppSidebar() {
  return (
    <Sidebar className="[--sidebar:#020203] [--sidebar-foreground:#FFFFFF] [--sidebar-border:#2A2A2A] border-r border-[#2A2A2A] text-[#FFFFFF]">
      <SidebarHeader className="relative overflow-hidden border-b border-[#FFFFFF]/10 px-4 py-4">
        <div className="pointer-events-none absolute -right-8 -top-8 h-20 w-20 rounded-full bg-[#DD050A]/25 blur-2xl" />
        <div className="pointer-events-none absolute inset-y-2 left-0 w-1 rounded-r-full bg-[#DD050A]" />
        <div className="relative flex items-center gap-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg border border-[#DD050A]/30 bg-[#DD050A]/20">
            <span className="text-sm font-bold text-[#DD050A]">G</span>
          </div>
          <div>
            <p className="text-sm font-semibold leading-none text-[#FFFFFF]">Gelo Team</p>
            <p className="mt-0.5 text-xs tracking-[0.18em] text-[#FFFFFF]/55">GESTAO DE ALUNOS</p>
          </div>
        </div>
      </SidebarHeader>

      <SidebarContent className="relative px-2 py-3">
        <div className="pointer-events-none absolute bottom-8 left-2 top-8 w-px bg-[linear-gradient(180deg,rgba(221,5,10,0)_0%,rgba(221,5,10,0.85)_38%,rgba(221,5,10,0)_100%)]" />
        <SidebarGroup>
          <SidebarGroupLabel className="mb-2 px-3 text-[10px] tracking-[0.25em] text-[#FFFFFF]/55">NAVEGACAO</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {navItems.map((item) => (
                <SidebarMenuItem key={item.title}>
                  <SidebarMenuButton
                    asChild
                    className="group relative overflow-hidden rounded-xl border border-transparent text-[#FFFFFF]/78 transition-all duration-300 hover:border-[#DD050A]/40 hover:bg-[#DD050A]/10 hover:text-[#FFFFFF]"
                  >
                    <a
                      href={item.url}
                      className="flex items-center gap-3 px-3 py-2.5"
                    >
                      <item.icon className="h-4 w-4" />
                      <span className="text-sm">{item.title}</span>
                    </a>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter className="border-t border-[#FFFFFF]/10 px-2 py-3">
        <SidebarMenu>
          {bottomItems.map((item) => (
            <SidebarMenuItem key={item.title}>
              <SidebarMenuButton asChild className="rounded-xl border border-transparent text-[#FFFFFF]/75 transition-all hover:border-[#FFFFFF]/20 hover:bg-[#FFFFFF]/5 hover:text-[#FFFFFF]">
                <a href={item.url} className="flex items-center gap-3 px-3 py-2.5">
                  <item.icon className="h-4 w-4" />
                  <span className="text-sm">{item.title}</span>
                </a>
              </SidebarMenuButton>
            </SidebarMenuItem>
          ))}
          <SidebarMenuItem>
            <SidebarMenuButton className="w-full rounded-xl border border-[#DD050A]/25 bg-[#DD050A]/8 text-[#FFFFFF]/88 transition-all hover:border-[#DD050A]/55 hover:bg-[#DD050A]/14 hover:text-[#DD050A] cursor-pointer">
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
