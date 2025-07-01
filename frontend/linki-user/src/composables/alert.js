import { ref } from 'vue'

const alerts = ref([])

export function useAlert() {
  const showAlert = (message, type = 'info') => {
    const id = Date.now()
    alerts.value.push({
      id,
      message,
      type,
      show: true
    })

    // 3초 후 자동으로 알림 제거
    setTimeout(() => {
      removeAlert(id)
    }, 3000)
  }

  const removeAlert = (id) => {
    const index = alerts.value.findIndex(alert => alert.id === id)
    if (index !== -1) {
      alerts.value.splice(index, 1)
    }
  }

  return {
    alerts,
    showAlert,
    removeAlert
  }
} 