import type { ReactNode, SVGProps } from 'react'
import { motion, type Transition, type Variants } from 'framer-motion'

/* ===== 扁平矢量教育风插画库（动态 · 视频风格） =====
 * 品牌蓝主色 + 橙色点缀；所有场景为“活的面”：内置漂浮/呼吸/脉冲循环，
 * 让页面像一段视频而非静态贴图。零网络依赖、可缩放、遵循 reduced-motion。

 * 设计语言（对齐目标视频）：
 * - 场景：白底 + 大圆盘 + 前景主体（书/机器人/奖杯…）+ 空气感点缀（星、光、粒子）
 * - 动效：主体持续漂浮(translateY)、光环呼吸(pulse)、微粒上浮(drift)，
 *   学习数据用“生长线/进度圈”表达进步感。
 */

const B = '#4361ee' // 品牌蓝
const B2 = '#7b8cff' // 亮蓝
const L = '#dbe7ff' // 浅蓝
const L2 = '#eef4ff' // 极浅蓝
const O = '#ff9f43' // 橙色点缀
const P = '#ffedd3' // 浅橙
const G = '#8a94a6' // 次要灰

type P = SVGProps<SVGSVGElement> & { size?: number | string }

function Svg({ size = 24, children, ...rest }: P & { children: ReactNode }) {
  return (
    <svg width={size} height={size} viewBox="0 0 120 120" fill="none" aria-hidden {...rest}>
      {children}
    </svg>
  )
}

/* ===== 通用循环动效 ===== */
const EASE = ['easeIn', 'easeOut', 'easeInOut', 'linear'] as const
const loop = (dur: number, delay = 0, ease: (typeof EASE)[number] = 'easeInOut'): Transition => ({
  duration: dur, delay, repeat: Infinity, repeatType: 'mirror', ease,
})

/* 连续上浮（主体漂浮感） */
export const floatLoop: Variants = {
  animate: { y: [0, -9, 0], transition: loop(3.6) },
}
/* 呼吸缩放（光环） */
export const breatheLoop: Variants = {
  animate: { scale: [1, 1.09, 1], opacity: [0.5, 0.9, 0.5], transition: loop(3.2) },
}
/* 微粒上浮 + 淡入淡出 */
export const driftLoop: Variants = {
  animate: { y: [0, -16, 0], opacity: [0, 0.85, 0], transition: loop(4.2) },
}
/* 旋转播种（绕所在中心旋转，需要 g + 点到中心） */
export const spinSlow: Variants = {
  animate: { rotate: [0, 24, 0], transition: loop(6) },
}
/* 书页翻动（扑克闪卡） */
export const flipLoop: Variants = {
  animate: { scaleX: [1, 0, 1], transition: loop(1.6) },
}

