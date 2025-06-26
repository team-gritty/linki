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
            <div v-if="message.messageType ===  'NOTIFICATION'" class="alarm-wrapper">
              <div class="alarm-datetime">{{ formatDate(message.messageDate) }} {{ formatMessageTime(message.messageDate) }}</div>
              <div class="alarm-message">
                {{ message.content }}
              </div>
            </div>
            <!-- 일반 메시지 -->
            <div v-else :class="['message', { 'my-message': message.senderId === currentUserId, 'unread-message': !message.messageRead && message.senderId !== currentUserId }]">
              <div class="message-content">{{ message.content }}</div>
              <div class="message-time">
                {{ formatMessageTime(message.messageDate) }}
                <span v-if="!message.messageRead && message.senderId !== currentUserId" class="unread-indicator">●</span>
              </div>
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
// SockJS global polyfill
if (typeof global === 'undefined') {
  window.global = window;
}

import {ref, onMounted, watch, computed, onUnmounted} from 'vue'
import {chatApi} from '@/api/chat'
import {useAccountStore} from '@/stores/user'
import {useChatStore} from '@/stores/chat'
import Stomp from "stompjs";
import SockJS from "sockjs-client";

const props = defineProps({
  chatRoom: Object
})

const accountStore = useAccountStore()
const chatStore = useChatStore()
const currentUserId = computed(() => {
  return accountStore.getUser?.userId || accountStore.getUser?.id || null
})

const newMessage = ref('')
const loading = ref(false)
const error = ref(null)
const chatMessages = ref([])
const stompClient = ref(null)
const isConnected = ref(false)

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
  try {
    const token = accountStore.getAccessToken || localStorage.getItem('accessToken')
    if (!token) {
      console.error('Access token not found')
      error.value = '인증 토큰을 찾을 수 없습니다. 로그인 후 다시 시도해주세요.'
      return
    }

    console.log('Connecting to SockJS with chatId:', chatId)

    // 방법 1: SockJS를 사용하여 연결 (프록시를 통해)
    const socket = new SockJS('/v1/chat-service/ws/chat')
    stompClient.value = Stomp.over(socket)

    // 연결 시 헤더에 토큰 전달
    const headers = {
      'Authorization': `Bearer ${token}`,
      'token': token
    }


    stompClient.value.connect(headers, () => {
      console.log('STOMP connection established')
      isConnected.value = true
      error.value = null

      stompClient.value.subscribe(`/topic/chat/${chatId}`, async (msg) => {
        const message = JSON.parse(msg.body)

        // 현재 사용자가 보낸 메시지는 수신하지 않음
        if (message.senderId === currentUserId.value) {
          return
        }

        // 중복 메시지 체크 (같은 내용, 같은 시간대의 메시지)
        const isDuplicate = chatMessages.value.some(existingMsg =>
            existingMsg.content === message.content &&
            existingMsg.senderId === message.senderId &&
            Math.abs(new Date(existingMsg.messageDate) - new Date(message.messageDate)) < 1000 // 1초 이내
        )

        if (!isDuplicate) {
          chatMessages.value.push(message)
          
          console.log('🔔 [WEBSOCKET-INFLUENCER] ===== 새 메시지 수신 =====')
          console.log('🔔 [WEBSOCKET-INFLUENCER] 받은 메시지:', message)
          console.log('🔔 [WEBSOCKET-INFLUENCER] 메시지 chatId:', message.chatId)
          console.log('🔔 [WEBSOCKET-INFLUENCER] 메시지 senderId:', message.senderId)
          console.log('🔔 [WEBSOCKET-INFLUENCER] 현재 사용자 ID:', currentUserId.value)
          console.log('🔔 [WEBSOCKET-INFLUENCER] 현재 채팅방(props.chatRoom.chatId):', props.chatRoom?.chatId)
          console.log('🔔 [WEBSOCKET-INFLUENCER] 현재 채팅방인가?', props.chatRoom?.chatId === message.chatId)
          console.log('🔔 [WEBSOCKET-INFLUENCER] new 값으로 설정될 값:', false) // 현재 채팅방이므로 항상 false
          
          // 전역 chat store 직접 업데이트 (드롭다운용)
          console.log('🔄 [WEBSOCKET-INFLUENCER] 전역 chat store 업데이트 시작')
          
          try {
            // chatStore 직접 사용
            chatStore.updateChatMessage(
              message.chatId, 
              message.content, 
              message.messageDate || new Date().toISOString(), 
              false // 현재 채팅방이므로 읽음 상태
            )
            chatStore.moveChatsToTop(message.chatId)
            
            console.log('✅ [WEBSOCKET-INFLUENCER] chatStore 직접 업데이트 완료')
          } catch (storeError) {
            console.error('❌ [WEBSOCKET-INFLUENCER] chatStore 업데이트 실패:', storeError)
            
            // fallback: window 함수 사용
            if (window.updateChatMessage) {
              window.updateChatMessage(
                message.chatId, 
                message.content, 
                message.messageDate || new Date().toISOString(), 
                false
              )
              window.moveChatsToTop(message.chatId)
              console.log('✅ [WEBSOCKET-INFLUENCER] window 함수로 fallback 성공')
            }
          }
          
          // 즉시 읽음 처리
          try {
            await chatApi.markMessagesAsRead(message.chatId)
            if (window.markChatAsRead) {
              window.markChatAsRead(message.chatId)
            }
          } catch (readError) {
            console.error('Failed to mark message as read:', readError)
          }
        }
      })
    }, (connectionError) => {
      console.error('STOMP connection error:', connectionError)
      isConnected.value = false
      error.value = '채팅 연결에 실패했습니다. 서버가 실행 중인지 확인해주세요.'

      // 방법 2: 네이티브 WebSocket으로 재시도
      console.log('Attempting fallback with native WebSocket...')
      connectWithNativeWebSocket(chatId, token)
    })

  } catch (error) {
    console.error('Error setting up SockJS connection:', error)
    error.value = '채팅 연결 설정 중 오류가 발생했습니다.'
  }
}

