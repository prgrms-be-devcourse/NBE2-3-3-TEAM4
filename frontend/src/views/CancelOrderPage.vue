<script setup>
import NavBarNonMy from '@/components/NavBar-nonMy.vue';
import InputBarNon from '@/components/InputBar-non.vue';
import { useRouter } from 'vue-router';
import axios from '@/utils/axios';
import { onMounted, ref } from 'vue';

const router = useRouter();

const goBack = () => {
    router.back();
}



const order = ref(null);
const orderId = router.currentRoute.value.params.orderId;
const formatDate = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = date.getHours();
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const ampm = hours >= 12 ? '오후' : '오전';
    const displayHours = hours % 12 || 12;

    return `${year}년 ${month}월 ${day}일 ${ampm} ${displayHours}시 ${minutes}분`;
};

const calculateRefundStatus = (paymentDate) => {
    const paymentTime = new Date(paymentDate).getTime();
    const currentTime = new Date().getTime();
    const timeDiff = (currentTime - paymentTime) / (1000 * 60);

    if (timeDiff <= 10) {
        return {
            type: 'full',
            message: '전액 환불이 가능합니다',
            amount: order.value.price + order.value.addPrice
        };
    } else {
        return {
            type: 'partial',
            message: '50% 환불이 가능합니다',
            amount: (order.value.price + order.value.addPrice) / 2
        };
    }
};

const refundStatus = ref(null);

const getOrder = async () => {
    const response = await axios.get(`/api/orders/${orderId}`);
    order.value = response.data.data;
    refundStatus.value = calculateRefundStatus(order.value.paymentDate);
}

const handleCancelOrder = async () => {
    await cancelOrder();
    router.push(`/payment/cancel/${refundStatus.value.amount}`);
}

const cancelOrder = async () => {
    await axios.put(`/api/orders/${orderId}/cancel`);
}

onMounted(() => {
    getOrder();
})
</script>

<template>
  <NavBarNonMy />
  <InputBarNon @back="goBack" />
  <div v-if="order" class="cancel-page-container">
    <div class="cancel-card">
      <div class="section-title">주문 정보</div>
      <div class="info-items-container">
        <div class="info-item">
          <div class="info-content horizontal">
            <label>주차장</label>
            <p class="info-value">{{ order.parking }}</p>
          </div>
        </div>
        <div class="info-item">
          <div class="info-content horizontal">
            <label>주소</label>
            <p class="info-value">{{ order.addr }}</p>
          </div>
        </div>
        <div class="info-item">
          <div class="info-content horizontal">
            <label>차량번호</label>
            <p class="info-value">{{ order.carNum }}</p>
          </div>
        </div>
        <div class="info-item">
          <div class="info-content horizontal">
            <label>예약 시간</label>
            <p class="info-value">{{ order.pkDuration }}시간</p>
          </div>
        </div>
      </div>

      <div class="section-divider"></div>

      <div class="section-title">결제 정보</div>
      <div class="info-items-container">
        <div class="info-item">
          <div class="info-content horizontal">
            <label>결제 금액</label>
            <p class="info-value price">{{ order.price.toLocaleString() }}원</p>
          </div>
        </div>
        <div class="info-item">
          <div class="info-content horizontal">
            <label>결제 일자</label>
            <p class="info-value">{{ formatDate(order.paymentDate) }}</p>
          </div>
        </div>
      </div>

      <div class="section-divider"></div>

      <div class="section-title">환불 정보</div>
      <div class="refund-notice" :class="refundStatus.type">
        <div class="notice-header">
          <i class="notice-icon">
            {{ refundStatus.type === 'full' ? '💰' : '⏰' }}
          </i>
          <p class="notice-message">{{ refundStatus.message }}</p>
        </div>
      </div>

      <div class="refund-policy">
        <h4>환불 정책 안내</h4>
        <ul>
          <li>결제 후 10분 이내: 전액 환불</li>
          <li>결제 후 10분 이후: 50% 환불</li>
          <li>입차 완료 후: 환불 불가</li>
        </ul>
      </div>
    </div>

    <div class="bottom-spacing"></div>

    <div class="bottom-button-container">
      <div class="refund-amount-display">
        <span class="amount-label">환불 금액</span>
        <span class="amount-value">{{ refundStatus?.amount.toLocaleString() }}원</span>
      </div>
      <button class="cancel-button" @click="handleCancelOrder">주문 취소하기</button>
    </div>
  </div>
</template>

<style scoped>
.cancel-page-container {
  padding: 10px;
  margin-top: 120px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
  animation: fadeIn 0.5s ease-in-out;
  height: calc(100vh - 200px);
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 4px;
}

.cancel-card {
  width: 100%;
  min-height: 200px;
  height: auto;
  padding: 20px;
  background: white;
  border-radius: 15px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 12px 0 8px 0;
}

.section-title:first-child {
  margin-top: 0;
}

.section-divider {
  height: 1px;
  background: rgba(124, 140, 255, 0.1);
  margin: 12px 0;
}

.info-items-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  padding: 10px;
  border-radius: 8px;
  background: rgba(124, 140, 255, 0.03);
}

.info-content.horizontal {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-content label {
  font-size: 13px;
  color: #666;
}

.info-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.info-value.price {
  color: #7C9EFF;
  font-weight: 600;
}

.refund-notice {
  margin: 8px 0;
  padding: 14px;
  border-radius: 8px;
}

.refund-notice.full {
  background: rgba(46, 204, 113, 0.05);
  border-left: 3px solid #2ECC71;
}

.refund-notice.partial {
  background: rgba(255, 171, 0, 0.05);
  border-left: 3px solid #FFAB00;
}

.notice-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.notice-icon {
  font-size: 14px;
}

.notice-message {
  margin: 0;
  font-size: 13px;
  font-weight: 500;
}

.refund-notice.full .notice-message {
  color: #2ECC71;
}

.refund-notice.partial .notice-message {
  color: #FFAB00;
}

.refund-policy {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  margin-top: 12px;
}

.refund-policy h4 {
  font-size: 14px;
  color: #333;
  margin: 0 0 12px 0;
}

.refund-policy ul {
  margin: 0;
  padding-left: 20px;
}

.refund-policy li {
  font-size: 13px;
  color: #666;
  margin-bottom: 6px;
  line-height: 1.5;
}

.bottom-spacing {
  height: 130px;
}

.bottom-button-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 16px 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
}

.refund-amount-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.amount-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.amount-value {
  font-size: 18px;
  color: #7C9EFF;
  font-weight: 600;
}

.cancel-button {
  width: 100%;
  padding: 16px;
  background-color: #FF6B6B;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
}

.cancel-button:active {
  background-color: #ff5252;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media screen and (max-width: 768px) {
  .cancel-card {
    min-height: 180px;
    padding: 15px;
    gap: 12px;
  }
}

@media screen and (max-width: 480px) {
  .cancel-card {
    min-height: 160px;
    padding: 12px;
    gap: 10px;
  }
}
</style>
