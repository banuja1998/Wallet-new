import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import toast, { Toaster } from "react-hot-toast";

import Container from "../../components/utils/Container";
import Header from "../../components/utils/header";
import Loading from "../../components/utils/loading";

import SuperAdminService from "../../services/superAdminService";

function EditOrganization() {

  const { id } = useParams();

  const navigate = useNavigate();

  const [isLoading, setIsLoading] = useState(true);

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    address: "",
  });

  /*
  |--------------------------------------------------------------------------
  | Load Organization
  |--------------------------------------------------------------------------
  */

  const loadOrganization = async () => {

    try {

      const response =
        await SuperAdminService.getOrganizationById(id);

      const data = response?.data?.response;

      setFormData({
        name: data?.name || "",
        email: data?.email || "",
        phone: data?.phone || "",
        address: data?.address || "",
      });

    } catch (error) {

      console.error(error);

      toast.error("Failed to load organization");

    } finally {

      setIsLoading(false);

    }
  };

  /*
  |--------------------------------------------------------------------------
  | Handle Change
  |--------------------------------------------------------------------------
  */

  const handleChange = (e) => {

    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  /*
  |--------------------------------------------------------------------------
  | Handle Submit
  |--------------------------------------------------------------------------
  */

  const handleSubmit = async (e) => {

    e.preventDefault();

    try {

      await SuperAdminService.updateOrganization(
        id,
        formData
      );

      toast.success("Organization updated successfully");

      navigate("/superadmin/organizations");

    } catch (error) {

      console.error(error);

      toast.error("Failed to update organization");

    }
  };

  useEffect(() => {

    loadOrganization();

  }, []);

  if (isLoading) {
    return <Loading />;
  }

 return (

  <Container activeNavId={19}>

    <Header title="Edit Organization" />

    <Toaster />

    <div
      style={{
        width: "100%",
        display: "flex",
        justifyContent: "center",
        marginTop: "40px",
      }}
    >

      <form
        onSubmit={handleSubmit}
        style={{
          width: "450px",
          background: "#ffffff",
          padding: "30px",
          borderRadius: "12px",
          boxShadow: "0 2px 10px rgba(0,0,0,0.08)",
        }}
      >

        {/* Name */}
        <div
          style={{
            marginBottom: "20px",
            display: "flex",
            flexDirection: "column",
          }}
        >

          <label
            style={{
              marginBottom: "8px",
              fontWeight: "600",
            }}
          >
            Name
          </label>

          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            style={{
              padding: "12px",
              borderRadius: "8px",
              border: "1px solid #ccc",
              fontSize: "15px",
            }}
          />

        </div>

        {/* Email */}
        <div
          style={{
            marginBottom: "20px",
            display: "flex",
            flexDirection: "column",
          }}
        >

          <label
            style={{
              marginBottom: "8px",
              fontWeight: "600",
            }}
          >
            Email
          </label>

          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            style={{
              padding: "12px",
              borderRadius: "8px",
              border: "1px solid #ccc",
              fontSize: "15px",
            }}
          />

        </div>

        {/* Phone */}
        <div
          style={{
            marginBottom: "20px",
            display: "flex",
            flexDirection: "column",
          }}
        >

          <label
            style={{
              marginBottom: "8px",
              fontWeight: "600",
            }}
          >
            Phone
          </label>

          <input
            type="text"
            name="phone"
            value={formData.phone}
            onChange={handleChange}
            style={{
              padding: "12px",
              borderRadius: "8px",
              border: "1px solid #ccc",
              fontSize: "15px",
            }}
          />

        </div>

        {/* Address */}
        <div
          style={{
            marginBottom: "25px",
            display: "flex",
            flexDirection: "column",
          }}
        >

          <label
            style={{
              marginBottom: "8px",
              fontWeight: "600",
            }}
          >
            Address
          </label>

          <textarea
            name="address"
            value={formData.address}
            onChange={handleChange}
            rows="4"
            style={{
              padding: "12px",
              borderRadius: "8px",
              border: "1px solid #ccc",
              fontSize: "15px",
              resize: "none",
            }}
          />

        </div>

        {/* Button */}
        <button
          type="submit"
          style={{
            width: "100%",
            padding: "14px",
            border: "none",
            borderRadius: "10px",
            backgroundColor: "#4c84ff",
            color: "#fff",
            fontSize: "16px",
            fontWeight: "600",
            cursor: "pointer",
          }}
        >
          Update Organization
        </button>

      </form>

    </div>

  </Container>
);
}

export default EditOrganization;