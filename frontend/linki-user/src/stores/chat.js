import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { chatApi } from '@/api/chat'

export const useChatStore = defineStore('chat', () => {
  const chatList = ref([])
  const loading = ref(false)
  const sseConnection = ref(null)
  const isSSEConnected = ref(false)
  const hasGlobalNotification = ref(false) // 전역 알림 상태

    // 새로운 메시지가 있는지 여부 (서버의 기본 상태 사용)
  const hasNewMessages = computed(() => {
    if (hasGlobalNotification.value) {
      return true
    }
    
    // 서버의 기본 new 상태만 확인
    return chatList.value.some(chat => {
      return chat.new && chat.chatStatus !== 'DELETE'
    })
  })

  // 정렬된 채팅 목록
  const sortedChatList = computed(() => {
    return [...chatList.value].sort((a, b) => {
      // DELETE 상태가 아닌 채팅방들끼리 비교할 때만 NEW 우선순위 적용
      const aIsActive = a.chatStatus !== 'DELETE'
      const bIsActive = b.chatStatus !== 'DELETE'
      
      // 둘 다 활성 채팅방인 경우에만 NEW 상태로 우선 정렬
      if (aIsActive && bIsActive && a.new !== b.new) {
        return a.new ? -1 : 1  // new가 true인 것을 앞으로
      }
      
      // 나머지는 모두 시간순으로 정렬 (DELETE 포함)
      const timeA = new Date(a.lastMessageTime || 0)
      const timeB = new Date(b.lastMessageTime || 0)
      return timeB - timeA  // 최신이 위로
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
      // 채팅 목록 로드 후 전역 알림 상태 체크
      checkAndClearGlobalNotification()
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
      // 안읽은 메시지가 없으면 전역 알림 해제
      checkAndClearGlobalNotification()
    }
  }

  // 특정 채팅의 캠페인 ID로 읽음 상태 업데이트
  const markChatAsReadByCampaignId = (campaignId) => {
    const chat = chatList.value.find(c => c.campaignId === campaignId)
    if (chat) {
      chat.new = false
      // 안읽은 메시지가 없으면 전역 알림 해제
      checkAndClearGlobalNotification()
    }
  }

  // 특정 채팅의 제안 ID로 읽음 상태 업데이트
  const markChatAsReadByProposalId = (proposalId) => {
    const chat = chatList.value.find(c => c.proposalId === proposalId)
    if (chat) {
      chat.new = false
      // 안읽은 메시지가 없으면 전역 알림 해제
      checkAndClearGlobalNotification()
    }
  }

  // 강제 새로고침
  const refreshChatList = () => {
    return loadChatList()
  }


  // 전역 알림 해제
  const clearGlobalNotification = () => {
    hasGlobalNotification.value = false
  }

    // 안읽은 메시지가 없으면 전역 알림 자동 해제 (서버의 기본 상태 사용)
  const checkAndClearGlobalNotification = () => {
    // 서버의 기본 new 상태만 확인
    const hasUnreadMessages = chatList.value.some(chat => {
      return chat.new && chat.chatStatus !== 'DELETE'
    })
    
    if (!hasUnreadMessages && hasGlobalNotification.value) {
      clearGlobalNotification()
    }
  }



  // 채팅 메시지 업데이트 (실시간)
  const updateChatMessage = (chatId, lastMessage, lastMessageTime, isNew = true) => {
    const chatIndex = chatList.value.findIndex(c => c.chatId === chatId)
    if (chatIndex !== -1) {
      // 완전히 새로운 배열과 객체로 반응성 확실히 트리거
      const newChatList = [...chatList.value]
      newChatList[chatIndex] = {
        ...newChatList[chatIndex],
        lastMessage: lastMessage,
        lastMessageTime: lastMessageTime,
        new: isNew
      }
      chatList.value = newChatList
    } else {
      // 새로운 채팅방이거나 채팅 목록이 비어있는 경우 전체 로드
      loadChatList().then(() => {
        // 로드 후 다시 한번 업데이트 시도
        const newChatIndex = chatList.value.findIndex(c => c.chatId === chatId)
        if (newChatIndex !== -1) {
          const newChatList = [...chatList.value]
          newChatList[newChatIndex] = {
            ...newChatList[newChatIndex],
            lastMessage: lastMessage,
            lastMessageTime: lastMessageTime,
            new: isNew
          }
          chatList.value = newChatList
        } else {
          console.warn('⚠️ [UPDATE] 채팅 목록 로드 후에도 채팅방을 찾을 수 없음:', chatId)
        }
      }).catch(error => {
        console.error('❌ [UPDATE] 채팅 목록 로드 실패:', error)
      })
    }
  }

  // 채팅을 목록 맨 위로 이동
  const moveChatsToTop = (chatId) => {
    const chatIndex = chatList.value.findIndex(c => c.chatId === chatId)

    if (chatIndex > 0) {
      // 완전히 새로운 배열로 반응성 확실히 트리거
      const newChatList = [...chatList.value]
      const chat = newChatList.splice(chatIndex, 1)[0]
      newChatList.unshift(chat)
      chatList.value = newChatList
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

            // 모든 SSE 메시지에 대해 전역 알림 활성화
            hasGlobalNotification.value = true
            console.log('🔔 [SSE] 전역 알림 활성화:', data.type)

            if (data.type === 'NEW_MESSAGE') {
              // SSE로 받은 메시지는 무조건 new: true로 설정 (알림 보장)
              updateChatMessage(data.chatId, data.content, data.messageDate, true)
              moveChatsToTop(data.chatId)

              // 채팅방 컴포넌트에도 알림 전송 (실시간 메시지 업데이트용)
              if (typeof window !== 'undefined' && window.dispatchEvent) {
                const sseEvent = new CustomEvent('sse-new-message', {
                  detail: {
                    chatId: data.chatId,
                    content: data.content,
                    messageDate: data.messageDate,
                    messageType: data.messageType || 'MESSAGE'
                  }
                })
                window.dispatchEvent(sseEvent)
                console.log('📡 [SSE] 채팅방 컴포넌트로 이벤트 전송:', data.chatId, 'type:', data.messageType)
              }

              if (chatList.value.length === 0) {
                loadChatList()
              }
            } else if (data.type === 'PROPOSAL_REJECT') {
              // 제안서 거절 시 특별 처리
              console.log('🚫 [SSE] 제안서 거절 이벤트:', data)

              // 채팅방 컴포넌트에 제안서 거절 이벤트 전송
              if (typeof window !== 'undefined' && window.dispatchEvent) {
                const rejectEvent = new CustomEvent('proposal-reject', {
                  detail: {
                    chatId: data.chatId,
                    proposalId: data.proposalId,
                    campaignId: data.campaignId
                  }
                })
                window.dispatchEvent(rejectEvent)
                console.log('📡 [SSE] 제안서 거절 이벤트 전송:', data.chatId)
              }

              // 채팅 목록도 새로고침
              loadChatList()
            } else if (data.type === 'CHAT_LIST_UPDATE' ||
                       data.type === 'PROPOSAL_REQUEST' ||
                       data.type === 'PROPOSAL_CREATE' ||
                       data.type === 'PROPOSAL_MODIFY' ||
                       data.type === 'PROPOSAL_ACCEPT') {
              // 제안서 관련 기타 이벤트 시 채팅 목록 새로고침
              console.log('🔄 [SSE] 제안서/채팅 목록 업데이트 이벤트:', data.type)

              // 새로운 채팅방이 생성되었을 수 있으므로 강제 새로고침
              loadChatList()
            } else {
              // 알 수 없는 이벤트 타입도 로그로 기록
              console.log('🤔 [SSE] 알 수 없는 이벤트 타입:', data.type, data)
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

  // SSE 연결 상태 확인 함수
  const checkSSEStatus = () => {
    console.log('🔍 [SSE] 연결 상태 확인:')
    console.log('- 연결됨:', isSSEConnected.value)
    console.log('- 연결 객체:', sseConnection.value)
    console.log('- 연결 상태:', sseConnection.value?.readyState)
    console.log('- 채팅 목록 개수:', chatList.value.length)
    console.log('- 새 메시지 있음:', hasNewMessages.value)

    return {
      isConnected: isSSEConnected.value,
      connection: sseConnection.value,
      readyState: sseConnection.value?.readyState,
      chatListLength: chatList.value.length,
      hasNewMessages: hasNewMessages.value
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
    window.checkSSEStatus = checkSSEStatus
    window.clearGlobalNotification = clearGlobalNotification

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
    isSSEConnected,
    checkSSEStatus,
    clearGlobalNotification
  }
})