import axiosInstance from '../http';
import { API_ENDPOINTS } from '../http/apiConfig';

const authService = {
  register: async (userData) => {
    const response = await axiosInstance.post(API_ENDPOINTS.REGISTER, {
      name: userData.name,
      email: userData.email,
      password: userData.password,
      phone: userData.phone,
      profession: userData.profession || 'Resident',
      societyCode: userData.society_code,
    });
    return response.data;
  },

  login: async (email, password) => {
    const response = await axiosInstance.post(API_ENDPOINTS.LOGIN, {
      email,
      password,
    });
    if (response.data.success) {
      const { accessToken, refreshToken, user } = response.data.data;
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('user', JSON.stringify(user));
    }
    return response.data;
  },

  verifyOtp: async (email, otp) => {
    const response = await axiosInstance.post(API_ENDPOINTS.VERIFY_OTP, { email, otp });
    return response.data;
  },

  resendOtp: async (email) => {
    const response = await axiosInstance.post(API_ENDPOINTS.RESEND_OTP, { email });
    return response.data;
  },

  forgotPassword: async (email) => {
    const response = await axiosInstance.post(API_ENDPOINTS.FORGOT_PASSWORD, { email });
    return response.data;
  },

  resetPassword: async (email, otp, newPassword) => {
    const response = await axiosInstance.post(API_ENDPOINTS.RESET_PASSWORD, { 
      email, 
      otp, 
      newPassword 
    });
    return response.data;
  },

  refreshToken: async (refreshToken) => {
    try {
      const response = await axiosInstance.post(API_ENDPOINTS.REFRESH_TOKEN, {
        refreshToken,
      });
      
      if (response.data.success) {
        const { accessToken, refreshToken: newRefreshToken } = response.data.data;
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', newRefreshToken);
      }
      
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: error.message };
    }
  },

  logout: () => {
    localStorage.clear();
    window.location.href = '/login';
  },

  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('accessToken');
  },
};

export const logoutAdmin = authService.logout;
export const verifyAdmin = authService.verifyOtp;

export default authService;
