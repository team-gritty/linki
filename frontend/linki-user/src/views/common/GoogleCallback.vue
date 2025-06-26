<template>
  <div class="google-callback-container">
    <div class="loading-spinner">
      <div class="spinner"></div>
      <p>유튜브 인증 처리 중...</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  const code = new URLSearchParams(window.location.search).get('code')
  
  if (code) {
    // 마이페이지의 채널 변경 탭으로 리다이렉트하면서 코드를 쿼리 파라미터로 전달
    router.replace({
      path: '/mypage',
      query: { 
        currentMenu: 'profile.channel',
        code: code 
      }
    })
  } else {
    // 코드가 없으면 마이페이지로 이동
    router.replace('/mypage')
  }
})
</script>

<style scoped>
.google-callback-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #fff;
}

.loading-spinner {
  text-align: center;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #7b21e8;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-spinner p {
  color: #666;
  font-size: 1.1rem;
  margin: 0;
}
</style> 