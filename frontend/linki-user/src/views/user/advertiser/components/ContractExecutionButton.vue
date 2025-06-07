<template>
  <button :disabled="executed" @click="executeAd" class="ad-execution-btn">
    {{ executed ? '이행 완료' : '광고 이행 확인' }}
  </button>
</template>

<script setup>
import { ref, watch, toRefs } from 'vue'
import axios from 'axios'

const props = defineProps({
  contractId: {
    type: [Number, String],
    required: true
  },
  adExecuted: {
    type: String,
    required: true
  }
})

const executed = ref(props.adExecuted === 'yes')

watch(() => props.adExecuted, (val) => {
  executed.value = val === 'yes'
})

async function executeAd() {
  if (executed.value) return
  try {
    const res = await axios.patch(`/v1/api/contracts/${props.contractId}`, { ad_executed: 'yes' })
    executed.value = true
    console.log('[ContractExecutionButton] PATCH 성공:', {
      contractId: props.contractId,
      ad_executed: 'yes',
      response: res.data
    })
  } catch (e) {
    alert('광고 이행 처리에 실패했습니다.')
    console.error('[ContractExecutionButton] PATCH 실패:', e)
  }
}
</script>

<style scoped>
.ad-execution-btn {
  background: #8C30F5;
  color: #fff;
  font-weight: 700;
  border: none;
  border-radius: 6px;
  padding: 12px 24px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
}
.ad-execution-btn:disabled {
  background: #ccc;
  color: #fff;
  cursor: not-allowed;
}
</style>
