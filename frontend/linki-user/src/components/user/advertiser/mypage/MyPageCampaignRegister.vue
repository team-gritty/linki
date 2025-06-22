<template>
  <div class="register-content">
    <template v-if="showError && warningMessage">
      <div class="form-global-error">{{ warningMessage }}</div>
    </template>
    <h1 class="register-title">캠페인 등록</h1>
    <form class="register-form" @submit.prevent="submitForm">
      <div class="form-row">
        <div class="form-group">
          <label>캠페인 제품명 <span class="required-asterisk">*</span></label>
          <input type="text" placeholder="예) 리멜 마스카라" v-model="productName" :class="{'input-error': showError && productName.trim() === ''}" />
          <div v-if="showError && productName.trim() === ''" class="field-error">캠페인 제품명을 입력해주세요.</div>
        </div>
        <div class="form-group">
          <label>캠페인 조건</label>
          <input type="text" placeholder="예) 구독자 1만명 이상, 뷰티 유튜버" v-model="productCondition" />
        </div>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>상세 설명 <span class="required-asterisk">*</span></label>
          <input type="text" placeholder="예) 제품 사용 후기 영상 1건 업로드" v-model="productDesc" :class="{'input-error': showError && productDesc.trim() === ''}" />
          <div v-if="showError && productDesc.trim() === ''" class="field-error">상세 설명을 입력해주세요.</div>
        </div>
        <div class="form-group">
          <label>카테고리 <span class="required-asterisk">*</span></label>
          <div class="category-dropdown" @click="toggleCategoryDropdown">
            <span class="category-label">{{ selectedCategory || '카테고리 선택' }}</span>
            <svg class="dropdown-icon" width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 6L8 10L12 6" stroke="#6B7280" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <div v-if="categoryDropdownOpen" class="dropdown-menu compact-dropdown-menu" @click.stop>
              <div class="category-list">
                <div v-for="cat in categories" :key="cat" class="category-item compact-category-item" @click="selectCategory(cat)">
                  {{ cat }}
                </div>
              </div>
            </div>
          </div>
          <div v-if="showError && selectedCategory.trim() === ''" class="field-error">카테고리를 선택해주세요.</div>
        </div>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>제품 이미지 <span class="required-asterisk">*</span></label>
          <input type="file" accept="image/*" @change="onFileChange" :class="{'input-error': showError && imageUrl.trim() === '' && fileName.trim() === ''}" />
          <div v-if="imageUrl" class="image-preview">
            <img :src="imageUrl" alt="미리보기" />
            <div class="filename">{{ fileName }}</div>
          </div>
          <div v-else class="filename">{{ fileName }}</div>
          <div v-if="showError && imageUrl.trim() === '' && fileName.trim() === ''" class="field-error">이미지를 업로드해주세요.</div>
        </div>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>광고 시작일 <span class="required-asterisk">*</span></label>
          <input type="date" v-model="adStartDate" :class="{'input-error': showError && adStartDate.trim() === ''}" />
          <div v-if="showError && adStartDate.trim() === ''" class="field-error">광고 시작일을 입력해주세요.</div>
        </div>
        <div class="form-group">
          <label>캠페인 모집 마감일 <span class="required-asterisk">*</span></label>
          <input type="date" v-model="adEndDate" :class="{'input-error': showError && adEndDate.trim() === ''}" />
          <div v-if="showError && adEndDate.trim() === ''" class="field-error">캠페인 모집 마감일을 입력해주세요.</div>
        </div>
      </div>
      <div class="form-actions">
      <!-- 필수 입력 항목이 모두 입력되었는지 확인 후 등록 가능  -->
        <button
          type="submit"
          class="submit-btn"
          :disabled="!isFormValid"
        >
          등록
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
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
const warningMessage = ref('')
const showError = ref(false)

const categories = [
  '패션', '뷰티', '푸드 / 먹방', '엔터테인먼트', '여행', '스포츠', '음악', '전자기기', 'Vlog/라이프스타일', '교육', '동물/펫'
]

// 필수 입력 항목이 모두 입력되었는지 확인하기
const isFormValid = computed(() => {
  return (
    productName.value.trim() !== '' &&
    productDesc.value.trim() !== '' &&
    selectedCategory.value.trim() !== '' &&
    (imageUrl.value.trim() !== '' || fileName.value.trim() !== '') &&
    adStartDate.value.trim() !== '' &&
    adEndDate.value.trim() !== ''
  );
});

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
 * 필수 항목 입력 후 캠페인 등록 폼 제출
 */
async function submitForm() {
  warningMessage.value = '';
  showError.value = false;
  if (!isFormValid.value) {
    warningMessage.value = '필수 항목을 모두 입력해주세요.';
    showError.value = true;
    return;
  }
  const payload = {
    productName: productName.value,
    adCondition: productCondition.value,
    description: productDesc.value,
    category: selectedCategory.value,
    imageUrl: imageUrl.value || 'https://via.placeholder.com/80x60?text=이미지',
    startDate: adStartDate.value,
    deadline: adEndDate.value
  };
  try {
    await httpClient.post('/campaign-register', payload);
    // 폼 초기화
    productName.value = '';
    productCondition.value = '';
    productDesc.value = '';
    adStartDate.value = '';
    adEndDate.value = '';
    imageUrl.value = '';
    fileName.value = '';
    selectedCategory.value = '';
    alert('등록이 완료되었습니다!');
  } catch (e) {
    alert('등록에 실패했습니다.');
  }
}
</script>

<style>
@import '@/assets/css/mypage.css';
/* Required asterisk style */
.required-asterisk {
  color: #dc2626;
  margin-left: 2px;
  font-size: 1.1em;
}
.input-error {
  border: 1.5px solid #dc2626 !important;
  background: #fff0f0;
}
.field-error {
  color: #dc2626;
  font-size: 0.95em;
  margin-top: 4px;
}
/* Global error message for form */
.form-global-error {
  color: #dc2626;
  background: #fff0f0;
  border: 1px solid #dc2626;
  border-radius: 6px;
  padding: 10px 16px;
  margin-bottom: 18px;
  font-size: 1.05em;
  text-align: center;
}
/* Compact category dropdown item */
.compact-category-item {
  padding: 2px 8px !important;  /* 기존보다 작은 패딩 */
  font-size: 0.85rem !important;  /* 폰트 사이즈 줄이기 */
  min-height: 18px !important;  /* 최소 높이 줄이기 */
  line-height: 1 !important;  /* 라인 높이 줄이기 */
}

.compact-category-item:hover {
  background: #f3f4f6 !important;
  color: #7B21E8 !important;
}
/* Compact dropdown menu */
.compact-dropdown-menu {
  max-height: 220px;
  overflow-y: auto;
  min-width: 160px;
  width: 100%;
  box-sizing: border-box;
}
</style> 