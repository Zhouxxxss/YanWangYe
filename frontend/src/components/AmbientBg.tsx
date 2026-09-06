import { motion } from 'framer-motion'

/* 环境氛围背景：缓慢漂移的渐变光晕 + 淡噪点颗粒，让页面像一段流动的视频背景。
 * 使用 absolute + z-0 铺满父容器；调用方把内容包在 relative z-10 之上即可透出光晕。 */
export function AmbientBg() {
  return (
    <div className="pointer-events-none absolute inset-0 z-0 overflow-hidden" aria-hidden>
      {/* 主光晕 1：品牌蓝 */}
      <motion.div
        className="absolute -left-24 -top-24 h-[46vw] w-[46vw] rounded-full opacity-50 blur-3xl"
        style={{ background: 'radial-gradient(circle, #dbe7ff 0%, transparent 70%)' }}
        animate={{ x: [0, 40, 0], y: [0, 24, 0], scale: [1, 1.12, 1] }}
        transition={{ duration: 22, repeat: Infinity, ease: 'easeInOut' }}
      />
      {/* 主光晕 2：暖橙 */}
      <motion.div
        className="absolute -right-28 top-1/3 h-[38vw] w-[38vw] rounded-full opacity-45 blur-3xl"
        style={{ background: 'radial-gradient(circle, #ffedd3 0%, transparent 70%)' }}
        animate={{ x: [0, -50, 0], y: [0, -30, 0], scale: [1, 1.16, 1] }}
        transition={{ duration: 26, repeat: Infinity, ease: 'easeInOut' }}
      />
      {/* 主光晕 3：亮蓝 */}
      <motion.div
        className="absolute bottom-[-12vw] left-1/3 h-[36vw] w-[36vw] rounded-full opacity-40 blur-3xl"
        style={{ background: 'radial-gradient(circle, #bcd0ff 0%, transparent 70%)' }}
        animate={{ x: [0, 30, 0], y: [0, -26, 0], scale: [1, 1.1, 1] }}
        transition={{ duration: 30, repeat: Infinity, ease: 'easeInOut' }}
      />
      {/* 细颗粒噪点（胶片质感，极淡） */}
      <div
        className="absolute inset-0 opacity-[0.05]"
        style={{
          backgroundImage: 'radial-gradient(#4361ee 1px, transparent 1px)',
          backgroundSize: '22px 22px',
        }}
      />
    </div>
  )
}