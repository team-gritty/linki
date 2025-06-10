<script setup>
import InfluencerCategory from '@/components/home/InfluencerCategory.vue'
import Banner from '@/components/home/Banner.vue'
import CampaignCategory from '@/components/home/CampaignCategory.vue'
import CampaignSection from '@/components/home/CampaignSection.vue'
import InfluencerSection from '@/components/home/InfluencerSection.vue'
</script>

<template>
  <div class="home">
    <InfluencerCategory />
    <div class="main-content">
      <Banner />
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