/* ============ 场景插画（登录 / 首页学习桌面）============ */
export function StudyScene({ size = 340, className }: { size?: number; className?: string }) {
  return (
    <svg width={size} height={size} viewBox="0 0 340 252" fill="none" className={className} aria-hidden>
      {/* 背景圆盘 */}
      <circle cx="170" cy="126" r="118" fill={L2} />
      <circle cx="170" cy="126" r="92" fill="#ffffff" stroke={L} strokeWidth="7" />

      {/* 生长线：成绩/进步（头部呼吸脉冲） */}
      <motion.polyline
        points="78,170 120,132 150,146 196,100 236,104 262,80"
        stroke={O} strokeWidth="7" strokeLinecap="round" strokeLinejoin="round"
      />
      <motion.g variants={breatheLoop} animate="animate" style={{ originX: 0.5, originY: 0.5, transformBox: 'fill-box' }}>
        <circle cx="262" cy="80" r="8" fill={O} />
        <circle cx="262" cy="80" r="15" fill={O} opacity=".25" />
      </motion.g>

      {/* 打开的书：持续上浮 */}
      <motion.g variants={floatLoop} animate="animate">
        <path d="M120 112 L170 128 L220 112 L220 168 L170 186 L120 168 Z" fill={B} />
        <path d="M120 112 L170 128 L170 186 L120 168 Z" fill={B2} />
        <path d="M170 128 L170 186 L220 168 L220 112 Z" fill={L} />
        <line x1="120" y1="126" x2="120" y2="166" stroke="#ffffff" strokeWidth="3" strokeLinecap="round" opacity=".55" />
        <line x1="170" y1="142" x2="170" y2="186" stroke="#8a94a6" strokeWidth="3" strokeLinecap="round" opacity=".45" />
        <path d="M188 130 L204 124 L200 141 Z" fill="#ffffff" />
      </motion.g>

      {/* 悬浮星星：轻旋转呼吸 */}
      <motion.path
        d="M286 44 l4 9 9 1-6.5 7 1.5 9.5-8-4.5-8 4.5 1.5-9.5-6.5-7 9-1Z"
        fill={O} variants={spinSlow} animate="animate" style={{ originX: 0.5, originY: 0.5, transformBox: 'fill-box' }}
      />
      <motion.path
        d="M64 52 l3.5 8 8.5 1-6 6.5 1.5 9-7.5-4.5-7.5 4.5 1.5-9-6-6.5 8.5-1Z"
        fill={B} variants={floatLoop} animate="animate" style={{ originX: 0.5, originY: 0.5, transformBox: 'fill-box' }}
      />
      {/* 漂浮粒子 */}
      <motion.g variants={driftLoop} animate="animate">
        <circle cx="52" cy="112" r="5" fill={L} />
      </motion.g>
      <motion.g variants={driftLoop} animate="animate">
        <circle cx="294" cy="150" r="6" fill={P} />
      </motion.g>
    </svg>
  )
}

/* 首页学习桌面：专注计时 + 目标光环 */
export function FocusScene({ size = 340, className }: { size?: number; className?: string }) {
  const cx = 170, cy = 126
  return (
    <svg width={size} height={size} viewBox="0 0 340 252" fill="none" className={className} aria-hidden>
      <circle cx={cx} cy={cy} r="112" fill={L2} />
      {/* 呼吸专注光环 */}
      <motion.circle cx={cx} cy={cy} r="88" fill="none" stroke={B} strokeWidth="7" variants={breatheLoop} animate="animate" style={{ originX: 0.5, originY: 0.5, transformBox: 'fill-box' }} />
      <circle cx={cx} cy={cy} r="74" fill="#ffffff" stroke={L} strokeWidth="7" />
      {/* 旋转的分针（专注流逝） */}
      <motion.g variants={spinSlow} animate="animate" style={{ originX: '170px', originY: '126px' }}>
        <line x1={cx} y1={cy} x2={cx} y2={cy - 56} stroke={O} strokeWidth="7" strokeLinecap="round" />
      </motion.g>
      <motion.g variants={floatLoop} animate="animate">
        <path d="M152 96 l8 16 18 2-13 12 3 18-16-8-16 8 3-18-13-12 18-2Z" fill={O} />
        <circle cx={cx} cy={cy} r="7" fill={B} />
      </motion.g>
      {/* 漂浮书本与粒子 */}
      <motion.g variants={floatLoop} animate="animate">
        <path d="M268 168 l12 10 14-2-2 14-12 10-12-10 2-14Z" fill={B} />
      </motion.g>
      <motion.g variants={driftLoop} animate="animate"><circle cx="82" cy="88" r="6" fill={P} /></motion.g>
      <motion.g variants={driftLoop} animate="animate"><circle cx="250" cy="70" r="5" fill={L} /></motion.g>
    </svg>
  )
}

