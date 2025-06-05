<script setup>
import { ref, computed, onMounted, onUnmounted, watch, defineProps } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
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

// ----------------------
// 데스크톱 메뉴/서브메뉴 데이터 및 상태
// ----------------------
const menus = [
  {
    title: '계약관리',
    subs: ['전체 계약관리', '제품 관리']
  },
  {
    title: '결제관리',
    subs: ['광고비 지급 관리', '정산 관리', '구독 결제 관리']
  },
  {
    title: '고객관리',
    subs: ['일반 회원 조회', '광고주 조회', '인플루언서 조회', '구독 회원 조회']
  },
  {
    title: '운영관리',
    subs: ['공지사항', 'Q&A', 'FAQ']
  }
]
const openIdx = ref(null)
function openMenu(idx) { openIdx.value = idx }
function closeMenu() { openIdx.value = null }

// 상세메뉴 클릭 시 라우터 이동
function handleMenuClick(menuTitle, subTitle) {
  if (menuTitle === '고객관리' && subTitle === '일반 회원 조회') {
    router.push('/memberList')
  } else if (menuTitle === '고객관리' && subTitle === '광고주 조회') {
    router.push('/advertiserUserList')
  } else if (menuTitle === '고객관리' && subTitle === '인플루언서 조회') {
    router.push('/influencerUserList')
  } else if (menuTitle === '고객관리' && subTitle === '구독 회원 조회') {
    router.push('/subscriberUserList')
  }
  // 모바일에서 메뉴 클릭 시 사이드바 닫기
  if (props.openSidebar) {
    props.toggleSidebar()
  }
  // 다른 메뉴들도 추후 추가 예정
}

const openMobileSubmenu = ref(null)

function toggleMobileSubmenu(idx) {
  openMobileSubmenu.value = openMobileSubmenu.value === idx ? null : idx
}

function handleLogoClick() {
  router.push('/home')
}
</script>

<template>
  <nav class="navbar">
    <div class="navbar-left">
      <button class="hamburger" @click="toggleSidebar">
        ☰
      </button>
      <span class="logo" @click="handleLogoClick" style="cursor:pointer;">Linki</span>
      <div
        class="menu-mega-wrapper"
        @mouseenter="openMenu(openIdx !== null ? openIdx : 0)"
        @mouseleave="closeMenu"
        style="position: relative;"
      >
        <ul class="menu-list desktop-menu">
          <li
            v-for="(menu, idx) in menus"
            :key="menu.title"
            class="menu-item"
            @mouseenter="openMenu(idx)"
          >
            <span :class="{ active: openIdx === idx }">{{ menu.title }}</span>
          </li>
        </ul>
        <div
          v-if="openIdx !== null"
          class="mega-menu"
        >
          <div class="mega-menu-col" v-for="menu in menus" :key="menu.title">
            <div class="mega-menu-title">{{ menu.title }}</div>
            <div
              v-for="sub in menu.subs"
              :key="sub"
              class="mega-menu-item"
              @click="handleMenuClick(menu.title, sub)"
            >{{ sub }}</div>
          </div>
        </div>
      </div>
    </div>
    <!-- <div class="navbar-right" v-show="!isMobile">
      <div class="search-box">
        <input class="search-input" type="text" placeholder="검색어를 입력해주세요." />
        <button class="search-btn">🔍</button>
      </div>
    </div> -->
    <!-- 모바일 사이드바 오버레이 -->
    <div :class="['mobile-sidebar-overlay', { 'is-open': openSidebar }]" @click.self="toggleSidebar">
      <div :class="['mobile-sidebar', { 'is-open': openSidebar }]">
        <ul class="menu-list mobile-menu">
          <li v-for="(menu, idx) in menus" :key="menu.title" class="mobile-menu-item">
            <div class="mobile-menu-title" @click="toggleMobileSubmenu(idx)">
              {{ menu.title }}
              <span class="arrow" :class="{ 'is-open': openMobileSubmenu === idx }">▼</span>
            </div>
            <ul class="mobile-submenu" v-show="openMobileSubmenu === idx">
              <li 
                v-for="sub in menu.subs" 
                :key="sub"
                class="mobile-submenu-item"
                @click="handleMenuClick(menu.title, sub)"
              >
                {{ sub }}
              </li>
            </ul>
          </li>
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
  justify-content: center;
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
  max-width: 1200px;
  margin: 0 auto;
}

.logo {
  color: #b162c0;
  font-size: 24px;
  font-weight: bold;
  margin-right: 4em;
  transition: all 0.2s;
  white-space: nowrap;
}

.menu-mega-wrapper {
  position: relative;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  height: 73px;
  margin-left: auto;
  margin-right: auto;
}

.menu-list.desktop-menu {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  height: 73px;
  margin: 0;
  z-index: 10;
  background: transparent;
  gap: 3em;
}

.menu-item {
  display: flex;
  align-items: center;
  height: 73px;
  padding: 0 24px;
  font-size: 16px;
  cursor: pointer;
  position: relative;
  margin-right: 0;
  white-space: nowrap;
}

.menu-item .active {
  border-bottom: 3px solid #222;
}

