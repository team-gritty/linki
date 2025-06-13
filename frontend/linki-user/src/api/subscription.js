import axios from 'axios'

// 구독 정보 조회
export const getMySubscriptionInfo = async () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        data: {
          id: 1,
          plan: 'premium',
          startDate: '2024-03-01',
          endDate: '2024-04-01',
          nextBillingDate: '2024-04-01',
          paymentMethod: '신용카드',
          contractCount: 5,
          status: 'active',
          price: 99000,
          features: [
            '무제한 인플루언서 연결',
            '실시간 분석 리포트',
            '우선 매칭 서비스',
            '전문가 상담 서비스'
          ]
        }
      });
    }, 500);
  });
};

// 구독 환불 신청
export const requestSubscriptionRefund = async (data) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        data: {
          success: true,
          refundId: 'REF' + Date.now(),
          amount: data.amount,
          status: 'pending',
          message: '환불 신청이 접수되었습니다.'
        }
      });
    }, 500);
  });
}; 