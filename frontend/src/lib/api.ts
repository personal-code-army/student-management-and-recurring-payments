import axios from "axios"

export const BASE_URL =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080"

export const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
})

api.interceptors.request.use((config) => {
  if (typeof window === "undefined") {
    return config
  }

  const token = localStorage.getItem("auth_token")
  if (token) {
    config.headers = config.headers ?? {}
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const isAuthEndpoint = error.config?.url?.includes("/api/auth/")
    if (error.response?.status === 401 && !isAuthEndpoint && typeof window !== "undefined") {
      localStorage.removeItem("auth_token")
      window.location.href = "/login"
    }
    return Promise.reject(error)
  }
)
