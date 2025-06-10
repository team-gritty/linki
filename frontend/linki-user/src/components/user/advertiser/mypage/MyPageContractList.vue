<template>
  <main class="contract-list-content">
    <h1 class="contract-list-title">나의 계약서</h1>
    <div class="contract-list-header">
      <!-- 상태 토글은 카드별로 구현 -->
    </div>
    <div class="contract-list-box">
      <div
        v-for="(item, idx) in contracts"
        :key="item.id"
        class="contract-card"
      >
        <img :src="item.image" class="contract-thumb" alt="썸네일" />
        <div class="contract-info">
          <div class="contract-name">계약명: {{ item.contractTitle }}</div>
        </div>
        <div class="contract-meta">
          <div class="contract-status-toggle">
            <button
              :class="['status-btn', item.status === '서명 대기중' ? 'active' : '']"
              @click="openStatusModal(idx, '서명 대기중')"
              type="button"
            >서명 대기중</button>
            <button
              :class="['status-btn', item.status === '계약완료' ? 'inactive' : '']"
              @click="openStatusModal(idx, '계약완료')"
              type="button"
            >계약완료</button>
          </div>
          <button class="detail-btn" @click="showDetail(item)">상세</button>
          <button class="delete-btn" @click="openDeleteModal(idx)" title="삭제"
            @mouseenter="handleDeleteBtnMouseEnter(idx)" @mouseleave="handleDeleteBtnMouseLeave">
            <svg class="trash-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path :fill="deleteBtnHoverIdx === idx ? '#fff' : '#888'" d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
            </svg>
          </button>
        </div>
      </div>
      <div v-if="contracts.length === 0" class="empty-msg">
        등록된 계약서가 없습니다.
      </div>
    </div>
    <div v-if="deleteModalOpen" class="modal-backdrop">
      <div class="modal-box">
        <div class="modal-message">
          정말 이 계약서를 삭제하시겠습니까?
        </div>
        <div class="modal-actions">
          <button class="modal-cancel" @click="deleteModalOpen = false">취소</button>
          <button class="modal-delete" @click="confirmDelete">삭제</button>
        </div>
      </div>
    </div>
    <div v-if="modalOpen" class="modal-backdrop">
      <div class="modal-box">
        <div class="modal-message">
          상태를 '{{ nextStatus }}'로 변경하시겠습니까?
        </div>
        <div class="modal-actions">
          <button class="modal-cancel" @click="modalOpen = false">취소</button>
          <button class="modal-confirm" @click="confirmStatusChange">확인</button>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import httpClient from '@/utils/httpRequest'

const emit = defineEmits(['show-detail'])

const route = useRoute()
const contracts = ref([])

// 계약서 목록 조회
onMounted(async () => {
  try {
    // 캠페인 ID 조회
    const campaignId = route.query.campaignId || route.params.id
    console.log('디버깅: campaignId', campaignId)
    const response = await httpClient.get('/contracts')
    console.log('디버깅: contracts API 응답', response.data)
    contracts.value = response.data.filter(item => item.campaignId === campaignId)
    console.log('디버깅: 필터링된 contracts', contracts.value)
  } catch (e) {
    contracts.value = []
    console.error('디버깅: contracts 불러오기 실패', e)
  }
})

const deleteModalOpen = ref(false)
const deleteIdx = ref(null)
const deleteBtnHoverIdx = ref(null)
const modalOpen = ref(false)
const nextStatus = ref('')
const targetIdx = ref(null)

function openDeleteModal(idx) {
  deleteIdx.value = idx
  deleteModalOpen.value = true
}
function confirmDelete() {
  if (deleteIdx.value !== null) {
    contracts.value.splice(deleteIdx.value, 1)
  }
  deleteModalOpen.value = false
  deleteIdx.value = null
}
function handleDeleteBtnMouseEnter(idx) {
  deleteBtnHoverIdx.value = idx
}
function handleDeleteBtnMouseLeave() {
  deleteBtnHoverIdx.value = null
}
function openStatusModal(idx, status) {
  nextStatus.value = status
  targetIdx.value = idx
  modalOpen.value = true
}
function confirmStatusChange() {
  if (targetIdx.value !== null) {
    contracts.value[targetIdx.value].status = nextStatus.value
  }
  modalOpen.value = false
  targetIdx.value = null
}
function showDetail(item) {
  emit('show-detail', item)
}
</script>

<style>
@import '@/assets/css/mypage.css';
</style> 