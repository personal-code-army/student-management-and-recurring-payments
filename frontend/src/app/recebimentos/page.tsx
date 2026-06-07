import type { Metadata } from "next"

import { RecebimentosClient } from "./recebimentos-client"

export const metadata: Metadata = {
  title: "Recebimentos | Gelo Team",
}

export default function RecebimentosPage() {
  return <RecebimentosClient />
}
