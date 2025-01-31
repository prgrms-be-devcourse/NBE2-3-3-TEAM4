<script setup>
import NavBar from '../components/NavBar.vue';
import InputBar from '../components/InputBar.vue';
import { useRouter } from 'vue-router';
import { ref } from 'vue';
import axios from '../utils/axios-non';
import { getLocation } from '../utils/geolocation';
import { onMounted } from 'vue';
import { KakaoMap, KakaoMapMarker } from 'vue3-kakao-maps';
import { usePlaceStore } from '../stores/place';

const map = ref(null);
const onLoadKakaoMap = (mapRef) => {
  map.value = mapRef;
  map.value.setLevel(4);

  if (placeStore.selectedPlace) {
    map.value.panTo(new kakao.maps.LatLng(placeStore.selectedPlace.y, placeStore.selectedPlace.x));
  }
};


const router = useRouter();

const isShowSearchResult = ref(false);
const searchKeyword = ref('');
const searchResult = ref([]);

async function handleSearch(keyword) {
  searchKeyword.value = keyword;
  try {
    const response = await axios.get(`/api/auth/kakao/search?keyword=${searchKeyword.value}&size=15`);
    searchResult.value = response.data.data;
  } catch {
    alert('예기치 않은 오류가 발생했습니다.');
  }
}
const handleClick = () => {
  isShowSearchResult.value = true;
}

const closeResults = () => {
  isShowSearchResult.value = false;
}

const goBack = () => {
  if(isShowSearchResult.value) {
    closeResults();
  } else {
    placeStore.setSelectedPlace(null);
    router.push('/');
  }
}

const currentLocation = ref({
  latitude: null,
  longitude: null
});

const placeStore = usePlaceStore();
const isLoading = ref(true);

onMounted(async () => {
  try {
    const location = await getLocation();
    currentLocation.value.latitude = location.latitude;
    currentLocation.value.longitude = location.longitude;

    if (placeStore.selectedPlace) {
      await getNearbyParking(placeStore.selectedPlace.y, placeStore.selectedPlace.x);
    } else {
      await getNearbyParking(location.latitude, location.longitude);
    }
  } finally {
    isLoading.value = false;
  }
});

function calculateDistance(lat1, lon1, lat2, lon2) {
  const R = 6371;
  const dLat = (lat2 - lat1) * Math.PI / 180;
  const dLon = (lon2 - lon1) * Math.PI / 180;
  const a =
    Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
    Math.sin(dLon/2) * Math.sin(dLon/2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  const distance = R * c;
  return distance.toFixed(1);
}

const nearbyParking = ref([]);

const getNearbyParking = async (lat, lng) => {
  const response = await axios.get(`/api/parking/search?lat=${lat}&lng=${lng}`);
  nearbyParking.value = response.data.data;
}

const handleClickPlace = (place) => {
  isShowSearchResult.value = false;
  placeStore.setSelectedPlace(place);

  getNearbyParking(place.y, place.x);

  if(map.value) {
    map.value.panTo(new kakao.maps.LatLng(place.y, place.x));
  }
}

const getMarkerImage = (status) => {
  if(status === '여유') {
    return '../src/assets/icons/parking-available.png';
  } else if(status === '보통') {
    return '../src/assets/icons/parking-normal.png';
  } else {
    return '../src/assets/icons/parking-busy.png';
  }
}

const handleClickParking = (id) => {
  router.push(`/parking/${id}`);
}

const handleClickStatus = (parking) => {
  if(map.value) {
    map.value.panTo(new kakao.maps.LatLng(parking.latitude, parking.longitude));
  }
}

</script>


<template>
  <NavBar />
  <InputBar @back="goBack" @search="handleSearch" @click="handleClick"/>
  <div v-if="!isLoading && currentLocation.latitude && currentLocation.longitude">
    <KakaoMap
      :lat="currentLocation.latitude"
      :lng="currentLocation.longitude"
      :draggable="true"
      :disable-double-click-zoom="true"
      :width="'100%'"
      :height="1000"
      id="map"
      @onLoadKakaoMap="onLoadKakaoMap"
    >
      <KakaoMapMarker
        :lat="currentLocation.latitude"
        :lng="currentLocation.longitude"
        :image="{
          imageSrc: '../src/assets/icons/marker-mylocation.png',
          imageWidth: 37,
          imageHeight: 37,
          imageOption: {}
        }"
      />
      <KakaoMapMarker v-if="placeStore.selectedPlace"
      :lat="placeStore.selectedPlace.y"
      :lng="placeStore.selectedPlace.x"
      :image="{
        imageSrc: '../src/assets/icons/search-marker.png',
        imageWidth: 37,
        imageHeight: 37,
        imageOption: {}
      }"
      />
      <KakaoMapMarker v-for="(parking, index) in nearbyParking"
      :key="index"
      :lat="parking.latitude"
      :lng="parking.longitude"
      :id="parking.id"
      :clickable="true"
      :image="{
        imageSrc: getMarkerImage(parking.status),
        imageOption: {}
      }"
      @onClickKakaoMapMarker="handleClickParking(parking.id)"
      />
    </KakaoMap>
    <div class="parking-footer">
      <div class="status-header">
        <span>주변 주차장</span>
        <span class="found-count" v-if="nearbyParking.length > 0">
          {{ nearbyParking.length }}개 발견됨
        </span>
      </div>
      <div v-if="nearbyParking.length === 0" class="no-data">
        주변에 주차장이 없습니다.
      </div>
      <div v-else class="parking-list">
        <div v-for="parking in nearbyParking.slice(0, 2)" :key="parking.id" class="parking-item" @click="handleClickStatus(parking)">
          <div class="parking-info">
            <span class="label">{{ parking.name }}</span>
          </div>
          <span class="status" :class="{
            'available': parking.status === '여유',
            'normal': parking.status === '보통',
            'busy': parking.status === '혼잡'
          }">
            {{ parking.status }}
          </span>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="loading-container">
    <p>지도를 불러오는 중입니다...</p>
  </div>

   <div v-if="isShowSearchResult" class="search-results-overlay">
    <div v-if="searchResult.length > 0" class="results-container">
      <div v-for="place in searchResult" :key="place.placeId" class="result-item" @click="handleClickPlace(place)">
        <div class="place-header">
          <h4 class="place-name">{{ place.name }}</h4>
          <span class="distance" v-if="currentLocation.latitude && currentLocation.longitude">
            {{ calculateDistance(currentLocation.latitude, currentLocation.longitude, place.y, place.x) }}km
          </span>
        </div>
        <p class="address-primary">
          <span class="address-label road">도로명</span>
          {{ place.roadAddress }}
        </p>
        <p class="address-secondary">
          <span class="address-label jibun">지번</span>
          {{ place.address }}
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.search-results-overlay {
  position: fixed;
  top: 120px;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.98);
  z-index: 1000;
  overflow-y: auto;
  border-top: 1px solid #e0e0e0;
}

