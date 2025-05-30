<script setup>
import { ref, computed, onMounted, onUnmounted, watch, defineProps } from 'vue'
const isMobile = ref(false)

const props = defineProps({
  openSidebar: Boolean,
  toggleSidebar: Function
})

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
      <span class="logo">Linki</span>
      <ul class="menu-list desktop-menu">
        <li class="menu-item">계약관리</li>
        <li class="menu-item">결제관리</li>
        <li class="menu-item">고객관리</li>
        <li class="menu-item">운영관리</li>
      </ul>
    </div>
    <div class="navbar-right" v-show="!isMobile">
      <div class="search-box">
        <input class="search-input" type="text" placeholder="검색어를 입력해주세요." />
        <button class="search-btn">🔍</button>
      </div>
    </div>
    <!-- 모바일 사이드바 오버레이 -->
    <div :class="['mobile-sidebar-overlay', { 'is-open': openSidebar }]" @click.self="toggleSidebar">
      <div :class="['mobile-sidebar', { 'is-open': openSidebar }]">
        <ul class="menu-list mobile-menu">
          <li class="menu-item">계약관리</li>
          <li class="menu-item">결제관리</li>
          <li class="menu-item">고객관리</li>
          <li class="menu-item">운영관리</li>
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
  color: #b162c0;
  font-size: 24px;
  font-weight: bold;
  margin-right: 6em;
  transition: all 0.2s;
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

.menu-item {
  font-size: 15px;
  margin-right: 0;
  padding: 0 8px;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.search-box {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 4px;
  padding: 0 8px;
  height: 32px;
}
.search-input {
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
  padding: 0 8px;
  width: 180px;
}
.search-btn {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  color: #888;
}
.user-menu {
  display: flex;
  align-items: center;
  gap: 12px;
}
.login-btn, .mypage-btn {
  background: none;
  border: none;
  color: #222;
  font-size: 14px;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: 4px;
  transition: background 0.2s;
}
.login-btn:hover, .mypage-btn:hover {
  background: #f0f0f0;
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

@media (min-width: 1200px) {
  .menu-item {
    margin-right: 4em;
  }
  .menu-item:last-child {
    margin-right: 0;
  }
}

@media (max-width: 768px) {
  .navbar {
    height: 65px;
    padding: 0 8px;
    justify-content: center;
    position: fixed;
  }
  .navbar-left {
    width: 100%;
    justify-content: flex-start;
    position: relative;
    gap: 16px;
  }
  .logo {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    margin-right: 0;
  }
  .hamburger {
    display: block;
    position: relative;
    left: 0;
    margin-left: 0;
    z-index: 2;
  }
  .menu-list.desktop-menu {
    display: none;
  }
  .navbar-right {
    display: none;
  }
  .mobile-menu {
    flex-direction: column !important;
    gap: 24px;
  }
  .mobile-sidebar-overlay.is-open .mobile-sidebar {
    left: 0;
  }
}
@media (min-width: 769px) {
  .mobile-sidebar-overlay {
    display: none !important;
  }
  .logo {
    margin-right: 4em;
  }
}

@media (min-width: 769px) and (max-width: 1200px) {
  .navbar-left {
    gap: 3em;
  }
  .logo {
    margin-right: 4em;
  }
}

@media (min-width: 1201px) and (max-width: 1600px) {
  .navbar-left {
    gap: 3.5em;
  }
  .logo {
    margin-right: 5em;
  }
}

@media (min-width: 1601px) {
  .navbar-left {
    gap: 4em;
  }
  .logo {
    margin-right: 6em;
  }
}

@media (max-width: 1400px) {
  .navbar {
    padding: 0 32px;
  }
  .menu-list {
    gap: 24px;
  }
}
@media (max-width: 1100px) {
  .navbar {
    padding: 0 16px;
  }
  .menu-list {
    gap: 12px;
  }
}
</style>