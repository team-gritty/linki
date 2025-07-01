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
      <CampaignCategory />
      <div class="section-divider"></div>
      <InfluencerSection />
      <div class="section-divider"></div>
      <CampaignSection />
    </div>
  </div>
</template>

<style>
@import '@/assets/css/home.css';
</style>

