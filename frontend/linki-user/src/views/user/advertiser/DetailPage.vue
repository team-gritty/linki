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
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import DetailHeader from '@/components/user/advertiser/detail/DetailHeader.vue'
import DetailCampaign from '@/components/user/advertiser/detail/DetailCampaign.vue'
import DetailProposalList from '@/components/user/advertiser/detail/DetailProposalList.vue'
import DetailContractList from '@/components/user/advertiser/detail/DetailContractList.vue'
import DetailChat from '@/components/user/advertiser/detail/DetailChat.vue'

const route = useRoute()
const campaignId = computed(() => route.params.id)
const currentTab = ref('campaign.info')
</script>

<style>
@import '@/assets/css/detail.css';

.detail-page {
  min-height: 100vh;
  background: #f8f8f8;
}

.content-area {
  padding: 24px;
}
</style> 