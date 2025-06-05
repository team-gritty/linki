import { createRouter, createWebHistory } from 'vue-router'
import ChannelListPage from '@/views/user/advertiser/ChannelListPage.vue'
import ChannelDetailPage from '@/views/user/advertiser/ChannelDetailPage.vue'
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
      path: '/channels',
      name: 'influencer-list',
      component: ChannelListPage
    },
    {
      path: '/channels/:id',
      name: 'channel-detail',
      component: ChannelDetailPage
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
    }
  ]
})

export default router
