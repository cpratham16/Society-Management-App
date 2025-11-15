import axiosInstance from '../http';
import { API_ENDPOINTS } from '../http/apiConfig';

export const eventService = {
  getAllEvents: async () => {
    const response = await axiosInstance.get(API_ENDPOINTS.GET_ALL_EVENTS);
    return response.data;
  },

  createEvent: async (eventData) => {
    const response = await axiosInstance.post(API_ENDPOINTS.CREATE_EVENT, eventData);
    return response.data;
  },

  updateEvent: async (id, eventData) => {
    const response = await axiosInstance.put(API_ENDPOINTS.UPDATE_EVENT(id), eventData);
    return response.data;
  },

  deleteEvent: async (id) => {
    const response = await axiosInstance.delete(API_ENDPOINTS.DELETE_EVENT(id));
    return response.data;
  },
};

export default eventService;
