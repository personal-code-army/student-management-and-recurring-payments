import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/app-sidebar";
import { Separator } from "@/components/ui/separator";
import { Check, CircleDollarSign } from "lucide-react";

const planos = [
	{
		id: 1,
		nome: "Basico",
		preco: "R$ 150,00",
		periodo: "por mes",
		destaque: "Mais escolhido",
		beneficios: [
			"2 modalidades liberadas",
			"Aulas em horarios fixos",
			"Acesso ao tatame",
		],
	},
	{
		id: 2,
		nome: "Intermediario",
		preco: "R$ 400,00",
		periodo: "a cada 3 meses",
		destaque: "Melhor equilibrio",
		beneficios: [
			"3 modalidades liberadas",
			"Treinos livres aos sabados",
			"Acesso ao tatame",
			"Desconto no pacote",
		],
	},
	{
		id: 3,
		nome: "Premium",
		preco: "R$ 1.400,00",
		periodo: "por ano",
		destaque: "Maximo desempenho",
		beneficios: [
			"Todas as modalidades liberadas",
			"Treinos livres todos os dias",
			"Acesso ao tatame",
			"Avaliacao fisica trimestral",
			"Acompanhamento tecnico",
		],
	},
];

export default function PlanosPage() {
	return (
		<SidebarProvider>
			<AppSidebar />
			<div className="flex flex-col flex-1 min-h-screen bg-white dark:bg-zinc-950 text-black dark:text-white">

				{/* Header */}
				<header className="flex items-center gap-3 px-6 py-4 border-b border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-950 sticky top-0 z-10">
					<SidebarTrigger className="text-zinc-600 dark:text-zinc-400 hover:text-black dark:hover:text-white" />
					<Separator orientation="vertical" className="h-5 bg-zinc-300 dark:bg-zinc-700" />
					<div>
						<h1 className="text-sm font-semibold text-black dark:text-white leading-none">Planos</h1>
						<p className="text-xs text-zinc-600 dark:text-zinc-500 mt-0.5">
							Gerencie os planos disponíveis
						</p>
					</div>
				</header>

				<main className="flex-1 p-6 space-y-6">
					<div className="mx-auto w-full max-w-7xl">
						<div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

							{planos.map((plano) => (
								<Card
									key={plano.id}
									className="group relative overflow-hidden bg-white dark:bg-zinc-900 border-zinc-200 dark:border-zinc-800 transition-all duration-300 hover:border-emerald-500/50 hover:-translate-y-1 hover:shadow-lg hover:shadow-emerald-500/10 min-h-[560px]"
								>

									{/* efeito hover */}
									<div className="pointer-events-none absolute inset-0 opacity-0 transition-opacity duration-300 group-hover:opacity-100">
										<div className="absolute -top-24 left-1/2 h-48 w-48 -translate-x-1/2 rounded-full bg-emerald-500/10 blur-3xl" />
										<div className="absolute -bottom-24 right-6 h-48 w-48 rounded-full bg-emerald-500/5 blur-3xl" />
									</div>

									<CardHeader className="pb-3 pt-6 px-6">
										<div className="flex items-center justify-between">
											<CardTitle className="text-lg text-black dark:text-white">
												{plano.nome}
											</CardTitle>

											<Badge className="bg-zinc-100 dark:bg-zinc-800 text-zinc-700 dark:text-zinc-300 border-zinc-200 dark:border-zinc-700">
												{plano.destaque}
											</Badge>
										</div>

										<CardDescription className="text-zinc-600 dark:text-zinc-500 text-sm">
											Planos para evoluir na academia de lutas
										</CardDescription>
									</CardHeader>

									<CardContent className="px-6 pb-10 space-y-7">

										<div className="flex flex-col items-center gap-3 text-center">
											<span className="flex items-center justify-center w-10 h-10 rounded-md bg-emerald-500/10">
												<CircleDollarSign className="h-5 w-5 text-emerald-500" />
											</span>

											<div>
												<p className="text-3xl font-bold text-black dark:text-white group-hover:text-emerald-400 transition-colors">
													{plano.preco}
												</p>
												<p className="text-xs text-zinc-600 dark:text-zinc-500 uppercase tracking-wider">
													{plano.periodo}
												</p>
											</div>
										</div>

										<div className="space-y-2">
											<p className="text-xs text-zinc-600 dark:text-zinc-500 uppercase tracking-wider">
												O que inclui
											</p>

											<ul className="space-y-2">
												{plano.beneficios.map((beneficio) => (
													<li key={beneficio} className="flex items-start gap-2 text-sm text-zinc-700 dark:text-zinc-300">
														<span className="flex h-5 w-5 items-center justify-center rounded-full bg-emerald-500/10">
															<Check className="h-3 w-3 text-emerald-500" />
														</span>
														<span>{beneficio}</span>
													</li>
												))}
											</ul>
										</div>

									</CardContent>
								</Card>
							))}

						</div>
					</div>
				</main>
			</div>
		</SidebarProvider>
	);
}