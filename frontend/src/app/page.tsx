export default function Home() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-black">
      <div className="text-center space-y-6">
        <h1 className="text-5xl font-extrabold text-blue-400">
          Next + Tailwind 🚀
        </h1>

        <p className="text-gray-300 text-lg">
          Seu ambiente frontend está funcionando perfeitamente.
        </p>

        <button className="px-6 py-3 bg-blue-600 rounded-lg hover:bg-blue-700 transition">
          Começar Projeto
        </button>
      </div>
    </main>
  );
}