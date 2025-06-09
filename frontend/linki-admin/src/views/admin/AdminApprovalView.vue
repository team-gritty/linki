<template>
  <div class="admin-approval-container">
    <h2>관리자 가입 승인</h2>
    <div class="search-section">
      <div class="search-bar">
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="이름, 이메일, 전화번호로 검색"
          @input="handleSearch"
        >
        <button class="search-btn" @click="handleSearch">검색</button>
      </div>
      <div class="filter-section">
        <select v-model="selectedStatus" @change="handleSearch">
          <option value="ALL">전체</option>
          <option value="PENDING">대기중</option>
          <option value="APPROVED">승인</option>
          <option value="REJECTED">거절</option>
        </select>
      </div>
    </div>

    <div class="table-container">
      <table class="admin-table">
        <thead>
          <tr>
            <th>이름</th>
            <th>이메일</th>
            <th>전화번호</th>
            <th>가입일</th>
            <th>상태</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="admin in pagedAdmins" :key="admin.id">
            <td>{{ admin.name }}</td>
            <td>{{ admin.email }}</td>
            <td>{{ admin.phone }}</td>
            <td>{{ admin.createdAt }}</td>
            <td>
              <span :class="['status', admin.status.toLowerCase()]">
                {{ getStatusText(admin.status) }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button 
                  v-if="admin.status === 'PENDING'"
                  class="approve-btn" 
                  @click="handleApprove(admin.id)"
                >
                  승인
                </button>
                <button 
                  v-if="admin.status === 'PENDING'"
                  class="reject-btn" 
                  @click="handleReject(admin.id)"
                >
                  거절
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
import { getAdminList, approveAdmin, rejectAdmin } from '@/js/Admin'

export default {
  name: 'AdminApprovalView',
  setup() {
    const admins = ref([])
    const searchKeyword = ref('')
    const selectedStatus = ref('ALL')
    const currentPage = ref(1)
    const itemsPerPage = 10

    const pagedAdmins = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage
      const end = start + itemsPerPage
      return admins.value.slice(start, end)
    })

    const totalPages = computed(() => 
      Math.ceil(admins.value.length / itemsPerPage)
    )

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
        const response = await getAdminList({
          keyword: searchKeyword.value,
          status: selectedStatus.value
        })
        admins.value = response.data
      } catch (error) {
        console.error('관리자 목록 조회 실패:', error)
      }
    }

    const handleApprove = async (adminId) => {
      try {
        await approveAdmin(adminId)
        await handleSearch()
      } catch (error) {
        console.error('관리자 승인 실패:', error)
      }
    }

    const handleReject = async (adminId) => {
      try {
        await rejectAdmin(adminId)
        await handleSearch()
      } catch (error) {
        console.error('관리자 거절 실패:', error)
      }
    }

    // 초기 데이터 로드
    handleSearch()

    return {
      admins,
      searchKeyword,
      selectedStatus,
      currentPage,
      pagedAdmins,
      totalPages,
      getStatusText,
      handleSearch,
      handleApprove,
      handleReject
    }
  }
}
</script>

<style scoped>
.admin-approval-container {
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

.filter-section select {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.admin-table th,
.admin-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.admin-table th {
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
.reject-btn {
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

  .admin-table {
    display: block;
    overflow-x: auto;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style> 