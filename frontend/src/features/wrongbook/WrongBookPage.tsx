import { useState } from 'react'
import { Button, Card, Form, Input, Select, Modal, message, Tag } from 'antd'
import { useQuery } from '@tanstack/react-query'
import { get, post } from '@/api/http'
import type { PageView } from '@/api/http'

interface WrongQuestion {
  id: number
  subject: string
  chapter?: string
  content: string
  myAnswer?: string
  correctAnswer?: string
  errorReason?: string
  nextReviewDate?: string
}

const subjectColor: Record<string, string> = {
  math: 'blue', english: 'green', politics: 'red', major: 'purple',
}

export function WrongBookPage() {
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm()

  const { data } = useQuery({
    queryKey: ['wrongbook'],
    queryFn: () => get<PageView<WrongQuestion>>('/v1/wrongbook/page', { size: 50 }),
  })

  const add = async () => {
    const v = await form.validateFields()
    await post('/v1/wrongbook', v)
    setOpen(false)
    form.resetFields()
    message.success('已加入错题')
    queryClientRefresh()
  }

  return (
    <Card
      title="错题本（Anki 智能复习）"
      extra={<Button type="primary" onClick={() => setOpen(true)}>录入错题</Button>}
    >
      <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
        {data?.list.map((w) => (
          <FlashCard key={w.id} w={w} />
        ))}
      </div>

      <Modal open={open} title="录入错题" onOk={add} onCancel={() => setOpen(false)} destroyOnClose>
        <Form form={form} layout="vertical">
          <Form.Item name="subject" label="科目" rules={[{ required: true }]}>
            <Select options={['math', 'english', 'politics', 'major'].map((s) => ({ value: s, label: s }))} />
          </Form.Item>
          <Form.Item name="chapter" label="章节"><Input /></Form.Item>
          <Form.Item name="content" label="题目" rules={[{ required: true }]}><Input.TextArea /></Form.Item>
          <Form.Item name="myAnswer" label="我的答案"><Input.TextArea /></Form.Item>
          <Form.Item name="correctAnswer" label="正确答案" rules={[{ required: true }]}><Input.TextArea /></Form.Item>
          <Form.Item name="errorReason" label="错因">
            <Select options={[
              { value: 'careless', label: '粗心' }, { value: 'knowledge', label: '知识点薄弱' },
              { value: 'method', label: '方法不对' }, { value: 'other', label: '其他' },
            ]} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}

import { motion } from 'framer-motion'
// Anki 卡片 3D 翻面占位：复习评分按钮接入 /v1/wrongbook/{id}/review?quality=x
function FlashCard({ w }: { w: WrongQuestion }) {
  return (
    <motion.div whileHover={{ y: -4 }} className="rounded-xl border border-gray-100 bg-white p-4 shadow-sm">
      <div className="mb-2 flex items-center gap-2">
        <Tag color={subjectColor[w.subject]}>{w.subject}</Tag>
        {w.chapter && <span className="text-xs text-gray-400">{w.chapter}</span>}
      </div>
      <p className="text-sm">{w.content}</p>
      <div className="mt-3 flex items-center justify-between text-xs text-gray-400">
        <span>下次复习：{w.nextReviewDate ?? '待调度'}</span>
        <span className="text-brand-500">翻面评分 · 待接入</span>
      </div>
    </motion.div>
  )
}

function queryClientRefresh() {
  // TODO: 接入 queryClient.invalidateQueries 触发列表刷新
  window.location.reload()
}