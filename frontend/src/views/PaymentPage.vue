<script setup>
import NavBarNonMy from '../components/NavBar-nonMy.vue';
import InputBarNon from '../components/InputBar-non.vue';
import { useRouter, useRoute } from 'vue-router';
import axios from '../utils/axios.js';
import { ref, onMounted } from 'vue';
import { KakaoMap, KakaoMapMarker } from 'vue3-kakao-maps';

const map = ref(null);
const onLoadKakaoMap = (mapRef) => {
  map.value = mapRef;
}

const router = useRouter();
const route = useRoute();
const member = ref({});
const getMember = async () => {
  try {
    const response = await axios.get('/api/members');
    member.value = response.data.data;
  } catch {
    alert('로그인이 필요합니다.');
    router.push('/login');
  }
}

const parking = ref({});
const latitude = ref(null);
const longitude = ref(null);
const isLoading = ref(false);
const getParking = async () => {
  const parkingId = route.params.id;
  const response = await axios.get(`/api/parking/${parkingId}`);
  parking.value = response.data.data;
  latitude.value = parking.value.latitude;
  longitude.value = parking.value.longitude;
  isLoading.value = true;
}

const ticket = ref({});
const getTicket = async () => {
  const ticketId = route.params.ticketId;
  const response = await axios.get(`/api/tickets/${ticketId}`);
  ticket.value = response.data.data;
}



onMounted(async () => {
  await getMember();
  await getParking();
  await getCars();
  await getTicket();
});

const cars = ref([]);
const getCars = async () => {
  const response = await axios.get('/api/cars');
  cars.value = response.data.data;
  const primaryCar = cars.value.find(car => car.isPrimary);
  if (primaryCar) {
    selectedCar.value = primaryCar;
  }
}

const handleBack = () => {
  router.back();
}

const selectedCar = ref(null);

const selectCar = (car) => {
  selectedCar.value = car;
}

const handlePayment = async () => {
  try {
    const response = await axios.post('/api/orders/tickets', {
      ticketId: route.params.ticketId,
      carNumber: selectedCar.value.carNumber,
    });
    const orderId = response.data.data.orderId;
    const amount = ticket.value.price;
    await axios.post('/api/toss-payments/amounts/session', {
      orderId: orderId,
      amount: amount,
    }, {
      withCredentials: true,
    });


    router.push(`/payment/process/${orderId}`);
  } catch (error) {
    alert(error.response.data.message);
  }
}
</script>

<template>
  <NavBar-nonMy />
  <InputBarNon @back="handleBack"/>
  <div v-if="isLoading && latitude && longitude" class="map-container">
    <h3 class="section-title">결제 정보 확인</h3>
    <KakaoMap
      :lat="latitude"
      :lng="longitude"
      :draggable="false"
      :disable-double-click-zoom="true"
      :level="4"
      :width="'100%'"
      :height="250"
      id="map"
      @onLoadKakaoMap="onLoadKakaoMap"
    >
      <KakaoMapMarker
        :lat="latitude"
        :lng="longitude"
        :image="{
          imageSrc: '/src/assets/icons/search-marker.png',
          imageWidth: 37,
          imageHeight: 37,
          imageOption: {}
        }"
      />
    </KakaoMap>
  </div>
  <div class="parking-info-container">
    <div class="parking-info">
      <div class="info-row">
        <span class="label">주차장명</span>
        <span class="value">{{ parking.name }}</span>
      </div>
      <div class="info-row">
        <span class="label">주소</span>
        <span class="value">{{ parking.address }}</span>
      </div>

      <div class="info-row">
        <span class="label">추가요금</span>
        <span class="value">{{ parking.addCharge }}원 / {{ parking.addChargeTime }}분</span>
      </div>

      <div class="info-row">
        <span class="label">이용시간</span>
        <span class="value">{{ ticket.pkDuration }}시간</span>
      </div>
      <div class="info-row notice-row">
        <span class="value">결제 시점으로부터 10분의 유예 시간이 제공됩니다</span>
      </div>
    </div>
    <div class="car-info">
      <h2>차량 선택</h2>
      <div v-if="cars.length === 0" class="no-car-container">
        <p class="no-car-message">등록된 차량이 없습니다.</p>
        <button class="add-car-button" @click="router.push('/car/add')">차량 등록하기</button>
      </div>
      <div
        v-else
        v-for="car in cars"
        :key="car.carId"
        class="car-item"
        :class="{ 'car-selected': selectedCar === car }"
        @click="selectCar(car)"
      >
        <div class="car-header">
          <span class="car-type">{{ car.carType }}</span>
        </div>
        <div class="car-details">
          <div class="car-detail-item">
            <span class="detail-value">{{ car.carNumber }}</span>
            <span v-if="car.isPrimary" class="primary-badge">대표차량</span>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="bottom-button-container">
    <div class="payment-amount">
      <span class="amount-label">결제 금액</span>
      <span class="amount-value">{{ ticket.price }}원</span>
    </div>
    <button class="payment-button" @click="handlePayment">확인했습니다</button>
  </div>
