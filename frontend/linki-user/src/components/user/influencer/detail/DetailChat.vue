<template>
  <div class="chat-container">
    <!-- 채팅 메시지 영역 -->
    <div class="chat-messages">
      <!-- 채팅방 헤더 -->
      <div class="chat-header">
        <div class="chat-header-info">
          <span class="header-date">{{ new Date().toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' }) }}</span>
          <span :class="['nego-status-badge', `nego-status-${chatRoom?.negoStatus?.replace(/ /g, '-')}`]">
            {{ chatRoom?.negoStatus }}
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
          <div v-if="chatMessages.length === 0" class="no-message">메시지가 없습니다.</div>

          <template v-for="(message, index) in chatMessages" :key="message?.messageId || index">
            <!-- 날짜 구분선 -->
            <div v-if="shouldShowDateSeparator(message, index)" class="date-separator">
              <span>{{ formatDate(message?.messageDate) }}</span>
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
      <div class="message-input-container" :class="{ 'disabled': chatRoom?.chatStatus === 'PENDING' }">
        <input 
          type="text" 
          v-model="newMessage"
          @keyup.enter="sendMessage"
          :placeholder="chatRoom?.chatStatus === 'PENDING' ? '제안서 승인 후 채팅 가능합니다' : '메시지를 입력하세요...'"
          class="message-input"
          :disabled="chatRoom?.chatStatus === 'PENDING'"
        >
        <button 
          class="send-button" 
          @click="sendMessage"
          :disabled="chatRoom?.chatStatus === 'PENDING'"
        >전송</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, watch, computed} from 'vue'
import {chatApi} from '@/api/chat'
import {useRoute} from 'vue-router'
import {useAccountStore} from '@/stores/user'
import Stomp from "stompjs";

const props = defineProps({
  chatRoom: Object
})

<<<<<<< HEAD
const accountStore = useAccountStore()
const currentUserId = computed(() => {
  // 임시로 하드코딩된 사용자 ID 사용 (실제로는 로그인된 사용자 정보에서 가져와야 함)
  return 'USER0000'
})
=======
// const accountStore = useAccountStore()
// const currentUserId = computed(() => {
//   // 임시로 하드코딩된 사용자 ID 사용 (실제로는 로그인된 사용자 정보에서 가져와야 함)
//   return 'USER0000'
// })
>>>>>>> 8d6bf02a132a4e74a62cada50b000b5266771968

const newMessage = ref('')
const loading = ref(false)
const error = ref(null)
const chatMessages = ref([])
const stompClient = ref(null)
const propoalId = "PROP0000"
//const propoalId = route.params.id

// 메시지 시간 포맷 함수
const formatMessageTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleTimeString('ko-KR', { 
    hour: '2-digit',
    minute: '2-digit',
    hour12: true
  }).replace('오전 ', 'AM ').replace('오후 ', 'PM ')
}

// 날짜 포맷 함수
const formatDate = (dateString) => {
  if (!dateString) return ''
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
  if (!currentMessage?.messageDate || !chatMessages.value[index - 1]?.messageDate) return false

  const currentDate = new Date(currentMessage.messageDate).toDateString()
  const prevDate = new Date(chatMessages.value[index - 1].messageDate).toDateString()
  
  return currentDate !== prevDate
}

const connectSocket = (chatId) => {
  const socket = new WebSocket('ws://localhost:8000/v1/chat-service/ws/chat')
  stompClient.value = Stomp.over(socket)
  socket.onopen = () => {
    //const token = localStorage.getItem('accessToken')
    const token = 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJVU0VSMDAwMCIsInVzZXJSb2xlIjoiSU5GTFVFTkNFUiIsImlhdCI6MTc1MDMwOTMzMCwiZXhwIjoxNzUwMzQ1MzMwfQ.iwigD2TSxa4mWUcRERXaedI3EFJ7GGW7mtkg5I7b4ks'

    const socket = new WebSocket(`http://localhost:8000/v1/chat-service/ws/chat`)
    stompClient.value = Stomp.over(socket)

    stompClient.value.connect({
      Authorization: `${token}` // 이렇게 써야 함
    }, () => {
      stompClient.value.subscribe(`/topic/chat/${chatId}`, (msg) => {
        const message = JSON.parse(msg.body)

        // 중복 메시지 체크 (같은 내용, 같은 시간대의 메시지)
        const isDuplicate = chatMessages.value.some(existingMsg =>
            existingMsg.content === message.content &&
            existingMsg.senderId === message.senderId &&
            Math.abs(new Date(existingMsg.messageDate) - new Date(message.messageDate)) < 1000 // 1초 이내
        )

        if (!isDuplicate) {
          chatMessages.value.push(message)
          console.log('받은 메세지:', message)
        }
      }
    )
    })
  }
}

const sendMessage = () => {
  if (!newMessage.value.trim() || !stompClient.value || !props.chatRoom || !currentUserId.value) {
    if (!currentUserId.value) {
      console.warn('사용자 정보가 없습니다. 로그인 후 다시 시도해주세요.')
    }
    return
  }

  const msgObj = {
    chatId: props.chatRoom.chatId,
    senderId: currentUserId.value,
    content: newMessage.value,
    messageType: 'message',
    messageDate: new Date().toISOString()
  }

  // 웹소켓으로 메시지 전송
  stompClient.value.send(
      '/app/send/message',
      {},
      JSON.stringify(msgObj)
  )

  // 즉시 화면에 메시지 추가 (낙관적 업데이트)
  const newMessageObj = {
    messageId: Date.now().toString(), // 임시 ID
    chatId: props.chatRoom.chatId,
    senderId: currentUserId.value,
    content: newMessage.value,
    messageType: 'message',
    messageDate: new Date().toISOString()
  }

  // 반응형 배열 업데이트를 위해 새 배열 생성
  chatMessages.value = [...chatMessages.value, newMessageObj]
  // 입력창 초기화
  newMessage.value = ''
}

const loadChatInfo = async () => {
  loading.value = true
  error.value = null
  try {
    if (!props.chatRoom) {
      loading.value = true
      return
    }
    // 메시지 로드
    const messagesResponse = await chatApi.getMessages(props.chatRoom.chatId)

    // API 응답이 배열인지 확인하고 안전하게 처리
    if (Array.isArray(messagesResponse)) {
      chatMessages.value = messagesResponse.sort((a, b) => new Date(a.messageDate) - new Date(b.messageDate))
    } else if (Array.isArray(messagesResponse.data)) {
      chatMessages.value = messagesResponse.data.sort((a, b) => new Date(a.messageDate) - new Date(b.messageDate))
    } else {
      console.warn('예상치 못한 API 응답 구조:', messagesResponse)
      chatMessages.value = []
    }

    error.value = null
  } catch (err) {
    console.error('채팅 정보 로드 에러:', err)
    error.value = '채팅 정보를 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
  // 소켓 연결은 별도 try/catch로 분리
  try {
    if (props.chatRoom) {
      connectSocket(props.chatRoom.chatId)
    }
  } catch (e) {
    console.error('소켓 연결 에러:', e)
  }
}

watch(
    () => props.chatRoom,
    (newVal) => {
      if (newVal) {
        error.value = null
        loadChatInfo()
      }
    },
    {immediate: true}
)

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