import axiosInstance from '../http';
import { API_ENDPOINTS } from '../http/apiConfig';

export const contactService = {
  createContact: async (contactData) => {
    const response = await axiosInstance.post(API_ENDPOINTS.CREATE_CONTACT, contactData);
    return response.data;
  },

  getAllContacts: async () => {
    const response = await axiosInstance.get(API_ENDPOINTS.GET_ALL_CONTACTS);
    return response.data;
  },

  deleteContact: async (id) => {
    const response = await axiosInstance.delete(API_ENDPOINTS.DELETE_CONTACT(id));
    return response.data;
  },
};

export default contactService;
