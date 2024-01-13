import { SetStateAction, useEffect, useState } from "react";
import ConfirmOrderModal from "../../../Cart/ConfirmOrderModal";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../../../store";
import { cartActions } from "../../../../store/cart/cartSlice";
import { useToast } from "@chakra-ui/react";
import {
  areDeliveryDetailsMissing,
  arePersonalDetailsMissing,
} from "../../../Profile/Preferences/OrderPreferencesForms";

type BuyNowModalProps = {
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  buyNow: (value: SetStateAction<number>) => void;
};

const BuyNowModal = ({ isOpen, setIsOpen, buyNow }: BuyNowModalProps) => {
  const orderData = useSelector((state: RootState) => state.cart.orderData);
  const orderDetails = useSelector((state: RootState) => state.cart);
  const orderPreferences = useSelector(
    (state: RootState) => state.cart.orderPreferences
  );
  const dispatch = useDispatch();
  const toast = useToast();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if(!isOpen){
      dispatch(cartActions.clearCart());
    }
    if (arePersonalDetailsMissing(orderPreferences.orderDetails) && isOpen) {
      toast({
        title: "Missing Personal Details  In Order Preferences",
        description:
          "Required personal details are missing. Please fill them in order preferences before proceeding",
        status: "error",
        duration: 5000,
        isClosable: true,
      });
      setIsOpen(false);
    } else if (
      areDeliveryDetailsMissing(orderPreferences.orderDetails) &&
      isOpen
    ) {
      toast({
        title: "Missing Delivery Details In Order Preferences",
        description:
          "Required delivery details are missing. Please fill them in order preferences before proceeding",
        status: "error",
        duration: 5000,
        isClosable: true,
      });
      setIsOpen(false);
    } else if (isOpen) {
      dispatch(
        cartActions.updateDeliveryMethod(orderPreferences.deliveryMethod)
      );
      dispatch(cartActions.updatePaymentMethod(orderPreferences.paymentMethod));
      dispatch(cartActions.updateOrderDetails(orderPreferences.orderDetails));
      setIsLoading(false);
    }
  }, [
    dispatch,
    isOpen,
    orderPreferences.deliveryMethod,
    orderPreferences.orderDetails,
    orderPreferences.paymentMethod,
    setIsOpen,
    toast,
  ]);

  return (
    <>
      {!isLoading && (
        <ConfirmOrderModal
          isOpen={isOpen}
          setIsOpen={setIsOpen}
          changeCurrentCartStep={buyNow}
          orderData={orderData}
          orderDetails={orderDetails}
        />
      )}
    </>
  );
};

export default BuyNowModal;