// 네이티브 WebSocket 연결 (fallback)
const connectWithNativeWebSocket = (chatId, token) => {
  try {
    console.log('Connecting with native WebSocket...')
    const wsUrl = `ws://localhost:8000/v1/chat-service/ws/chat?token=${encodeURIComponent(token)}`
    const socket = new WebSocket(wsUrl)
    stompClient.value = Stomp.over(socket)

    stompClient.value.connect({}, () => {
      console.log('Native WebSocket STOMP connection established')
      isConnected.value = true
      error.value = null

      stompClient.value.subscribe(`/topic/chat/${chatId}`, async (msg) => {
        const message = JSON.parse(msg.body)

        // 현재 사용자가 보낸 메시지는 수신하지 않음
        if (message.senderId === currentUserId.value) {
          return
        }

        const isDuplicate = chatMessages.value.some(existingMsg =>
            existingMsg.content === message.content &&
            existingMsg.senderId === message.senderId &&
            Math.abs(new Date(existingMsg.messageDate) - new Date(message.messageDate)) < 1000
        )

        if (!isDuplicate) {
          chatMessages.value.push(message)
        }
      })
    }, (error) => {
      console.error('Native WebSocket connection error:', error)
      isConnected.value = false
      error.value = '실시간 채팅 연결에 실패했습니다.'
    })
  } catch (error) {
    console.error('Error setting up native WebSocket:', error)
    isConnected.value = false
    error.value = '실시간 채팅 연결에 실패했습니다.'
  }
}

const sendMessage = async () => {
  if (!newMessage.value.trim()) {
    console.warn('Empty message cannot be sent')
    return
  }

  if (!isConnected.value) {
    console.warn('WebSocket connection not available')
    error.value = '채팅 연결이 되지 않았습니다. 잠시 후 다시 시도해주세요.'
    return
  }

  if (!stompClient.value) {
    console.warn('STOMP client not available')
    error.value = '채팅 클라이언트가 초기화되지 않았습니다.'
    return
  }

  if (!props.chatRoom) {
    console.warn('Chat room not available')
    error.value = '채팅방 정보가 없습니다.'
    return
  }

  if (!currentUserId.value) {
    console.warn('사용자 정보가 없습니다. 로그인 후 다시 시도해주세요.')
    error.value = '사용자 정보가 없습니다. 로그인 후 다시 시도해주세요.'
    return
  }

  const messageContent = newMessage.value.trim()

  try {
  // 한국 시간으로 LocalDateTime 형태로 전송
  const now = new Date()
  const koreanTime = new Date(now.getTime() + (9 * 60 * 60 * 1000))
  const messageDate = koreanTime.toISOString().slice(0, 19) // YYYY-MM-DDTHH:mm:ss
  
  const msgObj = {
    chatId: props.chatRoom.chatId,
    senderId: currentUserId.value,
    content: messageContent,
    messageType: 'message',
    messageDate: messageDate
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
    content: messageContent,
    messageType: 'message',
    messageDate: messageDate
  }

  // 반응형 배열 업데이트를 위해 새 배열 생성
  chatMessages.value = [...chatMessages.value, newMessageObj]
  // 입력창 초기화
  newMessage.value = ''

    // 에러 메시지 초기화
    error.value = null

    // 전역 chat store 직접 업데이트 (자신의 메시지는 읽음 상태)
    try {
      chatStore.updateChatMessage(
        props.chatRoom.chatId, 
        messageContent, 
        messageDate, 
        false // 자신이 보낸 메시지는 읽음 상태
      )
      chatStore.moveChatsToTop(props.chatRoom.chatId)
      console.log('✅ [SEND-INFLUENCER] chatStore 직접 업데이트 완료')
    } catch (storeError) {
      console.error('❌ [SEND-INFLUENCER] chatStore 업데이트 실패:', storeError)
      
      // fallback: window 함수 사용
      if (window.updateChatMessage) {
        window.updateChatMessage(
          props.chatRoom.chatId, 
          messageContent, 
          messageDate, 
          false
        )
        window.moveChatsToTop(props.chatRoom.chatId)
        console.log('✅ [SEND-INFLUENCER] window 함수로 fallback 성공')
      }
    }

  } catch (error) {
    console.error('Error sending message:', error)
    error.value = '메시지 전송에 실패했습니다.'
  }
}

