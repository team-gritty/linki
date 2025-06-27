<template>
  <div class="proposal-list-content">
    <h1 class="content-title">내 제안서 목록</h1>
    <div class="content-box">
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
          <div v-for="proposal in proposals" :key="proposal.proposalId" class="card">
            <img 
              :src="proposal.campaignImg" 
              :alt="proposal.campaignTitle"
              class="thumb"
            >
            <div class="info">
              <div class="name">{{ proposal.campaignName || '캠페인 정보 없음' }}</div>
              <div class="meta">
                <span>제출일: {{ formatDate(proposal.submittedAt) }}</span>
                <div class="status-container">
                  <span :class="['status', proposal.status.toLowerCase()]">
                    {{ getStatusText(proposal.status) }}
                  </span>
                </div>
              </div>
            </div>
            <button class="detail-btn" @click="viewDetail(proposal.proposalId)">
              상세 조회
            </button>
          </div>
        </div>
      </div>
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
        // 제안서 목록을 가져옵니다 (캠페인 정보 포함)
        const response = await httpClient.get('/v1/api/influencer/mypage/proposals');

        const proposalList = response.data;
        console.log('Fetched proposals:', proposalList);

        if (!Array.isArray(proposalList)) {
          throw new Error('제안서 데이터 형식이 올바르지 않습니다.');
        }

        proposals.value = proposalList;
        console.log('Final proposals:', proposals.value);

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
<style scoped>
.proposal-list-content {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.content-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #1a1a1a;
}

.content-box {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.list-layout {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.detail-btn {
  padding: 8px 24px;
  background-color: #8B5CF6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.detail-btn:hover {
  background-color: #7C3AED;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.2);
}
</style> 