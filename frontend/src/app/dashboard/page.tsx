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
import { CircleAlert, CircleDollarSign, Clock, TrendingUp, Users } from "lucide-react";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/app-sidebar";
import { Separator } from "@/components/ui/separator";

const pagamentos = [
  { id: 1, aluno: "Felipe Souza", plano: "Mensal", valor: "R$ 150,00", vencimento: "01/03/2026", status: "Pago" },
  { id: 2, aluno: "Ana Lima", plano: "Trimestral", valor: "R$ 400,00", vencimento: "05/03/2026", status: "Pendente" },
  { id: 3, aluno: "Carlos Mendes", plano: "Mensal", valor: "R$ 150,00", vencimento: "28/02/2026", status: "Atrasado" },
  { id: 4, aluno: "Bruna Costa", plano: "Anual", valor: "R$ 1.400,00", vencimento: "10/03/2026", status: "Pago" },
  { id: 5, aluno: "Marcos Rocha", plano: "Mensal", valor: "R$ 150,00", vencimento: "15/02/2026", status: "Atrasado" },
];

const statusStyle: Record<string, string> = {
  Pago: "bg-emerald-500/15 text-emerald-400 border-emerald-500/20",
  Pendente: "bg-yellow-500/15 text-yellow-400 border-yellow-500/20",
  Atrasado: "bg-red-500/15 text-red-400 border-red-500/20",
};

