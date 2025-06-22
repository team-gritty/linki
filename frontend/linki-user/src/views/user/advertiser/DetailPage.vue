<template>
  <div class="detail-page">
    <DetailHeader
      v-model:currentTab="currentTab"
    />
    
    <div class="content-area">
      <!-- 캠페인 상세 정보 -->
      <DetailCampaign 
        v-if="currentTab === 'campaign.info'"
        :campaign-id="campaignId"
      />
      
      <!-- 제안서 목록 -->
      <DetailProposalList 
        v-if="currentTab === 'proposal.list'"
        :campaign-id="campaignId"
      />
      
      <!-- 계약서 목록 -->
      <DetailContractList 
        v-if="currentTab === 'contract.list'"
        :campaign-id="campaignId"
      />
      
      <!-- 채팅 -->
      <DetailChat 
        v-if="currentTab === 'chat.room'"
        :campaign-id="campaignId"
        :chat-id="chatId"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import DetailHeader from '@/components/user/advertiser/detail/DetailHeader.vue'
import DetailCampaign from '@/components/user/advertiser/detail/DetailCampaign.vue'
import DetailProposalList from '@/components/user/advertiser/detail/DetailProposalList.vue'
import DetailContractList from '@/components/user/advertiser/detail/DetailContractList.vue'
import DetailChat from '@/components/user/advertiser/detail/DetailChat.vue'

const route = useRoute()
const campaignId = computed(() => route.params.id)
const currentTab = ref('campaign.info')

// URL 쿼리 파라미터에서 탭 정보 초기화
onMounted(() => {
  const tabFromQuery = route.query.tab
  if (tabFromQuery) {
    currentTab.value = tabFromQuery
  }
})

// route.query 변경 감지
watch(() => route.query.tab, (newTab) => {
  if (newTab) {
    currentTab.value = newTab
  }
})
</script>

<style>
@import '@/assets/css/detail.css';

</style> 