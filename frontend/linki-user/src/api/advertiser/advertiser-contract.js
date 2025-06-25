import httpClient from '@/utils/httpRequest'

// Contract status constants
export const CONTRACT_STATUS = {
  PENDING_SIGN: 'PENDING_SIGN',  // 서명 대기중
  ONGOING: 'ONGOING',            // 진행중
  COMPLETED: 'COMPLETED',        // 완료
}

export const contractApi = {
  // 계약 목록 조회 (상태별)
  async getMyContracts(statuses = ['ONGOING', 'COMPLETED', 'PENDING_SIGN']) {
    try {
      const statusParams = statuses.map(status => `status=${status}`).join('&');
      const response = await httpClient.get(`/v1/api/advertiser/mypage/contracts?${statusParams}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching contracts:', error);
      throw error;
    }
  },
 // 계약 상세 조회
 async getContractDetail(contractId) {
  try {
    const response = await httpClient.get(`/v1/api/advertiser/mypage/contracts/${contractId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching contract detail:', error);
    throw error;
  }
},
  // 계약서 조회
  getContractDocument(contractId) {
    return httpClient.get(`/v1/api/advertiser/mypage/contracts/${contractId}/document`);
  },

  // 계약 서명
  signContract(contractId, signData) {
    return httpClient.post(`/v1/api/advertiser/mypage/contracts/${contractId}/sign`, signData);
  },

  // 계약서 작성 시작 - 제안서 상세에서 '계약'버튼 누를때 
  startContract(contractData) {
    console.log('[startContract] POST /v1/api/advertiser/contracts DTO:', contractData);
    return httpClient.post(`/v1/api/advertiser/mypage/contracts/${contractData.proposalId}/create`, contractData);
  },

  // 계약 이행 상태 업데이트
  executeContract(contractId, isExecuted = true) {
    return httpClient.patch(`/v1/api/advertiser/contracts/${contractId}`, { isExecuted });
  }
};