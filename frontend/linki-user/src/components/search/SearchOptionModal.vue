<template>
  <div class="modal-backdrop" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <h2 class="modal-title">검색 옵션</h2>
      <div class="modal-section">
        <div class="form-row">
          <div class="form-group">
            <label class="section-label">카테고리</label>
            <div class="category-dropdown-modal" @click="toggleCategoryDropdown">
              <span class="category-label-modal">{{ selectedCategoryLabel }}</span>
              <svg class="dropdown-icon" :class="{ 'rotated': categoryDropdownOpen }" width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4 6L8 10L12 6" stroke="#6B7280" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <div v-if="categoryDropdownOpen" class="dropdown-menu google-style-dropdown" @click.stop>
                <div class="category-option" 
                     :class="{ 'selected': selectedCategory === '' }"
                     @click="selectCategory('')">
                  <span>전체</span>
                </div>
                <div v-for="cat in categories" 
                     :key="cat" 
                     class="category-option"
                     :class="{ 'selected': selectedCategory === cat }"
                     @click="selectCategory(cat)">
                  <span>{{ cat }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="form-row">
          <div class="form-group full-width">
            <label class="section-label">구독자</label>
            <div class="subscriber-direct-row">
              <input type="number" placeholder="최소" class="direct-input" v-model="subscriberMin" />
              <span>~</span>
              <input type="number" placeholder="최대" class="direct-input" v-model="subscriberMax" />
            </div>
            <div class="subscriber-options">
              <label v-for="(opt, i) in subscriberOptions" :key="i">
                <input type="checkbox" v-model="subscriberChecks[i]" @change="handleSubscriberCheckChange(i)" /> 
                {{ opt }}
              </label>
            </div>
          </div>
        </div>
        <div class="form-row">
          <div class="form-group full-width">
            <label class="section-label">평균 조회수</label>
            <div class="subscriber-direct-row">
              <input type="number" placeholder="최소" class="direct-input" v-model="viewMin" />
              <span>~</span>
              <input type="number" placeholder="최대" class="direct-input" v-model="viewMax" />
            </div>
            <div class="subscriber-options view-checkbox-row">
              <label v-for="(opt, i) in viewOptions" :key="i">
                <input type="checkbox" v-model="viewChecks[i]" @change="handleViewCheckChange(i)" /> 
                {{ opt }}
              </label>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-btn-row">
        <button class="clear-btn" @click="clearAll">전체해제</button>
        <button class="modal-search-btn" @click="handleSearch">검색</button> 
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, defineEmits, watch } from 'vue'

const props = defineProps({
  initCategory: String,
  initSubscriberChecks: Array,
  initSubscriberMin: String,
  initSubscriberMax: String,
  initViewChecks: Array,
  initViewMin: String,
  initViewMax: String
})

const emit = defineEmits(['close', 'apply-filters'])

const subscriberOptions = [
  '전체', '~1천', '1천~5천', '5천~1만', '1만~5만', '5만~10만', '10만~50만', '50만~100만', '100만+'
]
const viewOptions = [
  '전체', '~1천', '1천~1만', '1만~10만', '10만~100만', '100만+'
]

// 한국어 -> 영어 카테고리 매핑
const categoryMapping = {
  '패션': 'FASHION',
  '뷰티': 'BEAUTY', 
  '푸드 / 먹방': 'FOOD',
  '엔터테인먼트': 'ENTERTAINMENT',
  '여행': 'TRAVEL',
  '스포츠': 'SPORTS',
  '음악': 'MUSIC',
  '전자기기': 'ELECTRONICS',
  'Vlog/라이프스타일': 'VLOG',
  '교육': 'EDUCATION',
  '동물/펫': 'ANIMAL'
}

const subscriberChecks = ref(props.initSubscriberChecks ? [...props.initSubscriberChecks] : [true, false, false, false, false, false, false, false, false])
const viewChecks = ref(props.initViewChecks ? [...props.initViewChecks] : [true, false, false, false, false, false])
const subscriberMin = ref(props.initSubscriberMin || '')
const subscriberMax = ref(props.initSubscriberMax || '')
const viewMin = ref(props.initViewMin || '')
const viewMax = ref(props.initViewMax || '')
const categories = [
  '패션', '뷰티', '푸드 / 먹방', '엔터테인먼트', '여행', '스포츠', '음악', '전자기기', 'Vlog/라이프스타일', '교육', '동물/펫'
]
const selectedCategory = ref(props.initCategory || '')
const categoryDropdownOpen = ref(false)

const selectedCategoryLabel = computed(() => {
  return selectedCategory.value || '전체'
})

const toggleCategoryDropdown = () => { 
  categoryDropdownOpen.value = !categoryDropdownOpen.value 
}

function closeDropdownAndEmit() {
  categoryDropdownOpen.value = false
}

// 구독자 체크박스 변경 처리
function handleSubscriberCheckChange(index) {
  if (index === 0) { // '전체' 선택
    if (subscriberChecks.value[0]) {
      subscriberChecks.value = subscriberChecks.value.map((_, i) => i === 0)
    }
  } else {
    subscriberChecks.value[0] = false // 다른 옵션 선택 시 '전체' 해제
  }
}

