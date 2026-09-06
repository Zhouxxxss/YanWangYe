import { useState } from 'react'
import { Button, Card, Form, Input, Select, Modal, message, Tag } from 'antd'
import { useQuery } from '@tanstack/react-query'
import { motion, AnimatePresence } from 'framer-motion'
import { get, post } from '@/api/http'
import type { PageView } from '@/api/http'
import { FlipBookScene, SparkleIcon } from '@/components/illustrations'

interface ReciteCard { id: number; subject: string; chapter?: string; content: string; status: string }

const quanMeta = [
  { q: 1, label: '生疏', emoji: '😖', color: 'red' },
  { q: 3, label: '模糊', emoji: '🤔', color: 'orange' },
  { q: 5, label: '熟练', emoji: '💪', color: 'green' },
]

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

  const recite = async (id: number, quality: number) => {
    await post(`/v1/recite/${id}/recite?quality=${quality}`)
    message.success('打卡成功，继续保持！')
  }

  const list = data?.list ?? []

  return (
    <Card title="背诵打卡（SM-2 间隔重复）" extra={<Button type="primary" onClick={() => setOpen(true)}>新增卡片</Button>}>
      {list.length === 0 ? (
        <div className="py-12 text-center">
          <FlipBookScene size={210} className="mx-auto" />
          <p className="mt-3 font-semibold text-gray-600">还没有背诵卡片</p>
          <p className="mt-1 text-xs text-gray-400">添加政治、专业课知识点，用间隔重复对抗遗忘</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
          {list.map((c) => (
            <FlashRecite key={c.id} c={c} onRecite={recite} />
          ))}
        </div>
      )}

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
}

function FlashRecite({ c, onRecite }: { c: ReciteCard; onRecite: (id: number, q: number) => Promise<void> }) {
  const [burst, setBurst] = useState({ on: false, seed: 0 })

  const click = async (q: number) => {
    await onRecite(c.id, q)
    setBurst((b) => ({ on: true, seed: b.seed + 1 }))
    window.setTimeout(() => setBurst((b) => ({ ...b, on: false })), 700)
  }

  return (
    <div className="card-lift relative rounded-2xl border border-gray-100 bg-white p-4 shadow-sm">
      <AnimatePresence>
        {burst.on && (
          <motion.span
            key={burst.seed}
            initial={{ opacity: 0, scale: 0.4, y: 6 }}
            animate={{ opacity: 1, scale: 1, y: -20 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.55 }}
            className="pointer-events-none absolute -top-1 right-2"
          >
            <SparkleIcon size={28} />
          </motion.span>
        )}
      </AnimatePresence>

      <div className="mb-2 flex items-center gap-2">
        <Tag color={c.subject === 'politics' ? 'red' : 'purple'}>{c.subject}</Tag>
        {c.status === 'MASTERED' && <Tag color="green">已掌握</Tag>}
        {c.chapter && <span className="text-xs text-gray-400">{c.chapter}</span>}
      </div>
      <p className="min-h-[3rem] text-sm leading-relaxed">{c.content}</p>
      <div className="mt-3 flex items-center justify-between border-t border-gray-50 pt-3">
        <span className="text-xs text-gray-400">复习反馈</span>
        <div className="flex gap-2">
          {quanMeta.map((m) => (
            <Button key={m.q} size="small" className="press" onClick={() => click(m.q)}>
              {m.emoji} {m.label}
            </Button>
          ))}
        </div>
      </div>
    </div>
  )
}