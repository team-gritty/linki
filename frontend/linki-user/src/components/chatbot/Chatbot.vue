<template>
  <div v-if="showChatbot" class="chatbot-container" :class="{ 'is-open': isOpen }" ref="chatbotContainer">
    <!-- 챗봇 토글 버튼 -->
    <div class="chat-toggle-container">
      <button class="chat-toggle" @click="toggleChat">

      </button>
    </div>

    <!-- 챗봇 메인 창 -->
    <div class="chat-window" v-show="isOpen">
      <!-- 챗봇 헤더 -->
      <div class="chat-header">
        <div class="chat-title">
          <div class="bot-avatar"></div>
          <span>Linki Assistant</span>
        </div>
      </div>

      <!-- 메시지 영역 -->
      <div class="chat-messages" ref="messageContainer">
        <div
          v-for="(message, index) in messages"
          :key="index"
          :class="['message', message.type]"
        >
          <div class="message-content">
            <template v-if="message.type === 'bot'">
              <div class="message-avatar"></div>
            </template>
            {{ message.text }}
          </div>
          <div class="message-time">{{ formatTime(message.timestamp) }}</div>
        </div>
        <div v-if="isTyping" class="message bot">
          <div class="message-content">
            <div class="message-avatar"></div>
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 입력 영역 -->
      <div class="chat-input">
        <input
          type="text"
          v-model="userInput"
          @keyup.enter="sendMessage"
          placeholder="메시지를 입력하세요..."
          :disabled="isTyping"
        />
        <button @click="sendMessage" :disabled="!userInput.trim() || isTyping">
          <span>↑</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch, nextTick, computed, onUnmounted } from 'vue'
import { useChatbotStore } from '@/stores/chatbot'

