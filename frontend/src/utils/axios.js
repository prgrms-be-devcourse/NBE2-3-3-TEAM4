import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000,
  headers: { 'Content-Type': 'application/json' },
});

instance.interceptors.request.use(function (config) {
  const accessToken = localStorage.getItem('accessToken');

  if(accessToken) {
    config.headers['authorization'] = `Bearer ${accessToken}`;
  }

  return config;
}, function (error) {
  return Promise.reject(error);
});

instance.interceptors.response.use(function (response) {
  return response;
}, async function (error) {

  if(error.response.data.message.includes('만료')) {
    const refreshToken = localStorage.getItem('refreshToken');

    const response = await instance.post('/api/auth/reissue',
      { refreshToken }, {
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );

    localStorage.setItem('accessToken', response.data.data.accessToken);
    localStorage.setItem('refreshToken', response.data.data.refreshToken);


    return instance(error.config);
  }
  return Promise.reject(error);
});

export default instance;
