import axios from "axios";
import { useAccountStore } from "@/stores/account";

const instance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api', // API 기본 URL 설정
    timeout: 15000, // 타임아웃 설정 (15초)
    withCredentials: true // 쿠키 포함 설정
});

// 요청 인터셉터
instance.interceptors.request.use(
    (config) => {
        // 요청 보내기 전 수행할 작업
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 응답 인터셉터
instance.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
        const { response, config } = error;

        if (!response) {
            window.alert("네트워크 연결을 확인해주세요.");
            return Promise.reject(error);
        }

        switch (response.status) {
            case 400:
                window.alert("잘못된 요청입니다.");
                break;

            case 401:
                if (config.retried) {
                    window.alert("권한이 없습니다.");
                    window.location.replace("/");
                    return Promise.reject(error);
                }

                try {
                    // 리프레시 토큰으로 새 액세스 토큰 요청
                    const res = await axios.get("/v1/api/account/token");
                    const accessToken = res.data;

                    // 계정 스토어 업데이트
                    const accountStore = useAccountStore();
                    accountStore.setAccessToken(accessToken);

                    // 원래 요청 재시도
                    config.headers.authorization = `Bearer ${accessToken}`;
                    config.retried = true;
                    return instance(config);
                } catch (refreshError) {
                    window.alert("세션이 만료되었습니다. 다시 로그인해주세요.");
                    window.location.replace("/login");
                    return Promise.reject(refreshError);
                }

            case 403:
                window.alert("접근 권한이 없습니다.");
                break;

            case 404:
                window.alert("요청하신 리소스를 찾을 수 없습니다.");
                break;

            case 500:
                window.alert("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
                break;

            default:
                window.alert("오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
                break;
        }

        return Promise.reject(error);
    }
);

// HTTP 요청 설정 생성
const generateConfig = () => {
    const accountStore = useAccountStore();
    
    const config = {
        headers: {}
    };

    if (accountStore.accessToken) {
        config.headers.authorization = `Bearer ${accountStore.accessToken}`;
    }

    return config;
};

// HTTP 클라이언트 메서드
const httpClient = {
    /**
     * GET 요청
     * @param {string} url - 요청 URL
     * @param {Object} [params] - URL 쿼리 파라미터
     * @returns {Promise} 
     */
    get(url, params) {
        const config = generateConfig();
        config.params = params;
        return instance.get(url, config);
    },

    /**
     * POST 요청
     * @param {string} url - 요청 URL
     * @param {Object} data - 요청 바디 데이터
     * @returns {Promise}
     */
    post(url, data) {
        return instance.post(url, data, generateConfig());
    },

    /**
     * PUT 요청
     * @param {string} url - 요청 URL
     * @param {Object} data - 요청 바디 데이터
     * @returns {Promise}
     */
    put(url, data) {
        return instance.put(url, data, generateConfig());
    },

    /**
     * DELETE 요청
     * @param {string} url - 요청 URL
     * @returns {Promise}
     */
    delete(url) {
        return instance.delete(url, generateConfig());
    },

    /**
     * PATCH 요청
     * @param {string} url - 요청 URL
     * @param {Object} data - 요청 바디 데이터
     * @returns {Promise}
     */
    patch(url, data) {
        return instance.patch(url, data, generateConfig());
    }
};

export default httpClient; 