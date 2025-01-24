<script setup>
import axios from '../utils/axios';
import { onMounted } from 'vue';
import NavBarNonMy from '../components/NavBar-nonMy.vue';
import InputBarNon from '../components/InputBar-non.vue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
const userInfo = ref({});
const router = useRouter();

onMounted(async () => {
  const response = await axios.get('/api/members');
  userInfo.value = response.data.data;
});

const goBack = () => {
  router.push('/my-page');
};

const updateUserInfo = async () => {
 try {
  await axios.put('/api/members',
  {
    contact: userInfo.value.contact,
    name: userInfo.value.name,
  }
 );
 } catch (error) {
  console.error('회원정보 수정 실패', error);
  return;
 }

 alert('회원정보 수정 완료');
 router.push('/my-page');
};
</script>

<template>
  <NavBar-nonMy />
  <InputBar-non @back="goBack"/>
  <div class="edit-my-info">
    <section class="info-section">
      <div class="section-title">기본정보</div>

      <div class="input-group">
        <label class="input-label">이름</label>
        <input type="text" v-model="userInfo.name" placeholder="이름" />
      </div>

      <div class="input-group">
        <label class="input-label">휴대폰</label>
        <input type="tel" v-model="userInfo.contact" placeholder="010-1234-1234" />
      </div>

      <div class="input-group">
        <label class="input-label">이메일</label>
        <input
          type="email"
          v-model="userInfo.email"
          placeholder="abc@naver.com"
          disabled
          class="disabled-input"
        />
      </div>
    </section>


    <button class="submit-button" @click="updateUserInfo">
      수정
    </button>
  </div>
</template>

<style scoped>
.edit-my-info {
  padding: 24px;
  margin-top: 120px;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
  position: relative;
  z-index: 1;
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

.info-section {
  background-color: #fff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 30px;
  padding-bottom: 12px;
  letter-spacing: 0.5px;
}

.input-group {
  margin-bottom: 24px;
}

.input-label {
  display: block;
  font-size: 15px;
  color: #5c6a7e;
  margin-bottom: 10px;
  font-weight: 500;
  letter-spacing: 0.3px;
}

input {
  width: 100%;
  padding: 14px 16px;
  border: 1.5px solid #e1e6ef;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.2s ease;
  color: #2c3e50;
}

input:focus {
  outline: none;
  border-color: #99BBFF;
  box-shadow: 0 0 0 4px rgba(124, 140, 255, 0.1);
}

input::placeholder {
  color: #a0aec0;
}

.disabled-input {
  background-color: #f8fafc;
  color: #94a3b8;
  cursor: not-allowed;
  border-color: #e2e8f0;
}

.submit-button {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #7C9EFF 0%, #5C7CFF 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 30px;
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
  box-shadow: 0 4px 12px rgba(124, 140, 255, 0.2);
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(124, 140, 255, 0.3);
}

.submit-button:active {
  transform: translateY(0);
}
</style>
