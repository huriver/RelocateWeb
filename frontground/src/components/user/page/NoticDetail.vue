<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
const item = JSON.parse(decodeURIComponent(route.params.item))
</script>

<template>
  <div class="notic-detail-container home-container">
    111
  </div>
</template>

<style scoped></style>