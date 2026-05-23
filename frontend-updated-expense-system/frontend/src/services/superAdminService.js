import axios from "axios";
import AuthService from "./auth.service";
import API_BASE_URL from "./auth.config";

/*
|--------------------------------------------------------------------------
| Common Auth Header (FIXED)
|--------------------------------------------------------------------------
*/

const authHeader = () => {
  const token = AuthService.getCurrentUser()?.token;

  return {
    Authorization: token ? `Bearer ${token}` : "",
    "Content-Type": "application/json",
  };
};

/*
|--------------------------------------------------------------------------
| Organization APIs
|--------------------------------------------------------------------------
*/

const getOrganizations = async () => {
  return await axios.get(
    `${API_BASE_URL}/management/organizations`,
    {
      headers: authHeader(),
    }
  );
};

const getOrganizationById = async (id) => {
  return await axios.get(
    `${API_BASE_URL}/management/organizations/${id}`,
    {
      headers: authHeader(),
    }
  );
};

const createOrganization = async (organizationData) => {
  return await axios.post(
    `${API_BASE_URL}/management/organizations`,
    organizationData,
    {
      headers: authHeader(),
    }
  );
};

const updateOrganization = async (id, organizationData) => {
  return await axios.put(
    `${API_BASE_URL}/management/organizations/${id}`,
    organizationData,
    {
      headers: authHeader(),
    }
  );
};

const deleteOrganization = async (id) => {
  return await axios.delete(
    `${API_BASE_URL}/management/organizations/${id}`,
    {
      headers: authHeader(),
    }
  );
};

/*
|--------------------------------------------------------------------------
| Admin APIs
|--------------------------------------------------------------------------
*/

const getAdmins = async (pageNumber, pageSize, searchKey) => {
  return await axios.get(
    `${API_BASE_URL}/management/admins`,
    {
      headers: authHeader(),
      params: {
        pageNumber,
        pageSize,
        searchKey,
      },
    }
  );
};

const createAdmin = async (userName, email, password, organizationId) => {
  return await axios.post(
    `${API_BASE_URL}/management/admins`,
    {
      userName,
      email,
      password,
      organizationId,
    },
    {
      headers: authHeader(),
    }
  );
};

/*
|--------------------------------------------------------------------------
| Export
|--------------------------------------------------------------------------
*/

const SuperAdminService = {
  // Organization
  getOrganizations,
  getOrganizationById,
  createOrganization,
  updateOrganization,
  deleteOrganization,

  // Admin
  getAdmins,
  createAdmin,
};

export default SuperAdminService;