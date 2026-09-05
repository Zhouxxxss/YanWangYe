import type { ReactNode, SVGProps } from 'react'

/* ===== 扁平矢量教育风插画库 =====
 * 风格对齐参考视频：白底、品牌蓝主色 + 橙色点缀、几何化扁平、前景可作配图/空状态/图标
 * 全部为内联 SVG，零网络依赖、可缩放、风格统一 */

const B = '#4361ee' // 品牌蓝
const L = '#dbe7ff' // 浅蓝
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

/* 登录页主视觉：书桌/学习场景 */
export function StudyScene({ size = 340, className }: { size?: number; className?: string }) {
  return (
    <svg width={size} height={size} viewBox="0 0 340 252" fill="none" className={className} aria-hidden>
      <circle cx="170" cy="126" r="118" fill="#eef4ff" />
      <circle cx="170" cy="126" r="92" fill="#ffffff" stroke={L} strokeWidth="7" />
      {/* 折线上升（成绩/进步） */}
      <polyline
        points="78,170 120,132 150,146 196,100 236,104 262,80"
        stroke={O}
        strokeWidth="7"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <circle cx="262" cy="80" r="8" fill={O} />
      {/* 打开的书 */}
      <path d="M120 112 L170 128 L220 112 L220 168 L170 186 L120 168 Z" fill={B} />
      <path d="M120 112 L170 128 L170 186 L120 168 Z" fill="#7b8cff" />
      <path d="M170 128 L170 186 L220 168 L220 112 Z" fill={L} />
      <line x1="120" y1="126" x2="120" y2="166" stroke="#ffffff" strokeWidth="3" strokeLinecap="round" opacity=".55" />
      <line x1="170" y1="142" x2="170" y2="186" stroke="#8a94a6" strokeWidth="3" strokeLinecap="round" opacity=".45" />
      <path d="M188 130 L204 124 L200 141 Z" fill="#ffffff" />
      {/* 悬浮星星 */}
      <path d="M286 44 l4 9 9 1-6.5 7 1.5 9.5-8-4.5-8 4.5 1.5-9.5-6.5-7 9-1Z" fill={O} />
      <path d="M64 52 l3.5 8 8.5 1-6 6.5 1.5 9-7.5-4.5-7.5 4.5 1.5-9-6-6.5 8.5-1Z" fill={B} />
      <circle cx="52" cy="112" r="5" fill={L} />
      <circle cx="294" cy="150" r="6" fill={P} />
    </svg>
  )
}

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
        stroke={O}
        strokeWidth="6"
        strokeLinecap="round"
        strokeLinejoin="round"
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
        background:
          'linear-gradient(135deg, #eef4ff 0%, #ffffff 55%, #fff4e6 100%)',
        boxShadow: '0 10px 30px rgba(67,97,238,.12)',
      }}
    >
      {children}
    </div>
  )
}