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
    <DetailProposal v-if="selectedProposal" :proposal="selectedProposal" @close="closeDetail" @back="closeDetail" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import axios from 'axios'
import DetailProposal from '../ProposalDetailComponent.vue'

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

@import '@/assets/css/detail.css';

</style>
