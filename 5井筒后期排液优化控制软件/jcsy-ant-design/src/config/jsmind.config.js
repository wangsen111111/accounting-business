import { homePageIcon } from '@/core/icons'

const jsmindRouterMap = [
  {
    path: '/jsmindmainindex',
    name: 'JsMindMainIndex',
    component: () => import('@/views/JsmindMain/index'),
    meta: {
      title: '首页导图',
      keepAlive: true,
      permission: ['dashboard'],
      icon: homePageIcon
    }
  }
]

export default jsmindRouterMap
