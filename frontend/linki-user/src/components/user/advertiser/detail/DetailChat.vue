<script setup>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { chatApi } from '@/api/chat'
import DetailProposalModal from './DetailProposalModal.vue'
import { useRouter } from 'vue-router'
import { useAccountStore } from '@/stores/account'
import Stomp from "stompjs";
import SockJs from "sockjs-client";

const router = useRouter()
const accountStore = useAccountStore()

const props = defineProps({
  campaignId: {
    type: [String, Number],
    required: true
  },
  chatId: {
    type: String,
    default: null
  }
})

const searchQuery = ref('')
const selectedChatId = ref(null)
const newMessage = ref('')
const loading = ref(false)
const error = ref(null)
const currentUserId = computed(() => {
  return accountStore.getUser?.userId || accountStore.getUser?.id || null
})
const chatList = ref([])
const chatMessages = ref([])
const chatDetails = ref([])
const sortedChatList = ref([]) // 정렬된 채팅 목록 상태 저장
const showProposalModal = ref(false)
const selectedProposal = ref(null)
const stompClient = ref(null)
const isConnected = ref(false)


// 채팅 목록 필터링
const filteredChats = computed(() => {
  // 검색어가 없을 때는 정렬된 목록 반환
  if (!searchQuery.value) return sortedChatList.value
  
  // 검색어가 있을 때는 현재 정렬된 목록에서 필터링
  const query = searchQuery.value.toLowerCase()
  return sortedChatList.value.filter(chat => 
    chat.partnerName.toLowerCase().includes(query) ||
    chat.lastMessage.toLowerCase().includes(query)
  )
})

// 채팅 상세 정보 가져오기
const getChatDetail = (chatId) => {
  const response = chatDetails.value.find(room => room.chatId === chatId)
  return response || null
}


// 선택된 채팅방
const selectedChat = computed(() => 
  chatList.value.find(chat => chat.chatId === selectedChatId.value)
)

// 선택된 채팅방의 메시지
const selectedChatMessages = computed(() => {
  if (!selectedChatId.value || !Array.isArray(chatMessages.value)) return []
  console.log('Current messages:', chatMessages.value) // 디버깅용 로그 추가
  return chatMessages.value
    .filter(msg => msg.chatId === selectedChatId.value)
    .sort((a, b) => new Date(a.messageDate) - new Date(b.messageDate))
})

// 채팅 목록 시간 포맷 함수 (원래 버전으로 복구)
const formatTime = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  
  if (date.getFullYear() !== now.getFullYear()) {
    const formatted = date.toLocaleDateString('ko-KR', { 
      year: 'numeric',
      month: 'numeric',
      day: 'numeric'
    })
    return formatted.replace(/\.$/, '')
  }

  const diffMs = now - date
  const diffSec = Math.floor(diffMs / 1000)
  const diffMin = Math.floor(diffSec / 60)
  const diffHour = Math.floor(diffMin / 60)
  const diffDay = Math.floor(diffHour / 24)

  if (diffDay > 30) {
    const formatted = date.toLocaleDateString('ko-KR', { 
      month: 'numeric',
      day: 'numeric'
    })
    return formatted.replace(/\.$/, '')
  }

  if (diffSec < 10) {
    return 'now'
  } else if (diffMin < 60) {
    return `${diffMin}m`
  } else if (diffHour < 24) {
    return `${diffHour}H`
  } else {
    return `${diffDay}d`
  }
}

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
const shouldShowDateSeparator = (currentMessage, index, messages) => {
  if (index === 0) return true

  const currentDate = new Date(currentMessage.messageDate).toDateString()
  const prevDate = new Date(messages[index - 1].messageDate).toDateString()
  
  return currentDate !== prevDate
}

// 채팅 목록 정렬 함수
const sortChats = () => {
  sortedChatList.value = [...chatList.value].sort((a, b) => {
    // 1순위: 안 읽은 메시지가 있는 채팅방을 위로
    if (a.new && !b.new) return -1
    if (!a.new && b.new) return 1
    
    // 2순위: 같은 읽음 상태 내에서는 최신 메시지 순으로
    return new Date(b.lastMessageTime) - new Date(a.lastMessageTime)
  })
}

