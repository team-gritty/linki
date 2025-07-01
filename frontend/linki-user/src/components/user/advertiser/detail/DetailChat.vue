<script setup>
import { ref, computed, onMounted, watch, onUnmounted, nextTick } from 'vue'
import { chatApi } from '@/api/chat'
import { proposalAPI } from '@/api/advertiser/advertiser-proposal'
import { contractApi } from '@/api/advertiser/advertiser-contract'
import DetailProposalModal from './DetailProposalModal.vue'
import { useRouter, useRoute } from 'vue-router'
import { useAccountStore } from '@/stores/account'
import { useChatStore } from '@/stores/chat'
import Stomp from "stompjs";
import SockJs from "sockjs-client";

const router = useRouter()
const route = useRoute()
const accountStore = useAccountStore()
const chatStore = useChatStore()

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
const isCurrentChatRejected = ref(false) // 현재 채팅방이 거절되었는지 추적
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

// 현재 채팅방이 삭제된 상태인지 (computed로 반응성 강화)
const isCurrentChatDeleted = computed(() => {
  return isCurrentChatRejected.value || isChatDeleted(selectedChat.value?.chatId)
})

// 메시지 입력 비활성화 여부
const isMessageInputDisabled = computed(() => {
  const chatDetail = getChatDetail(selectedChat.value?.chatId)
  return chatDetail?.chatStatus === 'PENDING' || isCurrentChatDeleted.value
})

// 입력창 플레이스홀더 텍스트
const getInputPlaceholder = () => {
  const chatDetail = getChatDetail(selectedChat.value?.chatId)
  if (chatDetail?.chatStatus === 'PENDING') {
    return '제안서 승인 후 채팅 가능합니다'
  } else if (isCurrentChatDeleted.value) {
    return '제안서가 거절되어 메시지를 보낼 수 없습니다'
  } else {
    return '메시지를 입력하세요...'
  }
}

// 메시지가 클라이언트에서 읽혔는지 확인
const isMessageClientRead = (message) => {
  // 내가 보낸 메시지는 항상 읽음 상태
  if (message.senderId === currentUserId.value) {
    return true
  }
  
  // 서버에서 읽음 처리되지 않은 메시지만 클라이언트 읽음 상태 확인
  if (!message.messageRead) {
    // 현재 채팅방을 방문한 시간과 메시지 시간 비교
    try {
      const visitKey = `linki_chat_visited_${selectedChatId.value}`
      const lastVisited = localStorage.getItem(visitKey)
      if (lastVisited) {
        const lastVisitTime = new Date(lastVisited)
        const messageTime = new Date(message.messageDate)
        
        // 마지막 방문 시간 이후의 메시지면 안읽음
        return messageTime <= lastVisitTime
      }
    } catch (error) {
      console.error('방문 시간 확인 실패:', error)
    }
  }
  
  // 서버에서 읽음 처리된 메시지는 읽음 상태
  return message.messageRead
}

// 채팅방이 삭제된 상태인지 확인 (현재 채팅방 거절 상태 + 서버 상태)
const isChatDeleted = (chatId) => {
  // 현재 선택된 채팅방이 거절된 경우
  if (selectedChatId.value === chatId && isCurrentChatRejected.value) {
    return true
  }
  
  // 서버 상태 확인
  const chatDetail = getChatDetail(chatId)
  return chatDetail?.chatStatus === 'DELETE'
}

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

    // URL 쿼리에 chatId가 있으면 해당 채팅방 자동 선택 (메신저에서 온 경우)
    const chatIdFromQuery = route.query.chatId || props.chatId
    if (chatIdFromQuery) {
      // 채팅 목록에 해당 chatId가 있는지 확인
      const targetChat = chatList.value.find(chat => chat.chatId === chatIdFromQuery)
      if (targetChat) {
        console.log('Auto-selecting chat from URL query:', chatIdFromQuery)
        selectChat(chatIdFromQuery)
      }
    }
  } catch (err) {
    error.value = '데이터를 불러오는데 실패했습니다.'
    console.error('Error loading initial data:', err)
  }
}


