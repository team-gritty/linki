import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home',
    },

    {
      path: '/home',
      name: 'home',
      component: HomeView
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/oAuth/login/AdminLogin.vue')
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('@/views/oAuth/signUp/AdminSignUp.vue')
    },

    {
      path: '/memberList',
      name: 'memberList',
      component: () => import('../views/admin/user/GeneralUserListView.vue')
    },
    {
      path: '/advertiserUserList',
      name: 'advertiserUserList',
      component: () => import('../views/admin/user/AdvertiserUserListView.vue')
    },
    {
      path: '/influencerUserList',
      name: 'influencerUserList',
      component: () => import('../views/admin/user/InfluencerUserListView.vue')
    },
    {
      path: '/subscriberUserList',
      name: 'subscriberUserList',
      component: () => import('../views/admin/user/SubscriberUserListView.vue')
    },
    {
      path: '/contractList',
      name: 'contractList',
      component: () => import('../views/admin/contract/ContractListView.vue')
    },
    {
      path: '/campaignList',
      name: 'campaignList',
      component: () => import('../views/admin/contract/CampaignView.vue')
    },
    {
      path: '/settlement',
      name: 'settlement',
      component: () => import('../views/admin/payment/SettlementView.vue')
    },
    {
      path: '/subscriptionPayment',
      name: 'subscriptionPayment',
      component: () => import('../views/admin/payment/SubscriptionPaymentView.vue')
    },
    {
      path: '/influencerReviews',
      name: 'influencerReviews',
      component: () => import('../views/admin/operations/InfluencerReviews.vue')
    },
    {
      path: '/advertisersReviews',
      name: 'advertisersReviews',
      component: () => import('../views/admin/operations/AdvertisersReviews.vue')
    },
    {
      path: '/adminSignUp',
      name: 'adminSignUp',
      component: () => import('../views/admin/operations/AdminSignUp.vue')
    },
    {
      path: '/mypage',
      name: 'MyPage',
      component: () => import('@/views/admin/mypage/MyPage.vue')
    },
    {
      path: '/mypage/change-password',
      name: 'changePassword',
      component: () => import('@/views/admin/mypage/MyPage.vue')
    }
  ]
})

export default router
