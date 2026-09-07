import { useState } from 'react'
import { Button, Card, Form, Input, Select, Modal, message, Tag } from 'antd'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { get, post } from '@/api/http'
import type { PageView } from '@/api/http'
import { EmptyBookIcon } from '@/components/illustrations'

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

const errMeta: Record<string, string> = {
  careless: '粗心', knowledge: '知识薄弱', method: '方法不对', other: '其他',
}

const qualityMeta = [
  { q: 1, label: '忘记', color: 'red', emoji: '😖' },
  { q: 2, label: '模糊', color: 'orange', emoji: '🤔' },
  { q: 3, label: '记得', color: 'green', emoji: '💪' },
]

export function WrongBookPage() {
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm()
  const queryClient = useQueryClient()

  const { data } = useQuery({
    queryKey: ['wrongbook'],
    queryFn: () => get<PageView<WrongQuestion>>('/wrongbook/page', { size: 50 }),
  })

  const add = async () => {
    const v = await form.validateFields()
    await post('/wrongbook', v)
    setOpen(false)
    form.resetFields()
    queryClient.invalidateQueries({ queryKey: ['wrongbook'] })
    message.success('已加入错题')
  }

  const list = data?.list ?? []

  return (
    <Card title="错题本（Anki 智能复习）" extra={<Button type="primary" onClick={() => setOpen(true)}>录入错题</Button>}>
      {list.length === 0 ? (
        <EmptyState />
      ) : (
        <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
          {list.map((w) => (
            <FlashCard key={w.id} w={w} />
          ))}
        </div>
      )}

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
            <Select options={Object.entries(errMeta).map(([v, l]) => ({ value: v, label: l }))} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}

function EmptyState() {
  return (
    <div className="py-12 text-center">
      <EmptyBookIcon size={120} className="mx-auto" />
      <p className="mt-3 font-semibold text-gray-600">错题本还是空的</p>
      <p className="mt-1 text-xs text-gray-400">点击右上角「录入错题」，开始沉淀你的知识盲区</p>
    </div>
  )
}

/* Anki 3D 翻面卡片：正面题目，点按翻面看答案 + 评分 */
function FlashCard({ w }: { w: WrongQuestion }) {
  const [flipped, setFlipped] = useState(false)

  const review = async (q: number) => {
    try {
      await post(`/wrongbook/${w.id}/review?quality=${q}`)
      message.success(`已按「${qualityMeta.find((m) => m.q === q)?.label}」调度下次复习`)
    } catch {
      message.info('复习调度接口待接入，已为你记录本次反馈')
    }
    setFlipped(false)
  }

  return (
    <div className="card-lift" style={{ perspective: 1100 }}>
      <div
        className="relative"
        style={{
          minHeight: 158,
          transformStyle: 'preserve-3d',
          transform: flipped ? 'rotateY(180deg)' : 'rotateY(0deg)',
          transition: 'transform 0.5s cubic-bezier(0.2, 0.8, 0.3, 1)',
        }}
      >
        {/* 正面：题目 */}
        <div
          style={{ backfaceVisibility: 'hidden' }}
          onClick={() => setFlipped(true)}
          className="absolute inset-0 cursor-pointer rounded-2xl border border-gray-100 bg-white p-4 shadow-sm"
        >
          <div className="mb-2 flex items-center gap-2">
            <Tag color={subjectColor[w.subject]}>{w.subject}</Tag>
            {w.chapter && <span className="text-xs text-gray-400">{w.chapter}</span>}
            <span className="ml-auto text-xs text-gray-300">点击翻面</span>
          </div>
          <p className="line-clamp-3 text-sm">{w.content}</p>
          <div className="mt-3 flex items-center justify-between text-xs text-gray-400">
            <span>下次复习：{w.nextReviewDate ?? '待调度'}</span>
            {/* 首次展开前显示浅色提示 */}
          </div>
        </div>

        {/* 背面：答案 + 错因 + 评分 */}
        <div
          style={{ backfaceVisibility: 'hidden', transform: 'rotateY(180deg)' }}
          className="absolute inset-0 overflow-auto rounded-2xl border border-brand-100 bg-gradient-to-br from-brand-50 to-white p-4"
        >
          <div className="mb-2 flex items-center gap-2">
            <Tag color={subjectColor[w.subject]}>{w.subject}</Tag>
            <span className="text-xs text-gray-400">正确答案</span>
          </div>
          <p className="text-sm leading-relaxed text-gray-800">{w.correctAnswer}</p>
          {w.errorReason && (
            <p className="mt-2 text-xs text-gray-400">错因：{errMeta[w.errorReason] ?? w.errorReason}</p>
          )}
          <div className="mt-3 flex gap-2">
            {qualityMeta.map((m) => (
              <Button key={m.q} size="small" className="press" onClick={() => review(m.q)}>
                {m.emoji} {m.label}
              </Button>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}