<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { chatApi } from '@/api/chat'

const props = defineProps({
  campaignId: {
    type: [String, Number],
    required: true
  }
})

const searchQuery = ref('')
const selectedChatId = ref(null)
const newMessage = ref('')
const loading = ref(false)
const error = ref(null)
const currentUserId = 'advertiser1'
const chatList = ref([])
const chatMessages = ref([])
const chatProfiles = ref([])

// 채팅 목록 필터링
const filteredChats = computed(() => {
  // 먼저 안 읽은 메시지 순으로 정렬
  const sortedChats = [...chatList.value].sort((a, b) => {
    // 안 읽은 메시지가 있는 채팅방을 위로
    if (a.isNew && !b.isNew) return -1
    if (!a.isNew && b.isNew) return 1
    // 같은 상태면 최신 메시지 순으로
    return new Date(b.lastMessageTime) - new Date(a.lastMessageTime)
  })

  if (!searchQuery.value) return sortedChats
  const query = searchQuery.value.toLowerCase()
  return sortedChats.filter(chat => 
    chat.opponentName.toLowerCase().includes(query) ||
    chat.lastMessage.toLowerCase().includes(query)
  )
})

// 선택된 채팅방
const selectedChat = computed(() => 
  chatList.value.find(chat => chat.chatId === selectedChatId.value)
)

// 선택된 채팅방의 메시지
const selectedChatMessages = computed(() => {
  if (!selectedChatId.value) return []
  console.log('Current messages:', chatMessages.value) // 디버깅용 로그 추가
  return chatMessages.value
    .filter(msg => msg.chatId === selectedChatId.value)
    .sort((a, b) => new Date(a.messageDate) - new Date(b.messageDate))
})

// 채팅 프로필 가져오기
const getChatProfile = (userId) => {
  const profile = chatProfiles.value.find(profile => profile.userId === userId)
  console.log('Getting profile for userId:', userId, 'Found:', profile) // 디버깅용 로그 추가
  return profile
}

// 시간 포맷 함수
const formatTime = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24))

  if (diffDays === 0) {
    return date.toLocaleTimeString('ko-KR', { 
      hour: '2-digit', 
      minute: '2-digit',
      hour12: true 
    })
  } else if (diffDays === 1) {
    return '어제'
  } else if (diffDays < 7) {
    return date.toLocaleDateString('ko-KR', { weekday: 'long' })
  } else {
    return date.toLocaleDateString('ko-KR', { 
      month: 'long', 
      day: 'numeric' 
    })
  }
}

// 채팅방 선택
const selectChat = async (chatId) => {
  console.log('Selecting chat:', chatId) // 디버깅용 로그 추가
  selectedChatId.value = chatId
  
  // 선택한 채팅방의 안 읽은 메시지 상태 업데이트
  const chatIndex = chatList.value.findIndex(chat => chat.chatId === chatId)
  if (chatIndex !== -1 && chatList.value[chatIndex].isNew) {
    chatList.value[chatIndex] = {
      ...chatList.value[chatIndex],
      isNew: false
    }
  }
  
  // 메시지 로드
  await loadMessages(chatId)
}

// 메시지 로드
const loadMessages = async (chatId) => {
  loading.value = true
  error.value = null
  chatMessages.value = [] // 메시지 초기화

  try {
    const response = await chatApi.getMessages(chatId)
    console.log('Loaded messages for chatId:', chatId, response.data)
    chatMessages.value = response.data
  } catch (err) {
    error.value = '메시지를 불러오는데 실패했습니다.'
    console.error('Error loading messages:', err)
  } finally {
    loading.value = false
  }
}

// 메시지 전송
const sendMessage = async () => {
  if (!newMessage.value.trim() || !selectedChatId.value) return

  const messageObj = {
    messageId: `msg${Date.now()}`,
    chatId: selectedChatId.value,
    senderId: currentUserId,
    content: newMessage.value,
    messageDate: new Date().toISOString(),
    messageRead: false
  }

  try {
    chatMessages.value.push(messageObj)
    
    // 채팅 목록의 마지막 메시지 업데이트
    const chatIndex = chatList.value.findIndex(chat => chat.chatId === selectedChatId.value)
    if (chatIndex !== -1) {
      const updatedChat = {
        ...chatList.value[chatIndex],
        lastMessage: messageObj.content,
        lastMessageTime: messageObj.messageDate,
        isNew: false
      }
      chatList.value.splice(chatIndex, 1)
      chatList.value.unshift(updatedChat)
    }

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

// 초기 데이터 로드
const loadInitialData = async () => {
  try {
    // 채팅 목록 로드
    const chatListResponse = await chatApi.getChatList()
    chatList.value = chatListResponse.data

    // 프로필 정보 로드
    const profilesResponse = await chatApi.getProfiles()
    chatProfiles.value = profilesResponse.data
  } catch (err) {
    error.value = '데이터를 불러오는데 실패했습니다.'
    console.error('Error loading initial data:', err)
  }
}

onMounted(() => {
  loadInitialData()
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
              :src="getChatProfile(chat.opponentId)?.profileImage" 
              :alt="chat.opponentName"
              class="profile-image"
            >
          </div>
          <div class="chat-item-content">
            <div class="chat-item-header">
              <span class="chat-name">{{ chat.opponentName }}</span>
              <span class="chat-time">{{ formatTime(chat.lastMessageTime) }}</span>
            </div>
            <div class="chat-preview">{{ chat.lastMessage }}</div>
          </div>
          <div v-if="chat.isNew" class="new-message-badge"></div>
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
              :src="getChatProfile(selectedChat?.opponentId)?.profileImage" 
              :alt="selectedChat?.opponentName"
              class="profile-image"
            >
          </div>
          <span class="chat-partner-name">{{ selectedChat?.opponentName }}</span>
        </div>
        <div class="chat-header-actions">
          <span :class="['status-badge', `status-${selectedChat?.chatStatus}`]">
            {{ selectedChat?.chatStatus }}
          </span>
          <button class="primary-button">제안서 보기</button>
          <button class="primary-button">계약서 보기</button>
        </div>
      </div>

      <!-- 메시지 목록 -->
      <div class="messages-container" ref="messagesContainer">
        <div v-if="loading" class="loading">메시지를 불러오는 중...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <template v-else>
          <div 
            v-for="message in selectedChatMessages" 
            :key="message.messageId"
            :class="['message', { 'my-message': message.senderId === currentUserId }]"
          >
            <div class="message-content">{{ message.content }}</div>
            <div class="message-time">{{ formatTime(message.messageDate) }}</div>
          </div>
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
        <button @click="sendMessage" class="send-button">전송</button>
      </div>
    </div>

    <!-- 채팅방 미선택 시 -->
    <div v-else class="no-chat-selected">
      채팅방을 선택해주세요
    </div>
  </div>
</template>

<style>

@import '@/assets/css/detail.css';

</style>


