<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const toHomePage = () => {
  router.push('/');
};
</script>

<template>
<nav class="navigation-bar">
    <div class="nav-content">
      <div class="logo" @click="toHomePage">
        <span>뚝딱파킹 🚗</span>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.navigation-bar {
  width: 100%;
  background-color: #ffffff;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  margin: 0 auto;
}

.logo {
  font-size: 21px;
  font-weight: bold;
  cursor: pointer;
}

.user-icon-btn {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.user-icon-btn img {
  height: 21px;
}

.user-icon-btn:hover {
  opacity: 0.8;
}
</style>
