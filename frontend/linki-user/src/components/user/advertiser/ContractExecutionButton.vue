<template>
  <!-- 버튼: 클릭하면 executeAd 함수 실행. executed가 true면 disabled -->
  <button :disabled="executed" @click="executeAd" class="ad-execution-btn">
    {{ executed ? '이행 완료' : '광고 이행 확인' }}
  </button>
</template>

<script setup>
 // reactive 상태 관리, watch는 props.isExecuted 변화를 감지
import { ref, watch } from 'vue'
 // HTTP 요청을 위해 axios
import { contractApi } from '@/api/advertiser/advertiser-contract'

// 컴포넌트 속성 (props) 정의
const props = defineProps({
  // 서버에 PATCH 요청 보낼 때 어떤 계약서를 업데이트할지 식별하는 ID
  contractId: {
    type: [Number, String],
    required: true
  },
  // 광고 이행 상태. 'yes'면 이미 이행됨.
  isExecuted: {
    type: Boolean,
    required: true
  }
})
// 버튼 활성화/비활성화 상태를 저장하는 반응형 변수 executed
// 초기값은 props.isExecuted가 true인지 확인
const executed = ref(props.isExecuted)

// 광고 이행 상태가 변경될 때마다 executed 업데이트
watch(() => props.isExecuted, (val) => {
  executed.value = val
})

// 광고 이행 처리 함수
// 이미 이행된 경우 아무것도 하지 않음
// 광고 이행 상태를 'yes'로 업데이트하고 성공/실패 메시지 출력
async function executeAd() {
  if (executed.value) return
  try {
    await contractApi.executeContract(props.contractId, true)
    executed.value = true
    console.log('[ContractExecutionButton] PATCH 성공:', {
      contractId: props.contractId,
      isExecuted: true
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
