import axios from "axios";
import API_BASE_URL from "./auth.config";

const register_req = async (username, email, password) => {
  return await axios.post(API_BASE_URL + "/auth/signup", {
    userName: username,
    email,
    password,
  });
};

const normalizeUser = (payload) => {
  if (!payload) return null;

  return {
    ...payload,
    type: payload.type || "Bearer",
    roles: Array.isArray(payload.roles) ? payload.roles : [],
    organizationId: payload.organizationId ?? null,
    organizationName: payload.organizationName ?? "",
  };
};

const login_req = async (email, password) => {
  const response = await axios.post(API_BASE_URL + "/auth/signin", { email, password });

  if (response.data.token) {
    localStorage.setItem("user", JSON.stringify(normalizeUser(response.data)));
  }

  return response;
};

const verifyRegistrationVerificationCode = async (verificationCode) => {
  return await axios.get(API_BASE_URL + "/auth/signup/verify", {
    params: {
      code: verificationCode,
    },
  });
};

const resendRegistrationVerificationCode = async (email) => {
  return await axios.get(API_BASE_URL + "/auth/signup/resend", {
    params: {
      email,
    },
  });
};

const getCurrentUser = () => {
  const rawUser = localStorage.getItem("user");
  if (!rawUser) return null;

  try {
    return normalizeUser(JSON.parse(rawUser));
  } catch (error) {
    localStorage.removeItem("user");
    return null;
  }
};

const hasRole = (role) => {
  const user = getCurrentUser();
  return !!user && user.roles.includes(role);
};

const getHomeRoute = () => {
  if (hasRole("ROLE_SUPER_ADMIN")) return "/superadmin/organizations";
  if (hasRole("ROLE_ADMIN")) return "/admin/users";
  return "/user/dashboard";
};

const logout_req = () => {
  localStorage.removeItem("user");
};

const forgotPasswordVerifyEmail = async (email) => {
  return await axios.get(API_BASE_URL + "/auth/forgotPassword/verifyEmail", {
    params: {
      email,
    },
  });
};

const forgotPasswordverifyCode = async (code) => {
  return await axios.get(API_BASE_URL + "/auth/forgotPassword/verifyCode", {
    params: {
      code,
    },
  });
};

const resendResetPasswordVerificationCode = async (email) => {
  return await axios.get(API_BASE_URL + "/auth/forgotPassword/resendEmail", {
    params: {
      email,
    },
  });
};

const resetPassword = async (email, password) => {
  return await axios.post(API_BASE_URL + "/auth/forgotPassword/resetPassword", {
    email,
    currentPassword: "",
    newPassword: password,
  });
};

const authHeader = () => {
  const user = getCurrentUser();
  if (user && user.token) {
    return { Authorization: `${user.type || "Bearer"} ${user.token}` };
  }
  return {};
};

const AuthService = {
  register_req,
  login_req,
  verifyRegistrationVerificationCode,
  resendRegistrationVerificationCode,
  getCurrentUser,
  hasRole,
  getHomeRoute,
  logout_req,
  forgotPasswordVerifyEmail,
  forgotPasswordverifyCode,
  resendResetPasswordVerificationCode,
  resetPassword,
  authHeader,
};

export default AuthService;
