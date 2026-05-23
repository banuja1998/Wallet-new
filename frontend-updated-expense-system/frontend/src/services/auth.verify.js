import React, { useEffect } from "react";
import { useLocation } from "react-router-dom";

const parseJwt = (token) => {
  try {
    if (!token) return null;

    return JSON.parse(atob(token.split(".")[1]));
  } catch (e) {
    return null;
  }
};

const AuthVerify = (props) => {
  const location = useLocation();

  useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user"));

    if (!user?.token) return;

    const decodedJwt = parseJwt(user.token);

    if (!decodedJwt || !decodedJwt.exp) return;

    const isExpired = decodedJwt.exp * 1000 < Date.now();

    if (isExpired) {
      props.logOut();
    }
  }, [location, props]);

  return null;
};

export default AuthVerify;
