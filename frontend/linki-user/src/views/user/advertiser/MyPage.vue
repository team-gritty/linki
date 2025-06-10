<template>
  <div class="list-layout">
    <MyPageSideBar />
    <main class="list-content">
      <!-- 프로필/비밀번호 변경 -->
      <MyPageProfile v-if="$route.name === 'advertiser-profile'" />
      
      <!-- 광고 캠페인 -->
      <MyPageCampaignRegister v-if="$route.name === 'campaign-register'" />
      <MyPageCampaignList v-if="$route.name === 'advertiser-campaign-list'" />
      
      <!-- 계약서 관리 -->
      <MyPageContractList v-if="$route.name === 'Contracts'" />
      
      <!-- 구독 관리 -->
      <MyPageSubscription v-if="$route.name === 'subscription-status'" />
    </main>
  </div>
</template>

<script setup>
import MyPageSideBar from '@/components/user/advertiser/mypage/MyPageSideBar.vue'
import MyPageProfile from '@/components/user/advertiser/mypage/MyPageProfile.vue'
import MyPageCampaignRegister from '@/components/user/advertiser/mypage/MyPageCampaignRegister.vue'
import MyPageCampaignList from '@/components/user/advertiser/mypage/MyPageCampaignList.vue'
import MyPageContractList from '@/components/user/advertiser/mypage/MyPageContractList.vue'
import MyPageSubscription from '@/components/user/advertiser/mypage/MyPageSubscription.vue'
</script>

<style>
@import '@/assets/css/mypage.css';
</style> 