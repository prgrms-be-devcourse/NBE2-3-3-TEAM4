import { defineStore } from 'pinia';
import { ref } from 'vue';

export const usePlaceStore = defineStore('place', () => {
  const selectedPlace = ref(null);

  function setSelectedPlace(place) {
    selectedPlace.value = place;
  }

  return {
    selectedPlace,
    setSelectedPlace,
  };
});
