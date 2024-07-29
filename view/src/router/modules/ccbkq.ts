import type { Route } from '../index.type'
import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'

const route: Route[] = [
  {
    path: '/ccbkq',
    component: Layout,
    name: 'CcbKq',
    alwayShow: true,
    meta: {
      title: '考勤',
      icon: 'sfont system-yonghu'
    },
    children: [
      {
        path: 'month_data_compare',
        component: createNameComponent(() => import('@/views/ccbkq/monthDataCompare.vue')),
        name: 'month_data_compare',
        meta: {
          title: '门禁APP月考勤数据对比',
          icon: 'sfont system-yonghu'
        }
      },
      {
        path: 'month_data',
        component: createNameComponent(() => import('@/views/ccbkq/monthData.vue')),
        name: 'month_data',
        meta: {
          title: '月考勤汇总',
          icon: 'sfont system-yonghu'
        }
      },
      {
        path: 'month_data_detail',
        component: createNameComponent(() => import('@/views/ccbkq/monthDataDetail.vue')),
        name: 'month_data_detail',
        meta: {
          title: '月考勤明细',
          icon: 'sfont system-yonghu'
        }
      },
      {
        path: 'js_data',
        component: createNameComponent(() => import('@/views/ccbkq/jsData.vue')),
        name: 'js_data',
        meta: {
          title: '结算考勤汇总',
          icon: 'sfont system-yonghu'
        }
      },
      {
        path: 'js_data_detail',
        component: createNameComponent(() => import('@/views/ccbkq/jsDataDetail.vue')),
        name: 'js_data_detail',
        meta: {
          title: '结算考勤明细',
          icon: 'sfont system-yonghu'
        }
      },
      {
        path: 'data_compare',
        component: createNameComponent(() => import('@/views/ccbkq/dataCompare.vue')),
        name: 'data_compare',
        meta: {
          title: '月考勤结算考勤对比',
          icon: 'sfont system-yonghu'
        }
      },
    ]
  }
]

export default route
