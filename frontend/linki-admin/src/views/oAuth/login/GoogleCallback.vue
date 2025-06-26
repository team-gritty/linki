<template>
  <div class="google-callback-container">
    <div class="loading-spinner">
      <div class="spinner"></div>
      <p>관리자 Google 로그인 처리 중...</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import httpClient from '../../../libs/httpRequester'
import { useAccountStore } from '../../../stores/account'

const router = useRouter();
const accountStore = useAccountStore();

onMounted(async () => {
  const url = new URL(window.location.href);
  const accessToken = url.searchParams.get('accessToken');
  const adminId = url.searchParams.get('adminId');
  const role = url.searchParams.get('role');

  if (accessToken && adminId && role) {
    try {
      accountStore.setLoginInfo(accessToken, { userId: adminId, userRole: role }, 'admin');
      localStorage.setItem('admin_token', accessToken);

      // 관리자 대시보드로 리다이렉트
      router.push('/admin/dashboard');
    } catch (error) {
      console.error('관리자 로그인 정보 저장 실패:', error);
      router.push('/login?error=login_failed');
    }
  } else {
    console.error('쿼리 파라미터 누락:', { accessToken, adminId, role });
    router.push('/login?error=invalid_callback');
  }
});
</script>

<style scoped>
.google-callback-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.loading-spinner {
  text-align: center;
  background: white;
  padding: 48px;
  border-radius: 12px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-spinner p {
  color: #1f2937;
  font-size: 1.1rem;
  margin: 0;
  font-weight: 500;
}
</style> 