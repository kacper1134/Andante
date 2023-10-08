import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { DateTime } from "luxon";
import { OrderData, OrderDetails } from "../../components/Cart/CartConfirmation";
import { CartEntryData } from "../../components/Cart/CartEntry";
import { DeliveryMethod, DeliveryPickupLocation, PaymentMethodData } from "../../components/Cart/CartForms";

export interface CartSliceState {
    cartItems: CartEntryData[],
    orderData: OrderData,
    invoice?: Blob,
    deliveryPickupLocation: DeliveryPickupLocation | null,
    currency: string,
}

export interface ChangeQuantity {
    id: number,
    newQuantity: number,
};

const cartInitialState: CartSliceState = {
    cartItems: [],
    currency: "$",
    orderData: {
        orderId: 0,
        orderDate: DateTime.now(),
        orderDetails: {
            personal: {
                name: "",
                surname: "",
                emailAddress: "",
                phoneNumber: "",
            },
            location: {
                country: "",
                city: "",
                street: "",
                buildingNumber: "",
                postalCode: ""
            }
        },
        deliveryMethod: {
            provider: "",
            expectedDeliveryDays: 0,
            price: 0,
            type: "Courier",
        },
        paymentMethod: {
            provider: "",
            description: "",
            cost: 0,
        }
    },
    deliveryPickupLocation: null,
}

const cartSlice = createSlice({
    name: "cart",
    initialState: cartInitialState,
    reducers: {
        addToCart: (state, action: PayloadAction<CartEntryData>) => {
            if (state.cartItems.some(item => item.variant.id === action.payload.variant.id)) {
                state.cartItems.filter(item => item.variant.id === action.payload.variant.id).forEach(item => item.quantity += action.payload.quantity)
            } else {
                state.cartItems.push(action.payload);
            }
        },
        removeFromCart: (state, action: PayloadAction<number>) => {
            const productToRemove = state.cartItems.find(item => item.variant.id === action.payload);
            const index = productToRemove ? state.cartItems.indexOf(productToRemove) : -1;

            if (index > -1) {
                state.cartItems.splice(index, 1);
            }
        },
        changeProductQuantity: (state, action: PayloadAction<ChangeQuantity>) => {
            if (state.cartItems.some(item => item.variant.id === action.payload.id)) {
                state.cartItems.filter(item => item.variant.id === action.payload.id).forEach(item => item.quantity = action.payload.newQuantity)
            }
        },
        clearCart: (state) => {
            state.cartItems = []
        },
        changeCurrency: (state, action: PayloadAction<string>) => {
            state.currency = action.payload;
        },
        placeOrder: (state, action: PayloadAction<{orderDetails: OrderDetails, orderId: number}>) => {
            state.orderData.orderId = action.payload.orderId;
            state.orderData.orderDate = DateTime.now();
            state.orderData.deliveryDate = state.orderData.orderDate?.plus({days: state.orderData.deliveryMethod.expectedDeliveryDays});
            state.orderData.orderDetails = action.payload.orderDetails;
        },
        updateDeliveryMethod: (state, action: PayloadAction<DeliveryMethod>) => {
            state.orderData.deliveryMethod = action.payload;
        },
        updatePaymentMethod: (state, action: PayloadAction<PaymentMethodData>) => {
            state.orderData.paymentMethod = action.payload;
        },
        updateDeliveryPickupLocation: (state, action: PayloadAction<DeliveryPickupLocation>) => {
            state.deliveryPickupLocation = action.payload;
        }
    },
})

export const cartActions = cartSlice.actions;
export default cartSlice.reducer;