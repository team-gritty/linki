<script setup>
import { RouterLink, RouterView } from 'vue-router'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'
import Footer from './components/Footer.vue'
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAccountStore } from './stores/account'
import Chatbot from '@/components/chatbot/Chatbot.vue'
import Alert from '@/components/common/Alert.vue'
import axios from 'axios'
import { check } from '@/services/accountService'

const router = useRouter()
const accountStore = useAccountStore()
const openSidebar = ref(false)

const toggleSidebar = () => {
  openSidebar.value = !openSidebar.value
}


// JWT 토큰 파싱 함수
const parseJwtToken = (token) => {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))
    return JSON.parse(jsonPayload)
  } catch (error) {
    console.error('JWT 파싱 실패:', error)
    return null
  }
}

// 토큰 만료 확인 함수
const isTokenExpired = (token) => {
  const payload = parseJwtToken(token)
  if (!payload || !payload.exp) return true
  
  const currentTime = Math.floor(Date.now() / 1000)
  return payload.exp < currentTime
}

// 앱 초기화 시 토큰 복원
const initializeAuth = () => {
  const token = localStorage.getItem('token')
  
  if (token && !isTokenExpired(token)) {
    const payload = parseJwtToken(token)
    if (payload) {
      const userRole = payload.userRole
      const userId = payload.userId
      
      // 백엔드 role을 프론트엔드 userType으로 매핑
      let userType = 'general'
      if (userRole === 'ROLE_INFLUENCER') {
        userType = 'influencer'
      } else if (userRole === 'ROLE_ADVERTISER') {
        userType = 'advertiser'
      }
      
      // Store에 로그인 정보 복원
      accountStore.setLoginInfo(token, { userId, userRole }, userType)
    }
  } else if (token) {
    // 토큰이 만료된 경우 제거
    localStorage.removeItem('token')
  }
}
// 계정 체크 함수 정의
const checkAccount = async () => {
  try {
    await check()
  } catch (error) {
    console.error('계정 체크 실패:', error)
  }
}

onMounted(() => {
  initializeAuth()

})

// 라우터 변경 감지
watch(
  () => router.currentRoute.value,
  () => {
    // 페이지 변경 시 스크롤을 맨 위로
    window.scrollTo(0, 0)
    // 로그인된 상태일 때만 계정 체크
    if (accountStore.isLoggedIn) {
      checkAccount();
    }
  }
)
</script>

<template>
  <div class="app">
    <Header :openSidebar="openSidebar" :toggleSidebar="toggleSidebar" />
    <Sidebar :openSidebar="openSidebar" :toggleSidebar="toggleSidebar" />
    <div class="main-container">
      <main class="content">
        <RouterView v-slot="{ Component }">
          <component :is="Component" :key="$route.fullPath" />
        </RouterView>
      </main>
    </div>
    <Footer />
    <Chatbot />
    <Alert />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-container {
  flex: 1;
  width: 100%;
  min-height: 0;
  padding-top: 121px; /* Header(48) + Navbar(73) */
  padding-bottom: 60px; /* Footer 높이 */
  box-sizing: border-box;
}

.content {
  width: 100%;
  min-height: calc(100vh - 48px - 73px - 60px);
  overflow-y: auto;
  background-color: #f5f5f5;
}

@media (max-width: 768px) {
  .main-container {
    padding-top: 108px; /* Header(48) + Mobile Navbar(60) */
  }

  .content {
    min-height: calc(100vh - 48px - 60px - 60px);
  }
}
</style>
