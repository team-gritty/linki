import httpClient from '@/utils/httpRequest'

// 기존 API (호환성 유지)
export const getCampaignList = async (page, size) => {
  return await httpClient.get('/v1/admin/api/campaigns')
}

// Keyset 페이지네이션 기본 조회
export const getCampaignListWithKeyset = async (cursor = null, size = 10) => {
  const params = { size }
  if (cursor) {
    params.cursor = cursor
  }
  
  return await httpClient.get('/v1/admin/api/campaigns', params)
}

// Keyset 페이지네이션 검색
export const searchCampaignWithKeyset = async (searchType, keyword, cursor = null, size = 10) => {
  const requestBody = {
    searchType,
    keyword,
    cursor,
    size
  }
  
  return await httpClient.post('/v1/admin/api/campaigns/search', requestBody)
}

export const searchCampaign = async (searchType, keyword) => {
  // 스프링 연동 시 사용할 코드
  return await httpClient.post('/v1/admin/api/campaigns/search', {
    searchType,
    keyword
  })

  // json-server 테스트용 코드
  // return await httpClient.get(`/v1/admin/api/campaigns/search/${searchType}?q=${keyword}`)
}

export async function exportExcel() {
  const res = await httpClient.post('/v1/admin/api/campaigns/exportExcel');
  const downloadUrl = res.data;
  window.open(downloadUrl);
}