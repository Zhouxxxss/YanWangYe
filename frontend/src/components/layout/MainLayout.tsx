import { Layout as AntLayout, Menu, Avatar, Dropdown } from 'antd'
import {
  DashboardOutlined,
  EditOutlined,
  ReadOutlined,
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

const items = [
  { key: '/', icon: <DashboardOutlined />, label: '学习桌面' },
  { key: '/wrongbook', icon: <EditOutlined />, label: '错题本' },
  { key: '/recite', icon: <FireOutlined />, label: '背诵打卡' },
  { key: '/ask', icon: <RobotOutlined />, label: 'AI 答疑' },
  { key: '/plan', icon: <ScheduleOutlined />, label: '计划模板', icon2: <ReadOutlined /> },
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
        <div className="px-5 py-4 text-lg font-bold text-brand-600">
          研王爷<span className="ml-1 text-xs font-normal text-gray-400">伴学</span>
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
        />
      </AntLayout.Sider>
      <AntLayout>
        <AntLayout.Header
          style={{ background: '#fff', paddingInline: 24, display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}
        >
          <Dropdown
            menu={{
              items: [{ key: 'logout', icon: <LogoutOutlined />, label: '退出登录', onClick: logout }],
            }}
          >
            <div className="flex cursor-pointer items-center gap-2">
              <Avatar style={{ background: '#4361ee' }}>{username?.[0]?.toUpperCase()}</Avatar>
              <span className="text-sm">{username}</span>
              <span className="rounded bg-brand-50 px-1.5 text-xs text-brand-600">{role}</span>
            </div>
          </Dropdown>
        </AntLayout.Header>
        <AntLayout.Content style={{ padding: 24 }}>
          <motion.div key={pathname} initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}>
            <Outlet />
          </motion.div>
        </AntLayout.Content>
      </AntLayout>
    </AntLayout>
  )
}