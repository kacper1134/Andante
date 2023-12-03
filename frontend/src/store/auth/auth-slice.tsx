import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { KeycloakTokenParsed } from "keycloak-js";
import { getUserDetails, UserDetails } from "../../utils/KeycloakUtils";
import i18n from 'i18next';

const authInititalState: AuthSliceState = { isAuthenticated: false, shouldLogout: false, alternativeVersionOfInterface: false, language: "pl"};


export interface AuthSliceState {
  isAuthenticated: boolean,
  idToken?: string,
  tokenParsed?: KeycloakTokenParsed,
  userDetails?: UserDetails,
  shouldLogout: boolean,
  alternativeVersionOfInterface: boolean,
  language: string;
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
      i18n.changeLanguage(state.language === "pl" ? "en" : "pl");
    },
    logout: (state) => {
      state.isAuthenticated = false;
      state.tokenParsed = undefined;
      state.idToken = undefined;
      state.userDetails = undefined;
      state.shouldLogout = false;
      i18n.changeLanguage(state.language === "pl" ? "en" : "pl");
    },
    queueLogout: (state) => {
      state.shouldLogout = true;
    },
    changeInterfaceVersion: (state) => {
      state.alternativeVersionOfInterface = !state.alternativeVersionOfInterface;
    },
    changeLanguage: (state) => {
      state.language = state.language === "pl" ? "en" : "pl"
    }
  },
});

export const authActions = authSlice.actions;
export default authSlice.reducer;
