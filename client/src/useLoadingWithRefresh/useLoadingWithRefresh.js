import { refresh } from "../http";
import { useEffect, useState, useRef } from "react";
import { useGlobalContext } from "../context/context";

export function useLoadingWithRefresh() {
  const [loading, setLoading] = useState(true);
  const { Auth, Activate, setUserData, setAdmin } = useGlobalContext();
  const tempRefresh = useRef();

  async function refreshAndStore() {
    const storedRefreshToken = localStorage.getItem("refreshToken");

    // No refresh token means user is unauthenticated, so skip calling the API
    if (!storedRefreshToken) {
      Auth(false);
      Activate(false);
      setAdmin(false);
      setUserData(null);
      setLoading(false);
      return;
    }

    try {
      const { data } = await refresh();
      if (!data.success) {
        Auth(false);
        Activate(false);
        setUserData(null);
        setAdmin(false);
        setLoading(false);
        return;
      }

      setUserData(data.user);
      Auth(data.auth);
      Activate(data.activate);
      setAdmin(data.isAdmin);
      setLoading(false);
    } catch (err) {
      console.log(err);
      Auth(false);
      Activate(false);
      setUserData(null);
      setLoading(false);
      setAdmin(false);
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
    }
  }

  tempRefresh.current = refreshAndStore;

  useEffect(() => {
    tempRefresh.current();
  }, []);

  return { loading };
}
export const updating = useLoadingWithRefresh;