// 채팅방 선택
const selectChat = async (chatId) => {
  console.log('Selecting chat:', chatId)
  
  // 새로운 채팅방 선택 시 거절 상태 초기화
  isCurrentChatRejected.value = false
  
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
      
      // 드롭다운 채팅 목록에도 읽음 상태 반영
      if (window.markChatAsRead) {
        window.markChatAsRead(chatId)
      }
    } catch (readError) {
      console.error('Failed to mark messages as read:', readError)
    }
    
    // 소켓 연결 (채팅방 상태가 PENDING이나 DELETE가 아닌 경우에만)
    if (chatDetail?.chatStatus !== 'PENDING' && chatDetail?.chatStatus !== 'DELETE') {
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

  // DELETE 상태인 경우 메시지를 로드하지 않고 바로 종료
  if (isCurrentChatDeleted.value) {
    loading.value = false
    return
  }

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
    
    // 스마트 스크롤: 안읽은 메시지가 있으면 첫 번째 안읽은 메시지로, 없으면 맨 밑으로
    // Vue의 nextTick을 사용하여 DOM 업데이트 완료 후 스크롤
    setTimeout(async () => {
      await scrollToUnreadOrBottom()
    }, 100)
  } catch (err) {
    error.value = '메시지를 불러오는데 실패했습니다.'
    console.error('Error loading messages:', err)
  } finally {
    loading.value = false
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
        stompClient.value.subscribe(`/topic/chat/${chatId}`, async (msg) => {
          try {
            const message = JSON.parse(msg.body)
            console.log('Received message:', message)

            // 내가 보낸 메시지는 무시
            if (message.senderId === currentUserId.value) {
              return
            }

            // 현재 채팅방의 메시지인지 확인
            const isCurrentChat = message.chatId === selectedChatId.value

            // 새 메시지를 채팅 메시지 목록에 추가 (일단 안읽음으로 설정)
            const newMessageObj = {
              messageId: message.messageId || Date.now(),
              chatId: message.chatId,
              senderId: message.senderId,
              content: message.content,
              messageDate: message.messageDate || new Date().toISOString(),
              messageRead: false, // 일단 안읽음 상태로 설정
              messageType: message.messageType || 'message'
            }
            chatMessages.value.push(newMessageObj)

            // 채팅 목록의 마지막 메시지 업데이트
            const chatIndex = chatList.value.findIndex(chat => chat.chatId === message.chatId)
            if (chatIndex !== -1) {
              const updatedChat = {
                ...chatList.value[chatIndex],
                lastMessage: message.content,
                lastMessageTime: message.messageDate || new Date().toISOString(),
                new: !isCurrentChat // 현재 채팅방이 아닌 경우에만 new = true
              }
              chatList.value.splice(chatIndex, 1)
              chatList.value.unshift(updatedChat)
              
              // 채팅 목록 재정렬
              sortChats()
              
              // 전역 chat store 직접 업데이트 (드롭다운용)
              console.log('🔄 [WEBSOCKET] 전역 chat store 업데이트 시작')
              console.log('🔄 [WEBSOCKET] 업데이트 정보:', {
                chatId: message.chatId,
                content: message.content,
                messageDate: message.messageDate,
                isNew: !isCurrentChat
              })
              
              try {
                // chatStore 직접 사용
                chatStore.updateChatMessage(
                  message.chatId, 
                  message.content, 
                  message.messageDate || new Date().toISOString(), 
                  !isCurrentChat // 현재 채팅방이 아닌 경우에만 new = true
                )
                chatStore.moveChatsToTop(message.chatId)
                
                console.log('[WEBSOCKET] chatStore 직접 업데이트 완료')
              } catch (storeError) {
                console.error('[WEBSOCKET] chatStore 업데이트 실패:', storeError)
                
                // fallback: window 함수 사용
                if (window.updateChatMessage) {
                  window.updateChatMessage(
                    message.chatId, 
                    message.content, 
                    message.messageDate || new Date().toISOString(), 
                    !isCurrentChat
                  )
                  window.moveChatsToTop(message.chatId)
                  console.log('[WEBSOCKET] window 함수로 fallback 성공')
                } else {
                  console.warn('[WEBSOCKET] window.updateChatMessage 함수도 없음')
                }
              }
            }

            // 현재 선택된 채팅방이면 스크롤 이동 후 지연된 읽음 처리
            if (isCurrentChat) {
              // 먼저 스크롤을 최하단으로 이동
              setTimeout(() => {
                if (messagesContainer.value) {
                  messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
                }
              }, 200)
              
              // 1.5초 후 읽음 처리 (사용자가 메시지를 확인할 시간 제공)
              setTimeout(async () => {
                // 여전히 같은 채팅방을 보고 있는 경우에만 읽음 처리
                if (selectedChatId.value === message.chatId) {
                  try {
                    await chatApi.markMessagesAsRead(message.chatId)
                    if (window.markChatAsRead) {
                      window.markChatAsRead(message.chatId)
                    }
                    
                    // 클라이언트 방문 시간 업데이트 (실시간 메시지 확인)
                    const now = new Date().toISOString()
                    localStorage.setItem(`linki_chat_visited_${message.chatId}`, now)
                    
                    // 해당 메시지의 읽음 상태 업데이트
                    const messageIndex = chatMessages.value.findIndex(m => m.messageId === newMessageObj.messageId)
                    if (messageIndex !== -1) {
                      chatMessages.value[messageIndex].messageRead = true
                    }
                    
                  } catch (readError) {
                    console.error('❌ [AUTO READ] 자동 읽음 처리 실패:', readError)
                  }
                } else {
                  console.log('ℹ️ [AUTO READ] 다른 채팅방으로 이동하여 자동 읽음 처리 생략')
                }
              }, 1500)
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
  console.log('🔌 [DISCONNECT] 웹소켓 연결 해제 시작')
  console.log('🔌 [DISCONNECT] 현재 상태:', {
    hasStompClient: !!stompClient.value,
    isConnected: isConnected.value,
    selectedChatId: selectedChatId.value
  })
  
  if (stompClient.value && isConnected.value) {
    stompClient.value.disconnect()
    stompClient.value = null
    isConnected.value = false
    console.log('✅ [DISCONNECT] 웹소켓 연결 해제 완료')
  } else {
    console.log('ℹ️ [DISCONNECT] 해제할 웹소켓 연결 없음')
  }
}


// 메시지 전송
const sendMessage = async () => {
  if (!newMessage.value.trim() || !selectedChatId.value) return

  // DELETE 상태인 경우 메시지 전송 차단
  if (isCurrentChatDeleted.value) {
    error.value = '제안서가 거절되어 메시지를 보낼 수 없습니다.'
    return
  }

  if (!stompClient.value || !isConnected.value) {
    error.value = '채팅에 연결되지 않았습니다. 잠시 후 다시 시도해주세요.'
    return;
  }

  // 한국 시간으로 LocalDateTime 형태로 전송
  const now = new Date()
  const koreanTime = new Date(now.getTime() + (9 * 60 * 60 * 1000))
  const messageDate = koreanTime.toISOString().slice(0, 19) // YYYY-MM-DDTHH:mm:ss
  
  const messageObj = {
    chatId: selectedChatId.value,
    senderId: currentUserId.value,
    content: newMessage.value.trim(),
    messageDate: messageDate,
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
      messageDate: messageDate,
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

          // 드롭다운 채팅 목록도 실시간 업데이트 (자신의 메시지는 읽음 상태)
      if (window.updateChatMessage) {
        window.updateChatMessage(
          selectedChatId.value, 
          messageObj.content, 
          messageDate, 
          false // 자신이 보낸 메시지는 읽음 상태
        )
        window.moveChatsToTop(selectedChatId.value)
      }

    newMessage.value = ''
    
    // 스크롤 최하단으로 이동
    setTimeout(() => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        console.log('메시지 전송 후 스크롤 완료')
      }
    }, 200)
  } catch (err) {
    error.value = '메시지 전송에 실패했습니다.'
    console.error('Error sending message:', err)
  }
}

// 탭 전환 감지를 위한 라우트 감시
watch(() => route.query.tab, (newTab, oldTab) => {
  console.log('🔄 [TAB CHANGE] 탭 변경 감지:', oldTab, '->', newTab)
  
  // 채팅 탭에서 다른 탭으로 이동할 때
  if (oldTab === 'chat' && newTab !== 'chat') {
    console.log('🔌 [TAB CHANGE] 채팅 탭에서 나감 - 웹소켓 해제 및 상태 초기화')
    
    // 웹소켓 연결 해제
    disconnectSocket()
    
    // 채팅방 선택 상태 초기화
    selectedChatId.value = null
    chatMessages.value = []
    newMessage.value = ''
    error.value = null
    isCurrentChatRejected.value = false
    
    console.log('✅ [TAB CHANGE] 채팅 상태 초기화 완료')
  }
  
  // 다른 탭에서 채팅 탭으로 이동할 때
  if (oldTab !== 'chat' && newTab === 'chat') {
    console.log('🔌 [TAB CHANGE] 채팅 탭으로 진입 - 데이터 재로드')
    
    // 채팅 데이터 재로드
    loadInitialData()
  }
})

onMounted(() => {
  console.log('🚀 [MOUNT] DetailChat 컴포넌트 마운트')
  loadInitialData()
  
  // 전역 SSE에서 알림 메시지 수신 (채팅방에서도 실시간 업데이트)
  if (window.addEventListener) {
    window.addEventListener('sse-new-message', handleSSEMessage)
    window.addEventListener('proposal-reject', handleProposalReject)
  }
})

// SSE 메시지 핸들러 (전역 SSE에서 메시지 받음)
const handleSSEMessage = (event) => {
  const { chatId, content, messageDate, messageType } = event.detail
  
  // 현재 선택된 채팅방의 NOTIFICATION 메시지인 경우
  if (selectedChatId.value === chatId && messageType === 'NOTIFICATION') {
    // 채팅방 상태가 DELETE가 아닌 경우에만 메시지 새로고침
    if (!isCurrentChatDeleted.value) {
      loadMessages(chatId) // 메시지 목록 새로고침
      
      // 현재 채팅방 보고 있으니까 메시지 읽음 처리
      chatApi.markMessagesAsRead(chatId).then(() => {
        // 드롭다운에도 읽음 상태 반영
        if (window.markChatAsRead) {
          window.markChatAsRead(chatId)
        }
      })
    }
  }
}

// 제안서 거절 이벤트 핸들러
const handleProposalReject = (event) => {
  const { chatId, proposalId } = event.detail
  
  console.log('🚫 [REJECT] 제안서 거절 이벤트 수신:', { chatId, proposalId })
  
  // 현재 선택된 채팅방이 거절된 경우
  if (selectedChatId.value === chatId) {
    console.log('🚫 [REJECT] 현재 채팅방이 거절됨 - 즉시 UI 업데이트')
    
    // 웹소켓 연결 즉시 해제
    disconnectSocket()
    
    // 현재 채팅방 거절 상태로 설정
    isCurrentChatRejected.value = true
    
    console.log('✅ [REJECT] 채팅방 즉시 거절 상태 설정 완료:', chatId)
  }
}

// 컴포넌트 언마운트 시 소켓 연결 정리 및 상태 초기화
onUnmounted(() => {
  console.log('🔥 [UNMOUNT] DetailChat 컴포넌트 언마운트 시작')
  
  disconnectSocket()
  
  // SSE 이벤트 리스너 제거
  if (window.removeEventListener) {
    window.removeEventListener('sse-new-message', handleSSEMessage)
    window.removeEventListener('proposal-reject', handleProposalReject)
  }
  
  // 채팅방 선택 상태 초기화 (다른 탭으로 갔다가 다시 올 때 선택 화면 표시)
  selectedChatId.value = null
  chatMessages.value = []
  newMessage.value = ''
  error.value = null
  isCurrentChatRejected.value = false // 거절 상태 초기화
  
  console.log('✅ [UNMOUNT] DetailChat 컴포넌트 언마운트 완료')
})

// 메시지 자동 스크롤 (새 메시지 수신 시에만)
const messagesContainer = ref(null)
watch(selectedChatMessages, (newMessages, oldMessages) => {
  // 새 메시지가 추가된 경우에만 맨 밑으로 스크롤
  if (newMessages.length > oldMessages.length) {
    console.log('새 메시지 감지, 스크롤 이동')
    setTimeout(() => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        console.log('새 메시지로 인한 스크롤 완료')
      }
    }, 200)
  }
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
    const response = await proposalAPI.getProposalDetail(selectedChat.value.proposalId, props.campaignId)
    selectedProposal.value = response
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
const openContractModal = async () => {
  const chatDetail = getChatDetail(selectedChat.value?.chatId)
  
  // 계약서가 아직 생성되지 않은 상태 체크
  const negoStatus = chatDetail?.negoStatus
  if (negoStatus === 'PENDING' || negoStatus === 'REJECTED') {
    alert('계약서가 아직 생성되지 않았습니다.')
    return
  }
  
  if (!selectedChat.value?.proposalId) {
    alert('제안서 정보가 없습니다.')
    return
  }
  
  try {
    // proposalId를 통해 계약서 목록에서 해당 계약서 찾기
    const contracts = await contractApi.getMyContracts(['ONGOING', 'COMPLETED', 'PENDING_SIGN'])
    const matchingContract = contracts.find(contract => 
      contract.proposalId === selectedChat.value.proposalId
    )
    
    if (!matchingContract) {
      alert('해당 제안서의 계약서를 찾을 수 없습니다.')
      return
    }
    
    // 계약서 상세 페이지로 이동
    router.push({
      path: `/mypage/campaign-detail/${props.campaignId}`,
      query: { 
        tab: 'contract',
        contractId: matchingContract.contractId 
      }
    })
  } catch (error) {
    console.error('계약서 조회 실패:', error)
    alert('계약서 정보를 불러오는데 실패했습니다.')
  }
}

// 인플루언서 상세 페이지로 이동
const goToInfluencerDetail = (influencerId) => {
  router.push(`/channels/${influencerId}`)
}

// 계약 작성 페이지로 이동
const goToContractCreate = (proposal) => {
  console.log('🔄 [Contract Create] 계약 작성 페이지로 이동:', proposal)
  
  // proposalId 확인 (여러 가능한 속성명 체크)
  const proposalId = proposal.proposalId || proposal.id
  
  if (!proposalId) {
    console.error('❌ [Contract Create] 제안서 ID를 찾을 수 없음:', proposal)
    alert('제안서가 존재하지 않습니다. 다시 시도해주세요.')
    return
  }
  
  router.push(`/contract/create?proposalId=${proposalId}`)
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

// 제안서 거절 핸들러 (광고주가 직접 거절 버튼 클릭)
const handleRejectProposal = (proposalId) => {
  console.log('🚫 [DIRECT REJECT] 광고주가 직접 제안서 거절:', proposalId)
  
  // 채팅 목록에서 해당 채팅방의 상태 업데이트
  const chatIndex = chatList.value.findIndex(chat => chat.proposalId === proposalId)
  if (chatIndex !== -1) {
    const targetChatId = chatList.value[chatIndex].chatId
    console.log('🚫 [DIRECT REJECT] 거절된 채팅방 ID:', targetChatId)
    
    // 현재 선택된 채팅방이 거절된 경우 즉시 UI 업데이트
    if (selectedChatId.value === targetChatId) {
      console.log('🚫 [DIRECT REJECT] 현재 보고 있는 채팅방이 거절됨 - 즉시 UI 변경')
      
      // 웹소켓 연결 해제
      disconnectSocket()
      
      // 현재 채팅방 거절 상태로 설정
      isCurrentChatRejected.value = true
      
      console.log('✅ [DIRECT REJECT] 즉시 거절 UI 적용 완료')
    }
    
    chatList.value[chatIndex] = {
      ...chatList.value[chatIndex],
      negoStatus: 'REJECTED'
    }
  }
  
  // 채팅 상세 정보도 업데이트
  const chatDetailIndex = chatDetails.value.findIndex(detail => detail.proposalId === proposalId)
  if (chatDetailIndex !== -1) {
    chatDetails.value[chatDetailIndex] = {
      ...chatDetails.value[chatDetailIndex],
      negoStatus: 'REJECTED'
    }
  }
  
  closeProposalModal()
}

// 제안서 승낙 핸들러
const handleAcceptProposal = (proposalId) => {
  // 채팅 목록에서 해당 채팅방의 상태 업데이트
  const chatIndex = chatList.value.findIndex(chat => chat.proposalId === proposalId)
  if (chatIndex !== -1) {
    chatList.value[chatIndex] = {
      ...chatList.value[chatIndex],
      negoStatus: 'ACCEPTED'
    }
  }
  
  // 채팅 상세 정보도 업데이트
  const chatDetailIndex = chatDetails.value.findIndex(detail => detail.proposalId === proposalId)
  if (chatDetailIndex !== -1) {
    chatDetails.value[chatDetailIndex] = {
      ...chatDetails.value[chatDetailIndex],
      negoStatus: 'ACCEPTED',
      chatStatus: 'ACTIVE' // 채팅방 활성화
    }
  }
  
  closeProposalModal()
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
          :class="['chat-item', { 
            active: chat.chatId === selectedChatId,
            'deleted-chat': chat.chatStatus === 'DELETE'
          }]"
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
                <span class="chat-name">
                  {{ getChatDetail(chat.chatId)?.partnerName }}
                  <span v-if="chat.chatStatus === 'DELETE'" class="rejected-badge">거절됨</span>
                </span>
              </div>
              <span class="chat-time">{{ formatTime(chat.lastMessageTime) }}</span>
            </div>
            <div class="chat-preview-wrapper">
              <div class="chat-preview">{{ chat.lastMessage }}</div>
              <div v-if="chat.new && chat.chatStatus !== 'DELETE'" class="new-message-badge"></div>
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
          <span :class="['nego-status-badge', `nego-status-${getNegoStatusClass(getChatDetail(selectedChat?.chatId)?.negoStatus)}`]">
            {{ getNegoStatusText(getChatDetail(selectedChat?.chatId)?.negoStatus) }}
          </span>
          <button class="primary-button" @click="openProposalModal">제안서 보기</button>
          <button class="primary-button" @click="openContractModal">계약서 보기</button>
        </div>
      </div>

      <!-- 메시지 목록 -->
      <div class="messages-container" ref="messagesContainer">
        <div v-if="loading" class="loading">메시지를 불러오는 중...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <!-- 삭제된 채팅방 안내 메시지 -->
        <div v-else-if="isCurrentChatDeleted" class="delete-notice">
          <div class="delete-icon">🚫</div>
          <div class="delete-message">
            <h3>제안서가 거절되었습니다</h3>
            <p>해당 제안서가 거절되어 더 이상 메시지를 주고받을 수 없습니다.</p>
          </div>
        </div>
        <template v-else>
          <template v-for="(message, index) in selectedChatMessages" :key="message.messageId">
            <!-- 날짜 구분선 -->
            <div v-if="shouldShowDateSeparator(message, index, selectedChatMessages)" class="date-separator">
              <span>{{ formatDate(message.messageDate) }}</span>
            </div>
            <!-- 알람 메시지 -->
            <div v-if="message.messageType === 'NOTIFICATION'" class="alarm-wrapper">
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
      <div class="message-input-wrapper">
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
            @click="sendMessage" 
            class="send-button"
            :disabled="isMessageInputDisabled"
          >전송</button>
        </div>
      </div>
    </div>

    <!-- 채팅방 미선택 시 -->
    <div v-else class="no-chat-selected">
      <div class="no-chat-icon">
        <i class="fas fa-comments"></i>
      </div>
      <div class="no-chat-message">
        <h3>채팅방을 선택해주세요</h3>
        <p>왼쪽 목록에서 대화하고 싶은 인플루언서를 선택하세요</p>
      </div>
    </div>
  </div>

  <!-- 제안서 모달 -->
  <div v-if="showProposalModal" class="modal-backdrop" @click="closeProposalModal">
    <div @click.stop>
      <DetailProposalModal
        :proposal="selectedProposal"
        :campaignId="props.campaignId"
        @close="closeProposalModal"
        @contract="goToContractCreate"
        @reject="handleRejectProposal"
        @accept="handleAcceptProposal"
      />
    </div>
  </div>
</template>

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

/* 삭제된 채팅방 목록 스타일 */
.deleted-chat {
  opacity: 0.6;
  background-color: rgba(248, 249, 250, 0.7);
}

.deleted-chat:hover {
  background-color: rgba(233, 236, 239, 0.8);
}

.rejected-badge {
  background: #dc3545;
  color: white;
  padding: 2px 6px;
  border-radius: 8px;
  font-size: 0.7rem;
  font-weight: 600;
  margin-left: 8px;
}

.deleted-chat .chat-name {
  color: #6c757d;
}

.deleted-chat .chat-preview {
  color: #adb5bd;
}

.deleted-chat .chat-time {
  color: #adb5bd;
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


