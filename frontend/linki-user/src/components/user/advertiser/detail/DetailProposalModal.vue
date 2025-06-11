<template>
  <div class="proposal-detail-modal">
    <div class="proposal-detail-header">
      <span class="proposal-detail-title">제안서 상세</span>
      <span class="status-badge" :class="statusClass">{{ statusText }}</span>
      <button class="proposal-detail-close" @click="$emit('close')">×</button>
    </div>
    <div class="proposal-detail-body">
      <div class="proposal-detail-profile">
        <img :src="proposal.img" class="proposal-detail-img" />
        <div class="proposal-detail-meta">
          <div>이름 : {{ proposal.user }}</div>
          <div>채널 : {{ proposal.channel }}</div>
        </div>
      </div>
      <hr class="proposal-detail-divider" />
      <div class="proposal-detail-content-block">
        <div class="proposal-detail-content-title">내용</div>
        <div v-if="!isEditMode" class="proposal-detail-content-text">{{ proposal.content }}</div>
        <div v-else>
          <textarea v-model="editContent" rows="6" style="width:100%;margin-bottom:12px;"></textarea>
        </div>
      </div>
      <!-- 버튼 영역 -->
      <div class="proposal-detail-btns">
        <template v-if="!isEditMode">
          <button class="proposal-detail-btn" @click="startEdit">수정</button>
          <button class="proposal-detail-btn accept" @click="handleContract">계약</button>
        </template>
        <template v-else>
          <button class="proposal-detail-btn" @click="cancelEdit">취소</button>
          <button class="proposal-detail-btn accept" @click="saveEdit">저장</button>
        </template>
      </div>
    </div>
    <div v-if="rejectModalOpen" class="modal-backdrop">
      <div class="modal-box">
        <div class="modal-message">제안서를 거절하시겠습니까?</div>
        <div class="modal-actions">
          <button class="modal-cancel" @click="handleRejectCancel">취소</button>
          <button class="modal-confirm" @click="handleRejectConfirm">확인</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { proposalAPI } from '@/api/advertiser/advertiser-proposal'
import { contractApi } from '@/api/advertiser/advertiser-contract'

const props = defineProps({
  proposal: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'reject', 'contract'])

const statusText = computed(() => {
  const statusMap = {
    'PENDING': '검토중',
    'ACCEPTED': '수락됨',
    'REJECTED': '거절됨',
    'pending': '검토중',
    'accepted': '수락됨',
    'rejected': '거절됨'
  }
  return statusMap[props.proposal.status] || '검토중'
})

const statusClass = computed(() => {
  const status = props.proposal.status?.toLowerCase() || 'pending'
  return status
})

const rejectModalOpen = ref(false)
function handleRejectClick() {
  rejectModalOpen.value = true
}
function handleRejectCancel() {
  rejectModalOpen.value = false
}
function handleRejectConfirm() {
  emit('reject', props.proposal.id)
  rejectModalOpen.value = false
}

// 수정 모드 관련
const isEditMode = ref(false)
const editContent = ref('')

function startEdit() {
  isEditMode.value = true
  editContent.value = props.proposal.content
}
function cancelEdit() {
  isEditMode.value = false
}
async function saveEdit() {
  try {
    await proposalAPI.updateProposal(props.proposal.id, { ...props.proposal, content: editContent.value })
    props.proposal.content = editContent.value // 즉시 반영
    isEditMode.value = false
    alert('제안서가 수정되었습니다.')
  } catch (e) {
    alert('제안서 수정에 실패했습니다.')
  }
}
function handleContract() {
  contractApi.startContract(props.proposal.id)
  alert('계약서 작성을 시작합니다')
}
</script>

<style scoped>
.proposal-detail-modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  padding: 24px;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  z-index: 1000;
  box-shadow: 0 0 0 0.5px rgba(123, 33, 232, 0.1), 0 1px 2px rgba(0, 0, 0, 0.08);
}

.proposal-detail-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  position: relative;
}

.proposal-detail-title {
  font-size: 1.2em;
  font-weight: bold;
  margin-right: 8px;
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

.proposal-detail-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #7B21E8;
  padding: 0;
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

.proposal-detail-close:hover {
  color: #5B10B8;
}

.proposal-detail-body {
  padding: 0;
}

.proposal-detail-profile {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
  padding: 0;
  border: none;
  border-radius: 0;
}

.proposal-detail-img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
}

.proposal-detail-meta {
  flex: 1;
}

.proposal-detail-meta > div {
  margin: 8px 0;
}

.proposal-detail-divider {
  border: none;
  border-top: 1px solid #eee;
  margin: 24px 0;
}

.proposal-detail-content-block {
  margin-bottom: 24px;
  padding: 0;
  border: none;
  border-radius: 0;
}

.proposal-detail-content-title {
  font-weight: bold;
  margin-bottom: 16px;
}

.proposal-detail-content-text {
  line-height: 1.6;
  white-space: pre-wrap;
}

.proposal-detail-btns {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.proposal-detail-btn {
  padding: 12px 32px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  background: #f3f4f6;
  color: #7B21E8;
  transition: background 0.2s, color 0.2s;
}

.proposal-detail-btn.accept {
  background: #7B21E8;
  color: #fff;
}

.proposal-detail-btn:not(.accept):hover {
  background: #e5e7eb;
}

.proposal-detail-btn.accept:hover {
  background: #5B10B8;
}

.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1001;
}

.modal-box {
  background-color: white;
  padding: 24px;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 0 0 0.5px rgba(123, 33, 232, 0.1), 0 1px 2px rgba(0, 0, 0, 0.08);
}

.modal-message {
  font-size: 1.2em;
  font-weight: bold;
  margin-bottom: 24px;
}

.modal-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.modal-cancel, .modal-confirm {
  padding: 12px 24px;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.modal-cancel {
  background-color: white;
  border: 1px solid #DC3545;
  color: #DC3545;
}

.modal-cancel:hover {
  background-color: #DC3545;
  color: white;
}

.modal-confirm {
  background-color: #7B21E8;
  color: white;
}

.modal-confirm:hover {
  background-color: #5B10B8;
}
</style> 