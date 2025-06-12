<template>
  <div class="proposal-detail">
    <!-- 상단 헤더 -->
    <div class="header">
      <div class="product-info" v-if="campaignDetail">
        <img :src="campaignDetail.campaignImg" :alt="campaignDetail.campaignName" class="product-img">
        <div class="product-text">
          <h2>{{ campaignDetail.campaignName }}</h2>
          <p class="product-desc">{{ campaignDetail.campaignDesc }}</p>
          <h2>{{ campaignDetail.productName }}</h2>
          <p class="product-desc">{{ campaignDetail.productDesc }}</p>
        </div>
      </div>
      <div class="header-buttons">
        <button class="list-button" @click="goToProposalList">
          제안서 목록
        </button>
        <button class="detail-button" @click="goToCampaignDetail">
          캠페인 확인 →
        </button>
      </div>
    </div>

    <!-- 탭 메뉴 -->
    <div class="tab-container">
      <div class="tab-menu">
        <button 
          v-for="tab in tabs" 
          :key="tab.id"
          :class="['tab-button', { active: currentTab === tab.id }]"
          @click="currentTab = tab.id"
        >
          {{ tab.name }}
        </button>
      </div>
    </div>

    <!-- 탭 컨텐츠 -->
    <div class="content-wrapper">
      <!-- 제안서 상세 -->
      <div v-if="currentTab === 'proposal'" class="proposal-content">
        <div class="proposal-section">
          <h3>제안서 상세</h3>
          <div class="proposal-text" v-if="!isEditing">
            {{ proposal?.contents }}
          </div>
          <textarea
            v-else
            v-model="editingContent"
            class="proposal-edit-textarea"
            placeholder="제안서 내용을 입력해주세요"
          ></textarea>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <label>광고 선택 마감일</label>
            <p>{{ formatDate(campaignDetail?.campaignDeadline) }}</p>
          </div>
          <div class="info-item">
            <label>광고 조건</label>
            <p>{{ campaignDetail?.campaignCondition }}</p>
          </div>
          <div class="info-item">
            <label>카테고리</label>
            <p>{{ campaignDetail?.campaignCategory }}</p>
          </div>
          <div class="info-item">
            <label>브랜드</label>
            <p>{{ campaignDetail?.companyName }}</p>
          </div>
        </div>

        <div class="action-buttons" v-if="proposal?.status === 'PENDING'">
          <button v-if="!isEditing" class="submit-button" @click="startEdit">수정</button>
          <button v-if="!isEditing" class="delete-button" @click="deleteProposal">삭제</button>
          <button v-if="isEditing" class="submit-button" @click="saveProposal">저장</button>
          <button v-if="isEditing" class="cancel-button" @click="cancelEdit">취소</button>
        </div>
      </div>

      <!-- 캠페인내용 탭 -->
      <div v-if="currentTab === 'campaign'" class="campaign-content">
        <p class="coming-soon">캠페인 내용 기능 준비중입니다.</p>
      </div>

      <!-- 채팅 탭 -->
      <div v-if="currentTab === 'chat'" class="chat-content">
        <p class="coming-soon">채팅 기능 준비중입니다.</p>
      </div>

      <!-- 계약서 탭 -->
      <div v-if="currentTab === 'contract'" class="contract-content">
        <p class="coming-soon">계약서 기능 준비중입니다.</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { proposalAPI } from '@/api/proposal';

