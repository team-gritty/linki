import httpRequester from '@/libs/httpRequester.js'

export const getSettlementList = async (page, size) => {
  return await httpRequester.get('/v1/admin/api/settlements')
}

export const searchSettlement = async (searchType, keyword) => {
  // 스프링 연동 시 사용할 코드
  // return await httpRequester.post('/v1/admin/api/settlements/search', {
  //   searchType,
  //   keyword
  // })

  // json-server 테스트용 코드
  return await httpRequester.get(`/v1/admin/api/settlements/search/${searchType}?q=${keyword}`)
}

// Keyset 페이지네이션 기본 조회
export const getSettlementListWithKeyset = async (cursor = null, size = 10) => {
  const params = { size }
  if (cursor) {
    params.cursor = cursor
  }
  
  return await httpRequester.get('/v1/admin/api/settlements', { params })
}

// Keyset 페이지네이션 검색
export const searchSettlementWithKeyset = async (searchType, keyword, cursor = null, size = 10) => {
  const requestBody = {
    searchType,
    keyword,
    cursor,
    size
  }
  
  return await httpRequester.post('/v1/admin/api/settlements/search', requestBody)
}

export async function exportExcel() {
  const res = await httpRequester.post('/v1/admin/api/settlements/exportExcel');
  const downloadUrl = res.data;
  window.open(downloadUrl);
}

export const processSettlement = async (contractId) => {
  // 먼저 현재 데이터를 가져옵니다
  const response = await httpRequester.get(`/v1/admin/api/settlements/${contractId}`)
  const currentData = response.data

  // isSettled를 true로 설정하고 PUT 요청을 보냅니다
  return await httpRequester.put(`/v1/admin/api/settlements/${contractId}/process`, {
    ...currentData,
    isSettled: true
  })

  // 스프링 연동 시 사용할 코드
  // return await httpRequester.post(`/v1/admin/api/settlements/${contractId}/process`, {
  //   isSettled: true
  // }) 
}