/* 数据大盘：进步柱状图 + 奖杯 */
export function GoalScene({ size = 340, className }: { size?: number; className?: string }) {
  return (
    <svg width={size} height={size} viewBox="0 0 340 252" fill="none" className={className} aria-hidden>
      <circle cx="170" cy="126" r="112" fill={L2} />
      {/* 生长柱状图（逐根淡入） */}
      {[
        { x: 84, w: 22, h: 46 }, { x: 120, w: 22, h: 70 }, { x: 156, w: 22, h: 96 }, { x: 192, w: 22, h: 118 },
      ].map((bar, i) => (
        <motion.rect
          key={bar.x}
          x={bar.x} y={190 - bar.h} width={bar.w} height={bar.h} rx="8"
          fill={i === 3 ? O : B} opacity={i === 3 ? 1 : 0.55}
          initial={{ scaleY: 0, y: 190 }}
          animate={{ scaleY: 1, y: 0 }}
          style={{ originX: 0.5, originY: '190px', transformBox: 'fill-box' }}
          transition={{ delay: 0.15 * i, type: 'spring', stiffness: 200, damping: 18 }}
        />
      ))}
      <motion.g variants={floatLoop} animate="animate">
        {/* 奖杯 */}
        <path d="M252 74h44a22 22 0 0 1-8 18 22 22 0 0 1-28-4c3 6 10 10 18 10v28h-20v-14h-10Z" fill={B} />
        <path d="M260 86h28a14 14 0 0 0-14-14h-14Z" fill={B2} />
        <path d="M250 128h48" stroke={O} strokeWidth="8" strokeLinecap="round" />
        <path d="M258 116l3 6 6 1-4.5 5 .5 7-6-3-6 3 .5-7-4.5-5 6-1Z" fill="#ffffff" />
      </motion.g>
      <motion.g variants={driftLoop} animate="animate"><circle cx="70" cy="70" r="6" fill={P} /></motion.g>
      <motion.g variants={driftLoop} animate="animate"><circle cx="292" cy="150" r="5" fill={L} /></motion.g>
    </svg>
  )
}

/* AI 答疑：机器人 + 呼吸信号 + 打字点 */
export function AskScene({ size = 340, className }: { size?: number; className?: string }) {
  const cx = 170, cy = 150
  return (
    <svg width={size} height={size} viewBox="0 0 340 252" fill="none" className={className} aria-hidden>
      <circle cx="170" cy="126" r="112" fill={L2} />
      {/* 呼吸信号环 */}
      <motion.circle cx={cx} cy={cy} r="72" stroke={B} strokeWidth="6" variants={breatheLoop} animate="animate" style={{ originX: 0.5, originY: 0.5, transformBox: 'fill-box' }} />
      <circle cx={cx} cy={cy} r="60" fill="#ffffff" stroke={L} strokeWidth="6" />
      {/* 机器人头部 */}
      <motion.g variants={floatLoop} animate="animate">
        <rect x="140" y="118" width="60" height="54" rx="16" fill={B} />
        <rect x="152" y="140" width="36" height="7" rx="3.5" fill="#ffffff" />
        <motion.g variants={flipLoop} animate="animate" style={{ originX: '170px', originY: '158px' }} transition={loop(1.4)}>
          <circle cx="154" cy="134" r="5" fill="#ffffff" />
          <circle cx="184" cy="134" r="5" fill="#ffffff" />
          <circle cx="170" cy="162" r="6" fill={O} />
        </motion.g>
        <path d="M154 172l14-6 14 6Z" fill={B2} />
        <path d="M158 118l-8-14M182 118l8-14" stroke={B} strokeWidth="6" strokeLinecap="round" />
      </motion.g>
      {/* 流动数据胶囊 */}
      <motion.g variants={driftLoop} animate="animate">
        <circle cx="238" cy="104" r="9" fill={P} />
      </motion.g>
      <motion.g variants={driftLoop} animate="animate" transition={undefined as never}>
        <circle cx="102" cy="150" r="8" fill={L} />
      </motion.g>
      <motion.circle cx="132" cy="196" r="5" fill={O} opacity=".7" />
      <motion.circle cx="214" cy="196" r="5" fill={B} opacity=".5" />
    </svg>
  )
}

