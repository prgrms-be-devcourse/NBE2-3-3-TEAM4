import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'MainPage',
      component: () => import('../views/MainPage.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginPage.vue'),
    },
    {
      path: '/my-ticket',
      name: 'my-ticket',
      component: () => import('../views/MyTicketPage.vue'),
    },
    {
      path: '/my-page',
      name: 'my-page',
      component: () => import('../views/MyPage.vue'),
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('../views/SignupPage.vue'),
    },
    {
      path: '/my-page/edit',
      name: 'edit',
      component: () => import('../views/EditMyInfoPage.vue'),
    },
    {
      path: '/car',
      name: 'car-manage',
      component: () => import('../views/CarManagePage.vue'),
    },
    {
      path: '/car/add',
      name: 'add-car',
      component: () => import('../views/AddCarPage.vue'),
    },
    {
      path: '/search',
      name: 'search',
      component: () => import('../views/SearchPage.vue'),
    },
    {

      path: '/parking/:id',
      name: 'parking',
      component: () => import('../views/ParkingPage.vue'),
    },
    {
      path: '/parking/:id/payment/:ticketId',
      name: 'payment',
      component: () => import('../views/PaymentPage.vue'),
    },
    {
      path: '/payment/process/:orderId',
      name: 'payment-process',
      component: () => import('../views/PaymentProcessPage.vue'),
    }, {
      path: '/payment/success',
      name: 'payment-success',
      component: () => import('../views/PaymentSuccessPage.vue'),
    }, {
      path: '/my-ticket/cancel/:orderId',
      name: 'cancel-order',
      component: () => import('../views/CancelOrderPage.vue'),
    }, {
      path: '/ticket-history',
      name: 'ticket-history',
      component: () => import('../views/MyOrderHistoryPage.vue'),
    }, {
      path: '/ticket-detail',
      name: 'ticket-detail',
      component: () => import('../views/TicketHistoryDetailPage.vue'),
    }
  ],
})

export default router
