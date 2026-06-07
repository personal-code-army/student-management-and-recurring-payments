import { Skeleton } from "@/components/ui/skeleton"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { SidebarProvider } from "@/components/ui/sidebar"
import { AppSidebar } from "@/components/app-sidebar"

export default function RecebimentosLoading() {
  return (
    <SidebarProvider>
      <AppSidebar />
      <div className="flex min-h-screen flex-1 flex-col bg-[#000000]">
        <header className="sticky top-0 z-10 flex items-center justify-between border-b border-[#FFFFFF]/10 bg-[#020203]/90 px-4 py-3 backdrop-blur sm:px-6 sm:py-4">
          <Skeleton className="h-8 w-36 bg-[#FFFFFF]/10" />
          <Skeleton className="h-8 w-36 bg-[#FFFFFF]/10" />
        </header>

        <main className="flex-1 space-y-5 p-4 sm:space-y-6 sm:p-6">
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
            {Array.from({ length: 3 }).map((_, i) => (
              <Card key={i} className="border-[#FFFFFF]/12 bg-[#020203]">
                <CardHeader className="px-4 pb-2 pt-4 sm:px-5">
                  <Skeleton className="h-3 w-28 bg-[#FFFFFF]/10" />
                </CardHeader>
                <CardContent className="px-4 pb-4 sm:px-5">
                  <Skeleton className="h-7 w-24 bg-[#FFFFFF]/10" />
                  <Skeleton className="mt-2 h-3 w-20 bg-[#FFFFFF]/8" />
                </CardContent>
              </Card>
            ))}
          </div>

          <Card className="border-[#FFFFFF]/12 bg-[#020203]">
            <CardHeader className="px-4 pb-3 pt-5 sm:px-5">
              <Skeleton className="h-4 w-36 bg-[#FFFFFF]/10" />
              <Skeleton className="mt-1.5 h-3 w-52 bg-[#FFFFFF]/8" />
            </CardHeader>
            <CardContent className="space-y-3 px-4 pb-5 sm:px-5">
              {Array.from({ length: 8 }).map((_, i) => (
                <div key={i} className="flex items-center gap-4 py-2">
                  <Skeleton className="h-3 w-10 bg-[#FFFFFF]/10" />
                  <Skeleton className="h-3 w-20 bg-[#FFFFFF]/10" />
                  <div className="flex-1">
                    <Skeleton className="h-3.5 w-36 bg-[#FFFFFF]/10" />
                  </div>
                  <Skeleton className="hidden h-3 w-24 bg-[#FFFFFF]/8 sm:block" />
                  <Skeleton className="hidden h-3 w-24 bg-[#FFFFFF]/8 md:block" />
                  <Skeleton className="hidden h-3 w-20 bg-[#FFFFFF]/8 lg:block" />
                  <Skeleton className="h-5 w-16 rounded-full bg-[#FFFFFF]/10" />
                  <Skeleton className="h-7 w-20 bg-[#FFFFFF]/8" />
                </div>
              ))}
            </CardContent>
          </Card>
        </main>
      </div>
    </SidebarProvider>
  )
}
