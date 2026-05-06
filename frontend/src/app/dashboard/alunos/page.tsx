import type { Metadata } from "next"
import { AlunosClient } from "./alunos-client"

export const metadata: Metadata = {
  title: "Alunos | Gelo Team",
}

export default function AlunosPage() {
  return <AlunosClient />
}
