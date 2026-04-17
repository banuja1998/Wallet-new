import axios from "axios";
import AuthService from "./auth.service";
import API_BASE_URL from "./auth.config";

const getOrganizations = async () => {
  return axios.get(API_BASE_URL + "/management/organizations", {
    headers: AuthService.authHeader(),
  });
};

const createOrganization = async (name, email, phone, address) => {
  return axios.post(
    API_BASE_URL + "/management/organizations",
    { name, email, phone, address },
    { headers: AuthService.authHeader() },
  );
};

const getAdmins = async (pageNumber, pageSize, searchKey) => {
  return axios.get(API_BASE_URL + "/management/admins", {
    headers: AuthService.authHeader(),
    params: {
      pageNumber,
      pageSize,
      searchKey,
    },
  });
};

const createAdmin = async (userName, email, password, organizationId) => {
  return axios.post(
    API_BASE_URL + "/management/admins",
    { userName, email, password, organizationId },
    { headers: AuthService.authHeader() },
  );
};

const SuperAdminService = {
  getOrganizations,
  createOrganization,
  getAdmins,
  createAdmin,
};

export default SuperAdminService;
