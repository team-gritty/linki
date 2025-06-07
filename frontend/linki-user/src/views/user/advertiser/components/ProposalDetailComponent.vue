<template>
  <div class="proposal-detail-modal">
    <div class="proposal-detail-header">
      <button class="proposal-back-btn" @click="$emit('back')">← 뒤로가기</button>
      <span class="proposal-detail-title">제안서 상세</span>
      <span class="proposal-detail-status" :class="statusClass">대기 중</span>
      <button class="proposal-detail-close" @click="$emit('close')">X</button>
    </div>
    <div class="proposal-detail-body">
      <div class="proposal-detail-profile">
        <img :src="proposal.img" class="proposal-detail-img" />
        <div class="proposal-detail-meta">
          <div>이름 : {{ proposal.user }}</div>
          <div>채널 : {{ proposal.channel }}</div>
        </div>
      </div>
      <hr class="proposal-detail-divider" />
      <div class="proposal-detail-content-block">
        <div class="proposal-detail-content-title">내용</div>
        <div class="proposal-detail-content-text">{{ proposal.content }}</div>
      </div>
      <div class="proposal-detail-btns">
        <button class="proposal-detail-btn">거절</button>
        <div class="proposal-detail-accept-block">
          <button class="proposal-detail-btn">승낙</button>
          <div class="proposal-detail-accept-desc">승낙하고 채팅 시작하기</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  proposal: {
    type: Object,
    required: true
  }
})
const statusClass = {
  '대기': 'status-wait',
  '승인': 'status-approve',
  '반려': 'status-reject'
}[props.proposal.status] || 'status-wait'
</script>

<style scoped>
.proposal-detail-modal {
  max-width: 900px;
  margin: 32px auto;
  background: #fff;
  border-radius: 12px;
  border: 3px solid #2196f3;
  box-shadow: 0 2px 16px rgba(33,150,243,0.08);
  padding: 0 48px 48px 48px;
  position: relative;
}
.proposal-detail-header {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 18px;
  padding: 24px 0 0 0;
  font-size: 1.4rem;
  font-weight: 700;
}
.proposal-back-btn {
  background: none;
  border: none;
  color: #8C30F5;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  margin-right: 18px;
  padding: 0;
}
.proposal-detail-title {
  color: #222;
  font-weight: 800;
  font-size: 1.3rem;
}
.proposal-detail-status {
  background: #d1bfff;
  color: #7B21E8;
  font-size: 1rem;
  font-weight: 700;
  border-radius: 8px;
  padding: 4px 18px;
  margin-left: 12px;
}
.status-approve { background: #e0ffe0; color: #27ae60; }
.status-reject { background: #ffe0e0; color: #e74c3c; }
.status-wait { background: #d1bfff; color: #7B21E8; }
.proposal-detail-close {
  margin-left: auto;
  background: #9B51E0;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 1.1rem;
  font-weight: 700;
  width: 36px;
  height: 32px;
  cursor: pointer;
}
.proposal-detail-body {
  margin-top: 18px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.proposal-detail-profile {
  display: flex;
  align-items: center;
  gap: 32px;
  margin-bottom: 18px;
}
.proposal-detail-img {
  width: 120px;
  height: 120px;
  border-radius: 12px;
  object-fit: cover;
  border: 2px solid #eee;
}
.proposal-detail-meta {
  font-size: 1.1rem;
  color: #222;
  font-weight: 500;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.proposal-detail-divider {
  width: 100%;
  border: none;
  border-top: 2px solid #ccc;
  margin: 18px 0 24px 0;
}
.proposal-detail-content-block {
  width: 100%;
  margin-bottom: 32px;
}
.proposal-detail-content-title {
  font-size: 1.2rem;
  font-weight: 700;
  margin-bottom: 8px;
}
.proposal-detail-content-text {
  font-size: 1.1rem;
  color: #222;
  line-height: 1.7;
}
.proposal-detail-btns {
  display: flex;
  gap: 32px;
  margin-top: 32px;
  justify-content: center;
  width: 100%;
}
.proposal-detail-btn {
  background: #9B51E0;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 14px 36px;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
}
.proposal-detail-btn:hover {
  background: #7B21E8;
}
.proposal-detail-accept-block {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.proposal-detail-accept-desc {
  color: #222;
  font-size: 1rem;
  margin-top: 8px;
  text-align: center;
}
</style> 