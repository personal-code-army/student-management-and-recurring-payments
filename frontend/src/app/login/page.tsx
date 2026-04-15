"use client" 

import { useState } from "react"
import { LoginForm } from "@/components/login-form"
import { SignupForm } from "@/components/signup-form"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"

export default function Page() {
  const [activeTab, setActiveTab] = useState("login")

  return (
    <div className="grid min-h-svh bg-[#000000] text-[#FFFFFF] lg:grid-cols-2">
      <div className="relative hidden overflow-hidden lg:block">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_20%_30%,rgba(221,5,10,0.28),transparent_45%),radial-gradient(circle_at_80%_70%,rgba(255,255,255,0.08),transparent_45%),linear-gradient(160deg,#000000_0%,#020203_100%)]" />
        <div className="relative flex h-svh w-full flex-col justify-between p-10">
          <div className="inline-flex w-fit items-center gap-3 rounded-full border border-[#FFFFFF]/20 bg-[#020203]/80 px-4 py-2 backdrop-blur">
            <span className="h-2.5 w-2.5 rounded-full bg-[#DD050A]" />
            <span className="text-xs tracking-[0.2em] text-[#FFFFFF]/80">GELO TEAM CHAKURIKI</span>
          </div>
          <div>
            <h2 className="max-w-md text-4xl font-semibold leading-tight text-[#FFFFFF]">
              Gestão de alunos e recorrência com foco no que importa.
            </h2>
            <p className="mt-4 max-w-sm text-sm leading-relaxed text-[#FFFFFF]/72">
              Plataforma para acompanhar pagamentos, alunos e planos em uma visualização limpa e objetiva.
            </p>
          </div>
        </div>
      </div>


      <div className="relative flex flex-col items-center justify-center overflow-y-auto px-4 py-6 sm:px-6 sm:py-10 md:px-10">
        <div className="absolute inset-0 bg-[linear-gradient(180deg,#020203_0%,#000000_100%)]" />
        <div className="absolute -top-16 right-6 h-48 w-48 rounded-full bg-[#DD050A]/15 blur-3xl" />
        <div className="absolute bottom-0 left-0 h-40 w-40 rounded-full bg-[#FFFFFF]/10 blur-3xl" />

        <div className="relative mb-6 w-full max-w-sm lg:hidden">
          <p className="text-[10px] tracking-[0.24em] text-[#FFFFFF]/65">GELO TEAM CHAKURIKI</p>
          <h1 className="mt-2 text-2xl font-semibold leading-tight text-[#FFFFFF]">Acesse sua conta</h1>
          <p className="mt-2 text-sm text-[#FFFFFF]/65">Gerencie alunos e pagamentos de forma simples no celular.</p>
        </div>

        <div className="relative w-full max-w-sm">
          <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
            <TabsList className="mb-8 grid w-full grid-cols-2 border border-[#FFFFFF]/15 bg-[#020203]/90 p-1">
              <TabsTrigger value="login" className="text-[#FFFFFF]/75 data-[state=active]:bg-[#DD050A] data-[state=active]:text-[#FFFFFF]">
                Login
              </TabsTrigger>
              <TabsTrigger value="signup" className="text-[#FFFFFF]/75 data-[state=active]:bg-[#DD050A] data-[state=active]:text-[#FFFFFF]">
                Cadastro
              </TabsTrigger>
            </TabsList>

            <TabsContent value="login">
              <LoginForm onSwitchTab={() => setActiveTab("signup")} />
            </TabsContent>

            <TabsContent value="signup">
              <SignupForm onSwitchTab={() => setActiveTab("login")} />
            </TabsContent>
          </Tabs>
        </div>
      </div>
    </div>
  )
}
