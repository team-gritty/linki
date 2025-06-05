<template>
  <!-- 검색바 전체 컨테이너 -->
  <div class="search-container">
    <div class="search-wrapper">
      <!-- 검색 옵션(이름/이메일/연락처 등) 선택 드롭다운 -->
      <select 
        v-model="selectedOption" 
        class="search-select"
        @change="handleOptionChange"
      >
        <option 
          v-for="option in config.options" 
          :key="option.value" 
          :value="option.value"
        >
          {{ option.label }}
        </option>
      </select>
      
      <!-- 검색어 입력창과 검색 버튼 -->
      <div class="search-input-wrapper">
        <input
          v-model="keyword"
          type="text"
          :placeholder="config.placeholder"
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <button class="search-button" @click="handleSearch">
          검색
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

// 부모로부터 검색 옵션, placeholder 등 config를 전달받음
const props = defineProps({
  config: {
    type: Object,
    required: true
  }
})

// 검색 이벤트를 부모로 전달
const emit = defineEmits(['search'])

// 검색어 상태
const keyword = ref('')
// 선택된 검색 옵션 상태
const selectedOption = ref(props.config.options[0].value)

// 검색 옵션이 변경될 때 실행되는 함수
const handleOptionChange = () => {
  const option = props.config.options.find(opt => opt.value === selectedOption.value)
  if (option) {
    emit('search', {
      keyword: keyword.value,
      selectedOption: selectedOption.value,
      endpoint: option.endpoint
    })
  }
}

// 검색 버튼 클릭 또는 엔터 입력 시 실행되는 함수
const handleSearch = () => {
  const option = props.config.options.find(opt => opt.value === selectedOption.value)
  if (option) {
    emit('search', {
      keyword: keyword.value,
      selectedOption: selectedOption.value,
      endpoint: option.endpoint
    })
  }
}

// config가 변경될 때 selectedOption을 첫 번째 옵션으로 초기화
watch(() => props.config, (newConfig) => {
  selectedOption.value = newConfig.options[0].value
}, { deep: true })
</script>

<style scoped>
.search-container {
  margin-bottom: 24px;
  width: auto;
}

.search-wrapper {
  display: flex;
  gap: 12px;
  align-items: center;
  width: auto;
}

.search-select {
  min-width: 120px;
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: white;
  font-size: 0.95rem;
  color: #333;
}

.search-input-wrapper {
  display: flex;
  gap: 8px;
}

.search-input {
  padding: 8px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 0.95rem;
}

.search-button {
  padding: 8px 20px;
  background-color: #503cb4;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.search-button:hover {
  background-color: #3f2f8f;
}

@media screen and (max-width: 768px) {
  .search-wrapper {
    flex-direction: column;
    width: 100%;
  }
  
  .search-select {
    width: 100%;
  }
  
  .search-input-wrapper {
    width: 100%;
  }
}
</style> 