<template>
  <div class="proposal-detail" v-if="isLoading">
    <div class="loading">Loading...</div>
  </div>
  <div class="proposal-detail" v-else-if="proposal">
    <div class="proposal-header">
      <div class="header-content">
        <h2>제안서 상세</h2>
        <div class="status-section">
          <span class="status-badge" :class="statusClass">
            {{ getStatusText(proposal.status) }}
          </span>
          <p class="submission-date">제출일: {{ formatDate(proposal.submitted_at) }}</p>
        </div>
      </div>
    </div>

    <div class="proposal-content">
      <div class="proposal-text" v-if="!isEditing">
        {{ proposal.content }}
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
          <p>{{ formatDate(campaignDetail.productDeadline) }}</p>
        </div>
        <div class="info-item">
          <label>광고 조건</label>
          <p>팔로워 수 2만 명 이상</p>
        </div>
        <div class="info-item">
          <label>카테고리</label>
          <p>{{ campaignDetail.productCategory }}</p>
        </div>
        <div class="info-item">
          <label>브랜드</label>
          <p>{{ campaignDetail.companyName }}</p>
        </div>
      </div>
    </div>

    <div class="action-buttons" v-if="proposal.status === 'PENDING'">
      <button v-if="!isEditing" class="submit-button" @click="startEdit">수정</button>
      <button v-if="!isEditing" class="delete-button" @click="deleteProposal">삭제</button>
      <button v-if="isEditing" class="submit-button" @click="saveProposal">저장</button>
      <button v-if="isEditing" class="cancel-button" @click="cancelEdit">취소</button>
    </div>
  </div>
  <div class="proposal-detail" v-else>
    <div class="error">제안서를 찾을 수 없습니다.</div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'DetailProposal',
  props: {
    proposal: {
      type: Object,
      default: null
    },
    campaignDetail: {
      type: Object,
      default: null
    }
  },
  setup(props) {
    const isLoading = ref(false)
    const isEditing = ref(false)
    const editingContent = ref('')

    const statusClass = computed(() => {
      if (!props.proposal?.status) return ''
      return props.proposal.status.toLowerCase()
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

    const startEdit = () => {
      editingContent.value = props.proposal.content
      isEditing.value = true
    }

    const cancelEdit = () => {
      editingContent.value = ''
      isEditing.value = false
    }

    const saveProposal = async () => {
      try {
        // API 호출 로직 추가 필요
        isEditing.value = false
      } catch (error) {
        console.error('Failed to save proposal:', error)
      }
    }

    const deleteProposal = async () => {
      if (!confirm('정말로 제안서를 삭제하시겠습니까?')) return
      try {
        // API 호출 로직 추가 필요
      } catch (error) {
        console.error('Failed to delete proposal:', error)
      }
    }

    return {
      isLoading,
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

.header-content h3 {
  margin: 0;
  font-size: 1.2em;
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

.proposal-content h3 {
  margin-bottom: 16px;
  font-size: 1.2em;
  color: #333;
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

.loading, .error {
  text-align: center;
  padding: 20px;
  color: #666;
}

.error {
  color: #721c24;
  background-color: #f8d7da;
  border-radius: 4px;
}
</style> 