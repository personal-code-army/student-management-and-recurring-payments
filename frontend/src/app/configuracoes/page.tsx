"use client";

import { useTheme } from "next-themes";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

export default function SettingsPage() {
  const { theme, setTheme } = useTheme();
  const router = useRouter();

  const [user, setUser] = useState<any>(null);

  useEffect(() => {
    async function fetchUser() {
      try {
        const res = await fetch("http://localhost:8080/api/user");
        const data = await res.json();
        setUser(data);
      } catch (error) {
        console.error(error);
      }
    }

    fetchUser();
  }, []);

  const isDark = theme === "dark";

  return (
    <div className="min-h-screen bg-white dark:bg-zinc-950 text-black dark:text-white p-6">
      
      {/* Container central */}
      <div className="max-w-4xl mx-auto space-y-6">

        {/* Título */}
        <div>
          <h1 className="text-2xl font-bold">Configurações</h1>
          <p className="text-zinc-600 dark:text-zinc-400 text-sm">
            Gerencie suas preferências e informações
          </p>
        </div>

        {/* Card Tema */}
        <Card className="bg-white dark:bg-zinc-900 border-zinc-200 dark:border-zinc-800">
          <CardHeader>
            <CardTitle>Tema</CardTitle>
          </CardHeader>
          <CardContent className="flex items-center justify-between">
            <p className="text-sm text-zinc-600 dark:text-zinc-400">
              Altere entre modo claro e escuro
            </p>
            <Button onClick={() => setTheme(isDark ? "light" : "dark")}>
              {isDark ? "Modo Claro" : "Modo Escuro"}
            </Button>
          </CardContent>
        </Card>

        {/* Card Usuário */}
        <Card className="bg-white dark:bg-zinc-900 border-zinc-200 dark:border-zinc-800">
          <CardHeader>
            <CardTitle>Dados do Usuário</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2">
            {user ? (
              <>
                <p><strong>Nome:</strong> {user.nome}</p>
                <p><strong>Email:</strong> {user.email}</p>
                <p><strong>Telefone:</strong> {user.telefone}</p>
              </>
            ) : (
              <p className="text-zinc-500">
                Backend não conectado ainda
              </p>
            )}
          </CardContent>
        </Card>

        {/* Botão voltar */}
        <div className="flex justify-end">
          <Button variant="outline" onClick={() => router.push("/dashboard")}>
            Voltar para o Dashboard
          </Button>
        </div>

      </div>
    </div>
  );
}