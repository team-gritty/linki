<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { chatApi } from '@/api/chat'
import DetailProposalModal from './DetailProposalModal.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

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
const sortedChatList = ref([]) // 정렬된 채팅 목록 상태 저장
const showProposalModal = ref(false)
const selectedProposal = ref(null)

// 채팅 목록 필터링
const filteredChats = computed(() => {
  // 검색어가 없을 때는 정렬된 목록 반환
  if (!searchQuery.value) return sortedChatList.value
  
  // 검색어가 있을 때는 현재 정렬된 목록에서 필터링
  const query = searchQuery.value.toLowerCase()
  return sortedChatList.value.filter(chat => 
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
  
  // 현재 시간과 메시지 시간이 같은 연도가 아니면 날짜 표시
  if (date.getFullYear() !== now.getFullYear()) {
    const formatted = date.toLocaleDateString('ko-KR', { 
      year: 'numeric',
      month: 'numeric',
      day: 'numeric'
    })
    return formatted.replace(/\.$/, '') // 마지막 점 제거
  }

  const diffMs = now - date
  const diffSec = Math.floor(diffMs / 1000)
  const diffMin = Math.floor(diffSec / 60)
  const diffHour = Math.floor(diffMin / 60)
  const diffDay = Math.floor(diffHour / 24)

  // 30일이 넘어가면 날짜로 표시
  if (diffDay > 30) {
    const formatted = date.toLocaleDateString('ko-KR', { 
      month: 'numeric',
      day: 'numeric'
    })
    return formatted.replace(/\.$/, '') // 마지막 점 제거
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

// 채팅 목록 정렬 함수
const sortChats = () => {
  sortedChatList.value = [...chatList.value].sort((a, b) => {
    // 1순위: 안 읽은 메시지가 있는 채팅방을 위로
    if (a.isNew && !b.isNew) return -1
    if (!a.isNew && b.isNew) return 1
    
    // 2순위: 같은 읽음 상태 내에서는 최신 메시지 순으로
    return new Date(b.lastMessageTime) - new Date(a.lastMessageTime)
  })
}

// 채팅방 선택
const selectChat = async (chatId) => {
  console.log('Selecting chat:', chatId)
  
  // 이전에 선택된 채팅방이 없었을 때만 정렬 수행
  if (!selectedChatId.value) {
    sortChats()
  }
  
  selectedChatId.value = chatId
  
  // 선택한 채팅방의 안 읽은 메시지 상태 업데이트
  const chatIndex = chatList.value.findIndex(chat => chat.chatId === chatId)
  if (chatIndex !== -1 && chatList.value[chatIndex].isNew) {
    chatList.value[chatIndex] = {
      ...chatList.value[chatIndex],
      isNew: false
    }
    // sortedChatList에도 동일한 업데이트 적용
    const sortedIndex = sortedChatList.value.findIndex(chat => chat.chatId === chatId)
    if (sortedIndex !== -1) {
      sortedChatList.value[sortedIndex] = {
        ...sortedChatList.value[sortedIndex],
        isNew: false
      }
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
      
      // 채팅 목록 재정렬
      sortChats()
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
    // 초기 정렬 수행
    sortChats()

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

// 채팅방에서 나갈 때 정렬 초기화
watch(selectedChatId, (newValue) => {
  if (!newValue) {
    sortChats()
  }
})

// 제안서 모달 열기
const openProposalModal = async () => {
  console.log('Selected Chat:', selectedChat.value) // 선택된 채팅방 정보 출력
  
  if (!selectedChat.value?.proposalId) {
    alert('제안서 정보가 없습니다.')
    return
  }
  
  try {
    // API 호출하여 제안서 상세 정보 가져오기
    const response = await chatApi.getProposal(selectedChat.value.proposalId)
    selectedProposal.value = response.data
    showProposalModal.value = true
  } catch (err) {
    console.error('Error loading proposal:', err)
    alert('제안서를 불러오는데 실패했습니다.')
  }
}

// 제안서 모달 닫기
const closeProposalModal = () => {
  showProposalModal.value = false
  selectedProposal.value = null
}

// 인플루언서 상세 페이지로 이동
const goToInfluencerDetail = (influencerId) => {
  router.push(`/channels/${influencerId}`)
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
              :src="getChatProfile(chat.opponentId)?.profileImage" 
              :alt="chat.opponentName"
              class="profile-image"
              @click.stop="goToInfluencerDetail(chat.opponentId)"
            >
          </div>
          <div class="chat-item-content">
            <div class="chat-item-header">
              <div class="chat-info">
                <span class="chat-name">{{ chat.opponentName }}</span>
                <span class="chat-channel-id">{{ getChatProfile(chat.opponentId)?.channelId }}</span>
              </div>
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
              @click="goToInfluencerDetail(selectedChat?.opponentId)"
            >
          </div>
          <div class="chat-user-info">
            <span class="chat-partner-name">{{ selectedChat?.opponentName }}</span>
            <span class="chat-channel-id">{{ getChatProfile(selectedChat?.opponentId)?.channelId }}</span>
          </div>
        </div>
        <div class="chat-header-actions">
          <span :class="['status-badge', `status-${selectedChat?.chatStatus}`]">
            {{ selectedChat?.chatStatus }}
          </span>
          <button class="primary-button" @click="openProposalModal">제안서 보기</button>
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

  <!-- 제안서 모달 -->
  <div v-if="showProposalModal" class="modal-backdrop" @click="closeProposalModal">
    <div @click.stop>
      <DetailProposalModal
        :proposal="selectedProposal"
        @close="closeProposalModal"
      />
    </div>
  </div>
</template>

<style>
@import '@/assets/css/detail.css';

.chat-container {
  display: flex;
  height: 600px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
}

.chat-list {
  width: 300px;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
}

.chat-items {
  flex: 1;
  overflow-y: auto;
}

.chat-item {
  display: flex;
  padding: 12px;
  position: relative;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
}

.chat-item:hover {
  background-color: #f5f5f5;
}

.chat-item.active {
  background-color: #f0f0f0;
}

.chat-profile {
  margin-right: 12px;
}

.profile-image {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.profile-image:hover {
  transform: scale(1.05);
}

.chat-item-content {
  flex: 1;
  min-width: 0;
}

.chat-item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 4px;
}

.chat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.chat-name {
  font-weight: 500;
  font-size: 14px;
}

.chat-time {
  font-size: 12px;
  color: #666;
  margin-left: 8px;
  flex-shrink: 0;
}

.chat-preview {
  font-size: 13px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-right: 24px;
}

.new-message-badge {
  position: absolute;
  right: 12px;
  bottom: 12px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #7B21E8;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f5f5;
}

.chat-messages {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  color: #818080;
  margin-right: 8px;
}

.status-대기 {
  background-color: #f5f5f5;
}

.status-협의중 {
  background-color: #e3f2fd;
}

.status-계약체결 {
  background-color: #e8f5e9;
}

.chat-channel-id {
  font-size: 12px;
  color: #8C30F5;
}

.chat-user-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
</style>


