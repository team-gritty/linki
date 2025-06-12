import { defineStore } from 'pinia'

export const useChatbotStore = defineStore('chatbot', {
  state: () => ({
    showChatbot: true
  }),
  
  actions: {
    toggleChatbot(value) {
      if (typeof value === 'boolean') {
        this.showChatbot = value
      } else {
        this.showChatbot = !this.showChatbot
      }
    }
  }
}) 