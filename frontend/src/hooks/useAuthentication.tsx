import { useKeycloak } from "@react-keycloak/web";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { RootState } from "../store";
import { useCreateUserMutation } from "../store/api/chat-api-slice";
import { useLazyGetProfileQuery } from "../store/api/profile-api-slice";
import { authActions } from "../store/auth/auth-slice";
import { getUserDetails } from "../utils/KeycloakUtils";

const useAuthentication = (redirectPath: string) => {
  const { keycloak, initialized } = useKeycloak();

  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [createUser] = useCreateUserMutation();
  const [getProfileTrigger] = useLazyGetProfileQuery();
  const isAuthenticated = useSelector(
    (state: RootState) => state.auth.isAuthenticated
  );

  useEffect(() => {
    keycloak.onAuthSuccess = () => {
      dispatch(authActions.login({tokenParsed: keycloak.idTokenParsed, idToken: keycloak.idToken}));
      const details = getUserDetails(keycloak.idTokenParsed!).personal;
      const isRegistered = Math.abs(details.accountCreated.diffNow(['minutes']).minutes) < 5;
      if(isRegistered) {
        createUser({username: details.username, first_name: details.name, last_name: details.surname, secret: details.emailAddress, email: details.emailAddress});
        getProfileTrigger();
      }
    };
    keycloak.onAuthRefreshSuccess = () =>
      dispatch(authActions.login({tokenParsed: keycloak.idTokenParsed, idToken: keycloak.idToken}));
  }, [keycloak, dispatch, createUser]);

  if (!isAuthenticated && initialized) {
    keycloak.login().then(() => navigate(redirectPath));
  }
};

export default useAuthentication;
