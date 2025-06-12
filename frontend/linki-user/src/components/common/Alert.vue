<template>
  <div class="alert-container">
    <TransitionGroup name="alert">
      <div
        v-for="alert in alerts"
        :key="alert.id"
        :class="['alert', `alert-${alert.type}`]"
        @click="removeAlert(alert.id)"
      >
        {{ alert.message }}
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
import { useAlert } from '@/composables/alert'

const { alerts, removeAlert } = useAlert()
</script>

<style scoped>
.alert-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.alert {
  padding: 15px 20px;
  border-radius: 4px;
  color: white;
  cursor: pointer;
  min-width: 280px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.alert-success {
  background-color: #4caf50;
}

.alert-error {
  background-color: #f44336;
}

.alert-info {
  background-color: #2196f3;
}

.alert-warning {
  background-color: #ff9800;
}

/* Alert 애니메이션 */
.alert-enter-active,
.alert-leave-active {
  transition: all 0.3s ease;
}

.alert-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.alert-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style> 