<template>
  <div class="loading-container">
    <p>이동 중...</p>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

onMounted(() => {
  // 디버깅을 위한 로그 추가
  console.log('현재 URL:', window.location.href);
  console.log('현재 경로:', route.path);
  console.log('쿼리 파라미터:', route.query);
  console.log('검색 문자열:', window.location.search);
  
  // 방법 1: URLSearchParams 사용
  const urlParams = new URLSearchParams(window.location.search);
  console.log('URLSearchParams 결과:', urlParams);
  
  // 방법 2: Vue Router의 route.query 사용
  const queryKeys = Object.keys(route.query);
  console.log('쿼리 키들:', queryKeys);
  
  // 방법 3: URL을 직접 파싱
  const url = new URL(window.location.href);
  console.log('파싱된 URL:', url);
  console.log('URL 검색 파라미터:', url.searchParams);
  
  const shortUrl = queryKeys[0];
  
  console.log('최종 추출된 shortUrl:', shortUrl);

  if (shortUrl) {
    console.log('리다이렉트 URL:', `/v1/admin/api/redirect/${shortUrl}`);
    // 백엔드로 리다이렉트 요청
    window.location.href = `/v1/admin/api/redirect/${shortUrl}`;
  } else {
    console.log('shortUrl이 없습니다.');
    alert("잘못된 링크입니다.");
  }
});
</script>

<style scoped>
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-size: 1.2rem;
  color: #666;
}
</style>