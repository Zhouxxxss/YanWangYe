import { Card, Result } from 'antd'

/**
 * 二期占位页（社区互助 / 搭子小组 / 择校库）。
 * 二期实现时替换为对应 features 页即可，路由与侧边栏入口已预置。
 */
export function Phase2Placeholder({ name }: { name: string }) {
  return (
    <Card bordered={false}>
      <Result
        status="info"
        title={`${name}（二期规划中）`}
        subTitle="一期已预埋权限点、数据表与相关领域事件接口，二期接入即可复用，无需返工。"
      />
    </Card>
  )
}