// 메시지 읽음 처리
const markChatAsRead = async (chatId) => {
  try {
    await chatApi.markMessagesAsRead(chatId)
    console.log('Messages marked as read for chatId:', chatId)
    
    // 드롭다운 채팅 목록에도 읽음 상태 반영
    if (window.markChatAsRead) {
      window.markChatAsRead(chatId)
    }
  } catch (err) {
    console.error('Error marking messages as read:', err)
  }
}

const loadChatInfo = async () => {
  loading.value = true
  error.value = null
  try {
    if (!props.chatRoom) {
      console.warn('Chat room is not available')
      error.value = '채팅방 정보를 불러올 수 없습니다.'
      return
    }

    if (!props.chatRoom.chatId) {
      console.warn('Chat ID is missing from chat room')
      error.value = '채팅방 ID가 없습니다.'
      return
    }

    console.log('Loading chat messages for chatId:', props.chatRoom.chatId)

    // 메시지 로드
    const messagesResponse = await chatApi.getMessagesWithoutRead(props.chatRoom.chatId)

    // API 응답이 배열인지 확인하고 안전하게 처리
    let messages = []
    if (Array.isArray(messagesResponse)) {
      messages = messagesResponse
    } else if (Array.isArray(messagesResponse.data)) {
      messages = messagesResponse.data
    } else if (messagesResponse && typeof messagesResponse === 'object') {
      // 응답이 객체인 경우 data 필드 확인
      messages = messagesResponse.data || messagesResponse.messages || []
    } else {
      console.warn('예상치 못한 API 응답 구조:', messagesResponse)
      messages = []
    }

    // 메시지를 날짜순으로 정렬
    chatMessages.value = messages.sort((a, b) => new Date(a.messageDate) - new Date(b.messageDate))

    // 읽음 처리 (채팅방 입장 시)
    await markChatAsRead(props.chatRoom.chatId)

    error.value = null
  } catch (err) {
    console.error('채팅 정보 로드 에러:', err)

    // HTTP 상태 코드에 따른 구체적인 에러 메시지
    if (err.response) {
      switch (err.response.status) {
        case 403:
          error.value = '채팅방에 접근할 권한이 없습니다.'
          break
        case 404:
          error.value = '채팅방을 찾을 수 없습니다.'
          break
        case 500:
          error.value = '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'
          break
        default:
          error.value = `채팅 정보를 불러오지 못했습니다. (${err.response.status})`
      }
    } else if (err.request) {
      error.value = '서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요.'
    } else {
      error.value = '채팅 정보를 불러오지 못했습니다.'
    }
  } finally {
    loading.value = false
  }
  // 소켓 연결은 별도 try/catch로 분리
  try {
    if (props.chatRoom && props.chatRoom.chatId) {
      console.log('Attempting WebSocket connection for chatId:', props.chatRoom.chatId)
      // WebSocket 연결은 백그라운드에서 시도하고, 실패해도 메시지 조회는 계속 가능
      setTimeout(() => {
        connectSocket(props.chatRoom.chatId)
      }, 1000) // 1초 후 연결 시도
    }
  } catch (e) {
    console.error('소켓 연결 에러:', e)
    // WebSocket 연결 실패는 치명적이지 않으므로 에러 메시지 설정하지 않음
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

onUnmounted(() => {
  disconnectSocket()
})

// 소켓 연결 해제
const disconnectSocket = () => {
  if (stompClient.value && isConnected.value) {
    stompClient.value.disconnect()
    stompClient.value = null
    isConnected.value = false
    console.log('Stomp connection disconnected')
  }
}

</script>

<style>
@import '@/assets/css/detail.css';
</style> 