// 채팅 목록 로드
const loadInitialData = async () => {
  try {
    // 채팅 목록 로드 - campaignId를 사용하여 해당 캠페인의 채팅 목록을 가져옴
    const chatListResponse = await chatApi.getChatList(props.campaignId)
    chatList.value = chatListResponse.data || []
    console.log('Loaded chat list:', chatList.value)
    
    // 채팅방 상세 정보 로드
    if (chatList.value.length > 0) {
      const detailPromises = chatList.value.map(chat => 
        chatApi.getChatDetails(chat.chatId)
      )
      const detailResponses = await Promise.all(detailPromises)
      chatDetails.value = detailResponses.map(response => response.data).filter(Boolean)
      console.log('Loaded chat details:', chatDetails.value)
    }
    
    // 초기 정렬 수행
    sortChats()

    // chatId prop이 있으면 해당 채팅방을 선택합니다.
    if (props.chatId) {
      selectChat(props.chatId);
    }
  } catch (err) {
    error.value = '데이터를 불러오는데 실패했습니다.'
    console.error('Error loading initial data:', err)
  }
}


// 채팅방 선택
const selectChat = async (chatId) => {
  console.log('Selecting chat:', chatId)
  
  // 이전에 선택된 채팅방이 없었을 때만 정렬 수행
  if (!selectedChatId.value) {
    sortChats()
  }
  
  // 이전 채팅방과 다른 채팅방을 선택한 경우 소켓 재연결
  if (selectedChatId.value && selectedChatId.value !== chatId) {
    disconnectSocket()
  }
  
  selectedChatId.value = chatId
  
  try {
    // 채팅방 상세 정보 로드
    const detailResponse = await chatApi.getChatDetails(chatId)
    const chatDetail = Array.isArray(detailResponse.data) ? detailResponse.data[0] : detailResponse.data
    
    // 채팅방 상세 정보 업데이트
    const existingDetailIndex = chatDetails.value.findIndex(room => room.chatId === chatId)
    if (existingDetailIndex !== -1) {
      chatDetails.value[existingDetailIndex] = chatDetail
    } else {
      chatDetails.value.push(chatDetail)
    }
    
    // 선택한 채팅방의 안 읽은 메시지 상태 업데이트
    const chatIndex = chatList.value.findIndex(chat => chat.chatId === chatId)
    if (chatIndex !== -1 && chatList.value[chatIndex].new) {
      chatList.value[chatIndex] = {
        ...chatList.value[chatIndex],
        new: false
      }
      // sortedChatList에도 동일한 업데이트 적용
      const sortedIndex = sortedChatList.value.findIndex(chat => chat.chatId === chatId)
      if (sortedIndex !== -1) {
        sortedChatList.value[sortedIndex] = {
          ...sortedChatList.value[sortedIndex],
          new: false
        }
      }
    }
    
    // 메시지 로드
    await loadMessages(chatId)
    
    // 메시지 읽힘 처리
    try {
      await chatApi.markMessagesAsRead(chatId)
      console.log('Messages marked as read for chatId:', chatId)
    } catch (readError) {
      console.error('Failed to mark messages as read:', readError)
    }
    
    // 소켓 연결 (채팅방 상태가 PENDING이 아닌 경우에만)
    if (chatDetail?.chatStatus !== 'PENDING') {
      await connectSocket(chatId)
    }
  } catch (err) {
    error.value = '채팅방 정보를 불러오는데 실패했습니다.'
    console.error('Error loading chat details:', err)
  }
}

