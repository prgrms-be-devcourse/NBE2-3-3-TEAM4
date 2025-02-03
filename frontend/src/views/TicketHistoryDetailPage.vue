<script setup>
import InputBarNon from '@/components/InputBar-non.vue';
import axios from '../utils/axios';
import { onMounted } from 'vue';
import { ref } from 'vue';
import NavBar from '../components/NavBar.vue';
import { useRouter } from 'vue-router';
import 'swiper/css';
import 'swiper/css/pagination';


onMounted(async () => {
  const req = new URLSearchParams(location.search);
  const orderId = atob(req.get('orderid'));
  await getTicket(orderId);
  await getMember();
});

const router = useRouter();

const ticket = ref(null);
const member = ref(null);

const getMember = async () => {
  try {
    const response = await axios.get('/api/members');
    member.value = response.data.data;
  } catch {
    member.value = null;
  }
}

const getTicket = async (orderId) => {
  try {
    const response = await axios.get('/api/tickets/detail/' + orderId);
    ticket.value = response.data.data;
  } catch {
    ticket.value = null;
  }
}

const goBack = () => {
  router.back();
}

</script>

<template>
    <NavBar />
    <InputBarNon @back="goBack" />
    <div class="ticket-page-container">
      <div v-if="!member" class="no-ticket-container">
          <div class="no-ticket-content">
              <h3>로그인이 필요한 서비스입니다</h3>
              <p>로그인 후 이용해주세요!</p>
              <button class="buy-ticket-button" @click="router.push('/login')">
                  로그인하기
              </button>
          </div>
      </div>
      <div v-else-if="!ticket" class="no-ticket-container">
        <div class="no-ticket-content">
          <h3>잘못된 주문 내역입니다</h3>
          <p>주문 내역을 확인해주세요!</p>
          <button class="buy-ticket-button" @click="router.push('/ticket-history')">
            주문 내역 확인하기
          </button>
        </div>
      </div>
      <div v-else class="ticket-container">
        <div v-if="ticket.status==='환불'" class="ticket-section ticket-canceled">
          <div class="section-header">
            <div class="parking-name">
              <h3>{{ ticket.parkingName }}</h3>
            </div>
            <div class="car-number">
              <img src="@/assets/icons/car-icon-pink.png" class="icon" alt="차 아이콘">
              <p>{{ ticket.carNum }}</p>
            </div>
          </div>
          <div class="section-body">
            <div class="parking-address">
              <img src="@/assets/icons/pin-pink.png" class="icon" alt="차 아이콘">
              <h4>{{ ticket.parkingAddress }}</h4>
            </div>
            <div class="parking-time">
              <h3 style="color: #e86969;">구매</h3>
              <h4>{{ ticket.orderTime }}</h4>
            </div>
            <div class="parking-time">
              <h3 style="color: #e86969;">취소</h3>
              <h4>{{ ticket.cancelTime }}</h4>
            </div>
          </div>
          <div class="section-bottom">
            <h3 v-if="ticket.cancelType==='무료'" class="cancel-text section-bottom-item">
              <span>전액 환불 되었습니다.</span>
            </h3>
            <h3 v-else-if="ticket.cancelType==='반액'" class="cancel-text section-bottom-item">
              <span>구매 후 10분 경과되어 반액 환불 되었습니다.</span>
            </h3>
            <h3 v-else class="cancel-text section-bottom-item">
              <span>주차권 유효 기간이 만료되어 환불 불가합니다.</span>
            </h3>
            <hr/>
            <div class="total-price section-bottom-item">
              <h3>청구 금액</h3>
              <h3>{{ ticket.cancelPrice.toLocaleString() }} 원</h3>
            </div>
          </div>
        </div>
        <div v-else class="ticket-section">
          <div class="section-header">
            <div class="parking-name">
              <h3>{{ ticket.parkingName }}</h3>
            </div>
            <div class="car-number">
              <img src="@/assets/icons/car-icon-blue.png" class="icon" alt="차 아이콘">
              <p>{{ ticket.carNum }}</p>
            </div>
          </div>
          <div class="section-body">
            <div class="parking-address">
              <img src="@/assets/icons/pin-blue.png" class="icon" alt="차 아이콘">
              <h4>{{ ticket.parkingAddress }}</h4>
            </div>
            <div class="parking-time">
              <h3 style="color: #7C9DFE;">입차</h3>
              <h4>{{ ticket.enterTime }}</h4>
            </div>
            <div class="parking-time">
              <h3 style="color: #7C9DFE;">출차</h3>
              <h4>{{ ticket.exitTime }}</h4>
            </div>
          </div>
          <div class="section-bottom">
            <div class="section-bottom-item">
              <h4>기본 시간</h4>
              <h4>{{ ticket.basicTime }} 시간</h4>
            </div>
            <div class="section-bottom-item">
              <h4>기본 금액</h4>
              <h4>{{ ticket.basicPrice.toLocaleString() }} 원</h4>
            </div>
            <div v-if="ticket.addTime" class="section-bottom-item">
              <h4>추가 시간</h4>
              <h4>{{ ticket.addTime }}</h4>
            </div>
            <div v-if="ticket.addTime" class="section-bottom-item">
              <h4>추가 금액</h4>
              <h4>{{ ticket.addPrice.toLocaleString() }} 원</h4>
            </div>
            <hr/>
            <div class="total-price section-bottom-item">
              <h3>청구 금액</h3>
              <h3>{{ ticket.totalPrice.toLocaleString() }} 원</h3>
            </div>
          </div>
        </div>
      </div>
    </div>

