import axiosInstance from '../http';
import { API_ENDPOINTS } from '../http/apiConfig';

export const adminService = {
  getAllUsers: async () => {
    const response = await axiosInstance.get(API_ENDPOINTS.ADMIN_GET_ALL_USERS);
    return response.data;
  },

  deleteUser: async (id) => {
    const response = await axiosInstance.delete(API_ENDPOINTS.ADMIN_DELETE_USER(id));
    return response.data;
  },

  updateUser: async (id, userData) => {
    const response = await axiosInstance.put(API_ENDPOINTS.ADMIN_UPDATE_USER(id), userData);
    return response.data;
  },

  getSettings: async () => {
    const response = await axiosInstance.get(API_ENDPOINTS.ADMIN_GET_SETTINGS);
    return response.data;
  },

  updateSettings: async (settingsData) => {
    const response = await axiosInstance.put(API_ENDPOINTS.ADMIN_UPDATE_SETTINGS, settingsData);
    return response.data;
  },
};

export default adminService;
