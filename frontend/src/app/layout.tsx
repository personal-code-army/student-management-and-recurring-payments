import type { Metadata } from "next";
import { Geist } from "next/font/google";
import { AuthGuard } from "@/components/auth-guard";
import "./globals.css";

import { ThemeProvider } from "@/components/ui/theme-provider";

const geist = Geist({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Controle de Alunos e Pagamentos",
  description: "Painel de controle Gelo Team Chakuriki",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="pt-BR" suppressHydrationWarning>
      <body className={geist.className}>
        <ThemeProvider>
          <AuthGuard>{children}</AuthGuard>
        </ThemeProvider>
      </body>
    </html>
  );
}
