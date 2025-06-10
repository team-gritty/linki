<template>
  <div class="header-container">
    <div class="campaign-summary-box" v-if="campaignDetail">
      <div class="summary-left">
        <img :src="campaignDetail.campaign_img" :alt="campaignDetail.campaign_name" class="summary-thumb">
        <div class="summary-info">
          <h2 class="summary-title">{{ campaignDetail.campaign_name }}</h2>
          <p class="summary-sub">{{ campaignDetail.campaign_desc }}</p>
        </div>
      </div>
      <div class="header-buttons">
        <button class="go-list-btn" @click="goToProposalList">
          제안서 목록
        </button>
        <button class="go-list-btn" @click="goToCampaigns">
          캠페인 목록
        </button>
      </div>
    </div>
    <div v-else class="campaign-summary-box">
      <div class="summary-left">
        <div class="summary-info">
          <h2 class="summary-title">데이터 로딩중...</h2>
        </div>
      </div>
      <div class="header-buttons">
        <button class="go-list-btn" @click="goToProposalList">
          제안서 목록
        </button>
      </div>
    </div>

    <div class="campaign-tabs">
      <button 
        v-for="tab in tabs" 
        :key="tab.id"
        :class="['tab', { active: currentTab === tab.id }]"
        @click="$emit('update:currentTab', tab.id)"
      >
        {{ tab.name }}
      </button>
      <div class="tab-underline"></div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'DetailHeader',
  props: {
    campaignDetail: {
      type: Object,
      required: false,
      default: null
    },
    currentTab: {
      type: String,
      required: true
    },
    tabs: {
      type: Array,
      required: true,
      default: () => [
        { id: 'campaign', name: '캠페인내용' },
        { id: 'proposal', name: '제안서' },
        { id: 'chat', name: '채팅' },
        { id: 'contract', name: '계약서' }
      ]
    }
  },
  setup() {
    const router = useRouter()

    const goToCampaigns = () => {
      router.push('/campaigns')
    }

    return {
      goToCampaigns
    }
  },
  methods: {
    goToProposalList() {
      this.$emit('go-to-proposal-list');
    }
  }
}
</script> 