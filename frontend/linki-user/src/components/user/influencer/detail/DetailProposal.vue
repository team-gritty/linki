<template>
  <div class="proposal-detail-content">
    <div v-if="loading" class="loading">
      <p>데이터 로딩중...</p>
    </div>
    <div v-else-if="!proposalData" class="no-proposal">
      <p>제안서 정보가 없습니다.</p>
    </div>
    <div v-else>
      <div class="proposal-detail">
        <div class="proposal-header">
          <div class="header-content">
            <h2>제안서 상세</h2>
            <div class="status-section">
              <span class="status-badge" :class="statusClass">
                {{ getStatusText(proposalData.status) }}
              </span>
              <p class="submission-date">제출일: {{ formatDate(proposalData.submitted_at) }}</p>
            </div>
          </div>
        </div>

        <div class="proposal-content">
          <div class="proposal-text" v-if="!isEditing">
            {{ proposalData.content }}
          </div>
          <textarea
            v-else
            v-model="editingContent"
            class="proposal-edit-textarea"
            placeholder="제안서 내용을 입력해주세요"
          ></textarea>
        </div>
        
        <div class="campaign-info" v-if="campaignDetail">
          <h4>캠페인 정보</h4>
          <div class="info-grid">
            <div class="info-item">
              <label>광고 선택 마감일</label>
              <p>{{ formatDate(campaignDetail.campaignDeadline) }}</p>
            </div>
            <div class="info-item">
              <label>광고 조건</label>
              <p>{{ campaignDetail.campaignCondition }}</p>
            </div>
            <div class="info-item">
              <label>카테고리</label>
              <p>{{ campaignDetail.campaignCategory }}</p>
            </div>
            <div class="info-item">
              <label>브랜드</label>
              <p>{{ campaignDetail.companyName }}</p>
            </div>
          </div>
        </div>

        <div class="action-buttons" v-if="proposalData.status === 'PENDING'">
          <button v-if="!isEditing" class="submit-button" @click="startEdit">수정</button>
          <button v-if="!isEditing" class="delete-button" @click="deleteProposal">삭제</button>
          <button v-if="isEditing" class="submit-button" @click="saveProposal">저장</button>
          <button v-if="isEditing" class="cancel-button" @click="cancelEdit">취소</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

