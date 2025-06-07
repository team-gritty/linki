<template>
  <div class="campaign-detail-wrap">
    <div class="campaign-summary-box">
      <div class="summary-left">
        <img class="summary-thumb" :src="form.image" alt="제품 썸네일" />
        <div class="summary-info">
          <div class="summary-title">
            <template v-if="editMode">
              <input v-model="form.title" class="edit-input" />
            </template>
            <template v-else>{{ form.title }}</template>
          </div>
          <div class="summary-sub">
            <template v-if="editMode">
              <input v-model="form.subtitle" class="edit-input" />
            </template>
            <template v-else>{{ form.subtitle }}</template>
          </div>
        </div>
      </div>
      <button class="go-list-btn" @click="goToList">캠페인 목록 <span class="arrow">→</span></button>
    </div>
    <div class="campaign-tabs">
      <div class="tab" :class="{active: tab === 'intro'}" @click="tab = 'intro'">캠페인 설명</div>
      <div class="tab" :class="{active: tab === 'proposal'}" @click="tab = 'proposal'">제안서 목록</div>
      <div class="tab" :class="{active: tab === 'chat'}" @click="tab = 'chat'">채팅 목록</div>
      <div class="tab" :class="{active: tab === 'contract'}" @click="tab = 'contract'">계약서 목록</div>
    </div>
    <div class="tab-underline"></div>
    <div v-if="tab === 'intro'" class="campaign-detail-main">
      <div class="detail-img-box">
        <img class="main-img" :src="form.image" alt="제품 메인 이미지" />
      </div>
      <div class="detail-info-box">
        <div class="detail-title">캠페인 설명</div>
        <div class="detail-desc">
          <template v-if="editMode">
            <textarea v-model="form.desc" class="edit-textarea" rows="6" />
          </template>
          <template v-else>{{ form.desc }}</template>
        </div>
        <div class="detail-meta-service">
          <div class="meta-row-service">
            <span class="meta-label">광고 신청 마감일</span>
            <span class="meta-value">
              <template v-if="editMode">
                <input type="date" v-model="form.due" class="edit-input" />
              </template>
              <template v-else>{{ form.due }}</template>
            </span>
          </div>
          <div class="meta-row-service">
            <span class="meta-label">광고 조건</span>
            <span class="meta-value">
              <template v-if="editMode">
                <input v-model="form.condition" class="edit-input" />
              </template>
              <template v-else>{{ form.condition }}</template>
            </span>
          </div>
          <div class="meta-row-service">
            <span class="meta-label">카테고리</span>
            <span class="meta-value">
              <template v-if="editMode">
                <select v-model="form.category" class="edit-input">
                  <option value="게임">게임</option>
                  <option value="IT">IT</option>
                  <option value="가전">가전</option>
                  <option value="기타">기타</option>
                </select>
              </template>
              <template v-else>{{ form.category }}</template>
            </span>
          </div>
          <div class="meta-row-service">
            <span class="meta-label">브랜드</span>
            <span class="meta-value">
              <template v-if="editMode">
                <input v-model="form.brand" class="edit-input" />
              </template>
              <template v-else>{{ form.brand }}</template>
            </span>
          </div>
        </div>
        <button class="edit-btn" @click="editMode ? save() : editMode = true">
          {{ editMode ? '저장' : '수정' }}
        </button>
      </div>
    </div>
    <div v-else-if="tab === 'proposal'">
      <ProposalListComponent :campaign-id="id" />
    </div>
    <div v-if="tab === 'contract'">
      <ContractListComponent />
    </div>
    <div v-if="showToast" class="toast-success">
      정상적으로 수정되었습니다.
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import ContractListComponent from './components/ContractListComponent.vue'
import ProposalListComponent from './components/ProposalListComponent.vue'
const router = useRouter()
const route = useRoute()

function goToList() {
  router.push('/mypage/campaign-list')
}

