const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const API_ENDPOINTS = {
  // AUTH
  REGISTER: `${API_BASE_URL}/auth/register`,
  LOGIN: `${API_BASE_URL}/auth/login`,
  VERIFY_OTP: `${API_BASE_URL}/auth/verify-otp`,
  RESEND_OTP: `${API_BASE_URL}/auth/resend-otp`,
  FORGOT_PASSWORD: `${API_BASE_URL}/auth/forgot-password`,
  RESET_PASSWORD: `${API_BASE_URL}/auth/reset-password`,
  REFRESH_TOKEN: `${API_BASE_URL}/auth/refresh`,

  // USER
  USER_PROFILE: `${API_BASE_URL}/user/profile`,
  UPDATE_PROFILE: `${API_BASE_URL}/user/profile`,
  GET_ALL_MEMBERS: `${API_BASE_URL}/user/members`,

  // EVENTS
  GET_ALL_EVENTS: `${API_BASE_URL}/events`,
  CREATE_EVENT: `${API_BASE_URL}/events`,
  UPDATE_EVENT: (id) => `${API_BASE_URL}/events/${id}`,
  DELETE_EVENT: (id) => `${API_BASE_URL}/events/${id}`,

  // COMPLAINTS
  GET_ALL_COMPLAINTS: `${API_BASE_URL}/complaints`,
  CREATE_COMPLAINT: `${API_BASE_URL}/complaints`,
  UPDATE_COMPLAINT: (id) => `${API_BASE_URL}/complaints/${id}`,
  DELETE_COMPLAINT: (id) => `${API_BASE_URL}/complaints/${id}`,

  // SERVICES (Advertise)
  GET_ALL_SERVICES: `${API_BASE_URL}/services`,
  CREATE_SERVICE: `${API_BASE_URL}/services`,
  UPDATE_SERVICE: (id) => `${API_BASE_URL}/services/${id}`,
  DELETE_SERVICE: (id) => `${API_BASE_URL}/services/${id}`,

  // GALLERY
  GET_ALL_GALLERY: `${API_BASE_URL}/gallery`,
  UPLOAD_GALLERY: `${API_BASE_URL}/gallery`,
  DELETE_GALLERY: (id) => `${API_BASE_URL}/gallery/${id}`,

  // CONTACT
  CREATE_CONTACT: `${API_BASE_URL}/contact`,
  GET_ALL_CONTACTS: `${API_BASE_URL}/contact`,

  // ADMIN
  ADMIN_GET_ALL_USERS: `${API_BASE_URL}/admin/users`,
  ADMIN_DELETE_USER: (id) => `${API_BASE_URL}/admin/users/${id}`,
  ADMIN_GET_SETTINGS: `${API_BASE_URL}/admin/settings`,
  ADMIN_UPDATE_SETTINGS: `${API_BASE_URL}/admin/settings`,
  ADMIN_REGISTER_INIT: `${API_BASE_URL}/auth/admin/initiate`,
  ADMIN_REGISTER_COMPLETE: `${API_BASE_URL}/auth/admin/complete`,
};

export default API_BASE_URL;
