<template>
  <div class="campaign-list-layout">
    <MyPageSideBar />
    <main class="campaign-list-content">
      <h1 class="campaign-list-title">캠페인 목록</h1>
      <div class="campaign-list-box">
        <div
          v-for="(item, idx) in campaigns"
          :key="item.id"
          class="campaign-card"
        >
          <img :src="item.image" class="campaign-thumb" alt="썸네일" />
          <div class="campaign-info">
            <div class="campaign-name">{{ item.name }}</div>
            <div class="campaign-meta">
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
            <button
              class="detail-btn"
              :class="{ active: item.status === '모집중' }"
              :disabled="item.status !== '모집중'"
              @click="goToDetail(item.id)"
            >
              상세
            </button>
            <button class="delete-btn" @click="openDeleteModal(idx)" title="삭제"
              @mouseenter="handleDeleteBtnMouseEnter(idx)" @mouseleave="handleDeleteBtnMouseLeave">
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
      <div v-if="deleteModalOpen" class="modal-backdrop">
        <div class="modal-box">
          <div class="modal-message">
            정말 이 캠페인을 삭제하시겠습니까?
          </div>
          <div class="modal-actions">
            <button class="modal-cancel" @click="deleteModalOpen = false">취소</button>
            <button class="modal-delete" @click="confirmDelete">삭제</button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import MyPageSideBar from './MyPageSideBar.vue'
import httpClient from '@/utils/httpRequest'

const router = useRouter()

const campaigns = ref([])

onMounted(async () => {
  try {
    const response = await httpClient.get('http://localhost:3001/campaign-list')
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

<style scoped>
.campaign-list-layout {
  display: flex;
  min-height: 100vh;
  background: #fff;
}
.campaign-list-content {
  flex: 1;
  padding: 80px 0 0 0;
  max-width: 900px;
  margin: 0 auto;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
}
.campaign-list-title {
  font-size: 2rem;
  font-weight: 700;
  color: #9B51E0;
  margin-bottom: 40px;
}
.campaign-list-box {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.06);
  padding: 32px 32px 24px 32px;
}
.campaign-card {
  display: flex;
  align-items: center;
  gap: 32px;
  background: #fafafd;
  border-radius: 12px;
  margin-bottom: 24px;
  padding: 18px 24px;
  position: relative;
  transition: box-shadow 0.15s;
}
.campaign-card:hover {
  box-shadow: 0 4px 16px rgba(140,48,245,0.08);
}
.card-actions {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 8px;
  margin-left: 8px;
}
.delete-btn {
  background: #eee;
  border: none;
  color: #888;
  font-size: 1.2rem;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(255,59,59,0.08);
  z-index: 3;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.15s, color 0.15s;
  padding: 0;
  margin-left: 8px;
  position: static;
  transform: none;
}
.delete-btn:hover {
  background: #ff3b3b;
  color: #fff;
}
.trash-icon {
  display: block;
  width: 18px;
  height: 18px;
}
.campaign-thumb {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #eee;
  background: #fff;
}
.campaign-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.campaign-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #222;
}
.campaign-meta {
  display: flex;
  gap: 32px;
  font-size: 1rem;
  color: #666;
  align-items: center;
}
.campaign-status-toggle {
  display: flex;
  gap: 8px;
}
.status-btn {
  border: none;
  border-radius: 16px;
  padding: 4px 18px;
  font-size: 1rem;
  font-weight: 600;
  background: #f2eaff;
  color: #bbb;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
}
.status-btn.active {
  background: #9B51E0;
  color: #fff;
}
.status-btn.inactive {
  background: #eee;
  color: #888;
}
.status-btn:not(.active):hover, .status-btn:not(.inactive):hover {
  background: #e1cfff;
  color: #9B51E0;
}
.detail-btn {
  background: #E1CFFF;
  color: #9B51E0;
  border: none;
  border-radius: 8px;
  padding: 10px 32px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.detail-btn.active {
  background: #9B51E0;
  color: #fff;
}
.detail-btn:disabled {
  background: #f2eaff;
  color: #bbb;
  cursor: not-allowed;
}
.empty-msg {
  text-align: center;
  color: #bbb;
  font-size: 1.1rem;
  margin: 48px 0;
}
.modal-backdrop {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}
.modal-box {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 16px rgba(140,48,245,0.10);
  padding: 32px 40px 24px 40px;
  min-width: 320px;
  text-align: center;
}
.modal-message {
  font-size: 1.15rem;
  color: #333;
  margin-bottom: 24px;
}
.modal-actions {
  display: flex;
  justify-content: center;
  gap: 24px;
}
.modal-cancel {
  background: #eee;
  color: #888;
  border: none;
  border-radius: 8px;
  padding: 10px 32px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.modal-cancel:hover {
  background: #e1cfff;
  color: #9B51E0;
}
.modal-confirm {
  background: #9B51E0;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 32px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.modal-confirm:hover {
  background: #7B21E8;
}
.modal-delete {
  background: #ff3b3b;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 32px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.modal-delete:hover {
  background: #d60000;
}
</style> 