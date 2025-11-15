import axiosInstance from '../http';
import { API_ENDPOINTS } from '../http/apiConfig';

export const galleryService = {
  getAllGallery: async () => {
    const response = await axiosInstance.get(API_ENDPOINTS.GET_ALL_GALLERY);
    return response.data;
  },

  uploadGallery: async (formData) => {
    const response = await axiosInstance.post(API_ENDPOINTS.UPLOAD_GALLERY, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },

  deleteGallery: async (id) => {
    const response = await axiosInstance.delete(API_ENDPOINTS.DELETE_GALLERY(id));
    return response.data;
  },
};

export default galleryService;
