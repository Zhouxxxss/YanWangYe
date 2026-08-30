import { useState } from 'react'
import { Button, Card, Form, Input, Tabs, message } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { post } from '@/api/http'
import { useAuthStore } from '@/stores/auth'

export function LoginPage() {
  const nav = useNavigate()
  const setAuth = useAuthStore((s) => s.setAuth)
  const [mode, setMode] = useState<'login' | 'register'>('login')
  const [loading, setLoading] = useState(false)

  const onSubmit = async (v: { username: string; password: string }) => {
    setLoading(true)
    try {
      if (mode === 'login') {
        const data = await post<{ token: string; userId: number; username: string; role: string }>(
          '/v1/auth/login', v)
        setAuth(data)
        message.success('欢迎回来')
        nav('/', { replace: true })
      } else {
        await post('/v1/auth/register', v)
        message.success('注册成功，请登录')
        setMode('login')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-brand-50 to-indigo-200 p-4">
      <MotionCard>
        <h1 className="mb-1 text-center text-2xl font-bold text-brand-600">研王爷</h1>
        <p className="mb-6 text-center text-sm text-gray-400">考研伴学系统</p>
        <Tabs
          activeKey={mode}
          centered
          onChange={(k) => setMode(k as typeof mode)}
          items={[
            { key: 'login', label: '登录' },
            { key: 'register', label: '注册' },
          ]}
        />
        <Form onFinish={onSubmit} layout="vertical">
          <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input prefix={<UserOutlined />} placeholder="用户名" size="large" />
          </Form.Item>
          <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password prefix={<LockOutlined />} placeholder="密码" size="large" />
          </Form.Item>
          <Button type="primary" htmlType="submit" size="large" block loading={loading}>
            {mode === 'login' ? '登 录' : '注 册'}
          </Button>
        </Form>
      </MotionCard>
    </div>
  )
}

import { motion } from 'framer-motion'
function MotionCard({ children }: { children: React.ReactNode }) {
  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.96 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ type: 'spring', stiffness: 200, damping: 20 }}
    >
      <Card style={{ width: 380, boxShadow: '0 10px 30px rgba(67,97,238,.15)' }}>{children}</Card>
    </motion.div>
  )
}