// 평균 조회수 체크박스 변경 처리
function handleViewCheckChange(index) {
  if (index === 0) { // '전체' 선택
    if (viewChecks.value[0]) {
      viewChecks.value = viewChecks.value.map((_, i) => i === 0)
    }
  } else {
    viewChecks.value[0] = false // 다른 옵션 선택 시 '전체' 해제
  }
}

// 구독자 범위 계산
function getSubscriberRange() {
  // 직접 입력 값이 있으면 우선 사용
  if (subscriberMin.value || subscriberMax.value) {
    return {
      min: subscriberMin.value ? parseInt(subscriberMin.value) : 0,
      max: subscriberMax.value ? parseInt(subscriberMax.value) : 999999999 // 10억으로 제한
    }
  }
  
  // 체크박스 선택 확인 - "전체"가 아닌 특정 범위가 선택된 경우만
  for (let i = 1; i < subscriberChecks.value.length; i++) {
    if (subscriberChecks.value[i]) {
      switch (i) {
        case 1: return { min: 0, max: 1000 } // ~1천
        case 2: return { min: 1000, max: 5000 } // 1천~5천
        case 3: return { min: 5000, max: 10000 } // 5천~1만
        case 4: return { min: 10000, max: 50000 } // 1만~5만
        case 5: return { min: 50000, max: 100000 } // 5만~10만
        case 6: return { min: 100000, max: 500000 } // 10만~50만
        case 7: return { min: 500000, max: 1000000 } // 50만~100만
        case 8: return { min: 1000000, max: 999999999 } // 100만+ (10억으로 제한)
      }
    }
  }
  
  // "전체"가 선택되었거나 아무것도 선택하지 않은 경우 - 필터 적용 안함
  return null
}

// 평균 조회수 범위 계산
function getViewCountRange() {
  // 직접 입력 값이 있으면 우선 사용
  if (viewMin.value || viewMax.value) {
    return {
      min: viewMin.value ? parseInt(viewMin.value) : 0,
      max: viewMax.value ? parseInt(viewMax.value) : 999999999 // 10억으로 제한
    }
  }
  
  // 체크박스 선택 확인 - "전체"가 아닌 특정 범위가 선택된 경우만
  for (let i = 1; i < viewChecks.value.length; i++) {
    if (viewChecks.value[i]) {
      switch (i) {
        case 1: return { min: 0, max: 1000 } // ~1천
        case 2: return { min: 1000, max: 10000 } // 1천~1만
        case 3: return { min: 10000, max: 100000 } // 1만~10만
        case 4: return { min: 100000, max: 1000000 } // 10만~100만
        case 5: return { min: 1000000, max: 999999999 } // 100만+ (10억으로 제한)
      }
    }
  }
  
  // "전체"가 선택되었거나 아무것도 선택하지 않은 경우 - 필터 적용 안함
  return null
}

// 검색 버튼 클릭 처리
function handleSearch() {
  console.log('=== 모달 검색 버튼 클릭 ===')
  console.log('selectedCategory:', selectedCategory.value)
  console.log('subscriberMin:', subscriberMin.value, typeof subscriberMin.value)
  console.log('subscriberMax:', subscriberMax.value, typeof subscriberMax.value)
  console.log('viewMin:', viewMin.value, typeof viewMin.value)
  console.log('viewMax:', viewMax.value, typeof viewMax.value)
  console.log('subscriberChecks:', subscriberChecks.value)
  console.log('viewChecks:', viewChecks.value)
  
  const subscriberRange = getSubscriberRange()
  const viewCountRange = getViewCountRange()
  
  console.log('계산된 subscriberRange:', subscriberRange)
  console.log('계산된 viewCountRange:', viewCountRange)
  
  const filters = {
    category: selectedCategory.value ? categoryMapping[selectedCategory.value] : null,
    // 모달 재오픈을 위한 원본 데이터 저장
    originalModalData: {
      selectedCategory: selectedCategory.value,
      subscriberMin: subscriberMin.value,
      subscriberMax: subscriberMax.value,
      subscriberChecks: [...subscriberChecks.value],
      viewMin: viewMin.value,
      viewMax: viewMax.value,
      viewChecks: [...viewChecks.value]
    }
  }
  
  // 구독자 수 필터가 있을 때만 추가
  if (subscriberRange) {
    filters.minSubscribers = subscriberRange.min
    filters.maxSubscribers = subscriberRange.max
  }
  
  // 평균 조회수 필터가 있을 때만 추가
  if (viewCountRange) {
    filters.minAvgViewCount = viewCountRange.min
    filters.maxAvgViewCount = viewCountRange.max
  }
  
  console.log('최종 전달할 filters:', JSON.stringify(filters, null, 2))
  console.log('=== 모달 검색 끝 ===')
  
  emit('apply-filters', filters)
  emit('close')
}

