<template>
    <div class="list-box">
      <div
        v-for="(item, idx) in campaigns"
        :key="item.id"
        class="card"
      >
        <img :src="item.image" class="thumb" alt="썸네일" />
        <div class="info">
          <div class="name">{{ item.name }}</div>
          <div class="meta">
            <span>마감일: {{ item.due }}</span>
            <div class="campaign-status-toggle">
              <button
                :class="['status-btn', item.status === '모집중' ? 'active' : '']"
                @click="openStatusModal(idx, '모집중')"
                type="button"
              >모집중</button>
              <button
                :class="['status-btn', item.status === '비공개' ? 'inactive' : '']"
                @click="openStatusModal(idx, '비공개')"
                type="button"
              >비공개</button>
            </div>
          </div>
        </div>
        <div class="card-actions">
          <button class="detail-btn" @click="goToDetail(item.id)">상세보기</button>
          <button 
            class="delete-btn"
            @click="openDeleteModal(idx)"
            @mouseenter="handleDeleteBtnMouseEnter(idx)"
            @mouseleave="handleDeleteBtnMouseLeave"
          >
            <span class="trash-icon">×</span>
          </button>
        </div>
      </div>
      <div v-if="campaigns.length === 0" class="empty-msg">
        등록된 캠페인이 없습니다.
      </div>
  
      <!-- 상태 변경 모달 -->
      <div v-if="modalOpen" class="modal-backdrop">
        <div class="modal-box">
          <div class="modal-message">
            캠페인 상태를 {{ nextStatus }}(으)로 변경하시겠습니까?
          </div>
          <div class="modal-actions">
            <button class="modal-cancel" @click="modalOpen = false">취소</button>
            <button class="modal-confirm" @click="confirmStatusChange">확인</button>
          </div>
        </div>
      </div>
  
      <!-- 삭제 모달 -->
      <div v-if="deleteModalOpen" class="modal-backdrop">
        <div class="modal-box">
          <div class="modal-message">
            캠페인을 삭제하시겠습니까?
          </div>
          <div class="modal-actions">
            <button class="modal-cancel" @click="deleteModalOpen = false">취소</button>
            <button class="modal-delete" @click="confirmDelete">삭제</button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import httpClient from '@/utils/httpRequest'
  
  const router = useRouter()
  const campaigns = ref([])
  
  onMounted(async () => {
    try {
      const response = await httpClient.get('/campaign-list')
      campaigns.value = response.data.map(item => ({
        id: item.campaign_id,
        name: item.campaign_name,
        image: item.campaign_img,
        due: item.campaign_deadline ? item.campaign_deadline.split('T')[0] : '',
        status: item.campaign_publish_status
      }))
    } catch (e) {
      campaigns.value = []
    }
  })
  
  const modalOpen = ref(false)
  const nextStatus = ref('')
  const targetIdx = ref(null)
  
  const deleteModalOpen = ref(false)
  const deleteIdx = ref(null)
  const deleteBtnHoverIdx = ref(null)
  
  function openDeleteModal(idx) {
    deleteIdx.value = idx
    deleteModalOpen.value = true
  }
  
  function confirmDelete() {
    if (deleteIdx.value !== null) {
      campaigns.value.splice(deleteIdx.value, 1)
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
      campaigns.value[targetIdx.value].status = nextStatus.value
    }
    modalOpen.value = false
    targetIdx.value = null
  }
  
  function goToDetail(id) {
    router.push(`/mypage/campaign-detail/${id}`)
  }
  </script>
  
  <style>
  @import '@/assets/css/mypage.css';
  </style> 