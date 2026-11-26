/**
 * 左侧菜单与首页蜂窝导航的统一配置。
 * 菜单项与路由 / 需求文档中的 12 项核心功能一一对应。
 */
export const menuGroups = [
  {
    title: '概览',
    items: [{ path: '/home', label: '首页驾驶舱', icon: 'home' }]
  },
  {
    title: '能耗管理',
    items: [
      { path: '/energy/query', label: '能耗数据查询', icon: 'list' },
      { path: '/energy/trend', label: '能耗历史追溯', icon: 'chart' },
      { path: '/energy/analysis', label: '能源消费量与强度计算', icon: 'calc' },
      { path: '/energy/strategy', label: '用能分析与策略推荐', icon: 'bulb' },
      { path: '/efficiency/benchmark', label: '能效对标', icon: 'target' },
      { path: '/efficiency/flow', label: '能流分析', icon: 'flow' },
      { path: '/energy/budget', label: '用能与碳排放预算', icon: 'wallet' }
    ]
  },
  {
    title: '碳排放管理',
    items: [
      { path: '/carbon/accounting', label: '碳排放核算', icon: 'carbon' },
      { path: '/carbon/intensity', label: '碳排放强度分析', icon: 'pulse' },
      { path: '/carbon/alert', label: '碳排放趋势预警', icon: 'alert' },
      { path: '/carbon/verify', label: '碳核查支撑', icon: 'file' },
      { path: '/carbon/factor', label: '碳因子库设置', icon: 'factor' }
    ]
  },
  {
    title: '高级碳管理',
    items: [
      { path: '/carbon/footprint', label: '产品碳足迹核算', icon: 'leaf' },
      { path: '/supply-chain', label: '供应链碳管理', icon: 'chain' },
      { path: '/carbon/asset', label: '碳资产管理', icon: 'asset' }
    ]
  },
  {
    title: '系统',
    items: [{ path: '/system', label: '系统管理', icon: 'settings' }]
  }
]

/** 首页蜂窝导航（12 项核心功能 + 辅助功能） */
export const hexagons = [
  { label: '能耗查询', path: '/energy/query', tone: 'green' },
  { label: '能效对标', path: '/efficiency/benchmark', tone: 'green' },
  { label: '供应链碳管理', path: '/supply-chain', tone: 'green' },
  { label: '零碳', path: '/carbon/asset', tone: 'dark' },
  { label: '能源消费量和强度计算', path: '/energy/analysis', tone: 'orange' },
  { label: '用能与碳排放预算管理', path: '/energy/budget', tone: 'orange' },
  { label: '碳核查支撑', path: '/carbon/verify', tone: 'green' },
  { label: '智慧', path: '/system', tone: 'dark' },
  { label: '能流分析', path: '/efficiency/flow', tone: 'green' },
  { label: '碳排放核算', path: '/carbon/accounting', tone: 'green' },
  { label: '经济', path: '/carbon/factor', tone: 'dark' },
  { label: '能源消费分析与用能策略推荐', path: '/energy/strategy', tone: 'green' },
  { label: '碳排放趋势预警', path: '/carbon/alert', tone: 'green' },
  { label: '碳资产管理', path: '/carbon/asset', tone: 'orange' },
  { label: '能效平衡与优化记录', path: '/efficiency/balance', tone: 'orange' },
  { label: '产品碳足迹核算', path: '/carbon/footprint', tone: 'green' }
]

/** 按每行 4 个、偶数行错位半格，交错排布成蜂窝 */
export const honeycombRows = [
  hexagons.slice(0, 4),
  hexagons.slice(4, 8),
  hexagons.slice(8, 12),
  hexagons.slice(12, 16)
]
