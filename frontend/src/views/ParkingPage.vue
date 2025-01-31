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


onMounted(async () => {
  await getParking();
  await getParkingStatus();
});

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

    </div>
  </div>
</template>

<style scoped>
.parking-detail-container {
  background-color: #f5f7fa;
  min-height: calc(100vh - 120px);
  margin-top: 120px;
}

.parking-detail-card {
  background: white;
  padding: 20px;
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
</style>
