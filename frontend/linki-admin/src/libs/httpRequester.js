import axios from "axios";

const instance = axios.create({
    timeout: 3000, // 3초 타임아웃 설정
});

// 인터셉터(응답 시)
instance.interceptors.response.use((res) => {
    return res;
}, (err) => {
    if (!err.response) {
        window.alert("네트워크 연결을 확인해주세요.");
        return Promise.reject(err);
    }

    switch (err.response.status) {
        case 400:
            window.alert("잘못된 요청입니다.");
            break;
        case 403:
            window.alert("접근이 거부되었습니다.");
            break;
        case 404:
            window.alert("요청한 리소스를 찾을 수 없습니다.");
            break;
        case 408:
            window.alert("요청 시간이 초과되었습니다.");
            break;
        case 409:
            window.alert("요청이 충돌했습니다.");
            break;
        case 429:
            window.alert("너무 많은 요청이 발생했습니다. 잠시 후 다시 시도해주세요.");
            break;
        case 500:
            window.alert("서버 오류가 발생했습니다. 관리자에게 문의해주세요.");
            break;
        case 502:
            window.alert("서버 게이트웨이 오류가 발생했습니다.");
            break;
        case 503:
            window.alert("서비스가 일시적으로 사용할 수 없습니다.");
            break;
        case 504:
            window.alert("게이트웨이 시간 초과가 발생했습니다.");
            break;
        default:
            window.alert("알 수 없는 오류가 발생했습니다.");
            break;
    }
    return Promise.reject(err);
});

export default {
    get(url, params) {
        return instance.get(url, { params });
    },
    post(url, params) {
        return instance.post(url, params);
    },
    put(url, params) {
        return instance.put(url, params);
    },
    delete(url) {
        return instance.delete(url);
    },
};