.results-container {
  max-width: 100%;
  margin: 0 auto;
}

.result-item {
  margin: 8px 0;
  background-color: #ffffff;
  border-bottom: 1px solid #edf2f7;
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 10px 20px 10px;
}

.place-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.distance {
  font-size: 0.9rem;
  color: #666;
  background-color: rgba(255, 255, 255, 0.8);
  padding: 2px 8px;
  border-radius: 12px;
  font-weight: 500;
}

.place-name {
  margin: 0;
}

.address-primary {
  font-size: 0.9rem;
  color: #666;
  margin: 4px 0;
}

.address-secondary {
  font-size: 0.9rem;
  color: #666;
  margin: 4px 0;
}

.address-label {
  display: inline-block;
  font-size: 0.7rem;
  padding: 2px 6px;
  border-radius: 4px;
  margin-right: 6px;
  font-weight: 500;
  width: 48px;
  text-align: center;
}

.address-label.road {
  background-color: #7C9EFF;
  color: white;
}

.address-label.jibun {
  background-color: #ff9a9a;
  color: white;
}

.parking-footer {
  background-color: white;
  padding: 1rem 1rem;
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 999;
  border-top: 1px solid #e0e0e0;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 1rem;
  color: #1a1a1a;
}

.found-count {
  color: #696969;
  font-weight: bold;
  font-size: 13px;
}

.parking-list {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.parking-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0.4rem;
  border-radius: 8px;
  background-color: #f5f5f5;
  transition: all 0.2s ease;
}

.parking-item:hover {
  transform: translateY(-1px);
}

.parking-info {
  flex: 1;
  min-width: 0;
  margin-right: 10px;
}

.label {
  font-size: 14px;
  color: #333333;
  font-weight: bold;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  margin-left: 10px;
  display: block;
}

.status {
  font-size: 14px;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: bold;
  white-space: nowrap;
  flex-shrink: 0;
}

.available {
  color: #2ea043;
}

.normal {
  color: #eec927;
}

.busy {
  color: #ff4646;
}

.no-data {
  text-align: center;
  color: #666;
  padding: 0.7rem;
  background-color: #f5f5f5;
  font-weight: bold;
  border-radius: 8px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-weight: bold;
  color: #666;
}
</style>