export default {
  name: 'Chatbot',
  setup() {
    const isOpen = ref(false)
    const userInput = ref('')
    const messages = ref([])
    const isTyping = ref(false)
    const messageContainer = ref(null)
    const chatbotContainer = ref(null)
    const chatbotStore = useChatbotStore()
    const showChatbot = computed(() => chatbotStore.showChatbot)

    // 초기 메시지
    const initialMessage = {
      type: 'bot',
      text: '안녕하세요! Linki Assistant입니다. 무엇을 도와드릴까요?',
      timestamp: new Date()
    }

    // 외부 클릭 이벤트 핸들러
    const handleClickOutside = (event) => {
      if (isOpen.value && chatbotContainer.value && !chatbotContainer.value.contains(event.target)) {
        isOpen.value = false
      }
    }

    // 컴포넌트 마운트 시 이벤트 리스너 추가
    onMounted(() => {
      document.addEventListener('click', handleClickOutside)
    })

    // 컴포넌트 언마운트 시 이벤트 리스너 제거
    onUnmounted(() => {
      document.removeEventListener('click', handleClickOutside)
    })

    // 챗봇 토글
    const toggleChat = () => {
      isOpen.value = !isOpen.value
      if (isOpen.value && messages.value.length === 0) {
        messages.value.push(initialMessage)
      }
    }

    // 시간 포맷팅
    const formatTime = (date) => {
      return new Date(date).toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    // 메시지 스크롤
    const scrollToBottom = async () => {
      await nextTick()
      if (messageContainer.value) {
        messageContainer.value.scrollTop = messageContainer.value.scrollHeight
      }
    }

    // 봇 응답 생성
    const generateBotResponse = async (userMessage) => {
      // 실제 구현에서는 API 호출 등으로 대체
      const responses = {
        '안녕': '안녕하세요! 오늘도 좋은 하루 보내세요.',
        '도움말': '다음과 같은 것들을 물어보실 수 있습니다:\n- 인플루언서 등록 방법\n- 광고주 등록 방법\n- 계정 설정\n- 결제 방법',
        '광고': '광고주 등록 후 광고를 등록하실 수 있습니다. 자세한 내용은 "광고주 등록 방법"을 참고해 주세요.',
      }

      isTyping.value = true
      await new Promise(resolve => setTimeout(resolve, 1000))

      let botResponse = '죄송합니다. 잘 이해하지 못했습니다. "도움말"을 입력하시면 가능한 질문 목록을 보여드립니다.'
      
      for (const [key, value] of Object.entries(responses)) {
        if (userMessage.includes(key)) {
          botResponse = value
          break
        }
      }

      messages.value.push({
        type: 'bot',
        text: botResponse,
        timestamp: new Date()
      })

      isTyping.value = false
      await scrollToBottom()
    }

    // 메시지 전송
    const sendMessage = async () => {
      const message = userInput.value.trim()
      if (!message || isTyping.value) return

      messages.value.push({
        type: 'user',
        text: message,
        timestamp: new Date()
      })

      userInput.value = ''
      await scrollToBottom()
      await generateBotResponse(message)
    }

    // 메시지 추가될 때마다 스크롤
    watch(messages, () => {
      scrollToBottom()
    })

    return {
      isOpen,
      userInput,
      messages,
      isTyping,
      messageContainer,
      chatbotContainer,
      toggleChat,
      sendMessage,
      formatTime,
      showChatbot
    }
  }
}
</script>

<style scoped>
.chatbot-container {
  position: fixed;
  bottom: 100px;
  right: 20px;
  z-index: 1000;
}

.chat-toggle-container {
  position: relative;
  display: inline-block;
}

.chat-toggle {
  width: 60px;
  height: 60px;
  border-radius: 30px;
  background-color: #7b21e8;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  background-image: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDUwMCA1MDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxwYXRoIGZpbGwtcnVsZT0iZXZlbm9kZCIgY2xpcC1ydWxlPSJldmVub2RkIiBkPSJNMjUwIDQxNkMxNjcuMTU3IDQxNiAxMDAgMzQ4Ljg0MyAxMDAgMjY2QzEwMCAxODMuMTU3IDE2Ny4xNTcgMTE2IDI1MCAxMTZDMzMyLjg0MyAxMTYgNDAwIDE4My4xNTcgNDAwIDI2NkM0MDAgMzQ4Ljg0MyAzMzIuODQzIDQxNiAyNTAgNDE2Wk0yNTAgNDUwQzE0OC40NTggNDUwIDY2IDM2Ny41NDIgNjYgMjY2QzY2IDE2NC40NTggMTQ4LjQ1OCA4MiAyNTAgODJDMzUxLjU0MiA4MiA0MzQgMTY0LjQ1OCA0MzQgMjY2QzQzNCAzNjcuNTQyIDM1MS41NDIgNDUwIDI1MCA0NTBaIiBmaWxsPSJ3aGl0ZSIvPgo8cGF0aCBkPSJNMjUwIDUwQzI1MCA1MCAyNTAgNTAgMjUwIDUwQzI2MS4wNDYgNTAgMjcwIDU4Ljk1NDMgMjcwIDcwVjkwQzI3MCAxMDEuMDQ2IDI2MS4wNDYgMTEwIDI1MCAxMTBDMjM4Ljk1NCAxMTAgMjMwIDEwMS4wNDYgMjMwIDkwVjcwQzIzMCA1OC45NTQzIDIzOC45NTQgNTAgMjUwIDUwWiIgZmlsbD0id2hpdGUiLz4KPGNpcmNsZSBjeD0iMTgwIiBjeT0iMjMwIiByPSIzMCIgZmlsbD0id2hpdGUiLz4KPGNpcmNsZSBjeD0iMzIwIiBjeT0iMjMwIiByPSIzMCIgZmlsbD0iIzdiMjFlOCIvPgo8cGF0aCBkPSJNMTgwIDMzMEMxODAgMzE4Ljk1NCAxODguOTU0IDMxMCAyMDAgMzEwSDMwMEMzMTEuMDQ2IDMxMCAzMjAgMzE4Ljk1NCAzMjAgMzMwQzMyMCAzNDEuMDQ2IDMxMS4wNDYgMzUwIDMwMCAzNTBIMjAwQzE4OC45NTQgMzUwIDE4MCAzNDEuMDQ2IDE4MCAzMzBaIiBmaWxsPSIjN2IyMWU4Ii8+PC9zdmc+');
  background-size: 35px;
  background-position: center;
  background-repeat: no-repeat;
}

.chat-window {
  position: absolute;
  bottom: 80px;
  right: 0;
  width: 360px;
  height: 500px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  padding: 20px;
  background: #7b21e8;
  color: white;
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
}

.bot-avatar {
  width: 32px;
  height: 32px;
  border-radius: 16px;
  background-image: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDUwMCA1MDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxwYXRoIGZpbGwtcnVsZT0iZXZlbm9kZCIgY2xpcC1ydWxlPSJldmVub2RkIiBkPSJNMjUwIDQxNkMxNjcuMTU3IDQxNiAxMDAgMzQ4Ljg0MyAxMDAgMjY2QzEwMCAxODMuMTU3IDE2Ny4xNTcgMTE2IDI1MCAxMTZDMzMyLjg0MyAxMTYgNDAwIDE4My4xNTcgNDAwIDI2NkM0MDAgMzQ4Ljg0MyAzMzIuODQzIDQxNiAyNTAgNDE2Wk0yNTAgNDUwQzE0OC40NTggNDUwIDY2IDM2Ny41NDIgNjYgMjY2QzY2IDE2NC40NTggMTQ4LjQ1OCA4MiAyNTAgODJDMzUxLjU0MiA4MiA0MzQgMTY0LjQ1OCA0MzQgMjY2QzQzNCAzNjcuNTQyIDM1MS41NDIgNDUwIDI1MCA0NTBaIiBmaWxsPSIjN2IyMWU4Ii8+CjxwYXRoIGQ9Ik0yNTAgNTBDMjUwIDUwIDI1MCA1MCAyNTAgNTBDMjYxLjA0NiA1MCAyNzAgNTguOTU0MyAyNzAgNzBWOTBDMjcwIDEwMS4wNDYgMjYxLjA0NiAxMTAgMjUwIDExMEMyMzguOTU0IDExMCAyMzAgMTAxLjA0NiAyMzAgOTBWNzBDMjMwIDU4Ljk1NDMgMjM4Ljk1NCA1MCAyNTAgNTBaIiBmaWxsPSIjN2IyMWU4Ii8+CjxjaXJjbGUgY3g9IjE4MCIgY3k9IjIzMCIgcj0iMzAiIGZpbGw9IiM3YjIxZTgiLz4KPGNpcmNsZSBjeD0iMzIwIiBjeT0iMjMwIiByPSIzMCIgZmlsbD0iIzdiMjFlOCIvPgo8cGF0aCBkPSJNMTgwIDMzMEMxODAgMzE4Ljk1NCAxODguOTU0IDMxMCAyMDAgMzEwSDMwMEMzMTEuMDQ2IDMxMCAzMjAgMzE4Ljk1NCAzMjAgMzMwQzMyMCAzNDEuMDQ2IDMxMS4wNDYgMzUwIDMwMCAzNTBIMjAwQzE4OC45NTQgMzUwIDE4MCAzNDEuMDQ2IDE4MCAzMzBaIiBmaWxsPSIjN2IyMWU4Ii8+PC9zdmc+');
  background-size: 24px;
  background-position: center;
  background-repeat: no-repeat;
  background-color: white;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-width: 80%;
}

.message.user {
  align-self: flex-end;
}

.message.bot {
  align-self: flex-start;
}

.message-content {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.4;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.message.user .message-content {
  background: #7b21e8;
  color: white;
}

.message.bot .message-content {
  background: #f5f5f5;
  color: #333;
}

.message-avatar {
  width: 24px;
  height: 24px;
  border-radius: 12px;
  background-image: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDUwMCA1MDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxwYXRoIGZpbGwtcnVsZT0iZXZlbm9kZCIgY2xpcC1ydWxlPSJldmVub2RkIiBkPSJNMjUwIDQxNkMxNjcuMTU3IDQxNiAxMDAgMzQ4Ljg0MyAxMDAgMjY2QzEwMCAxODMuMTU3IDE2Ny4xNTcgMTE2IDI1MCAxMTZDMzMyLjg0MyAxMTYgNDAwIDE4My4xNTcgNDAwIDI2NkM0MDAgMzQ4Ljg0MyAzMzIuODQzIDQxNiAyNTAgNDE2Wk0yNTAgNDUwQzE0OC40NTggNDUwIDY2IDM2Ny41NDIgNjYgMjY2QzY2IDE2NC40NTggMTQ4LjQ1OCA4MiAyNTAgODJDMzUxLjU0MiA4MiA0MzQgMTY0LjQ1OCA0MzQgMjY2QzQzNCAzNjcuNTQyIDM1MS41NDIgNDUwIDI1MCA0NTBaIiBmaWxsPSIjN2IyMWU4Ii8+CjxwYXRoIGQ9Ik0yNTAgNTBDMjUwIDUwIDI1MCA1MCAyNTAgNTBDMjYxLjA0NiA1MCAyNzAgNTguOTU0MyAyNzAgNzBWOTBDMjcwIDEwMS4wNDYgMjYxLjA0NiAxMTAgMjUwIDExMEMyMzguOTU0IDExMCAyMzAgMTAxLjA0NiAyMzAgOTBWNzBDMjMwIDU4Ljk1NDMgMjM4Ljk1NCA1MCAyNTAgNTBaIiBmaWxsPSIjN2IyMWU4Ii8+CjxjaXJjbGUgY3g9IjE4MCIgY3k9IjIzMCIgcj0iMzAiIGZpbGw9IiM3YjIxZTgiLz4KPGNpcmNsZSBjeD0iMzIwIiBjeT0iMjMwIiByPSIzMCIgZmlsbD0iIzdiMjFlOCIvPgo8cGF0aCBkPSJNMTgwIDMzMEMxODAgMzE4Ljk1NCAxODguOTU0IDMxMCAyMDAgMzEwSDMwMEMzMTEuMDQ2IDMxMCAzMjAgMzE4Ljk1NCAzMjAgMzMwQzMyMCAzNDEuMDQ2IDMxMS4wNDYgMzUwIDMwMCAzNTBIMjAwQzE4OC45NTQgMzUwIDE4MCAzNDEuMDQ2IDE4MCAzMzBaIiBmaWxsPSIjN2IyMWU4Ii8+PC9zdmc+');
  background-size: 20px;
  background-position: center;
  background-repeat: no-repeat;
  background-color: white;
}

.message-time {
  font-size: 12px;
  color: #999;
  align-self: flex-end;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 8px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #999;
  border-radius: 50%;
  animation: typing 1s infinite ease-in-out;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}

.chat-input {
  padding: 20px;
  display: flex;
  gap: 12px;
  border-top: 1px solid #eee;
}

.chat-input input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s ease;
}

.chat-input input:focus {
  border-color: #7b21e8;
}

.chat-input button {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  background: #7b21e8;
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  transition: background-color 0.3s ease;
}

.chat-input button:disabled {
  background: #ddd;
  cursor: not-allowed;
}

.chat-input button:not(:disabled):hover {
  background: #6618c4;
}
</style> 