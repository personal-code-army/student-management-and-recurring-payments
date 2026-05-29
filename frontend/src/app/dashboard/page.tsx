"use client";

import { useEffect, useMemo, useState } from "react";
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
import { CircleAlert, CircleDollarSign, Clock, Users } from "lucide-react";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/app-sidebar";
import { api } from "@/lib/api";

type ApiResponse<T> = { data: T };

interface Payment {
	id: number;
	value: number;
	status: string;
}

interface StudentLite {
	id: number;
	active: boolean;
}

const brl = new Intl.NumberFormat("pt-BR", {
	style: "currency",
	currency: "BRL",
	maximumFractionDigits: 0,
});

// MOCK — pendente de backend: "A Receber", "Atrasados" e esta tabela precisam de
// status de pagamento confirmados e de um payload com nome de aluno/plano.
// Ver contrato pendente (PaymentResponse enriquecido + /api/dashboard/summary).
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
	const [pagamentosApi, setPagamentosApi] = useState<Payment[]>([]);
	const [clientesAtivos, setClientesAtivos] = useState(0);
	const [carregando, setCarregando] = useState(true);
	const [erro, setErro] = useState<string | null>(null);

	useEffect(() => {
		let ativo = true;
		(async () => {
			try {
				setErro(null);
				setCarregando(true);
				const [pagRes, alunosRes] = await Promise.all([
					api.get<ApiResponse<Payment[]>>("/api/payments"),
					api.get<ApiResponse<StudentLite[]>>("/api/students", { params: { active: true } }),
				]);
				if (!ativo) return;
				setPagamentosApi(Array.isArray(pagRes.data?.data) ? pagRes.data.data : []);
				setClientesAtivos(Array.isArray(alunosRes.data?.data) ? alunosRes.data.data.length : 0);
			} catch (err) {
				console.error("Erro ao carregar dados do dashboard", err);
				if (ativo) setErro("Nao foi possivel carregar os dados do dashboard.");
			} finally {
				if (ativo) setCarregando(false);
			}
		})();
		return () => {
			ativo = false;
		};
	}, []);

	const pagos = useMemo(() => pagamentosApi.filter((p) => p.status === "PAID"), [pagamentosApi]);
	const receitaTotal = useMemo(() => pagos.reduce((soma, p) => soma + (p.value ?? 0), 0), [pagos]);

	return (
		<SidebarProvider>
			<AppSidebar />
			<div className="flex flex-col flex-1 min-h-screen bg-white dark:bg-zinc-950 text-black dark:text-white">

				{/* Header */}
				<header className="flex items-center gap-3 px-6 py-4 border-b border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-950 sticky top-0 z-10">
					<SidebarTrigger className="text-zinc-600 dark:text-zinc-400 hover:text-black dark:hover:text-white" />
					<div
						aria-hidden
						className="shrink-0 self-center"
						style={{ width: "1px", height: "20px", backgroundColor: "rgba(255,255,255,0.45)" }}
					/>
					<div>
						<h1 className="text-sm font-semibold text-black dark:text-white leading-none">Visão Geral</h1>
						<p className="text-xs text-zinc-600 dark:text-zinc-500 mt-0.5">Bem-vindo de volta</p>
					</div>
				</header>

				<main className="flex-1 p-6 space-y-6">

					{erro && (
						<div className="rounded-md border border-red-500/40 bg-red-500/10 px-3 py-2 text-xs text-red-500">
							{erro}
						</div>
					)}

					{/* KPI Cards */}
					<div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">

						{/* Receita Total — dados reais (pagamentos com status PAID) */}
						<Card className="bg-white dark:bg-zinc-900 border-zinc-200 dark:border-zinc-800">
							<CardHeader className="flex flex-row items-center justify-between pb-2 pt-4 px-5">
								<CardTitle className="text-xs font-medium text-zinc-600 dark:text-zinc-400 uppercase">Receita Total</CardTitle>
								<CircleDollarSign className="h-4 w-4 text-emerald-500" />
							</CardHeader>
							<CardContent className="px-5 pb-4">
								<p className="text-2xl font-bold text-black dark:text-white">
									{carregando ? "—" : brl.format(receitaTotal)}
								</p>
								<p className="text-xs text-zinc-600 dark:text-zinc-500 mt-1">
									{carregando ? "Carregando..." : `${pagos.length} pagamento${pagos.length !== 1 ? "s" : ""} recebido${pagos.length !== 1 ? "s" : ""}`}
								</p>
							</CardContent>
						</Card>

						{/* A Receber — MOCK (pendente de backend) */}
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

						{/* Atrasados — MOCK (pendente de backend) */}
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

						{/* Clientes Ativos — dados reais (/api/students?active=true) */}
						<Card className="bg-white dark:bg-zinc-900 border-zinc-200 dark:border-zinc-800">
							<CardHeader className="flex flex-row items-center justify-between pb-2 pt-4 px-5">
								<CardTitle className="text-xs font-medium text-zinc-600 dark:text-zinc-400 uppercase">Clientes Ativos</CardTitle>
								<Users className="h-4 w-4 text-blue-500" />
							</CardHeader>
							<CardContent className="px-5 pb-4">
								<p className="text-2xl font-bold text-black dark:text-white">
									{carregando ? "—" : clientesAtivos}
								</p>
								<p className="text-xs text-zinc-600 dark:text-zinc-500 mt-1">Base ativa</p>
							</CardContent>
						</Card>

					</div>

					{/* Tabela — MOCK (pendente de backend: pagamento com nome de aluno/plano) */}
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
