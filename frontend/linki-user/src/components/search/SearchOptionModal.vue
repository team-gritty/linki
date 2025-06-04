<template>
  <div class="modal-backdrop" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <h2 class="modal-title">검색 옵션</h2>
      <div class="modal-tabs">
        <div class="tab active">기본</div>
        <div class="tab">채널</div>
      </div>
      <div class="modal-divider"></div>
      <div class="modal-section">
        <div class="section-title">기본</div>
        <div class="form-row">
          <div class="form-group">
            <label class="section-label">국가</label>
            <select class="form-select">
              <option>대한민국</option>
              <option>일본</option>
              <option>미국</option>
            </select>
          </div>
          <div class="form-group">
            <label class="section-label">카테고리</label>
            <div class="category-dropdown-modal" @click="toggleCategoryDropdown">
              <span class="category-label-modal">{{ selectedCategoryLabel }}</span>
              <svg class="dropdown-icon" width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4 6L8 10L12 6" stroke="#6B7280" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <div v-if="categoryDropdownOpen" class="dropdown-menu custom-category-dropdown" @click.stop>
                <button class="select-all-btn" :class="{ active: allSelected }" @click.stop="toggleSelectAll">전체 선택</button>
                <div class="category-list">
                  <label v-for="cat in categories" :key="cat" class="category-item">
                    <input type="checkbox" v-model="selectedCategories" :value="cat" />
                    <span>{{ cat }}</span>
                  </label>
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
              <label v-for="(opt, i) in subscriberOptions" :key="i"><input type="checkbox" v-model="subscriberChecks[i]" /> {{ opt }}</label>
            </div>
          </div>
        </div>
        <div class="form-row">
          <div class="form-group full-width">
            <label class="section-label">평균 조회수</label>
            <div class="view-direct-row">
              <input type="number" placeholder="직접 입력" class="direct-input" v-model="viewDirect" />
            </div>
            <div class="subscriber-options view-checkbox-row">
              <label v-for="(opt, i) in viewOptions" :key="i"><input type="checkbox" v-model="viewChecks[i]" /> {{ opt }}</label>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-btn-row">
        <button class="clear-btn" @click="clearAll">전체해제</button>
        <button class="modal-search-btn" @click="$emit('close')">검색</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, defineEmits } from 'vue'
