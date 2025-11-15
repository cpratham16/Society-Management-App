import axiosInstance from '../http';
import { API_ENDPOINTS } from '../http/apiConfig';

export const serviceService = {
  getAllServices: async () => {
    const response = await axiosInstance.get(API_ENDPOINTS.GET_ALL_SERVICES);
    return response.data;
  },

  createService: async (serviceData) => {
    const response = await axiosInstance.post(API_ENDPOINTS.CREATE_SERVICE, serviceData);
    return response.data;
  },

  updateService: async (id, serviceData) => {
    const response = await axiosInstance.put(API_ENDPOINTS.UPDATE_SERVICE(id), serviceData);
    return response.data;
  },

  deleteService: async (id) => {
    const response = await axiosInstance.delete(API_ENDPOINTS.DELETE_SERVICE(id));
    return response.data;
  },

  getAdvertise: async () => {
    const response = await axiosInstance.get('/service/advertise');
    return response.data;
  },
};

export default serviceService;
