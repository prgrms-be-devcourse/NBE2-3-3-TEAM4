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
    await getOrderHistory();
    await getMember();
});

const router = useRouter();

const orders = ref([]);
const member = ref(null);

const getMember = async () => {
  try {
    const response = await axios.get('/api/members');
    member.value = response.data.data;
  } catch {
    member.value = null;
  }
}
const getOrderHistory = async () => {
    try {
        const response = await axios.get('/api/tickets/history');
        orders.value = response.data.data;
    } catch {
        orders.value = null;
    }
}

const goBack = () => {
    router.push('/');
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
        <div v-else-if="orders.length === 0" class="no-ticket-container">
            <div class="no-ticket-content">
                <h3>주차권 구매기록이 없습니다</h3>
                <p>새로운 주차권을 구매해보세요!</p>
                <button class="buy-ticket-button" @click="router.push('/search')">
                    주차권 구매하기
                </button>
            </div>
        </div>
      <div v-else>
        <div v-for="order in orders" :key="order.id" class="ticket-container">
          <div :class="ticket-section">
            <h3>Ticket</h3>
          </div>
        </div>
      </div>
    </div>

</template>

<style scoped>
.ticket-page-container {
    margin-top: 110px;
    max-width: 800px;
    margin-left: auto;
    margin-right: auto;
    animation: fadeIn 0.5s ease-in-out;
    height: calc(100vh - 177px);
    overflow: auto;
}

.ticket-container {
  margin-top: 30px;
  transform: translate(5%, 0);
  width: 90%;
}

.ticket-section {
  background: white;
  padding: 30px;
  margin-bottom: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  border: 0;
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
