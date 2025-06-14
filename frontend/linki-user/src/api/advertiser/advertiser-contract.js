import httpClient from '@/utils/httpRequest'

// Contract status constants
export const CONTRACT_STATUS = {
  PENDING_SIGN: 'PENDING_SIGN',  // 서명 대기중
  ONGOING: 'ONGOING',            // 진행중
  COMPLETED: 'COMPLETED',        // 완료
}

export const contractApi = {
  // 계약 목록 조회
  getMyContracts() {
    return httpClient.get(`/v1/api/advertiser/contracts`);
  },

  // 계약 상세 조회
  getContractDetail(contractId) {
    return httpClient.get(`/v1/api/advertiser/contracts/${contractId}`);
  },

  // 계약서 조회
  getContractDocument(contractId) {
    return httpClient.get(`/v1/api/advertiser/contracts/${contractId}/document`);
  },

  // 계약 서명
  signContract(contractId, signData) {
    return httpClient.post(`/v1/api/advertiser/contracts/${contractId}/sign`, signData);
  },

  // 계약서 작성 시작 - 제안서 상세에서 '계약'버튼 누를때 
  startContract(proposalId) {
    console.log('[startContract] POST /v1/api/advertiser/contracts DTO:', proposalId);
    return httpClient.post('/v1/api/advertiser/contracts', proposalId);
  },

  // 계약 이행 상태 업데이트
  executeContract(contractId, isExecuted = true) {
    return httpClient.patch(`/v1/api/advertiser/contracts/${contractId}`, { isExecuted });
  }
};