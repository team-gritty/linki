<template>
  <div class="header-container">
    <div class="campaign-summary-box">
      <div class="summary-left">
        <template v-if="!loading && detailData.campaign">
          <img :src="detailData.campaign.campaignImg" :alt="detailData.campaign.campaignName" class="summary-thumb">
          <div class="summary-info">
            <h2 class="summary-title">{{ detailData.campaign.campaignName }}</h2>
            <p class="summary-sub">{{ detailData.campaign.campaignDesc }}</p>
          </div>
        </template>
        <div v-else class="summary-info">
          <h2 class="summary-title">데이터 로딩중...</h2>
        </div>
      </div>
      <div class="header-buttons">
        <template v-if="currentTab === 'contract'">
          <button class="go-list-btn" @click="goToContractList">계약서 목록</button>
        </template>
        <template v-else>
          <button class="go-list-btn" @click="goToProposalList">제안서 목록</button>
        </template>
      </div>
    </div>

    <div class="campaign-tabs">
      <button 
        v-for="tab in tabs" 
        :key="tab.id"
        :class="['tab', { active: currentTab === tab.id }]"
        @click="$emit('update:currentTab', tab.id)"
      >
        {{ tab.name }}
      </button>
      <div class="tab-underline"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import httpClient from '@/utils/httpRequest';
import {chatApi} from "@/api/chat.js";

const props = defineProps({
  currentTab: {
    type: String,
    required: true
  },
  tabs: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update:currentTab', 'update:detailData', 'update:chatRoom', 'go-to-proposal-list', 'go-to-campaign-detail']);

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const detailData = ref({
  proposal: null,
  campaign: null,
  contract: null,
  chat: null
});
const proposalId = 'PROP0000' // 테스트용 하드코딩
const chatRoom = ref(null)

    const fetchDetailData = async () => {
      try {
        loading.value = true;
        const id = route.params.id; // 'p1' 형태의 proposal_id
        console.log('Starting data fetch for ID:', id);

        // 1. 제안서 조회
        const proposalsResponse = await httpClient.get('/v1/api/influencer/proposals');
        const foundProposal = proposalsResponse.data.find(p => p.proposal_id === id);
        if (!foundProposal) {
          throw new Error('제안서 정보를 찾을 수 없습니다.');
        }
        console.log('Found proposal:', foundProposal);
        detailData.value.proposal = foundProposal;

        // 2. 캠페인 조회
        const campaignsResponse = await httpClient.get('/v1/api/influencer/campaigns');
        const foundCampaign = campaignsResponse.data.find(c => c.campaignId === foundProposal.campaign_id);
        if (foundCampaign) {
          console.log('Found campaign:', foundCampaign);
          detailData.value.campaign = foundCampaign;
        }

        // 3. 계약 조회
        const contractsResponse = await httpClient.get('/v1/api/influencer/contracts');
        const foundContract = contractsResponse.data.find(c => c.proposal_id === id);
        if (foundContract) {
          console.log('Found contract:', foundContract);
          detailData.value.contract = foundContract;
        }

        // 4. 채팅 조회
    chatRoom.value = await chatApi.getChatRoom(proposalId)
    emit('update:chatRoom', chatRoom.value)

        // 데이터 로드 후 탭 설정 (route.query.tab이 있는 경우)
        if (route.query.tab && props.tabs.some(tab => tab.id === route.query.tab)) {
          emit('update:currentTab', route.query.tab);
        }

        console.log('Final detail data:', detailData.value);
        // 부모 컴포넌트에 데이터 전달 (깊은 복사)
        emit('update:detailData', JSON.parse(JSON.stringify(detailData.value)));
      } catch (error) {
        console.error('데이터 조회 실패:', error);
      } finally {
        loading.value = false;
      }
    };

    const goToContractList = () => {
      router.push({ name: 'influencer-mypage', query: { currentMenu: 'contract.ongoing' } });
    };

    const goToProposalList = () => {
      router.push({ name: 'influencer-mypage', query: { currentMenu: 'campaign.proposals' } });
    };

    // URL 변경 시 데이터 다시 조회
    watch([() => route.params.id, () => route.query.tab], ([newId, newTab]) => {
      if (newId) {
        fetchDetailData();
      }
    });

    // 컴포넌트 마운트 시 데이터 조회
    onMounted(() => {
      fetchDetailData();
    });
</script> 