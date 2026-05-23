import React, { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";

const parseJwt = (token) => {
  try {

    if (!token) return null;

    const base64Url = token.split(".")[1];

    if (!base64Url) return null;

    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");

    return JSON.parse(window.atob(base64));

  } catch (error) {

    console.error("Invalid JWT Token", error);

    return null;
  }
};

const AuthVerify = ({ logOut }) => {

  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {

    const rawUser = localStorage.getItem("user");

    if (!rawUser) return;

    let user = null;

    try {

      user = JSON.parse(rawUser);

    } catch (error) {

      console.error("Invalid localStorage user");

      localStorage.removeItem("user");

      navigate("/login");

      return;
    }

    // TOKEN CHECK
    if (!user?.token) {

      console.warn("Token missing");

      localStorage.removeItem("user");

      navigate("/login");

      return;
    }

    const decodedJwt = parseJwt(user.token);

    // INVALID TOKEN
    if (!decodedJwt || !decodedJwt.exp) {

      console.warn("Invalid JWT");

      localStorage.removeItem("user");

      navigate("/login");

      return;
    }

    // TOKEN EXPIRED
    const isExpired = decodedJwt.exp * 1000 < Date.now();

    if (isExpired) {

      console.warn("JWT Expired");

      logOut();

      navigate("/login");
    }

  }, [location, logOut, navigate]);

  return null;
};

export default AuthVerify;