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

  /*
  |--------------------------------------------------------------------------
  | Load Organizations
  |--------------------------------------------------------------------------
  */

  const loadOrganizations = async () => {

    try {

      setIsFetching(true);

      const response = await SuperAdminService.getOrganizations();

      const payload = response?.data?.response;

      const records = Array.isArray(payload)
        ? payload
        : payload?.data || [];

      setOrganizations(records);

    } catch (error) {

      console.error(error);

      toast.error("Failed to fetch organizations!");

    } finally {

      setIsFetching(false);

    }
  };

  /*
  |--------------------------------------------------------------------------
  | Delete Organization
  |--------------------------------------------------------------------------
  */

  const handleDelete = async (id) => {

    const confirmDelete = window.confirm(
      "Are you sure you want to delete this organization?"
    );

    if (!confirmDelete) return;

    try {

      await SuperAdminService.deleteOrganization(id);

      toast.success("Organization deleted successfully");

      loadOrganizations();

    } catch (error) {

      console.error(error);

      toast.error("Failed to delete organization");

    }
  };

  /*
  |--------------------------------------------------------------------------
  | Use Effect
  |--------------------------------------------------------------------------
  */

  useEffect(() => {

    loadOrganizations();

  }, []);

  return (

    <Container activeNavId={19}>

      <Header title="Organizations" />

      <Toaster />

      <div
        className="utils page"
        style={{
          marginBottom: "20px",
        }}
      >

        <div>
          <p style={{ margin: 0 }}>
            Manage all organizations created by super admin.
          </p>
        </div>

        <Link to="/superadmin/organizations/new">
          <button>Create Organization</button>
        </Link>

      </div>

      {/* Loading */}
      {isFetching && <Loading />}

      {/* Empty State */}
      {!isFetching && organizations.length === 0 && (
        <Info text="No organizations found!" />
      )}

      {/* Table */}
      {!isFetching && organizations.length > 0 && (

        <table>

          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Address</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>

          <tbody>

            {organizations.map((item, index) => (

              <tr key={item.id || index}>

                <td>{item.id || index + 1}</td>

                <td>{item.name || "-"}</td>

                <td>{item.email || "-"}</td>

                <td>{item.phone || "-"}</td>

                <td>{item.address || "-"}</td>

                <td
                  style={{
                    color:
                      item.active === false
                        ? "#ff0000"
                        : "#6aa412",
                    fontWeight: "600",
                  }}
                >
                  {item.active === false
                    ? "Inactive"
                    : "Active"}
                </td>

                <td
                  style={{
                    display: "flex",
                    gap: "10px",
                  }}
                >

                  <Link
                    to={`/superadmin/organizations/edit/${item.id}`}
                  >
                    <button>Edit</button>
                  </Link>

                  <button
                    onClick={() => handleDelete(item.id)}
                    style={{
                      backgroundColor: "#dc3545",
                    }}
                  >
                    Delete
                  </button>

                </td>

              </tr>

            ))}

          </tbody>

        </table>

      )}

    </Container>
  );
}

export default Organizations;