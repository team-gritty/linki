<template>
  <!-- 버튼: 클릭하면 executeAd 함수 실행. executed가 true면 disabled -->
  <button :disabled="executed" @click="executeAd" class="ad-execution-btn">
    {{ executed ? '이행 완료' : '광고 이행 확인' }}
  </button>
</template>

<script setup>
 // reactive 상태 관리, watch는 props.isExecuted 변화를 감지
import { ref, watch, defineEmits } from 'vue'
 // HTTP 요청을 위해 axios
import { contractApi } from '@/api/advertiser/advertiser-contract'

// emit 정의
const emit = defineEmits(['execution-completed'])

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
// 로컬 스토리지에서 이행 상태 확인
const getStoredExecutionStatus = () => {
  const stored = localStorage.getItem(`contract_executed_${props.contractId}`)
  return stored === 'true'
}

// 버튼 활성화/비활성화 상태를 저장하는 반응형 변수 executed
// 초기값은 props.isExecuted 또는 로컬 스토리지 값 확인
const executed = ref(props.isExecuted || getStoredExecutionStatus())

// 광고 이행 상태가 변경될 때마다 executed 업데이트
watch(() => props.isExecuted, (val) => {
  // props가 true이거나 로컬 스토리지에 저장된 값이 true면 executed를 true로 설정
  executed.value = val || getStoredExecutionStatus()
})

// 광고 이행 처리 함수
// 이미 이행된 경우 아무것도 하지 않음
// 광고 이행 상태를 'yes'로 업데이트하고 성공/실패 메시지 출력
async function executeAd() {
  if (executed.value) return
  
  try {
    console.log('[ContractExecutionButton] 광고 이행 처리 시작')
    console.log('Contract ID:', props.contractId)
    console.log('Contract ID type:', typeof props.contractId)
    console.log('Is Executed:', props.isExecuted)
    
    const response = await contractApi.executeContract(props.contractId, true)
    executed.value = true
    
    // 로컬 스토리지에 이행 완료 상태 저장
    localStorage.setItem(`contract_executed_${props.contractId}`, 'true')
    
    // 부모 컴포넌트에 이행 완료 이벤트 전달
    emit('execution-completed', {
      contractId: props.contractId,
      isExecuted: true
    })
    
    alert('광고 이행이 완료되었습니다.')
    console.log('[ContractExecutionButton] API 성공:', {
      contractId: props.contractId,
      response: response
    })
    
  } catch (e) {
    console.error('[ContractExecutionButton] API 실패:', e)
    console.error('Error status:', e.response?.status)
    console.error('Error details:', e.response?.data)
    console.error('Error config:', e.config)
    console.error('Full error:', e)
    
    let errorMessage = '광고 이행 처리에 실패했습니다.'
    if (e.response?.data?.message) {
      errorMessage += '\n에러: ' + e.response.data.message
    } else if (e.response?.status) {
      errorMessage += '\n상태코드: ' + e.response.status
    }
    alert(errorMessage)
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
