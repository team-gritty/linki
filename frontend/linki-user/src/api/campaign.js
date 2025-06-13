import httpClient from '@/utils/httpRequest'

export const campaignAPI = {
  // 카테고리 목록 조회
  getCategories: async () => {
    try {
      const response = await httpClient.get('/v1/api/home/categories')
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

      const response = await httpClient.get('/v1/api/influencer/campaigns', {
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
      const response = await httpClient.get(`/v1/api/campaigns/${campaignId}`);
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

  // 광고주 리뷰 조회
  getAdvertiserReviews: async (advertiserId) => {
    try {
      const response = await httpClient.get(`/api/influencer/advertiser-reviews`, {
        params: {
          advertiser_id: String(advertiserId)
        }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching advertiser reviews:', error)
      return []
    }
  },

  // 제안서 제출
  submitProposal: async (campaignId, contents) => {
    try {
      const response = await httpClient.post(`/v1/api/influencer/campaigns/${campaignId}/proposals`, {
        contents: contents,
        campaign_id: String(campaignId),
        status: 'PENDING',
        submitted_at: new Date().toISOString(),
        influencer_id: 'INF001' // TODO: 실제 로그인한 사용자 ID로 대체
      })
      return response.data
    } catch (error) {
      console.error('Error submitting proposal:', error)
      throw error
    }
  },

  // 마이페이지 - 캠페인 목록 조회
  getMyCampaigns: async (params = {}) => {
    try {
      const response = await httpClient.get('/v1/api/mypage/campaign-list', {
        params: {
          _page: params._page || 1,
          _limit: params._limit || 10,
          _sort: params._sort || 'createdAt',
          _order: params._order || 'desc'
        }
      })
      return {
        campaigns: response.data,
        totalItems: parseInt(response.headers['x-total-count'] || '0')
      }
    } catch (error) {
      console.error('Error fetching my campaigns:', error)
      return {
        campaigns: [],
        totalItems: 0
      }
    }
  },

  // 마이페이지 - 캠페인 상세 정보 조회
  getMyCampaignDetail: async (campaignId) => {
    try {
      console.log('API: Fetching campaign detail for ID:', campaignId)
      // campaign-list에서 데이터 가져오기
      const response = await httpClient.get(`/campaign-list?campaignId=${campaignId}`)
      console.log('API: Raw response:', response)
      
      if (!response.data || response.data.length === 0) {
        throw new Error('Campaign not found')
      }

      // 응답 데이터 변환
      const campaign = response.data[0]
      return {
        campaignImg: campaign.campaignImg,
        campaignName: campaign.campaignName,
        companyName: campaign.companyName,
        campaignDesc: campaign.campaignDesc,
        campaignDeadline: campaign.campaignDeadline,
        campaignCondition: campaign.campaignCondition,
        campaignCategory: campaign.campaignCategory
      }
    } catch (error) {
      console.error('Error fetching my campaign detail:', error)
      throw error
    }
  },

  // 마이페이지 - 캠페인 등록
  registerCampaign: async (campaignData) => {
    try {
      const response = await httpClient.post('/v1/api/mypage/campaign-register', campaignData)
      return response.data
    } catch (error) {
      console.error('Error registering campaign:', error)
      throw error
    }
  },

  // 마이페이지 - 계약서 목록 조회
  getContractList: async (params = {}) => {
    try {
      const response = await httpClient.get('/v1/api/contracts', {
        params: {
          _page: params._page || 1,
          _limit: params._limit || 10,
          _sort: params._sort || 'createdAt',
          _order: params._order || 'desc'
        }
      })
      return {
        contracts: response.data,
        totalItems: parseInt(response.headers['x-total-count'] || '0')
      }
    } catch (error) {
      console.error('Error fetching contract list:', error)
      return {
        contracts: [],
        totalItems: 0
      }
    }
  },

  // 계약서 상세 조회
  getContract: async (contractId) => {
    try {
      const response = await httpClient.get(`/v1/api/contracts/${contractId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching contract:', error)
      throw error
    }
  }
} 