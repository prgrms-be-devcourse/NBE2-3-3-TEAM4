<script setup>
import axios from '../utils/axios';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import NavBarNonMy from '../components/NavBar-nonMy.vue';
import InputBarNon from '../components/InputBar-non.vue';
const router = useRouter();
import { usePlaceStore } from '../stores/place';

const member = ref({});
const getMember = async () => {
  try {
    const response = await axios.get('/api/members');
    member.value = response.data.data;
  } catch {
    router.push('/login');
  }
};
const placeStore = usePlaceStore();
const primaryCar = ref(null);

const getCars = async () => {
  try {
    const response = await axios.get('/api/cars');
    primaryCar.value = response.data.data.find(car => car.isPrimary === true).carNumber;
  } catch {
    primaryCar.value = "없음";
  }
};
const goBack = () => {
  placeStore.setSelectedPlace(null);
  router.push('/');
};

const toEdit = () => {
  router.push('/my-page/edit');
};

const toCarManage = () => {
  router.push('/car');
};

const toLogout = () => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  router.push('/');
};

onMounted(() => {
  getMember();
  getCars();
});

</script>

<template>
  <NavBar-nonMy />
  <InputBar-non @back="goBack" />
  <div class="my-page-container">
    <div class="my-page-header">
      <div class="user-info">
        <h3>{{ member.name }}<span>님 안녕하세요</span></h3>
        <div class="info-items-container">
          <div class="info-item">
            <div class="info-content">
              <label>이메일</label>
              <p>{{ member.email }}</p>
            </div>
          </div>
          <div class="info-item">
            <div class="info-content">
              <label>연락처</label>
              <p>{{ member.contact }}</p>
            </div>
          </div>
          <div class="info-primaryCard">
            <div class="info-content">
              <label>대표 차량 번호</label>
              <p>{{ primaryCar }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="button-container">
    <button class="edit-button" @click="toEdit">정보 수정</button>
  </div>
  <div class="button-container">
    <button class="car-manage-button" @click="toCarManage">차량 관리</button>
  </div>
  <div class="button-container">
    <button class="logout-button" @click="toLogout">로그아웃</button>
  </div>
</template>

<style>
.my-page-container {
  padding: 20px;
  margin-top: 120px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
  animation: fadeIn 0.5s ease-in-out;
}

.my-page-header {
  background: #ffffff;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 4px 16px rgba(124, 140, 255, 0.15);
}

.user-info h3 {
  font-size: 26px;
  color: #2c3e50;
  margin-bottom: 25px;
}

.user-info h3 span {
  font-size: 19px;
  color: #7d7d7d;
  margin-left: 8px;
}

.info-items-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-item, .info-primaryCard {
  padding: 12px;
  border-radius: 8px;
  background: rgba(124, 140, 255, 0.03);
  transition: background-color 0.2s ease;
}

.info-item:hover, .info-primaryCard:hover {
  background: rgba(124, 140, 255, 0.06);
}

.info-content label {
  font-size: 14px;
  color: #666;
  display: block;
  margin-bottom: 6px;
}

.info-content p {
  color: #333;
  font-size: 16px;
  margin: 0;
}

.button-container {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}

.edit-button, .logout-button, .car-manage-button {
  background: linear-gradient(135deg, #7C9EFF 0%, #5C7CFF 100%);
  width: 90%;
  padding: 16px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  color: white;
  border: none;
  transition: transform 0.2s ease;
  box-shadow: 0 4px 12px rgba(124, 140, 255, 0.2);
}

.edit-button:hover, .logout-button:hover, .car-manage-button:hover {
  transform: translateY(-2px);
}

.logout-button {
  background: linear-gradient(135deg, #ff9a9a 0%, #ff8888 100%);
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
</style>