/* 背诵打卡：翻页书本（闪卡） */
export function FlipBookScene({ size = 340, className }: { size?: number; className?: string }) {
  return (
    <svg width={size} height={size} viewBox="0 0 340 252" fill="none" className={className} aria-hidden>
      <circle cx="170" cy="126" r="112" fill={L2} />
      {/* 翻开书 + 翻页动画 */}
      <motion.g variants={floatLoop} animate="animate">
        <path d="M120 110 L170 126 L220 110 L220 168 L170 186 L120 168 Z" fill={B} />
        <path d="M120 110 L170 126 L170 186 L120 168 Z" fill={B2} />
        <motion.g variants={flipLoop} animate="animate" style={{ originX: '170px', originY: '148px' }} transition={loop(1.8)}>
          <path d="M170 126 L220 110 L220 168 L170 186 Z" fill={L} />
        </motion.g>
        <path d="M188 128 L204 122 L200 139 Z" fill="#ffffff" />
        <line x1="170" y1="140" x2="170" y2="186" stroke="#8a94a6" strokeWidth="3" strokeLinecap="round" opacity=".45" />
      </motion.g>
      {/* 牢记弹星 */}
      <motion.path
        d="M286 58 l4 9 9 1-6.5 7 1.5 9.5-8-4.5-8 4.5 1.5-9.5-6.5-7 9-1Z"
        fill={O} variants={spinSlow} animate="animate" style={{ originX: 0.5, originY: 0.5, transformBox: 'fill-box' }}
      />
      <motion.g variants={driftLoop} animate="animate"><circle cx="66" cy="84" r="6" fill={P} /></motion.g>
      <motion.g variants={driftLoop} animate="animate"><circle cx="280" cy="178" r="5" fill={L} /></motion.g>
    </svg>
  )
}

/* 计划模板：日历签到 + 对勾生长 */
export function PlanScene({ size = 340, className }: { size?: number; className?: string }) {
  return (
    <svg width={size} height={size} viewBox="0 0 340 252" fill="none" className={className} aria-hidden>
      <circle cx="170" cy="126" r="112" fill={L2} />
      <motion.g variants={floatLoop} animate="animate">
        <rect x="120" y="84" width="100" height="86" rx="14" fill="#ffffff" stroke={B} strokeWidth="7" />
        <rect x="120" y="98" width="100" height="16" rx="6" fill={L} />
        <path d="M138 76v18M188 76v18" stroke={B} strokeWidth="6" strokeLinecap="round" />
        {/* 对勾依次生长 */}
        <motion.path
          d="M140 138 l10 10 26-24"
          stroke={O} strokeWidth="7" strokeLinecap="round" strokeLinejoin="round"
          fill="none"
          initial={{ pathLength: 0 }} animate={{ pathLength: 1 }}
          transition={{ delay: 0.4, duration: 0.6, ease: 'easeOut' }}
        />
        <motion.rect
          x="156" y="148" width="34" height="12" rx="6" fill={L2}
          initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 1 }}
        />
      </motion.g>
      <motion.g variants={driftLoop} animate="animate"><circle cx="86" cy="104" r="6" fill={P} /></motion.g>
      <motion.g variants={driftLoop} animate="animate"><circle cx="262" cy="150" r="5" fill={L} /></motion.g>
      <motion.circle cx="286" cy="70" r="5" fill={O} opacity=".7" />
    </svg>
  )
}

/* ============ 便捷图标（静态为主，用于小尺寸装饰）============ */
export function GrowthIcon(p: P) {
  return (
    <Svg {...p}>
      <circle cx="60" cy="60" r="50" fill="#eef4ff" />
      <polyline points="34,84 50,62 62,70 82,44 90,50" stroke={O} strokeWidth="7" strokeLinecap="round" strokeLinejoin="round" />
      <polyline points="38,70 52,52 64,58 84,30" stroke={B} strokeWidth="7" strokeLinecap="round" strokeLinejoin="round" opacity=".55" />
      <circle cx="84" cy="30" r="6" fill={B} />
    </Svg>
  )
}

