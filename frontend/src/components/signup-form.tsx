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
    <Card {...props}>
      <CardHeader>
        <CardTitle>Criar conta</CardTitle>
        <CardDescription>
          Insira suas informações abaixo para criar sua conta
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={(e) => e.preventDefault()}>
          <FieldGroup>
            <Field>
              <FieldLabel htmlFor="name">Nome Completo</FieldLabel>
              <Input id="name" type="text" placeholder="Felipe Figueiredo Mascarenhas" required />
            </Field>
            <Field>
              <FieldLabel htmlFor="email">Email</FieldLabel>
              <Input
                id="email"
                type="email"
                placeholder="felipe@gmail.com"
                required
              />
            </Field>
            <Field>
              <FieldLabel htmlFor="password">Senha</FieldLabel>
              <Input id="password" type="password" required />
              <FieldDescription>
                Deve ter pelo menos 8 caracteres.
              </FieldDescription>
            </Field>
            <Field>
              <FieldLabel htmlFor="confirm-password">
                Confirmar Senha
              </FieldLabel>
              <Input id="confirm-password" type="password" required />
              <FieldDescription>Por favor, confirme sua senha.</FieldDescription>
            </Field>
            <Field>
              <Button type="submit" className="w-full">Criar conta</Button>
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