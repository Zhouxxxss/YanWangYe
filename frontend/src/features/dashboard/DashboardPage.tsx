import type { ReactNode } from 'react'
import { Card, Row, Col, Segmented } from 'antd'
import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import ReactECharts from 'echarts-for-react'
import {
  ClockCircleOutlined, CalendarOutlined, TrophyOutlined, EditOutlined, FireOutlined,
} from '@ant-design/icons'
import { get } from '@/api/http'
import { GoalScene } from '@/components/illustrations'
import { staggerContainer, fadeUp, useCountUp, fmtDuration } from '@/components/motion'

interface Summary {
  studySeconds: number
  checkinDays: number
  taskDoneRate: number
  wrongReviewDone: number
  reciteDone: number
  heatmap: Record<string, number>
}

export function DashboardPage() {
  const [period, setPeriod] = useState<'week' | 'month'>('week')
  const { data } = useQuery({
    queryKey: ['dashboard', period],
    queryFn: () => get<Summary>('/dashboard/summary', { period }),
  })

  const study = data?.studySeconds ?? 0
  const shareOption = {
    tooltip: {},
    color: ['#4361ee', '#ff9f43', '#22c55e'],
    series: [{
      type: 'pie',
      radius: ['42%', '70%'],
      center: ['50%', '50%'],
      data: [
        { value: study, name: '总时长' },
      ],
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      animationType: 'scale',
      animationEasing: 'elasticOut',
      label: { color: '#6b7280', formatter: '{b}: {d}%' },
    }],
  }

  return (
    <motion.div variants={staggerContainer} initial="hidden" animate="show" className="space-y-5">
      <Card
        title="数据大盘"
        extra={<Segmented options={[{ value: 'week', label: '周' }, { value: 'month', label: '月' }]} value={period} onChange={(v) => setPeriod(v as typeof period)} />}
      >
        <Row gutter={[16, 16]}>
          <Col xs={12} md={5}><StatCard title="本周学习" value={study} format={(n) => fmtDuration(Math.round(n))} icon={<ClockCircleOutlined />} accent="#4361ee" /></Col>
          <Col xs={12} md={5}><StatCard title="打卡天数" value={data?.checkinDays ?? 0} icon={<CalendarOutlined />} accent="#22c55e" /></Col>
          <Col xs={12} md={5}><StatCard title="任务完成率" value={(data?.taskDoneRate ?? 0) * 100} format={(n) => `${Math.round(n)}%`} icon={<TrophyOutlined />} accent="#ff9f43" /></Col>
          <Col xs={12} md={4}><StatCard title="错题复习" value={data?.wrongReviewDone ?? 0} icon={<EditOutlined />} accent="#7c3aed" /></Col>
          <Col xs={12} md={5}><StatCard title="背诵打卡" value={data?.reciteDone ?? 0} icon={<FireOutlined />} accent="#ef4444" /></Col>
        </Row>

        <div className="mt-6 grid gap-4 md:grid-cols-5">
          <div className="md:col-span-3">
            <ReactECharts option={shareOption} style={{ height: 280 }} />
          </div>
          <div className="flex flex-col items-center justify-center rounded-2xl bg-gradient-to-b from-brand-50 to-white p-4 text-center">
            <GoalScene size={190} />
            <p className="mt-2 font-semibold text-brand-600">让进步可视化</p>
            <p className="mt-1 text-xs text-gray-400">热力图与科目占比<br />接入 study_stat_daily 预聚合后展示</p>
          </div>
        </div>
      </Card>
    </motion.div>
  )
}

function StatCard({
  title, value, format, icon, accent,
}: {
  title: string
  value: number
  format?: (n: number) => string
  icon: ReactNode
  accent: string
}) {
  const v = useCountUp(value, 900)
  return (
    <motion.div variants={fadeUp}>
      <div className="card-lift rounded-2xl border border-gray-100 bg-white p-4 shadow-sm">
        <div className="flex items-center gap-1.5 text-xs text-gray-400">
          <span style={{ color: accent }}>{icon}</span>
          <span>{title}</span>
        </div>
        <div className="mt-2 text-2xl font-black tabular-nums" style={{ color: accent }}>
          {format ? format(v) : Math.round(v)}
        </div>
      </div>
    </motion.div>
  )
}