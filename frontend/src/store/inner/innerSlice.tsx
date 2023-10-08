import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { ProductType, Subcategory } from "../../components/Shop/MainPage/FilterMenu/ProductCategory";
import { State } from "../../components/Shop/MainPage/FilterMenu/ProductStateFilter";

export interface ProductQuery {
    type: ProductType,
    query: string,
};

export interface SelectedCategory {
    category: ProductType,
    subcategoryName: string,
    selectedSubcategories: Subcategory[],
    selectedAll: boolean,
}

export interface FilterState {
    selectedCategory?: SelectedCategory,
    minimumRating: number,
    priceRange: {
        min?: number,
        max?: number,
    },
    selectedStates: State[],
    query: string,
}

export interface InnerSliceState {
    sidebarWidth: number,
    filterState: FilterState,
    errorMessages: string[],
}

const initialState: InnerSliceState = {
    sidebarWidth: 0,
    filterState: {
        minimumRating: 0,
        priceRange: {},
        selectedStates: [],
        query: "",
    },
    errorMessages: [],
};

const innerSlice = createSlice({
    name: "inner",
    initialState: initialState,
    reducers: {
        setSidebarWidth: (state, action: PayloadAction<number>) => {
            state.sidebarWidth = action.payload;
        },
        addErrorMessages: (state, action: PayloadAction<string[]>) => {
            state.errorMessages.push(...action.payload);
        },
        clearErrorMessages: (state) => {
            state.errorMessages = [];
        },
        setCategoryState: (state, action: PayloadAction<SelectedCategory>) => {
            if (action.payload.selectedSubcategories.length === 0) {
                state.filterState.selectedCategory = undefined;
            } else {
                state.filterState.selectedCategory = action.payload;
            }
        },
        setRatingState: (state, action: PayloadAction<number>) => {
            state.filterState.minimumRating = action.payload;
        },
        setPriceRange: (state, action: PayloadAction<{min: number, max: number}>) => {
            state.filterState.priceRange = action.payload;
        },
        setSelectedStates: (state, action: PayloadAction<State[]>) => {
            state.filterState.selectedStates = action.payload;
        },
        setQuery: (state, action: PayloadAction<string>) => {
            state.filterState.query = action.payload;
        },
    }
});

export const innerActions = innerSlice.actions;
export default innerSlice.reducer;