function onDocumentClick(e) {
  if (categoryDropdownOpen.value) {
    const dropdown = document.querySelector('.google-style-dropdown')
    if (dropdown && !dropdown.contains(e.target)) closeDropdownAndEmit()
  }
}

function onDocumentKeydown(e) {
  if (categoryDropdownOpen.value && (e.key === 'Enter' || e.keyCode === 13)) closeDropdownAndEmit()
}

onMounted(() => {
  document.addEventListener('mousedown', onDocumentClick)
  document.addEventListener('keydown', onDocumentKeydown)
})

onBeforeUnmount(() => {
  document.removeEventListener('mousedown', onDocumentClick)
  document.removeEventListener('keydown', onDocumentKeydown)
})

const clearAll = () => {
  subscriberChecks.value = [true, false, false, false, false, false, false, false, false]
  viewChecks.value = [true, false, false, false, false, false]
  subscriberMin.value = ''
  subscriberMax.value = ''
  viewMin.value = ''
  viewMax.value = ''
  selectedCategory.value = ''
}

function selectCategory(category) {
  selectedCategory.value = category
  closeDropdownAndEmit()
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  background: #fff;
  border-radius: 12px;
  padding: 24px 32px;
  min-width: 520px;
  min-height: 350px;
  box-shadow: 0 8px 28px rgba(0,0,0,0.12);
  position: relative;
  font-size: 14px;
}
.modal-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #202124;
}
.modal-section {
  margin-bottom: 16px;
}
.form-row {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 180px;
  flex: 1;
}
.form-group.full-width {
  flex: 1;
}
.form-select {
  height: 40px;
  border-radius: 8px;
  border: 1.5px solid #E0E0E0;
  padding: 0 16px;
  font-size: 1rem;
  background: #fff;
}
.subscriber-options {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 16px;
  margin-top: 4px;
}
.subscriber-options label {
  font-size: 14px;
  color: #5f6368;
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.15s;
}
.subscriber-options label:hover {
  background: #f8f9fa;
}
.subscriber-options input[type="checkbox"] {
  accent-color: #8C30F5;
  width: 16px;
  height: 16px;
}
.section-label {
  font-size: 14px;
  font-weight: 600;
  color: #202124;
  margin-bottom: 8px;
  display: inline-block;
}
.direct-input {
  width: 120px;
  height: 40px;
  border: 1px solid #dadce0;
  border-radius: 8px;
  padding: 0 12px;
  font-size: 14px;
  transition: border-color 0.15s, box-shadow 0.15s;
}
.direct-input:focus {
  outline: none;
  border-color: #8C30F5;
  box-shadow: 0 0 0 1px #8C30F5;
}
.subscriber-direct-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.subscriber-direct-row span {
  color: #5f6368;
  font-size: 14px;
}
.view-checkbox-row {
  margin-top: 0;
}
.modal-btn-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
  padding-bottom: 0;
  position: static;
}
.clear-btn {
  background: #fff;
  color: #8C30F5;
  border: 1px solid #dadce0;
  border-radius: 8px;
  padding: 10px 24px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s;
  font-size: 14px;
  margin-right: auto;
  margin-top: 0;
  position: static;
}
.clear-btn:hover {
  background: #f8f9fa;
  border-color: #8C30F5;
}
.modal-search-btn {
  background: #8C30F5;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 12px 32px;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.15s;
  position: static;
}
.modal-search-btn:hover {
  background: #7C3AED;
}
.category-dropdown-modal {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 500;
  color: #202124;
  font-size: 14px;
  cursor: pointer;
  position: relative;
  user-select: none;
  min-width: 180px;
  max-width: 180px;
  width: 180px;
  border: 1px solid #dadce0;
  border-radius: 8px;
  padding: 12px 16px;
  background: #fff;
  transition: border-color 0.15s, box-shadow 0.15s;
}
.category-dropdown-modal:hover {
  border-color: #8C30F5;
}
.category-dropdown-modal:focus-within {
  border-color: #8C30F5;
  box-shadow: 0 0 0 1px #8C30F5;
}
.category-label-modal {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  display: inline-block;
}
.dropdown-icon {
  transition: transform 0.2s;
  margin-left: 8px;
  flex-shrink: 0;
}
.dropdown-icon path {
  stroke: #5f6368;
}
.rotated {
  transform: rotate(180deg);
}
.google-style-dropdown {
  position: absolute;
  top: 110%;
  left: 0;
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 220px;
  width: 220px;
  z-index: 20;
  padding: 4px 0;
  max-height: 200px;
  overflow-y: auto;
  /* 스크롤바 숨기기 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* Internet Explorer 10+ */
}
.google-style-dropdown::-webkit-scrollbar {
  display: none; /* Safari and Chrome */
}
.category-option {
  padding: 10px 16px;
  cursor: pointer;
  transition: background 0.15s;
  font-size: 14px;
  color: #202124;
  white-space: nowrap;
}
.category-option:hover {
  background: #f8f9fa;
}
.category-option.selected {
  background: #f5f0ff;
  color: #8C30F5;
  font-weight: 500;
}
</style> 