import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'node:path'

// 网关服务前缀（去掉 api/v1 后的统一入口映射）：路由到 8100 网关
const servicePrefixes = [
  '/auth', '/users', '/friends', '/wrongbook', '/recite', '/study',
  '/plan', '/dashboard', '/anki', '/rag', '/knowledge', '/community', '/shopping',
]
const proxy = Object.fromEntries(
  servicePrefixes.map((p) => [p, { target: 'http://localhost:8100', changeOrigin: true }]),
)

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
      '@features': path.resolve(__dirname, 'src/features'),
    },
  },
  server: {
    port: 3000,
    host: true,
    proxy,
  },
  build: {
    outDir: 'dist',
    chunkSizeWarningLimit: 1500,
  },
})