.mega-menu {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  top: 100%;
  margin-top: 0;
  background: #fff;
  padding: 32px 48px 28px 48px;
  border-radius: 18px;
  display: flex;
  flex-direction: row;
  gap: 60px;
  min-width: 900px;
  z-index: 9999;
  box-shadow: 0 8px 32px rgba(80,60,180,0.13), 0 1.5px 6px rgba(0,0,0,0.06);
  border: 1px solid #ececec;
  animation: fadeInMenu 0.18s;
}
@keyframes fadeInMenu {
  from { opacity: 0; transform: translateX(-50%) translateY(10px);}
  to   { opacity: 1; transform: translateX(-50%) translateY(0);}
}
.mega-menu-col {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 180px;
}
.mega-menu-title {
  font-size: 1.1rem;
  font-weight: 700;
  margin-bottom: 10px;
  color: #503cbc;
}
.mega-menu-item {
  font-size: 1.05rem;
  color: #222;
  font-weight: 500;
  padding: 10px 14px;
  border-radius: 8px;
  transition: background 0.18s, color 0.18s;
  cursor: pointer;
  white-space: nowrap;
}
.mega-menu-item:hover {
  background: #f4f2fa;
  color: #503cbc;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 24px;
  min-width: 200px;
  justify-content: flex-end;
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
  background: rgba(0,0,0,0.4);
  backdrop-filter: blur(4px);
  z-index: 2000;
  display: none;
  opacity: 0;
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.mobile-sidebar-overlay.is-open {
  display: block;
  opacity: 1;
}

.mobile-sidebar {
  width: 280px;
  background: #fff;
  height: 100%;
  padding: 32px 24px 16px 24px;
  box-shadow: 0 0 20px rgba(0,0,0,0.1);
  position: fixed;
  top: 0; bottom: 0;
  left: -280px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 2001;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}
.mobile-sidebar.is-open {
  left: 0;
  box-shadow: 0 0 30px rgba(0,0,0,0.15);
}

.mobile-menu {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 32px;
}

.mobile-menu-item {
  display: flex;
  flex-direction: column;
  width: 100%;
  background: #f8f9fa;
  border-radius: 12px;
  overflow: hidden;
}

.mobile-menu-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  cursor: pointer;
  transition: all 0.2s;
  background: #fff;
  border: 1px solid #e9ecef;
}

.mobile-menu-title:hover {
  background: #f4f2fa;
  color: #503cbc;
}

.arrow {
  font-size: 12px;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: #6c757d;
}

.arrow.is-open {
  transform: rotate(180deg);
  color: #503cbc;
}

.mobile-submenu {
  list-style: none;
  padding: 8px;
  margin: 0;
  background: #fff;
  border: 1px solid #e9ecef;
  border-top: none;
  border-radius: 0 0 12px 12px;
}

.mobile-submenu-item {
  padding: 12px 16px;
  font-size: 14px;
  color: #495057;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
  margin: 4px 0;
}

.mobile-submenu-item:hover {
  background: #f4f2fa;
  color: #503cbc;
  transform: translateX(4px);
}

@media (max-width: 768px) {
  .navbar {
    height: 65px;
    padding: 0 16px;
    justify-content: space-between;
    position: fixed;
  }

  .navbar-left {
    width: 100%;
    justify-content: space-between;
    position: relative;
  }

  .logo {
    position: relative;
    left: 0;
    transform: none;
    margin-right: 0;
    font-size: 20px;
  }

  .hamburger {
    display: block;
    position: relative;
    left: 0;
    margin-left: 0;
    z-index: 2;
    font-size: 24px;
    padding: 8px;
    color: #222;
  }

  .menu-mega-wrapper {
    display: none;
  }

  .mobile-sidebar-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0,0,0,0.5);
    z-index: 2000;
    display: none;
    opacity: 0;
    transition: opacity 0.3s ease-in-out;
  }

  .mobile-sidebar-overlay.is-open {
    display: block;
    opacity: 1;
  }

  .mobile-sidebar.is-open {
    left: 0;
  }
}

@media (max-width: 480px) {
  .navbar {
    padding: 0 12px;
  }

  .logo {
    font-size: 18px;
  }

  .hamburger {
    font-size: 20px;
    padding: 6px;
  }

  .mobile-sidebar {
    width: 260px;
    padding: 24px 20px 16px 20px;
  }

  .mobile-menu {
    gap: 16px;
    margin-top: 32px;
  }

  .mobile-menu .menu-item {
    padding: 10px 14px;
    font-size: 15px;
  }

  .mobile-menu-title {
    padding: 14px 18px;
    font-size: 15px;
  }

  .mobile-submenu {
    padding: 6px;
  }

  .mobile-submenu-item {
    padding: 10px 14px;
    font-size: 13px;
    margin: 3px 0;
  }
}

@media (min-width: 600px) {
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
    max-width: 1000px;
  }
  .logo {
    margin-right: 3em;
  }
  .menu-list.desktop-menu {
    gap: 2em;
  }
}

@media (min-width: 1201px) and (max-width: 1600px) {
  .navbar-left {
    max-width: 1100px;
  }
  .logo {
    margin-right: 3.5em;
  }
  .menu-list.desktop-menu {
    gap: 2.5em;
  }
}

@media (min-width: 1601px) {
  .navbar-left {
    max-width: 1200px;
  }
  .logo {
    margin-right: 4em;
  }
  .menu-list.desktop-menu {
    gap: 3em;
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