</template>

<style scoped>
@import url('https://cdn.jsdelivr.net/gh/moonspam/NanumSquare@2.0/nanumsquare.css');
@import url('https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/static/pretendard.css');
.ticket-page-container {
  margin-top: 110px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
  height: 75vh;
  animation: fadeIn 0.5s ease-in-out;
  overflow: auto;
}

.ticket-container {
  margin-top: 30px;
  transform: translate(5%, 0);
  width: 90%;
}

.ticket-section {
  font-family: "Pretendard", sans-serif;
  color: #2c2c2c;
  background: white;
  margin-bottom: 20px;
  transition: all 0.3s ease;
  p {
    color: #7C9DFE;
    font-family: "NanumSquare", sans-serif;
    font-size: 16px;
    font-weight: 800;
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  padding: 20px;
  align-items: center;
  margin-bottom: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
  border: 0;
}

.section-body {
  font-size: 16px;
  padding: 20px;
  color: #707070;
}

.section-bottom {
  color: #707070;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
  border: 0;
  h3 {
    font-size: 20px;
    color: #2c2c2c;
  }
  h4, span {
    font-size: 16px;
    padding: 10px;
    color: #707070;
  }
}

hr {
  margin-top: 10px;
}
.section-bottom-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.parking-address {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  margin-left: 4px;
  h4 {
    margin-left: 8px;
  }
}

.parking-time {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.parking-time h3 {
  margin-right: 12px;
}
.car-number {
  display: flex;
  align-items: center;
}

.ticket-canceled {
  p {
    color: #e86969;
  }
}

.cancel-text {
  display: flex;
  justify-content: flex-end;
  font-family: "NanumSquare", sans-serif;
  margin-bottom: 4px;
}

.total-price {
  margin-top: 20px;
  padding: 10px;
}

.icon {
  width: 24px;
  height: 24px;
  margin-right: 8px;
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

.no-ticket-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  padding: 20px;
}

.no-ticket-content {
  text-align: center;
  animation: fadeIn 0.5s ease-in-out;
}

.no-ticket-content h3 {
  color: #333;
  font-size: 18px;
  margin-bottom: 8px;
}

.no-ticket-content p {
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
}

.buy-ticket-button {
  background: linear-gradient(135deg, #7C9EFF 0%, #5C7AE6 100%);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.buy-ticket-button:hover {
  transform: translateY(-2px);
}

.buy-ticket-button:active {
  transform: translateY(0);
}
</style>
