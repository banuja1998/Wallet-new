import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import toast, { Toaster } from "react-hot-toast";
import Container from "../../components/utils/Container";
import Header from "../../components/utils/header";
import Loading from "../../components/utils/loading";
import Info from "../../components/utils/Info";
import SuperAdminService from "../../services/superAdminService";

function Organizations() {
  const [organizations, setOrganizations] = useState([]);
  const [isFetching, setIsFetching] = useState(true);

  const loadOrganizations = async () => {
    setIsFetching(true);
    await SuperAdminService.getOrganizations().then(
      (response) => {
        const payload = response?.data?.response;
        const records = Array.isArray(payload) ? payload : payload?.data || [];
        setOrganizations(records);
      },
      () => {
        toast.error("Failed to fetch organizations: Try again later!");
      },
    );
    setIsFetching(false);
  };

  useEffect(() => {
    loadOrganizations();
  }, []);

  return (
    <Container activeNavId={19}>
      <Header title="Organizations" />
      <Toaster />

      <div className="utils page" style={{ marginBottom: "20px" }}>
        <div>
          <p style={{ margin: 0 }}>Manage all organizations created by super admin.</p>
        </div>
        <Link to="/superadmin/organizations/new">
          <button>Create Organization</button>
        </Link>
      </div>

      {isFetching && <Loading />}
      {!isFetching && organizations.length === 0 && <Info text="No organizations found!" />}
      {!isFetching && organizations.length !== 0 && (
        <table>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Address</th>
            <th>Status</th>
          </tr>
          {organizations.map((item, index) => (
            <tr key={item.id || index}>
              <td>{item.id || index + 1}</td>
              <td>{item.name || item.organizationName || "-"}</td>
              <td>{item.email || "-"}</td>
              <td>{item.phone || "-"}</td>
              <td>{item.address || "-"}</td>
              <td style={{ color: item.active === false ? "#ff0000" : "#6aa412" }}>
                {item.active === false ? "Inactive" : "Active"}
              </td>
            </tr>
          ))}
        </table>
      )}
    </Container>
  );
}

export default Organizations;
