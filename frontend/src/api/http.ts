import axios, { AxiosError } from 'axios'
import { message } from 'antd'

export interface R<T = unknown> {
  code: number
  message: string
  data: T
}

export interface PageView<T> {
  list: T[]
  total: number
  current: number
  size: number
}

const TOKEN_KEY = 'yanyan_token'

export const http = axios.create({ baseURL: '/api', timeout: 15000 })

http.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

const jumpLogin = () => {
  localStorage.removeItem(TOKEN_KEY)
  if (!location.pathname.startsWith('/login')) location.href = '/login'
}

http.interceptors.response.use(
  (res) => {
    const body = res.data as R
    // 业务码非 0 视为业务失败
    if (body && typeof body.code === 'number' && body.code !== 0) {
      message.error(body.message || '请求失败')
      return Promise.reject(new Error(body.message))
    }
    return res
  },
  (err: AxiosError<R>) => {
    if (err.response?.status === 401) {
      jumpLogin()
    } else {
      message.error(err.response?.data?.message || err.message || '网络异常')
    }
    return Promise.reject(err)
  },
)

export const saveToken = (t: string) => localStorage.setItem(TOKEN_KEY, t)
export const clearToken = () => localStorage.removeItem(TOKEN_KEY)

/** 便捷 GET/POST：直接解包 data */
export const get = async <T,>(url: string, params?: object): Promise<T> => {
  const res = await http.get<R<T>>(url, { params })
  return res.data.data
}

export const post = async <T,>(url: string, body?: unknown): Promise<T> => {
  const res = await http.post<R<T>>(url, body)
  return res.data.data
}