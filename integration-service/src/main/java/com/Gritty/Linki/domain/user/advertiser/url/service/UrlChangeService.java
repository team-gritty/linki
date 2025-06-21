package com.Gritty.Linki.domain.user.advertiser.url.service;


public interface UrlChangeService {

    /**
     * 광고주의 origin URL을 전달받아, 고유한 redirect URL을 생성하여 저장합니다.
     * 이후 외부 API에서 사용할 수 있도록 redirect URL을 반환합니다.
     *
     * @param userId redirect URL을 생성할 대상 유저 ID
     * @param originUrl 원본 URL
     * @return 생성된 redirect URL
     */
    String changeUrl(String userId,String originUrl);

    /**
     * 주어진 redirect URL에 해당하는 RedirectLinks 엔티티에 계약서 ID를 연결합니다.
     *
     * @param contractId 연결할 계약서의 ID
     * @param url 계약서 ID를 연결할 대상 redirect URL
     */
    void saveContractId(String contractId,String url);
}
