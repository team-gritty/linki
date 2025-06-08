import { createRouter, createWebHistory } from 'vue-router'
import ChannelListPage from '@/views/user/advertiser/ChannelListPage.vue'
import ChannelDetailPage from '@/views/user/advertiser/ChannelDetailPage.vue'
import HomeView from '../views/HomeView.vue'
import CampaignListPage from '@/views/user/advertiser/CampaignListPage.vue'
import CampaignDetailPage from '@/views/user/advertiser/CampaignDetailPage.vue'
import CampaignRegisterPage from '@/views/user/advertiser/CampaignRegisterPage.vue'
import ContractListPage from '@/views/user/advertiser/ContractListPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home'
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
      component: ChannelDetailPage,
      props: true
    },
    {
      path: '/campaigns',
      name: 'campaigns',
      component: () => import('../views/user/influencer/CampaignListView.vue')
    },
    {
      path: '/campaign/:id',
      name: 'influencer-campaign-detail',
      component: () => import('../views/user/influencer/CampaignDetailView.vue'),
      props: true
    },
    {
      path: '/campaign/:id/proposal',
      name: 'campaign-proposal',
      component: () => import('../views/user/influencer/CampaignProposalView.vue'),
      props: true
    },
    {
      path: '/mypage',
      name: 'mypage',
      component: () => import('../views/user/influencer/MyPageView.vue')
    },
    {
      path: '/mypage/advertiser',
      name: 'advertiser-mypage',
      component: CampaignListPage
    },
    {
      path: '/proposal/:id',
      name: 'proposal-detail',
      component: () => import('../views/user/influencer/ProposalDetailView.vue'),
      props: true
    },
    {
      path: '/mypage/campaign-list',
      name: 'advertiser-campaign-list',
      component: CampaignListPage
    },
    {
      path: '/mypage/campaign-detail/:id',
      name: 'advertiser-campaign-detail',
      component: CampaignDetailPage
    },
    {
      path: '/mypage/campaign-register',
      name: 'campaign-register',
      component: CampaignRegisterPage
    },
    {
      path: '/mypage/contract-list',
      name: 'ContractList',
      component: ContractListPage
    }
  ],
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

export default router
