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
            <span class="proposal-status">{{ proposal.status }}</span>
            <button class="proposal-detail-btn" @click="showDetail(proposal)">자세히 보기</button>
          </div>
        </div>
      </div>
    </template>
    <ProposalDetailComponent v-if="selectedProposal" :proposal="selectedProposal" @close="closeDetail" @back="closeDetail" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import axios from 'axios'
import ProposalDetailComponent from './ProposalDetailComponent.vue'

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

async function fetchProposals() {
  loading.value = true
  error.value = null
  try {
    const res = await axios.get(`/v1/api/proposals?campaign_id=${props.campaignId}`)
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

watch(() => props.campaignId, fetchProposals, { immediate: true })
</script>

<style scoped>
.proposal-list-box {
  margin: 32px 0 0 0;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(140,48,245,0.06);
  padding: 0 40px 36px 40px;
  max-width: 1100px;
  margin-left: auto;
  margin-right: auto;
}
.proposal-list-title {
  font-size: 1.3rem;
  font-weight: 800;
  color: #8C30F5;
  margin-bottom: 24px;
  padding-top: 32px;
}
.proposal-list-loading,
.proposal-list-error,
.proposal-list-empty {
  text-align: center;
  color: #888;
  font-size: 1.1rem;
  padding: 32px 0;
}
.proposal-list-table {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.proposal-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fafaff;
  border-radius: 12px;
  padding: 18px 32px;
  box-shadow: 0 1px 4px rgba(140,48,245,0.04);
}
.proposal-row-left {
  display: flex;
  align-items: center;
  gap: 24px;
}
.proposal-profile-img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.proposal-user {
  font-size: 1.1rem;
  font-weight: 700;
  color: #23262F;
  margin-right: 12px;
}
.proposal-date {
  font-size: 1rem;
  color: #888;
  margin-right: 12px;
}
.proposal-row-right {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-left: auto;
}
.proposal-status {
  font-size: 1rem;
  color: #8C30F5;
  font-weight: 700;
  margin-right: 0;
}
.proposal-detail-btn {
  background: #8C30F5;
  color: #fff;
  font-weight: 700;
  border: none;
  border-radius: 6px;
  padding: 8px 18px;
  font-size: 15px;
  cursor: pointer;
  transition: background 0.2s;
}
.proposal-detail-btn:hover {
  background: #6B21E8;
}
</style>
