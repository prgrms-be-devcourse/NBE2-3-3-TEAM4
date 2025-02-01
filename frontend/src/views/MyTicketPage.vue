<script setup>
import InputBarNon from '@/components/InputBar-non.vue';
import axios from '../utils/axios';
import { onMounted } from 'vue';
import { ref } from 'vue';
import NavBar from '../components/NavBar.vue';
import { useRouter } from 'vue-router';
import { Swiper, SwiperSlide } from 'swiper/vue';
import { Pagination } from 'swiper/modules';
import 'swiper/css';
import 'swiper/css/pagination';
import { KakaoMap, KakaoMapMarker } from 'vue3-kakao-maps';

const map = ref(null);
const onLoadKakaoMap = (mapRef) => {
  map.value = mapRef;
}

onMounted(async () => {
    await getMember();
    await getOrderList();
});

const router = useRouter();

const member = ref(null);
const getMember = async () => {
    try {
        const response = await axios.get('/api/members');
        member.value = response.data.data;
    } catch {
        member.value = null;
    }
}

const orderlist = ref([]);
const parkingNames = ref({});
const parkingLocations = ref({});

const getParkingName = async (parkingId) => {
    try {
        const response = await axios.get(`/api/parking/${parkingId}`);
        parkingNames.value[parkingId] = response.data.data.name;
        parkingLocations.value[parkingId] = {
            lat: response.data.data.latitude,
            lng: response.data.data.longitude
        };
    } catch (error) {
        console.error(`Error fetching parking name for ID ${parkingId}:`, error);
        parkingNames.value[parkingId] = '알 수 없음';
    }
};

const getOrderList = async () => {
    try {
        const response = await axios.get(`/api/orders/order-history`);
        orderlist.value = response.data.data
            .filter(order => order.status === "주차 대기" || order.status === "주차중");
        for (const order of orderlist.value) {
            await getParkingName(order.parkingId);
        }
    } catch (error) {
        console.error("Error fetching order list:", error);
    }
}

const goBack = () => {
    router.push('/');
}

const cancelOrder = async (orderId) => {
    router.push(`/my-ticket/cancel/${orderId}`);
};

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
        <div v-else-if="orderlist.length === 0" class="no-ticket-container">
            <div class="no-ticket-content">
                <h3>사용 가능한 주차권이 없습니다</h3>
                <p>새로운 주차권을 구매해보세요!</p>
                <button class="buy-ticket-button" @click="router.push('/search')">
                    주차권 구매하기
                </button>
            </div>
        </div>
        <swiper v-else
            :modules="[Pagination]"
            :slides-per-view="1"
            :space-between="50"
            :pagination="{ clickable: true }"
            class="mySwiper"
        >
            <swiper-slide
                v-for="(order, index) in orderlist"
                :key="index"
            >
                <div class="ticket-card">
                  <div
                        class="map-container"
                        v-if="parkingLocations[order.parkingId]"
                    >
                        <kakao-map
                            :lat="parkingLocations[order.parkingId].lat"
                            :lng="parkingLocations[order.parkingId].lng"
                            :level="4"
                            :draggable="false"
                            :disable-double-click-zoom="true"
                            :width="'100%'"
                            :height="250"
                            @onLoadKakaoMap="onLoadKakaoMap"
                        >
                            <KakaoMapMarker
                                :lat="parkingLocations[order.parkingId].lat"
                                :lng="parkingLocations[order.parkingId].lng"
                                :image="{
                                    imageSrc: '/src/assets/icons/search-marker.png',
                                    imageWidth: 37,
                                    imageHeight: 37,
                                    imageOption: {}
                                }"
                            />
                        </kakao-map>
                    </div>
                    <div class="ticket-header">
                        <h3>{{ parkingNames[order.parkingId] || '로딩중...' }}</h3>
                    </div>
                    <div class="info-items-container">
                        <div class="info-item">
                            <div class="info-content horizontal">
                                <label>상태</label>
                                <p
                                    class="info-value status-p"
                                    :class="{
                                        'status-waiting': order.status === '주차 대기',
                                        'status-parking': order.status === '주차중'
                                    }"
                                >
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

                        <div class="info-item">
                            <div class="info-content">
                                <label>{{ order.status === '주차중' ? '입차 시간' : '예상 시작 시간' }}</label>
                                <div class="info-row">
                                    <p class="info-value price-horizontal">{{ order.startTime }}</p>
                                </div>
                            </div>
                        </div>
                        <div class="info-item">
                            <div class="info-content">
                                <label>{{ order.status === '주차중' ? '예상 종료 시간' : '예상 종료 시간' }}</label>
                                <div class="info-row">
                                    <p class="info-value price-horizontal">{{ order.endTime }}</p>
                                </div>
                            </div>
                        </div>


                        <div v-if="order.status === '주차 대기'" class="cancel-button-container">
                            <div class="auto-cancel-notice">
                                <i class="notice-icon">⚠️</i>
                                <h5>예약 시간 내 입차하지 않을 경우 자동 취소됩니다</h5>
                            </div>
                            <button class="cancel-button" @click="cancelOrder(order.orderId)">
                                취소
                            </button>
                        </div>
                        <div v-if="order.status === '주차중'" class="overtime-notice-container">
                            <div class="auto-cancel-notice">
                                <i class="notice-icon">⚠️</i>
                                <h5>예상 종료시간 이후 출차 시 추가요금이 부과됩니다</h5>
                            </div>
                        </div>
                    </div>

                </div>
        </swiper-slide>
        </swiper>
    </div>
    <div class="ticket-history-button" @click="router.push('/ticket-history')">
        <div class="button-content">
            <div class="left-content">
                <div class="text-content">
                    <span class="history-text">지난 주차권 내역</span>
                    <span class="sub-text">이전 주차 기록을 확인해보세요</span>
                </div>
            </div>
            <span class="arrow">›</span>
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

