import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'

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
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue')
    },
    {
      path: '/campaigns',
      name: 'campaigns',
      component: () => import('../views/user/influencer/CampaignListView.vue')
    },
    {
      path: '/campaign/:id',
      name: 'campaign-detail',
      component: () => import('../views/user/influencer/CampaignDetailView.vue')
    },
    {
      path: '/campaign/:id/proposal',
      name: 'campaign-proposal',
      component: () => import('../views/user/influencer/CampaignProposalView.vue')
    },
    {
      path: '/mypage',
      name: 'mypage',
      component: () => import('../views/user/influencer/MyPageView.vue')
    },
    {
      path: '/proposals/:id',
      name: 'proposal-detail',
      component: () => import('../views/user/influencer/ProposalDetailView.vue')
    }
  ]
})

export default router
