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
    <Sidebar>
      <SidebarHeader className="px-4 py-4 border-b border-zinc-800">
        <div className="flex items-center gap-2">
          <div className="flex items-center justify-center w-8 h-8 rounded-lg bg-emerald-500/20">
            <span className="text-emerald-400 font-bold text-sm">G</span>
          </div>
          <div>
            <p className="text-sm font-semibold text-white leading-none">Gelo Team</p>
            <p className="text-xs text-zinc-500 mt-0.5">Gestão de alunos</p>
          </div>
        </div>
      </SidebarHeader>

      <SidebarContent className="px-2 py-3">
        <SidebarGroup>
          <SidebarGroupLabel className="text-zinc-500 text-xs px-2 mb-1">Navegação</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {navItems.map((item) => (
                <SidebarMenuItem key={item.title}>
                  <SidebarMenuButton asChild className="text-zinc-400 hover:text-white hover:bg-zinc-800 rounded-md transition-colors">
                    <a href={item.url} className="flex items-center gap-3 px-3 py-2">
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

      <SidebarFooter className="px-2 py-3 border-t border-zinc-800">
        <SidebarMenu>
          {bottomItems.map((item) => (
            <SidebarMenuItem key={item.title}>
              <SidebarMenuButton asChild className="text-zinc-400 hover:text-white hover:bg-zinc-800 rounded-md transition-colors">
                <a href={item.url} className="flex items-center gap-3 px-3 py-2">
                  <item.icon className="h-4 w-4" />
                  <span className="text-sm">{item.title}</span>
                </a>
              </SidebarMenuButton>
            </SidebarMenuItem>
          ))}
          <SidebarMenuItem>
            <SidebarMenuButton className="text-zinc-400 hover:text-red-400 hover:bg-red-500/10 rounded-md transition-colors w-full">
              <div className="flex items-center gap-3 px-3 py-2">
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
