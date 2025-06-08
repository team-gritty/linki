import axios from 'axios'

export const chatApi = {
  // 채팅방 생성
  createRoom: async (proposalId) => {
    try {
      return await axios.post(`/v1/api/chat/room`, {
        chatId: `chat${Date.now()}`,
        opponentName: "New Chat",
        opponentId: `inf${Date.now()}`,
        lastMessage: "",
        lastMessageTime: new Date().toISOString(),
        isNew: false,
        proposalId
      })
    } catch (error) {
      console.error('Error creating chat room:', error)
      throw error
    }
  },

  // 채팅방 활성화
  activateRoom: async (proposalId) => {
    try {
      return await axios.post(`/v1/api/chat/room/activate/${proposalId}`)
    } catch (error) {
      console.error('Error activating chat room:', error)
      throw error
    }
  },

  // 채팅방 목록 조회
  getChatList: async () => {
    try {
      return await axios.get(`/v1/api/chat/list`)
    } catch (error) {
      console.error('Error getting chat list:', error)
      throw error
    }
  },

  // 채팅방 상세 정보 조회
  enterRoom: async (chatId) => {
    try {
      const response = await axios.get(`/v1/api/chat/room/${chatId}`)
      const messages = await axios.get(`/v1/api/chat/messages/${chatId}`)
      return {
        ...response.data,
        messages: messages.data
      }
    } catch (error) {
      console.error('Error entering chat room:', error)
      throw error
    }
  },

  // 메시지 조회
  getMessages: async (chatId) => {
    try {
      return await axios.get(`/v1/api/chat/messages/${chatId}`)
    } catch (error) {
      console.error('Error getting messages:', error)
      throw error
    }
  },

  // 알람 읽음 처리
  readAlarm: async (alarmDto) => {
    try {
      return await axios.patch(`/v1/api/chat/alarm/read`, alarmDto)
    } catch (error) {
      console.error('Error marking alarm as read:', error)
      throw error
    }
  },

  // 제안서 거절
  rejectProposal: async (chatId) => {
    try {
      return await axios.post(`/v1/api/proposals/${chatId}/reject`)
    } catch (error) {
      console.error('Error rejecting proposal:', error)
      throw error
    }
  },

  // 메시지 전송
  sendMessage: async (messageData) => {
    try {
      return await axios.post(`/v1/api/chat/messages`, messageData)
    } catch (error) {
      console.error('Error sending message:', error)
      throw error
    }
  },

  // 프로필 정보 조회
  getProfiles: async () => {
    try {
      return await axios.get(`/v1/api/chat/profiles`)
    } catch (error) {
      console.error('Error getting profiles:', error)
      throw error
    }
  }
} 