import { createSlice } from "@reduxjs/toolkit";

export interface ImageSliderState {
  isAnimation: boolean,
}

const imageSliderInitialState: ImageSliderState = {
  isAnimation: false,
};

const imageSliderSlice = createSlice({
  name: "imageSlider",
  initialState: imageSliderInitialState,
  reducers: {
    startAnimation: (state) => {
      state.isAnimation = true;
    },
    stopAnimation: (state) => {
      state.isAnimation = false;
    },
  },
});

export const imageSliderActions = imageSliderSlice.actions;
export default imageSliderSlice.reducer;
