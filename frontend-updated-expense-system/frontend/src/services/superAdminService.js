import axios from "axios";
import AuthService from "./auth.service";
import API_BASE_URL from "./auth.config";

/*
|--------------------------------------------------------------------------
| Organization APIs
|--------------------------------------------------------------------------
*/

const getOrganizations = async () => {
  return await axios.get(
    API_BASE_URL + "/management/organizations",
    {
      headers: AuthService.authHeader(),
    }
  );
};

const getOrganizationById = async (id) => {
  return await axios.get(
    API_BASE_URL + `/management/organizations/${id}`,
    {
      headers: AuthService.authHeader(),
    }
  );
};

const createOrganization = async (organizationData) => {
  return await axios.post(
    API_BASE_URL + "/management/organizations",
    organizationData,
    {
      headers: AuthService.authHeader(),
    }
  );
};

const updateOrganization = async (id, organizationData) => {
  return await axios.put(
    API_BASE_URL + `/management/organizations/${id}`,
    organizationData,
    {
      headers: AuthService.authHeader(),
    }
  );
};

const deleteOrganization = async (id) => {
  return await axios.delete(
    API_BASE_URL + `/management/organizations/${id}`,
    {
      headers: AuthService.authHeader(),
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
    API_BASE_URL + "/management/admins",
    {
      headers: AuthService.authHeader(),
      params: {
        pageNumber,
        pageSize,
        searchKey,
      },
    }
  );
};

const createAdmin = async (
  userName,
  email,
  password,
  organizationId
) => {
  return await axios.post(
    API_BASE_URL + "/management/admins",
    {
      userName,
      email,
      password,
      organizationId,
    },
    {
      headers: AuthService.authHeader(),
    }
  );
};

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