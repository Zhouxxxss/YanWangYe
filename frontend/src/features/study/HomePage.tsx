import { useEffect, useState } from 'react'
import { Button, Card, Select, Row, Col, message, Tag } from 'antd'
import { motion } from 'framer-motion'
import { http } from '@/api/http'
import { useAuthStore } from '@/stores/auth'
import { StudyScene, StampIcon } from '@/components/illustrations'
import { staggerContainer, fadeUp, fmtDuration } from '@/components/motion'

const TARGET_SEC = 10800 // 每日专注目标 3h（示例，未来可接用户配置）

export function HomePage() {
  const username = useAuthStore((s) => s.username)
  const [subject, setSubject] = useState('math')
  const [sessionId, setSessionId] = useState<number | null>(null)
  const [elapsed, setElapsed] = useState(0)
  const progress = Math.min(1, elapsed / TARGET_SEC)

  return (
    <motion.div variants={staggerContainer} initial="hidden" animate="show" className="space-y-5">
      <motion.div variants={fadeUp}>
        <div className="flex flex-wrap items-center gap-2">
          <h2 className="mr-2 text-xl font-bold">
            早上好，{username} <span className="text-brand-600">☀️</span>
          </h2>
          <Tag color="blue">今日进度 {Math.max(0, Math.round(progress * 100))}%</Tag>
          <Tag color="orange" icon={<StampIcon size={14} />}>新的一天，保持节奏</Tag>
        </div>
        <p className="mt-1 text-sm text-gray-400">今天也要高效备考，专注每一分钟。</p>
      </motion.div>

      <Row gutter={16}>
        {/* 学习计时（环形进度） */}
        <Col xs={24} md={14}>
          <motion.div variants={fadeUp}>
            <Card
              title="学习计时"
              extra={
                <Select value={subject} onChange={setSubject} style={{ width: 130 }}
                  options={['math', 'english', 'politics', 'major'].map((s) => ({ value: s, label: s }))} />
              }
              className="h-full"
            >
              <div className="flex flex-col items-center justify-center gap-6 sm:flex-row">
                <Ring size={200} stroke={14} radius={86} progress={progress} time={elapsed} />
                <div className="flex flex-col items-center gap-3 sm:items-start">
                  <p className="text-sm text-gray-400">当前科目：<b className="text-brand-600">{subject}</b></p>
                  {sessionId == null ? (
                    <Button type="primary" onClick={doStart} className="press"
                      style={{ boxShadow: '0 8px 18px rgba(67,97,238,.32)' }}>
                      开始专注
                    </Button>
                  ) : (
                    <Button danger onClick={doEnd} className="press">结束本次学习</Button>
                  )}
                  <p className="text-xs text-gray-400">今日目标 {Math.floor(TARGET_SEC / 3600)} 小时，环形随专注时长推进</p>
                </div>
              </div>
            </Card>
          </motion.div>
        </Col>

        {/* 学习概览（插画激励卡） */}
        <Col xs={24} md={10}>
          <motion.div variants={fadeUp}>
            <Card className="card-lift h-full">
              <div className="flex flex-col items-center justify-center py-2 text-center">
                <div className="animate-float">
                  <StudyScene size={190} />
                </div>
                <p className="mt-2 font-semibold text-brand-600">专注 · 坚持 · 上岸</p>
                <p className="mt-1 text-xs text-gray-400">
                  每次专注 25 分钟，休息 5 分钟；<br />错题及时复盘，让努力被看见。
                </p>
              </div>
            </Card>
          </motion.div>
        </Col>
      </Row>
    </motion.div>
  )

  async function doStart() {
    const res = await http.post<{ data: { sessionId: number } }>('/v1/study/session/start', null, {
      params: { subject },
    })
    setSessionId(res.data.data.sessionId)
    setElapsed(0)
    message.success('开始专注，加油！')
  }

  async function doEnd() {
    if (sessionId == null) return
    await http.post(`/v1/study/session/${sessionId}/end`)
    setSessionId(null)
    message.success('已记录本次学习')
  }

  useEffect(() => {
    if (sessionId == null) return
    // 简化实现：setInterval +1；完整版用 rAF + 时间戳差值避免漂移
    const t = window.setInterval(() => setElapsed((e) => e + 1), 1000)
    return () => window.clearInterval(t)
  }, [sessionId])
}

function Ring({
  size, stroke, radius, progress, time,
}: { size: number; stroke: number; radius: number; progress: number; time: number }) {
  const C = 2 * Math.PI * radius
  const offset = C * (1 - Math.min(1, Math.max(0, progress)))
  return (
    <div className="relative" style={{ width: size, height: size }}>
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`}>
        <g style={{ transform: 'rotate(-90deg)', transformOrigin: 'center' }}>
          <circle cx={size / 2} cy={size / 2} r={radius} fill="none" stroke="#eef0f4" strokeWidth={stroke} />
          <circle
            cx={size / 2} cy={size / 2} r={radius} fill="none"
            stroke="#4361ee" strokeWidth={stroke} strokeLinecap="round"
            strokeDasharray={C} strokeDashoffset={offset}
            style={{ transition: 'stroke-dashoffset 1s linear' }}
          />
        </g>
      </svg>
      <div className="absolute inset-0 flex flex-col items-center justify-center">
        <span className="text-2xl font-black text-brand-600 tabular-nums">{fmtDuration(time)}</span>
        <span className="text-xs text-gray-400">本次专注</span>
      </div>
    </div>
  )
}