// 메시지 로드
const loadMessages = async (chatId) => {
  console.log('Debug loadMessages called with chatId:', chatId, typeof chatId)
  loading.value = true
  error.value = null
  chatMessages.value = [] // 메시지 초기화

  try {
    console.log('Debug: About to call chatApi.getMessages with chatId:', chatId)
    const response = await chatApi.getMessages(chatId)
    console.log('Debug loadMessages RAW response:', response)
    console.log('Debug loadMessages response details:', {
      chatId,
      response,
      responseType: typeof response,
      isArray: Array.isArray(response),
      length: response?.length,
      firstMessage: response?.[0],
      responseKeys: response ? Object.keys(response) : 'null response'
    })
    
    const messages = response || []
    chatMessages.value = messages
    
    console.log('Debug messages after setting:', {
      chatMessagesValue: chatMessages.value,
      length: chatMessages.value.length,
      firstMessage: chatMessages.value[0]
    })
    
    // 스크롤을 최하단으로 이동
    setTimeout(() => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }, 0)
  } catch (err) {
    error.value = '메시지를 불러오는데 실패했습니다.'
    console.error('Error loading messages:', err)
  } finally {
    loading.value = false
  }
}
//소켓 연결
const connectSocket = (chatId) => {
  // 이미 연결된 상태라면 기존 연결 해제
  if (stompClient.value && isConnected.value) {
    disconnectSocket()
  }

  const token = accountStore.getAccessToken || localStorage.getItem('accessToken')
  if(!token){
    console.error('Access token not found')
    error.value = '인증토큰을 찾을 수 없습니다. 로그인 후 다시 시도해주세요. '
    return
  }
  console.log('Connecting to SockJS with chatId:', chatId)
  try {
  //SockJs 연결
  const socket = new SockJs('/v1/chat-service/ws/chat')
  stompClient.value = Stomp.over(socket)

    // 연결 시 헤더에 토큰 전달
    const headers = {
      'Authorization': `Bearer ${token}`,
      'token': token
    }

    stompClient.value.connect(headers, 
      // 성공 콜백
      () => {
        console.log('Stomp connection successful')
        isConnected.value = true
        error.value = null

        // 채팅방 구독
        stompClient.value.subscribe(`/topic/chat/${chatId}`, (msg) => {
          try {
            const message = JSON.parse(msg.body)
            console.log('Received message:', message)

            // 내가 보낸 메시지는 무시
            if (message.senderId === currentUserId.value) {
              return
            }

            // 새 메시지를 채팅 메시지 목록에 추가
            chatMessages.value.push({
              messageId: message.messageId || Date.now(),
              chatId: message.chatId,
              senderId: message.senderId,
              content: message.content,
              messageDate: message.messageDate || new Date().toISOString(),
              messageRead: false,
              messageType: message.messageType || 'message'
            })

            // 채팅 목록의 마지막 메시지 업데이트
            const chatIndex = chatList.value.findIndex(chat => chat.chatId === message.chatId)
            if (chatIndex !== -1) {
              const updatedChat = {
                ...chatList.value[chatIndex],
                lastMessage: message.content,
                lastMessageTime: message.messageDate || new Date().toISOString(),
                new: true
              }
              chatList.value.splice(chatIndex, 1)
              chatList.value.unshift(updatedChat)
              
              // 채팅 목록 재정렬
              sortChats()
            }

            // 현재 선택된 채팅방이면 스크롤을 최하단으로 이동
            if (selectedChatId.value === message.chatId) {
              setTimeout(() => {
                if (messagesContainer.value) {
                  messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
                }
              }, 100)
            }
          } catch (parseError) {
            console.error('Error parsing received message:', parseError)
          }
        })

        // 연결 에러 처리
        stompClient.value.onerror = (error) => {
          console.error('Stomp connection error:', error)
          isConnected.value = false
          error.value = '소켓 연결에 실패했습니다.'
        }

        // 연결 끊김 처리
        stompClient.value.onclose = () => {
          console.log('Stomp connection closed')
          isConnected.value = false
        }
      },
      // 에러 콜백
      (error) => {
        console.error('Stomp connection failed:', error)
        isConnected.value = false
        error.value = '소켓 연결에 실패했습니다.'
      }
    )
  } catch (err) {
    console.error('Error creating socket connection:', err)
    error.value = '소켓 연결을 생성하는데 실패했습니다.'
    isConnected.value = false
  }
}

