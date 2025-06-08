<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  campaignId: {
    type: String,
    required: true
  }
})

// 채팅 관련 상태
const messages = ref([])
const newMessage = ref('')
const loading = ref(false)
const error = ref(null)

// 채팅 메시지 불러오기
const fetchMessages = async () => {
  try {
    loading.value = true
    error.value = null
    // TODO: API 연동
    const response = await fetch(`/v1/api/chats?campaign_id=${props.campaignId}`)
    const data = await response.json()
    messages.value = data
  } catch (err) {
    error.value = '채팅 내역을 불러오는데 실패했습니다.'
    console.error('Failed to fetch messages:', err)
  } finally {
    loading.value = false
  }
}

// 새 메시지 전송
const sendMessage = async () => {
  if (!newMessage.value.trim()) return
  
  try {
    // TODO: API 연동
    await fetch('/v1/api/chats', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        campaign_id: props.campaignId,
        message: newMessage.value,
        timestamp: new Date().toISOString()
      })
    })
    
    // 메시지 전송 후 목록 새로고침
    await fetchMessages()
    newMessage.value = ''
  } catch (err) {
    error.value = '메시지 전송에 실패했습니다.'
    console.error('Failed to send message:', err)
  }
}

onMounted(() => {
  fetchMessages()
})
</script>

<template>
  <div class="chat-container">
    <h2 class="chat-title">채팅</h2>
    
    <!-- 로딩 상태 -->
    <div v-if="loading" class="chat-loading">
      메시지를 불러오는 중...
    </div>
    
    <!-- 에러 상태 -->
    <div v-else-if="error" class="chat-error">
      {{ error }}
    </div>
    
    <!-- 채팅 목록 -->
    <div v-else class="chat-messages">
      <div v-for="message in messages" :key="message.id" class="message">
        <img :src="message.sender.avatar" :alt="message.sender.name" class="message-avatar">
        <div class="message-content">
          <div class="message-header">
            <span class="message-sender">{{ message.sender.name }}</span>
            <span class="message-time">{{ new Date(message.timestamp).toLocaleString() }}</span>
          </div>
          <p class="message-text">{{ message.text }}</p>
        </div>
      </div>
    </div>
    
    <!-- 메시지 입력 -->
    <div class="chat-input">
      <input 
        v-model="newMessage"
        type="text"
        placeholder="메시지를 입력하세요..."
        @keyup.enter="sendMessage"
      >
      <button @click="sendMessage" :disabled="!newMessage.trim()">전송</button>
    </div>
  </div>
</template>

<style scoped>
.chat-container {
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.chat-title {
  font-size: 1.5rem;
  margin-bottom: 20px;
  color: #333;
}

.chat-loading, .chat-error {
  text-align: center;
  padding: 20px;
  color: #666;
}

.chat-error {
  color: #dc2626;
  background: #fee2e2;
  border-radius: 4px;
}

.chat-messages {
  height: 400px;
  overflow-y: auto;
  padding: 10px;
  background: #f9fafb;
  border-radius: 8px;
  margin-bottom: 20px;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.message-content {
  flex: 1;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.message-sender {
  font-weight: 600;
  color: #333;
}

.message-time {
  font-size: 0.8rem;
  color: #666;
}

.message-text {
  margin: 0;
  color: #444;
  line-height: 1.4;
}

.chat-input {
  display: flex;
  gap: 8px;
}

.chat-input input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.chat-input button {
  padding: 8px 16px;
  background: #7c3aed;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
}

.chat-input button:disabled {
  background: #d1d5db;
  cursor: not-allowed;
}

.chat-input button:not(:disabled):hover {
  background: #6d28d9;
}
</style>