export default function Dashboard() {
  return (
    <SidebarProvider>
      <AppSidebar />
      <div className="flex flex-col flex-1 min-h-screen bg-zinc-950 text-white">
        {/* Header */}
        <header className="flex items-center gap-3 px-6 py-4 border-b border-zinc-800 bg-zinc-950/80 backdrop-blur sticky top-0 z-10">
          <SidebarTrigger className="text-zinc-400 hover:text-white" />
          <Separator orientation="vertical" className="h-5 bg-zinc-700" />
          <div>
            <h1 className="text-sm font-semibold text-white leading-none">Visão Geral</h1>
            <p className="text-xs text-zinc-500 mt-0.5">Bem-vindo de volta 👋</p>
          </div>
        </header>

        <main className="flex-1 p-6 space-y-6">
          {/* KPI Cards */}
          <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
            <Card className="bg-zinc-900 border-zinc-800 hover:border-zinc-700 transition-colors">
              <CardHeader className="flex flex-row items-center justify-between pb-2 pt-4 px-5">
                <CardTitle className="text-xs font-medium text-zinc-400 uppercase tracking-wider">Receita Total</CardTitle>
                <span className="flex items-center justify-center w-7 h-7 rounded-md bg-emerald-500/10">
                  <CircleDollarSign className="h-4 w-4 text-emerald-400" />
                </span>
              </CardHeader>
              <CardContent className="px-5 pb-4">
                <p className="text-2xl font-bold text-white">R$ 12.400</p>
                <p className="text-xs text-emerald-400 mt-1 flex items-center gap-1">
                  <TrendingUp className="h-3 w-3" /> +8% este mês
                </p>
              </CardContent>
            </Card>

            <Card className="bg-zinc-900 border-zinc-800 hover:border-zinc-700 transition-colors">
              <CardHeader className="flex flex-row items-center justify-between pb-2 pt-4 px-5">
                <CardTitle className="text-xs font-medium text-zinc-400 uppercase tracking-wider">A Receber</CardTitle>
                <span className="flex items-center justify-center w-7 h-7 rounded-md bg-yellow-500/10">
                  <Clock className="h-4 w-4 text-yellow-400" />
                </span>
              </CardHeader>
              <CardContent className="px-5 pb-4">
                <p className="text-2xl font-bold text-white">R$ 1.800</p>
                <p className="text-xs text-zinc-500 mt-1">3 pagamentos pendentes</p>
              </CardContent>
            </Card>

            <Card className="bg-zinc-900 border-zinc-800 hover:border-zinc-700 transition-colors">
              <CardHeader className="flex flex-row items-center justify-between pb-2 pt-4 px-5">
                <CardTitle className="text-xs font-medium text-zinc-400 uppercase tracking-wider">Atrasados</CardTitle>
                <span className="flex items-center justify-center w-7 h-7 rounded-md bg-red-500/10">
                  <CircleAlert className="h-4 w-4 text-red-400" />
                </span>
              </CardHeader>
              <CardContent className="px-5 pb-4">
                <p className="text-2xl font-bold text-white">2</p>
                <p className="text-xs text-red-400 mt-1">Requer atenção</p>
              </CardContent>
            </Card>

            <Card className="bg-zinc-900 border-zinc-800 hover:border-zinc-700 transition-colors">
              <CardHeader className="flex flex-row items-center justify-between pb-2 pt-4 px-5">
                <CardTitle className="text-xs font-medium text-zinc-400 uppercase tracking-wider">Clientes Ativos</CardTitle>
                <span className="flex items-center justify-center w-7 h-7 rounded-md bg-blue-500/10">
                  <Users className="h-4 w-4 text-blue-400" />
                </span>
              </CardHeader>
              <CardContent className="px-5 pb-4">
                <p className="text-2xl font-bold text-white">20</p>
                <p className="text-xs text-zinc-500 mt-1">+2 este mês</p>
              </CardContent>
            </Card>
          </div>

          {/* Tabela de pagamentos */}
          <Tabs defaultValue="recentes">
            <div className="flex items-center justify-between mb-3">
              <TabsList className="bg-zinc-900 border border-zinc-800">
                <TabsTrigger value="recentes" className="data-[state=active]:bg-zinc-800 data-[state=active]:text-white text-zinc-400">
                  Recentes
                </TabsTrigger>
                <TabsTrigger value="atrasados" className="data-[state=active]:bg-zinc-800 data-[state=active]:text-white text-zinc-400">
                  Atrasados
                </TabsTrigger>
              </TabsList>
            </div>

            <TabsContent value="recentes">
              <Card className="bg-zinc-900 border-zinc-800">
                <CardHeader className="px-5 pt-5 pb-3">
                  <CardTitle className="text-base text-white">Recebimentos Recentes</CardTitle>
                  <CardDescription className="text-zinc-500 text-xs">Últimas transações registradas na plataforma</CardDescription>
                </CardHeader>
                <CardContent className="px-5 pb-5">
                  <Table>
                    <TableHeader>
                      <TableRow className="border-zinc-800 hover:bg-transparent">
                        <TableHead className="text-zinc-500 text-xs uppercase">Aluno</TableHead>
                        <TableHead className="text-zinc-500 text-xs uppercase">Plano</TableHead>
                        <TableHead className="text-zinc-500 text-xs uppercase">Valor</TableHead>
                        <TableHead className="text-zinc-500 text-xs uppercase">Vencimento</TableHead>
                        <TableHead className="text-zinc-500 text-xs uppercase">Status</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {pagamentos.map((p) => (
                        <TableRow key={p.id} className="border-zinc-800 hover:bg-zinc-800/50 transition-colors">
                          <TableCell className="text-white font-medium">{p.aluno}</TableCell>
                          <TableCell className="text-zinc-400">{p.plano}</TableCell>
                          <TableCell className="text-zinc-300 font-mono">{p.valor}</TableCell>
                          <TableCell className="text-zinc-400">{p.vencimento}</TableCell>
                          <TableCell>
                            <Badge variant="outline" className={statusStyle[p.status]}>
                              {p.status}
                            </Badge>
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="atrasados">
              <Card className="bg-zinc-900 border-zinc-800">
                <CardHeader className="px-5 pt-5 pb-3">
                  <CardTitle className="text-base text-white">Pagamentos Atrasados</CardTitle>
                  <CardDescription className="text-zinc-500 text-xs">Alunos com mensalidades em atraso</CardDescription>
                </CardHeader>
                <CardContent className="px-5 pb-5">
                  <Table>
                    <TableHeader>
                      <TableRow className="border-zinc-800 hover:bg-transparent">
                        <TableHead className="text-zinc-500 text-xs uppercase">Aluno</TableHead>
                        <TableHead className="text-zinc-500 text-xs uppercase">Plano</TableHead>
                        <TableHead className="text-zinc-500 text-xs uppercase">Valor</TableHead>
                        <TableHead className="text-zinc-500 text-xs uppercase">Vencimento</TableHead>
                        <TableHead className="text-zinc-500 text-xs uppercase">Status</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {pagamentos
                        .filter((p) => p.status === "Atrasado")
                        .map((p) => (
                          <TableRow key={p.id} className="border-zinc-800 hover:bg-zinc-800/50 transition-colors">
                            <TableCell className="text-white font-medium">{p.aluno}</TableCell>
                            <TableCell className="text-zinc-400">{p.plano}</TableCell>
                            <TableCell className="text-zinc-300 font-mono">{p.valor}</TableCell>
                            <TableCell className="text-zinc-400">{p.vencimento}</TableCell>
                            <TableCell>
                              <Badge variant="outline" className={statusStyle[p.status]}>
                                {p.status}
                              </Badge>
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
