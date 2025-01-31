<script setup>
import { onMounted } from "vue";
import axios from '../utils/axios';
import { useRoute } from 'vue-router';


const route = useRoute();
const orderId = route.query.orderId || "";
const paymentKey = route.query.paymentKey || "";
const amount = route.query.amount || "";

onMounted(() => {
  confirmPaymentRequest();
});

const confirmPaymentRequest = async () => {
  const url = "http://localhost:8080/api/toss-payments/confirm";

  try {
    const response = await axios.post(url, {orderId: orderId, paymentKey: paymentKey, amount: amount});
    console.log("결제 승인 성공:", response.data);
    return response.data;
  } catch (error) {
    console.error("결제 승인 실패:", error);
  }

};
</script>

<template>
  <div>
    <h1>결제 완료</h1>
    <p>결제가 완료되었습니다.</p>
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

