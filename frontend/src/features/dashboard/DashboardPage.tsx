import { Card, Statistic, Row, Col, Segmented } from 'antd'
import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import ReactECharts from 'echarts-for-react'
import { get } from '@/api/http'

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
    queryFn: () => get<Summary>('/v1/dashboard/summary', { period }),
  })

  const shareOption = {
    tooltip: {},
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [ // TODO: 后端返回 subjectShare
        { value: (data?.studySeconds ?? 0), name: '总时长' },
      ],
      animationType: 'scale',
      animationEasing: 'elasticOut',
    }],
  }

  return (
    <div className="space-y-5">
      <Card title="数据大盘" extra={<Segmented options={[{ value: 'week', label: '周' }, { value: 'month', label: '月' }]} value={period} onChange={(v) => setPeriod(v as typeof period)} />}>
        <Row gutter={16}>
          <Col span={5}><Statistic title="本周学习" value={data?.studySeconds ?? 0} suffix="秒" /></Col>
          <Col span={5}><Statistic title="打卡天数" value={data?.checkinDays ?? 0} /></Col>
          <Col span={5}><Statistic title="任务完成率" value={((data?.taskDoneRate ?? 0) * 100).toFixed(0)} suffix="%" /></Col>
          <Col span={4}><Statistic title="错题复习" value={data?.wrongReviewDone ?? 0} /></Col>
          <Col span={5}><Statistic title="背诵打卡" value={data?.reciteDone ?? 0} /></Col>
        </Row>
        <div className="mt-6">
          <ReactECharts option={shareOption} style={{ height: 260 }} />
        </div>
      </Card>
      {/* 二期预聚合接通后：热力图逐格渐入 + 数字滚动（ECharts + motion） */}
      <div className="text-center text-xs text-gray-400">热力图与科目占比：接入 study_stat_daily 预聚合后展示</div>
    </div>
  )
}