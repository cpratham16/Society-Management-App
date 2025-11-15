import axios from 'axios';
import API_BASE_URL from './apiConfig';

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) throw new Error('No refresh token');

        const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
          refreshToken,
        });

        const { accessToken } = response.data.data;
        localStorage.setItem('accessToken', accessToken);

        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return axiosInstance(originalRequest);
      } catch (err) {
        localStorage.clear();
        window.location.href = '/';
        return Promise.reject(err);
      }
    }

    return Promise.reject(error);
  }
);

// OLD NODE.JS API FUNCTIONS - UPDATE TO SPRING BOOT FORMAT

// AUTH APIs
export const login = async (data) => {
  const response = await axiosInstance.post('/auth/login', data.data);
  if (response.data.success) {
    localStorage.setItem('accessToken', response.data.data.accessToken);
    localStorage.setItem('refreshToken', response.data.data.refreshToken);
  }
  return { data: response.data };
};

export const register = async (data) => {
  const response = await axiosInstance.post('/auth/register', {
    ...data.data,
    societyCode: data.data.society_code,
  });
  return { data: response.data };
};

export const sendOtp = async (data) => {
  const response = await axiosInstance.post('/auth/resend-otp', data);
  return { data: response.data };
};

export const verifyOtp = async (data) => {
  const response = await axiosInstance.post('/auth/verify-otp', data);
  return { data: response.data };
};

export const forgotPassword = async (data) => {
  const response = await axiosInstance.post('/auth/forgot-password', data);
  return { data: response.data };
};

export const changePassword = async (data) => {
  const response = await axiosInstance.post('/auth/reset-password', data);
  return { data: response.data };
};

export const logout = () => {
  localStorage.clear();
  window.location.href = '/';
};

// USER APIs
export const getProfile = async (data) => {
  const response = await axiosInstance.get(`/user/members/${data.publicUrl}`);
  return { data: response.data };
};

export const getMembers = async () => {
  const response = await axiosInstance.get('/user/members');
  return { data: response.data };
};

export const updateUser = async (data) => {
  const response = await axiosInstance.put('/user/profile', data);
  return { data: response.data };
};

export const userOperations = async (data) => {
  const { type, formData } = data;
  if (type === 'add') {
    const response = await axiosInstance.post('/services', formData);
    return { data: response.data };
  } else if (type === 'delete') {
    const response = await axiosInstance.delete(`/services/${formData.id}`);
    return { data: response.data };
  }
};

// HOME PAGE
export const getHomePageData = async () => {
  const response = await axiosInstance.get('/events');
  return { data: response.data };
};

// EVENTS
export const getEvents = async () => {
  const response = await axiosInstance.get('/events');
  return { data: response.data };
};

// ADVERTISE (Services)
export const getAdvertise = async () => {
  const response = await axiosInstance.get('/services');
  return { data: response.data };
};

// ADMIN
export const adminDataOperation = async (data) => {
  const { type, path, formData } = data;
  
  if (type === 'get') {
    let endpoint = '';
    if (path === 'complaines') endpoint = '/complaints';
    else if (path === 'events') endpoint = '/events';
    else if (path === 'contacts') endpoint = '/contact';
    else if (path === 'management') endpoint = '/admin/management';
    
    const response = await axiosInstance.get(endpoint);
    return { data: response.data };
  } 
  else if (type === 'add') {
    let endpoint = '';
    if (path === 'events') endpoint = '/events';
    else if (path === 'management') endpoint = '/admin/management';
    
    const response = await axiosInstance.post(endpoint, formData);
    return { data: response.data };
  }
  else if (type === 'delete') {
    let endpoint = '';
    if (path === 'complaines') endpoint = `/complaints/${formData.id}`;
    else if (path === 'events') endpoint = `/events/${formData.id}`;
    else if (path === 'contacts') endpoint = `/contact/${formData.id}`;
    else if (path === 'management') endpoint = `/admin/management/${formData.id}`;
    
    const response = await axiosInstance.delete(endpoint);
    return { data: response.data };
  }
};

export const deleteUser = async (data) => {
  const response = await axiosInstance.delete(`/admin/users/${data.id}`);
  return { data: response.data };
};

export const adminSetting = async (data) => {
  if (data.type === 'get') {
    const response = await axiosInstance.get('/admin/settings');
    return { data: response.data };
  } else if (data.type === 'update') {
    const response = await axiosInstance.put('/admin/settings', data.formData);
    return { data: response.data };
  }
};

export const logoutAdmin = logout;
export const registerAdmin = register;
export const verifyAdmin = verifyOtp;

export const refresh = async () => {
  const token = localStorage.getItem('refreshToken');
  const response = await axiosInstance.post('/auth/refresh', { refreshToken: token });
  if (response.data.success) {
    localStorage.setItem('accessToken', response.data.data.accessToken);
  }
  return { data: response.data };
};

export const activate = async (data) => {
  const response = await axiosInstance.post('/auth/verify-otp', data);
  return { data: response.data };
};

export default axiosInstance;
