<template>
  <div class="campaign-detail-wrap">
    <div class="campaign-detail-main">
      <div v-if="!campaignInfo" class="loading">
        데이터를 불러오는 중입니다...
      </div>
      <div v-else class="campaign-content">
        <div class="detail-img-box">
          <img class="main-img" :src="form.campaignImg" alt="캠페인 메인 이미지" />
        </div>
        <div class="detail-info-box">
          <div class="detail-title">캠페인 설명</div>
          <div class="detail-desc">
            <template v-if="editMode">
              <textarea v-model="form.campaignDesc" class="edit-textarea" rows="6" />
            </template>
            <template v-else>{{ form.campaignDesc }}</template>
          </div>
          <div class="detail-meta-service">
            <div class="meta-row-service">
              <span class="meta-label">광고 신청 마감일</span>
              <span class="meta-value">
                <template v-if="editMode">
                  <input type="date" v-model="form.campaignDeadline" class="edit-input" />
                </template>
                <template v-else>{{ formatDate(form.campaignDeadline) }}</template>
              </span>
            </div>
            <div class="meta-row-service">
              <span class="meta-label">광고 조건</span>
              <span class="meta-value">
                <template v-if="editMode">
                  <input v-model="form.campaignCondition" class="edit-input" />
                </template>
                <template v-else>{{ form.campaignCondition }}</template>
              </span>
            </div>
            <div class="meta-row-service">
              <span class="meta-label">카테고리</span>
              <span class="meta-value">
                <template v-if="editMode">
                  <select v-model="form.campaignCategory" class="edit-input">
                    <option value="게임">게임</option>
                    <option value="IT">IT</option>
                    <option value="가전">가전</option>
                    <option value="기타">기타</option>
                  </select>
                </template>
                <template v-else>{{ form.campaignCategory }}</template>
              </span>
            </div>
            <div class="meta-row-service">
              <span class="meta-label">브랜드</span>
              <span class="meta-value">
                <template v-if="editMode">
                  <input v-model="form.companyName" class="edit-input" />
                </template>
                <template v-else>{{ form.companyName }}</template>
              </span>
            </div>
          </div>
          <button class="edit-btn" @click="editMode ? save() : editMode = true">
            {{ editMode ? '저장' : '수정' }}
          </button>
        </div>
      </div>
    </div>
    <div v-if="showToast" class="toast-success">
      정상적으로 수정되었습니다.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import campaignApi from '@/api/advertiser/advertiser-campaign'

const props = defineProps({
  campaignId: {
    type: String,
    required: true
  }
})

const editMode = ref(false)
const showToast = ref(false)
const campaignInfo = ref(null)
const form = ref({
  campaignImg: '',
  campaignName: '',
  companyName: '',
  campaignDesc: '',
  campaignDeadline: '',
  campaignCondition: '',
  campaignCategory: ''
})

const fetchCampaignInfo = async () => {
  try {
    console.log('DetailCampaign: Fetching campaign info for ID:', props.campaignId)
    const data = await campaignApi.getCampaignDetail(props.campaignId)
    console.log('DetailCampaign: Received campaign data:', data)
    campaignInfo.value = data
    form.value = { ...data }
  } catch (error) {
    console.error('Failed to fetch campaign info:', error)
  }
}

async function save() {
  try {
    await campaignApi.updateCampaign(props.campaignId, form.value)
    campaignInfo.value = { ...form.value }
    editMode.value = false
    showToast.value = true
    setTimeout(() => {
      showToast.value = false
    }, 1500)
  } catch (error) {
    console.error('Failed to update campaign:', error)
    alert('캠페인 정보 업데이트에 실패했습니다.')
  }
}

function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

onMounted(() => {
  fetchCampaignInfo()
})
</script>

<style scoped>
@import '@/assets/css/detail.css';

.loading {
  text-align: center;
  padding: 2rem;
  color: #666;
}

.campaign-detail-wrap {
  padding: 1rem;
}

.campaign-content {
  display: grid;
  gap: 2rem;
}

.detail-img-box {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
}

.main-img {
  width: 100%;
  height: auto;
  border-radius: 8px;
}

.edit-textarea {
  width: 100%;
  min-height: 100px;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.edit-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.edit-btn {
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background-color: #7c3aed;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.edit-btn:hover {
  background-color: #6d28d9;
}

.toast-success {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background-color: #10b981;
  color: white;
  padding: 1rem 2rem;
  border-radius: 4px;
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style> 