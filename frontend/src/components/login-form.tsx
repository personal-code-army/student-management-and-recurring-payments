import { cn } from "@/lib/utils"
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

interface LoginFormProps extends React.ComponentProps<"div"> {
  onSwitchTab?: () => void
}

export function LoginForm({ className, onSwitchTab, ...props }: LoginFormProps) {
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
          <form onSubmit={(e) => e.preventDefault()}>
            <FieldGroup>
              <Field>
                <FieldLabel htmlFor="email" className="text-[#FFFFFF]">Email</FieldLabel>
                <Input
                  id="email"
                  type="email"
                  placeholder="felipe@gmail.com"
                  className="border-[#FFFFFF]/20 bg-[#000000] text-[#FFFFFF] placeholder:text-[#FFFFFF]/40 focus-visible:border-[#DD050A]"
                  required
                />
              </Field>
              <Field>
                <div className="flex items-center">
                  <FieldLabel htmlFor="password" className="text-[#FFFFFF]">Senha</FieldLabel>
                  <a
                    href="#"
                    className="ml-auto inline-block text-sm text-[#FFFFFF]/70 underline-offset-4 hover:text-[#FFFFFF] hover:underline"
                  >
                    Esqueceu sua senha?
                  </a>
                </div>
                <Input
                  id="password"
                  type="password"
                  required
                  placeholder="********"
                  className="border-[#FFFFFF]/20 bg-[#000000] text-[#FFFFFF] placeholder:text-[#FFFFFF]/40 focus-visible:border-[#DD050A]"
                />
              </Field>
              <Field>
                <Button type="submit" className="w-full bg-[#DD050A] text-[#FFFFFF] hover:bg-[#DD050A]/90">Login</Button>
                <FieldDescription className="mt-4 text-center text-[#FFFFFF]/70">
                  Não tem uma conta?{" "}
                  <button
                    type="button"
                    onClick={onSwitchTab}
                    className="text-[#FFFFFF] underline underline-offset-4 transition-colors hover:text-[#DD050A]"
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
