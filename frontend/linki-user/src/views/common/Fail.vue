<template>
  <div></div>
</template>

<script>
export default {
  name: 'PaymentFail',
  mounted() {
    const message = this.$route.query.message
      ? decodeURIComponent(this.$route.query.message)
      : '카드 등록에 실패했습니다. 다시 시도해주세요.'
    alert(message)
    this.$router.back()
  }
}
</script> 