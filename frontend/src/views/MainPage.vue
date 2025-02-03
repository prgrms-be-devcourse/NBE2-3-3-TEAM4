<script setup >
    import { useRouter } from 'vue-router';
    import NavBar from '@/components/NavBar.vue';
    import { usePlaceStore } from '../stores/place';
    import { onMounted } from 'vue';
    const placeStore = usePlaceStore();

    const router = useRouter();

    const toMyTicketPage = () => {
        router.push('/my-ticket');
    }

    const toSearchPage = () => {
        router.push('/search');
    }

    onMounted(() => {
        placeStore.setSelectedPlace(null);
    })
</script>

<template>
    <NavBar />
    <div class="main-container">
      <!-- 주차권 구매 섹션 -->
    <div class="ticket-section purchase">
      <div class="section-header">
        <img src="@/assets/icons/car-icon.png" alt="차 아이콘">
        <h3>주차권 구매</h3>
      </div>
      <p class="description">새로운 주차권을 구매하세요</p>
      <button class="action-button purchase" @click="toSearchPage">주차권 구매하기</button>
    </div>
    <!-- 주차권 확인 섹션 -->
    <div class="ticket-section discount">
      <div class="section-header">
        <img src="@/assets/icons/ticket-icon.png" alt="티켓 아이콘">
        <h3>주차권 확인</h3>
      </div>
      <p class="description">구매한 주차권을 확인하세요</p>
      <button class="action-button" @click="toMyTicketPage">주차권 확인하기</button>
    </div>
  </div>
</template>

<style scoped>
.main-container {
  padding: 20px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 90%;
  max-width: 500px;
}

.ticket-section {
  background: white;
  padding: 30px;
  margin-bottom: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 0;
}

.description {
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
  font-weight: 500;
}

.action-button {
  width: 100%;
  padding: 15px;
  border: none;
  border-radius: 12px;
  color: white;
  font-weight: 700;
  cursor: pointer;
  font-size: 16px;
  background: linear-gradient(135deg, #7C9EFF 0%, #5C7CFF 100%);
  transition: all 0.2s ease;
}

.action-button.purchase {
  background: linear-gradient(135deg, #ff9a9a 0%, #ff8888 100%);
}
.action-button:hover {
  transform: translateY(-1px);
  filter: brightness(1.1);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.section-header img {
  width: 28px;
  height: 28px;
}

.section-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #333;
}
</style>
