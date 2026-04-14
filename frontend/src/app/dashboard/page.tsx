import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { CircleAlert, CircleDollarSign, Clock, MessageCircle, TrendingUp, Users } from "lucide-react";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/app-sidebar";
import { Separator } from "@/components/ui/separator";

const pagamentos = [
  { id: 1, aluno: "Felipe Souza", plano: "Mensal", valor: "R$ 120,00", vencimento: "01/03/2026", status: "Pago", whatsapp: "" },
  { id: 2, aluno: "Ana Lima", plano: "Trimestral", valor: "R$ 150,00", vencimento: "05/03/2026", status: "Pendente", whatsapp: "" },
  { id: 3, aluno: "Carlos Mendes", plano: "Mensal", valor: "R$ 150,00", vencimento: "28/02/2026", status: "Atrasado", whatsapp: "" },
  { id: 4, aluno: "Bruna Costa", plano: "Anual", valor: "R$ 130,00", vencimento: "10/03/2026", status: "Pago", whatsapp: "" },
  { id: 5, aluno: "Marcos Rocha", plano: "Mensal", valor: "R$ 150,00", vencimento: "15/02/2026", status: "Atrasado", whatsapp: "" },
];

const statusStyle: Record<string, string> = {
  Pago: "border-[#00FF00]/30 bg-[#00FF00]/10 text-[#00FF00]",
  Pendente: "border-[#DD050A]/40 bg-[#DD050A]/10 text-[#DD050A]",
  Atrasado: "border-[#DD050A]/60 bg-[#DD050A]/20 text-[#DD050A]",
};

