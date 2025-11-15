import axiosInstance from '../http';
import { API_ENDPOINTS } from '../http/apiConfig';

export const complaintService = {
  getAllComplaints: async () => {
    const response = await axiosInstance.get(API_ENDPOINTS.GET_ALL_COMPLAINTS);
    return response.data;
  },

  createComplaint: async (complaintData) => {
    const response = await axiosInstance.post(API_ENDPOINTS.CREATE_COMPLAINT, complaintData);
    return response.data;
  },

  updateComplaint: async (id, complaintData) => {
    const response = await axiosInstance.put(API_ENDPOINTS.UPDATE_COMPLAINT(id), complaintData);
    return response.data;
  },

  deleteComplaint: async (id) => {
    const response = await axiosInstance.delete(API_ENDPOINTS.DELETE_COMPLAINT(id));
    return response.data;
  },
};

export default complaintService;
