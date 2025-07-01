<script setup>
import { RouterLink, RouterView } from 'vue-router'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'
import Footer from './components/Footer.vue'
import { ref, watch, onMounted, onUnmounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAccountStore } from './stores/account'
import { useChatStore } from './stores/chat'
import Chatbot from '@/components/chatbot/Chatbot.vue'
import Alert from '@/components/common/Alert.vue'
import {check} from "@/services/accountService.js";

const router = useRouter()
const route = useRoute()
const accountStore = useAccountStore()
const chatStore = useChatStore()
const openSidebar = ref(false)
const toggleSidebar = () => {
  openSidebar.value = !openSidebar.value
}

// /go 경로인지 확인하는 computed 속성
const isRedirectRoute = computed(() => route.name === 'redirect-url')

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
      if (userRole.toUpperCase() === 'ROLE_INFLUENCER') {
        userType = 'influencer'
      } else if (userRole.toUpperCase() === 'ROLE_ADVERTISER') {
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
  // try {
  //   await check()
  // } catch (error) {
  //   console.error('계정 체크 실패:', error)
  // }
  try {
    await check()
  } catch (error) {
    console.error('계정 체크 실패:', error)

    if (error.response && error.response.status === 401) {
      console.warn('토큰 만료 또는 인증 실패 → 로그인 페이지로 이동')
      localStorage.removeItem('token') // 필요 시 토큰 제거
      router.push('/login') // 또는 window.location.href = '/login'
    }
  }
}

onMounted(() => {
  initializeAuth()
  
  // 페이지 가시성 변화 이벤트 리스너 등록
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

// 로그인 상태에 따른 전역 SSE 관리
watch(
  () => accountStore.isLoggedIn,
  (isLoggedIn) => {
    if (isLoggedIn) {
      const userId = accountStore.getUser?.userId || accountStore.getUser?.id
      
      if (userId) {
        console.log('[APP] SSE 시작:', userId)
        
        if (chatStore.isSSEConnected) {
          chatStore.stopGlobalSSE()
          setTimeout(() => chatStore.startGlobalSSE(userId), 1000)
        } else {
          chatStore.startGlobalSSE(userId)
        }
      }
    } else {
      chatStore.stopGlobalSSE()
    }
  },
  { immediate: true }
)

// 페이지 가시성 변화에 따른 SSE 재연결
const handleVisibilityChange = () => {
  if (!document.hidden && accountStore.isLoggedIn) {
    const userId = accountStore.getUser?.userId || accountStore.getUser?.id
    
    if (userId && !chatStore.isSSEConnected) {
      console.log('[APP] 페이지 활성화 - SSE 재연결:', userId)
      chatStore.startGlobalSSE(userId)
    }
  }
}

// 컴포넌트 언마운트 시 SSE 정리
onUnmounted(() => {
  chatStore.stopGlobalSSE()
  document.removeEventListener('visibilitychange', handleVisibilityChange)
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

.main-container.no-padding {
  padding: 0;
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