export default function Dashboard() {
  const getWhatsappUrl = (aluno: string, whatsapp: string) => {
    const mensagem = encodeURIComponent(`Olá ${aluno}, identificamos um pagamento em atraso.`);
    return `https://wa.me/${whatsapp}?text=${mensagem}`;
  };

  return (
    <SidebarProvider>
      <AppSidebar />
      <div className="flex min-h-screen flex-1 flex-col bg-[#000000] text-[#FFFFFF]">
        {/* Header */}
        <header className="sticky top-0 z-10 flex items-center gap-3 border-b border-[#FFFFFF]/10 bg-[#020203]/90 px-4 py-3 backdrop-blur sm:px-6 sm:py-4">
          <SidebarTrigger className="text-[#FFFFFF]/70 hover:text-[#FFFFFF] md:hidden" />
          <Separator orientation="vertical" className="h-5 bg-[#FFFFFF]/20 md:hidden" />
          <div>
            <h1 className="text-sm font-semibold leading-none text-[#FFFFFF]">Visão Geral</h1>
            <p className="mt-0.5 text-xs text-[#FFFFFF]/60">Bem-vindo de volta</p>
          </div>
        </header>


        <main className="flex-1 space-y-5 p-4 sm:space-y-6 sm:p-6">
          {/* KPI Cards */}
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
            <Card className="group border-[#FFFFFF]/12 bg-[#020203] transition-colors hover:border-[#DD050A]/50">
              <CardHeader className="flex flex-row items-center justify-between px-4 pb-2 pt-4 sm:px-5">
                <CardTitle className="text-xs font-medium uppercase tracking-wider text-[#FFFFFF]/65">Receita Total</CardTitle>
                <span className="flex h-7 w-7 items-center justify-center rounded-md bg-[#FFFFFF]/10 transition-colors group-hover:bg-[#DD050A]/12">
                  <CircleDollarSign className="h-4 w-4 text-[#FFFFFF] transition-colors group-hover:text-[#DD050A]" />
                </span>
              </CardHeader>
              <CardContent className="px-4 pb-4 sm:px-5">
                <p className="text-xl font-bold text-[#FFFFFF] sm:text-2xl">R$ 12.400</p>
                <p className="mt-1 flex items-center gap-1 text-xs text-[#DD050A]">
                  <TrendingUp className="h-3 w-3" /> +8% este mês
                </p>
              </CardContent>
            </Card>

            <Card className="group border-[#FFFFFF]/12 bg-[#020203] transition-colors hover:border-[#DD050A]/50">
              <CardHeader className="flex flex-row items-center justify-between px-4 pb-2 pt-4 sm:px-5">
                <CardTitle className="text-xs font-medium uppercase tracking-wider text-[#FFFFFF]/65">A Receber</CardTitle>
                <span className="flex h-7 w-7 items-center justify-center rounded-md bg-[#FFFFFF]/10 transition-colors group-hover:bg-[#DD050A]/12">
                  <Clock className="h-4 w-4 text-[#FFFFFF] transition-colors group-hover:text-[#DD050A]" />
                </span>
              </CardHeader>
              <CardContent className="px-4 pb-4 sm:px-5">
                <p className="text-xl font-bold text-[#FFFFFF] sm:text-2xl">R$ 1.800</p>
                <p className="mt-1 text-xs text-[#FFFFFF]/60">3 pagamentos pendentes</p>
              </CardContent>
            </Card>

            <Card className="group border-[#FFFFFF]/12 bg-[#020203] transition-colors hover:border-[#DD050A]/50">
              <CardHeader className="flex flex-row items-center justify-between px-4 pb-2 pt-4 sm:px-5">
                <CardTitle className="text-xs font-medium uppercase tracking-wider text-[#FFFFFF]/65">Atrasados</CardTitle>
                <span className="flex h-7 w-7 items-center justify-center rounded-md bg-[#FFFFFF]/10 transition-colors group-hover:bg-[#DD050A]/12">
                  <CircleAlert className="h-4 w-4 text-[#FFFFFF] transition-colors group-hover:text-[#DD050A]" />
                </span>
              </CardHeader>
              <CardContent className="px-4 pb-4 sm:px-5">
                <p className="text-xl font-bold text-[#FFFFFF] sm:text-2xl">2</p>
                <p className="mt-1 text-xs text-[#DD050A]">Requer atenção</p>
              </CardContent>
            </Card>

            <Card className="group border-[#FFFFFF]/12 bg-[#020203] transition-colors hover:border-[#DD050A]/50">
              <CardHeader className="flex flex-row items-center justify-between px-4 pb-2 pt-4 sm:px-5">
                <CardTitle className="text-xs font-medium uppercase tracking-wider text-[#FFFFFF]/65">Clientes Ativos</CardTitle>
                <span className="flex h-7 w-7 items-center justify-center rounded-md bg-[#FFFFFF]/10 transition-colors group-hover:bg-[#DD050A]/12">
                  <Users className="h-4 w-4 text-[#FFFFFF] transition-colors group-hover:text-[#DD050A]" />
                </span>
              </CardHeader>
              <CardContent className="px-4 pb-4 sm:px-5">
                <p className="text-xl font-bold text-[#FFFFFF] sm:text-2xl">20</p>
                <p className="mt-1 text-xs text-[#FFFFFF]/60">+2 este mês</p>
              </CardContent>
            </Card>
          </div>

          {/* Tabela de pagamentos */}
          <Tabs defaultValue="recentes">
            <div className="mb-3 flex items-center justify-between">
              <TabsList className="grid w-full grid-cols-2 border border-[#FFFFFF]/12 bg-[#020203] sm:w-auto">
                <TabsTrigger value="recentes" className="text-[#FFFFFF]/70 data-[state=active]:bg-[#DD050A] data-[state=active]:text-[#FFFFFF]">
                  Recentes
                </TabsTrigger>
                <TabsTrigger value="atrasados" className="text-[#FFFFFF]/70 data-[state=active]:bg-[#DD050A] data-[state=active]:text-[#FFFFFF]">
                  Atrasados
                </TabsTrigger>
              </TabsList>
            </div>

            <TabsContent value="recentes">
              <Card className="border-[#FFFFFF]/12 bg-[#020203]">
                <CardHeader className="px-4 pb-3 pt-5 sm:px-5">
                  <CardTitle className="text-base text-[#FFFFFF]">Recebimentos Recentes</CardTitle>
                  <CardDescription className="text-xs text-[#FFFFFF]/60">Últimas transações registradas na plataforma</CardDescription>
                </CardHeader>
                <CardContent className="px-4 pb-5 sm:px-5">
                  <Table className="min-w-140 table-fixed sm:min-w-0">
                    <TableHeader>
                      <TableRow className="border-[#FFFFFF]/10 hover:bg-transparent">
                        <TableHead className="text-xs uppercase text-[#FFFFFF]/60">Aluno</TableHead>
                        <TableHead className="hidden text-xs uppercase text-[#FFFFFF]/60 sm:table-cell">Plano</TableHead>
                        <TableHead className="text-xs uppercase text-[#FFFFFF]/60">Valor</TableHead>
                        <TableHead className="hidden text-xs uppercase text-[#FFFFFF]/60 sm:table-cell">Vencimento</TableHead>
                        <TableHead className="text-xs uppercase text-[#FFFFFF]/60">Status</TableHead>
                        <TableHead className="text-xs uppercase text-[#FFFFFF]/60">Contato</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {pagamentos.map((p) => (
                        <TableRow key={p.id} className="border-[#FFFFFF]/10 transition-colors hover:bg-[#FFFFFF]/5">
                          <TableCell className="font-medium text-[#FFFFFF]">{p.aluno}</TableCell>
                          <TableCell className="hidden text-[#FFFFFF]/70 sm:table-cell">{p.plano}</TableCell>
                          <TableCell className="font-mono text-[#FFFFFF]/85">{p.valor}</TableCell>
                          <TableCell className="hidden text-[#FFFFFF]/70 sm:table-cell">{p.vencimento}</TableCell>
                          <TableCell>
                            <Badge variant="outline" className={statusStyle[p.status]}>
                              {p.status}
                            </Badge>
                          </TableCell>
                          <TableCell>
                            <a
                              href={getWhatsappUrl(p.aluno, p.whatsapp)}
                              target="_blank"
                              rel="noreferrer"
                              className="inline-flex items-center gap-1.5 rounded-md border border-[#DD050A]/50 bg-[#DD050A]/10 px-2.5 py-1.5 text-xs font-medium text-[#FFFFFF] transition-colors hover:bg-[#DD050A]/20"
                            >
                              <MessageCircle className="h-3.5 w-3.5" />
                              WhatsApp
                            </a>
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="atrasados">
              <Card className="border-[#FFFFFF]/12 bg-[#020203]">
                <CardHeader className="px-4 pb-3 pt-5 sm:px-5">
                  <CardTitle className="text-base text-[#FFFFFF]">Pagamentos Atrasados</CardTitle>
                  <CardDescription className="text-xs text-[#FFFFFF]/60">Alunos com mensalidades em atraso</CardDescription>
                </CardHeader>
                <CardContent className="px-4 pb-5 sm:px-5">
                  <Table className="min-w-140 table-fixed sm:min-w-0">
                    <TableHeader>
                      <TableRow className="border-[#FFFFFF]/10 hover:bg-transparent">
                        <TableHead className="text-xs uppercase text-[#FFFFFF]/60">Aluno</TableHead>
                        <TableHead className="hidden text-xs uppercase text-[#FFFFFF]/60 sm:table-cell">Plano</TableHead>
                        <TableHead className="text-xs uppercase text-[#FFFFFF]/60">Valor</TableHead>
                        <TableHead className="hidden text-xs uppercase text-[#FFFFFF]/60 sm:table-cell">Vencimento</TableHead>
                        <TableHead className="text-xs uppercase text-[#FFFFFF]/60">Status</TableHead>
                        <TableHead className="text-xs uppercase text-[#FFFFFF]/60">Contato</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {pagamentos
                        .filter((p) => p.status === "Atrasado")
                        .map((p) => (
                          <TableRow key={p.id} className="border-[#FFFFFF]/10 transition-colors hover:bg-[#FFFFFF]/5">
                            <TableCell className="font-medium text-[#FFFFFF]">{p.aluno}</TableCell>
                            <TableCell className="hidden text-[#FFFFFF]/70 sm:table-cell">{p.plano}</TableCell>
                            <TableCell className="font-mono text-[#FFFFFF]/85">{p.valor}</TableCell>
                            <TableCell className="hidden text-[#FFFFFF]/70 sm:table-cell">{p.vencimento}</TableCell>
                            <TableCell>
                              <Badge variant="outline" className={statusStyle[p.status]}>
                                {p.status}
                              </Badge>
                            </TableCell>
                            <TableCell>
                              <a
                                href={getWhatsappUrl(p.aluno, p.whatsapp)}
                                target="_blank"
                                rel="noreferrer"
                                className="inline-flex items-center gap-1.5 rounded-md border border-[#DD050A]/50 bg-[#DD050A]/10 px-2.5 py-1.5 text-xs font-medium text-[#FFFFFF] transition-colors hover:bg-[#DD050A]/20"
                              >
                                <MessageCircle className="h-3.5 w-3.5" />
                                WhatsApp
                              </a>
                            </TableCell>
                          </TableRow>
                        ))}
                    </TableBody>
                  </Table>
                </CardContent>
              </Card>
            </TabsContent>
          </Tabs>
        </main>
      </div>
    </SidebarProvider>
  );
}
