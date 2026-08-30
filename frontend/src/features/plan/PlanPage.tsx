import { useState } from 'react'
import { Button, Card, List, message } from 'antd'
import { useQuery } from '@tanstack/react-query'
import { get, post } from '@/api/http'

export function PlanPage() {
  const { data } = useQuery({ queryKey: ['plan'], queryFn: () => get<string[]>('/v1/plan/templates') })
  const [importing, setImporting] = useState<string | null>(null)

  const doImport = async (id: string) => {
    setImporting(id)
    try {
      const r = await post<{ generated: number }>(`/v1/plan/import?templateId=${id}`)
      message.success(`已导入日程 ${r.generated} 条`)
    } finally {
      setImporting(null)
    }
  }

  return (
    <Card title="计划模板导入（规划 → 日程）">
      <List
        dataSource={data ?? []}
        renderItem={(id) => (
          <List.Item
            actions={[
              <Button key="i" type="primary" size="small" loading={importing === id} onClick={() => doImport(id)}>
                一键导入
              </Button>,
            ]}
          >
            <List.Item.Meta title={id} description="基础/强化/冲刺阶段复习日程模板" />
          </List.Item>
        )}
      />
      <div className="mt-3 text-xs text-gray-400">
        导入后批量生成 schedule_task 日程；完成数据回到学习时长 → 大盘统计形成闭环。
      </div>
    </Card>
  )
}