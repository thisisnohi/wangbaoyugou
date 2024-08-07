import type { Route } from '../index.type'
import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'
const route: Route[] = [
  {
    path: '/component',
    component: Layout,
    redirect: '/component/table',
    meta: { title: 'message.menu.component.name', icon: 'sfont system-component' },
    alwayShow: true,
    children: [
      {
        path: 'button',
        component: createNameComponent(() => import('@/views/main/components/button/index.vue')),
        meta: { title: 'message.menu.component.button' },
      },
      {
        path: 'office',
        component: createNameComponent(() => import('@/views/office/view/index.vue')),
        redirect: '/component/office/pptx',
        meta: { title: 'office文件预览' },
        children: [
          {
            path: 'pptx',
            component: createNameComponent(() => import('@/views/office/view/pptx.vue')),
            meta: { title: 'pptx' },
          },
          {
            path: 'word',
            component: createNameComponent(() => import('@/views/office/view/word.vue')),
            meta: { title: 'word' },
          },
          {
            path: 'excel',
            component: createNameComponent(() => import('@/views/office/view/excel.vue')),
            meta: { title: 'excel' },
          },
        ]
      },
      {
        path: 'vue-office',
        component: createNameComponent(() => import('@/views/office/view/index.vue')),
        redirect: '/component/vue-office/word',
        meta: { title: 'vue-office' },
        children: [
          {
            path: 'word',
            component: createNameComponent(() => import('@/views/office/viewOffice/word.vue')),
            meta: { title: 'word' },
          },
          {
            path: 'excel',
            component: createNameComponent(() => import('@/views/office/viewOffice/excel.vue')),
            meta: { title: 'excel' },
          },
          {
            path: 'viewOfficePdf',
            component: createNameComponent(() => import('@/views/office/viewOffice/pdf.vue')),
            meta: { title: 'viewOfficePdf' },
          },
        ]
      },
      {
        path: 'pdf',
        component: createNameComponent(() => import('@/views/office/view/index.vue')),
        redirect: '/component/pdf/vue-pdf',
        meta: { title: 'pdf' },
        children: [
          {
            path: 'vue-pdf',
            component: createNameComponent(() => import('@/views/office/pdf/vue-pdf.vue')),
            meta: { title: 'vue-pdf' },
          },
          {
            path: 'viewOfficePdf',
            component: createNameComponent(() => import('@/views/office/viewOffice/pdf.vue')),
            meta: { title: 'viewOfficePdf' },
          },
        ]
      },

      {
        path: 'wordEditor',
        component: createNameComponent(() => import('@/views/main/components/wordEditor/index.vue')),
        meta: { title: 'message.menu.component.wordEditor' },
      },
      {
        path: 'tinymceTest',
        component: createNameComponent(() => import('@/views/main/components/wordEditor/tinymce-test.vue')),
        meta: { title: 'Tinymce' },
      },
      {
        path: 'quill',
        component: createNameComponent(() => import('@/views/main/components/wordEditor/quill.vue')),
        meta: { title: 'quill' },
      },
      {
        path: 'mdEditor',
        component: createNameComponent(() => import('@/views/main/components/mdEditor/index.vue')),
        meta: { title: 'message.menu.component.mdEditor' },
      },
      {
        path: 'codeEditor',
        component: createNameComponent(() => import('@/views/main/components/codeEditor/index.vue')),
        meta: { title: 'message.menu.component.codeEditor' },
      },
      {
        path: 'jsonEditor',
        component: createNameComponent(() => import('@/views/main/components/jsonEditor/index.vue')),
        meta: { title: 'message.menu.component.jsonEditor' },
      },
      {
        path: 'dragPane',
        component: createNameComponent(() => import('@/views/main/components/dragPane/index.vue')),
        meta: { title: 'message.menu.component.dragPane' },
      },
      {
        path: 'map',
        component: createNameComponent(() => import('@/views/main/components/map/index.vue')),
        meta: { title: 'message.menu.component.map' },
      },
      {
        path: 'cutPhoto',
        component: createNameComponent(() => import('@/views/main/components/cutPhoto/index.vue')),
        meta: { title: 'message.menu.component.cutPhoto' },
      },
      {
        path: 'rightMenu',
        component: createNameComponent(() => import('@/views/main/components/rightMenu/index.vue')),
        meta: { title: 'message.menu.component.rightMenu' },
      },
      {
        path: 'exportExcel',
        component: createNameComponent(() => import('@/views/main/components/exportExcel/index.vue')),
        meta: { title: 'message.menu.component.exportExcel' },
      },
    ]
  }
]

export default route
