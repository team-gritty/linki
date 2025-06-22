<template>
  <div class="campaign-detail-wrap">
    <div class="campaign-detail-main">
      <div v-if="loading" class="loading-state">
        <p>데이터 로딩중...</p>
      </div>
      <div v-else-if="campaignDetail" class="campaign-content">
        <div class="detail-img-box">
          <img class="main-img" :src="campaignDetail.campaignImg" alt="캠페인 메인 이미지" />
        </div>
        <div class="detail-info-box">
          <div class="detail-title">캠페인 설명</div>
          <div class="detail-desc">
            {{ campaignDetail.campaignDesc }}
          </div>
          <div class="detail-meta-service">
            <div class="meta-row-service">
              <span class="meta-label">광고 신청 마감일</span>
              <span class="meta-value">{{ formatDate(campaignDetail.campaignDeadline) }}</span>
            </div>
            <div class="meta-row-service">
              <span class="meta-label">광고 조건</span>
              <span class="meta-value">{{ campaignDetail.campaignCondition }}</span>
            </div>
            <div class="meta-row-service">
              <span class="meta-label">카테고리</span>
              <span class="meta-value">{{ campaignDetail.campaignCategory }}</span>
            </div>
            <div class="meta-row-service">
              <span class="meta-label">브랜드</span>
              <span class="meta-value">{{ campaignDetail.companyName }}</span>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="no-data">
        <p>캠페인 정보를 찾을 수 없습니다.</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { proposalAPI } from '@/api/proposal';
import { campaignAPI } from '@/api/campaign';

export default {
  name: 'DetailCampaign',

  setup() {
    const route = useRoute();
    const loading = ref(true);
    const campaignDetail = ref(null);

    // 캠페인 상세 정보 가져오기
    const fetchCampaignDetail = async () => {
      try {
        loading.value = true;
        const proposalId = route.params.id;
        console.log('Fetching campaign detail for proposal:', proposalId);
        
        // 1. 제안서 정보를 통해 캠페인 ID 가져오기
        const proposalResponse = await proposalAPI.getProposalDetail(proposalId);
        console.log('Fetched proposal:', proposalResponse);
        
        const campaignId = proposalResponse.campaignId;
        if (!campaignId) {
          throw new Error('캠페인 ID를 찾을 수 없습니다.');
        }
        
        // 2. 캠페인 ID로 캠페인 상세 정보 조회
        const campaignResponse = await campaignAPI.getCampaignDetail(campaignId);
        console.log('Fetched campaign detail:', campaignResponse);
        
        campaignDetail.value = campaignResponse;
        
      } catch (error) {
        console.error('Failed to fetch campaign detail:', error);
        campaignDetail.value = null;
      } finally {
        loading.value = false;
      }
    };

    onMounted(() => {
      fetchCampaignDetail();
    });

    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    };

    return {
      loading,
      campaignDetail,
      formatDate
    };
  }
};
</script>

<style scoped>
@import '@/assets/css/detail.css';

.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  font-size: 16px;
  color: #666;
}

.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  color: #666;
  font-size: 16px;
}

.campaign-detail-main {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-top: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.campaign-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-img-box {
  width: 100%;
  max-height: 400px;
  overflow: hidden;
  border-radius: 8px;
}

.main-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-info-box {
  padding: 20px;
}

.detail-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #1a1a1a;
}

.detail-desc {
  font-size: 16px;
  line-height: 1.6;
  color: #4b5563;
  margin-bottom: 24px;
  white-space: pre-wrap;
}

.detail-meta-service {
  display: grid;
  gap: 16px;
}

.meta-row-service {
  display: flex;
  align-items: center;
}

.meta-label {
  width: 140px;
  font-size: 14px;
  color: #666;
}

.meta-value {
  font-size: 14px;
  color: #1a1a1a;
  font-weight: 500;
}
</style> 