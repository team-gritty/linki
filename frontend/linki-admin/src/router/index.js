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
    }
  ]
})

export default router
