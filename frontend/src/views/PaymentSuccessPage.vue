<script setup>
import NavBarNonMy from '../components/NavBar-nonMy.vue';
import InputBarNon from '../components/InputBar-non.vue';
import { useRoute } from 'vue-router';
import { onMounted } from 'vue';
import axios from '../utils/axios';
import { useRouter } from 'vue-router';
import { ref } from 'vue';

const router = useRouter();
const route = useRoute();

const orderId = route.query.orderId;
const amount = route.query.amount;
const paymentKey = route.query.paymentKey;
const verifyPayment = async () => {
  try {
    await axios.post(`/api/toss-payments/amounts/verify`, {
      orderId: orderId,
      amount: amount,
    },{
      withCredentials: true,
    });
  } catch {
    alert('예기치 않은 오류가 발생하였습니다.');
    await deleteOrder();
    router.push('/');
  }
};

const deleteOrder = async () => {
  try {
    await axios.delete(`/api/orders/${orderId}`);
  } catch (error) {
    console.error("주문 삭제 실패:", error);
  }
};

onMounted(async () => {
  await verifyPayment();
});

const loading = ref(true);
const confirmPayment = async () => {
  try {
    await axios.post(`/api/toss-payments/confirm`, {
      orderId: orderId,
      paymentKey: paymentKey,
      amount: amount,
    });

    await getOrder();

    loading.value = false;
  } catch (error) {
    alert(error.response.data.message);
  }
}

const order = ref(null);
const getOrder = async () => {
  try {
    const response = await axios.get(`/api/orders/${orderId}`);
    order.value = response.data.data;
  } catch (error) {
    console.error("주문 조회 실패:", error);
  }
}

const goBack = () => {
  router.push('/');
}

</script>

<template>
  <NavBarNonMy />
  <InputBarNon @back="goBack"/>
  <div class="wrapper w-100">
    <!-- 결제 진행 중 UI -->
    <div v-if="loading" class="flex-column align-center confirm-loading w-100 max-w-540">
      <div class="flex-column align-center">
        <img src="https://static.toss.im/lotties/loading-spot-apng.png" width="100" height="100" />
        <h2 class="title text-center">결제 요청까지 성공했어요.</h2>
        <h4 class="text-center description">결제 승인하고 완료해보세요.</h4>
      </div>
      <div class="w-100">
        <button @click="confirmPayment" class="btn primary w-100">결제 승인하기</button>
      </div>
    </div>

    <div v-if="!loading" class="flex-column align-center confirm-success w-100 max-w-540">
      <transition name="fade-bounce" appear>
        <img src="https://static.toss.im/illusts/check-blue-spot-ending-frame.png" width="100" height="100" />
      </transition>
      <transition name="fade-up" appear>
        <h2 class="title">결제를 완료했어요</h2>
      </transition>
      <transition name="fade-up" appear>
        <div class="response-section w-100">
          <div class="flex justify-between response-box">
            <span class="response-label">주차장명</span>
            <span class="response-text">{{ order.parking }}</span>
          </div>
          <div class="flex justify-between response-box">
            <span class="response-label">상품명</span>
            <span class="response-text">{{ order.pkDuration }}시간 주차권</span>
          </div>
          <div class="flex justify-between response-box">
            <span class="response-label">결제 금액</span>
            <span class="response-text">{{ amount.toLocaleString() }} 원</span>
          </div>
        </div>
      </transition>

      <div class="w-100 button-group">
        <div class="flex" style="gap: 12px;">
          <button @click="goBack" class="btn primary w-100">홈으로 돌아가기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
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

.wrapper {
display: flex;
flex-direction: column;
align-items: center;
padding: 24px;
overflow: auto;
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
  font-size: 16px;
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
