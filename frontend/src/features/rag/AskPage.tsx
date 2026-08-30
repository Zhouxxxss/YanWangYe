import { useState } from 'react'
import { Button, Card, Input } from 'antd'
import { motion } from 'framer-motion'
import { post } from '@/api/http'

interface AiAnswer { answer: string; citations: { docId: number; docTitle: string; excerpt: string }[] }

export function AskPage() {
  const [q, setQ] = useState('')
  const [loading, setLoading] = useState(false)
  const [answer, setAnswer] = useState<AiAnswer | null>(null)

  const ask = async () => {
    if (!q.trim()) return
    setLoading(true)
    try {
      const data = await post<AiAnswer>('/v1/rag/ask', { question: q, scope: 'all' })
      setAnswer(data)
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card title="AI 溯源答疑" style={{ maxWidth: 760 }}>
      <div className="flex gap-2">
        <Input.TextArea
          autoSize={{ minRows: 2, maxRows: 5 }}
          value={q}
          onChange={(e) => setQ(e.target.value)}
          placeholder="输入考研问题，AI 结合你的知识库作答并溯源……"
        />
        <Button type="primary" loading={loading} onClick={ask} style={{ alignSelf: 'flex-end' }}>
          提问
        </Button>
      </div>

      {answer && (
        <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} className="mt-5 whitespace-pre-wrap">
          <p className="text-sm leading-relaxed">{answer.answer}</p>
          {answer.citations.length > 0 && (
            <div className="mt-4 space-y-2">
              <div className="text-xs text-gray-400">引用溯源</div>
              {answer.citations.map((c, i) => (
                <div key={i} className="rounded-lg border border-brand-50 bg-brand-50/50 p-3 text-sm">
                  <span className="font-semibold text-brand-600">{c.docTitle}</span>
                  <p className="mt-1 text-gray-600">{c.excerpt}</p>
                </div>
              ))}
            </div>
          )}
          {answer.citations.length === 0 && (
            <div className="mt-3 text-xs text-gray-400">（当前为 Mock 模式，接入 RAG + 向量库后展示溯源引用）</div>
          )}
        </motion.div>
      )}
      {/* 强交互增强预留：SSE 流式打字机效果接入真实 Provider 后替换整体渲染 */}
    </Card>
  )
}