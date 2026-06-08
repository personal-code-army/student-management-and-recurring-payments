"use client"

import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { AlertTriangle, ArrowLeft, Home } from "lucide-react"

export default function NotFound() {
  const router = useRouter()

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-[#000000] text-[#FFFFFF] px-6">
      <div className="flex flex-col items-center text-center max-w-md">
        <div className="flex h-16 w-16 items-center justify-center rounded-full bg-[#DD050A]/15 mb-6">
          <AlertTriangle className="h-8 w-8 text-[#DD050A]" />
        </div>

        <p className="text-7xl font-bold text-[#DD050A] leading-none mb-4">404</p>

        <h1 className="text-xl font-semibold text-[#FFFFFF] mb-2">Página não encontrada</h1>
        <p className="text-sm text-[#FFFFFF]/50 mb-8">
          A página que você está tentando acessar não existe ou foi removida.
        </p>

        <div className="flex flex-col sm:flex-row gap-3 w-full">
          <Button
            variant="outline"
            onClick={() => router.back()}
            className="flex-1 gap-2 border-[#FFFFFF]/15 bg-transparent text-[#FFFFFF]/70 hover:bg-[#FFFFFF]/5 hover:text-[#FFFFFF]"
          >
            <ArrowLeft className="h-4 w-4" />
            Voltar
          </Button>
          <Button
            onClick={() => router.replace("/dashboard")}
            className="flex-1 gap-2 bg-[#DD050A] text-[#FFFFFF] hover:bg-[#DD050A]/85"
          >
            <Home className="h-4 w-4" />
            Ir para o início
          </Button>
        </div>
      </div>
    </div>
  )
}
