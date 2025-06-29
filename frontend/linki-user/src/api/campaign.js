import httpClient from '@/utils/httpRequest'

export const campaignAPI = {
  // 카테고리 목록 조회 - 카테고리별 캠페인
  getCampaignsByCategory: async (category) => {
    try {
      const response = await httpClient.get(`/v1/api/nonuser/campaigns/categories/${category}`)
      return response.data
    } catch (error) {
      console.error('Error fetching campaigns by category:', error)
      return []
    }
  },

  // 캠페인 목록 조회 (페이지네이션)
  getCampaignsWithPagination: async (params = {}) => {
    try {
      // 프론트엔드에서는 1-based, Spring에서는 0-based
      const page = params._page ? params._page - 1 : 0; 
      const size = params._limit || 15;
      const sortBy = params._sort || 'createdAt';
      const sortDir = params._order || 'desc';

      console.log('Original params._page:', params._page, 'Converted page:', page);

      // 카테고리별 조회인지 전체 조회인지 구분
      const isCategory = params.campaignCategory && params.campaignCategory !== '전체';
      const url = isCategory 
        ? `/v1/api/nonuser/campaigns/categories/${params.campaignCategory}/page`
        : '/v1/api/nonuser/campaigns/page';

      const queryParams = {
        page: page,
        size: size,
        sortBy: sortBy,
        sortDir: sortDir
      };

      console.log('=== API 요청 디버깅 ===');
      console.log('1. 원본 params:', params);
      console.log('2. 변환된 값들:', { 
        'params._page': params._page, 
        'converted page': page, 
        'size': size, 
        'sortBy': sortBy, 
        'sortDir': sortDir 
      });
      console.log('3. 최종 queryParams:', queryParams);
      console.log('4. 요청 URL:', url);
      
      // URL에 직접 쿼리 파라미터 포함
      const queryString = new URLSearchParams(queryParams).toString();
      const fullUrl = `${url}?${queryString}`;
      console.log('5. Full URL:', fullUrl);

      const response = await httpClient.get(fullUrl);
      const pageData = response.data;

      console.log('Page data from backend:', {
        number: pageData.number,
        totalPages: pageData.totalPages,
        totalElements: pageData.totalElements,
        first: pageData.first,
        last: pageData.last
      });

      return {
        campaigns: pageData.content,
        totalItems: pageData.totalElements,
        totalPages: pageData.totalPages,
        currentPage: pageData.number + 1, // 프론트엔드에서는 1부터 시작
        hasNext: !pageData.last,
        hasPrevious: !pageData.first
      };
    } catch (error) {
      console.error('Error fetching campaigns with pagination:', error);
      return {
        campaigns: [],
        totalItems: 0,
        totalPages: 0,
        currentPage: 1,
        hasNext: false,
        hasPrevious: false
      };
    }
  },

  // 캠페인 목록 조회 (기존 방식 - 호환성 유지)
  getCampaigns: async (params = {}) => {
    try {
      // 카테고리가 선택된 경우 카테고리별 전용 API 사용
      if (params.campaignCategory && params.campaignCategory !== '전체') {
        console.log('Fetching campaigns by category:', params.campaignCategory) // 디버깅용
        const response = await httpClient.get(`/v1/api/nonuser/campaigns/categories/${params.campaignCategory}`)

        let allCampaigns = response.data;
        console.log('Received campaigns by category:', allCampaigns.length) // 디버깅용

        // 정렬 처리
        if (params._sort) {
          allCampaigns.sort((a, b) => {
            let aValue = a[params._sort];
            let bValue = b[params._sort];

            // 날짜 정렬
            if (params._sort === 'createdAt' || params._sort === 'campaignDeadline') {
              aValue = new Date(aValue);
              bValue = new Date(bValue);
            }

            if (params._order === 'desc') {
              return bValue > aValue ? 1 : -1;
            } else {
              return aValue > bValue ? 1 : -1;
            }
          });
        }

        // 프론트엔드에서 페이지네이션을 처리하므로 모든 데이터 반환
        return {
          campaigns: allCampaigns,
          totalItems: allCampaigns.length
        }
      }

      // 전체 캠페인 조회
      const queryParams = {
        _page: params._page || 1,
        _limit: params._limit || 15,
        _sort: params._sort || 'createdAt',
        _order: params._order || 'desc'
      }

      console.log('Fetching all campaigns with params:', queryParams) // 디버깅용

      const response = await httpClient.get('/v1/api/nonuser/campaigns', {
        params: queryParams
      })

      let campaigns = response.data;
      const totalCount = parseInt(response.headers['x-total-count']) || campaigns.length;
      
      console.log('Received all campaigns:', campaigns.length) // 디버깅용

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
      return response.data;
    } catch (error) {
      console.error('Error fetching campaign detail:', error)
      throw error
    }
  },

  // 인플루언서용 캠페인 상세 정보 조회 (인증 필요)
  getInfluencerCampaignDetail: async (campaignId) => {
    try {
      const response = await httpClient.get(`/v1/api/influencer/campaigns/proposals/${campaignId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching influencer campaign detail:', error)
      throw error
    }
  },

  submitProposal: async (id, contents) => {
    return await httpClient.post(`/v1/api/influencer/campaigns/${id}/proposal`, { contents })
  },
  // 캠페인별 광고주 리뷰 조회
  getAdvertiserReviewsByCampaign: async (campaignId) => {
    try {
      const response = await httpClient.get(`/v1/api/nonuser/reviews/advertiser/campaign/${campaignId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching advertiser reviews by campaign:', error)
      return []
    }
  },

 



  // 계약서 상세 조회
  // getContract: async (contractId) => {
  //   try {
  //     const response = await httpClient.get(`/v1/api/contracts/${contractId}`)
  //     return response.data
  //   } catch (error) {
  //     console.error('Error fetching contract:', error)
  //     throw error
  //   }
  // },

  // 카테고리 목록 조회
  getCategories: async () => {
    try {
      const response = await httpClient.get('/v1/api/nonuser/categories');
      console.log('Categories from API:', response.data); // 디버깅용

      // 백엔드에서 받은 데이터를 프론트엔드 형식에 맞게 변환
      return response.data.map(category => ({
        id: category.name,        // enum 이름 (BEAUTY, FASHION 등) - API 요청에 사용
        name: category.displayName || category.name  // 한국어 표시명 (뷰티, 패션 등) - 화면에 표시
      }));
    } catch (error) {
      console.error('Error fetching categories:', error)

      // API 실패 시 폴백으로 하드코딩된 카테고리 사용
      const fallbackCategories = [
        { id: 'BEAUTY', name: '뷰티' },
        { id: 'FASHION', name: '패션' },
        { id: 'FOOD', name: '음식' },
        { id: 'TRAVEL', name: '여행' },
        { id: 'VLOG', name: '브이로그' },
        { id: 'MUSIC', name: '음악' },
        { id: 'SPORTS', name: '스포츠' },
        { id: 'EDUCATION', name: '교육' },
        { id: 'ENTERTAINMENT', name: '엔터테인먼트' },
        { id: 'ELECTRONICS', name: '전자제품' },
        { id: 'ANIMAL', name: '동물' }
      ];

      return fallbackCategories;
    }
  }
} 