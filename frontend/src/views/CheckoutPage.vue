<script setup>
import { onMounted } from "vue";
import { loadTossPayments } from "@tosspayments/tosspayments-sdk";

const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
const customerKey = "WdKa3a4w5zcW3yTMJYvmB";
const orderId = "d04a6b41-c838-4805-a6ff-ce45b02ec993";
const amount = 50000;
let tossPayments = null;
let widgets = null;

// Vue에서 초기화는 `onMounted()`에서 처리
onMounted(async () => {
  tossPayments = await loadTossPayments(clientKey);
  widgets = tossPayments.widgets({ customerKey });

  await widgets.setAmount({ currency: "KRW", value: amount });

  await Promise.all([
    widgets.renderPaymentMethods({ selector: "#payment-method", variantKey: "DEFAULT" }),
    widgets.renderAgreement({ selector: "#agreement", variantKey: "AGREEMENT" }),
  ]);
});

// 결제 요청 함수
const requestPayment = async () => {
  if (!widgets) return;
  await widgets.requestPayment({
    orderId: orderId,
    orderName: "테스트용 주차권",
    successUrl: window.location.origin + "/checkout-success",
    failUrl: window.location.origin + "/fail.html",
  });
};

</script>

<template>
  <div>
    <!-- 결제 UI 렌더링할 요소 -->
    <div id="payment-method"></div>
    <div id="agreement"></div>

    <!-- 버튼 클릭 시 `requestPayment()` 실행 -->
    <button @click="requestPayment">결제하기</button>
  </div>
</template>

<style>
button {
  display: block;
  width: 100%;
  padding: 10px;
  margin-top: 20px;
  background-color: #5C7CFF;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
</style>

