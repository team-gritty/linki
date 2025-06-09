<template>
  <div class="register-content">
    <h1 class="register-title">캠페인 등록</h1>
    <form class="register-form" @submit.prevent="submitForm">
      <div class="form-row">
        <div class="form-group">
          <label>캠페인 제품명</label>
          <input type="text" placeholder="예) 리멜 마스카라" v-model="productName" />
        </div>
        <div class="form-group">
          <label>캠페인 조건</label>
          <input type="text" placeholder="예) 구독자 1만명 이상, 뷰티 유튜버" v-model="productCondition" />
        </div>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>상세 설명</label>
          <input type="text" placeholder="예) 제품 사용 후기 영상 1건 업로드" v-model="productDesc" />
        </div>
        <div class="form-group">
          <label>카테고리</label>
          <div class="category-dropdown" @click="toggleCategoryDropdown">
            <span class="category-label">{{ selectedCategory || '카테고리 선택' }}</span>
            <svg class="dropdown-icon" width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 6L8 10L12 6" stroke="#6B7280" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <div v-if="categoryDropdownOpen" class="dropdown-menu" @click.stop>
              <div class="category-list">
                <div v-for="cat in categories" :key="cat" class="category-item" @click="selectCategory(cat)">
                  {{ cat }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>제품 이미지</label>
          <input type="file" accept="image/*" @change="onFileChange" />
          <div v-if="imageUrl" class="image-preview">
            <img :src="imageUrl" alt="미리보기" />
            <div class="filename">{{ fileName }}</div>
          </div>
          <div v-else class="filename">{{ fileName }}</div>
        </div>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>광고 시작일</label>
          <input type="date" v-model="adStartDate" />
        </div>
        <div class="form-group">
          <label>캠페인 모집 마감일</label>
          <input type="date" v-model="adEndDate" />
        </div>
      </div>
      <div class="form-actions">
        <button type="button" class="cancel-btn">Cancel</button>
        <button type="submit" class="submit-btn">
          등록 신청 <span class="arrow">→</span>
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import httpClient from '@/utils/httpRequest'

const productName = ref('')
const productCondition = ref('')
const productDesc = ref('')
const adStartDate = ref('')
const adEndDate = ref('')
const imageUrl = ref('')
const fileName = ref('')
const selectedCategory = ref('')
const categoryDropdownOpen = ref(false)

const categories = [
  '패션', '뷰티', '푸드 / 먹방', '엔터테인먼트', '여행', '스포츠', '음악', '전자기기', 'Vlog/라이프스타일', '교육', '동물/펫'
]

function onFileChange(e) {
  const file = e.target.files[0]
  if (file) {
    fileName.value = file.name
    const reader = new FileReader()
    reader.onload = (ev) => {
      imageUrl.value = ev.target.result
    }
    reader.readAsDataURL(file)
  } else {
    fileName.value = ''
    imageUrl.value = ''
  }
}

function toggleCategoryDropdown() {
  categoryDropdownOpen.value = !categoryDropdownOpen.value
}

function selectCategory(cat) {
  selectedCategory.value = cat
  categoryDropdownOpen.value = false
}

function onDocumentClick(e) {
  if (categoryDropdownOpen.value) {
    const dropdown = document.querySelector('.dropdown-menu')
    if (dropdown && !dropdown.contains(e.target)) {
      categoryDropdownOpen.value = false
    }
  }
}

onMounted(() => {
  document.addEventListener('mousedown', onDocumentClick)
})

onBeforeUnmount(() => {
  document.removeEventListener('mousedown', onDocumentClick)
})

/**
 * 캠페인 등록 폼 제출
 */
async function submitForm() {
  const payload = {
    productName: productName.value,
    adCondition: productCondition.value,
    description: productDesc.value,
    category: selectedCategory.value,
    imageUrl: imageUrl.value || 'https://via.placeholder.com/80x60?text=이미지',
    startDate: adStartDate.value,
    deadline: adEndDate.value
  }
  try {
    await httpClient.post('/campaign-register', payload)
    // 폼 초기화
    productName.value = ''
    productCondition.value = ''
    productDesc.value = ''
    adStartDate.value = ''
    adEndDate.value = ''
    imageUrl.value = ''
    fileName.value = ''
    selectedCategory.value = ''
    alert('등록이 완료되었습니다!')
  } catch (e) {
    alert('등록에 실패했습니다.')
  }
}
</script>

<style>
@import '@/assets/css/mypage.css';
</style> 