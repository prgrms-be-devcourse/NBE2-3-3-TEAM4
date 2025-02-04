<script setup>
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const errorCode = route.query.code || "알 수 없음";
const errorMessage = route.query.message || "결제에 실패했습니다.";

const goHome = () => {
  router.push('/');
};

const closeWindow = () => {
  if (window.opener) {
    window.close();
  } else {
    goHome();
  }
};
</script>

<template>
  <div class="fail-container">
    <h2>❌ 결제 실패</h2>
    <p id="code">에러코드: {{ errorCode }}</p>
    <p id="message">실패 사유: {{ errorMessage }}</p>

    <div class="button-group">
      <button class="home-button" @click="goHome">홈으로 돌아가기</button>
      <button class="close-button" @click="closeWindow">창 닫기</button>
    </div>
  </div>
</template>

<style scoped>
.fail-container {
  text-align: center;
  padding: 50px 20px;
  background-color: #f8f8f8;
  min-height: 100vh;
}

h2 {
  color: #ff4d4f;
  font-size: 2rem;
  margin-bottom: 20px;
}

p {
  font-size: 1.2rem;
  margin: 10px 0;
  color: #333;
}

.button-group {
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 0 40px;
}

.home-button, .close-button {
  padding: 14px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.3s ease;
}

.home-button {
  background: #007bff;
  color: white;
}

.home-button:hover {
  background: #0056b3;
}

.close-button {
  background: #ff4d4f;
  color: white;
}

.close-button:hover {
  background: #d9363e;
}
</style>
