import axios from 'axios';

const BASE_URL = ''; // vite proxy를 사용하므로 BASE_URL은 비워둡니다

export const contractApi = {
  // 계약 목록 조회
  getMyContracts() {
    return axios.get(`/v1/api/advertiser/contracts`);
  },

  // 계약 상세 조회
  getContractDetail(contractId) {
    return axios.get(`/v1/api/advertiser/contracts/${contractId}`);
  },

  // 계약서 조회
  getContractDocument(contractId) {
    return axios.get(`/v1/api/advertiser/contracts/${contractId}/document`);
  },

  // 계약 서명
  signContract(contractId, signData) {
    return axios.post(`/v1/api/advertiser/contracts/${contractId}/sign`, signData);
  }
};