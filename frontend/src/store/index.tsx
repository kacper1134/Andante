import { configureStore } from "@reduxjs/toolkit";
import { useDispatch } from "react-redux";
import orderApiSlice from "./api/order-api-slice";
import chatApiSlice from "./api/chat-api-slice";
import authReducer from "./auth/auth-slice";
import configReducer from "./config/config-slice";
import imageSliderReducer from "./image-slider/image-slider";
import innerReducer from "./inner/innerSlice";
import cartReducer from "./cart/cartSlice";
import publicChatApiSlice from "./api/public-chat-api-slice";
import productSlice from "./api/productSlice";
import profileApiSlice from "./api/profile-api-slice";
import { persistReducer, persistStore } from "redux-persist";
import storageSession from "redux-persist/lib/storage/session";
import activitySlice from "./api/activitySlice";
import forumSlice from "./api/forum-api-slice";

const store = configureStore({
  reducer: {
    auth: persistReducer(
      { key: "auth", storage: storageSession },
      authReducer
    )!,
    config: configReducer,
    imageSlider: imageSliderReducer,
    inner: innerReducer,
    cart: persistReducer(
      { key: "cart", storage: storageSession },
      cartReducer
    )!,
    [orderApiSlice.reducerPath]: orderApiSlice.reducer,
    [chatApiSlice.reducerPath]: chatApiSlice.reducer,
    [publicChatApiSlice.reducerPath]: publicChatApiSlice.reducer,
    [productSlice.reducerPath]: productSlice.reducer,
    [profileApiSlice.reducerPath]: profileApiSlice.reducer,
    [activitySlice.reducerPath]: activitySlice.reducer,
    [forumSlice.reducerPath]: forumSlice.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({ serializableCheck: false, immutableCheck: false })
      .concat(orderApiSlice.middleware)
      .concat(profileApiSlice.middleware)
      .concat(productSlice.middleware)
      .concat(chatApiSlice.middleware)
      .concat(publicChatApiSlice.middleware)
      .concat(forumSlice.middleware),
});

export type RootState = ReturnType<typeof store.getState>;

export type AppDispatch = typeof store.dispatch;
export const useAppDispatch: () => AppDispatch = useDispatch;

export default store;
export const persistor = persistStore(store);
