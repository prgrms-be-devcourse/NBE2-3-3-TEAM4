<script setup>
import NavBarNonMy from '../components/NavBar-nonMy.vue';
import { useRouter } from 'vue-router';
import axios from '../utils/axios-non';
import {ref} from 'vue';
import InputBarNon from '../components/InputBar-non.vue';
const router = useRouter();
const email = ref('');
const password = ref('');

const toSignupPage = () => {
  router.push('/signup');
};

const handleLogin = async () => {
  try {
    const response = await axios.post('/api/auth/login', {
      email: email.value,
      password: password.value,
    });

    if(response.data) {
      localStorage.setItem('accessToken', response.data.data.accessToken);
      localStorage.setItem('refreshToken', response.data.data.refreshToken);
      alert('로그인 성공');
      router.push('/');
    }
  } catch {
    alert('로그인 실패');
  }
};

const goBack = () => {
  router.back();
};

</script>

<template>
  <NavBar-nonMy />
  <InputBar-non @back="goBack" />
  <div class="login-container">
    <div class="login-box">
      <h2>로그인</h2>
      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label for="email">이메일</label>
          <input
            type="email"
            id="email"
            v-model="email"
            required
            placeholder="이메일을 입력하세요"
          >
        </div>
        <div class="input-group">
          <label for="password">비밀번호</label>
          <input
            type="password"
            id="password"
            v-model="password"
            required
            placeholder="비밀번호를 입력하세요"
          >
        </div>
        <button type="submit" class="login-btn">로그인</button>
      </form>
      <div class="signup-link">
        계정이 없으신가요? <span @click="toSignupPage()" class="signup-text">회원가입</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  position: relative;
  width: 100%;
  background-color: #ffffff;
  min-height: 100vh;
  padding-top: 28%;
  height: 100%;
  animation: fadeIn 0.5s ease-in-out;
}
.login-box {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  width: 100%;
  max-width: 400px;
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 2rem;
}

.input-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: #666;
  font-weight: 600;
}

input {
  width: 100%;
  padding: 14px;
  border: 0;
  border-radius: 10px;
  font-size: 1rem;
  background-color: #f8faf8;
}


input:focus {
  outline: none;
  border-color: #4169e1;
}

.login-btn {
  width: 100%;
  padding: 0.8rem;
  background: linear-gradient(135deg, #7c8cff 0%, #6c63ff 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
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

.login-btn:hover {
  background-color: #4169e1;
}

.signup-link {
  text-align: center;
  margin-top: 1rem;
}

.signup-text {
  color: #7c8cff;
  font-weight: 600;
  text-decoration: none;
  cursor: pointer;
}

.signup-text:hover {
  text-decoration: underline;
}
</style>

