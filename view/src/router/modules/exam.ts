import type { Route } from '../index.type'
// import ExamLayout from '@/views/exam/ExamLayout.vue'
import ExamLayout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'

const route: Route[] = [
  {
    path: '/exam',
    component: ExamLayout,
    redirect: '/exam/exam-index',
    alwayShow: true,
    meta: { title: 'message.exam.menu.name', icon: 'sfont system-menu' },
    children: [
      {
        path: 'exam-index',
        component: createNameComponent(() => import('@/views/exam/exam-index.vue')),
        meta: { title: 'message.exam.menu.index' }
      },
      {
        path: 'exam-start',
        component: createNameComponent(() => import('@/views/exam/exam-start.vue')),
        meta: { title: 'message.exam.menu.start' }
      },
      {
        path: 'exam-test',
        component: createNameComponent(() => import('@/views/exam/exam-test.vue')),
        meta: { title: 'message.exam.menu.start' }
      },
    ]
  }
]

// @ts-ignore
export default route
