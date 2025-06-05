import axios from 'axios';

const BASE_URL = 'http://localhost:3000';

export const getMyProposals = async () => {
  try {
    const response = await axios.get(`${BASE_URL}/proposals`);
    return response.data;
  } catch (error) {
    console.error('Failed to fetch proposals:', error);
    throw error;
  }
};

export const submitProposal = async (proposalData) => {
  try {
    const response = await axios.post(`${BASE_URL}/proposals`, proposalData);
    return response.data;
  } catch (error) {
    console.error('Failed to submit proposal:', error);
    throw error;
  }
};

export const updateProposal = async (id, proposalData) => {
  try {
    const response = await axios.put(`${BASE_URL}/proposals/${id}`, proposalData);
    return response.data;
  } catch (error) {
    console.error('Failed to update proposal:', error);
    throw error;
  }
};

export const deleteProposal = async (id) => {
  try {
    const response = await axios.delete(`${BASE_URL}/proposals/${id}`);
    return response.data;
  } catch (error) {
    console.error('Failed to delete proposal:', error);
    throw error;
  }
}; 