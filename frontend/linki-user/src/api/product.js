// 상품 추천 mock API
export function getRecommendedProducts() {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        data: [
          {
            id: 1,
            name: 'The north coat',
            desc: '',
            img: 'https://dummyimage.com/300x300/eee/aaa&text=Coat',
            category: '패션',
            price: '$260'
          },
          {
            id: 2,
            name: 'Gucci duffle bag',
            desc: '',
            img: 'https://dummyimage.com/300x300/eee/aaa&text=Bag',
            category: '패션',
            price: '$960'
          },
          {
            id: 3,
            name: 'RGB Liquid CPU Cooler',
            desc: '',
            img: 'https://dummyimage.com/300x300/eee/aaa&text=Cooler',
            category: '전자기기',
            price: '$160'
          },
          {
            id: 4,
            name: 'Small BookSelf',
            desc: '',
            img: 'https://dummyimage.com/300x300/eee/aaa&text=BookSelf',
            category: '가구',
            price: '$360'
          }
        ]
      })
    }, 500)
  })
} 