const subscriberOptions = [
  '전체', '~1천', '1천~5천', '5천~1만', '1만~5만', '5만~10만', '10만~50만', '50만~100만', '100만+'
]
const viewOptions = [
  '전체', '~1천', '1천~1만', '1만~10만', '10만~100만', '100만+'
]
const subscriberChecks = ref([true, false, false, false, false, false, false, false, false])
const viewChecks = ref([true, false, false, false, false, false])
const subscriberMin = ref('')
const subscriberMax = ref('')
const viewDirect = ref('')
const categories = [
  '패션', '뷰티', '푸드 / 먹방', '엔터테인먼트', '여행', '스포츠', '음악', '전자기기', 'Vlog/라이프스타일', '교육', '동물/펫'
]
const selectedCategories = ref([])
const categoryDropdownOpen = ref(false)
const allSelected = computed(() => selectedCategories.value.length === categories.length)
const selectedCategoryLabel = computed(() => {
  if (selectedCategories.value.length === 0) return '카테고리'
  if (selectedCategories.value.length === categories.length) return '전체 선택됨'
  if (selectedCategories.value.length === 1) return selectedCategories.value[0]
  return `${selectedCategories.value[0]} 외 ${selectedCategories.value.length - 1}개`
})
const toggleCategoryDropdown = () => { categoryDropdownOpen.value = !categoryDropdownOpen.value }
const toggleSelectAll = () => {
  if (allSelected.value) selectedCategories.value = []
  else selectedCategories.value = [...categories]
}
function closeDropdownAndEmit() {
  categoryDropdownOpen.value = false
  emit('update:categories', [...selectedCategories.value])
}
function onDocumentClick(e) {
  if (categoryDropdownOpen.value) {
    const dropdown = document.querySelector('.custom-category-dropdown')
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
  subscriberChecks.value = subscriberChecks.value.map(() => false)
  viewChecks.value = viewChecks.value.map(() => false)
  subscriberMin.value = ''
  subscriberMax.value = ''
  viewDirect.value = ''
}
const emit = defineEmits(['update:categories'])
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
  padding: 32px 40px 32px 40px;
  min-width: 520px;
  min-height: 380px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.15);
  position: relative;
  font-size: 18px;
}
.modal-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 18px;
}
.modal-tabs {
  display: flex;
  gap: 18px;
  margin-bottom: 8px;
}
.tab {
  font-size: 1.1rem;
  font-weight: 500;
  color: #888;
  padding: 4px 18px 8px 18px;
  border-radius: 8px 8px 0 0;
  background: none;
  border: none;
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
.modal-divider {
  border-bottom: 1.5px solid #eee;
  margin-bottom: 18px;
}
.modal-section {
  margin-bottom: 18px;
}
.section-title {
  font-size: 1.1rem;
  font-weight: 700;
  margin-bottom: 16px;
}
.form-row {
  display: flex;
  gap: 32px;
  margin-bottom: 18px;
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
  gap: 18px 24px;
  margin-top: 4px;
}
.subscriber-options label {
  font-size: 1rem;
  color: #444;
  display: flex;
  align-items: center;
  gap: 4px;
}
.view-direct-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}
.view-checkbox-row {
  margin-top: 0;
}
.modal-btn-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 24px;
  margin-top: 32px;
  padding-bottom: 8px;
  position: static;
}
.clear-btn {
  background: #eee;
  color: #8C30F5;
  border: none;
  border-radius: 4px;
  padding: 10px 32px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
  font-size: 1rem;
  margin-right: auto;
  margin-top: 0;
  position: static;
}
.clear-btn:hover {
  background: #f5f0ff;
}
.modal-search-btn {
  background: #8C30F5;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 12px 48px;
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
  transition: background 0.2s;
  position: static;
}
.modal-search-btn:hover {
  background: #B18CFF;
}
.subscriber-direct-input {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-left: 18px;
}
.direct-input {
  width: 120px;
  height: 40px;
  border: 1px solid #E0E0E0;
  border-radius: 6px;
  padding: 0 12px;
  font-size: 18px;
}
.section-label {
  font-size: 1.15em;
  font-weight: 700;
  color: #222;
  margin-bottom: 8px;
  display: inline-block;
  border-bottom: 1.5px solid #E0E0E0;
  padding-bottom: 2px;
  width: auto;
}
.form-group label.section-label {
  margin-bottom: 12px;
}
.category-dropdown-modal {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 700;
  color: #23262F;
  font-size: 17px;
  cursor: pointer;
  position: relative;
  user-select: none;
  min-width: 180px;
  max-width: 180px;
  width: 180px;
  border: 1.5px solid #8C30F5;
  border-radius: 8px;
  padding: 8px 12px;
  background: #fff;
}
.category-label-modal {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
  display: inline-block;
}
.custom-category-dropdown {
  position: absolute;
  top: 110%;
  left: 0;
  background: #fff;
  border: 1.5px solid #8C30F5;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(140,48,245,0.08);
  min-width: 260px;
  width: 260px;
  z-index: 20;
  padding: 18px 0 12px 0;
}
.select-all-btn {
  width: auto;
  min-width: 80px;
  background: #F5F0FF;
  color: #8C30F5;
  border: none;
  font-weight: 700;
  font-size: 0.98em;
  padding: 4px 16px;
  border-radius: 16px;
  cursor: pointer;
  margin-bottom: 8px;
  transition: background 0.2s, color 0.2s;
  display: inline-block;
  margin-left: 28px;
}
.select-all-btn.active, .select-all-btn:hover {
  background: #8C30F5;
  color: #fff;
}
.category-list {
  max-height: 220px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 0 18px;
}
.category-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.08em;
  padding: 8px 0;
  cursor: pointer;
  border-radius: 6px;
  transition: background 0.15s;
}
.category-item:hover {
  background: #F5F0FF;
}
.category-item input[type="checkbox"] {
  accent-color: #8C30F5;
  width: 18px;
  height: 18px;
}
</style> 