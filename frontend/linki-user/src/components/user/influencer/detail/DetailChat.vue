<template>
  <div class="chat-container">
    <!-- 채팅 메시지 영역 -->
    <div class="chat-messages">
      <!-- 채팅방 헤더 -->
      <div class="chat-header">
        <div class="chat-header-info">
          <span class="header-date">{{ new Date().toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' }) }}</span>
          <span :class="['nego-status-badge', `nego-status-${getNegoStatusClass(chatRoom?.negoStatus)}`]">
            {{ getNegoStatusText(chatRoom?.negoStatus) }}
          </span>
        </div>
        <div class="chat-header-actions">
        </div>
      </div>

      <!-- 메시지 목록 -->
      <div class="messages-container" ref="messagesContainer">
        <div v-if="loading" class="loading">메시지를 불러오는 중...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <!-- 삭제된 채팅방 안내 메시지 -->
        <div v-else-if="chatRoom?.chatStatus === 'DELETE'" class="delete-notice">
          <div class="delete-icon">🚫</div>
          <div class="delete-message">
            <h3>제안서가 거절되었습니다</h3>
            <p>해당 제안서가 거절되어 더 이상 메시지를 주고받을 수 없습니다.</p>
          </div>
        </div>
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
            <div 
              v-else 
              :id="`message-${message.messageId}`"
                            :class="['message', { 
                'my-message': message.senderId === currentUserId
              }]"
            >
              <div class="message-content">{{ message.content }}</div>
              <div class="message-time">
                {{ formatMessageTime(message.messageDate) }}
              </div>
            </div>
          </template>
        </template>
      </div>

      <!-- 메시지 입력 -->
      <div class="message-input-container" :class="{ 'disabled': isMessageInputDisabled }">
        <input 
          type="text" 
          v-model="newMessage"
          @keyup.enter="sendMessage"
          :placeholder="getInputPlaceholder()"
          class="message-input"
          :disabled="isMessageInputDisabled"
        >
        <button 
          class="send-button" 
          @click="sendMessage"
          :disabled="isMessageInputDisabled"
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

import {ref, onMounted, watch, computed, onUnmounted, nextTick} from 'vue'
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

// 메시지 입력 비활성화 여부
const isMessageInputDisabled = computed(() => {
  return props.chatRoom?.chatStatus === 'PENDING' || props.chatRoom?.chatStatus === 'DELETE'
})

// 입력창 플레이스홀더 텍스트
const getInputPlaceholder = () => {
  if (props.chatRoom?.chatStatus === 'PENDING') {
    return '제안서 승인 후 채팅 가능합니다'
  } else if (props.chatRoom?.chatStatus === 'DELETE') {
    return '제안서가 거절되어 메시지를 보낼 수 없습니다'
  } else {
    return '메시지를 입력하세요...'
  }
}

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
          // 현재 채팅방이므로 메시지를 읽음 상태로 설정
          const newMessageObj = {
            ...message,
            messageRead: true // 현재 채팅방에서 수신한 메시지는 읽음 상태로 설정
          }
          chatMessages.value.push(newMessageObj)

          
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
          
          // 백그라운드에서 읽음 처리 (UI는 이미 읽음 상태로 표시됨)
          try {
            await chatApi.markMessagesAsRead(message.chatId)
            if (window.markChatAsRead) {
              window.markChatAsRead(message.chatId)
            }
          } catch (readError) {
            console.error('Failed to mark message as read:', readError)
          }
          
          // 스크롤을 최하단으로 이동 (새 메시지 수신 시)
          setTimeout(() => {
            if (messagesContainer.value) {
              messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
              console.log('✅ [WEBSOCKET-INFLUENCER] 메시지 수신 후 스크롤 완료')
            }
          }, 200)
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
          // 현재 채팅방이므로 메시지를 읽음 상태로 설정
          const newMessageObj = {
            ...message,
            messageRead: true // 현재 채팅방에서 수신한 메시지는 읽음 상태로 설정
          }
          chatMessages.value.push(newMessageObj)
          
          // 스크롤을 최하단으로 이동 (fallback WebSocket 메시지 수신 시)
          setTimeout(() => {
            if (messagesContainer.value) {
              messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
              console.log('✅ [WEBSOCKET-FALLBACK-INFLUENCER] 메시지 수신 후 스크롤 완료')
            }
          }, 200)
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

  // DELETE 상태인 경우 메시지 전송 차단
  if (props.chatRoom?.chatStatus === 'DELETE') {
    console.warn('Cannot send message to deleted chat room')
    error.value = '제안서가 거절되어 메시지를 보낼 수 없습니다.'
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
    
    // 스크롤 최하단으로 이동 (메시지 전송 시)
    setTimeout(() => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        console.log('✅ [SEND-INFLUENCER] 메시지 전송 후 스크롤 완료')
      }
    }, 200)

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

