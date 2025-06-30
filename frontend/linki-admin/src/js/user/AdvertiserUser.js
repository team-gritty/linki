import httpClient from '@/utils/httpRequest'

export const getAdvertiserUserList = async (page, size) => {
  return await httpClient.get('/v1/admin/api/advertiserUsers')
}

export const searchAdvertiserUser = async (searchType, keyword) => {
  // 스프링 연동 시 사용할 코드
  return await httpClient.post('/v1/admin/api/advertiserUsers/search', {
    searchType,
    keyword
  })

  // json-server 테스트용 코드
  // return await httpClient.get(`/v1/admin/api/advertiserUsers/search/${searchType}?q=${keyword}`)
}

export async function exportExcel() {
  const res = await httpClient.post('/v1/admin/api/advertiserUsers/exportExcel');
  const downloadUrl = res.data;
  window.open(downloadUrl);
}

// Keyset 페이지네이션 기본 조회
export const getAdvertiserUserListWithKeyset = async (cursor = null, size = 10) => {
  const params = { size }
  if (cursor) {
    params.cursor = cursor
  }
  return await httpClient.get('/v1/admin/api/advertiserUsers', params)
}

// Keyset 페이지네이션 검색
export const searchAdvertiserUserWithKeyset = async (searchType, keyword, cursor = null, size = 10) => {
  const requestBody = {
    searchType,
    keyword,
    cursor,
    size
  }
  return await httpClient.post('/v1/admin/api/advertiserUsers/search', requestBody)
}