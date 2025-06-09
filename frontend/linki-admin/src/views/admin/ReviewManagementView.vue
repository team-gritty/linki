<template>
  <div class="review-management-container">
    <h2>리뷰 관리</h2>
    <div class="search-section">
      <div class="search-bar">
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="작성자, 내용으로 검색"
          @input="handleSearch"
        >
        <button class="search-btn" @click="handleSearch">검색</button>
      </div>
      <div class="filter-section">
        <select v-model="selectedType" @change="handleSearch">
          <option value="ALL">전체</option>
          <option value="INFLUENCER">인플루언서</option>
          <option value="ADVERTISER">광고주</option>
        </select>
        <select v-model="selectedStatus" @change="handleSearch">
          <option value="ALL">전체</option>
          <option value="PENDING">대기중</option>
          <option value="APPROVED">승인</option>
          <option value="REJECTED">거절</option>
        </select>
      </div>
    </div>

    <div class="table-container">
      <table class="review-table">
        <thead>
          <tr>
            <th>작성자</th>
            <th>유형</th>
            <th>내용</th>
            <th>평점</th>
            <th>작성일</th>
            <th>상태</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="review in pagedReviews" :key="review.id">
            <td>{{ review.writer }}</td>
            <td>{{ getTypeText(review.type) }}</td>
            <td>{{ review.content }}</td>
            <td>{{ review.rating }}</td>
            <td>{{ review.createdAt }}</td>
            <td>
              <span :class="['status', review.status.toLowerCase()]">
                {{ getStatusText(review.status) }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button 
                  v-if="review.status === 'PENDING'"
                  class="approve-btn" 
                  @click="handleApprove(review.id)"
                >
                  승인
                </button>
                <button 
                  v-if="review.status === 'PENDING'"
                  class="reject-btn" 
                  @click="handleReject(review.id)"
                >
                  거절
                </button>
                <button 
                  class="delete-btn" 
                  @click="handleDelete(review.id)"
                >
                  삭제
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination">
      <button 
        :disabled="currentPage === 1" 
        @click="currentPage--"
      >
        이전
      </button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <button 
        :disabled="currentPage === totalPages" 
        @click="currentPage++"
      >
        다음
      </button>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { getReviewList, approveReview, rejectReview, deleteReview } from '@/js/Review'

export default {
  name: 'ReviewManagementView',
  setup() {
    const reviews = ref([])
    const searchKeyword = ref('')
    const selectedType = ref('ALL')
    const selectedStatus = ref('ALL')
    const currentPage = ref(1)
    const itemsPerPage = 10

    const pagedReviews = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage
      const end = start + itemsPerPage
      return reviews.value.slice(start, end)
    })

    const totalPages = computed(() => 
      Math.ceil(reviews.value.length / itemsPerPage)
    )

    const getTypeText = (type) => {
      const typeMap = {
        'INFLUENCER': '인플루언서',
        'ADVERTISER': '광고주'
      }
      return typeMap[type] || type
    }

    const getStatusText = (status) => {
      const statusMap = {
        'PENDING': '대기중',
        'APPROVED': '승인',
        'REJECTED': '거절'
      }
      return statusMap[status] || status
    }

    const handleSearch = async () => {
      try {
        const response = await getReviewList({
          keyword: searchKeyword.value,
          type: selectedType.value,
          status: selectedStatus.value
        })
        reviews.value = response.data
      } catch (error) {
        console.error('리뷰 목록 조회 실패:', error)
      }
    }

    const handleApprove = async (reviewId) => {
      try {
        await approveReview(reviewId)
        await handleSearch()
      } catch (error) {
        console.error('리뷰 승인 실패:', error)
      }
    }

    const handleReject = async (reviewId) => {
      try {
        await rejectReview(reviewId)
        await handleSearch()
      } catch (error) {
        console.error('리뷰 거절 실패:', error)
      }
    }

    const handleDelete = async (reviewId) => {
      if (!confirm('정말로 이 리뷰를 삭제하시겠습니까?')) return
      
      try {
        await deleteReview(reviewId)
        await handleSearch()
      } catch (error) {
        console.error('리뷰 삭제 실패:', error)
      }
    }

    // 초기 데이터 로드
    handleSearch()

    return {
      reviews,
      searchKeyword,
      selectedType,
      selectedStatus,
      currentPage,
      pagedReviews,
      totalPages,
      getTypeText,
      getStatusText,
      handleSearch,
      handleApprove,
      handleReject,
      handleDelete
    }
  }
}
</script>

<style scoped>
.review-management-container {
  padding: 20px;
}

.search-section {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  gap: 10px;
}

.search-bar input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 300px;
}

.search-btn {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.filter-section {
  display: flex;
  gap: 10px;
}

.filter-section select {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.review-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.review-table th,
.review-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.review-table th {
  background-color: #f8f9fa;
  font-weight: bold;
}

.status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.9em;
}

.status.pending {
  background-color: #ffd700;
  color: #000;
}

.status.approved {
  background-color: #28a745;
  color: white;
}

.status.rejected {
  background-color: #dc3545;
  color: white;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.approve-btn,
.reject-btn,
.delete-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.approve-btn {
  background-color: #28a745;
  color: white;
}

.reject-btn {
  background-color: #dc3545;
  color: white;
}

.delete-btn {
  background-color: #6c757d;
  color: white;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  background-color: #f8f9fa;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .search-section {
    flex-direction: column;
    gap: 10px;
  }

  .search-bar input {
    width: 100%;
  }

  .filter-section {
    flex-direction: column;
  }

  .review-table {
    display: block;
    overflow-x: auto;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style> 