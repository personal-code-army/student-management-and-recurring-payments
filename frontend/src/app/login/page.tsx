"use client" 

import { useState } from "react"
import { LoginForm } from "@/components/login-form"
import { SignupForm } from "@/components/signup-form"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"

export default function Page() {
  const [activeTab, setActiveTab] = useState("login")

  return (
    <div className="grid min-h-svh lg:grid-cols-2 bg-zinc-950">
      
      <div className="relative hidden lg:block">
        <div className="sticky top-0 h-svh w-full">
          <img src="./logo.jpg" alt="Background" className="h-full w-full object-cover opacity-40" />
          <div className="absolute inset-0 flex items-center justify-center p-12 bg-gradient-to-t from-zinc-950 via-transparent to-transparent">
             <h2 className="text-3xl font-bold text-white max-w-md text-center">Gelo Team Chakuriki</h2>
          </div>
        </div>
      </div>

      <div className="flex flex-col items-center justify-start p-6 md:p-10 pt-12 lg:pt-24 overflow-y-auto">
        <div className="w-full max-w-sm">
          <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
            <TabsList className="grid w-full grid-cols-2 mb-8">
              <TabsTrigger value="login">Login</TabsTrigger>
              <TabsTrigger value="signup">Cadastro</TabsTrigger>
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