const editMode = ref(false)
const showToast = ref(false)
const form = ref({
  image: 'https://cdn.pixabay.com/photo/2016/03/31/19/14/controller-1294077_1280.png',
  title: '게임기',
  subtitle: 'HAVIT HV-G92 Gamepad',
  desc: `Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.`,
  due: '2025-05-26',
  condition: '평균 조회수 2만 회 이상',
  category: '게임',
  brand: 'HAVIT'
})
function save() {
  // 실제 서비스에서는 이곳에서 API로 저장 요청
  editMode.value = false
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 1500)
}

const tab = ref('intro')
const id = computed(() => route.params.id)
</script>

<style scoped>
.campaign-detail-wrap {
  max-width: 1100px;
  margin: 48px auto 0 auto;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
}
.campaign-summary-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 2px solid #E1CFFF;
  border-radius: 8px;
  padding: 32px 40px;
  margin-bottom: 32px;
  background: #fff;
}
.summary-left {
  display: flex;
  align-items: center;
  gap: 24px;
}
.summary-thumb {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 50%;
  border: 2px solid #eee;
  background: #fff;
}
.summary-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.summary-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: #222;
}
.summary-sub {
  font-size: 1rem;
  color: #888;
}
.go-list-btn {
  background: #9B51E0;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 14px 36px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background 0.2s;
}
.go-list-btn:hover {
  background: #7B21E8;
}
.arrow {
  font-size: 1.2em;
}
.campaign-tabs {
  display: flex;
  gap: 0;
  margin-bottom: 0;
  background: #fff;
}
.tab {
  flex: 1;
  text-align: center;
  font-size: 1.1rem;
  font-weight: 500;
  color: #888;
  padding: 14px 0 12px 0;
  border: none;
  background: none;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  transition: color 0.2s, border 0.2s;
}
.tab.active {
  color: #8C30F5;
  border-bottom: 3px solid #8C30F5;
  background: #F5F0FF;
  font-weight: 700;
}
.tab-underline {
  border-bottom: 1.5px solid #eee;
  margin-bottom: 32px;
}
.campaign-detail-main {
  display: flex;
  gap: 48px;
  background: #fafafd;
  border-radius: 12px;
  padding: 48px 40px;
  align-items: flex-start;
}
.detail-img-box {
  flex: 0 0 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(140,48,245,0.06);
}
.main-img {
  width: 260px;
  height: 200px;
  object-fit: contain;
  border-radius: 8px;
  background: #fff;
}
.detail-info-box {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.detail-title {
  font-size: 1.2rem;
  font-weight: 700;
  margin-bottom: 8px;
  color: #222;
}
.detail-desc {
  font-size: 1rem;
  color: #333;
  margin-bottom: 18px;
  line-height: 1.7;
}
.detail-meta-service {
  display: flex;
  flex-direction: column;
  gap: 0;
  margin-bottom: 18px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fff;
  overflow: hidden;
}
.meta-row-service {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 1rem;
  color: #444;
  padding: 16px 24px;
  border-bottom: 1px solid #f2f2f2;
}
.meta-row-service:last-child {
  border-bottom: none;
}
.meta-label {
  font-weight: 600;
  color: #222;
}
.meta-value {
  color: #666;
  font-weight: 500;
}
.edit-btn {
  background: #9B51E0;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 32px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  align-self: flex-end;
  transition: background 0.2s;
  margin-top: 16px;
}
.edit-btn:hover {
  background: #7B21E8;
}
.edit-input {
  font-size: 1rem;
  padding: 6px 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
  background: #fafafd;
  color: #222;
  margin: 0 0 0 0;
  min-width: 0;
}
.edit-textarea {
  font-size: 1rem;
  padding: 8px 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
  background: #fafafd;
  color: #222;
  width: 100%;
  resize: vertical;
}
.toast-success {
  position: fixed;
  top: 32px;
  right: 40px;
  z-index: 4000;
  background: #27ae60;
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
  padding: 16px 32px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(39,174,96,0.10);
  animation: toast-fadein 0.3s, toast-fadeout 0.5s 1.2s;
  pointer-events: none;
}
@keyframes toast-fadein {
  from { opacity: 0; transform: translateY(-16px); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes toast-fadeout {
  from { opacity: 1; }
  to { opacity: 0; }
}
</style> 