<script setup>
import axios from '../utils/axios';
import { onMounted, ref } from 'vue';
import NavBarNonMy from '../components/NavBar-nonMy.vue';
import InputBarNon from '../components/InputBar-non.vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const cars = ref([]);
const primaryCar = ref(null);

const getCars = async () => {
  const response = await axios.get('/api/cars');
  cars.value = response.data.data;
  const primary = cars.value.find(car => car.isPrimary);
  primaryCar.value = primary ? primary.id : null;
};

const goBack = () => {
  router.push('/my-page');
};

const selectedCar = ref(null);

function selectCar(id) {
  selectedCar.value = id;
}

const toAddCar = () => {
  if(cars.value.length >= 3) {
    alert('최대 3개까지만 등록 가능해요');
    return;
  }
  router.push('/car/add');
}

const changePrimaryCar = async () => {
  if (!selectedCar.value) {
    return;
  }

  if (primaryCar.value) {
    await axios.put(`/api/cars/${primaryCar.value}/primary`);
  }
  await axios.put(`/api/cars/${selectedCar.value}/primary`);
  alert('대표 차량이 변경되었습니다.');
  await getCars();
};

const deleteCar = async () => {
  if(primaryCar.value === selectedCar.value) {
    alert('대표 차량은 삭제할 수 없어요');
    return;
  }
  try {
    await axios.delete(`/api/cars/${selectedCar.value}`);
    alert('차량이 삭제되었습니다.');
  } catch {
    alert('예기치 않은 오류가 발생하였습니다.');
  }
  await getCars();
};

onMounted(async () => {
  await getCars();
});
</script>


<template>
  <NavBarNonMy />
  <InputBarNon @back="goBack" />
  <div class="car-manage-container">
    <h3 class="title">등록된 차량</h3>
    <div class="button-container">
      <button v-for="car in cars"
             :key="car.id"
             class="car-item"
             :class="{ 'selected': selectedCar === car.id }"
             @click="selectCar(car.id)">
             <span class="car-number">{{ car.carNumber }}</span>
             <span v-if="car.isPrimary" class="primary-badge">대표 차량</span>
      </button>
    </div>
  </div>
  <div class="button-container">
    <button class="action-button primary-car-button" @click="changePrimaryCar">
      <span class="button-icon">★</span>
      <div class="button-text">
        <span class="primary-text">대표 차량 변경</span>
        <span class="secondary-text">차량을 선택하고 누르면 변경되요</span>
      </div>
    </button>

    <button class="action-button add-car-button" @click="toAddCar">
      <span class="button-icon">+</span>
      <div class="button-text">
        <span class="primary-text">차량 등록</span>
        <span class="secondary-text">최대 3개까지 등록 가능해요</span>
      </div>
    </button>

    <button class="action-button delete-car-button" @click="deleteCar">
      <span class="button-icon">-</span>
      <div class="button-text">
        <span class="primary-text">차량 삭제</span>
        <span class="secondary-text">차량을 선택하고 누르면 삭제되요</span>
      </div>
    </button>
  </div>


</template>



<style scoped>
.car-manage-container {
  padding: 20px;
  margin-top: 120px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
  animation: fadeIn 0.5s ease-in-out;
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

.title {
  color: #2c3e50;
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 25px;
  margin-left: 10px;
  position: relative;
}

.title::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 40px;
  height: 3px;
  background: linear-gradient(135deg, #7C9EFF 0%, #5C7CFF 100%);
  border-radius: 2px;
}

.button-container {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 10px;
}

.car-item {
  width: 100%;
  background-color: #ffffff;
  border: 0;
  border-radius: 12px;
  padding: 18px 24px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  text-align: left;
  display: flex;
  justify-content: space-between;
  align-items: center;
  outline: none;
  position: relative;
  top: 0;
}
.car-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.car-item.selected {
  background: rgba(150, 176, 255, 0.1);
  border: 0;
  box-shadow: 0 1px 4px rgba(143, 136, 255, 0.2);
  top: 2px;
}

.car-item.selected .car-number {
  color: #gray;
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

.car-item.selected .primary-badge {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.car-number {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.action-button {
  width: 100%;
  padding: 20px;
  border: none;
  border-radius: 15px;
  background: white;
  display: flex;
  align-items: center;
  gap: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.add-car-button {
  background: linear-gradient(135deg, #7C9EFF 0%, #5C7CFF 100%);
  width: 90%;
  margin-left: auto;
  margin-right: auto;
  margin-bottom: 10px;
}

.primary-car-button {
  background: linear-gradient(135deg, #FFB6C1 0%, #FF69B4 100%);
  width: 90%;
  margin-left: auto;
  margin-right: auto;
  margin-bottom: 10px;
}

.delete-car-button {
  background: linear-gradient(135deg, #ff9a9a 0%, #ff8888 100%);
  width: 90%;
  margin-left: auto;
  margin-right: auto;
  margin-bottom: 10px;
}

.action-button:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.button-icon {
  font-size: 24px;
  color: white;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
}

.button-text {
  text-align: left;
  color: white;
}

.primary-text {
  display: block;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.secondary-text {
  display: block;
  font-size: 13px;
  opacity: 0.8;
}
</style>
