import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Layout } from '@/components/layout/MainLayout'
import { RequireAuth } from '@/components/auth/RequireAuth'
import { LoginPage } from '@/features/auth/LoginPage'
import { HomePage, noop } from '@/features/study/HomePage'
import { WrongBookPage } from '@/features/wrongbook/WrongBookPage'
import { RecitePage } from '@/features/recite/RecitePage'
import { AskPage } from '@/features/rag/AskPage'
import { DashboardPage } from '@/features/dashboard/DashboardPage'
import { PlanPage } from '@/features/plan/PlanPage'
// 二期占位路由：合并后从 features 下即插即用
import { Phase2Placeholder } from '@/features/_phase2/Phase2Placeholder'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<RequireAuth />}>
          <Route element={<Layout />}>
            <Route path="/" element={<HomePage />} />
            <Route path="/wrongbook" element={<WrongBookPage />} />
            <Route path="/recite" element={<RecitePage />} />
            <Route path="/ask" element={<AskPage />} />
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/plan" element={<PlanPage />} />
            {/* 二期占位：community / group / school */}
            <Route path="/community" element={<Phase2Placeholder name="社区互助" />} />
            <Route path="/group" element={<Phase2Placeholder name="搭子小组" />} />
            <Route path="/school" element={<Phase2Placeholder name="择校库" />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Route>
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
void noop