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
              <p class="submission-date">제출일: {{ formatDate(proposalData.submittedAt) }}</p>
            </div>
          </div>
        </div>

        <div class="proposal-content">
          <div class="proposal-text" v-if="!isEditing">
            {{ proposalData.contents }}
          </div>
          <textarea
            v-else
            v-model="editingContent"
            class="proposal-edit-textarea"
            placeholder="제안서 내용을 입력해주세요"
          ></textarea>
        </div>
        
        <div class="campaign-info" v-if="proposalData?.campaignTitle">
          <h4>캠페인 정보</h4>
          <div class="info-grid">
            <div class="info-item">
              <label>캠페인 제목</label>
              <p>{{ proposalData.campaignTitle }}</p>
            </div>
            <div class="info-item">
              <label>광고 선택 마감일</label>
              <p>{{ formatDate(proposalData.campaignDeadline) }}</p>
            </div>
            <div class="info-item">
              <label>광고 조건</label>
              <p>{{ proposalData.campaignCondition }}</p>
            </div>
            <div class="info-item">
              <label>카테고리</label>
              <p>{{ proposalData.campaignCategory }}</p>
            </div>
            <div class="info-item">
              <label>브랜드</label>
              <p>{{ proposalData.companyName }}</p>
            </div>
          </div>
        </div>

        <div class="action-buttons" v-if="showActionButtons">
          <button v-if="!isEditing && canEditProposal" class="submit-button" @click="startEdit">수정</button>
          <button v-if="!isEditing && canDeleteProposal" class="delete-button" @click="deleteProposal">삭제</button>
          <button v-if="isEditing" class="submit-button" @click="saveProposal">저장</button>
          <button v-if="isEditing" class="cancel-button" @click="cancelEdit">취소</button>
        </div>
        
        <!-- 상태별 안내 메시지 -->
        <div v-if="proposalData && proposalData.contractStatus === 'ONGOING'" class="status-message">
          <div class="status-info ongoing">
            <i class="icon-info"></i>
            <span>계약이 진행 중인 제안서는 수정할 수 없습니다.</span>
          </div>
        </div>
        <div v-if="proposalData && proposalData.contractStatus === 'COMPLETED'" class="status-message">
          <div class="status-info completed">
            <i class="icon-check"></i>
            <span>계약이 완료된 제안서는 수정할 수 없습니다.</span>
          </div>
        </div>
        <div v-if="proposalData && proposalData.status === 'ACCEPTED' && !canDeleteProposal && proposalData.contractStatus !== 'ONGOING' && proposalData.contractStatus !== 'COMPLETED'" class="status-message">
          <div class="status-info accepted">
            <i class="icon-check"></i>
            <span>제안서가 승인되었습니다. 삭제할 수 없지만 수정은 가능합니다.</span>
          </div>
        </div>
        <div v-if="proposalData && proposalData.status === 'REJECTED'" class="status-message">
          <div class="status-info rejected">
            <i class="icon-close"></i>
            <span>제안서가 거절되었습니다.</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { proposalAPI } from '@/api/proposal'

