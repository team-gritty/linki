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
import { ref, watch } from 'vue';

export default {
  name: 'DetailCampaign',
  props: {
    detailData: {
      type: Object,
      required: true
    }
  },

  setup(props) {
    const loading = ref(true);
    const campaignDetail = ref(null);

    // detailData 변경 감지하여 캠페인 정보 설정
    watch(() => props.detailData, (newData) => {
      loading.value = true;
      console.log('DetailCampaign received data:', newData);
      
      if (newData) {
        // campaign 직접 접근 또는 campaign 속성 접근 시도
        const campaignData = newData.campaign || newData;
        if (campaignData) {
          console.log('Setting campaign detail:', campaignData);
          campaignDetail.value = campaignData;
        } else {
          console.warn('No campaign data found in:', newData);
          campaignDetail.value = null;
        }
      } else {
        console.warn('No detail data received');
        campaignDetail.value = null;
      }
      
      loading.value = false;
    }, { immediate: true });

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