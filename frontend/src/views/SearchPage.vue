<script setup>
import NavBar from '../components/NavBar.vue';
import InputBar from '../components/InputBar.vue';
import { useRouter } from 'vue-router';
import { ref } from 'vue';
import axios from '../utils/axios-non';
import { getLocation } from '../utils/geolocation';
import { onMounted } from 'vue';
import { KakaoMap } from 'vue3-kakao-maps';

const map = ref(null);
const onLoadKakaoMap = (mapRef) => {
  map.value = mapRef;
};

const router = useRouter();
function goBack() {
  router.push('/');
}

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

const currentLocation = ref({});
onMounted(async () => {
  currentLocation.value = await getLocation();
});

</script>


<template>
  <NavBar />
  <InputBar @back="goBack" @search="handleSearch"/>
  <KakaoMap
                :lat="currentLocation.latitude"
                :lng="currentLocation.longitude"
                :draggable="true"
                :disable-double-click-zoom="true"
                :width="'100%'"
                :height="750"
                id="map"
                @onLoadKakaoMap="onLoadKakaoMap"
            >
</template>

<style scoped>

</style>
