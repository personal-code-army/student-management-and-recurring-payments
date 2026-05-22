import { useState, type ComponentProps, type FormEvent } from "react"
import axios from "axios"
import { Button } from "@/components/ui/button"
import { api } from "@/lib/api"
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

interface SignupFormProps extends ComponentProps<typeof Card> {
  onSwitchTab?: () => void
}

export function SignupForm({ onSwitchTab, ...props }: SignupFormProps) {
  const [name, setName] = useState("")
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const [cellphoneNumber, setCellphoneNumber] = useState("")
  const [companyId] = useState(2)
  const [status, setStatus] = useState<"idle" | "loading" | "success" | "error">("idle")
  const [message, setMessage] = useState("")

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const formData = new FormData(event.currentTarget)
    const submittedPassword = String(formData.get("password") ?? "")
    const submittedConfirmPassword = String(formData.get("confirmPassword") ?? "")

    if (submittedPassword && submittedPassword !== password) {
      setPassword(submittedPassword)
    }
    if (submittedConfirmPassword && submittedConfirmPassword !== confirmPassword) {
      setConfirmPassword(submittedConfirmPassword)
    }

    const payloadPassword = submittedPassword || password
    const payloadConfirmPassword = submittedConfirmPassword || confirmPassword

    if (!payloadPassword) {
      setStatus("error")
      setMessage("Informe uma senha para continuar.")
      return
    }

    if (payloadPassword.length < 8) {
      setStatus("error")
      setMessage("A senha deve ter pelo menos 8 caracteres.")
      return
    }

    if (payloadPassword !== payloadConfirmPassword) {
      setStatus("error")
      setMessage("As senhas nao conferem.")
      return
    }

    setMessage("")
    setStatus("loading")

    try {
      await api.post("/api/auth/register", {
        name,
        email,
        password: payloadPassword,
        cellphoneNumber,
        companyId,
      })

      setStatus("success")
      setMessage("Conta criada com sucesso. Voce pode entrar agora.")
    } catch (error) {
      const errorMessage = axios.isAxiosError(error)
        ? error.response?.data?.message
        : null

      setStatus("error")
      setMessage(errorMessage ?? "Falha na conexao com o servidor.")
    }
  }

  return (
    <Card
      {...props}
      className="border border-[#FFFFFF]/15 bg-[#020203]/90 text-[#FFFFFF] shadow-[0_20px_60px_rgba(0,0,0,0.5)] backdrop-blur"
    >
      <CardHeader>
        <CardTitle className="text-2xl text-[#FFFFFF]">Criar conta</CardTitle>
        <CardDescription>
          Insira suas informações abaixo para criar sua conta
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit}>
          <FieldGroup>
            <Field>
              <FieldLabel htmlFor="name">Nome Completo</FieldLabel>
                <Input
                  id="name"
                  name="name"
                  type="text"
                  placeholder="Felipe Figueiredo Mascarenhas"
                  autoComplete="name"
                  required
                  value={name}
                  onChange={(event) => setName(event.target.value)}
                />
            </Field>
            <Field>
              <FieldLabel htmlFor="email" className="text-[#FFFFFF]">Email</FieldLabel>
                <Input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="felipe@gmail.com"
                  autoComplete="email"
                  required
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                />
            </Field>
            <Field>
              <FieldLabel htmlFor="password" className="text-[#FFFFFF]">Senha</FieldLabel>
                <Input
                  id="password"
                  name="password"
                  type="password"
                  className="border-[#FFFFFF]/20 bg-[#000000] text-[#FFFFFF] placeholder:text-[#FFFFFF]/40 focus-visible:border-[#DD050A]"
                  autoComplete="new-password"
                  required
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                />
              <FieldDescription className="text-[#FFFFFF]/65">
                Deve ter pelo menos 8 caracteres.
              </FieldDescription>
            </Field>
            <Field>
              <FieldLabel htmlFor="confirm-password">
                Confirmar Senha
              </FieldLabel>
                <Input
                  id="confirm-password"
                  name="confirmPassword"
                  type="password"
                  className="border-[#FFFFFF]/20 bg-[#000000] text-[#FFFFFF] placeholder:text-[#FFFFFF]/40 focus-visible:border-[#DD050A]"
                  autoComplete="new-password"
                  required
                  value={confirmPassword}
                  onChange={(event) => setConfirmPassword(event.target.value)}
                />
              <FieldDescription className="text-[#FFFFFF]/65">Por favor, confirme sua senha.</FieldDescription>
            </Field>
            <Field>
              <Button type="submit" className="w-full bg-[#DD050A] text-[#FFFFFF] hover:bg-[#DD050A]/90">Criar conta</Button>
              <FieldDescription className="mt-4 text-center text-[#FFFFFF]/70">
                Já tem uma conta?{" "}
                <button
                  type="button"
                  onClick={onSwitchTab}
                  className="text-[#FFFFFF] underline underline-offset-4 transition-colors hover:text-[#DD050A]"
                >
                  Entrar
                </button>
              </FieldDescription>
            </Field>
            {message ? (
              <FieldDescription className={status === "error" ? "text-red-400" : "text-emerald-400"}>
                {message}
              </FieldDescription>
            ) : null}
            <Field>
              <Button type="submit" className="w-full" disabled={status === "loading"}>
                {status === "loading" ? "Criando..." : "Criar conta"}
              </Button>
              <FieldDescription className="text-center mt-4 text-zinc-400">
                Já tem uma conta?{" "}
                <button 
                  type="button"
                  onClick={onSwitchTab}
                  className="text-[#09090B] underline underline-offset-4 hover:text-[#535353] transition-colors"
                >
                  Entrar
                </button>
              </FieldDescription>
            </Field>
          </FieldGroup>
        </form>
      </CardContent>
    </Card>
  )
}
