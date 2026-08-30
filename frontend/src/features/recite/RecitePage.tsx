import { useState } from 'react'
import { Button, Card, Form, Input, Select, Modal, message, Tag } from 'antd'
import { useQuery } from '@tanstack/react-query'
import { get, post } from '@/api/http'
import type { PageView } from '@/api/http'

interface ReciteCard { id: number; subject: string; chapter?: string; content: string; status: string }

export function RecitePage() {
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm()
  const { data } = useQuery({
    queryKey: ['recite'],
    queryFn: () => get<PageView<ReciteCard>>('/v1/recite/page', { size: 50 }),
  })

  const add = async () => {
    const v = await form.validateFields()
    await post('/v1/recite', v)
    setOpen(false)
    form.resetFields()
    message.success('卡片已添加')
  }

  return (
    <Card title="背诵打卡（SM-2 间隔重复）" extra={<Button type="primary" onClick={() => setOpen(true)}>新增卡片</Button>}>
      <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
        {data?.list.map((c) => (
          <div key={c.id} className="rounded-xl border border-gray-100 bg-white p-4 shadow-sm">
            <div className="mb-2 flex items-center gap-2">
              <Tag color={c.subject === 'politics' ? 'red' : 'purple'}>{c.subject}</Tag>
              {c.status === 'MASTERED' && <Tag color="green">已掌握</Tag>}
            </div>
            <p className="text-sm">{c.content}</p>
            <div className="mt-3 flex gap-2">
              {[1, 3, 5].map((q) => (
                <Button key={q} size="small" onClick={() => recite(c.id, q)}>评分 {q}</Button>
              ))}
            </div>
          </div>
        ))}
      </div>

      <Modal open={open} title="新增背诵卡片" onOk={add} onCancel={() => setOpen(false)} destroyOnClose>
        <Form form={form} layout="vertical">
          <Form.Item name="subject" label="科目" rules={[{ required: true }]}>
            <Select options={['politics', 'major'].map((s) => ({ value: s, label: s }))} />
          </Form.Item>
          <Form.Item name="chapter" label="章节"><Input /></Form.Item>
          <Form.Item name="content" label="背诵内容" rules={[{ required: true }]}><Input.TextArea /></Form.Item>
        </Form>
      </Modal>
    </Card>
  )

  async function recite(id: number, quality: number) {
    await post(`/v1/recite/${id}/recite?quality=${quality}`)
    message.success('打卡成功')
  }
}