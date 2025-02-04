<script setup>
import NavBar from '../components/NavBar.vue';
import axios from '../utils/axios';
import { useRoute, useRouter} from 'vue-router';
import { onMounted } from 'vue';
import InputBarNon from '@/components/InputBar-non.vue';
import { ref } from 'vue';
const route = useRoute();
const orderId = route.params.orderId;
const router = useRouter();

const order = ref(null);
const getOrder = async () => {
    try {
        const response = await axios.get(`/api/orders/${orderId}`);
        order.value = response.data.data;
    } catch (error) {
        console.error('주문 데이터 조회 실패:', error);
    }
}

const goBack = () => {
    router.go(-1);
}

onMounted(() => {
    getOrder();
})

const formatDate = (dateString) => {
    const date = new Date(dateString);
    return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일 ${date.getHours()}시 ${date.getMinutes()}분`;
}
</script>
<template>
    <NavBar />
    <InputBarNon @back="goBack"/>
    <div class="ticket-page-container">
        <div v-if="!order" class="no-ticket-container">
            <p>로딩중...</p>
        </div>
        <div v-else class="ticket-card">
            <div class="ticket-header">
                <h3>{{ order.parking }}</h3>
            </div>
            <div class="info-items-container">
                <div class="info-item">
                    <div class="info-content horizontal">
                        <label>주소</label>
                        <p class="info-value">{{ order.addr }}</p>
                    </div>
                </div>
                <div class="info-item">
                    <div class="info-content horizontal">
                        <label>차량번호</label>
                        <p class="info-value">{{ order.carNum }}</p>
                    </div>
                </div>

                <!-- 환불 상태 -->
                <div v-if="order.status === '취소'" class="status refunded">
                    <div class="info-item">
                        <div class="info-content">
                            <p class="status-text">취소</p>
                            <div class="info-row">
                                <label>결제일자</label>
                                <p class="info-value">{{ formatDate(order.paymentDate) }}</p>
                            </div>
                            <div class="info-row">
                                <label>환불 금액</label>
                                <p class="info-value price-horizontal">{{ order.totalPrice.toLocaleString() }}원</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 주차 완료 상태 -->
                <div v-else-if="order.status === '주차 종료'" class="status completed">
                    <div class="info-item">
                        <div class="info-content">
                            <p class="status-text">주차 완료</p>
                            <div class="info-row">
                                <label>입차 시간</label>
                                <p class="info-value">{{ order.startTime }}</p>
                            </div>
                            <div class="info-row">
                                <label>출차 시간</label>
                                <p class="info-value">{{ order.endTime }}</p>
                            </div>
                            <div class="info-row">
                                <label>결제일자</label>
                                <p class="info-value">{{ formatDate(order.paymentDate) }}</p>
                            </div>
                            <div class="info-row">
                                <label>기본 요금</label>
                                <p class="info-value price-horizontal">{{ order.price.toLocaleString() }}원</p>
                            </div>
                            <div class="info-row">
                                <label>추가 요금</label>
                                <p class="info-value price-horizontal">{{ order.addPrice.toLocaleString() }}원</p>
                            </div>
                            <div class="info-row">
                                <label>총 결제 금액</label>
                                <p class="info-value price-horizontal">{{ order.totalPrice.toLocaleString() }}원</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 주차 중 상태 -->
                <div v-else-if="order.status === '주차중'" class="status in-progress">
                    <div class="info-item">
                        <div class="info-content">
                            <p class="status-text">주차 중</p>
                            <div class="info-row">
                                <label>입차 시간</label>
                                <p class="info-value">{{ order.startTime }}</p>
                            </div>
                            <div class="info-row">
                                <label>예상 출차 시간</label>
                                <p class="info-value">{{ order.endTime }}</p>
                            </div>
                            <div class="auto-cancel-notice">
                                <i class="notice-icon">⚠️</i>
                                <h5>예상 종료시간 이후 출차 시 추가요금이 부과됩니다</h5>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 주차 대기 상태 -->
                <div v-else-if="order.status === '주차 대기'" class="status waiting">
                    <div class="info-item">
                        <div class="info-content">
                            <p class="status-text">주차 대기</p>
                            <div class="info-row">
                                <label>예상 입차 시간</label>
                                <p class="info-value">{{ order.startTime }}</p>
                            </div>
                            <div class="info-row">
                                <label>예상 출차 시간</label>
                                <p class="info-value">{{ order.endTime }}</p>
                            </div>
                            <div class="info-row">
                                <label>결제일자</label>
                                <p class="info-value">{{ formatDate(order.paymentDate) }}</p>
                            </div>
                            <div class="info-row">
                                <label>결제 금액</label>
                                <p class="info-value price-horizontal">{{ order.price.toLocaleString() }}원</p>
                            </div>
                            <div class="auto-cancel-notice">
                                <i class="notice-icon">⚠️</i>
                                <h5>예약 시간 내 입차하지 않을 경우 자동 취소됩니다</h5>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.ticket-page-container {
    padding: 20px;
    margin-top: 110px;
    max-width: 800px;
    margin-left: auto;
    margin-right: auto;
}

.ticket-card {
    background: white;
    border-radius: 15px;
    padding: 20px;
}

.ticket-header {
    margin-bottom: 20px;
    text-align: center;
}

.ticket-header h3 {
    font-size: 18px;
    color: #2c3e50;
    margin: 0;
}

.info-items-container {
    display: flex;
    flex-direction: column;
    gap: 12px;
    padding: 5px;
}

.info-item {
    border-radius: 8px;
    padding: 12px;
}

.info-content {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.info-content.horizontal {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
}

.info-content label {
    font-size: 12px;
    color: #666;
}

.info-value {
    font-size: 14px;
    color: #333;
    font-weight: 500;
}

.info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
}

.price-horizontal {
    color: #7C9EFF;
    font-weight: 600;
}

.status {
    margin-top: 16px;
    border-radius: 8px;
}

.status-text {
    font-size: 16px;
    font-weight: 600;
    text-align: center;
    margin-bottom: 16px;
}

.refunded {
    background: rgba(255, 107, 107, 0.05);
}

.completed {
    background: rgba(46, 204, 113, 0.05);
}

.in-progress {
    background: rgba(52, 152, 219, 0.05);
}

.waiting {
    background: rgba(241, 196, 15, 0.05);
}

.status .info-item {
    background: transparent;
}

.auto-cancel-notice {
    margin-top: 16px;
    padding: 12px;
    background: rgba(255, 171, 0, 0.1);
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 8px;
}

.notice-icon {
    font-style: normal;
}

.auto-cancel-notice h5 {
    margin: 0;
    font-size: 12px;
    color: #666;
    font-weight: 500;
}

.no-ticket-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 200px;
}

@media screen and (max-width: 768px) {
    .ticket-page-container {
        padding: 15px;
    }

    .ticket-card {
        padding: 15px;
    }
}

@media screen and (max-width: 480px) {
    .ticket-page-container {
        padding: 10px;
    }

    .ticket-card {
        padding: 12px;
    }
}
</style>
