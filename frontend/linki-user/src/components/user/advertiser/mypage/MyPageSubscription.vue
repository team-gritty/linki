<template>
  <div class="subscription-content">
    <h1 class="content-title">구독 상태 확인</h1>
    <div class="content-box">
      <!-- 구독 상태 내용 추가 예정 -->
    </div>
  </div>
</template>

<script>
export default {
  name: 'MyPageSubscription'
}
</script>

<style scoped>
.subscription-content {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
}

.content-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 2rem;
  color: #333;
  text-align: center;
}

.content-box {
  background: #faf7ff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(124,58,237,0.06);
  padding: 32px 28px 28px 28px;
  margin-bottom: 2rem;
}

.button-purple {
  padding: 8px 24px;
  background-color: #8B5CF6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}
.button-purple:hover {
  background-color: #7C3AED;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.2);
}
</style> 