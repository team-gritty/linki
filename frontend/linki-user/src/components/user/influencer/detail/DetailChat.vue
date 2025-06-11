<template>
  <div class="chat-container">
    <!-- 채팅 메시지 영역 -->
    <div class="chat-messages">
      <!-- 채팅방 헤더 -->
      <div class="chat-header">
        <div class="chat-header-info">
          <span class="header-date">{{ new Date().toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' }) }}</span>
          <span :class="['status-badge', `status-${chatDetails?.contractStatus}`]">
            {{ chatDetails?.contractStatus }}
          </span>
        </div>
        <div class="chat-header-actions">
        </div>
      </div>

      <!-- 메시지 목록 -->
      <div class="messages-container" ref="messagesContainer">
        <div v-if="loading" class="loading">메시지를 불러오는 중...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <template v-else>
          <template v-for="(message, index) in chatMessages" :key="message.messageId">
            <!-- 날짜 구분선 -->
            <div v-if="shouldShowDateSeparator(message, index)" class="date-separator">
              <span>{{ formatDate(message.messageDate) }}</span>
            </div>
            <!-- 알람 메시지 -->
            <div v-if="message.messageType === 'alarm'" class="alarm-wrapper">
              <div class="alarm-datetime">{{ formatDate(message.messageDate) }} {{ formatMessageTime(message.messageDate) }}</div>
              <div class="alarm-message">
                {{ message.content }}
              </div>
            </div>
            <!-- 일반 메시지 -->
            <div v-else :class="['message', { 'my-message': message.senderId === currentUserId }]">
              <div class="message-content">{{ message.content }}</div>
              <div class="message-time">{{ formatMessageTime(message.messageDate) }}</div>
            </div>
          </template>
        </template>
      </div>

      <!-- 메시지 입력 -->
      <div class="message-input-container">
        <input 
          type="text" 
          v-model="newMessage"
          @keyup.enter="sendMessage"
          placeholder="메시지를 입력하세요..."
          class="message-input"
        >
        <button class="send-button" @click="sendMessage">전송</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { chatApi } from '@/api/chat'
import { useRouter, useRoute } from 'vue-router'

const route = useRoute()

const newMessage = ref('')
const loading = ref(false)
const error = ref(null)
const currentUserId = 'inf1'
const chatInfo = ref(null)
const chatMessages = ref([])
const chatDetails = ref(null)

// 메시지 시간 포맷 함수
const formatMessageTime = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleTimeString('ko-KR', { 
    hour: '2-digit',
    minute: '2-digit',
    hour12: true
  }).replace('오전 ', 'AM ').replace('오후 ', 'PM ')
}

// 날짜 포맷 함수
const formatDate = (dateString) => {
  const date = new Date(dateString)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)

  if (date.toDateString() === today.toDateString()) {
    return '오늘'
  } else if (date.toDateString() === yesterday.toDateString()) {
    return '어제'
  } else {
    return date.toLocaleDateString('ko-KR', {
      month: 'long',
      day: 'numeric'
    }).replace('월 ', '월 ')
  }
}

// 날짜 구분선 표시 여부 확인
const shouldShowDateSeparator = (currentMessage, index) => {
  if (index === 0) return true

  const currentDate = new Date(currentMessage.messageDate).toDateString()
  const prevDate = new Date(chatMessages.value[index - 1].messageDate).toDateString()
  
  return currentDate !== prevDate
}

// 채팅방 정보 로드
const loadChatInfo = async () => {
  loading.value = true
  error.value = null
  
  try {
    // proposalId로 채팅방 찾기
    const proposalId = route.params.id
    
    // 채팅 목록 로드
    const chatListResponse = await chatApi.getChatList()
    const targetChat = chatListResponse.data.find(chat => chat.proposalId === proposalId)
    
    if (!targetChat) {
      error.value = '채팅방을 찾을 수 없습니다.'
      return
    }
    chatInfo.value = targetChat

    // 채팅 상세 정보 로드
    const detailsResponse = await chatApi.getChatDetails()
    const detail = detailsResponse.data.find(d => d.chatId === targetChat.chatId)
    chatDetails.value = detail

    // 메시지 로드
    const messagesResponse = await chatApi.getMessages(targetChat.chatId)
    chatMessages.value = messagesResponse.data
      .sort((a, b) => new Date(a.messageDate) - new Date(b.messageDate))

  } catch (err) {
    error.value = '채팅방 정보를 불러오는데 실패했습니다.'
    console.error('Error loading chat info:', err)
  } finally {
    loading.value = false
  }
}

// 메시지 전송
const sendMessage = async () => {
  if (!newMessage.value.trim() || !chatInfo.value) return

  const messageObj = {
    messageId: `msg${Date.now()}`,
    chatId: chatInfo.value.chatId,
    senderId: currentUserId,
    content: newMessage.value,
    messageDate: new Date().toISOString(),
    messageRead: false,
    messageType: 'message'
  }

  try {
    chatMessages.value.push(messageObj)
    newMessage.value = ''
    
    setTimeout(() => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }, 0)
  } catch (err) {
    error.value = '메시지 전송에 실패했습니다.'
    console.error('Error sending message:', err)
  }
}

onMounted(() => {
  loadChatInfo()
})

// 메시지 자동 스크롤
const messagesContainer = ref(null)
watch(chatMessages, () => {
  setTimeout(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  }, 0)
})



</script>

<style>
@import '@/assets/css/detail.css';
</style> 