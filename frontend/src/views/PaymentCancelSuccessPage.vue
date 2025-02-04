<script setup>
import { useRoute } from 'vue-router';
import NavBarNonMy from '../components/NavBar-nonMy.vue';
import { useRouter } from 'vue-router';
const router = useRouter();
const route = useRoute();

const amount = route.params.amount;

const goBack = () => {
  router.push('/my-ticket');
}

</script>

<template>
  <NavBarNonMy />
  <div class="wrapper">
    <div class="flex-column align-center confirm-success w-100 max-w-540">
      <transition name="fade-bounce" appear>
        <img src="https://static.toss.im/illusts/check-blue-spot-ending-frame.png" width="100" height="100" />
      </transition>
      <transition name="fade-up" appear>
        <h2 class="title">환불이 완료되었어요</h2>
      </transition>
      <transition name="fade-up" appear>
        <div class="response-section w-100">
          <div class="flex justify-between response-box">
            <span class="response-label">환불 금액</span>
            <span class="response-text">{{ amount.toLocaleString() }} 원</span>
          </div>
        </div>
      </transition>

      <div class="w-100 button-group">
        <div class="flex" style="gap: 12px;">
          <button @click="goBack" class="btn primary w-100">돌아가기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.wrapper {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 80vh;
  padding: 24px;
  overflow: auto;
}

.w-100 {
width: 100%;
}

.h-100 {
height: 100%;
}

a {
text-decoration: none;
text-align: center;
}

.max-w-540 {
max-width: 540px;
}

.btn-wrapper {
padding: 0 24px;
}

.btn {
padding: 11px 22px;
border: none;
border-radius:  8px;

background-color: #f2f4f6;
color: #4e5968;
font-weight: 600;
font-size: 17px;
cursor: pointer;
}

.btn.primary {
  background: linear-gradient(135deg, #7C9EFF 0%, #5C7CFF 100%);
color: #f9fcff;
font-size: 16px;
padding: 14px 20px;
transition: transform 0.2s ease;
}

.btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(92, 124, 255, 0.3);
}

.text-center {
text-align: center;
}

.flex {
display: flex;
}

.flex-column {
display: flex;
flex-direction: column;
}

.justify-center {
justify-content: center;
}

.justify-between {
justify-content: space-between;
}

.align-center {
align-items: center;
}

.confirm-loading {
margin-top: 72px;
height: 400px;
justify-content: space-between;
}


.confirm-success {
margin-top: 72px;
position: relative;
}

.button-group {
margin-top: 32px;
display: flex;
flex-direction: column;
justify-content: center;
gap: 16px;
}

.title {
margin-top: 24px;
margin-bottom: 0;
color: #191f28;
font-weight: bold;
font-size: 20px;
}

.description {
margin-top: 8px;
color: #4e5968;
font-size: 17px;
font-weight: 500;
}

.response-section {
  margin-top: 32px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  font-size: 16px;
}

.response-box {
  padding: 16px 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.response-section .response-label {
  font-weight: 600;
  color: #333d48;
  font-size: 15px;
}

.response-section .response-text {
  font-weight: 600;
  color: #4e5968;
  font-size: 15px;
  padding-left: 12px;
  word-break: break-word;
  text-align: right;
}

.color-grey {
color: #b0b8c1;
}

/* 애니메이션 스타일 추가 */
.fade-bounce-enter-active {
  animation: bounce-in 0.5s;
}

.fade-up-enter-active {
  animation: fade-up 0.5s;
}

@keyframes bounce-in {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes fade-up {
  0% {
    transform: translateY(20px);
    opacity: 0;
  }
  100% {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
