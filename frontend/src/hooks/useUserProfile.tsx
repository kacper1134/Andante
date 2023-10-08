import { useKeycloak } from "@react-keycloak/web";
import { useEffect } from "react";
import { useLazyGetProfileQuery } from "../store/api/profile-api-slice";

const useUserProfile = () => {
  const [trigger, result] = useLazyGetProfileQuery();
  const { keycloak, initialized } = useKeycloak();

  useEffect(() => {
    if (initialized && keycloak.authenticated) {
      trigger();
    }
  }, [initialized, keycloak.authenticated, trigger]);

  return result.data;
};

export default useUserProfile;
