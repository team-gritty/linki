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

function goToInfluencer() {
  router.push('/channels') // ChannelListPage로 이동 
  if (props.toggleSidebar) { 
    props.toggleSidebar() // 모바일 사이드바 닫기
  }
}

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
        <li class="menu-item">고객센터</li>
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
            <router-link to="/home">홈</router-link>
          </li>
          <li class="menu-item">
            <router-link to="/channels">인플루언서</router-link>
          </li>
          <li class="menu-item" @click="goToCampaigns">캠페인</li>
          <li class="menu-item">고객센터</li>
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
  gap: 4em;
  position: relative;
  width: 100%;
}

.logo {
  color: #7B21E8;
  font-size: 24px;
  font-weight: bold;
  margin-right: 6em;
  transition: all 0.2s;
  text-decoration: none;
}

.logo:hover {
  opacity: 0.8;
}

.menu-list.desktop-menu {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.menu-list {
  display: flex;
  flex-direction: row;
  gap: 40px;
  list-style: none;
  padding: 0;
  margin: 0;
}

/* 메뉴 아이템 한개당 텍스트 스타일 */
.menu-item { 
  font-size: 15px;
  margin-right: 0;
  padding: 0 8px;
  cursor: pointer;
  transition: color 0.2s;
}

.menu-item a {
  text-decoration: none;
  color: inherit;
}

.menu-item:hover a {
  color: #7B21E8;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 24px;
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

@media (max-width: 600px) {
  .menu-list { gap: 6px; }
  .menu-item { font-size: 13px; padding: 0 2px; }
  .mypage-button {
    min-width: unset;
    padding: 8px;
  }
  .mypage-button span { display: none; }
}

@media (min-width: 601px) and (max-width: 900px) {
  .menu-list { gap: 10px; }
  .menu-item { font-size: 14px; padding: 0 4px; }
}

@media (min-width: 901px) and (max-width: 1100px) {
  .menu-list { gap: 12px; }
  .menu-item { font-size: 14px; padding: 0 6px; }
}

@media (min-width: 1101px) and (max-width: 1399px) {
  .menu-list { gap: 16px; }
  .menu-item { font-size: 15px; padding: 0 8px; }
}

@media (min-width: 1400px) and (max-width: 1699px) {
  .menu-list { gap: 18px; }
  .menu-item { font-size: 15px; padding: 0 10px; }
}

@media (min-width: 1700px) {
  .menu-list { gap: 24px; }
  .menu-item { font-size: 16px; margin-right: 10em; padding: 0 16px; }
}

@media (max-width: 768px) {
  .navbar {
    padding: 0 16px;
  }
  
  .hamburger {
    display: block;
  }
  
  .desktop-menu {
    display: none;
  }
  
  .logo {
    margin-right: 0;
  }
}
</style>