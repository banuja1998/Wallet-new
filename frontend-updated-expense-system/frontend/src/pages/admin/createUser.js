import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import toast, { Toaster } from "react-hot-toast";
import Container from "../../components/utils/Container";
import Header from "../../components/utils/header";
import AdminService from "../../services/adminService";
import AuthService from "../../services/auth.service";

function CreateUser() {
  const { register, handleSubmit, watch, reset, formState } = useForm();
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const password = watch("password", "");
  const currentUser = AuthService.getCurrentUser();

  const onSubmit = async (data) => {
    setIsLoading(true);

    await AdminService.createUser(data.userName, data.email, data.password).then(
      (response) => {
        if (response.data.status === "SUCCESS") {
          toast.success("User created successfully");
          reset();
          navigate("/admin/users", { replace: true });
          return;
        }
        toast.error("Failed to create user");
      },
      (error) => {
        const message =
          error?.response?.data?.message ||
          error?.response?.data?.response ||
          "Failed to create user: Try again later!";
        toast.error(message);
      },
    );

    setIsLoading(false);
  };

  return (
    <Container activeNavId={9}>
      <Header title="Create User" />
      <Toaster />

      <form className="auth-form t-form" onSubmit={handleSubmit(onSubmit)} style={{ marginTop: "25px" }}>
        <div className="input-box">
          <label>Organization</label>
          <br />
          <input type="text" value={currentUser?.organizationName || "Not assigned"} disabled />
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
            value={isLoading ? "Creating..." : "Create user"}
            className={isLoading ? "button button-fill loading" : "button button-fill"}
          />
        </div>
      </form>
    </Container>
  );
}

export default CreateUser;
