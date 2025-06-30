<script setup>
import { defineProps } from 'vue'
import { useAccountStore } from '@/stores/account'
import { useRouter } from 'vue-router'

const props = defineProps({
  openSidebar: Boolean,
  toggleSidebar: Function
})

const accountStore = useAccountStore()
const router = useRouter()

const handleLogout = () => {
  accountStore.logout()
  router.push('/login')
}
</script>

<template>
  <header class="header">
    <div class="header-content">
      <div class="header-left">
        <button v-if="openSidebar" class="close-sidebar-btn" @click="toggleSidebar">☰</button>
      </div>
      <router-link to="/" class="header-title">Linki Admin</router-link>
      <div class="header-right">
        <!-- 로그인된 경우 -->
        <template v-if="accountStore.isLoggedIn">
          <span class="user-name">{{ accountStore.getUser?.adminName || '관리자' }}님</span>
          <button @click="handleLogout" class="header-button logout-btn">로그아웃</button>
        </template>
        
        <!-- 로그인되지 않은 경우 -->
        <template v-else>
          <router-link to="/login" class="header-button">로그인</router-link>
          <router-link to="/signup" class="header-button">회원가입</router-link>
        </template>
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

.header-left {
  flex: 1;
}

.header-title {
  font-size: 1.5rem;
  color: white;
  text-decoration: none;
  font-weight: bold;
}

.header-right {
  flex: 1;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16px;
}

.user-name {
  font-size: 0.9rem;
  color: #ccc;
}

.header-button {
  background: none;
  border: 1px solid white;
  color: white;
  padding: 6px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  text-decoration: none;
  transition: all 0.2s ease;
}

.header-button:hover {
  background-color: white;
  color: black;
}

.logout-btn {
  background: none;
  font-family: inherit;
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
  
  .header-title {
    font-size: 1.2rem;
  }
  
  .header-button {
    padding: 4px 12px;
    font-size: 0.8rem;
  }
  
  .user-name {
    font-size: 0.8rem;
  }
}
</style>