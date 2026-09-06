import { useEffect, useState } from 'react'
import { Button, Card, Input } from 'antd'
import { motion, AnimatePresence } from 'framer-motion'
import { post } from '@/api/http'
import { RobotIcon, AskScene } from '@/components/illustrations'
import { staggerContainer, fadeUp } from '@/components/motion'

interface AiAnswer {
  answer: string
  citations: { docId: number; docTitle: string; excerpt: string }[]
}

export function AskPage() {
  const [q, setQ] = useState('')
  const [loading, setLoading] = useState(false)
  const [answer, setAnswer] = useState<AiAnswer | null>(null)
  const [typed, setTyped] = useState('')
  const [done, setDone] = useState(false)

  // 流式打字机：模拟 SSE 逐字触达
  useEffect(() => {
    if (!answer) return
    const full = answer.answer
    setTyped('')
    setDone(false)
    let i = 0
    const iv = window.setInterval(() => {
      i += 1
      setTyped(full.slice(0, i))
      if (i >= full.length) {
        window.clearInterval(iv)
        setDone(true)
      }
    }, 16)
    return () => window.clearInterval(iv)
  }, [answer])

  const ask = async () => {
    if (!q.trim()) return
    setLoading(true)
    setAnswer(null)
    setDone(false)
    try {
      const data = await post<AiAnswer>('/v1/rag/ask', { question: q, scope: 'all' })
      setAnswer(data)
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card title="AI 溯源答疑" style={{ maxWidth: 780 }}>
      <div className="flex items-end gap-2">
        <div className="shrink-0 animate-soft-bounce">
          <RobotIcon size={44} />
        </div>
        <Input.TextArea
          autoSize={{ minRows: 2, maxRows: 5 }}
          value={q}
          onChange={(e) => setQ(e.target.value)}
          placeholder="输入考研问题，AI 结合你的知识库作答并溯源……"
          onPressEnter={(e) => { if (!e.shiftKey) { e.preventDefault(); ask() } }}
        />
        <Button type="primary" loading={loading} onClick={ask} className="press">
          提问
        </Button>
      </div>

      {!answer && !loading && (
        <div className="mt-6 flex flex-col items-center justify-center gap-2 py-6 text-center">
          <AskScene size={240} />
          <p className="font-semibold text-brand-600">把问题交给研王爷</p>
          <p className="text-xs text-gray-400">正在结合你的知识库作答并逐条溯源引用</p>
        </div>
      )}

      {answer && (
        <div className="mt-5 rounded-2xl bg-brand-50/40 p-4">
          {/* 流式答案 */}
          <p className="whitespace-pre-wrap text-sm leading-relaxed">
            {typed}
            {!done && <span className="ml-0.5 inline-block h-4 w-[2px] animate-blink bg-brand-500 align-middle" />}
          </p>

          {/* 引用溯源：done 后逐条淡入 */}
          <AnimatePresence>
            {done && answer.citations.length > 0 && (
              <motion.div
                variants={staggerContainer}
                initial="hidden"
                animate="show"
                className="mt-4 space-y-2"
              >
                <div className="text-xs font-medium text-gray-400">引用溯源</div>
                {answer.citations.map((c, i) => (
                  <motion.div key={i} variants={fadeUp} className="rounded-xl border border-brand-100 bg-white p-3 text-sm">
                    <span className="inline-flex items-center gap-1 font-semibold text-brand-600">
                      <span className="flex h-5 w-5 items-center justify-center rounded-full bg-brand-50 text-xs">{i + 1}</span>
                      {c.docTitle}
                    </span>
                    <p className="mt-1 text-gray-600">{c.excerpt}</p>
                  </motion.div>
                ))}
              </motion.div>
            )}
          </AnimatePresence>

          {done && answer.citations.length === 0 && (
            <div className="mt-3 text-xs text-gray-400">
              （当前为 Mock 模式，接入 RAG + 向量库后展示溯源引用）
            </div>
          )}
        </div>
      )}
    </Card>
  )
}