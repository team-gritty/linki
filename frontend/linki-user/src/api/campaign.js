import httpClient from '@/utils/httpRequest'

export const campaignAPI = {
  // 카테고리 목록 조회
  getCategories: async () => {
    try {
      const response = await httpClient.get('/v1/api/nonuser/home/categories')
      return response.data
    } catch (error) {
      console.error('Error fetching categories:', error)
      return []
    }
  },

  // 캠페인 목록 조회
  getCampaigns: async (params = {}) => {
    try {
      const queryParams = {
        _page: params._page || 1,
        _limit: params._limit || 10,
        _sort: params._sort || 'createdAt',
        _order: params._order || 'desc'
      }

      // 카테고리 필터링 파라미터 추가
      if (params.campaignCategory && params.campaignCategory !== '전체') {
        queryParams.campaignCategory = params.campaignCategory
      }

      console.log('Fetching campaigns with params:', queryParams) // 디버깅용

      const response = await httpClient.get('/v1/api/nonuser/campaigns', {
        params: queryParams
      })

      let campaigns = response.data;
      const totalCount = parseInt(response.headers['x-total-count']) || campaigns.length;
      
      console.log('Received campaigns:', campaigns) // 디버깅용

      return {
        campaigns: campaigns,
        totalItems: totalCount
      }
    } catch (error) {
      console.error('Error fetching campaigns:', error)
      return {
        campaigns: [],
        totalItems: 0
      }
    }
  },

  // 캠페인 상세 정보 조회
  getCampaignDetail: async (campaignId) => {
    try {
      const response = await httpClient.get(`/v1/api/nonuser/campaigns/${campaignId}`);
      const campaigns = response.data;
      const found = Array.isArray(campaigns) ? campaigns.find(c => c.campaignId === campaignId) : null;
      if (!found) {
        throw new Error('해당 캠페인을 찾을 수 없습니다.');
      }
      return found;
    } catch (error) {
      console.error('Error fetching campaign detail:', error)
      throw error
    }
  },
  submitProposal: async (id, contents) => {
    return await httpClient.post(`/v1/api/influencer/campaigns/${id}/proposal`, { contents })
  },
  // 광고주 리뷰 조회
  // getAdvertiserReviews: async (advertiserId) => {
  //   try {
  //     const response = await httpClient.get(`/api/influencer/advertiser-reviews`, {
  //       params: {
  //         advertiser_id: String(advertiserId)
  //       }
  //     })
  //     return response.data
  //   } catch (error) {
  //     console.error('Error fetching advertiser reviews:', error)
  //     return []
  //   }
  // },

 



  // 계약서 상세 조회
  // getContract: async (contractId) => {
  //   try {
  //     const response = await httpClient.get(`/v1/api/contracts/${contractId}`)
  //     return response.data
  //   } catch (error) {
  //     console.error('Error fetching contract:', error)
  //     throw error
  //   }
  // }
} 