.mySwiper {
    width: 100%;
    height: 100%;
}

.ticket-card {
    width: 100%;
    min-height: 200px;
    height: auto;
    background: white;
    border-radius: 15px;
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.car-num {
    font-size: 22px;
    font-weight: 600;
    color: #333;
    margin-bottom: 12px;
    text-align: center;
}

.ticket-header {
    margin-top: 15px;
    margin-bottom: 15px;
}

.ticket-header h3 {
    font-size: 16px;
    color: #2c3e50;
    margin-bottom: 0;
    text-align: center;
}

.status-p {
    font-size: 10px;
    color: #7d7d7d;
    margin-left: 8px;
    padding: 3px 8px;
    background: rgba(124, 140, 255, 0.1);
    border-radius: 20px;
}

.status-waiting {
    background-color: rgba(255, 171, 0, 0.15);
    color: #FF9800;
}

.status-parking {
    background-color: rgba(75, 192, 119, 0.15);
    color: #2ECC71;
}

.ticket-header h3 span {
    font-size: 13px;
    color: #7d7d7d;
    margin-left: 8px;
    padding: 3px 8px;
    background: rgba(124, 140, 255, 0.1);
    border-radius: 20px;
}

.info-items-container {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.info-item, .info-price {
    padding: 8px;
    border-radius: 8px;
    background: rgba(124, 140, 255, 0.03);
    transition: background-color 0.2s ease;
}

.info-content label {
    font-size: 12px;
    color: #666;
    display: block;
    margin-bottom: 4px;
}

.info-label {
    color: #666;
    font-size: 12px;
    margin: 0;
}

.info-value {
    color: #333;
    font-size: 14px;
    margin: 0;
    font-weight: 500;
}

.info-price .info-value {
    color: #7C9EFF;
    font-size: 16px;
    font-weight: 600;
}

:deep(.swiper-button-next),
:deep(.swiper-button-prev) {
    display: none;
}

:deep(.swiper-pagination-bullet-active) {
    background: #7C9EFF;
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

.info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.price-horizontal {
    margin-left: auto;
}

.info-content.horizontal {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.info-content.horizontal label {
    margin-bottom: 0;
}

.info-content.horizontal .info-value {
    margin-left: auto;
}

.map-container {
    margin-top: 20px;
    border-radius: 8px;
    overflow: hidden;
}

.cancel-button-container {
    margin-top: 12px;
    text-align: center;
}

.cancel-button {
    background-color: #FF6B6B;
    color: white;
    border: none;
    padding: 8px 20px;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.2s ease;
}

.cancel-button:hover {
  background: linear-gradient(135deg, #ff9a9a 0%, #ff8888 100%);
}

.ticket-history-button {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: white;
    padding: 16px 20px;
    box-shadow: 0 -4px 15px rgba(0, 0, 0, 0.08);
    cursor: pointer;
    transition: all 0.2s ease;
}

.ticket-history-button:active {
    background-color: #f8f9ff;
    transform: scale(0.99);
}

.button-content {
    max-width: 800px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.left-content {
    display: flex;
    align-items: center;
    gap: 12px;
}

.text-content {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.history-text {
    color: #333;
    font-size: 15px;
    font-weight: 600;
}

.sub-text {
    color: #888;
    font-size: 12px;
}

.arrow {
    color: #7C9EFF;
    font-size: 22px;
    font-weight: 600;
    margin-left: 10px;
    transition: transform 0.2s ease;
}

.ticket-history-button:hover .arrow {
    transform: translateX(3px);
}

@media screen and (max-width: 768px) {
    .ticket-card {
        min-height: 180px;
        padding: 15px;
        gap: 12px;
    }
}

@media screen and (max-width: 480px) {
    .ticket-card {
        min-height: 160px;
        padding: 12px;
        gap: 10px;
    }

    .ticket-history-button {
        padding: 14px 16px;
    }

    .history-icon {
        font-size: 18px;
        padding: 6px;
    }

    .sub-text {
        font-size: 11px;
    }
}

.auto-cancel-notice {
    margin: 0 0 12px 0;
    display: flex;
    align-items: center;
    background: linear-gradient(to right, rgba(255, 171, 0, 0.05), rgba(255, 171, 0, 0.1));
    border-left: 3px solid #FFAB00;
    padding: 10px 15px;
    border-radius: 8px;
}

.notice-icon {
    font-size: 14px;
    margin-right: 8px;
}

.auto-cancel-notice h5 {
    color: #666;
    font-size: 12px;
    font-weight: 500;
    margin: 0;
    line-height: 1.4;
}

.swiper {
    width: 100%;
    height: auto;
    min-height: 300px;
}

.swiper-slide {
    height: auto;
    display: flex;
    align-items: stretch;
    padding: 10px;
    margin-bottom:30px;
}

.overtime-notice-container {
    margin-top: 12px;
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

.empty-ticket-icon {
    width: 120px;
    height: 120px;
    margin-bottom: 20px;
    opacity: 0.8;
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