// 소켓 연결 해제
const disconnectSocket = () => {
  if (stompClient.value && isConnected.value) {
    try {
      stompClient.value.disconnect(() => {
        console.log('Stomp connection disconnected')
        isConnected.value = false
        stompClient.value = null
      })
    } catch (err) {
      console.error('Error disconnecting socket:', err)
    }
  }
}

// 메시지 전송
const sendMessage = async () => {
  if (!newMessage.value.trim() || !selectedChatId.value) return

  if (!stompClient.value || !isConnected.value) {
    error.value = '채팅에 연결되지 않았습니다. 잠시 후 다시 시도해주세요.'
    return;
  }

  const messageObj = {
    chatId: selectedChatId.value,
    senderId: currentUserId.value,
    content: newMessage.value.trim(),
    messageDate: new Date().toISOString(),
    messageType: 'message',
  }

  try {
    // 웹소켓으로 메시지 전송
    stompClient.value.send(
        '/app/send/message',
        {},
        JSON.stringify(messageObj)
    );

    // 낙관적 UI 업데이트
    chatMessages.value.push({
      ...messageObj,
      messageId: Date.now().toString(), // 임시 ID
    });
    
    // 채팅 목록의 마지막 메시지 업데이트
    const chatIndex = chatList.value.findIndex(chat => chat.chatId === selectedChatId.value)
    if (chatIndex !== -1) {
      const updatedChat = {
        ...chatList.value[chatIndex],
        lastMessage: messageObj.content,
        lastMessageTime: messageObj.messageDate,
      }
      chatList.value.splice(chatIndex, 1)
      chatList.value.unshift(updatedChat)
      sortChats()
    }

    newMessage.value = ''
    
    // 스크롤 최하단으로 이동
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
  loadInitialData()
})

// 컴포넌트 언마운트 시 소켓 연결 정리
onUnmounted(() => {
  disconnectSocket()
})

// 메시지 자동 스크롤
const messagesContainer = ref(null)
watch(selectedChatMessages, () => {
  setTimeout(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  }, 0)
})

// 채팅방에서 나갈 때 정렬 초기화
watch(selectedChatId, (newValue) => {
  if (!newValue) {
    sortChats()
  }
})

// 제안서 모달 열기
const openProposalModal = async () => {
  if (!selectedChat.value?.proposalId) {
    alert('제안서 정보가 없습니다.')
    return
  }
  
  try {
    const response = await chatApi.getProposal(selectedChat.value.proposalId)
    selectedProposal.value = response.data
    showProposalModal.value = true
  } catch (error) {
    console.error('제안서 로딩 실패:', error)
    alert('제안서를 불러오는데 실패했습니다.')
  }
}

// 제안서 모달 닫기
const closeProposalModal = () => {
  showProposalModal.value = false
  selectedProposal.value = null
}

// 계약서 보기
const openContractModal = () => {
  if (!selectedChat.value?.proposalId) {
    alert('계약서 정보가 없습니다.')
    return
  }
  // 계약서 관련 로직 추가
}

// 인플루언서 상세 페이지로 이동
const goToInfluencerDetail = (influencerId) => {
  router.push(`/channels/${influencerId}`)
}

// 계약 작성 페이지로 이동
const goToContractCreate = (proposal) => {
  router.push(`/contract/create?proposalId=${proposal.id}`)
}
</script>

