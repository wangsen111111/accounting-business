// eslint-disable-next-line
import { UserLayout, BasicLayout, RouteView, BlankLayout, PageView,IndexLayout,PageSiderView } from '@/layouts'
import { homePageIcon, teamIcon, sharingCenterIcon, help, workSurfaceIcon, docIcon, collectionIcon, hotIcon } from '@/core/icons'
import jsmindRouterMap from './jsmind.config'

export const asyncRouterMap = [

  {
    path: '/',
    name: 'index1',
    component: BasicLayout,
    meta: { title: '首页1', icon: homePageIcon },
    redirect: 'index',
    children: [
      ...jsmindRouterMap,
      // keepAlive缓存，true路由进入新页面mounted不执行，false执行
      {
        path: '/page/ProjectDetail',
        name: 'ProjectDetail',
        hidden: true,
        component: () => import('@/views/page/ProjectDetail'),
        meta: { title: '项目详情', keepAlive: false, permission: ['form'] }
      },
      {
        path: '/page/FilePreviewAndNotes',
        name: 'FilePreviewAndNotes',
        hidden: true,
        component: () => import('@/views/page/FilePreviewAndNotes'),
        meta: { title: '文件预览笔记', keepAlive: false, permission: ['form'] }
      },
      {
        path: '/page/MindMap',
        name: 'MindMap',
        hidden: true,
        component: () => import('@/views/page/MindMap'),
        // redirect: '/page/Index',
        meta: { title: '思维导图操作', keepAlive: false, permission: ['form'] }
      },
      {
        path: '/page/Index',
        name: 'index',
        // hidden: true,
        component: () => import('@/views/page/Index'),
        // redirect: '/page/Index',
        meta: { title: '主页', keepAlive: false, permission: ['form'] }
      },
      {
        path: '/page/WorkSurface',
        name: 'WorkSurface',
        component: () => import('@/views/page/WorkSurface'),
        meta: { title: '工作面板', keepAlive: false, icon: workSurfaceIcon, permission: ['form'] }
      },
      {
        path: '/page/JsmindMindMap',
        name: 'JsmindMindMap',
        component: () => import('@/views/page/JsmindMindMap'),
        meta: { title: '思维导图', keepAlive: false, permission: ['form'] }
      },
      {
        path: '/page/project',
        name: 'project',
        component: () => import('@/views/page/Project'),
        meta: { title: '项目库', keepAlive: false, permission: ['form'] }
      },
      {
        path: '/page/Doc',
        name: 'Doc',
        component: () => import('@/views/page/Doc'),
        meta: { title: '文档', keepAlive: false, icon: docIcon, permission: ['form'] }
      },
      {
        path: '/page/KnowledgeBaseDetail',
        name: 'KnowledgeBaseDetail',
        hidden: true,
        component: () => import('@/views/page/KnowledgeBaseDetail'),
        meta: { title: '模板定制', keepAlive: true, icon: hotIcon, permission: ['form'] }
      },
      {
        path: '/page/Team',
        name: 'Team',
        hidden: true,
        component: () => import('@/views/page/Team'),
        meta: { title: '团队', keepAlive: true, icon: teamIcon, permission: ['form'] }
      },
      {
        path: '/page/Collection',
        name: 'Collection',
        hidden: true,
        component: () => import('@/views/page/Collection'),
        meta: { title: '收藏', keepAlive: true, icon: collectionIcon, permission: ['form'] }
      },
      {
        path: '/page/SharingCenter',
        name: 'SharingCenter',
        hidden: true,
        component: () => import('@/views/page/SharingCenter'),
        meta: { title: '共享中心', keepAlive: true, icon: sharingCenterIcon, permission: ['form'] }
      },
      {
        path: '/page/Help',
        name: 'Help',
        hidden: true,
        component: () => import('@/views/page/Help'),
        meta: { title: '帮助', keepAlive: true, icon: help, permission: ['form'] }
      },

      // {
      //   path: '/page/QuickPage',
      //   name: 'QuickPage',
      //   hidden: true,
      //   component: () => import('@/views/page/QuickPage'),
      //   meta: { title: '快速访问', keepAlive: true, permission: ['form'] }
      // },
      // {
      //   path: '/page/TagPage',
      //   name: 'TagPage',
      //   hidden: true,
      //   component: () => import('@/views/page/TagPage'),
      //   meta: { title: '标签', keepAlive: true, permission: ['form'] }
      // },
      // {
      //   path: '/page/RecycleBin',
      //   name: 'RecycleBin',
      //   hidden: true,
      //   component: () => import('@/views/page/RecycleBin'),
      //   meta: { title: '回收站', keepAlive: true, permission: ['form'] }
      // },
      // {
      //   path: '/page/RecentBrowse',
      //   name: 'RecentBrowse',
      //   hidden: true,
      //   component: () => import('@/views/page/RecentBrowse'),
      //   meta: { title: '最近浏览', keepAlive: true, permission: ['form'] }
      // },
      // {
      //   path: '/page/Collection',
      //   name: 'Collection',
      //   hidden: true,
      //   component: () => import('@/views/page/Collection'),
      //   meta: { title: '收藏', keepAlive: true, permission: ['form'] }
      // },
      // {
      //   path: '/page/FocusOn',
      //   name: 'FocusOn',
      //   hidden: true,
      //   component: () => import('@/views/page/FocusOn'),
      //   meta: { title: '关注', keepAlive: true, permission: ['form'] }
      // },
      // {
      //   path: '/page/KnowledgeBase',
      //   name: 'KnowledgeBase',
      //   hidden: true,
      //   component: () => import('@/views/page/KnowledgeBase'),
      //   meta: { title: '项目库', keepAlive: false, permission: ['form'] }
      // },
      // {
      //   path: '/page/KnowledgeBaseDetail',
      //   name: 'KnowledgeBaseDetail',
      //   hidden: true,
      //   component: () => import('@/views/page/KnowledgeBaseDetail'),
      //   meta: { title: '知识库详情', keepAlive: false, permission: ['form'] }
      // },
      // {
      //   path: '/page/Doc',
      //   name: 'Doc',
      //   hidden: true,
      //   component: () => import('@/views/page/Doc'),
      //   meta: { title: '文档', keepAlive: true, permission: ['form'] }
      // },
      // {
      //   path: '/page/Tag',
      //   name: 'Tag',
      //   hidden: true,
      //   component: () => import('@/views/page/Tag'),
      //   meta: { title: '标签', keepAlive: true, permission: ['form'] }
      // },

      {
        path: '/page/CreateProject',
        name: 'CreateProject',
        hidden: true,
        component: () => import('@/views/page/CreateProject'),
        meta: { title: '创建项目', keepAlive: true, permission: ['form'] }
      },
      {
        path: '/page/SelectTemplate',
        name: 'SelectTemplate',
        hidden: true,
        component: () => import('@/views/page/SelectTemplate'),
        meta: { title: '选择模板', keepAlive: true, permission: ['form'] }
      },
      {
        path: '/page/UploadFiles',
        name: 'UploadFiles',
        hidden: true,
        component: () => import('@/views/page/UploadFiles'),
        meta: { title: '上传文件', keepAlive: true, permission: ['form'] }
      }
    ]
  },

  // {
  //   path: '/',
  //   name: 'index',
  //   component: BasicLayout,
  //   meta: { title: '首页' },
  //   redirect: '/dashboard/workplace',
  //   children: [
  //     // dashboard
  //     {
  //       path: 'dashboard',
  //       name: 'dashboard',
  //       redirect: '/dashboard/workplace',
  //       component: RouteView,
  //       meta: { title: '仪表盘', keepAlive: true, icon: bxAnaalyse, permission: [ 'dashboard' ] },
  //       children: [
  //         {
  //           path: 'analysis/:pageNo([1-9]\\d*)?',
  //           name: 'Analysis',
  //           component: () => import('@/views/dashboard/Analysis'),
  //           meta: { title: '分析页', keepAlive: false, permission: [ 'dashboard' ] }
  //         },
  //         // 外部链接
  //         {
  //           path: 'https://www.baidu.com/',
  //           name: 'Monitor',
  //           meta: { title: '监控页（外部）', target: '_blank' }
  //         },
  //         {
  //           path: 'workplace',
  //           name: 'Workplace',
  //           component: () => import('@/views/dashboard/Workplace'),
  //           meta: { title: '工作台', keepAlive: true, permission: [ 'dashboard' ] }
  //         },
  //         {
  //           path: 'test-work',
  //           name: 'TestWork',
  //           component: () => import('@/views/dashboard/TestWork'),
  //           meta: { title: '测试功能', keepAlive: true, permission: [ 'dashboard' ] }
  //         }
  //       ]
  //     },

  //     // forms
  //     {
  //       path: '/form',
  //       redirect: '/form/base-form',
  //       component: PageView,
  //       meta: { title: '表单页', icon: 'form', permission: [ 'form' ] },
  //       children: [
  //         {
  //           path: '/form/base-form',
  //           name: 'BaseForm',
  //           component: () => import('@/views/form/BasicForm'),
  //           meta: { title: '基础表单', keepAlive: true, permission: [ 'form' ] }
  //         },
  //         {
  //           path: '/form/step-form',
  //           name: 'StepForm',
  //           component: () => import('@/views/form/stepForm/StepForm'),
  //           meta: { title: '分步表单', keepAlive: true, permission: [ 'form' ] }
  //         },
  //         {
  //           path: '/form/advanced-form',
  //           name: 'AdvanceForm',
  //           component: () => import('@/views/form/advancedForm/AdvancedForm'),
  //           meta: { title: '高级表单', keepAlive: true, permission: [ 'form' ] }
  //         }
  //       ]
  //     },

  //     // list
  //     {
  //       path: '/list',
  //       name: 'list',
  //       component: PageView,
  //       redirect: '/list/table-list',
  //       meta: { title: '列表页', icon: 'table', permission: [ 'table' ] },
  //       children: [
  //         {
  //           path: '/list/table-list/:pageNo([1-9]\\d*)?',
  //           name: 'TableListWrapper',
  //           hideChildrenInMenu: true, // 强制显示 MenuItem 而不是 SubMenu
  //           component: () => import('@/views/list/TableList'),
  //           meta: { title: '查询表格', keepAlive: true, permission: [ 'table' ] }
  //         },
  //         {
  //           path: '/list/basic-list',
  //           name: 'BasicList',
  //           component: () => import('@/views/list/StandardList'),
  //           meta: { title: '标准列表', keepAlive: true, permission: [ 'table' ] }
  //         },
  //         {
  //           path: '/list/card',
  //           name: 'CardList',
  //           component: () => import('@/views/list/CardList'),
  //           meta: { title: '卡片列表', keepAlive: true, permission: [ 'table' ] }
  //         },
  //         {
  //           path: '/list/search',
  //           name: 'SearchList',
  //           component: () => import('@/views/list/search/SearchLayout'),
  //           redirect: '/list/search/article',
  //           meta: { title: '搜索列表', keepAlive: true, permission: [ 'table' ] },
  //           children: [
  //             {
  //               path: '/list/search/article',
  //               name: 'SearchArticles',
  //               component: () => import('../views/list/search/Article'),
  //               meta: { title: '搜索列表（文章）', permission: [ 'table' ] }
  //             },
  //             {
  //               path: '/list/search/project',
  //               name: 'SearchProjects',
  //               component: () => import('../views/list/search/Projects'),
  //               meta: { title: '搜索列表（项目）', permission: [ 'table' ] }
  //             },
  //             {
  //               path: '/list/search/application',
  //               name: 'SearchApplications',
  //               component: () => import('../views/list/search/Applications'),
  //               meta: { title: '搜索列表（应用）', permission: [ 'table' ] }
  //             }
  //           ]
  //         }
  //       ]
  //     },

  //     // profile
  //     {
  //       path: '/profile',
  //       name: 'profile',
  //       component: RouteView,
  //       redirect: '/profile/basic',
  //       meta: { title: '详情页', icon: 'profile', permission: [ 'profile' ] },
  //       children: [
  //         {
  //           path: '/profile/basic',
  //           name: 'ProfileBasic',
  //           component: () => import('@/views/profile/basic/Index'),
  //           meta: { title: '基础详情页', permission: [ 'profile' ] }
  //         },
  //         {
  //           path: '/profile/advanced',
  //           name: 'ProfileAdvanced',
  //           component: () => import('@/views/profile/advanced/Advanced'),
  //           meta: { title: '高级详情页', permission: [ 'profile' ] }
  //         }
  //       ]
  //     },

  //     // result
  //     {
  //       path: '/result',
  //       name: 'result',
  //       component: PageView,
  //       redirect: '/result/success',
  //       meta: { title: '结果页', icon: 'check-circle-o', permission: [ 'result' ] },
  //       children: [
  //         {
  //           path: '/result/success',
  //           name: 'ResultSuccess',
  //           component: () => import(/* webpackChunkName: "result" */ '@/views/result/Success'),
  //           meta: { title: '成功', keepAlive: false, hiddenHeaderContent: true, permission: [ 'result' ] }
  //         },
  //         {
  //           path: '/result/fail',
  //           name: 'ResultFail',
  //           component: () => import(/* webpackChunkName: "result" */ '@/views/result/Error'),
  //           meta: { title: '失败', keepAlive: false, hiddenHeaderContent: true, permission: [ 'result' ] }
  //         }
  //       ]
  //     },

  //     // Exception
  //     {
  //       path: '/exception',
  //       name: 'exception',
  //       component: RouteView,
  //       redirect: '/exception/403',
  //       meta: { title: '异常页', icon: 'warning', permission: [ 'exception' ] },
  //       children: [
  //         {
  //           path: '/exception/403',
  //           name: 'Exception403',
  //           component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/403'),
  //           meta: { title: '403', permission: [ 'exception' ] }
  //         },
  //         {
  //           path: '/exception/404',
  //           name: 'Exception404',
  //           component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/404'),
  //           meta: { title: '404', permission: [ 'exception' ] }
  //         },
  //         {
  //           path: '/exception/500',
  //           name: 'Exception500',
  //           component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/500'),
  //           meta: { title: '500', permission: [ 'exception' ] }
  //         }
  //       ]
  //     },

  //     // account
  //     {
  //       path: '/account',
  //       component: RouteView,
  //       redirect: '/account/center',
  //       name: 'account',
  //       meta: { title: '个人页', icon: 'user', keepAlive: true, permission: [ 'user' ] },
  //       children: [
  //         {
  //           path: '/account/center',
  //           name: 'center',
  //           component: () => import('@/views/account/center/Index'),
  //           meta: { title: '个人中心', keepAlive: true, permission: [ 'user' ] }
  //         },
  //         {
  //           path: '/account/settings',
  //           name: 'settings',
  //           component: () => import('@/views/account/settings/Index'),
  //           meta: { title: '个人设置', hideHeader: true, permission: [ 'user' ] },
  //           redirect: '/account/settings/base',
  //           hideChildrenInMenu: true,
  //           children: [
  //             {
  //               path: '/account/settings/base',
  //               name: 'BaseSettings',
  //               component: () => import('@/views/account/settings/BaseSetting'),
  //               meta: { title: '基本设置', hidden: true, permission: [ 'user' ] }
  //             },
  //             {
  //               path: '/account/settings/security',
  //               name: 'SecuritySettings',
  //               component: () => import('@/views/account/settings/Security'),
  //               meta: { title: '安全设置', hidden: true, keepAlive: true, permission: [ 'user' ] }
  //             },
  //             {
  //               path: '/account/settings/custom',
  //               name: 'CustomSettings',
  //               component: () => import('@/views/account/settings/Custom'),
  //               meta: { title: '个性化设置', hidden: true, keepAlive: true, permission: [ 'user' ] }
  //             },
  //             {
  //               path: '/account/settings/binding',
  //               name: 'BindingSettings',
  //               component: () => import('@/views/account/settings/Binding'),
  //               meta: { title: '账户绑定', hidden: true, keepAlive: true, permission: [ 'user' ] }
  //             },
  //             {
  //               path: '/account/settings/notification',
  //               name: 'NotificationSettings',
  //               component: () => import('@/views/account/settings/Notification'),
  //               meta: { title: '新消息通知', hidden: true, keepAlive: true, permission: [ 'user' ] }
  //             }
  //           ]
  //         }
  //       ]
  //     },

  //     // other
  //     {
  //       path: '/other',
  //       name: 'otherPage',
  //       component: PageView,
  //       meta: { title: '其他组件', icon: 'slack', permission: [ 'dashboard' ] },
  //       redirect: '/other/icon-selector',
  //       children: [
  //         {
  //           path: '/other/icon-selector',
  //           name: 'TestIconSelect',
  //           component: () => import('@/views/other/IconSelectorView'),
  //           meta: { title: 'IconSelector', icon: 'tool', keepAlive: true, permission: [ 'dashboard' ] }
  //         },
  //         {
  //           path: '/other/list',
  //           component: RouteView,
  //           meta: { title: '业务布局', icon: 'layout', permission: [ 'support' ] },
  //           redirect: '/other/list/tree-list',
  //           children: [
  //             {
  //               path: '/other/list/tree-list',
  //               name: 'TreeList',
  //               component: () => import('@/views/other/TreeList'),
  //               meta: { title: '树目录表格', keepAlive: true }
  //             },
  //             {
  //               path: '/other/list/edit-table',
  //               name: 'EditList',
  //               component: () => import('@/views/other/TableInnerEditList'),
  //               meta: { title: '内联编辑表格', keepAlive: true }
  //             },
  //             {
  //               path: '/other/list/user-list',
  //               name: 'UserList',
  //               component: () => import('@/views/other/UserList'),
  //               meta: { title: '用户列表', keepAlive: true }
  //             },
  //             {
  //               path: '/other/list/role-list',
  //               name: 'RoleList',
  //               component: () => import('@/views/other/RoleList'),
  //               meta: { title: '角色列表', keepAlive: true }
  //             },
  //             {
  //               path: '/other/list/system-role',
  //               name: 'SystemRole',
  //               component: () => import('@/views/role/RoleList'),
  //               meta: { title: '角色列表2', keepAlive: true }
  //             },
  //             {
  //               path: '/other/list/permission-list',
  //               name: 'PermissionList',
  //               component: () => import('@/views/other/PermissionList'),
  //               meta: { title: '权限列表', keepAlive: true }
  //             }
  //           ]
  //         }
  //       ]
  //     }
  //   ]
  // },
  {
    path: '*', redirect: '/404', hidden: true
  }
]

/**
 * 基础路由
 * @type { *[] }
 */
export const constantRouterMap = [
  {
    path: '/user',
    component: UserLayout,
    redirect: '/user/login',
    hidden: true,
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/Login')
      },
      {
        path: 'register',
        name: 'register',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/Register')
      },
      {
        path: 'register-result',
        name: 'registerResult',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/RegisterResult')
      },
      {
        path: 'recover',
        name: 'recover',
        component: undefined
      }
    ]
  },

  {
    path: '/test',
    component: BlankLayout,
    redirect: '/test/home',
    children: [
      {
        path: 'home',
        name: 'TestHome',
        component: () => import('@/views/Home')
      }
    ]
  },
  {
    path: '/index',
    component: BlankLayout,
    redirect: '/page/Index',
    children: [
      {
        path: 'home',
        name: 'TestHome',
        component: () => import('@/views/page/Index')
      }
    ]
  },

  {
    path: '/404',
    component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/404')
  }

]
