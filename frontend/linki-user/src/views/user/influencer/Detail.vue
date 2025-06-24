<template>
  <div class="detail-page">
    <DetailHeader
      :currentTab="currentTab"
      :tabs="tabs"
      @update:currentTab="currentTab = $event"
      @go-to-proposal-list="goToProposalList"
      @go-to-campaign-detail="goToCampaignDetail"
      @update:chatRoom="chatRoom = $event"
    />
    
    <div class="content-area">
      <!-- 캠페인 상세 정보 -->
      <DetailCampaign 
        v-if="currentTab === 'campaign'"
      />
      
      <!-- 제안서 -->
      <DetailProposal
        v-if="currentTab === 'proposal'"
      />
      
      <!-- 채팅 -->
      <DetailChat 
        v-if="currentTab === 'chat'"
        :chatRoom="chatRoom"
      />
      
      <!-- 계약서 -->
      <DetailContract 
        v-if="currentTab === 'contract'"
        :detailData="{
          contractId: proposal?.contractId || route.query.contractId,
          contract: proposal?.contract || {},
          campaign: campaignDetail
        }"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import httpClient from '@/utils/httpRequest'
import { proposalAPI } from '@/api/proposal'

import DetailHeader from '@/components/user/influencer/detail/DetailHeader.vue'
import DetailProposal from '@/components/user/influencer/detail/DetailProposal.vue'
import DetailCampaign from '@/components/user/influencer/detail/DetailCampaign.vue'
import DetailChat from '@/components/user/influencer/detail/DetailChat.vue'
import DetailContract from '@/components/user/influencer/detail/DetailContract.vue'

const route = useRoute()
const router = useRouter()
const currentTab = ref('proposal')
const proposal = ref(null)
const campaignDetail = ref(null)
const contractId = ref(null)
const campaignId = computed(() => proposal.value?.campaign_id || proposal.value?.product_id)
const chatRoom = ref(null)

const tabs = [
  { id: 'campaign', name: '캠페인내용' },
  { id: 'proposal', name: '제안서' },
  { id: 'chat', name: '채팅' },
  { id: 'contract', name: '계약서' }
]

// URL의 query parameter에서 tab 정보 가져오기
const initializeTab = () => {
  const tabFromQuery = route.query.tab
  if (tabFromQuery && tabs.some(tab => tab.id === tabFromQuery)) {
    currentTab.value = tabFromQuery
  }
}

const fetchData = async () => {
  try {
    const proposalId = route.params.id;
    console.log('Fetching proposal with ID:', proposalId);
    
    // 제안서 상세 정보 조회 (캠페인 정보 포함)
    const response = await proposalAPI.getProposalDetail(proposalId);
    console.log('Fetched proposal detail:', response);
    
    proposal.value = response;
    
    // 계약 ID 설정
    if (response.contractId) {
      contractId.value = response.contractId;
      console.log('Contract ID found:', contractId.value);
    }
    
    // 캠페인 정보도 포함되어 있다면 설정
    if (response.campaignTitle) {
      campaignDetail.value = response;
    }
    
  } catch (error) {
    console.error('Failed to fetch data:', error);
    alert(error.message || '데이터를 불러오는데 실패했습니다.');
    router.push({ name: 'influencer-mypage', query: { currentMenu: 'campaign.proposals' } });
  }
};

// route.query가 변경될 때마다 tab 초기화
watch(() => route.query, initializeTab)



const goToCampaignDetail = () => {
  if (campaignDetail.value?.campaign_id) {
    router.push(`/campaign/${campaignDetail.value.campaign_id}`);
  }
};

const goToProposalList = () => {
  router.push({ name: 'influencer-mypage', query: { currentMenu: 'campaign.proposals' } });
};

const goToContractDetail = (contractId) => {
  router.push({
    path: `/proposal/${contractId}`,
    query: { tab: 'contract' }
  });
};

onMounted(() => {
  fetchData();
  initializeTab();
});
</script> 