export default {
  name: 'DetailProposal',
  props: {
    campaignDetail: {
      type: Object,
      default: null
    }
  },
  setup(props) {
    const route = useRoute()
    const loading = ref(true)
    const isEditing = ref(false)
    const editingContent = ref('')
    const proposalData = ref(null)
    

    const fetchProposal = async () => {
      try {
        loading.value = true;
        const proposalId = route.params.id;
        console.log('Fetching proposal with ID:', proposalId);

        if (!proposalId) {
          throw new Error('제안서 ID가 없습니다.');
        }

        // 1. 제안서 정보를 직접 조회
        const proposalResponse = await axios.get(`/v1/api/influencer/proposals/${proposalId}`);
        console.log('Proposal response:', proposalResponse.data);

        if (!proposalResponse.data || !proposalResponse.data.length) {
          throw new Error('제안서를 찾을 수 없습니다.');
        }

        const proposal = proposalResponse.data[0]; // json-server는 항상 배열을 반환

        // 2. 캠페인 정보 조회
        if (proposal.campaign_id) {
          try {
            const campaignResponse = await axios.get(`/v1/api/influencer/campaigns/${proposal.campaign_id}`);
            console.log('Campaign response:', campaignResponse.data);
            
            if (campaignResponse.data && campaignResponse.data.length > 0) {
              proposal.campaign = campaignResponse.data[0];
            }
          } catch (error) {
            console.error('Failed to fetch campaign:', error);
          }
        }

        proposalData.value = proposal;

        // 3. 계약 정보가 있다면 조회
        if (proposal.contractId) {
          try {
            const contractResponse = await axios.get(`/v1/api/influencer/contracts/${proposal.contractId}`);
            if (contractResponse.data && contractResponse.data.length > 0) {
              proposalData.value.contract = contractResponse.data[0];
            }
          } catch (error) {
            console.error('Failed to fetch contract:', error);
          }
        }

      } catch (error) {
        console.error('Failed to fetch proposal:', error);
        proposalData.value = null;
      } finally {
        loading.value = false;
      }
    }

    const statusClass = computed(() => {
      if (!proposalData.value?.status) return ''
      return proposalData.value.status.toLowerCase()
    })

    const getStatusText = (status) => {
      if (!status) return ''
      const statusMap = {
        'PENDING': '검토중',
        'ACCEPTED': '수락됨',
        'REJECTED': '거절됨',
        'COMPLETED': '완료'
      }
      return statusMap[status] || status
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }

    const startEdit = () => {
      editingContent.value = proposalData.value.content
      isEditing.value = true
    }

    const cancelEdit = () => {
      editingContent.value = ''
      isEditing.value = false
    }

    const saveProposal = async () => {
      try {
        if (!proposalData.value?.id) return
        
        await axios.patch(`/v1/api/influencer/proposals/${proposalData.value.id}`, {
          content: editingContent.value,
          submitted_at: new Date().toISOString()
        })
        
        await fetchProposal()
        isEditing.value = false
      } catch (error) {
        console.error('Failed to save proposal:', error)
        alert('제안서 저장에 실패했습니다.')
      }
    }

    const deleteProposal = async () => {
      if (!confirm('정말로 제안서를 삭제하시겠습니까?')) return
      try {
        if (!proposalData.value?.id) return
        
        await axios.delete(`/v1/api/influencer/proposals/${proposalData.value.id}`)
        proposalData.value = null
      } catch (error) {
        console.error('Failed to delete proposal:', error)
        alert('제안서 삭제에 실패했습니다.')
      }
    }

    onMounted(() => {
      fetchProposal()
    })

    return {
      loading,
      proposalData,
      isEditing,
      editingContent,
      statusClass,
      getStatusText,
      formatDate,
      startEdit,
      cancelEdit,
      saveProposal,
      deleteProposal
    }
  }
}
</script>

<style scoped>
@import '@/assets/css/detail.css';

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  color: #666;
}

.no-proposal {
  text-align: center;
  padding: 48px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.no-proposal p {
  color: #666;
  font-size: 16px;
}

.proposal-detail {
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.proposal-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h2 {
  margin: 0;
  font-size: 1.5em;
  color: #333;
}

.status-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.submission-date {
  margin: 0;
  color: #666;
  font-size: 0.9em;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.9em;
  font-weight: 500;
}

.pending {
  background-color: #fff3cd;
  color: #856404;
}

.accepted {
  background-color: #d4edda;
  color: #155724;
}

.rejected {
  background-color: #f8d7da;
  color: #721c24;
}

.completed {
  background-color: #e2e8f0;
  color: #4a5568;
}

.proposal-content {
  margin: 24px 0;
}

.proposal-text {
  line-height: 1.6;
  color: #333;
  white-space: pre-wrap;
}

.proposal-edit-textarea {
  width: 100%;
  min-height: 200px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  font-family: inherit;
  line-height: 1.6;
}

.campaign-info {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.campaign-info h4 {
  margin: 0 0 15px;
  color: #333;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item label {
  font-size: 0.9em;
  color: #666;
}

.info-item p {
  margin: 0;
  color: #333;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  justify-content: flex-end;
}

.submit-button,
.delete-button,
.cancel-button {
  padding: 8px 24px;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.submit-button {
  background: #7B21E8;
  color: white;
  border: none;
}

.submit-button:hover {
  background: #5B10B8;
}

.delete-button {
  background: white;
  color: #DC3545;
  border: 1px solid #DC3545;
}

.delete-button:hover {
  background: #DC3545;
  color: white;
}

.cancel-button {
  background: white;
  color: #666;
  border: 1px solid #ddd;
}

.cancel-button:hover {
  background: #f5f5f5;
}
</style> 