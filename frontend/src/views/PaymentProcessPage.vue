<script setup>
import { useRoute, useRouter } from 'vue-router';
import axios from '../utils/axios';
import { loadTossPayments } from "@tosspayments/tosspayments-sdk";
import { ANONYMOUS } from "@tosspayments/tosspayments-sdk";
import { ref, onMounted } from 'vue';
import NavBarNonMy from '../components/NavBar-nonMy.vue';
import InputBarNon from '../components/InputBar-non.vue';

const route = useRoute();
const router = useRouter();
const orderId = route.params.orderId;

const member = ref(null);
const getMember = async () => {
  try {
    const response = await axios.get(`/api/members`);
    member.value = response.data.data;
  } catch (error) {
    console.error("회원 정보 조회 실패:", error);
  }
};

const order = ref(null);
const getOrder = async () => {
  try {
    const response = await axios.get(`/api/orders/${orderId}/payments`);
    order.value = response.data.data;
  } catch (error) {
    console.error("주문 정보 조회 실패:", error);
    await deleteOrder();
  }
};

const clientKey = import.meta.env.VITE_TOSS_CLIENT_KEY;
const tossPayments = ref(null);
const widgets = ref(null);


const deleteOrder = async () => {
  try {
    await axios.delete(`/api/orders/${orderId}`);
  } catch (error) {
    console.error("주문 삭제 실패:", error);
  }
};


const initializeTossPayments = async () => {
  try {
    tossPayments.value = await loadTossPayments(clientKey);
    widgets.value = tossPayments.value.widgets({
      customerKey: ANONYMOUS,
    });

    if (order.value && order.value.price) {
      await widgets.value.setAmount({
        currency: "KRW",
        value: order.value.price,
      });

      await Promise.all([
        widgets.value.renderPaymentMethods({
          selector: "#payment-method",
          variantKey: "DEFAULT",
        }),
        widgets.value.renderAgreement({
          selector: "#agreement",
          variantKey: "AGREEMENT",
        }),
      ]);
    } else {
      throw new Error("결제 금액이 유효하지 않습니다.");
    }
  } catch (error) {
    console.error("TossPayments 초기화 실패:", error);
    await deleteOrder();
    alert("결제 초기화 중 오류가 발생했습니다. 다시 시도해주세요.");
    router.back();
  }
};

const handlePayment = async () => {
  try {
    if (!order.value) {
      alert("주문 정보를 찾을 수 없습니다.");
      await deleteOrder();
      return;
    }

    await widgets.value.requestPayment({
      orderId: orderId,
      orderName: order.value.pkDuration + "시간 주차권",
      successUrl: "http://localhost:3000/payment/success",
      failUrl: "http://localhost:3000/payment/fail",
      customerEmail: member.value?.email || "",
      customerName: member.value?.name || "",
    });

  } catch (error) {
    console.error("결제 요청 실패:", error);
    alert(error.response?.data?.message || "결제 요청 중 오류가 발생했습니다.");
    await deleteOrder();
  }
};


const goBack = async () => {
  await deleteOrder();
  router.back();
};

// ✅ 마운트 시 실행
onMounted(async () => {
  await getMember();
  await getOrder();
  await initializeTossPayments();
});
</script>

<template>
  <NavBarNonMy />
  <InputBarNon @back="goBack" />
  <div class="payment-process-container">
    <!-- 결제 UI -->
    <div id="payment-method"></div>

    <!-- 이용약관 UI -->
    <div id="agreement"></div>

    <!-- 결제하기 버튼 -->
    <div class="payment-button-container">
      <button class="payment-button" @click="handlePayment">
        결제하기
      </button>
    </div>
  </div>
</template>

<style scoped>
.payment-process-container {
  position: fixed;
  top: 13%;
  width: 100%;
  height: 100%;
  align-items: center;
  justify-content: center;
}
.payment-button {
  width: 100%;
  background: linear-gradient(135deg, #7C9EFF 0%, #5C7CFF 100%);
  color: white;
  padding: 16px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s ease;
}
.payment-button-container {
  padding: 24px;
}
</style>
