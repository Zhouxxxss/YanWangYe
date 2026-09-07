import { useState } from 'react'
import { Button, Card, Form, Input, Tabs, message } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { post } from '@/api/http'
import { useAuthStore } from '@/stores/auth'
import { StudyScene, GrowthIcon, StampIcon, TrophyIcon } from '@/components/illustrations'
import { AmbientBg } from '@/components/AmbientBg'
import { staggerContainer, fadeUp } from '@/components/motion'

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
          '/auth/login', v)
        setAuth(data)
        message.success('欢迎回来')
        nav('/', { replace: true })
      } else {
        await post('/auth/register', v)
        message.success('注册成功，请登录')
        setMode('login')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="relative min-h-screen overflow-hidden bg-[#f6f8fd]">
      <AmbientBg />
      <div className="relative z-10 flex min-h-screen items-center justify-center p-4">
        <motion.div
          variants={staggerContainer}
          initial="hidden"
          animate="show"
          className="grid w-full max-w-4xl overflow-hidden rounded-3xl bg-white shadow-[0_20px_50px_rgba(67,97,238,.16)] md:grid-cols-2"
        >
        {/* 左侧：教育风插画 + 品牌价值 */}
        <div className="relative hidden flex-col items-center justify-center gap-4 overflow-hidden bg-gradient-to-b from-brand-50 to-white px-8 py-10 md:flex">
          <motion.div variants={pop2} className="pointer-events-none absolute -right-10 -top-10 opacity-40 blur-2xl">
            <StudyScene size={260} />
          </motion.div>
          <motion.div variants={fadeUp}>
            <StudyScene size={300} className="relative z-10" />
          </motion.div>
          <motion.div variants={fadeUp} className="relative z-10 text-center">
            <h2 className="text-2xl font-black text-brand-600">研王爷</h2>
            <p className="mt-1 text-sm text-gray-500">你的考研伴学管家</p>
          </motion.div>
          <motion.div variants={fadeUp} className="relative z-10 mt-2 grid w-full grid-cols-3 gap-2">
            {[
              { icon: <GrowthIcon size={30} />, label: '计时复盘' },
              { icon: <StampIcon size={30} />, label: '打卡背诵' },
              { icon: <TrophyIcon size={30} />, label: '进步可视' },
            ].map((f) => (
              <div key={f.label} className="card-lift flex flex-col items-center gap-1 rounded-2xl bg-white/70 p-3 text-xs text-gray-600 shadow-sm">
                {f.icon}
                {f.label}
              </div>
            ))}
          </motion.div>
        </div>

        {/* 右侧：登录 / 注册表单 */}
        <motion.div variants={fadeUp} className="flex items-center justify-center p-8 md:p-10">
          <Card bordered={false} className="w-full max-w-sm" styles={{ body: { padding: 0 } }}>
            <div className="mb-1 hidden text-center md:hidden">
              <h1 className="text-2xl font-black text-brand-600">研王爷</h1>
              <p className="mb-4 text-sm text-gray-400">考研伴学系统</p>
            </div>
            <Tabs
              activeKey={mode}
              centered
              onChange={(k) => setMode(k as typeof mode)}
              items={[
                { key: 'login', label: '登录' },
                { key: 'register', label: '注册' },
              ]}
            />
            <Form onFinish={onSubmit} layout="vertical" requiredMark={false}>
              <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
                <Input prefix={<UserOutlined />} placeholder="用户名" size="large" className="press" />
              </Form.Item>
              <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
                <Input.Password prefix={<LockOutlined />} placeholder="密码" size="large" className="press" />
              </Form.Item>
              <Button
                type="primary"
                htmlType="submit"
                size="large"
                block
                loading={loading}
                className="press"
                style={{ boxShadow: '0 8px 20px rgba(67,97,238,.35)' }}
              >
                {mode === 'login' ? '登 录' : '注 册'}
              </Button>
            </Form>
          </Card>
        </motion.div>
      </motion.div>
      </div>
    </div>
  )
}

const pop2 = {
  hidden: { opacity: 0, scale: 1.05 },
  show: { opacity: 1, scale: 1, transition: { duration: 0.8, ease: 'easeOut' as const } },
}