export function TrophyIcon(p: P) {
  return (
    <Svg {...p}>
      <path d="M38 34h44v8a22 22 0 0 1-44 0Z" fill={O} opacity=".35" />
      <path d="M44 34h32v26a16 16 0 0 1-32 0Z" fill={B} />
      <rect x="50" y="64" width="20" height="7" rx="3.5" fill={L} />
      <rect x="52" y="71" width="16" height="16" rx="4" fill={L} />
      <path d="M58 38l2.5 5 5.5.8-4 3.9.9 5.5-4.9-2.6-4.9 2.6.9-5.5-4-3.9 5.5-.8Z" fill="#ffffff" />
    </Svg>
  )
}

export function BulbIcon(p: P) {
  return (
    <Svg {...p}>
      <path d="M60 26a28 28 0 0 0-16 51c3 2.4 4.6 5.6 5 9h22c.4-3.4 2-6.6 5-9a28 28 0 0 0-16-51Z" fill={B} />
      <rect x="50" y="92" width="20" height="7" rx="3.5" fill={L} />
      <rect x="54" y="101" width="12" height="6" rx="3" fill={O} />
      <path d="M52 46l4 1.2 1.2 4-1.2 4-4 1.2-4-1.2-1.2-4 1.2-4Z" fill="#ffffff" />
      <path d="M68 58l3.4 1 1 3.4-1 3.4-3.4 1-3.4-1-1-3.4 1-3.4Z" fill="#ffffff" />
    </Svg>
  )
}

export function RocketIcon(p: P) {
  return (
    <Svg {...p}>
      <path d="M60 20c14 6 20 20 20 38l-10 26h-20l-10-26c0-18 6-32 20-38Z" fill={B} />
      <circle cx="60" cy="50" r="9" fill={O} />
      <path d="M55 60 l-20 12 20 6 12-12Z" fill={L} />
      <path d="M60 96c-6-2-10-6-12-12l8 12 8-12c-2 6-6 10-12 12Z" fill={L} />
      <circle cx="78" cy="30" r="4" fill="#ff9f43" />
      <circle cx="56" cy="26" r="3" fill="#dbe7ff" />
    </Svg>
  )
}

export function BookOpenIcon(p: P) {
  return (
    <Svg {...p}>
      <path d="M22 52a8 8 0 0 1 8-8h18v44H30a8 8 0 0 1-8-8Z" fill={L} />
      <path d="M98 52a8 8 0 0 0-8-8H72v44h18a8 8 0 0 0 8-8Z" fill={B} />
      <path d="M60 42c-8-6-20-6-30-2v46c10-4 22-4 30 2Z" fill={B} />
      <path d="M60 42c8-6 20-6 30-2v46c-10-4-22-4-30 2Z" fill="#7b8cff" />
      <line x1="60" y1="42" x2="60" y2="88" stroke="#ffffff" strokeWidth="4" strokeLinecap="round" />
    </Svg>
  )
}

export function CalendarIcon(p: P) {
  return (
    <Svg {...p}>
      <rect x="24" y="34" width="72" height="64" rx="12" fill="#ffffff" stroke={B} strokeWidth="7" />
      <rect x="24" y="50" width="72" height="14" fill={L} />
      <path d="M42 22v18M78 22v18" stroke={B} strokeWidth="7" strokeLinecap="round" />
      <rect x="42" y="72" width="24" height="10" rx="5" fill={B} />
    </Svg>
  )
}

export function ClockIcon(p: P) {
  return (
    <Svg {...p}>
      <circle cx="60" cy="60" r="44" fill="#ffffff" stroke={B} strokeWidth="7" />
      <circle cx="60" cy="60" r="56" fill="none" stroke="#eef4ff" strokeWidth="8" />
      <path d="M60 34v28l18 12" stroke={B} strokeWidth="7" strokeLinecap="round" strokeLinejoin="round" />
      <circle cx="60" cy="60" r="6" fill={O} />
    </Svg>
  )
}

