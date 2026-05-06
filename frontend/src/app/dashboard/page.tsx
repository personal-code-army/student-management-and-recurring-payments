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
	Pago: "bg-emerald-500/15 text-emerald-500 border-emerald-500/20",
	Pendente: "bg-yellow-500/15 text-yellow-500 border-yellow-500/20",
	Atrasado: "bg-red-500/15 text-red-500 border-red-500/20",
};

export default function Dashboard() {
	return (
		<SidebarProvider>
			<AppSidebar />
			<div className="flex flex-col flex-1 min-h-screen bg-white dark:bg-zinc-950 text-black dark:text-white">

				{/* Header */}
				<header className="flex items-center gap-3 px-6 py-4 border-b border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-950 sticky top-0 z-10">
					<SidebarTrigger className="text-zinc-600 dark:text-zinc-400 hover:text-black dark:hover:text-white" />
					<Separator orientation="vertical" className="h-5 bg-zinc-300 dark:bg-zinc-700" />
					<div>
						<h1 className="text-sm font-semibold text-black dark:text-white leading-none">Visão Geral</h1>
						<p className="text-xs text-zinc-600 dark:text-zinc-500 mt-0.5">Bem-vindo de volta</p>
					</div>
				</header>

				<main className="flex-1 p-6 space-y-6">

					{/* KPI Cards */}
					<div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">

						<Card className="bg-white dark:bg-zinc-900 border-zinc-200 dark:border-zinc-800">
							<CardHeader className="flex flex-row items-center justify-between pb-2 pt-4 px-5">
								<CardTitle className="text-xs font-medium text-zinc-600 dark:text-zinc-400 uppercase">Receita Total</CardTitle>
								<CircleDollarSign className="h-4 w-4 text-emerald-500" />
							</CardHeader>
							<CardContent className="px-5 pb-4">
								<p className="text-2xl font-bold text-black dark:text-white">R$ 12.400</p>
								<p className="text-xs text-emerald-500 mt-1 flex items-center gap-1">
									<TrendingUp className="h-3 w-3" /> +8% este mês
								</p>
							</CardContent>
						</Card>

						<Card className="bg-white dark:bg-zinc-900 border-zinc-200 dark:border-zinc-800">
							<CardHeader className="flex flex-row items-center justify-between pb-2 pt-4 px-5">
								<CardTitle className="text-xs font-medium text-zinc-600 dark:text-zinc-400 uppercase">A Receber</CardTitle>
								<Clock className="h-4 w-4 text-yellow-500" />
							</CardHeader>
							<CardContent className="px-5 pb-4">
								<p className="text-2xl font-bold text-black dark:text-white">R$ 1.800</p>
								<p className="text-xs text-zinc-600 dark:text-zinc-500 mt-1">3 pagamentos pendentes</p>
							</CardContent>
						</Card>

						<Card className="bg-white dark:bg-zinc-900 border-zinc-200 dark:border-zinc-800">
							<CardHeader className="flex flex-row items-center justify-between pb-2 pt-4 px-5">
								<CardTitle className="text-xs font-medium text-zinc-600 dark:text-zinc-400 uppercase">Atrasados</CardTitle>
								<CircleAlert className="h-4 w-4 text-red-500" />
							</CardHeader>
							<CardContent className="px-5 pb-4">
								<p className="text-2xl font-bold text-black dark:text-white">2</p>
								<p className="text-xs text-red-500 mt-1">Requer atenção</p>
							</CardContent>
						</Card>

						<Card className="bg-white dark:bg-zinc-900 border-zinc-200 dark:border-zinc-800">
							<CardHeader className="flex flex-row items-center justify-between pb-2 pt-4 px-5">
								<CardTitle className="text-xs font-medium text-zinc-600 dark:text-zinc-400 uppercase">Clientes Ativos</CardTitle>
								<Users className="h-4 w-4 text-blue-500" />
							</CardHeader>
							<CardContent className="px-5 pb-4">
								<p className="text-2xl font-bold text-black dark:text-white">20</p>
								<p className="text-xs text-zinc-600 dark:text-zinc-500 mt-1">+2 este mês</p>
							</CardContent>
						</Card>

					</div>

					{/* Tabela */}
					<Tabs defaultValue="recentes">
						<TabsList className="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800">
							<TabsTrigger value="recentes">Recentes</TabsTrigger>
							<TabsTrigger value="atrasados">Atrasados</TabsTrigger>
						</TabsList>

						<TabsContent value="recentes">
							<Card className="bg-white dark:bg-zinc-900 border-zinc-200 dark:border-zinc-800">
								<CardHeader>
									<CardTitle className="text-black dark:text-white">Recebimentos Recentes</CardTitle>
									<CardDescription className="text-zinc-600 dark:text-zinc-500 text-xs">
										Últimas transações registradas
									</CardDescription>
								</CardHeader>

								<CardContent>
									<Table>
										<TableHeader>
											<TableRow>
												<TableHead>Aluno</TableHead>
												<TableHead>Plano</TableHead>
												<TableHead>Valor</TableHead>
												<TableHead>Vencimento</TableHead>
												<TableHead>Status</TableHead>
											</TableRow>
										</TableHeader>

										<TableBody>
											{pagamentos.map((p) => (
												<TableRow key={p.id}>
													<TableCell className="font-medium">{p.aluno}</TableCell>
													<TableCell>{p.plano}</TableCell>
													<TableCell>{p.valor}</TableCell>
													<TableCell>{p.vencimento}</TableCell>
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