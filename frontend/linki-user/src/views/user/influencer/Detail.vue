<template>
  <div class="detail-page">
    <DetailHeader
      :campaignDetail="campaignDetail"
      :currentTab="currentTab"
      :tabs="tabs"
      @update:currentTab="currentTab = $event"
      @go-to-proposal-list="goToProposalList"
      @go-to-campaign-detail="goToCampaignDetail"
    />
    
    <div class="content-area">
      <!-- 캠페인 상세 정보 -->
      <DetailCampaign 
        v-if="currentTab === 'campaign'"
        :campaign-id="campaignId"
        :detailData="{ campaign: campaignDetail }"
      />
      
      <!-- 제안서 -->
      <DetailProposal
        v-if="currentTab === 'proposal' && proposal"
        :detailData="{
          proposal: proposal,
          campaign: campaignDetail
        }"
        @save-proposal="saveProposal"
        @delete-proposal="deleteProposal"
      />
      
      <!-- 채팅 -->
      <DetailChat 
        v-if="currentTab === 'chat'"
        :campaign-id="campaignId"
      />
      
      <!-- 계약서 -->
      <DetailContract 
        v-if="currentTab === 'contract'"
        :detailData="{
          contract: {
            contractId: contractId,
            ...proposal?.contract || {}
          },
          campaign: campaignDetail
        }"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
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
    const id = route.params.id;
    console.log('Fetching proposal with ID:', id);
    
    // 계약서 탭이고 query에 계약 정보가 있는 경우
    if (route.query.tab === 'contract' && route.query.contractId) {
      // URL query에서 계약 정보 가져오기
      contractId.value = route.query.contractId;
      
      // 계약 정보로 임시 proposal 객체 생성
      proposal.value = {
        id: id,
        contractId: route.query.contractId,
        contractTitle: route.query.contractTitle,
        contractStartDate: route.query.contractStartDate,
        contractEndDate: route.query.contractEndDate,
        contractAmount: route.query.contractAmount,
        contractStatus: route.query.contractStatus
      };
      
      return; // 여기서 함수 종료
    }

    // 1. 전체 제안서 목록 조회
    const proposalsResponse = await axios.get('/v1/api/influencer/proposals');
    console.log('All proposals:', proposalsResponse.data);
    
    // proposal_id로 해당 제안서 찾기
    const foundProposal = proposalsResponse.data.find(p => p.proposal_id === id);
    console.log('Found proposal:', foundProposal);
    
    if (!foundProposal) {
      throw new Error('제안서를 찾을 수 없습니다.');
    }
    
    proposal.value = foundProposal;
    
    // 2. 캠페인 정보 조회
    const campaignId = foundProposal.campaign_id;
    console.log('Campaign ID:', campaignId);
    
    if (!campaignId) {
      throw new Error('캠페인 ID를 찾을 수 없습니다.');
    }
    
    // 캠페인 정보 조회
    const campaignsResponse = await axios.get('/v1/api/influencer/campaigns');
    const foundCampaign = campaignsResponse.data.find(c => c.campaignId === campaignId);
    console.log('Found campaign:', foundCampaign);
    
    if (foundCampaign) {
      campaignDetail.value = foundCampaign;
    } else {
      throw new Error('캠페인 정보를 찾을 수 없습니다.');
    }
    
    // 3. 계약 정보 조회
    const contractsResponse = await axios.get('/v1/api/influencer/contracts');
    console.log('Fetching contracts for campaign:', campaignId);
    
    // campaignId로 계약 찾기
    const foundContract = contractsResponse.data.find(c => c.campaignId === campaignId);
    console.log('Found contract:', foundContract);
    
    if (foundContract) {
      contractId.value = foundContract.contractId;
      // contract 정보를 proposal에 추가
      proposal.value.contract = foundContract;
    } else {
      console.log('No contract found for campaign:', campaignId);
    }
    
  } catch (error) {
    console.error('Failed to fetch data:', error);
    if (!route.query.contractId) {
      alert(error.message || '데이터를 불러오는데 실패했습니다.');
      router.push('/mypage/influencer');
    }
  }
};

// route.query가 변경될 때마다 tab 초기화
watch(() => route.query, initializeTab)

const saveProposal = async (newContent) => {
  try {
    const proposalId = route.params.id;
    if (!proposalId) throw new Error('제안서 ID를 찾을 수 없습니다.');

    const updatedData = {
      ...proposal.value,
      contents: newContent,
      submitted_at: new Date().toISOString()
    };

    await proposalAPI.updateProposal(proposalId, updatedData);
    await fetchData();
    alert('제안서가 성공적으로 수정되었습니다.');
  } catch (error) {
    console.error('Failed to update proposal:', error);
    alert(`제안서 수정에 실패했습니다. 오류: ${error.message}`);
  }
};

const deleteProposal = async () => {
  if (!confirm('정말로 이 제안서를 삭제하시겠습니까?')) return;
  
  try {
    const proposalId = route.params.id;
    if (!proposalId) throw new Error('제안서 ID를 찾을 수 없습니다.');

    await proposalAPI.deleteProposal(proposalId);
    router.push('/mypage');
  } catch (error) {
    console.error('Failed to delete proposal:', error);
    alert(error.message || '제안서 삭제에 실패했습니다.');
  }
};

const goToCampaignDetail = () => {
  if (campaignDetail.value?.campaign_id) {
    router.push(`/campaign/${campaignDetail.value.campaign_id}`);
  }
};

const goToProposalList = () => {
  router.push('/mypage/influencer');
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