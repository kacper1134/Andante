import { createSlice } from "@reduxjs/toolkit";

const configInititalState = { serverUrl: "http://localhost:4561" };

const configSlice = createSlice({
  name: "config",
  initialState: configInititalState,
  reducers: {},
});

export default configSlice.reducer;