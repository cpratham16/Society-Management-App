import axios from 'axios';
import API_BASE_URL from './apiConfig';
import { defaultImg } from '../defaultImg';

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

const LOGGED_OUT_STATE = {
  auth: false,
  activate: false,
  isAdmin: false,
  user: null,
};

const normalizeFamilyMember = (member = {}) => ({
  id: member.id || '',
  name: member.name || '',
  relation: member.relation || '',
  proffession: member.proffession || member.profession || '',
  gender: member.gender || '',
  age: member.age ?? '',
  img: member.photoUrl || defaultImg,
  photoUrl: member.photoUrl || defaultImg,
});

const normalizeUserResponse = (user = {}) => {
  const familyMembers = Array.isArray(user.familyMembers) ? user.familyMembers : [];
  const advertises = Array.isArray(user.advertises) ? user.advertises : [];

  return {
    id: user.id || '',
    name: user.name || '',
    email: user.email || '',
    familyName: user.name || '',
    phone: user.phone || '',
    phoneNo: user.phone || '',
    profession: user.profession || '',
    proffession: user.profession || '',
    proffessionDiscription: user.profession ? `Working as ${user.profession}` : '',
    societyCode: user.societyCode || '',
    houseNo: user.houseNo || '',
    profilePhotoUrl: user.profilePhotoUrl || defaultImg,
    profileImg: user.profilePhotoUrl || defaultImg,
    totalMembers: familyMembers.length,
    members: familyMembers.map(normalizeFamilyMember),
    familyMembers,
    advertises,
    role: user.role || 'MEMBER',
  };
};

const buildSuccessSessionPayload = (user, message = '') => {
  const role = (user?.role || '').toUpperCase();
  const isAdmin = role === 'ADMIN';
  return {
    success: true,
    message,
    auth: true,
    activate: true,
    isAdmin,
    path: isAdmin ? '/' : '/profile',
    user,
  };
};

const buildFailurePayload = (message = 'Something went wrong') => ({
  success: false,
  message,
  ...LOGGED_OUT_STATE,
});

const buildLoggedOutPayload = (message = 'Logged out successfully') => ({
  success: true,
  message,
  ...LOGGED_OUT_STATE,
});

const fetchCurrentUserProfile = async () => {
  const response = await axiosInstance.get('/user/profile');
  if (!response.data?.success) {
    throw new Error(response.data?.message || 'Unable to load profile');
  }
  return normalizeUserResponse(response.data.data || {});
};

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
export const login = async (payload = {}) => {
  const credentials = payload.data ?? payload;
  try {
    const response = await axiosInstance.post('/auth/login', credentials);
    if (!response.data?.success) {
      return { data: buildFailurePayload(response.data?.message || 'Invalid credentials') };
    }

    const { accessToken, refreshToken } = response.data.data || {};
    if (accessToken) {
      localStorage.setItem('accessToken', accessToken);
    }
    if (refreshToken) {
      localStorage.setItem('refreshToken', refreshToken);
    }

    try {
      const user = await fetchCurrentUserProfile();
      return { data: buildSuccessSessionPayload(user, response.data.message || 'Login successful') };
    } catch (profileError) {
      return { data: buildFailurePayload(profileError.message) };
    }
  } catch (error) {
    const message = error.response?.data?.message || 'Unable to login. Please check your credentials.';
    return { data: buildFailurePayload(message) };
  }
};

export const register = async (data) => {
  const payload = {
    name: data.data.name,
    email: data.data.email,
    password: data.data.password,
    phone: data.data.phone,
    profession: data.data.profession,
    societyCode: data.data.society_code,
    houseNo: data.data.houseNo,
  };
  const response = await axiosInstance.post('/auth/register', payload);
  return { data: response.data };
};

export const sendOtp = async (data) => {
  const payload = data?.data ?? data;
  const response = await axiosInstance.post('/auth/resend-otp', payload);
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

export const logout = async () => {
  localStorage.clear();
  window.location.href = '/';
  return { data: buildLoggedOutPayload() };
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
  const [eventsResult, managementResult, advertiseResult] = await Promise.allSettled([
    axiosInstance.get('/events/upcoming'),
    axiosInstance.get('/management'),
    axiosInstance.get('/advertisements'),
  ]);

  const normalizeCollection = (result) => {
    if (!result || result.status !== 'fulfilled') return [];
    const payload = result.value?.data?.data;
    if (Array.isArray(payload)) return payload;
    if (Array.isArray(payload?.content)) return payload.content;
    return [];
  };

  return {
    data: {
      events: normalizeCollection(eventsResult),
      management: normalizeCollection(managementResult),
      advertise: normalizeCollection(advertiseResult),
    },
  };
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

export const verifyAdmin = async (data) => {
  const response = await axiosInstance.post('/auth/admin/initiate', data);
  return { data: response.data };
};

export const registerAdmin = async (data) => {
  const response = await axiosInstance.post('/auth/admin/complete', data);
  return { data: response.data };
};

export const refresh = async () => {
  const token = localStorage.getItem('refreshToken');
  if (!token) {
    return { data: buildFailurePayload('No session found') };
  }

  try {
    const response = await axiosInstance.post('/auth/refresh', { refreshToken: token });
    if (!response.data?.success) {
      return { data: buildFailurePayload(response.data?.message || 'Session expired') };
    }

    const { accessToken } = response.data.data || {};
    if (accessToken) {
      localStorage.setItem('accessToken', accessToken);
    }

    try {
      const user = await fetchCurrentUserProfile();
      return { data: buildSuccessSessionPayload(user, response.data.message || 'Session refreshed') };
    } catch (profileError) {
      return { data: buildFailurePayload(profileError.message) };
    }
  } catch (error) {
    const message = error.response?.data?.message || 'Session expired';
    return { data: buildFailurePayload(message) };
  }
};

export default axiosInstance;