export default {
  name: 'ProposalDetailView',

  setup() {
    const route = useRoute();
    const router = useRouter();
    const currentTab = ref('proposal');
    const proposal = ref(null);
    const campaignDetail = ref(null);
    const isEditing = ref(false);
    const editingContent = ref('');

    const tabs = [
      { id: 'campaign', name: '캠페인내용' },
      { id: 'proposal', name: '제안서' },
      { id: 'chat', name: '채팅' },
      { id: 'contract', name: '계약서' }
    ];

    const fetchData = async () => {
      try {
        const proposalId = route.params.id;
        // 제안서 정보 가져오기
        const proposalRes = await proposalAPI.getProposalDetail(proposalId);
        if (proposalRes) {
          proposal.value = proposalRes;
          
          // campaigns에서 캠페인 정보 가져오기
          const campaignId = proposal.value.product_id || proposal.value.campaign_id;
          if (!campaignId) {
            throw new Error('캠페인 ID를 찾을 수 없습니다.');
          }
          
          const campaignRes = await axios.get(`/v1/api/influencer/campaigns/${campaignId}`);
          if (campaignRes.data && campaignRes.data.length > 0) {
            campaignDetail.value = campaignRes.data[0];
          } else {
            console.warn('캠페인 정보를 찾을 수 없습니다.');
            throw new Error('캠페인 정보를 찾을 수 없습니다.');
          }
        } else {
          throw new Error('제안서를 찾을 수 없습니다.');
        }
      } catch (error) {
        console.error('Failed to fetch data:', error);
        alert(error.message || '데이터를 불러오는데 실패했습니다.');
        router.push('/mypage');
      }
    };

    const startEdit = () => {
      isEditing.value = true;
      editingContent.value = proposal.value?.contents || '';
    };

    const cancelEdit = () => {
      isEditing.value = false;
      editingContent.value = '';
    };

    const saveProposal = async () => {
      try {
        const proposalId = route.params.id;
        if (!proposalId) {
          throw new Error('제안서 ID를 찾을 수 없습니다.');
        }

        const updatedData = {
          ...proposal.value,  // 기존 데이터 유지
          contents: editingContent.value,
          submitted_at: new Date().toISOString()
        };

        await proposalAPI.updateProposal(proposalId, updatedData);
        
        // 수정 성공 후 데이터 다시 불러오기
        await fetchData();
        isEditing.value = false;
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
        if (!proposalId) {
          throw new Error('제안서 ID를 찾을 수 없습니다.');
        }

        await proposalAPI.deleteProposal(proposalId);
        router.push('/mypage');
      } catch (error) {
        console.error('Failed to delete proposal:', error);
        alert(error.message || '제안서 삭제에 실패했습니다.');
      }
    };

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

    const goToCampaignDetail = () => {
      router.push(`/campaign/${campaignDetail.value?.productId}`);
    };

    const goToProposalList = () => {
      router.push('/mypage');
    };

    onMounted(() => {
      fetchData();
    });

    return {
      currentTab,
      tabs,
      proposal,
      campaignDetail,
      isEditing,
      editingContent,
      getStatusText,
      formatDate,
      goToCampaignDetail,
      goToProposalList,
      startEdit,
      cancelEdit,
      saveProposal,
      deleteProposal
    };
  }
};
</script>

<style scoped>
.proposal-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.product-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.product-img {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
}

.product-text h2 {
  font-size: 1.5rem;
  margin: 0 0 8px 0;
}

.product-desc {
  color: #666;
  margin: 0;
}

.header-buttons {
  display: flex;
  gap: 12px;
}

.detail-button {
  background: #7B21E8;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.list-button {
  background: white;
  color: #7B21E8;
  border: 1px solid #7B21E8;
  padding: 12px 24px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.detail-button:hover {
  background: #7B21E8;
}

.list-button:hover {
  background: #f8f5ff;
}

.tab-container {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
  border-bottom: 1px solid #eee;
  position: relative;
  background: white;
}

.tab-menu {
  display: flex;
  gap: 32px;
  position: relative;
  max-width: 600px;
  margin: 0 auto;
}

.tab-button {
  padding: 12px 8px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.95rem;
  color: #7B21E8;
  transition: all 0.2s;
  position: relative;
}

.tab-button::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 1px;
  background-color: transparent;
  transition: background-color 0.2s;
}

.tab-button.active {
  color: #7B21E8;
  font-weight: 400;
}

.tab-button.active::after {
  background-color: #7B21E8;
}

.tab-button:hover {
  color: #7B21E8;
}

.content-wrapper {
  background: white;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.proposal-section {
  margin-bottom: 40px;
}

.proposal-section h3 {
  font-size: 1.2rem;
  margin: 0 0 20px 0;
  color: #333;
}

.proposal-text {
  line-height: 1.6;
  color: #444;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 40px;
}

.info-item {
  background: #f8f8f8;
  padding: 20px;
  border-radius: 8px;
}

.info-item label {
  display: block;
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 8px;
}

.info-item p {
  margin: 0;
  color: #333;
  font-weight: 500;
}

.proposal-edit-textarea {
  width: 100%;
  min-height: 200px;
  padding: 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  resize: vertical;
  font-family: inherit;
  font-size: 1rem;
  line-height: 1.6;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.submit-button, .delete-button, .cancel-button {
  padding: 12px 24px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.submit-button {
  background: #7B21E8;
  color: white;
  border: none;
}

.submit-button:hover {
  background: #7B21E8;
}

.delete-button {
  background: white;
  color: #ff4444;
  border: 1px solid #ff4444;
}

.delete-button:hover {
  background: #fff0f0;
}

.cancel-button {
  color: #7B21E8;
  border: 1px solid #7B21E8;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.95rem;
  background: white;
}

.cancel-button:hover {
  background: #7B21E8;
  color: white;
}

.coming-soon {
  text-align: center;
  color: #666;
  padding: 40px;
}

@media (max-width: 768px) {
  .proposal-detail {
    padding: 20px;
  }

  .header {
    flex-direction: column;
    gap: 20px;
    align-items: stretch;
  }

  .header-buttons {
    flex-direction: column;
    gap: 8px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .tab-menu {
    gap: 20px;
  }

  .tab-button {
    padding: 10px 4px;
    font-size: 0.9rem;
  }

  .content-wrapper {
    padding: 20px;
  }
}
</style> 