</template>

<style scoped>

.section-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 20px;
  align-self: flex-start;
  width: 100%;
}


.amount-label {
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 500;
}
.payment-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 0 4px;
}
.amount-value {
  font-size: 1.25rem;
  color: #1a1a1a;
  font-weight: 700;
}
.bottom-button-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 24px 24px;
  background: white;
  box-shadow: 0 -4px 6px -1px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.parking-info-container {
  background-color: #f5f7fa;
  min-height: calc(100vh - 240px);
  margin-top: 110px;
  padding-bottom: 120px;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.parking-info-container::-webkit-scrollbar {
  display: none;
}

.parking-info, .ticket-info {
  background: white;
  padding: 24px;
  padding-bottom: 12px;
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 12px 0;
}

.info-row:last-child {
  border-bottom: none;
}

.bottom-button-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 23px 24px;
  background: white;
  box-shadow: 0 -4px 6px -1px rgba(0, 0, 0, 0.1);
  z-index: 100;
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

.payment-button:disabled {
  background: #94a3b8;
  cursor: not-allowed;
}

.payment-button:not(:disabled):hover {
  opacity: 0.9;
}

.label {
  color: #64748b;
  font-size: 0.875rem;
  font-weight: 600;
  min-width: 80px;
}

.value {
  color: #334155;
  font-size: 0.875rem;
  text-align: right;
}

.operation-hours {
  display: flex;
  flex-direction: column;
  gap: 4px;
  text-align: right;
  color: #334155;
  font-size: 0.875rem;
}

.payment-button:not(:disabled):hover {
  opacity: 0.9;
}

.map-container {
  display: flex;
  flex-direction: column;
  position: relative;
  top: 120px;
  justify-content: center;
  align-items: center;
  padding: 20px;
  width: 100%;
}

#map {
  width: 100%;
  max-width: 800px;
  height: 400px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
}

.price {
  color: #007AFF;
  font-weight: 600;
  font-size: 18px;
}

.car-info {
  background: white;
  padding: 24px;
}

.car-item {
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.car-item:hover {
  border-color: #5C7CFF;
  background-color: #F8FAFF;
}

.car-selected {
  border: 2px solid #5C7CFF;
  background-color: #F8FAFF;
}

.car-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.car-type {
  font-weight: 600;
  font-size: 1rem;
  color: #5C7CFF;
}

.car-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.car-detail-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.detail-value {
  font-size: 0.875rem;
  color: #334155;
  font-weight: 500;
}

.primary-badge {
  font-size: 13px;
  color: white;
  font-weight: 600;
  padding: 6px 12px;
  background: linear-gradient(135deg, #7C9EFF 0%, #5C7CFF 100%);
  border-radius: 6px;
  transition: all 0.3s ease;
}

.no-car-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 32px 0;
}

.no-car-message {
  color: #64748b;
  font-size: 1rem;
}

.add-car-button {
  background: linear-gradient(135deg, #7C9EFF 0%, #5C7CFF 100%);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-car-button:hover {
  opacity: 0.9;
}

.notice-row {
  justify-content: center;
}

.notice-row .value {
  text-align: center;
  color: #64748b;
  font-size: 0.875rem;
}
</style>
