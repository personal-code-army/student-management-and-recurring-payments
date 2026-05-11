import { useState, type ComponentProps, type FormEvent } from "react"
import { useRouter } from "next/navigation"
import axios from "axios"
import { cn } from "@/lib/utils"
import { api } from "@/lib/api"
import { Button } from "@/components/ui/button"
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import {
  Field,
  FieldDescription,
  FieldGroup,
  FieldLabel,
} from "@/components/ui/field"
import { Input } from "@/components/ui/input"

interface LoginFormProps extends ComponentProps<"div"> {
  onSwitchTab?: () => void
}

export function LoginForm({ className, onSwitchTab, ...props }: LoginFormProps) {
  const router = useRouter()
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [status, setStatus] = useState<"idle" | "loading" | "success" | "error">("idle")
  const [message, setMessage] = useState("")

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setStatus("loading")
    setMessage("")

    try {
      const response = await api.post("/api/auth/login", {
        email,
        password,
      })

      if (response.data?.accessToken) {
        localStorage.setItem("auth_token", response.data.accessToken)
      } else {
        setStatus("error")
        setMessage("Login realizado, mas o token nao foi retornado.")
        return
      }

      setStatus("success")
      setMessage("Login realizado com sucesso.")
      router.replace("/dashboard")
    } catch (error) {
      const errorMessage = axios.isAxiosError(error)
        ? error.response?.data?.message
        : null

      setStatus("error")
      setMessage(errorMessage ?? "Falha na conexao com o servidor.")
    }
  }

  return (
    <div className={cn("flex flex-col gap-6", className)} {...props}>
      <Card className="border border-[#FFFFFF]/15 bg-[#020203]/90 text-[#FFFFFF] shadow-[0_20px_60px_rgba(0,0,0,0.5)] backdrop-blur">
        <CardHeader>
          <CardTitle className="text-2xl text-[#FFFFFF]">Faça login</CardTitle>
          <CardDescription>
            Insira seu e-mail abaixo para acessar sua conta.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit}>
            <FieldGroup>
              <Field>
                <FieldLabel htmlFor="email" className="text-[#FFFFFF]">Email</FieldLabel>
                <Input
                  id="email"
                  type="email"
                  placeholder="felipe@gmail.com"
                  required
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                />
              </Field>
              <Field>
                <div className="flex items-center">
                  <FieldLabel htmlFor="password">Senha</FieldLabel>
                  <a
                    href="#"
                    className="ml-auto inline-block text-sm underline-offset-4 hover:underline text-zinc-400"
                  >
                    Esqueceu sua senha?
                  </a>
                </div>
                <Input
                  id="password"
                  type="password"
                  required
                  placeholder="********"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                />
              </Field>
              {message ? (
                <FieldDescription className={status === "error" ? "text-red-400" : "text-emerald-400"}>
                  {message}
                </FieldDescription>
              ) : null}
              <Field>
                <Button type="submit" className="w-full" disabled={status === "loading"}>
                  {status === "loading" ? "Entrando..." : "Login"}
                </Button>
                <FieldDescription className="text-center mt-4 text-zinc-400">
                  Não tem uma conta?{" "}
                  <button 
                    type="button"
                    onClick={onSwitchTab}
                    className="text-[#09090B] underline underline-offset-4 hover:text-[#535353] transition-colors"
                  >
                    Criar agora
                  </button>
                </FieldDescription>
              </Field>
            </FieldGroup>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}