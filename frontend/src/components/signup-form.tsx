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

interface SignupFormProps extends React.ComponentProps<typeof Card> {
  onSwitchTab?: () => void
}

export function SignupForm({ onSwitchTab, ...props }: SignupFormProps) {
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
        <form onSubmit={(e) => e.preventDefault()}>
          <FieldGroup>
            <Field>
              <FieldLabel htmlFor="name" className="text-[#FFFFFF]">Nome Completo</FieldLabel>
              <Input
                id="name"
                type="text"
                placeholder="Felipe Figueiredo Mascarenhas"
                className="border-[#FFFFFF]/20 bg-[#000000] text-[#FFFFFF] placeholder:text-[#FFFFFF]/40 focus-visible:border-[#DD050A]"
                required
              />
            </Field>
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
              <FieldLabel htmlFor="password" className="text-[#FFFFFF]">Senha</FieldLabel>
              <Input
                id="password"
                type="password"
                className="border-[#FFFFFF]/20 bg-[#000000] text-[#FFFFFF] placeholder:text-[#FFFFFF]/40 focus-visible:border-[#DD050A]"
                required
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
                type="password"
                className="border-[#FFFFFF]/20 bg-[#000000] text-[#FFFFFF] placeholder:text-[#FFFFFF]/40 focus-visible:border-[#DD050A]"
                required
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
          </FieldGroup>
        </form>
      </CardContent>
    </Card>
  )
}
