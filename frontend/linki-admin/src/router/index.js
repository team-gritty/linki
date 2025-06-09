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
      path: '/memberList',
      name: 'memberList',
      component: () => import('../views/admin/GeneralUserListView.vue')
    },
    {
      path: '/advertiserUserList',
      name: 'advertiserUserList',
      component: () => import('../views/admin/AdvertiserUserListView.vue')
    },
    {
      path: '/influencerUserList',
      name: 'influencerUserList',
      component: () => import('../views/admin/InfluencerUserListView.vue')
    },
    {
      path: '/subscriberUserList',
      name: 'subscriberUserList',
      component: () => import('../views/admin/SubscriberUserListView.vue')
    },
    {
      path: '/contractList',
      name: 'contractList',
      component: () => import('../views/admin/ContractListView.vue')
    },
    {
      path: '/campaignList',
      name: 'campaignList',
      component: () => import('../views/admin/CampaignView.vue')
    },
    {
      path: '/settlement',
      name: 'settlement',
      component: () => import('../views/admin/SettlementView.vue')
    },
    {
      path: '/subscriptionPayment',
      name: 'subscriptionPayment',
      component: () => import('../views/admin/SubscriptionPaymentView.vue')
    },
    {
      path: '/influencerReviews',
      name: 'influencerReviews',
      component: () => import('../views/admin/operations/influencerReviews.vue')
    },
    {
      path: '/adminReviews',
      name: 'adminReviews',
      component: () => import('../views/admin/operations/AdminReviews.vue')
    },
    {
      path: '/adminSignUp',
      name: 'adminSignUp',
      component: () => import('../views/admin/operations/AdminSignUp.vue')
    },
    {
      path: '/mypage',
      component: () => import('@/views/admin/mypage/MyPage.vue'),
      children: [
        {
          path: '',
          name: 'MyPageProfile',
          component: () => import('@/views/admin/mypage/MyPageProfileView.vue')
        },
        {
          path: 'password',
          name: 'MyPagePassword',
          component: () => import('@/views/admin/mypage/MyPagePasswordView.vue')
        }
      ]
    }
  ]
})

export default router
