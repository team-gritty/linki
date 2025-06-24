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
        <div class="form-group">
          <label>공개 상태 <span class="required-asterisk">*</span></label>
          <div class="publish-status-dropdown" @click="togglePublishStatusDropdown">
            <span class="status-label">{{ getPublishStatusLabel(selectedPublishStatus) || '공개 상태 선택' }}</span>
            <svg class="dropdown-icon" width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 6L8 10L12 6" stroke="#6B7280" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <div v-if="publishStatusDropdownOpen" class="dropdown-menu compact-dropdown-menu" @click.stop>
              <div class="status-list">
                <div v-for="status in publishStatuses" :key="status.value" class="status-item compact-status-item" @click="selectPublishStatus(status)">
                  {{ status.label }}
                </div>
              </div>
            </div>
          </div>
          <div v-if="showError && selectedPublishStatus.trim() === ''" class="field-error">공개 상태를 선택해주세요.</div>
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
import campaignApi from '@/api/advertiser/advertiser-campaign'

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
  '뷰티', '패션', '스포츠', '음식', '브이로그', '여행', '음악', '교육', '동물', '전자제품', '엔터테인먼트'
]

const publishStatuses = [
  { value: 'HIDDEN', label: '숨김' },
  { value: 'ACTIVE', label: '공개' }
]

const selectedPublishStatus = ref('ACTIVE')
const publishStatusDropdownOpen = ref(false)

// 필수 입력 항목이 모두 입력되었는지 확인하기
const isFormValid = computed(() => {
  return (
    productName.value.trim() !== '' &&
    productDesc.value.trim() !== '' &&
    selectedCategory.value.trim() !== '' &&
    (imageUrl.value.trim() !== '' || fileName.value.trim() !== '') &&
    adStartDate.value.trim() !== '' &&
    adEndDate.value.trim() !== '' &&
    selectedPublishStatus.value.trim() !== ''
  );
});

// 파일 변경 처리를 위한 변수 추가
const selectedFile = ref(null)

function onFileChange(e) {
  const file = e.target.files[0]
  if (file) {
    fileName.value = file.name
    selectedFile.value = file // 실제 파일 객체 저장
    
    // 미리보기를 위한 Base64 변환 (서버 전송용이 아님)
    const reader = new FileReader()
    reader.onload = (ev) => {
      imageUrl.value = ev.target.result
    }
    reader.readAsDataURL(file)
  } else {
    fileName.value = ''
    imageUrl.value = ''
    selectedFile.value = null
  }
}

function toggleCategoryDropdown() {
  categoryDropdownOpen.value = !categoryDropdownOpen.value
}

function selectCategory(cat) {
  selectedCategory.value = cat
  categoryDropdownOpen.value = false
}

function togglePublishStatusDropdown() {
  publishStatusDropdownOpen.value = !publishStatusDropdownOpen.value
}

function selectPublishStatus(status) {
  selectedPublishStatus.value = status.value
  publishStatusDropdownOpen.value = false
}

function onDocumentClick(e) {
  if (categoryDropdownOpen.value || publishStatusDropdownOpen.value) {
    const dropdown = document.querySelector('.dropdown-menu')
    if (dropdown && !dropdown.contains(e.target)) {
      categoryDropdownOpen.value = false
      publishStatusDropdownOpen.value = false
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
  
  // 날짜를 LocalDateTime 형식으로 변환 (ISO 8601 형식)
  const deadlineDate = new Date(adEndDate.value + 'T23:59:59');
  
  // 백엔드 CampaignRequest DTO에 맞는 데이터 구조로 변환
  const campaignData = {
    campaignName: productName.value,
    campaignDesc: productDesc.value,
    campaignCondition: productCondition.value || '',
    campaignImg: '', // ObjectStorage에서 URL 생성되므로 빈 값
    campaignDeadline: deadlineDate.toISOString(),
    campaignPublishStatus: selectedPublishStatus.value,
    campaignCategory: selectedCategory.value
  };
  
  console.log('전송할 캠페인 데이터:', campaignData);
  
  try {
    // FormData 생성
    const formData = new FormData();
    
    // 이미지 파일 추가 (있는 경우)
    if (selectedFile.value) {
      formData.append('campaignImage', selectedFile.value);
    }
    
    // 캠페인 데이터를 JSON 문자열로 변환하여 추가
    formData.append('campaignData', JSON.stringify(campaignData));
    
    // API 호출 (multipart/form-data)
    await campaignApi.registerCampaignWithImage(formData);
    
    // 폼 초기화
    productName.value = '';
    productCondition.value = '';
    productDesc.value = '';
    adStartDate.value = '';
    adEndDate.value = '';
    imageUrl.value = '';
    fileName.value = '';
    selectedFile.value = null;
    selectedCategory.value = '';
    selectedPublishStatus.value = 'ACTIVE';
    
    alert('등록이 완료되었습니다!');
  } catch (e) {
    console.error('캠페인 등록 실패:', e);
    console.error('에러 상세:', e.response?.data);
    alert('등록에 실패했습니다. 콘솔을 확인해주세요.');
  }
}

function getPublishStatusLabel(value) {
  const status = publishStatuses.find(s => s.value === value);
  return status ? status.label : null;
}
</script>

<style>
@import '@/assets/css/mypage.css';

.register-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px;
}

.register-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 32px;
  text-align: center;
}

.register-form {
  background: #fff;
  border-radius: 16px;
  padding: 48px;
  box-shadow: 0 4px 20px rgba(123, 33, 232, 0.08);
}

.form-row {
  display: flex;
  gap: 32px;
  margin-bottom: 32px;
}

.form-group {
  flex: 1;
  position: relative;
}

.form-group label {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.required-asterisk {
  color: #e74c3c;
  font-weight: 700;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 16px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #7B21E8;
  box-shadow: 0 0 0 3px rgba(123, 33, 232, 0.1);
}

.input-error {
  border-color: #e74c3c !important;
  box-shadow: 0 0 0 3px rgba(231, 76, 60, 0.1) !important;
}

.field-error {
  color: #e74c3c;
  font-size: 14px;
  margin-top: 8px;
  font-weight: 500;
}

.form-global-error {
  background: #ffebee;
  color: #c62828;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 24px;
  font-weight: 500;
  text-align: center;
}

.category-dropdown,
.publish-status-dropdown {
  position: relative;
  width: 100%;
  padding: 16px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  background: white;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.2s ease;
}

.category-dropdown:hover,
.publish-status-dropdown:hover {
  border-color: #7B21E8;
}

.category-label,
.status-label {
  font-size: 16px;
  color: #333;
}

.dropdown-icon {
  transition: transform 0.2s ease;
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  max-height: 200px;
  overflow-y: auto;
}

.category-list,
.status-list {
  padding: 8px 0;
}

.category-item,
.status-item {
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s ease;
  font-size: 16px;
  color: #333;
}

.category-item:hover,
.status-item:hover {
  background: #f8f9fa;
  color: #7B21E8;
}

.image-preview {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.image-preview img {
  max-width: 200px;
  max-height: 150px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #e5e7eb;
}

.filename {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
}

.form-actions {
  display: flex;
  justify-content: center;
  margin-top: 48px;
}

.submit-btn {
  background: linear-gradient(135deg, #7B21E8 0%, #9333EA 100%);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 16px 48px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(123, 33, 232, 0.3);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(123, 33, 232, 0.4);
}

.submit-btn:disabled {
  background: #d1d5db;
  color: #9ca3af;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 24px;
  }
  
  .register-form {
    padding: 32px 24px;
  }
  
  .register-content {
    padding: 16px;
  }
}
</style> 