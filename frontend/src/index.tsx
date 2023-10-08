import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";
import theme from "./theme";
import { BrowserRouter } from "react-router-dom";

// Chakra
import { ChakraProvider } from "@chakra-ui/react";
import { Provider } from "react-redux";

// Redux
import store, { persistor } from "./store/index";

// Keycloak
import { ReactKeycloakProvider } from "@react-keycloak/web";
import keycloak from "./config/keycloak-config";

// Fonts
import "@fontsource/montserrat/700.css";
import "@fontsource/hind/400.css";
import "@fontsource/space-mono/400.css";
import { PersistGate } from "redux-persist/integration/react";

const root = ReactDOM.createRoot(
    document.getElementById("root") as HTMLElement
);

root.render(
    <ReactKeycloakProvider authClient={keycloak}>
        <Provider store={store}>
            <ChakraProvider theme={theme}>
                <BrowserRouter>
                    <PersistGate loading={null} persistor={persistor}>
                        <App />
                    </PersistGate>
                </BrowserRouter>
            </ChakraProvider>
        </Provider>
    </ReactKeycloakProvider>
);
