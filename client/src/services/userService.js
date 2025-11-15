import axiosInstance from '../http';
import { API_ENDPOINTS } from '../http/apiConfig';

export const userService = {
  getProfile: async () => {
    const response = await axiosInstance.get(API_ENDPOINTS.USER_PROFILE);
    return response.data;
  },

  updateProfile: async (userData) => {
    const response = await axiosInstance.put(API_ENDPOINTS.UPDATE_PROFILE, userData);
    return response.data;
  },

  getMembers: async () => {
    const response = await axiosInstance.get('/user/members');
    return response.data;
  },

  deleteUser: async (userData) => {
    const response = await axiosInstance.delete('/user/delete', { data: userData });
    return response.data;
  },

  getHomePageData: async () => {
    const response = await axiosInstance.get('/user/home-data');
    return response.data;
  },
};

export default userService;
