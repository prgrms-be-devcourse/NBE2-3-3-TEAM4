<script setup>
import InputBarNon from '@/components/InputBar-non.vue';
import NavBar from '../components/NavBar.vue';
import axios from '../utils/axios';
import { ref } from 'vue';
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter();
const orderHistory = ref([]);

const getOrderHistory = async () => {
    try {
        const response = await axios.get('/api/orders/order-history');
        const orders = response.data.data || [];
        // 각 주문에 대해 상세 정보를 가져옵니다
        const detailedOrders = await Promise.all(
            orders.map(async (order) => {
                const detailedOrder = await getOrder(order.orderId);
                return { ...order, ...detailedOrder };
            })
        );
        orderHistory.value = detailedOrders.sort((a, b) => {
            return new Date(b.createdAt) - new Date(a.createdAt);
        });
    } catch (error) {
        console.error('주문 내역을 불러오는데 실패했습니다:', error);
        orderHistory.value = [];
    }
}

const getOrder = async (orderId) => {
    const response = await axios.get(`/api/orders/${orderId}`);
    return response.data.data;
}
const goBack = () => {
    router.back();
}

const goToOrderDetail = (orderId) => {
    router.push(`/ticket-history/${orderId}`);
}

onMounted(async () => {
    await getOrderHistory();
})
</script>

<template>
    <NavBar />
    <InputBarNon @back="goBack" />
    <div class="order-history-container">
        <div v-if="orderHistory.length > 0" class="order-list">
            <div class="order-header">
            </div>
            <div class="order-items-container">
                <div v-for="order in orderHistory" :key="order.orderId" class="order-card" @click="goToOrderDetail(order.orderId)">
                    <div class="order-content">
                        <div class="info-items-container">
                          <div class="info-item">
                                <div class="info-content horizontal">
                                    <label>주차장</label>
                                    <p class="info-value">{{ order.parking }}</p>
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-content horizontal">
                                    <label>상태</label>
                                    <p class="info-value status-p"
                                        :class="{
                                            'status-completed': order.status === '주차 종료',
                                            'status-in-progress': order.status === '주차중',
                                            'status-waiting': order.status === '주차 대기',
                                            'status-refunded': order.status === '취소'
                                        }">
                                        {{ order.status }}
                                    </p>
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-content horizontal">
                                    <label>차량 번호</label>
                                    <p class="info-value">{{ order.carNum }}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div v-else class="no-orders-container">
            <div class="no-orders-content">
                <h3>지난 주차권 내역이 없습니다</h3>
                <p>새로운 주차권을 구매해보세요!</p>
                <button class="buy-ticket-button" @click="router.push('/search')">
                    주차권 구매하기
                </button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.order-history-container {
    margin-top: 130px;
    max-width: 800px;
    margin-left: auto;
    margin-right: auto;
    animation: fadeIn 0.5s ease-in-out;
    height: calc(100vh - 177px);
    overflow: auto;
    padding: 0 20px;
}

.order-header {
    margin-bottom: 20px;
}

.order-header h3 {
    font-size: 18px;
    color: #333;
    margin: 0;
    text-align: center;
}

.order-items-container {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.order-card {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: transform 0.2s ease;
}

.order-card:hover {
    transform: translateY(-2px);
}

.info-items-container {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.info-item {
    padding: 8px;
    border-radius: 8px;
    background: rgba(124, 140, 255, 0.03);
}

.info-content label {
    font-size: 12px;
    color: #666;
    margin-bottom: 4px;
}

.info-content.horizontal {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.info-value {
    color: #333;
    font-size: 14px;
    margin: 0;
    font-weight: 500;
}

.status-p {
    font-size: 12px;
    padding: 4px 12px;
    border-radius: 20px;
    font-weight: 500;
}

.status-completed {
    background-color: rgba(75, 192, 119, 0.15);
    color: #2ECC71;
}

.status-in-progress {
    background-color: rgba(124, 140, 255, 0.15);
    color: #7C8CFF;
}

.status-waiting {
    background-color: rgba(255, 171, 0, 0.15);
    color: #FF9800;
}

.status-refunded {
    background-color: rgba(255, 99, 99, 0.15);
    color: #FF6363;
}

.time-value {
    color: #333;
    font-weight: 500;
}

.right-align {
    text-align: right;
    width: 100%;
}

.no-orders-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    padding: 20px;
}

.no-orders-content {
    text-align: center;
    animation: fadeIn 0.5s ease-in-out;
}

.no-orders-content h3 {
    color: #333;
    font-size: 18px;
    margin-bottom: 8px;
}

.no-orders-content p {
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

@media screen and (max-width: 768px) {
    .order-card {
        padding: 15px;
    }
}

@media screen and (max-width: 480px) {
    .order-history-container {
        padding: 0 15px;
    }

    .order-card {
        padding: 12px;
    }
}
</style>
