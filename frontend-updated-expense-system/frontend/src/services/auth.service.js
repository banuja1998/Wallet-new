import axios from "axios";
import API_BASE_URL from "./auth.config";

/*
|--------------------------------------------------------------------------
| REGISTER
|--------------------------------------------------------------------------
*/

const register_req = async (username, email, password) => {
  return await axios.post(`${API_BASE_URL}/auth/signup`, {
    userName: username,
    email,
    password,
  });
};

/*
|--------------------------------------------------------------------------
| NORMALIZE USER DATA
|--------------------------------------------------------------------------
*/

const normalizeUser = (payload) => {
  if (!payload) return null;

  return {
    ...payload,

    // FIXED TOKEN SUPPORT
    token:
      payload.token ||
      payload.jwt ||
      payload.accessToken ||
      payload.access_token ||
      "",

    // FIXED TOKEN TYPE
    type: payload.type || payload.tokenType || "Bearer",

    // FIXED ROLES
    roles: Array.isArray(payload.roles) ? payload.roles : [],

    organizationId: payload.organizationId ?? null,
    organizationName: payload.organizationName ?? "",
  };
};

/*
|--------------------------------------------------------------------------
| LOGIN
|--------------------------------------------------------------------------
*/

const login_req = async (email, password) => {
  const response = await axios.post(`${API_BASE_URL}/auth/signin`, {
    email,
    password,
  });

  console.log("LOGIN RESPONSE :", response.data);

  const normalizedUser = normalizeUser(response.data);

  // FIXED LOCAL STORAGE SAVE
  if (normalizedUser?.token) {
    localStorage.setItem("user", JSON.stringify(normalizedUser));
  }

  return normalizedUser;
};

/*
|--------------------------------------------------------------------------
| VERIFY REGISTRATION CODE
|--------------------------------------------------------------------------
*/

const verifyRegistrationVerificationCode = async (verificationCode) => {
  return await axios.get(`${API_BASE_URL}/auth/signup/verify`, {
    params: {
      code: verificationCode,
    },
  });
};

/*
|--------------------------------------------------------------------------
| RESEND REGISTRATION CODE
|--------------------------------------------------------------------------
*/

const resendRegistrationVerificationCode = async (email) => {
  return await axios.get(`${API_BASE_URL}/auth/signup/resend`, {
    params: {
      email,
    },
  });
};

/*
|--------------------------------------------------------------------------
| GET CURRENT USER
|--------------------------------------------------------------------------
*/

const getCurrentUser = () => {
  const rawUser = localStorage.getItem("user");

  if (!rawUser) return null;

  try {
    return normalizeUser(JSON.parse(rawUser));
  } catch (error) {
    console.error("Invalid user in localStorage");

    localStorage.removeItem("user");

    return null;
  }
};

/*
|--------------------------------------------------------------------------
| CHECK ROLE
|--------------------------------------------------------------------------
*/

const hasRole = (role) => {
  const user = getCurrentUser();

  return !!user && user.roles.includes(role);
};

/*
|--------------------------------------------------------------------------
| HOME ROUTE
|--------------------------------------------------------------------------
*/

const getHomeRoute = () => {
  if (hasRole("ROLE_SUPER_ADMIN")) {
    return "/superadmin/organizations";
  }

  if (hasRole("ROLE_ADMIN")) {
    return "/admin/users";
  }

  return "/user/dashboard";
};

/*
|--------------------------------------------------------------------------
| LOGOUT
|--------------------------------------------------------------------------
*/

const logout_req = () => {
  localStorage.removeItem("user");
};

/*
|--------------------------------------------------------------------------
| FORGOT PASSWORD
|--------------------------------------------------------------------------
*/

const forgotPasswordVerifyEmail = async (email) => {
  return await axios.get(
    `${API_BASE_URL}/auth/forgotPassword/verifyEmail`,
    {
      params: {
        email,
      },
    }
  );
};

const forgotPasswordverifyCode = async (code) => {
  return await axios.get(
    `${API_BASE_URL}/auth/forgotPassword/verifyCode`,
    {
      params: {
        code,
      },
    }
  );
};

const resendResetPasswordVerificationCode = async (email) => {
  return await axios.get(
    `${API_BASE_URL}/auth/forgotPassword/resendEmail`,
    {
      params: {
        email,
      },
    }
  );
};

const resetPassword = async (email, password) => {
  return await axios.post(
    `${API_BASE_URL}/auth/forgotPassword/resetPassword`,
    {
      email,
      currentPassword: "",
      newPassword: password,
    }
  );
};

/*
|--------------------------------------------------------------------------
| AUTH HEADER
|--------------------------------------------------------------------------
*/

const authHeader = () => {
  const user = getCurrentUser();

  if (user && user.token) {
    return {
      Authorization: `${user.type} ${user.token}`,
    };
  }

  return {};
};

/*
|--------------------------------------------------------------------------
| AXIOS INSTANCE
|--------------------------------------------------------------------------
*/

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
});

// AUTO ADD TOKEN TO EVERY REQUEST
axiosInstance.interceptors.request.use(
  (config) => {
    const headers = authHeader();

    config.headers = {
      ...config.headers,
      ...headers,
    };

    return config;
  },
  (error) => Promise.reject(error)
);

/*
|--------------------------------------------------------------------------
| EXPORTS
|--------------------------------------------------------------------------
*/

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
  axiosInstance,
};

export default AuthService;