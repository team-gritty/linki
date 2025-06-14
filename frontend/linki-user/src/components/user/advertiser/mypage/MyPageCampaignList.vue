<template>
  <div class="campaign-list-content">
    <h1 class="content-title">내 캠페인 목록</h1>
    <div class="content-box">
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
                :class="['status-btn', item.status === 'ACTIVE' ? 'active' : '']"
                @click="item.status !== 'ACTIVE' && openStatusModal(idx, 'ACTIVE')"
                :disabled="item.status === 'ACTIVE'"
                type="button"
              >모집중</button>
              <button
                :class="['status-btn', item.status === 'HIDDEN' ? 'inactive private-active' : '']"
                @click="openStatusModal(idx, 'HIDDEN')"
                :disabled="item.status === 'HIDDEN'"
                type="button"
              >비공개</button>
            </div>
          </div>
        </div>
        <div class="card-actions">
          <button class="detail-btn" @click="goToDetail(item.id)">상세</button>
          <button 
            class="delete-btn"
            @click="openDeleteModal(idx)"
            @mouseenter="handleDeleteBtnMouseEnter(idx)"
            @mouseleave="handleDeleteBtnMouseLeave"
          >
            <svg class="trash-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path :fill="deleteBtnHoverIdx === idx ? '#fff' : '#888'" d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
            </svg>
          </button>
        </div>
      </div>
      <div v-if="campaigns.length === 0" class="empty-msg">
        등록된 캠페인이 없습니다.
      </div>
    </div>
  </div>
  
    <!-- 상태 변경 모달 -->
    <div v-if="modalOpen" class="modal-overlay" @click="modalOpen = false">
      <div class="modal-content" @click.stop>
        <h3>캠페인 상태 변경</h3>
        <p>캠페인 상태를 <strong>{{ nextStatus === 'ACTIVE' ? '모집중' : '비공개' }}</strong>로 변경하시겠습니까?</p>
        <div class="modal-actions">
          <button @click="modalOpen = false" class="cancel-btn">취소</button>
          <button @click="confirmStatusChange" class="confirm-btn">확인</button>
        </div>
      </div>
    </div>
  
    <!-- 삭제 확인 모달 -->
    <div v-if="deleteModalOpen" class="modal-overlay" @click="deleteModalOpen = false">
      <div class="modal-content" @click.stop>
        <h3>캠페인 삭제</h3>
        <p>정말로 이 캠페인을 삭제하시겠습니까?</p>
        <div class="modal-actions">
          <button @click="deleteModalOpen = false" class="cancel-btn">취소</button>
          <button @click="confirmDelete" class="confirm-btn delete">삭제</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import campaignApi from '@/api/advertiser/advertiser-campaign'
  
  const router = useRouter()
  const campaigns = ref([])
  
  /**
   * 광고주 - 마이페이지 - 캠페인 목록 조회
   */
  const fetchCampaigns = async () => {
    try {
      const data = await campaignApi.getMyPageCampaigns()
      campaigns.value = data.map(item => ({
        id: item.campaignId,
        name: item.campaignName,
        image: item.campaignImg,
        due: item.campaignDeadline ? new Date(item.campaignDeadline).toLocaleDateString('ko-KR') : '',
        status: item.campaignStatus
      }))
    } catch (e) {
      console.error('Failed to fetch campaigns:', e)
      campaigns.value = []
    }
  }
  
  /**
   * 마이페이지 - 캠페인 목록 들어왔을때 fetchCampaigns
   */
  onMounted(() => {
    fetchCampaigns()
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
  
  async function confirmStatusChange() {
    if (targetIdx.value !== null) {
      const campaign = campaigns.value[targetIdx.value]
      try {
        // API를 통해 상태 업데이트
        await campaignApi.updateCampaignStatus(campaign.id, { campaignStatus: nextStatus.value })
        
        // 로컬 상태 업데이트
        campaigns.value[targetIdx.value].status = nextStatus.value
        
        // 상태 변경 후 캠페인 목록 새로고침
        await fetchCampaigns()
      } catch (error) {
        console.error('Error updating campaign status:', error)
        alert('상태 변경에 실패했습니다.')
      }
    }
    modalOpen.value = false
    targetIdx.value = null
  }
  
  function goToDetail(id) {
    router.push(`/mypage/campaign-detail/${id}`)
  }
  </script>
  
  <style scoped>
  @import '@/assets/css/mypage.css';

.campaign-list-content {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.content-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #1a1a1a;
}

.content-box {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  max-width: 400px;
  width: 90%;
}

.modal-content h3 {
  margin: 0 0 1rem 0;
  color: #333;
}

.modal-content p {
  margin: 0 0 1.5rem 0;
  color: #666;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}

.cancel-btn, .confirm-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn {
  background: #f3f4f6;
  color: #374151;
}

.confirm-btn {
  background: #7c3aed;
  color: white;
}

.confirm-btn.delete {
  background: #dc2626;
}

.cancel-btn:hover {
  background: #e5e7eb;
}

.confirm-btn:hover {
  background: #6d28d9;
}

.confirm-btn.delete:hover {
  background: #b91c1c;
}

.detail-btn {
  padding: 8px 24px;
  background-color: #8B5CF6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.detail-btn:hover {
  background-color: #7C3AED;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.2);
}
</style> 