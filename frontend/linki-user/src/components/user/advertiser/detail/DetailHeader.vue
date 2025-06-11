<template>
  <div class="detail-header">
    <div class="campaign-summary-box">
      <div class="summary-left">
        <img class="summary-thumb" :src="campaignInfo.productImg" alt="제품 썸네일" />
        <div class="summary-info">
          <div class="summary-title">{{ campaignInfo.productName }}</div>
          <div class="summary-sub">{{ campaignInfo.companyName }}</div>
        </div>
      </div>
      <button class="go-list-btn" @click="goToList">캠페인 목록 <span class="arrow">→</span></button>
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
import { campaignAPI } from '@/api/campaign'

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
  productImg: '',
  productName: '',
  companyName: '',
  productDesc: '',
  productDeadline: '',
  productCondition: '',
  productCategory: ''
})

const fetchCampaignInfo = async () => {
  try {
    console.log('Fetching campaign info for ID:', route.params.id)
    const data = await campaignAPI.getMyCampaignDetail(route.params.id)
    console.log('Received campaign data:', data)
    campaignInfo.value = data
  } catch (error) {
    console.error('Failed to fetch campaign info:', error)
    // 에러 시 더미 데이터로 폴백
    campaignInfo.value = {
      productImg: 'https://cdn.pixabay.com/photo/2016/03/31/19/14/controller-1294077_1280.png',
      productName: '게임기',
      companyName: 'HAVIT HV-G92 Gamepad',
      productDesc: `Ipsum has been the industry's standard dummy text ever since the 1500s...`,
      productDeadline: '2025-05-26',
      productCondition: '평균 조회수 2만 회 이상',
      productCategory: '게임'
    }
  }
}

const updateTab = (tab) => {
  emit('update:currentTab', tab)
}

const goToList = () => {
  router.push('/mypage/advertiser/campaign-list')
}

onMounted(() => {
  fetchCampaignInfo()
})
</script>

<style scoped>
@import '@/assets/css/detail.css';

</style> 