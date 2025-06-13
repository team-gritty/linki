<template>
  <div class="list-layout">
    <main class="list-content">
      <h1 class="list-title">내 제안서 목록</h1>
      <div class="list-box">
        <div v-if="loading" class="loading">
          Loading...
        </div>
        <div v-else-if="error" class="error">
          {{ error }}
        </div>
        <div v-else-if="proposals.length === 0" class="empty-msg">
          제출한 제안서가 없습니다.
        </div>
        <div v-else class="proposals-container">
          <div v-for="proposal in proposals" :key="proposal.proposal_id" class="card">
            <img 
              :src="proposal.campaign?.campaignImg" 
              :alt="proposal.campaign?.campaignName"
              class="thumb"
            >
            <div class="info">
              <div class="name">{{ proposal.campaign?.campaignName || '캠페인 정보 없음' }}</div>
              <div class="meta">
                <span>제출일: {{ formatDate(proposal.submitted_at) }}</span>
                <div class="status-container">
                  <span :class="['status', proposal.status.toLowerCase()]">
                    {{ getStatusText(proposal.status) }}
                  </span>
                </div>
              </div>
            </div>
            <button class="detail-btn" @click="viewDetail(proposal.proposal_id)">
              상세 조회
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import httpClient from '../../../../utils/httpRequest';

export default {
  name: 'MyPageProposalList',
  
  setup() {
    const router = useRouter();
    const proposals = ref([]);
    const loading = ref(false);
    const error = ref(null);
    
    const getStatusText = (status) => {
      const statusMap = {
        'PENDING': '대기중',
        'ACCEPTED': '수락됨',
        'REJECTED': '거절됨'
      };
      return statusMap[status] || status;
    };
    
    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    };
    
    const fetchProposals = async () => {
      loading.value = true;
      error.value = null;
      try {
        // 1. 먼저 모든 제안서 목록을 가져옵니다
        const response = await httpClient.get('/v1/api/influencer/proposals', {
          params: {
            _page: 1,
            _limit: 10
          }
        });
        
        const proposalList = response.data;
        console.log('Fetched proposals:', proposalList);

        if (!Array.isArray(proposalList)) {
          throw new Error('제안서 데이터 형식이 올바르지 않습니다.');
        }

        // 2. 캠페인 목록을 한 번에 가져옵니다
        const campaignsResponse = await httpClient.get('/v1/api/influencer/campaigns');
        const campaigns = campaignsResponse.data;
        console.log('Fetched campaigns:', campaigns);

        // 3. 제안서와 캠페인 정보를 매칭합니다
        const proposalsWithCampaign = proposalList.map(proposal => {
          const matchingCampaign = campaigns.find(
            campaign => campaign.campaignId === proposal.campaign_id || campaign.campaignId === proposal.product_id
          );
          
          console.log(`Matching campaign for proposal ${proposal.proposal_id}:`, matchingCampaign);
          
          return {
            ...proposal,
            campaign: matchingCampaign || null
          };
        });

        proposals.value = proposalsWithCampaign;
        console.log('Final proposals with campaigns:', proposals.value);

      } catch (err) {
        error.value = '제안서 목록을 불러오는데 실패했습니다.';
        console.error('Failed to fetch proposals:', err);
      } finally {
        loading.value = false;
      }
    };
    
    const viewDetail = (proposalId) => {
      console.log('Navigating to proposal:', proposalId);
      router.push(`/proposal/${proposalId}`);
    };
    
    onMounted(() => {
      fetchProposals();
    });
    
    return {
      proposals,
      loading,
      error,
      getStatusText,
      formatDate,
      viewDetail
    };
  }
};
</script>
<style>
@import '@/assets/css/mypage.css';
</style> 