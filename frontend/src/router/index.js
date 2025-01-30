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
      path: '/checkout',
      name: 'checkout',
      component: () => import('../views/CheckoutPage.vue'),
    },
    {
      path: '/checkout-success',
      name: 'checkout-success',
      component: () => import('../views/CheckoutSuccessPage.vue'),
    }
  ],
})

export default router
