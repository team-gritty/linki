<template>
  <div class="google-callback-container">
    <div class="loading-spinner">
      <div class="spinner"></div>
      <p>Google 로그인 처리 중...</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import httpClient from '../../../utils/httpRequest'
import { useAccountStore } from '../../../stores/account'

const router = useRouter();
const accountStore = useAccountStore();

onMounted(async () => {
  const url = new URL(window.location.href);
  const accessToken = url.searchParams.get('accessToken');
  const userId = url.searchParams.get('userId');
  const role = url.searchParams.get('role');

  if (accessToken && userId && role) {
    try {
      accountStore.setLoginInfo(accessToken, { userId, userRole: role });
      localStorage.setItem('token', accessToken);

      router.push('/home');
    } catch (error) {
      console.error('로그인 정보 저장 실패:', error);
    }
  } else {
    console.error('쿼리 파라미터 누락:', { accessToken, userId, role });
  }
});
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
  border-top: 4px solid #d6bcf7;
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