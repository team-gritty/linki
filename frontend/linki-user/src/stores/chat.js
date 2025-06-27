import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { chatApi } from '@/api/chat'

export const useChatStore = defineStore('chat', () => {
  const chatList = ref([])
  const loading = ref(false)
  const sseConnection = ref(null)
  const isSSEConnected = ref(false)
  
  // 새로운 메시지가 있는지 여부
  let previousHasNewMessages = false
  const hasNewMessages = computed(() => {
    const result = chatList.value.some(chat => chat.new === true)
    
    // 값이 변경되었을 때만 로그 출력
    if (result !== previousHasNewMessages) {
      console.log(`🟡 [NEW] hasNewMessages: ${previousHasNewMessages} → ${result}`)
      previousHasNewMessages = result
    }
    
    return result
  })

  // 정렬된 채팅 목록
  const sortedChatList = computed(() => {
    return [...chatList.value].sort((a, b) => {
      // 먼저 읽지 않은 메시지 우선
      if (a.new !== b.new) {
        return b.new - a.new
      }
      // 그 다음 최신 메시지 순
      return new Date(b.lastMessageTime) - new Date(a.lastMessageTime)
    })
  })

  // 채팅 목록 로드
  const loadChatList = async () => {
    // 이미 로딩 중인 경우 중복 요청 방지
    if (loading.value) {
      return chatList.value
    }
    
    try {
      loading.value = true
      const data = await chatApi.getUserChatList()
      chatList.value = data || []
      return chatList.value
    } catch (error) {
      console.error('Failed to load chat list:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 특정 채팅의 읽음 상태 업데이트
  const markChatAsRead = (chatId) => {
    const chat = chatList.value.find(c => c.chatId === chatId)
    if (chat) {
      chat.new = false
    }
  }

  // 특정 채팅의 캠페인 ID로 읽음 상태 업데이트
  const markChatAsReadByCampaignId = (campaignId) => {
    const chat = chatList.value.find(c => c.campaignId === campaignId)
    if (chat) {
      chat.new = false
    }
  }

  // 특정 채팅의 제안 ID로 읽음 상태 업데이트
  const markChatAsReadByProposalId = (proposalId) => {
    const chat = chatList.value.find(c => c.proposalId === proposalId)
    if (chat) {
      chat.new = false
    }
  }

  // 강제 새로고침
  const refreshChatList = () => {
    return loadChatList()
  }

  // 캐시 무효화
  const invalidateCache = () => {
    // No cache management needed
  }

  // 채팅 메시지 업데이트 (실시간)
  const updateChatMessage = (chatId, lastMessage, lastMessageTime, isNew = true) => {
    
    const chatIndex = chatList.value.findIndex(c => c.chatId === chatId)
    if (chatIndex !== -1) {
      const chat = chatList.value[chatIndex]
      
      // Vue 반응성을 위해 새로운 객체로 교체
      const updatedChat = {
        ...chat,
        lastMessage: lastMessage,
        lastMessageTime: lastMessageTime,
        new: isNew
      }
      
      chatList.value.splice(chatIndex, 1, updatedChat)
      chatList.value = [...chatList.value] // 반응성 트리거
    } else {
      // 채팅 목록이 비어있다면 로드 시도
      if (chatList.value.length === 0) {
        loadChatList().then(() => {
          updateChatMessage(chatId, lastMessage, lastMessageTime, isNew)
        })
      }
    }
  }

  // 채팅을 목록 맨 위로 이동
  const moveChatsToTop = (chatId) => {
    const chatIndex = chatList.value.findIndex(c => c.chatId === chatId)
    
    if (chatIndex > 0) {
      const chat = chatList.value.splice(chatIndex, 1)[0]
      chatList.value.unshift(chat)
      chatList.value = [...chatList.value]
    }
  }

  // 전역 SSE 연결 시작
  const startGlobalSSE = (userId) => {
    // 기존 연결 정리
    if (sseConnection.value) {
      try {
        sseConnection.value.close()
      } catch (e) {}
      sseConnection.value = null
      isSSEConnected.value = false
    }
    
    if (!userId) return

    console.log('✨ [SSE] 연결 시작:', userId)
    
    try {
      sseConnection.value = chatApi.connectSSE(
        'global', // 전역 연결용 특별한 chatId
        // onMessage
        (event) => {
          console.log('📨 [SSE] 메시지 수신:', event.data)
          
          try {
            if (event.data === 'SSE connection established') {
              return
            }
            
            const data = JSON.parse(event.data)
            
            if (data.type === 'NEW_MESSAGE') {
              console.log('💬 [SSE] 새 메시지 처리:', data.chatId)
              updateChatMessage(data.chatId, data.content, data.messageDate, true)
              moveChatsToTop(data.chatId)
              
              if (chatList.value.length === 0) {
                loadChatList()
              }
            } else if (data.type === 'CHAT_LIST_UPDATE') {
              loadChatList()
            }
          } catch (error) {
            console.error('❌ [SSE] 메시지 파싱 실패:', error)
          }
        },
        // onError
        (error) => {
          isSSEConnected.value = false
          setTimeout(() => {
            if (userId) startGlobalSSE(userId)
          }, 5000)
        },
        // onOpen
        () => {
          console.log('✅ [SSE] 연결 완료')
          isSSEConnected.value = true
        }
      )
    } catch (error) {
      console.error('❌ [SSE] 연결 실패:', error)
    }
  }

  // 전역 SSE 연결 해제
  const stopGlobalSSE = () => {
    if (sseConnection.value) {
      chatApi.disconnectSSE(sseConnection.value)
      sseConnection.value = null
      isSSEConnected.value = false
    }
  }

  // 전역 함수로 쉽게 접근할 수 있도록 (실시간 업데이트용)
  if (typeof window !== 'undefined' && !window.chatStoreInitialized) {
    // 전역 함수들 - 다른 컴포넌트에서 쉽게 사용할 수 있도록
    window.markChatAsRead = markChatAsRead
    window.markChatAsReadByCampaignId = markChatAsReadByCampaignId  
    window.markChatAsReadByProposalId = markChatAsReadByProposalId
    window.updateChatList = loadChatList
    window.updateChatMessage = updateChatMessage
    window.moveChatsToTop = moveChatsToTop
    window.startGlobalSSE = startGlobalSSE
    window.stopGlobalSSE = stopGlobalSSE
    
    window.chatStoreInitialized = true
  }

  return {
    chatList,
    loading,
    hasNewMessages,
    sortedChatList,
    loadChatList,
    markChatAsRead,
    markChatAsReadByCampaignId,
    markChatAsReadByProposalId,
    refreshChatList,
    updateChatMessage,
    moveChatsToTop,
    startGlobalSSE,
    stopGlobalSSE,
    isSSEConnected
  }
}) 