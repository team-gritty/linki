import httpClient from '@/utils/httpRequest'

export const getSubscriberUserList = async (page, size) => {
  return await httpClient.get('/v1/admin/api/subscriberUsers')
}

export const searchSubscriberUser = async (searchType, keyword) => {
  // 스프링 연동 시 사용할 코드
  return await httpClient.post('/v1/admin/api/subscriberUsers/search', {
    searchType,
    keyword
  })

  // json-server 테스트용 코드
  // return await httpClient.get(`/v1/admin/api/subscriberUsers/search/${searchType}?q=${keyword}`)
}

export async function exportExcel() {
  const res = await httpClient.post('/v1/admin/api/subscriberUsers/exportExcel');
  const downloadUrl = res.data;
  window.open(downloadUrl);
}

// Keyset 페이지네이션 기본 조회
export const getSubscriberUserListWithKeyset = async (cursor = null, size = 10) => {
  const params = { size }
  if (cursor) {
    params.cursor = cursor
  }
  return await httpClient.get('/v1/admin/api/subscriberUsers', params)
}

// Keyset 페이지네이션 검색
export const searchSubscriberUserWithKeyset = async (searchType, keyword, cursor = null, size = 10) => {
  const requestBody = {
    searchType,
    keyword,
    cursor,
    size
  }
  return await httpClient.post('/v1/admin/api/subscriberUsers/search', requestBody)
}