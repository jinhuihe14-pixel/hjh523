import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/components/layout/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/business/report/Dashboard.vue'),
        meta: { title: '首页概览', icon: 'HomeFilled' }
      },
      {
        path: 'customer',
        name: 'Customer',
        component: () => import('@/views/business/customer/CustomerList.vue'),
        meta: { title: '客户管理', icon: 'User' }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/business/order/OrderList.vue'),
        meta: { title: '订单管理', icon: 'Document' }
      },
      {
        path: 'contract',
        name: 'Contract',
        component: () => import('@/views/business/contract/ContractList.vue'),
        meta: { title: '合同管理', icon: 'Tickets' }
      },
      {
        path: 'report',
        name: 'Report',
        component: () => import('@/views/business/report/ReportList.vue'),
        meta: { title: '报表中心', icon: 'DataLine' }
      },
      {
        path: 'operation-log',
        name: 'OperationLog',
        component: () => import('@/views/business/report/OperationLog.vue'),
        meta: { title: '操作记录', icon: 'Notebook' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isAuthenticated = userStore.token

  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.name === 'Login' && isAuthenticated) {
    next({ path: '/' })
  } else {
    next()
  }
})

export default router
