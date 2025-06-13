<script setup>
import Banner from '@/components/home/Banner.vue'
import CampaignCategory from '@/components/home/CampaignCategory.vue'
import CampaignSection from '@/components/home/CampaignSection.vue'
import InfluencerSection from '@/components/home/InfluencerSection.vue'
</script>

<template>
  <Banner />
  <div class="home">
    <div class="main-content">
      <div class="section-divider"></div>
      <CampaignCategory />
      <div class="section-divider"></div>
      <CampaignSection />
      <div class="section-divider"></div>
      <InfluencerSection />
    </div>
  </div>
</template>

<style>
@import '@/assets/css/home.css';
</style>

