<template>
  <div class="proposal-list-box">
    <h2 class="proposal-list-title">제안서 목록</h2>
    <template v-if="!selectedProposal">
      <div v-if="loading" class="proposal-list-loading">로딩 중...</div>
      <div v-else-if="error" class="proposal-list-error">{{ error }}</div>
      <div v-else-if="proposals.length === 0" class="proposal-list-empty">등록된 제안서가 없습니다.</div>
      <div v-else class="proposal-list-table">
        <div v-for="proposal in proposals" :key="proposal.id" class="proposal-row">
          <div class="proposal-row-left">
            <img :src="proposal.img" class="proposal-profile-img" />
            <span class="proposal-user">{{ proposal.user }}</span>
            <span class="proposal-date">신청일: {{ proposal.created_at }}</span>
          </div>
          <div class="proposal-row-right">
            <span class="status-badge" :class="getStatusClass(proposal.status)">{{ getStatusText(proposal.status) }}</span>
            <button class="proposal-detail-btn" @click="showDetail(proposal)">상세보기</button>
          </div>
        </div>
      </div>
    </template>
    <DetailProposal v-if="selectedProposal" :proposal="selectedProposal" @close="closeDetail" @back="closeDetail" @reject="handleRejectProposal" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import axios from 'axios'
import DetailProposal from './DetailProposalModal.vue'

const props = defineProps({
  campaignId: {
    type: String,
    required: true
  }
})

const proposals = ref([])
const loading = ref(true)
const error = ref(null)
const selectedProposal = ref(null)

const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '검토중',
    'ACCEPTED': '수락됨',
    'REJECTED': '거절됨',
    'pending': '검토중',
    'accepted': '수락됨',
    'rejected': '거절됨'
  }
  return statusMap[status] || '검토중'
}

const getStatusClass = (status) => {
  const statusLower = status?.toLowerCase() || 'pending'
  return statusLower
}

async function fetchProposals() {
  loading.value = true
  error.value = null
  try {
    const res = await axios.get(`/v1/api/advertiser/proposals?campaignId=${props.campaignId}`)
    proposals.value = res.data
  } catch (e) {
    error.value = '제안서 목록을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

function showDetail(proposal) {
  selectedProposal.value = proposal
}

function closeDetail() {
  selectedProposal.value = null
}

function handleRejectProposal(proposalId) {
  // Find and update the proposal status
  const idx = proposals.value.findIndex(p => p.id === proposalId)
  if (idx !== -1) {
    proposals.value[idx].status = 'REJECTED'
  }
  closeDetail()
}

watch(() => props.campaignId, fetchProposals, { immediate: true })
</script>

<style scoped>
.proposal-list-box {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.proposal-list-title {
  margin: 0 0 20px;
  font-size: 1.2em;
  font-weight: bold;
}

.proposal-list-loading,
.proposal-list-error,
.proposal-list-empty {
  text-align: center;
  padding: 20px;
  color: #666;
}

.proposal-list-error {
  color: #dc3545;
}

.proposal-list-table {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.proposal-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
}

.proposal-row-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.proposal-profile-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.proposal-user {
  font-weight: 500;
}

.proposal-date {
  color: #666;
  font-size: 0.9em;
}

.proposal-row-right {
  display: flex;
  align-items: center;
  gap: 15px;
  white-space: nowrap;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.9em;
  font-weight: 500;
  min-width: 65px;
  text-align: center;
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

.proposal-detail-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  background: #7B21E8;
  color: white;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.proposal-detail-btn:hover {
  background: #5B10B8;
}
</style>