export function StampIcon(p: P) {
  return (
    <Svg {...p}>
      <circle cx="60" cy="52" r="34" fill={O} opacity=".22" />
      <circle cx="60" cy="52" r="26" fill="#ffffff" stroke={O} strokeWidth="6" strokeDasharray="6 5" />
      <path d="M48 56l-10-12 14 8 22-16 2 10-14 18Z" fill={B} />
      <path d="M38 90h44" stroke={P} strokeWidth="8" strokeLinecap="round" />
      <path d="M32 100h56" stroke={B} strokeWidth="6" strokeLinecap="round" opacity=".8" />
    </Svg>
  )
}

export function RobotIcon(p: P) {
  return (
    <Svg {...p}>
      <rect x="34" y="40" width="52" height="44" rx="14" fill={B} />
      <rect x="46" y="52" width="28" height="6" rx="3" fill="#ffffff" />
      <circle cx="50" cy="50" r="4" fill="#ffffff" />
      <circle cx="70" cy="50" r="4" fill="#ffffff" />
      <path d="M44 76l8-8h16l8 8Z" fill="#7b8cff" />
      <path d="M50 40l-10-12M70 40l10-12" stroke={B} strokeWidth="7" strokeLinecap="round" />
      <circle cx="40" cy="42" r="4" fill={O} />
    </Svg>
  )
}

export function CalendarCheckIcon(p: P) {
  return (
    <Svg {...p}>
      <rect x="24" y="34" width="72" height="64" rx="12" fill="#ffffff" stroke={B} strokeWidth="7" />
      <rect x="24" y="50" width="72" height="14" fill={L} />
      <path d="M42 22v18M78 22v18" stroke={B} strokeWidth="7" strokeLinecap="round" />
      <path d="M46 78l9 9 20-20" stroke={O} strokeWidth="7" strokeLinecap="round" strokeLinejoin="round" />
    </Svg>
  )
}

export function SparkleIcon(p: P) {
  return (
    <Svg {...p}>
      <path
        d="M60 24l5 14 14 5-14 5-5 14-5-14-14-5 14-5Z"
        stroke={O} strokeWidth="6" strokeLinecap="round" strokeLinejoin="round"
      />
      <circle cx="36" cy="40" r="4" fill={B} opacity=".5" />
      <circle cx="84" cy="40" r="4" fill={B} opacity=".5" />
      <circle cx="84" cy="82" r="3" fill={O} opacity=".7" />
      <circle cx="36" cy="82" r="3" fill={B} />
    </Svg>
  )
}

/* 空状态通用：书本 + 星星 */
export function EmptyBookIcon(p: P) {
  return (
    <Svg {...p}>
      <circle cx="60" cy="60" r="50" fill={L} opacity=".4" />
      <path d="M30 46a8 8 0 0 1 8-8h44" stroke={B} strokeWidth="7" strokeLinecap="round" />
      <path d="M82 52v30a8 8 0 0 1-8 8H38a8 8 0 0 1-8-8V54" stroke={B} strokeWidth="7" strokeLinecap="round" />
      <path d="M60 40c-5-5-13-5-18-1v34c5-4 13-4 18 1Z" fill={B} />
      <path d="M60 40v34c5-5 13-5 18-1V39c-5-4-13-4-18 1Z" fill={G} opacity=".55" />
      <circle cx="94" cy="34" r="4" fill={O} />
    </Svg>
  )
}

/* 底色面板（登录页等用于承载插画的圆角底板） */
export function IllusPanel({ size = 100, children, className }: { size?: number; children?: ReactNode; className?: string }) {
  return (
    <div
      className={className}
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        width: size,
        height: size,
        borderRadius: size * 0.26,
        background: 'linear-gradient(135deg, #eef4ff 0%, #ffffff 55%, #fff4e6 100%)',
        boxShadow: '0 10px 30px rgba(67,97,238,.12)',
      }}
    >
      {children}
    </div>
  )
}