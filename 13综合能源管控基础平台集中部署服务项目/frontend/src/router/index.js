import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layouts/AppLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'home',
        component: () => import('@/views/HomeView.vue'),
        meta: { title: '首页驾驶舱', group: '概览' }
      },
      {
        path: 'energy/query',
        name: 'energy-query',
        component: () => import('@/views/EnergyQueryView.vue'),
        meta: { title: '能耗数据查询', group: '能耗管理' }
      },
      {
        path: 'energy/trend',
        name: 'energy-trend',
        component: () => import('@/views/EnergyTrendView.vue'),
        meta: { title: '能耗历史追溯', group: '能耗管理' }
      },
      {
        path: 'energy/analysis',
        name: 'energy-analysis',
        component: () => import('@/views/EnergyAnalysisView.vue'),
        meta: { title: '能源消费量与强度计算', group: '能耗管理' }
      },
      {
        path: 'carbon/accounting',
        name: 'carbon-accounting',
        component: () => import('@/views/CarbonAccountingView.vue'),
        meta: { title: '碳排放核算', group: '碳排放管理' }
      },
      {
        path: 'carbon/alert',
        name: 'carbon-alert',
        component: () => import('@/views/CarbonAlertView.vue'),
        meta: { title: '碳排放趋势预警', group: '碳排放管理' }
      },
      {
        path: 'carbon/factor',
        name: 'carbon-factor',
        component: () => import('@/views/CarbonFactorView.vue'),
        meta: { title: '碳因子库设置', group: '碳排放管理' }
      },
      {
        path: 'carbon/asset',
        name: 'carbon-asset',
        component: () => import('@/views/CarbonAssetView.vue'),
        meta: { title: '碳资产管理', group: '高级碳管理' }
      },
      {
        path: 'carbon/footprint',
        name: 'carbon-footprint',
        component: () => import('@/views/FootprintView.vue'),
        meta: { title: '产品碳足迹核算', group: '高级碳管理' }
      },
      {
        path: 'carbon/intensity',
        name: 'carbon-intensity',
        component: () => import('@/views/PlaceholderView.vue'),
        meta: {
          title: '碳排放强度分析',
          group: '碳排放管理',
          summary: '按产品类别、车间维度对比碳排放强度，支持同比/环比与行业先进值对标。',
          features: ['强度指标：tCO₂e/万元产值、tCO₂e/t 产品', '同比 / 环比自动计算', '行业先进值与标杆值对比', '异常月份自动标记']
        }
      },
      {
        path: 'carbon/verify',
        name: 'carbon-verify',
        component: () => import('@/views/PlaceholderView.vue'),
        meta: {
          title: '碳核查支撑',
          group: '碳排放管理',
          summary: '为第三方核查机构提供原始凭证、过程数据与核算报告的一键导出。',
          features: ['核查任务接收与分配', '原始凭证（表计读数）打包下载', '核算过程链留存', '核查报告 PDF 生成']
        }
      },
      {
        path: 'energy/strategy',
        name: 'energy-strategy',
        component: () => import('@/views/PlaceholderView.vue'),
        meta: {
          title: '用能分析与策略推荐',
          group: '能耗管理',
          summary: '结合大数据分析模型识别高耗能环节，输出可执行的节能策略清单。',
          features: ['分项能耗占比与异常识别', '峰谷用电优化建议', '设备能效等级评价', '节能措施节能量测算']
        }
      },
      {
        path: 'energy/budget',
        name: 'energy-budget',
        component: () => import('@/views/PlaceholderView.vue'),
        meta: {
          title: '用能与碳排放预算',
          group: '能耗管理',
          summary: '按时间周期编制用能与碳排放预算，实时跟踪执行偏差并动态调整。',
          features: ['月度预算编制与下发', '预算执行进度跟踪', '超预算预警', '年度目标分解']
        }
      },
      {
        path: 'efficiency/benchmark',
        name: 'efficiency-benchmark',
        component: () => import('@/views/PlaceholderView.vue'),
        meta: {
          title: '能效对标',
          group: '能耗管理',
          summary: '与行业标准、标杆企业开展能效对标，生成差距分析与改进清单。',
          features: ['对标对象选择', '单位产品能耗对比', '差距量化分析', '改进路径建议']
        }
      },
      {
        path: 'efficiency/flow',
        name: 'efficiency-flow',
        component: () => import('@/views/PlaceholderView.vue'),
        meta: {
          title: '能流分析',
          group: '能耗管理',
          summary: '以桑基图呈现能源从购入、转换、输配到终端使用的全流程流向。',
          features: ['能源流向桑基图', '转换损失分析', '分车间能流拆解', '能流平衡校验']
        }
      },
      {
        path: 'efficiency/balance',
        name: 'efficiency-balance',
        component: () => import('@/views/PlaceholderView.vue'),
        meta: {
          title: '能效平衡与优化记录',
          group: '能耗管理',
          summary: '记录能效平衡测试与优化措施执行情况，形成闭环管理台账。',
          features: ['平衡测试记录', '优化措施台账', '节能量核证', '整改跟踪']
        }
      },
      {
        path: 'supply-chain',
        name: 'supply-chain',
        component: () => import('@/views/PlaceholderView.vue'),
        meta: {
          title: '供应链碳管理',
          group: '高级碳管理',
          summary: '上游供应商数据采集与下游客户碳信息披露，构建供应链碳数据链。',
          features: ['供应商碳数据填报', '上游排放占比分析', '数据脱敏与权限隔离', '下游客户碳标签输出']
        }
      },
      {
        path: 'system',
        name: 'system',
        component: () => import('@/views/PlaceholderView.vue'),
        meta: {
          title: '系统管理',
          group: '系统',
          summary: '用户权限、采集接口与系统参数维护（）。',
          features: ['用户与角色管理', '采集接口配置（IoT / RPA / API）', '数据字典维护', '操作日志']
        }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/home' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to) => {
  const token = localStorage.getItem('gfecmp_token')
  if (!to.meta.public && !token) {
    return { path: '/login', query: to.fullPath === '/home' ? {} : { redirect: to.fullPath } }
  }
  if (to.path === '/login' && token) {
    return { path: '/home' }
  }
  document.title = to.meta.title ? `${to.meta.title} · 综合能源管控基础平台集中部署服务项目` : '综合能源管控基础平台集中部署服务项目'
  return true
})

export default router
