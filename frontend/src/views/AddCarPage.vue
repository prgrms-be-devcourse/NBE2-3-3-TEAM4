<script setup>
import NavBarNonMy from '../components/NavBar-nonMy.vue';
import InputBarNon from '../components/InputBar-non.vue';
import axios from '../utils/axios';
import { useRouter } from 'vue-router';
import { ref } from 'vue';

const router = useRouter();

const carNumber = ref('');

const validateAndFormatCarNumber = (value) => {
  // 숫자와 한글만 허용하고 공백 제거
  value = value.replace(/[^0-9가-힣]/g, '');
  carNumber.value = value;
};

const goBack = () => {
  router.back();
}

const addCar = async () => {
  if (!carNumber.value.trim()) {
    alert('차량 번호를 입력해주세요.');
    return;
  }

  try {
    await axios.post('/api/cars', {
    carNumber: carNumber.value,
  });
  } catch(error) {
    alert(error.response.data.message);
    return;
  }
  alert('차량 등록이 완료되었습니다.');
  router.back();
}
</script>

<template>
  <NavBarNonMy />
  <InputBarNon @back="goBack"/>
  <div class="car-manage-container">
    <h3 class="title">차량 등록</h3>
    <div class="input-wrapper">
      <input
        type="text"
        v-model="carNumber"
        @input="e => validateAndFormatCarNumber(e.target.value)"
        placeholder="차량 번호 (예: 12가3456)"
        class="car-number-input"
        maxlength="8"
      >
    </div>
  </div>
  <div class="button-container">
    <button class="action-button add-car-button" @click="addCar">
      <span class="button-icon">+</span>
      <div class="button-text">
        <span class="primary-text">차량 등록</span>
        <span class="secondary-text">최대 3개까지 등록 가능해요</span>
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

.input-wrapper {
  position: relative;
  margin-bottom: 10px;
  margin-top: 50px;
  width: 100%;
  margin-left: auto;
  margin-right: auto;
}

.car-number-input {
  width: 100%;
  padding: 15px 45px 15px 15px;
  border: 0;
  border-radius: 10px;
  font-size: 16px;
  transition: all 0.3s ease;
  background-color: #f8f9fa;
}

.car-number-input:focus {
  outline: none;
  border-color: #7C9EFF;
  box-shadow: 0 0 0 3px rgba(124, 158, 255, 0.2);
  background-color: #fff;
}

.car-number-input::placeholder {
  color: #adb5bd;
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
