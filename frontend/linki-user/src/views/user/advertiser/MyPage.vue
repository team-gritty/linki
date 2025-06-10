<template>
  <div class="list-layout">
    <MyPageSideBar v-model:currentMenu="currentMenu" />
    <main class="list-content">
      <!-- 프로필/비밀번호 변경 -->
      <MyPageProfile v-if="currentMenu === 'profile.basic'" />
      
      <!-- 광고 캠페인 -->
      <MyPageCampaignList v-if="currentMenu === 'campaign.list'" />
      <MyPageCampaignRegister v-if="currentMenu === 'campaign.register'" />
      
      <!-- 계약서 관리 -->
      <MyPageContractList v-if="currentMenu === 'contract.list'" />
      <MyPageOngoingContracts v-if="currentMenu === 'contract.ongoing'" />
      
      <!-- 구독 관리 -->
      <MyPageSubscription v-if="currentMenu === 'subscription.status'" />
      
      <!-- 리뷰 관리 -->
      <MyPageWrittenReviews v-if="currentMenu === 'review.written'" />
      <MyPageReceivedReviews v-if="currentMenu === 'review.received'" />
    </main>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import MyPageSideBar from '@/components/user/advertiser/mypage/MyPageSideBar.vue'
import MyPageProfile from '@/components/user/advertiser/mypage/MyPageProfile.vue'
import MyPageCampaignRegister from '@/components/user/advertiser/mypage/MyPageCampaignRegister.vue'
import MyPageCampaignList from '@/components/user/advertiser/mypage/MyPageCampaignList.vue'
import MyPageContractList from '@/components/user/advertiser/mypage/MyPageContractList.vue'
import MyPageOngoingContracts from '@/components/user/advertiser/mypage/MyPageOngoingContracts.vue'
import MyPageWrittenReviews from '@/components/user/advertiser/mypage/MyPageWrittenReviews.vue'
import MyPageReceivedReviews from '@/components/user/advertiser/mypage/MyPageReceivedReviews.vue'
import MyPageSubscription from '@/components/user/advertiser/mypage/MyPageSubscription.vue'

const route = useRoute()

function getMenuFromRoute(routeName) {
  switch (routeName) {
    case 'advertiser-campaign-list':
      return 'campaign.list'
    case 'campaign-register':
      return 'campaign.register'
    case 'ContractList':
      return 'contract.list'
    // Add more mappings as needed
    default:
      return 'profile.basic'
  }
}

const currentMenu = ref(getMenuFromRoute(route.name))

watch(() => route.name, (val) => {
  currentMenu.value = getMenuFromRoute(val)
  console.log('[MyPage] currentMenu changed by route:', val, '->', currentMenu.value)
})

watch(currentMenu, (val) => {
  console.log('[MyPage] currentMenu changed:', val)
})
</script>

<style>
@import '@/assets/css/mypage.css';
</style> 