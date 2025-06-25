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
      const url = `/v1/api/advertiser/mypage/contracts?${statusParams}`;
      console.log('=== getMyContracts API 호출 ===');
      console.log('Request URL:', url);
      console.log('Request statuses:', statuses);
      
      const response = await httpClient.get(url);
      console.log('Raw response:', response);
      console.log('Response data:', response.data);
      console.log('Response data type:', typeof response.data);
      console.log('Is response.data array?:', Array.isArray(response.data));
      
      if (Array.isArray(response.data)) {
        console.log('Response data contents:', response.data);
        response.data.forEach((item, index) => {
          console.log(`Item ${index}:`, item);
          console.log(`Item ${index} status:`, item.contractStatus);
        });
      }
      
      return response.data;
    } catch (error) {
      console.error('Error fetching contracts:', error);
      throw error;
    }
  },
 // 계약 상세 조회
 async getContractDetail(contractId) {
  try {
    console.log('=== contractApi.getContractDetail 시작 ===');
    console.log('Request contractId:', contractId);
    const url = `/v1/api/advertiser/mypage/contracts/${contractId}`;
    console.log('Request URL:', url);
    
    const response = await httpClient.get(url);
    console.log('API Response:', response);
    console.log('API Response data:', response.data);
    
    return response.data;
  } catch (error) {
    console.error('Error in contractApi.getContractDetail:', error);
    console.error('Error response:', error.response);
    console.error('Error status:', error.response?.status);
    console.error('Error data:', error.response?.data);
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