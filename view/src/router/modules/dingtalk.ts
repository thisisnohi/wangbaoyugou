import type { Route } from '../index.type'
import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'

const route: Route[] = [
  {
    path: '/dingTalk',
    component: Layout,
    redirect: '/dingTalk/dept',
    name: 'DingTalk',
    alwayShow: true,
    meta: {
      title: '钉钉',
      icon: 'sfont system-yonghu'
    },
    children: [
      {
        path: 'dingTokenList',
        component: createNameComponent(() => import('@/views/dingTalk/user/dingTokenList.vue')),
        name: 'dingTokenList',
        meta: {
          title: 'TOKEN维护',
          icon: 'sfont system-yonghu'
        }
      },
    ]
  }
]

export default route
