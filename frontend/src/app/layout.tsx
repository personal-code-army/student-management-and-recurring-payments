import type { Metadata } from "next";
import { Geist } from "next/font/google";
import "./globals.css";


const geist = Geist({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Controle de Alunos e Pagamentos ",
  description: "Painel de controle Gelo Team Chakuriki",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="pt-BR">
      <body className={geist.className}>{children}</body>
    </html>
  );
}
