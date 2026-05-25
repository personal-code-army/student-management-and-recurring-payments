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
			<div className="flex flex-col flex-1 min-h-screen bg-[#000000] text-[#FFFFFF]">
				<header className="flex items-center gap-3 px-6 py-4 border-b border-[#FFFFFF]/10 bg-[#020203]/90 backdrop-blur sticky top-0 z-10">
					<SidebarTrigger className="text-[#FFFFFF]/70 hover:text-[#FFFFFF]" />
					<Separator orientation="vertical" className="h-5 bg-[#FFFFFF]/20" />
					<div>
						<h1 className="text-sm font-semibold text-[#FFFFFF] leading-none">Planos</h1>
						<p className="text-xs text-[#FFFFFF]/60 mt-0.5">Gerencie os planos disponiveis</p>
					</div>
				</header>

				<main className="flex-1 p-6 space-y-6">
					<div className="mx-auto w-full max-w-7xl">
						<div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
							{planos.map((plano) => (
								<Card
									key={plano.id}
									className="group relative overflow-hidden border-[#FFFFFF]/12 bg-[#020203] transition-all duration-300 hover:border-[#DD050A]/50 hover:-translate-y-1 hover:shadow-lg hover:shadow-[#DD050A]/10 min-h-[560px]"
								>
									<div className="pointer-events-none absolute inset-0 opacity-0 transition-opacity duration-300 group-hover:opacity-100">
										<div className="absolute -top-24 left-1/2 h-48 w-48 -translate-x-1/2 rounded-full bg-[#DD050A]/12 blur-3xl" />
										<div className="absolute -bottom-24 right-6 h-48 w-48 rounded-full bg-[#DD050A]/8 blur-3xl" />
									</div>
									<CardHeader className="pb-3 pt-6 px-6">
										<div className="flex items-center justify-between">
											<CardTitle className="text-lg text-[#FFFFFF]">{plano.nome}</CardTitle>
											<Badge variant="outline" className="bg-[#DD050A]/15 text-[#DD050A] border-[#DD050A]/40">
												{plano.destaque}
											</Badge>
										</div>
										<CardDescription className="text-[#FFFFFF]/60 text-sm">Planos para evoluir na academia de lutas</CardDescription>
									</CardHeader>
									<CardContent className="px-6 pb-10 space-y-7">
										<div className="flex flex-col items-center gap-3 text-center">
											<span className="flex items-center justify-center w-10 h-10 rounded-md bg-[#DD050A]/12">
												<CircleDollarSign className="h-5 w-5 text-[#DD050A]" />
											</span>
											<div>
												<p className="text-3xl font-bold text-[#FFFFFF] group-hover:text-[#DD050A] transition-colors">
													{plano.preco}
												</p>
												<p className="text-xs text-[#FFFFFF]/50 uppercase tracking-wider">{plano.periodo}</p>
											</div>
										</div>

										<div className="space-y-2">
											<p className="text-xs text-[#FFFFFF]/50 uppercase tracking-wider">O que inclui</p>
											<ul className="space-y-2">
												{plano.beneficios.map((beneficio) => (
													<li key={beneficio} className="flex items-start gap-2 text-sm text-[#FFFFFF]/70">
														<span className="flex h-5 w-5 items-center justify-center rounded-full bg-[#DD050A]/12">
															<Check className="h-3 w-3 text-[#DD050A]" />
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