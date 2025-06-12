<script setup>
import { ref, onMounted, onUnmounted, watch, defineProps } from 'vue'

import { useRouter } from 'vue-router'
import ChatDropdown from '@/components/ChatDropdown.vue'


const router = useRouter()
const isMobile = ref(false)

const props = defineProps({
  openSidebar: Boolean,
  toggleSidebar: Function
})

const goToMyPage = () => {
  router.push('/mypage')
}
const goToHome = () => {
  router.push('/home')
}

const goTochannels = () => {
  router.push('/channels')
}

const goToCampaigns = () => {
  router.push('/campaigns')
}
function checkMobile() {
  isMobile.value = window.innerWidth <= 768
}
onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})
onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})

const sidebarElement = ref(null)



watch(() => props.openSidebar, (newValue) => {
  if (sidebarElement.value) {
    if (newValue) {
      sidebarElement.value.classList.add('is-open')
    } else {
      sidebarElement.value.classList.remove('is-open')
    }
  }
}, { immediate: true })
</script>

<template>
  <nav class="navbar">
    <div class="navbar-left">
      <button class="hamburger" @click="toggleSidebar">
        ☰
      </button>
      <router-link to="/home" class="logo">LINKI</router-link>
      <ul class="menu-list desktop-menu">

        <li class="menu-item" @click="goToHome">홈</li>
        <li class="menu-item" @click="goTochannels">인플루언서</li>
        <li class="menu-item" @click="goToCampaigns">캠페인</li>


      </ul>
    </div>
    <div class="navbar-right" v-show="!isMobile">
      <button class="mypage-button" @click="goToMyPage">
        <i class="fas fa-user"></i>
        <span>마이페이지</span>
      </button>
      <ChatDropdown />
    </div>
    <!-- 모바일 사이드바 오버레이 -->
    <div :class="['mobile-sidebar-overlay', { 'is-open': openSidebar }]" @click.self="toggleSidebar">
      <div :class="['mobile-sidebar', { 'is-open': openSidebar }]">
        <ul class="menu-list mobile-menu">
          <li class="menu-item">
            <router-link to="/home"><span>홈</span></router-link>
          </li>
          <li class="menu-item">
            <router-link to="/channels"><span>인플루언서</span></router-link>
          </li>

          <li class="menu-item" @click="goToCampaigns">캠페인</li>

          <li class="menu-item" @click="goToMyPage">마이페이지</li>

        </ul>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.navbar {
  position: fixed;
  top: 48px;
  left: 0;
  width: 100vw;
  height: 73px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #eee;
  padding: 0 60px;
  box-sizing: border-box;
  z-index: 1099;
}

body, html {
  width: 100vw;
  min-width: 100vw;
  max-width: 100vw;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
}

.navbar-left {
  display: flex;
  align-items: center;
  position: relative;
  width: 100%;
  justify-content: center;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-left: auto;
}

.logo {
  position: absolute;
  left: 60px;
  color: #7B21E8;
  font-size: 24px;
  font-weight: bold;
  text-decoration: none;
}

.logo:hover {
  opacity: 0.8;
}

.menu-list {
  display: flex;
  align-items: center;
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-list.desktop-menu {
  display: flex;
  align-items: center;
  justify-content: center;
  list-style: none;
  padding: 0;
  margin: 0 auto;
  gap: 48px;
}

.menu-item { 
  font-size: 15px;
  white-space: nowrap;
  cursor: pointer;
  transition: color 0.2s;
  padding: 8px 16px;
  text-align: center;
}

.menu-item span {
  position: relative;
  display: inline-block;
}

.menu-item span::after {
  content: '';
  position: absolute;
  width: 0;
  height: 1px;
  bottom: -4px;
  left: 0;
  background-color: #7B21E8;
  transition: width 0.2s ease;
}

.menu-item:hover span::after,
.menu-item.active span::after {
  width: 100%;
}

.menu-item:hover,
.menu-item.active {
  color: #7B21E8;
}

.menu-item a {
  text-decoration: none;
  color: inherit;
  white-space: nowrap;
}

.mypage-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background: none;
  border: 1px solid #7B21E8;
  color: #7B21E8;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
  white-space: nowrap;
  min-width: 120px;
  justify-content: center;
}

.mypage-button:hover {
  background-color: #7B21E8;
  color: white;
}

.mypage-button i {
  font-size: 1rem;
}

.hamburger {
  display: none;
  background: none;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  margin-left: 16px;
}

.mobile-sidebar-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.3);
  z-index: 2000;
  display: none;
}

.mobile-sidebar-overlay.is-open {
  display: block;
}

.mobile-sidebar {
  width: 220px;
  background: #fff;
  height: 100%;
  padding: 32px 16px 16px 16px;
  box-shadow: 2px 0 8px rgba(0,0,0,0.1);
  position: fixed;
  top: 0; bottom: 0;
  left: -220px;
  transition: left 0.3s ease-in-out;
  z-index: 2001;
  display: flex;
  flex-direction: column;
}

.mobile-sidebar.is-open {
  left: 0;
}

.mobile-menu {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-top: 40px;
}

@media (max-width: 1024px) {
  .navbar {
    padding: 0 30px;
  }

  .logo {
    left: 30px;
    font-size: 22px;
  }

  .menu-list.desktop-menu {
    gap: 32px;
  }

  .menu-item {
    padding: 8px;
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .navbar {
    padding: 0 16px;
    height: 60px;
  }

  .navbar-left {
    width: auto;
    display: flex;
    justify-content: flex-start;
    align-items: center;
    position: relative;
  }
  
  .hamburger {
    display: block;
    background: none;
    border: none;
    padding: 8px;
    font-size: 24px;
    cursor: pointer;
    margin-right: 16px;
  }

  .logo {
    position: static;
    font-size: 20px;
    margin: 0;
    padding: 0;
  }
  
  .desktop-menu {
    display: none;
  }

  .navbar-right {
    display: flex;
    gap: 8px;
  }

  .mypage-button {
    min-width: unset;
    padding: 8px;
    height: 36px;
    width: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .mypage-button span {
    display: none;
  }

  .mypage-button i {
    margin: 0;
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .navbar {
    padding: 0 12px;
  }

  .hamburger {
    margin-right: 12px;
    padding: 6px;
    font-size: 20px;
  }

  .logo {
    font-size: 18px;
  }

  .navbar-right {
    gap: 6px;
  }

  .mypage-button {
    padding: 6px;
    height: 32px;
    width: 32px;
  }

  .mypage-button i {
    font-size: 14px;
  }
}

@media (max-width: 360px) {
  .navbar {
    padding: 0 8px;
  }

  .hamburger {
    margin-right: 8px;
    padding: 4px;
    font-size: 18px;
  }

  .logo {
    font-size: 16px;
  }

  .navbar-right {
    gap: 4px;
  }

  .mypage-button {
    padding: 4px;
    height: 28px;
    width: 28px;
  }
}

@media (min-width: 769px) {
  .mobile-sidebar-overlay {
    display: none !important;
  }

  .hamburger {
    display: none;
  }
}
</style>