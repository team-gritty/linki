import httpRequest from "@/utils/httpRequest";

export const check = () => {
  // 로그인 체크 API 엔드포인트에 맞게 수정
  return httpRequest.get("/v1/api/nonuser/check");
}; 