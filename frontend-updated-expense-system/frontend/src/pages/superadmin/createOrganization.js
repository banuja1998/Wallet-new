import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import toast, { Toaster } from "react-hot-toast";
import Container from "../../components/utils/Container";
import Header from "../../components/utils/header";
import SuperAdminService from "../../services/superAdminService";

function CreateOrganization() {
  const { register, handleSubmit, reset, formState } = useForm();
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

const onSubmit = async (data) => {
  setIsLoading(true);

  try {
    const response = await SuperAdminService.createOrganization({
      name: data.name,
      email: data.email,
      phone: data.phone,
      address: data.address,
    });

    if (response.data.status === "SUCCESS") {
      toast.success("Organization created successfully");
      reset();
      navigate("/superadmin/organizations", { replace: true });
    } else {
      toast.error("Failed to create organization");
    }
  } catch (error) {
    const message =
      error?.response?.data?.message ||
      "Failed to create organization: Try again later!";
    toast.error(message);
  } finally {
    setIsLoading(false);
  }
};

  return (
    <Container activeNavId={20}>
      <Header title="Create Organization" />
      <Toaster />

      <form className="auth-form t-form" onSubmit={handleSubmit(onSubmit)} style={{ marginTop: "25px" }}>
        <div className="input-box">
          <label>Organization Name</label>
          <br />
          <input
            type="text"
            {...register("name", {
              required: "Organization name is required!",
              maxLength: { value: 100, message: "Organization name is too long!" },
            })}
          />
          {formState.errors.name && <small>{formState.errors.name.message}</small>}
        </div>

        <div className="input-box">
          <label>Email</label>
          <br />
          <input
            type="email"
            {...register("email", {
              required: "Email is required!",
              pattern: {
                value: /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/g,
                message: "Invalid email address!",
              },
            })}
          />
          {formState.errors.email && <small>{formState.errors.email.message}</small>}
        </div>

        <div className="input-box">
          <label>Phone</label>
          <br />
          <input
            type="text"
            {...register("phone", {
              required: "Phone number is required!",
              minLength: { value: 7, message: "Phone number looks too short!" },
            })}
          />
          {formState.errors.phone && <small>{formState.errors.phone.message}</small>}
        </div>

        <div className="input-box">
          <label>Address</label>
          <br />
          <textarea
            rows="4"
            {...register("address", {
              required: "Address is required!",
            })}
          />
          {formState.errors.address && <small>{formState.errors.address.message}</small>}
        </div>

        <div className="input-box">
          <input
            type="submit"
            value={isLoading ? "Creating..." : "Create organization"}
            className={isLoading ? "button button-fill loading" : "button button-fill"}
          />
        </div>
      </form>
    </Container>
  );
}

export default CreateOrganization;
