<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  openSidebar: Boolean,
  toggleSidebar: Function
})

// 헤더 컴포넌트 로직
</script>

<template>
  <header class="header">
    <div class="header-content">
      <div class="header-left">
        <button v-if="openSidebar" class="close-sidebar-btn" @click="toggleSidebar">☰</button>
      </div>
      <!-- <span class="header-title">Linki Admin</span> -->
      <div class="header-right">
        <button class="header-button" style="margin-right: 10px;">로그인</button>
        <button class="header-button">회원가입</button>
      </div>
    </div>
  </header>
</template>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  min-width: 100vw;
  max-width: 100vw;
  height: 48px;
  background-color: #000;
  color: white;
  display: flex;
  align-items: center;
  z-index: 1100;
  box-sizing: border-box;
  padding: 0 40px;
}

.header-content {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-title {
  font-size: 1.5rem;
  margin: 0 auto; /* 중앙 정렬 */
}

.header-button {
  background: none;
  border: 1px solid white;
  color: white;
  padding: 4px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.header-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.close-sidebar-btn {
  background: rgba(255, 255, 255, 0.1); /* 연한 흰색 배경 */
  border: 1px solid rgba(255, 255, 255, 0.3); /* 연한 흰색 테두리 */
  border-radius: 50%; /* 원형 */
  color: white; /* 흰색 X */
  font-size: 1.2rem; /* 아이콘 크기 */
  width: 28px; /* 버튼 크기 */
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 0;
  z-index: 1101;
  transition: background 0.2s, border-color 0.2s;
}

.close-sidebar-btn:hover {
  background: rgba(255, 255, 255, 0.2); /* 호버 시 배경 진하게 */
  border-color: rgba(255, 255, 255, 0.5); /* 호버 시 테두리 진하게 */
}

@media (min-width: 769px) {
  .close-sidebar-btn {
    display: none;
  }
  .header-title {
    margin: 0;
  }
}

@media (max-width: 768px) {
  .header {
    padding: 0;
  }
  .header-content {
    padding: 0 8px; /* 내부 컨텐츠의 좌우 패딩 */
  }
  .close-sidebar-btn {
    padding: 0 8px;
  }
  .header-title {
    /* 모바일 중앙 정렬은 이미 되어 있음 */
  }
}
</style>