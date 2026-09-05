import { useState } from 'react'
import { Button, Card, message } from 'antd'
import { useQuery } from '@tanstack/react-query'
import { get, post } from '@/api/http'
import { CalendarCheckIcon } from '@/components/illustrations'

const phases = [
  { label: '基础', emoji: '🌱', bg: '#eef4ff', fg: '#4361ee' },
  { label: '强化', emoji: '🚀', bg: '#f5f0ff', fg: '#7c3aed' },
  { label: '冲刺', emoji: '🔥', bg: '#fff7ed', fg: '#f0841f' },
]

export function PlanPage() {
  const { data } = useQuery({ queryKey: ['plan'], queryFn: () => get<string[]>('/v1/plan/templates') })
  const [importing, setImporting] = useState<string | null>(null)
  const [doneId, setDoneId] = useState<string | null>(null)

  const doImport = async (id: string) => {
    setImporting(id)
    try {
      const r = await post<{ generated: number }>(`/v1/plan/import?templateId=${id}`)
      message.success(`已导入日程 ${r.generated} 条`)
      setDoneId(id)
    } finally {
      setImporting(null)
    }
  }

  const list = data ?? []

  return (
    <Card title="计划模板导入（规划 → 日程）">
      {list.length === 0 ? (
        <div className="py-12 text-center">
          <CalendarCheckIcon size={120} className="mx-auto" />
          <p className="mt-3 font-semibold text-gray-600">暂无可用模板</p>
          <p className="mt-1 text-xs text-gray-400">模板就绪后将在这里供你一键导入复习日程</p>
        </div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {list.map((id, i) => (
            <TemplateCard
              key={id}
              id={id}
              phase={phases[i % phases.length]}
              importing={importing === id}
              done={doneId === id}
              onImport={() => doImport(id)}
            />
          ))}
        </div>
      )}
      <div className="mt-4 rounded-xl bg-brand-50/50 p-3 text-xs text-gray-500">
        导入后批量生成 schedule_task 日程；完成数据回到学习时长 → 大盘统计形成闭环。
      </div>
    </Card>
  )
}

function TemplateCard({
  id, phase, importing, done, onImport,
}: {
  id: string
  phase: { label: string; emoji: string; bg: string; fg: string }
  importing: boolean
  done: boolean
  onImport: () => void
}) {
  return (
    <div className="card-lift flex flex-col rounded-2xl border border-gray-100 bg-white p-4 shadow-sm">
      <div className="mb-2 flex items-center gap-2">
        <span className="rounded-full px-2.5 py-0.5 text-xs font-semibold" style={{ background: phase.bg, color: phase.fg }}>
          {phase.emoji} {phase.label}阶段
        </span>
        <span className="text-sm font-semibold text-gray-700">{id}</span>
      </div>
      <p className="mb-3 flex-1 text-xs text-gray-400">覆盖【复习 → 打卡】的日度数化日程，导入即生成 schedule_task</p>
      <Button
        type="primary"
        size="small"
        block
        loading={importing}
        disabled={done}
        onClick={onImport}
        className="press"
        style={done ? { background: '#22c55e', borderColor: '#22c55e' } : undefined}
      >
        {done ? '✓ 已导入' : '一键导入'}
      </Button>
    </div>
  )
}