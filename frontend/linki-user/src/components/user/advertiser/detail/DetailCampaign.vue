<template>
  <div class="campaign-detail-wrap">
  

    <div class="tab-underline"></div>
    <div v-if="tab === 'intro'" class="campaign-detail-main">
      <div class="detail-img-box">
        <img class="main-img" :src="form.productImg" alt="제품 메인 이미지" />
      </div>
      <div class="detail-info-box">
        <div class="detail-title">캠페인 설명</div>
        <div class="detail-desc">
          <template v-if="editMode">
            <textarea v-model="form.productDesc" class="edit-textarea" rows="6" />
          </template>
          <template v-else>{{ form.productDesc }}</template>
        </div>
        <div class="detail-meta-service">
          <div class="meta-row-service">
            <span class="meta-label">광고 신청 마감일</span>
            <span class="meta-value">
              <template v-if="editMode">
                <input type="date" v-model="form.productDeadline" class="edit-input" />
              </template>
              <template v-else>{{ formatDate(form.productDeadline) }}</template>
            </span>
          </div>
          <div class="meta-row-service">
            <span class="meta-label">광고 조건</span>
            <span class="meta-value">
              <template v-if="editMode">
                <input v-model="form.productCondition" class="edit-input" />
              </template>
              <template v-else>{{ form.productCondition }}</template>
            </span>
          </div>
          <div class="meta-row-service">
            <span class="meta-label">카테고리</span>
            <span class="meta-value">
              <template v-if="editMode">
                <select v-model="form.productCategory" class="edit-input">
                  <option value="게임">게임</option>
                  <option value="IT">IT</option>
                  <option value="가전">가전</option>
                  <option value="기타">기타</option>
                </select>
              </template>
              <template v-else>{{ form.productCategory }}</template>
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
    <div v-else-if="tab === 'proposal'">
      <DetailProposalList :campaign-id="id" />
    </div>
    <div v-else-if="tab === 'chat'">
      <DetailChat :campaign-id="id" />
    </div>
    <div v-if="tab === 'contract'">
      <DetailContractList />
    </div>
    <div v-if="showToast" class="toast-success">
      정상적으로 수정되었습니다.
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import DetailContractList from './DetailContractList.vue'
import DetailProposalList from './DetailProposalList.vue'
import DetailChat from './DetailChat.vue'
import { campaignAPI } from '@/api/campaign'

const router = useRouter()
const route = useRoute()

function goToList() {
  router.push('/mypage/advertiser/campaign-list')
}

const props = defineProps({
  campaignId: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['update:campaign-info'])

const editMode = ref(false)
const showToast = ref(false)
const campaignInfo = ref({
  productImg: '',
  productName: '',
  companyName: '',
  productDesc: '',
  productDeadline: '',
  productCondition: '',
  productCategory: ''
})
const form = ref({ ...campaignInfo.value })

const fetchCampaignInfo = async () => {
  try {
    console.log('DetailCampaign: Fetching campaign info for ID:', props.campaignId)
    const data = await campaignAPI.getMyCampaignDetail(props.campaignId)
    console.log('DetailCampaign: Received campaign data:', data)
    campaignInfo.value = data
    form.value = { ...data }
  } catch (error) {
    console.error('Failed to fetch campaign info:', error)
    // 에러 시 더미 데이터로 폴백
    const dummyData = {
      productImg: 'https://cdn.pixabay.com/photo/2016/03/31/19/14/controller-1294077_1280.png',
      productName: '게임기',
      companyName: 'HAVIT HV-G92 Gamepad',
      productDesc: `Ipsum has been the industry's standard dummy text ever since the 1500s...`,
      productDeadline: '2025-05-26',
      productCondition: '평균 조회수 2만 회 이상',
      productCategory: '게임'
    }
    campaignInfo.value = dummyData
    form.value = { ...dummyData }
  }
}

async function save() {
  try {
    // TODO: API를 통해 캠페인 정보 업데이트
    // await campaignAPI.updateMyCampaign(props.campaignId, form.value)
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

const tab = ref('intro')
const id = computed(() => route.params.id)

onMounted(() => {
  fetchCampaignInfo()
})
</script>

<style scoped>
@import '@/assets/css/detail.css';

</style> 