import { Navigate, Outlet } from 'react-router-dom'
import { useAuthStore } from '@/stores/auth'

/** 路由守卫：未登录跳登录页。 */
export function RequireAuth() {
  const token = useAuthStore((s) => s.token)
  if (!token) return <Navigate to="/login" replace />
  return <Outlet />
}