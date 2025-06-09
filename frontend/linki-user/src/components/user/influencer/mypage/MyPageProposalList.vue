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
              :src="proposal.campaign?.productImg" 
              :alt="proposal.campaign?.productName"
              class="thumb"
            >
            <div class="info">
              <div class="name">{{ proposal.campaign?.productName || '캠페인 정보 없음' }}</div>
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
import axios from 'axios';

export default {
  name: 'MyPageProposalList',
  
  setup() {
    const router = useRouter();
    const proposals = ref([]);
    const loading = ref(false);
    const error = ref(null);
    
    const getStatusText = (status) => {
      const statusMap = {
        'PENDING': '검토중',
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
        const proposalRes = await axios.get('http://localhost:3000/proposals');
        const proposalList = proposalRes.data;
        
        const proposalsWithCampaign = await Promise.all(
          proposalList.map(async (proposal) => {
            try {
              const campaignRes = await axios.get(`http://localhost:3000/campaigns?productId=${proposal.product_id}`);
              const campaign = campaignRes.data[0];
              return {
                ...proposal,
                campaign
              };
            } catch (err) {
              console.error(`Failed to fetch campaign for proposal ${proposal.proposal_id}:`, err);
              return {
                ...proposal,
                campaign: null
              };
            }
          })
        );
        
        proposals.value = proposalsWithCampaign;
      } catch (err) {
        error.value = '제안서 목록을 불러오는데 실패했습니다.';
        console.error('Failed to fetch proposals:', err);
      } finally {
        loading.value = false;
      }
    };
    
    const viewDetail = (proposalId) => {
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
@import '@/css/mypage.css';
</style> 