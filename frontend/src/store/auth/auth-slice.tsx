import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { KeycloakTokenParsed } from "keycloak-js";
import { getUserDetails, UserDetails } from "../../utils/KeycloakUtils";

const authInititalState: AuthSliceState = { isAuthenticated: false, shouldLogout: false, alternativeVersionOfInterface: false};

export interface AuthSliceState {
  isAuthenticated: boolean,
  idToken?: string,
  tokenParsed?: KeycloakTokenParsed,
  userDetails?: UserDetails,
  shouldLogout: boolean,
  alternativeVersionOfInterface: boolean,
}

const authSlice = createSlice({
  name: "auth",
  initialState: authInititalState,
  reducers: {
    login: (state, action: PayloadAction<{tokenParsed: KeycloakTokenParsed | undefined, idToken: string | undefined}>) => {
      state.isAuthenticated = true;
      state.tokenParsed = action.payload.tokenParsed;
      state.idToken = action.payload.idToken;
      state.userDetails = action.payload.tokenParsed ? getUserDetails(action.payload.tokenParsed) : undefined;
    },
    logout: (state) => {
      state.isAuthenticated = false;
      state.tokenParsed = undefined;
      state.idToken = undefined;
      state.userDetails = undefined;
      state.shouldLogout = false;
    },
    queueLogout: (state) => {
      state.shouldLogout = true;
    },
    changeInterfaceVersion: (state) => {
      state.alternativeVersionOfInterface = !state.alternativeVersionOfInterface;
    }
  },
});

export const authActions = authSlice.actions;
export default authSlice.reducer;
