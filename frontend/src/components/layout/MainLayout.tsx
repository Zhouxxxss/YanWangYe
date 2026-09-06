import { Layout as AntLayout, Menu, Avatar, Dropdown } from 'antd'
import {
  DashboardOutlined,
  EditOutlined,
  RobotOutlined,
  ScheduleOutlined,
  FireOutlined,
  TeamOutlined,
  GlobalOutlined,
  LogoutOutlined,
} from '@ant-design/icons'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useAuthStore } from '@/stores/auth'
import { BookOpenIcon } from '@/components/illustrations'
import { AmbientBg } from '@/components/AmbientBg'

const items = [
  { key: '/', icon: <DashboardOutlined />, label: '学习桌面' },
  { key: '/wrongbook', icon: <EditOutlined />, label: '错题本' },
  { key: '/recite', icon: <FireOutlined />, label: '背诵打卡' },
  { key: '/ask', icon: <RobotOutlined />, label: 'AI 答疑' },
  { key: '/plan', icon: <ScheduleOutlined />, label: '计划模板' },
  // 二期占位菜单（greyed），进入显示 placeholder
  { key: '/community', icon: <GlobalOutlined />, label: '社区互助', ph2: true },
  { key: '/group', icon: <TeamOutlined />, label: '搭子小组', ph2: true },
]

export function Layout() {
  const nav = useNavigate()
  const { pathname } = useLocation()
  const { username, role, logout } = useAuthStore()

  const selected = items.find((i) => pathname.startsWith(i.key))?.key ?? '/'

  return (
    <AntLayout style={{ minHeight: '100vh' }}>
      <AntLayout.Sider
        theme="light"
        width={220}
        style={{ borderRight: '1px solid #eef0f4' }}
      >
        <div className="flex items-center gap-2 px-5 py-4">
          <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-br from-brand-500 to-brand-700 text-white shadow-lg shadow-brand-500/30">
            <BookOpenIcon size={22} />
          </div>
          <div className="leading-tight">
            <div className="text-lg font-black text-brand-600">研王爷</div>
            <div className="text-[10px] text-gray-400">考研伴学管家</div>
          </div>
        </div>
        <Menu
          mode="inline"
          selectedKeys={[selected]}
          items={items.map((i) => ({
            key: i.key,
            icon: i.icon,
            label: (
              <span className={i.ph2 ? 'text-gray-400' : undefined}>
                {i.label}
                {i.ph2 && <span className="ml-1 text-[10px]">二期</span>}
              </span>
            ),
          }))}
          onClick={({ key }) => nav(key)}
          style={{ borderInlineEnd: 'none' }}
        />
        <div className="absolute bottom-4 left-4 right-4 rounded-xl bg-brand-50/70 px-3 py-2 text-[11px] text-gray-500">
          <span className="font-semibold text-brand-600">一期聚焦 · 二期可扩</span>
        </div>
      </AntLayout.Sider>
      <AntLayout>
        <AntLayout.Header
          style={{ background: '#fff', paddingInline: 24, display: 'flex', justifyContent: 'flex-end', alignItems: 'center', borderBottom: '1px solid #eef0f4' }}
        >
          <Dropdown
            menu={{
              items: [{ key: 'logout', icon: <LogoutOutlined />, label: '退出登录', onClick: logout }],
            }}
          >
            <div className="press flex cursor-pointer items-center gap-2 rounded-full border border-transparent px-2 py-1 transition-colors hover:border-brand-100 hover:bg-brand-50/50">
              <Avatar style={{ background: '#4361ee' }}>{username?.[0]?.toUpperCase()}</Avatar>
              <span className="text-sm">{username}</span>
              <span className="rounded bg-brand-50 px-1.5 text-xs text-brand-600">{role}</span>
            </div>
          </Dropdown>
        </AntLayout.Header>
        <AntLayout.Content style={{ padding: 24, position: 'relative' }}>
          <AmbientBg />
          <div className="relative z-10">
            <motion.div
              key={pathname}
              initial={{ opacity: 0, y: 16, scale: 0.985, filter: 'blur(8px)' }}
              animate={{ opacity: 1, y: 0, scale: 1, filter: 'blur(0px)' }}
              transition={{ type: 'spring', stiffness: 220, damping: 26 }}
            >
              <Outlet />
            </motion.div>
          </div>
        </AntLayout.Content>
      </AntLayout>
    </AntLayout>
  )
}