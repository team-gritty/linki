import httpClient from '@/utils/httpRequest'

const campaignApi = {
   /**
   * 마이페이지 - 캠페인 목록 조회
   * @returns 
   */
   getMyPageCampaigns: async () => {
    try {
      const response = await httpClient.get('/v1/api/advertiser/mypage/campaigns')
      return response.data
    } catch (error) {
      console.error('Error fetching mypage campaigns:', error)
      throw error
    }
  },

  // 캠페인 상세 정보 조회
  getCampaignDetail: async (campaignId) => {
    try {
      const response = await httpClient.get(`/v1/api/advertiser/mypage/campaigns/${campaignId}`)
      return Array.isArray(response.data) ? response.data[0] : response.data
    } catch (error) {
      console.error('Error fetching campaign detail:', error)
      throw error
    }
  },

  // 광고주 리뷰 조회
  getAdvertiserReviews: async (advertiserId) => {
    try {
      const response = await httpClient.get(`/v1/api/advertiser/mypage/reviews/received`, {
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


  // 마이페이지 - 캠페인 상세 정보 조회
  getMyCampaignDetail: async (campaignId) => {
    try {
      console.log('API: Fetching campaign detail for ID:', campaignId)
      
      // 쿼리 파라미터 방식으로 호출하면 배열이 반환됨
      const response = await httpClient.get(`/v1/api/advertiser/campaigns/${campaignId}`)
      console.log('API: Raw response:', response)
      
      if (!response.data || response.data.length === 0) {
        throw new Error('Campaign not found')
      }

      // 쿼리 파라미터 방식이므로 배열의 첫 번째 요소를 사용
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
      const response = await httpClient.post('/v1/api/advertiser/mypage/campaigns', campaignData)
      return response.data
    } catch (error) {
      console.error('Error registering campaign:', error)
      throw error
    }
  },

 
 

  // 캠페인 수정
  updateCampaign: async (campaignId, campaignData) => {
    console.log("PUT 요청 왔습니다---------", campaignId, campaignData)
    try {
      const response = await httpClient.put(`/v1/api/advertiser/mypage/campaigns/${campaignId}`, campaignData)
      return response.data
    } catch (error) {
      console.error('Error updating campaign:', error)
      throw error
    }
  },

  // 캠페인 상태 업데이트 (단일 캠페인용)
  updateCampaignStatus: async (campaignId, statusData) => {
    try {
      // ACTIVE/HIDDEN 상태를 makePublic boolean으로 변환
      const makePublic = statusData.campaignStatus === 'ACTIVE'
      
      const response = await httpClient.patch(`/v1/api/advertiser/mypage/campaigns/visibility`, 
        [campaignId], // 캠페인 ID 배열로 전송
        {
          params: { makePublic }
        }
      )
      return response.data
    } catch (error) {
      console.error('Error updating campaign status:', error)
      throw error
    }
  },



}

export default campaignApi
