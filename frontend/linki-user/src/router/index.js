import HomeView from '@/views/HomeView.vue'
import { createRouter, createWebHistory } from 'vue-router'

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
      component: () => import('../views/CampaignListView.vue')
    },
    {
      path: '/campaign/:id',
      name: 'campaign-detail',
      component: () => import('../views/CampaignDetailView.vue')
    },
    {
      path: '/campaign/:id/proposal',
      name: 'campaign-proposal',
      component: () => import('../views/CampaignProposalView.vue')
    }
  ]
})

export default router
