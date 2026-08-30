import { create } from 'zustand'
import { clearToken, saveToken } from '@/api/http'

export interface AuthState {
  token: string | null
  userId: number | null
  username: string | null
  role: string | null
  setAuth: (a: {
    token: string
    userId: number
    username: string
    role: string
  }) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  token: localStorage.getItem('yanyan_token'),
  userId: null,
  username: null,
  role: null,

  setAuth: ({ token, userId, username, role }) => {
    saveToken(token)
    set({ token, userId, username, role })
  },

  logout: () => {
    clearToken()
    set({ token: null, userId: null, username: null, role: null })
  },
}))