<template>
  <div class="header-container">
    <div class="campaign-summary-box">
      <div class="summary-left">
        <template v-if="detailData.campaign && detailData.campaign.campaignName">
          <img v-if="detailData.campaign.campaignImg" :src="detailData.campaign.campaignImg" :alt="detailData.campaign.campaignName" class="summary-thumb">
          <div class="summary-info">
            <h2 class="summary-title">{{ detailData.campaign.campaignName }}</h2>
            <p class="summary-sub">{{ detailData.campaign.campaignDesc }}</p>
          </div>
        </template>
        <div v-else class="summary-info">
          <h2 class="summary-title">{{ detailData.proposal?.campaignTitle || detailData.proposal?.campaignName || detailData.campaign?.campaignName || '데이터 로딩중...' }}</h2>
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
import { proposalAPI } from '@/api/proposal';
import { campaignAPI } from '@/api/campaign';
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

const chatRoom = ref(null)

    const fetchDetailData = async () => {
      try {
        loading.value = true;
        const proposalId = route.params.id;
        console.log('Starting data fetch for ID:', proposalId);

        // 제안서 상세 정보 조회
        const response = await proposalAPI.getProposalDetail(proposalId);
        console.log('Fetched proposal detail:', response);
        
        // 제안서 정보 설정
        detailData.value.proposal = response;
        
        // campaignId로 별도 캠페인 상세 정보 조회
        if (response.campaignId) {
          try {
            const campaignDetail = await campaignAPI.getCampaignDetail(response.campaignId);
            console.log('Fetched campaign detail:', campaignDetail);
            
            detailData.value.campaign = {
              campaignName: campaignDetail.campaignName || '캠페인 제목 없음',
              campaignDesc: campaignDetail.campaignDesc || '캠페인 설명 없음',
              campaignImg: campaignDetail.campaignImg || ''
            };
            
            console.log('Campaign info mapped from separate API:', {
              campaignId: response.campaignId,
              campaignName: campaignDetail.campaignName,
              finalCampaignName: detailData.value.campaign.campaignName
            });
          } catch (campaignError) {
            console.error('Failed to fetch campaign detail:', campaignError);
            // 캠페인 정보 조회 실패 시 기본값 설정
            detailData.value.campaign = {
              campaignName: response.campaignTitle || '캠페인 정보 로딩 실패',
              campaignDesc: response.campaignCondition || '',
              campaignImg: ''
            };
          }
        } else {
          // campaignId가 없는 경우 기본값 설정
          detailData.value.campaign = {
            campaignName: response.campaignTitle || '캠페인 정보 없음',
            campaignDesc: response.campaignCondition || '',
            campaignImg: ''
          };
        }

        // 채팅 조회
        chatRoom.value = await chatApi.getChatRoom(proposalId);
        emit('update:chatRoom', chatRoom.value);

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