// 스마트 스크롤: 안읽은 메시지가 있으면 첫 번째 안읽은 메시지로, 없으면 맨 밑으로
const scrollToUnreadOrBottom = async () => {
  
  // Vue의 DOM 업데이트가 완료될 때까지 기다림
  await nextTick()
  
  if (!messagesContainer.value) {
    return
  }


  // 현재 사용자가 받은 안읽은 메시지 중 첫 번째 찾기
  const firstUnreadMessage = chatMessages.value.find(message => 
    !message.messageRead && 
    message.senderId !== currentUserId.value &&
    message.messageType !== 'NOTIFICATION'
  )


  // 추가적인 DOM 렌더링 대기
  setTimeout(() => {
    if (firstUnreadMessage) {
      const messageElement = document.getElementById(`message-${firstUnreadMessage.messageId}`)
      
      if (messageElement) {
        messageElement.scrollIntoView({ 
          behavior: 'smooth', 
          block: 'center' 
        })
      } else {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    } else {
      // 안읽은 메시지가 없으면 맨 밑으로 스크롤
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }
  }, 300) // 더 충분한 시간을 줌
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

    // DELETE 상태인 경우 메시지를 로드하지 않고 바로 종료
    if (props.chatRoom.chatStatus === 'DELETE') {
      console.log('Chat room is deleted, skipping message load')
      chatMessages.value = []
      loading.value = false
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

    // 스마트 스크롤: 안읽은 메시지가 있으면 첫 번째 안읽은 메시지로, 없으면 맨 밑으로
    setTimeout(async () => {
      await scrollToUnreadOrBottom()
    }, 200)

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
  // 소켓 연결은 별도 try/catch로 분리 (DELETE 상태가 아닌 경우에만)
  try {
    if (props.chatRoom && props.chatRoom.chatId && props.chatRoom.chatStatus !== 'DELETE') {
      console.log('Attempting WebSocket connection for chatId:', props.chatRoom.chatId)
      // WebSocket 연결은 백그라운드에서 시도하고, 실패해도 메시지 조회는 계속 가능
      setTimeout(() => {
        connectSocket(props.chatRoom.chatId)
      }, 1000) // 1초 후 연결 시도
    } else if (props.chatRoom.chatStatus === 'DELETE') {
      console.log('Chat room is deleted, skipping WebSocket connection')
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

// SSE 메시지 핸들러 (전역 SSE에서 메시지 받음)
const handleSSEMessage = (event) => {
  const { chatId, content, messageDate, messageType } = event.detail
  
  // 현재 채팅방의 NOTIFICATION 메시지인 경우
  if (props.chatRoom?.chatId === chatId && messageType === 'NOTIFICATION') {
    loadChatInfo() // 채팅 정보 새로고침
    
    // 현재 채팅방 보고 있으니까 메시지 읽음 처리
    chatApi.markMessagesAsRead(chatId).then(() => {
      // 드롭다운에도 읽음 상태 반영
      if (window.markChatAsRead) {
        window.markChatAsRead(chatId)
      }
    })
  }
}

// 제안서 거절 이벤트 핸들러
const handleProposalReject = (event) => {
  const { chatId, proposalId } = event.detail

  // 현재 채팅방이 거절된 경우
  if (props.chatRoom?.chatId === chatId) {
    console.log('🚫 [REJECT-INFLUENCER] 현재 채팅방이 거절됨 - UI 업데이트')
    
    // props는 직접 수정할 수 없으므로 부모 컴포넌트에 이벤트 발생
    // 대신 웹소켓 연결 해제하고 상태를 업데이트
    disconnectSocket()
    
    // 채팅 메시지 클리어
    chatMessages.value = []
    
    console.log('✅ [REJECT-INFLUENCER] 채팅방 상태 업데이트 완료')
  }
}

// 이벤트 리스너 등록
onMounted(() => {
  if (window.addEventListener) {
    window.addEventListener('sse-new-message', handleSSEMessage)
    window.addEventListener('proposal-reject', handleProposalReject)
  }
})

// 메시지 자동 스크롤 (새 메시지 수신 시에만)
const messagesContainer = ref(null)
watch(chatMessages, (newMessages, oldMessages) => {
  // 새 메시지가 추가된 경우에만 맨 밑으로 스크롤
  if (newMessages.length > oldMessages.length) {
    setTimeout(() => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }, 200)
  }
})

onUnmounted(() => {
  disconnectSocket()
  
  // SSE 이벤트 리스너 제거
  if (window.removeEventListener) {
    window.removeEventListener('sse-new-message', handleSSEMessage)
    window.removeEventListener('proposal-reject', handleProposalReject)
  }
  
  // 채팅 상태 초기화 (다른 탭으로 갔다가 다시 올 때 초기 상태로)
  chatMessages.value = []
  newMessage.value = ''
  error.value = null
  loading.value = false
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

// 영어 상태를 한글로 변환하는 함수
const getNegoStatusText = (status) => {
  const statusMap = {
    'PENDING': '제안서 대기',
    'ACCEPTED': '제안서 승인', 
    'REJECTED': '제안서 거절',
    'PENDING_SIGN': '계약 서명대기',
    'ONGOING': '계약 진행중',
    'COMPLETED': '계약 완료'
  }
  return statusMap[status] || status
}

// 영어 상태를 CSS 클래스로 변환하는 함수
const getNegoStatusClass = (status) => {
  const statusMap = {
    'PENDING': 'pending',
    'ACCEPTED': 'accepted', 
    'REJECTED': 'rejected',
    'PENDING_SIGN': 'pending-sign',
    'ONGOING': 'ongoing',
    'COMPLETED': 'completed'
  }
  return statusMap[status] || 'pending'
}

</script>

<style>
@import '@/assets/css/detail.css';

/* 삭제된 채팅방 안내 메시지 스타일 */
.delete-notice {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60%;
  text-align: center;
  padding: 40px 20px;
  color: #6c757d;
}

.delete-icon {
  font-size: 4rem;
  margin-bottom: 20px;
  opacity: 0.7;
}

.delete-message h3 {
  color: #dc3545;
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 10px;
}

.delete-message p {
  color: #6c757d;
  font-size: 1rem;
  line-height: 1.5;
  margin: 0;
}

/* 메시지 입력창 비활성화 상태 스타일 */
.message-input-container.disabled .message-input {
  background-color: #f8f9fa;
  color: #6c757d;
  cursor: not-allowed;
}

.message-input-container.disabled .send-button {
  background-color: #e9ecef;
  color: #6c757d;
  cursor: not-allowed;
}

.message-input-container.disabled .send-button:hover {
  background-color: #e9ecef;
}
</style> 