import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import toast, { Toaster } from "react-hot-toast";
import Container from "../../components/utils/Container";
import Header from "../../components/utils/header";
import Loading from "../../components/utils/loading";
import SuperAdminService from "../../services/superAdminService";

function CreateAdmin() {
  const { register, handleSubmit, watch, formState } = useForm();
  const [organizations, setOrganizations] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isFetchingOrganizations, setIsFetchingOrganizations] = useState(true);
  const navigate = useNavigate();
  const password = watch("password", "");

  useEffect(() => {
    const loadOrganizations = async () => {
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
      setIsFetchingOrganizations(false);
    };

    loadOrganizations();
  }, []);

  const onSubmit = async (data) => {
    setIsLoading(true);

    await SuperAdminService.createAdmin(data.userName, data.email, data.password, Number(data.organizationId)).then(
      (response) => {
        if (response.data.status === "SUCCESS") {
          toast.success("Admin created successfully");
          navigate("/superadmin/admins", { replace: true });
          return;
        }
        toast.error("Failed to create admin");
      },
      (error) => {
        const message =
          error?.response?.data?.message ||
          error?.response?.data?.response ||
          "Failed to create admin: Try again later!";
        toast.error(message);
      },
    );

    setIsLoading(false);
  };

  return (
    <Container activeNavId={18}>
      <Header title="Create Admin" />
      <Toaster />

      {isFetchingOrganizations && <Loading />}
      {!isFetchingOrganizations && (
        <form className="auth-form t-form" onSubmit={handleSubmit(onSubmit)} style={{ marginTop: "25px" }}>
          <div className="input-box">
            <label>Organization</label>
            <br />
            <select
              defaultValue=""
              {...register("organizationId", {
                required: "Organization is required!",
              })}
            >
              <option value="" disabled>
                Select organization
              </option>
              {organizations.map((organization, index) => (
                <option key={organization.id || index} value={organization.id}>
                  {organization.name || organization.organizationName}
                </option>
              ))}
            </select>
            {formState.errors.organizationId && <small>{formState.errors.organizationId.message}</small>}
          </div>

          <div className="input-box">
            <label>Username</label>
            <br />
            <input
              type="text"
              {...register("userName", {
                required: "Username is required!",
                minLength: { value: 3, message: "Username must have at least 3 characters!" },
              })}
            />
            {formState.errors.userName && <small>{formState.errors.userName.message}</small>}
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
            <label>Password</label>
            <br />
            <input
              type="password"
              {...register("password", {
                required: "Password is required!",
                minLength: { value: 8, message: "Password must have at least 8 characters!" },
              })}
            />
            {formState.errors.password && <small>{formState.errors.password.message}</small>}
          </div>

          <div className="input-box">
            <label>Confirm Password</label>
            <br />
            <input
              type="password"
              {...register("confirmPassword", {
                required: "Confirm password is required!",
                validate: (value) => value === password || "Passwords do not match!",
              })}
            />
            {formState.errors.confirmPassword && <small>{formState.errors.confirmPassword.message}</small>}
          </div>

          <div className="input-box">
            <input
              type="submit"
              value={isLoading ? "Creating..." : "Create admin"}
              className={isLoading ? "button button-fill loading" : "button button-fill"}
            />
          </div>
        </form>
      )}
    </Container>
  );
}

export default CreateAdmin;
