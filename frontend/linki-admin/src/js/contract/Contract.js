import httpRequester from '@/libs/httpRequester.js'

export const getContractList = async (page, size) => {
  return await httpRequester.get('/v1/admin/api/contracts')
}

export const searchContract = async (searchType, keyword) => {
  // 스프링 연동 시 사용할 코드
  return await httpRequester.post('/v1/admin/api/contracts/search', {
    searchType,
    keyword
  })

  // json-server 테스트용 코드
  // return await httpRequester.get(`/v1/admin/api/contracts/search/${searchType}?q=${keyword}`)
}

// Keyset 페이지네이션 기본 조회
export const getContractListWithKeyset = async (cursor = null, size = 10) => {
  const params = { size }
  if (cursor) {
    params.cursor = cursor
  }
  
  return await httpRequester.get('/v1/admin/api/contracts', { params })
}

// Keyset 페이지네이션 검색
export const searchContractWithKeyset = async (searchType, keyword, cursor = null, size = 10) => {
  const requestBody = {
    searchType,
    keyword,
    cursor,
    size
  }
  
  return await httpRequester.post('/v1/admin/api/contracts/search', requestBody)
}

export async function exportExcel() {
  const res = await httpRequester.post('/v1/admin/api/contracts/exportExcel');
  const downloadUrl = res.data;
  window.open(downloadUrl);
}