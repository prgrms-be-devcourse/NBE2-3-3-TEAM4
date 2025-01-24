<script setup>
  const emit = defineEmits(['back']);

  const goBack = () => {
    emit('back');
  }
</script>

<template>
  <div class="search-header">
      <div class="back-button">
        <img src="@/assets/icons/back-arrow.png" alt="뒤로가기" @click="goBack">
      </div>
      <div class="search-bar">

      </div>
  </div>
</template>

<style>
  .search-header {
    position: fixed;
    top: 50px; /* NavigationBar 높이만큼 여백 추가 */
    left: 0;
    right: 0;
    padding: 15px;
    background-color: white;
    display: flex;
    align-items: center;
    z-index: 100;
    border-bottom: 1px solid #f0f0f0;
  }
  .back-button {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .back-button img {
    cursor: pointer;
  }

  .search-bar {
    flex: 1;
    margin-left: 5px;
    margin-right: 5px;
  }

  .search-bar input {
    width: 100%;
    padding: 13px 15px;
    border: 0;
    border-radius: 10px;
    font-size: 15px;
    background-color: #f5f5f5;
  }

  .search-bar input:focus {
      outline: none;
  }

  </style>
