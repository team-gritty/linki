// 구독 관리 mock API
export function getMySubscriptionInfo() {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        data: {
          startDate: '2024-06-01',
          nextBillingDate: '2024-07-01',
          paymentMethod: '신한카드 (****-1234)',
          status: 'active' // active, paused, canceled 등
        }
      })
    }, 500)
  })
} 