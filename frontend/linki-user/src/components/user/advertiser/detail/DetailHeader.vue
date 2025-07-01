<template>
  <div class="detail-header">
    <div class="campaign-summary-box">
      <div class="summary-left">
        <img class="summary-thumb" :src="campaignInfo.campaignImg" alt="제품 썸네일" />
        <div class="summary-info">
          <div class="summary-title">{{ campaignInfo.campaignName }}</div>
          <div class="summary-sub">{{ campaignInfo.companyName }}</div>
        </div>
      </div>
      <button class="go-list-btn" @click="goToList">캠페인 목록</button>
    </div>
    <div class="campaign-tabs">
      <div class="tab" 
           :class="{active: currentTab === 'campaign.info'}" 
           @click="updateTab('campaign.info')">캠페인 설명</div>
      <div class="tab" 
           :class="{active: currentTab === 'proposal.list'}" 
           @click="updateTab('proposal.list')">제안서 목록</div>
      <div class="tab" 
           :class="{active: currentTab === 'chat.room'}" 
           @click="updateTab('chat.room')">채팅 목록</div>
      <div class="tab" 
           :class="{active: currentTab === 'contract.list'}" 
           @click="updateTab('contract.list')">계약서 목록</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import campaignApi from '@/api/advertiser/advertiser-campaign'

const props = defineProps({
  currentTab: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['update:currentTab'])
const router = useRouter()
const route = useRoute()

const campaignInfo = ref({
  campaignImg: '',
  campaignName: '',
  companyName: '',
  campaignDesc: '',
  campaignDeadline: '',
  campaignCondition: '',
  campaignCategory: ''
})

const fetchCampaignInfo = async () => {
  try {
    console.log('Fetching campaign info for ID:', route.params.id)
    const data = await campaignApi.getCampaignDetail(route.params.id)
    console.log('Received campaign data:', data)
    campaignInfo.value = data
  } catch (error) {
    console.error('Failed to fetch campaign info:', error)
 
  }
}

const updateTab = (tab) => {
  // 새로운 쿼리 객체 생성
  const newQuery = { ...route.query }
  
  // 탭에 따른 쿼리 파라미터 매핑
  const tabQueryMapping = {
    'campaign.info': 'info',
    'proposal.list': 'proposal', 
    'contract.list': 'contract',
    'chat.room': 'chat'
  }
  
  // tab 쿼리 파라미터 설정
  newQuery.tab = tabQueryMapping[tab] || 'info'
  
  // 각 탭에 맞지 않는 쿼리 파라미터 정리
  if (tab !== 'contract.list') {
    delete newQuery.contractId
  }
  if (tab !== 'chat.room') {
    delete newQuery.chatId
  }
  
  // URL 업데이트
  router.push({
    path: route.path,
    query: newQuery
  })
  
  emit('update:currentTab', tab)
}

// 캠페인 목록으로 되돌아가기 
const goToList = () => {
  router.push('/mypage/advertiser/campaigns')
}

onMounted(() => {
  fetchCampaignInfo()
})
</script>

<style scoped>
@import '@/assets/css/detail.css';

</style> 