import { createRouter, createWebHistory } from 'vue-router'
import ChannelListPage from '@/views/user/advertiser/ChannelListPage.vue'
import ChannelDetailPage from '@/views/user/advertiser/ChannelDetailPage.vue'
import HomeView from '../views/HomeView.vue'
import AdvertiserMyPage from '@/views/user/advertiser/MyPage.vue'
import DetailPage from '@/views/user/advertiser/DetailPage.vue'
import InfluencerMyPage from '@/views/user/influencer/MyPage.vue'
import DetailContract from '@/components/user/influencer/detail/DetailContract.vue'
import UserMyPage from '@/views/user/general/MyPage.vue'


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
      path: '/login',
      name: 'login',
      component: () => import('@/views/oAuth/login/UserLogin.vue')
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('@/views/oAuth/signUp/UserSignUp.vue')
    },
    {
      path: '/findid',
      name: 'findid',
      component: () => import('@/views/oAuth/findId/FindId.vue')
    },
    {
      path: '/findpassword',
      name: 'findpassword',
      component: () => import('@/views/oAuth/findPassword/FindPassword.vue')
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
      props: true,
      children: [
        {
          path: 'contract/:contractId',
          name: 'campaign-contract-detail',
          component: DetailContract,
          props: true
        }
      ]
    },
    {
      path: '/campaign/:id/proposal',
      name: 'campaign-proposal',
      component: () => import('../views/user/influencer/CampaignProposalView.vue'),
      props: true
    },
    {
      path: '/mypage',
      name: 'user-mypage',
      component: UserMyPage
    },
    {
      path: '/mypage/influencer',
      name: 'influencer-mypage',
      component: InfluencerMyPage
    },
    {
      path: '/mypage/advertiser',
      name: 'advertiser-mypage',
      component: AdvertiserMyPage
    },
    {
      path: '/proposal/:id',
      name: 'proposal-detail',
      component: () => import('../views/user/influencer/Detail.vue'),
      props: true
    },
    {
      path: '/mypage/advertiser/campaigns',
      name: 'advertiser-campaign-list',
      component: AdvertiserMyPage
    },
    {
      path: '/mypage/campaign-detail/:id',
      name: 'advertiser-campaign-detail',
      component: DetailPage,
      props: true
    },
    {
      path: '/mypage/advertiser/campaign-register',
      name: 'campaign-register',
      component: AdvertiserMyPage
    },
    {
      path: '/mypage/advertiser/contracts',
      name: 'advertiser-contracts',
      component: AdvertiserMyPage
    },
    {
      path: '/contract/:contractId',
      name: 'contract-detail',
      component: DetailContract,
      props: true
    },
    {
      path: '/contract/create',
      name: 'contract-create',
      component: () => import('../views/user/advertiser/ContractCreate.vue')
    },
    {
      path: '/oauth/google/success',
      name: 'GoogleSuccess',
      component: () => import('@/views/oAuth/login/GoogleCallback.vue')
    },
    {
      path: '/go',
      name: 'redirect-url',
      component: () => import('../views/common/RedirectUrl.vue')
    },
    {
      path: '/success',
      name: 'payment-success',
      component: () => import('../views/common/PaymentSuccess.vue')
    },
    {
      path: '/google-callback',
      name: 'GoogleCallback',
      component: () => import('@/components/user/general/mypage/MyPageChannelChange.vue')
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