<script setup>
import { defineProps } from 'vue'
import { useRouter } from 'vue-router'
import { useAccountStore } from '../stores/account'
import axios from 'axios'

const router = useRouter()
const accountStore = useAccountStore()
const props = defineProps({
  openSidebar: Boolean,
  toggleSidebar: Function
})

const goToMyPage = () => {
  const userType = accountStore.getUserType
  
  // 사용자 타입에 따라 다른 페이지로 이동
  if (userType === 'influencer') {
    router.push('/mypage/influencer')
  } else if (userType === 'advertiser') {
    router.push('/mypage/advertiser')
  } else {
    router.push('/mypage')
  }
}

const handleLogout = () => {
  accountStore.clearAuth()
  // localStorage에서 토큰 제거
  localStorage.removeItem('token')
  // axios 헤더에서 토큰 제거
  delete axios.defaults.headers.common['Authorization']
  router.push('/login')
}

// 헤더 컴포넌트 로직
</script>

<template>
  <header class="header">
    <div class="header-content">
      <div class="header-left">
        <button v-if="openSidebar" class="close-sidebar-btn" @click="toggleSidebar">☰</button>
      </div>
      <div class="header-right">
        <!-- 로그인하지 않은 경우: 로그인/회원가입 버튼 표시 -->
        <template v-if="!accountStore.isLoggedIn">
          <router-link to="/login" class="header-button" style="margin-right: 10px;">로그인</router-link>
          <router-link to="/signup" class="header-button">회원가입</router-link>
        </template>
        
        <!-- 로그인한 경우: 로그아웃 버튼 표시 -->
        <template v-else>
          <button @click="handleLogout" class="header-button logout-button">
            로그아웃
          </button>
        </template>
      </div>
    </div>
  </header>
  <div class="sub-header">
    <div class="sub-header-content">
      <!-- 로그인한 경우에만 마이페이지 버튼 표시 -->
      <button 
        v-if="accountStore.isLoggedIn" 
        class="mypage-button"
        :class="{
          'influencer-button': accountStore.getUserType === 'influencer',
          'advertiser-button': accountStore.getUserType === 'advertiser',
          'general-button': !accountStore.getUserType || accountStore.getUserType === 'general'
        }"
        @click="goToMyPage"
      >
        <i class="fas fa-user"></i>
        <span>
          {{ 
            accountStore.getUserType === 'influencer' ? '인플루언서 마이페이지' :
            accountStore.getUserType === 'advertiser' ? '광고주 마이페이지' :
            '마이페이지'
          }}
        </span>
      </button>
    </div>
  </div>
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

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sub-header {
  position: fixed;
  top: 48px;
  left: 0;
  width: 100vw;
  height: 48px;
  background-color: white;
  border-bottom: 1px solid #eee;
  z-index: 1099;
  box-sizing: border-box;
}

.sub-header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 20px;
}

.mypage-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background: none;
  border: 1px solid #b162c0;
  color: #b162c0;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
  white-space: nowrap;
  min-width: 120px;
  justify-content: center;
  margin-right: 20px;
}

.mypage-button:hover {
  background-color: #b162c0;
  color: white;
}

.mypage-button i {
  font-size: 1rem;
}

.header-button {
  background: none;
  border: 1px solid white;
  color: white;
  padding: 4px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.header-button:hover {
  background-color: white;
  color: black;
}

.logout-button {
  background-color: #dc3545;
  border-color: #dc3545;
  color: white;
}

.logout-button:hover {
  background-color: #c82333;
  border-color: #bd2130;
  color: white;
}

/* 인플루언서 버튼 스타일 */
.influencer-button {
  border-color: #ff6b6b;
  color: #ff6b6b;
}

.influencer-button:hover {
  background-color: #ff6b6b;
  color: white;
}

/* 광고주 버튼 스타일 */
.advertiser-button {
  border-color: #4ecdc4;
  color: #4ecdc4;
}

.advertiser-button:hover {
  background-color: #4ecdc4;
  color: white;
}

/* 일반 사용자 버튼 스타일 */
.general-button {
  border-color: #b162c0;
  color: #b162c0;
}

.general-button:hover {
  background-color: #b162c0;
  color: white;
}

.close-sidebar-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  color: white;
  font-size: 1.2rem;
  width: 28px;
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
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
}

@media (min-width: 769px) {
  .close-sidebar-btn {
    display: none;
  }
}

@media (max-width: 768px) {
  .header {
    padding: 0 16px;
  }
  
  .mypage-button {
    margin-right: 16px;
    min-width: auto;
    padding: 8px 12px;
  }
  
  .mypage-button span {
    display: block;
    font-size: 0.8rem;
  }
  
  .header-button {
    padding: 4px 8px;
    font-size: 0.8rem;
  }

  .sub-header-content {
    padding: 0 16px;
  }
}
</style>