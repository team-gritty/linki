import axios from 'axios';

const BASE_URL = 'http://localhost:3000/v1/api';

export const proposalAPI = {
  // 내 제안서 목록 조회
  getMyProposals: async (status) => {
    try {
      const response = await axios.get(`${BASE_URL}/influencer/proposals`, {
        params: { status }
      });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch proposals:', error);
      throw error;
    }
  },

  // 제안서 수정
  updateProposal: async (proposalId, proposalData) => {
    try {
      const response = await axios.put(`${BASE_URL}/influencer/proposals/${proposalId}`, proposalData);
      return response.data;
    } catch (error) {
      console.error('Failed to update proposal:', error);
      throw error;
    }
  },

  // 제안서 삭제
  deleteProposal: async (proposalId) => {
    try {
      const response = await axios.delete(`${BASE_URL}/influencer/proposals/${proposalId}`);
      return response.data;
    } catch (error) {
      console.error('Failed to delete proposal:', error);
      throw error;
    }
  }
} 