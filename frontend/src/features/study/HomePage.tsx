import { useEffect, useState } from 'react'
import { Button, Card, Statistic, Select, Row, Col, message } from 'antd'
import { motion } from 'framer-motion'
import { http } from '@/api/http'
import { useAuthStore } from '@/stores/auth'

export function HomePage() {
  const username = useAuthStore((s) => s.username)
  const [subject, setSubject] = useState('math')
  const [sessionId, setSessionId] = useState<number | null>(null)
  // rAF 驱动倒计时，避免 setInterval 漂移
  const [elapsed, setElapsed] = useState(0)

  return (
    <div className="space-y-5">
      <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}>
        <h2 className="mb-1 text-xl font-bold">
          早上好，{username} <span className="text-brand-600">☀️</span>
        </h2>
        <p className="text-sm text-gray-400">今天也要高效备考。</p>
      </motion.div>

      {/* 学习计时（一期强交互：环形进度 / 沉浸模式） */}
      <Card title="学习计时" extra={<Select value={subject} onChange={setSubject} style={{ width: 120 }}
        options={['math', 'english', 'politics', 'major'].map((s) => ({ value: s, label: s }))} />}>
        <Row gutter={24} align="middle">
          <Col>
            <Statistic title="已学习时长" value={elapsed} suffix="秒" />
          </Col>
          <Col>
            {sessionId == null ? (
              <Button type="primary" onClick={doStart}>开始</Button>
            ) : (
              <Button danger onClick={doEnd}>结束(sessionId={sessionId})</Button>
            )}
          </Col>
        </Row>
      </Card>
    </div>
  )

  async function doStart() {
    const res = await http.post<{ data: { sessionId: number } }>('/v1/study/session/start', null, {
      params: { subject },
    })
    setSessionId(res.data.data.sessionId)
    setElapsed(0)
    message.success('开始学习')
  }

  async function doEnd() {
    if (sessionId == null) return
    await http.post(`/v1/study/session/${sessionId}/end`)
    setSessionId(null)
    message.success('已记录本次学习')
  }

  // 简化：使用定时器推进（完整版用 rAF + 时间戳差值避免漂移）
  useEffect(() => {
    if (sessionId == null) return
    const t = window.setInterval(() => setElapsed((e) => e + 1), 1000)
    return () => window.clearInterval(t)
  }, [sessionId])
}
export const noop = 0