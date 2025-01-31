<script setup>
import { useRouter, useRoute } from 'vue-router';
import NavBarNonMy from '../components/NavBar-nonMy.vue';
import InputBarNon from '../components/InputBar-non.vue';
import axios from '../utils/axios-non.js';
import { ref, onMounted } from 'vue';



const router = useRouter();
const route = useRoute();
const id = route.params.id;

const handleBack = () => {
  router.back();
}

const parking = ref(null);
const parkingStatus = ref(null);

const getParking = async () => {
  const response = await axios.get(`/api/parking/${id}`);
  parking.value = response.data.data;
}

const getParkingStatus = async () => {
  const response = await axios.get(`/api/parking/${id}/status`);
  parkingStatus.value = response.data.data;
}

const tickets = ref(null);
const getTickets = async () => {
  const response = await axios.get(`/api/parking/${id}/tickets`);
  tickets.value = response.data.data;
}

const selectedTicket = ref(null);

const selectTicket = (ticket) => {
  selectedTicket.value = selectedTicket.value === ticket ? null : ticket;
}

onMounted(async () => {
  await getParking();
  await getParkingStatus();
  await getTickets();
});

const handlePayment = () => {
  if (selectedTicket.value) {
    router.push(`/parking/${id}/payment/${selectedTicket.value.ticketId}`);
  } else {
    alert('주차권을 선택해주세요.');
  }
}

</script>

<template>
  <NavBar-nonMy />
  <InputBarNon @back="handleBack"/>
  <div v-if="parking && parkingStatus" class="parking-detail-container">
    <div class="parking-detail-card">
      <div class="parking-status-section">
        <h3 class="section-title">실시간 주차 현황</h3>
        <div class="status-grid">
          <div class="status-item total">
            <div class="status-content">
              <span class="status-value">{{ parkingStatus.totalSpace }}</span>
              <span class="status-label">전체</span>
            </div>
          </div>
          <div class="status-item used">
            <div class="status-content">
              <span class="status-value">{{ parkingStatus.usedSpace }}</span>
              <span class="status-label">사용 중</span>
            </div>
          </div>
          <div class="status-item available">
            <div class="status-content">
              <span class="status-value">{{ parkingStatus.availableSpace }}</span>
              <span class="status-label">여유</span>
            </div>
          </div>
        </div>
      </div>
      <div class="parking-info-section">
        <div class="info-item">
          <div class="info-content">
            <span class="info-label">주소</span>
            <span class="info-value">{{ parking.address }}</span>
          </div>
        </div>

        <div class="info-item">
          <div class="info-content">
            <span class="info-label">운영시간</span>
            <div class="operation-time">
              <span>평일 {{ parking.wdOpenTime.slice(0,2) }}:{{ parking.wdOpenTime.slice(2,4) }} ~ {{ parking.wdEndTime.slice(0,2) }}:{{ parking.wdEndTime.slice(2,4) }}</span>
              <span>주말 {{ parking.weOpenTime.slice(0,2) }}:{{ parking.weOpenTime.slice(2,4) }} ~ {{ parking.weEndTime.slice(0,2) }}:{{ parking.weEndTime.slice(2,4) }}</span>
            </div>
          </div>
        </div>

        <div class="info-item">
          <div class="info-content">
            <span class="info-label">추가 요금</span>
            <span class="info-value">{{ parking.addCharge.toLocaleString() }}원 / {{ parking.addChargeTime }}분</span>
          </div>
        </div>
      </div>
    </div>

    <div class="parking-detail-card">
        <h3 class="section-title">주차권 정보</h3>
        <div v-if="tickets && tickets.length > 0" class="tickets-container">
            <div
                v-for="ticket in tickets"
                :key="ticket.ticketId"
                class="ticket-item"
                :class="{ 'ticket-selected': selectedTicket === ticket }"
                @click="selectTicket(ticket)"
            >
                <div class="ticket-header">
                    <span class="ticket-type">{{ ticket.type }}</span>
                </div>
                <div class="ticket-details">
                    <div class="ticket-detail-item">
                        <span class="detail-value">{{ ticket.pkDuration }}시간 주차권</span>
                        <span class="detail-price">{{ ticket.price.toLocaleString() }}원</span>
                    </div>
                </div>
            </div>
        </div>
        <div v-else class="no-tickets">
            등록된 주차권이 없습니다.
        </div>
    </div>
  </div>
  <div class="bottom-button-container">
    <button class="payment-button" @click="handlePayment">결제하기</button>
  </div>
</template>

<style scoped>
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

.parking-detail-container {
  background-color: #f5f7fa;
  min-height: calc(100vh - 120px);
  margin-top: 120px;
  padding-bottom: 90px;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.parking-detail-container::-webkit-scrollbar {
  display: none;
}

.parking-detail-card {
  background: white;
  padding: 24px;
  margin-bottom: 16px;
}

.parking-detail-card:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 20px;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 22px;
}

.status-item {
  background: #f8fafc;
  border-radius: 12px;
  padding: 14px 12px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.status-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  width: 100%;
}

.status-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1a73e8;
  margin-bottom: 4px;
}

.status-label {
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 500;
}

.status-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-position: center;
  background-repeat: no-repeat;
  background-size: 24px;
}

.used {
  background-color: #FFF3E0;
}

.used .status-value {
  color: #F57C00;
}

.available {
  background-color: #E8F5E9;
}

.available .status-value {
  color: #2E7D32;
}

.total {
  background-color: #E3F2FD;
}

.parking-info-section {
  padding-top: 1px;
}

.info-item {
  margin-bottom: 20px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-content {
  display: flex;
  align-items: flex-start;
}

.info-label {
  min-width: 80px;
  font-weight: 600;
  color: #64748b;
  font-size: 0.875rem;
}

.info-value {
  color: #334155;
  font-size: 0.875rem;
}

.operation-time {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #334155;
  font-size: 0.875rem;
}

.tickets-container {
    display: grid;
    gap: 16px;
}

.ticket-item {
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 16px;
    cursor: pointer;
    transition: all 0.2s ease-in-out;
}

.ticket-item:hover {
    border-color: #5C7CFF;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.ticket-selected {
    border-color: #5C7CFF;
    background-color: #F5F7FF;
}

.ticket-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.ticket-type {
    font-weight: 600;
    font-size: 1rem;
    color: #5C7CFF;
}

.ticket-details {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.ticket-detail-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.detail-value {
    font-size: 0.875rem;
    color: #334155;
    font-weight: 500;
}

.detail-price {
    font-size: 0.875rem;
    color: #334155;
    font-weight: 700;
}

.no-tickets {
    text-align: center;
    color: #64748b;
    padding: 24px;
    background-color: #f8fafc;
    border-radius: 8px;
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
</style>