export default {
  name: 'DetailProposal',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const loading = ref(false)
    const isEditing = ref(false)
    const editingContent = ref('')
    const proposalData = ref(null)

    // 제안서 상세 정보 가져오기
    const fetchProposalDetail = async () => {
      try {
        loading.value = true
        const proposalId = route.params.id
        console.log('Route params:', route.params)
        console.log('Proposal ID:', proposalId)
        
        if (!proposalId) {
          throw new Error('제안서 ID가 없습니다.')
        }
        
        console.log('Calling API with proposalId:', proposalId)
        const response = await proposalAPI.getProposalDetail(proposalId)
        proposalData.value = response
        console.log('Fetched proposal detail:', response)
      } catch (error) {
        console.error('Failed to fetch proposal detail:', error)
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      fetchProposalDetail()
    })

    const statusClass = computed(() => {
      if (!proposalData.value?.status) return ''
      return proposalData.value.status.toLowerCase()
    })

    const getStatusText = (status) => {
      if (!status) return ''
      const statusMap = {
        'PENDING': '검토중',
        'ACCEPTED': '수락됨',
        'REJECTED': '거절됨'
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

    // 제안서 상태에 따른 권한 제어
    const canEditProposal = computed(() => {
      if (!proposalData.value?.status) return false
      
      // 계약이 진행 중(ONGOING)인 경우 수정 불가
      if (proposalData.value.contractStatus === 'ONGOING') return false

          // 계약이 완료인 경우 수정 불가
          if (proposalData.value.contractStatus === 'COMPLETED') return false
      
      // 검토중(PENDING)과 수락됨(ACCEPTED) 상태일 때 수정 가능
      return proposalData.value.status === 'PENDING' || proposalData.value.status === 'ACCEPTED'
    })

    const canDeleteProposal = computed(() => {
      if (!proposalData.value?.status) return false
      // 검토중(PENDING) 상태일 때만 삭제 가능
      return proposalData.value.status === 'PENDING'
    })

    const showActionButtons = computed(() => {
      if (!proposalData.value?.status) return false
      // PENDING, ACCEPTED 상태이거나 편집 중일 때 액션 버튼 표시
      return proposalData.value.status === 'PENDING' || proposalData.value.status === 'ACCEPTED' || isEditing.value
    })

    const startEdit = () => {
      // 수정 권한 검증
      if (!canEditProposal.value) {
        if (proposalData.value?.contractStatus === 'ONGOING') {
          alert('계약이 진행 중인 제안서는 수정할 수 없습니다.')
        } else if (proposalData.value?.contractStatus === 'COMPLETED') {
          alert('계약이 완료된 제안서는 수정할 수 없습니다.')
        } else {
          alert('검토중이거나 승인된 제안서만 수정할 수 있습니다.')
        }
        return
      }
      
      editingContent.value = proposalData.value.contents
      isEditing.value = true
    }

    const cancelEdit = () => {
      editingContent.value = ''
      isEditing.value = false
    }

    const saveProposal = async () => {
      try {
        if (!proposalData.value?.proposalId) return
        
        await proposalAPI.updateProposal(proposalData.value.proposalId, {
          contents: editingContent.value,
          
        })
        
        proposalData.value.contents = editingContent.value
        isEditing.value = false
        alert('제안서가 성공적으로 수정되었습니다.')
      } catch (error) {
        console.error('Failed to save proposal:', error)
        alert('제안서 저장에 실패했습니다.')
      }
    }

    const deleteProposal = async () => {
      // 삭제 권한 검증
      if (!canDeleteProposal.value) {
        alert('검토중인 제안서만 삭제할 수 있습니다.')
        return
      }
      
      if (!confirm('정말로 제안서를 삭제하시겠습니까?')) return
      
      try {
        if (!proposalData.value?.proposalId) return
        
        await proposalAPI.deleteProposal(proposalData.value.proposalId)
        alert('제안서가 성공적으로 삭제되었습니다.')
        
        // 삭제 후 인플루언서 마이페이지의 제안서 목록으로 이동
        router.push({ name: 'influencer-mypage', query: { currentMenu: 'campaign.proposals' } })
        
      } catch (error) {
        console.error('Failed to delete proposal:', error)
        alert('제안서 삭제에 실패했습니다.')
      }
    }

    return {
      loading,
      proposalData,
      isEditing,
      editingContent,
      statusClass,
      getStatusText,
      formatDate,
      canEditProposal,
      canDeleteProposal,
      showActionButtons,
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

/* 상태 메시지 스타일 */
.status-message {
  margin-top: 20px;
  padding: 16px;
  border-radius: 8px;
  background-color: #f8f9fa;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
}

.status-info.accepted {
  color: #28a745;
}

.status-info.rejected {
  color: #dc3545;
}

.status-info.ongoing {
  color: #ffc107;
}

.status-info.completed {
  color: #28a745;
}



.status-info i {
  font-size: 16px;
}

.icon-check::before {
  content: "✓";
}

.icon-close::before {
  content: "✗";
}

.icon-info::before {
  content: "ℹ";
}


</style> 