<template>
  <div class="chat-container">
    <!-- 채팅 목록 -->
    <div class="chat-list">
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="채팅방 검색..."
          class="search-input"
        >
        <button 
          class="refresh-button" 
          @click="sortChats"
          title="채팅 목록 새로고침"
        >
          <i class="fas fa-sync-alt"></i>
        </button>
      </div>
      <div class="chat-items">
        <div 
          v-for="chat in filteredChats" 
          :key="chat.chatId"
          :class="['chat-item', { active: chat.chatId === selectedChatId }]"
          @click="selectChat(chat.chatId)"
        >
          <div class="chat-profile">
            <img
              :src="getChatDetail(chat.chatId)?.profileImage"
              :alt="getChatDetail(chat.chatId)?.partnerName"
              class="profile-image"
              @click.stop="goToInfluencerDetail(chat.opponentId)"
            >
          </div>
          <div class="chat-item-content">
            <div class="chat-item-header">
              <div class="chat-info">
                <span class="chat-name">{{ getChatDetail(chat.chatId)?.partnerName }}</span>
              </div>
              <span class="chat-time">{{ formatTime(chat.lastMessageTime) }}</span>
            </div>
            <div class="chat-preview-wrapper">
              <div class="chat-preview">{{ chat.lastMessage }}</div>
              <div v-if="chat.new" class="new-message-badge"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 채팅 메시지 영역 -->
    <div class="chat-messages" v-if="selectedChatId">
      <!-- 채팅방 헤더 -->
      <div class="chat-header">
        <div class="chat-header-info">
          <div class="chat-profile">
            <img
              :src="getChatDetail(selectedChat?.chatId)?.profileImage"
              :alt="getChatDetail(selectedChat?.chatId)?.partnerName"
              class="profile-image"
              @click="goToInfluencerDetail(selectedChat?.opponentId)"
            >
          </div>
          <div class="chat-user-info">
            <span class="chat-partner-name">{{ getChatDetail(selectedChat?.chatId)?.partnerName }}</span><br>
            <span class="chat-channel-id">채널명 : {{ getChatDetail(selectedChat?.chatId)?.channelName }}</span>
          </div>
        </div>
        <div class="chat-header-actions">
          <span :class="['nego-status-badge', `nego-status-${getChatDetail(selectedChat?.chatId)?.negoStatus?.replace(/ /g, '-')}`]">
            {{ getChatDetail(selectedChat?.chatId)?.negoStatus }}
          </span>
          <button class="primary-button" @click="openProposalModal">제안서 보기</button>
          <button class="primary-button" @click="openContractModal">계약서 보기</button>
        </div>
      </div>

      <!-- 메시지 목록 -->
      <div class="messages-container" ref="messagesContainer">
        <div v-if="loading" class="loading">메시지를 불러오는 중...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <template v-else>
          <!-- 디버깅 정보 표시 -->
          <div style="background: #f0f0f0; padding: 8px; margin: 4px; font-size: 12px; border-radius: 4px;">
            DEBUG: selectedChatId = {{ selectedChatId }}, 
            총 메시지 수 = {{ chatMessages.length }}, 
            필터된 메시지 수 = {{ selectedChatMessages.length }}
          </div>
          <template v-for="(message, index) in selectedChatMessages" :key="message.messageId">
            <!-- 날짜 구분선 -->
            <div v-if="shouldShowDateSeparator(message, index, selectedChatMessages)" class="date-separator">
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
      <div class="message-input-wrapper">
        <div class="message-input-container" :class="{ 'disabled': getChatDetail(selectedChat?.chatId)?.chatStatus === 'PENDING' }">
          <input 
            type="text" 
            v-model="newMessage"
            @keyup.enter="sendMessage"
            :placeholder="getChatDetail(selectedChat?.chatId)?.chatStatus === 'PENDING' ? '제안서 승인 후 채팅 가능합니다' : '메시지를 입력하세요...'"
            class="message-input"
            :disabled="getChatDetail(selectedChat?.chatId)?.chatStatus === 'PENDING'"
          >
          <button 
            @click="sendMessage" 
            class="send-button"
            :disabled="getChatDetail(selectedChat?.chatId)?.chatStatus === 'PENDING'"
          >전송</button>
        </div>
      </div>
    </div>

    <!-- 채팅방 미선택 시 -->
    <div v-else class="no-chat-selected">
      채팅방을 선택해주세요
    </div>
  </div>

  <!-- 제안서 모달 -->
  <div v-if="showProposalModal" class="modal-backdrop" @click="closeProposalModal">
    <div @click.stop>
      <DetailProposalModal
        :proposal="selectedProposal"
        @close="closeProposalModal"
        @contract="goToContractCreate"
      />
    </div>
  </div>
</template>

<style>
@import '@/assets/css/detail.css';
</style>


