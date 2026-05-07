"use client"

import { useEffect, useRef, useState } from "react"
import { usePathname, useRouter } from "next/navigation"

const INACTIVITY_LIMIT_MS = 60 * 60 * 1000
const PUBLIC_PATHS = new Set(["/login"])

export function AuthGuard({ children }: { children: React.ReactNode }) {
  const router = useRouter()
  const pathname = usePathname()
  const [isChecking, setIsChecking] = useState(true)
  const inactivityTimerRef = useRef<number | null>(null)

  const startInactivityTimer = () => {
    if (inactivityTimerRef.current) {
      window.clearTimeout(inactivityTimerRef.current)
    }

    inactivityTimerRef.current = window.setTimeout(() => {
      localStorage.removeItem("auth_token")
      router.replace("/login")
    }, INACTIVITY_LIMIT_MS)
  }

  useEffect(() => {
    const isPublic = PUBLIC_PATHS.has(pathname)
    const token = localStorage.getItem("auth_token")

    if (token) {
      startInactivityTimer()
      if (isPublic) {
        router.replace("/dashboard")
        return
      }
      setIsChecking(false)
      return
    }

    if (isPublic) {
      setIsChecking(false)
      return
    }

    router.replace("/login")
  }, [pathname, router])

  useEffect(() => {
    if (isChecking) {
      return
    }

    const events = ["mousemove", "mousedown", "keydown", "touchstart", "scroll"]

    const handleActivity = () => {
      startInactivityTimer()
    }

    events.forEach((eventName) => window.addEventListener(eventName, handleActivity))

    return () => {
      if (inactivityTimerRef.current) {
        window.clearTimeout(inactivityTimerRef.current)
      }
      events.forEach((eventName) => window.removeEventListener(eventName, handleActivity))
    }
  }, [isChecking])

  if (isChecking) {
    return null
  }

  return <>{children}</>
}
