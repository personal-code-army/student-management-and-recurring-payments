import { AlertTriangle } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Dialog, DialogClose, DialogContent } from "@/components/ui/dialog"
import { cn } from "@/lib/utils"
import { type ApiErrorInfo, resolveApiErrorContent } from "@/lib/api-errors"

type ApiErrorScreenProps = {
  error: ApiErrorInfo | null
  onRetry?: () => void
  onClose?: () => void
  className?: string
  showCode?: boolean
}

export function ApiErrorScreen({
  error,
  onRetry,
  onClose,
  className,
  showCode = true,
}: ApiErrorScreenProps) {
  if (!error) return null
  const content = resolveApiErrorContent(error)
  const detail =
    content.detail && content.detail !== content.description ? content.detail : null

  return (
    <Dialog
      open={Boolean(error)}
      onOpenChange={(open) => {
        if (!open) {
          onClose?.()
        }
      }}
    >
      <DialogContent
        className={cn(
          "rounded-2xl border border-[#FFFFFF]/15 bg-[#020203] text-center",
          className
        )}
      >
        <div className="space-y-3">
          <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-[#DD050A]/15 text-[#DD050A]">
            <AlertTriangle className="h-5 w-5" />
          </div>
          <h2 className="text-lg font-semibold text-[#FFFFFF]">{content.title}</h2>
          <p className="text-sm text-[#FFFFFF]/60">{content.description}</p>
          {detail ? (
            <p className="text-sm text-[#FFFFFF]/80">{detail}</p>
          ) : null}
          {showCode && error.code ? (
            <p className="text-xs text-[#FFFFFF]/40">Codigo: {error.code}</p>
          ) : null}
          <div className="mt-4 flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-center">
            {onRetry ? (
              <Button onClick={onRetry} className="bg-[#DD050A] text-[#FFFFFF] hover:bg-[#DD050A]/90">
                Tentar novamente
              </Button>
            ) : null}
            <DialogClose asChild>
              <Button
                variant="outline"
                className="border-[#FFFFFF]/15 bg-transparent text-[#FFFFFF]/80 hover:bg-[#FFFFFF]/10"
              >
                Fechar
              </Button>
            </DialogClose>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
