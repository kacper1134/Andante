import {
  Divider,
  Box,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalHeader,
  ModalOverlay,
  VStack,
  useBreakpointValue,
} from "@chakra-ui/react";
import { Dispatch, SetStateAction, useEffect, useRef, useState } from "react";
import { DeliveryPickupLocation } from "../CartForms";
import { cartActions } from "../../../store/cart/cartSlice";
import { useDispatch, useSelector } from "react-redux";
import { AnyAction } from "@reduxjs/toolkit";
import { RootState } from "../../../store";

type DeliveryMethodProps = {
  isOpen: boolean;
  setIsOpen: Dispatch<SetStateAction<boolean>>;
};

const initMap = (
  dispatch: Dispatch<AnyAction>,
  deliveryLocation: DeliveryPickupLocation | null,
  setIsOpen: Dispatch<SetStateAction<boolean>>
) => {
  const selectedPos = deliveryLocation
    ? { code: deliveryLocation.code, operator: deliveryLocation.operator }
    : null;
  if (document.getElementById("bpWidget")) {
    (window as any).BPWidget.init(document.getElementById("bpWidget"), {
      googleMapApiKey: "AIzaSyCBte9RCEqpFfJLF5pxaLRPDLJ_rc_PmlU",
      callback: function (point: any) {
        let [street, streetNumber] = point.street
          .split(/(\d+)/g)
          .map((text: string) => text.trim());
        let postalCode = point.postalCode;
        if (postalCode.length > 3 && postalCode[2] !== "-") {
          postalCode =
            postalCode.substring(0, 2) + "-" + postalCode.substring(2);
        }
        let brand = point.brand === "ZABKA" ? "ŻABKA" : point.brand;

        const deliveryLocation: DeliveryPickupLocation = {
          country: "Poland",
          city: point.city,
          street: street,
          streetNumber: streetNumber,
          postCode: postalCode,
          brand: brand,
          code: point.code,
          operator: point.operator,
        };
        dispatch(cartActions.updateDeliveryPickupLocation(deliveryLocation));
        setIsOpen(false);
      },
      posType: "DELIVERY",
      selectedPos: selectedPos,
    });
  }
};

const checkPointsLoaded = (setCanClose: Dispatch<SetStateAction<boolean>>) => {
  setTimeout(() => {
    if (document.getElementById("BPPosCount")?.textContent?.length !== 62) {
      setCanClose(true);
    } else {
      checkPointsLoaded(setCanClose);
    }
  }, 500);
};

const DeliveryMethodModal = ({ isOpen, setIsOpen }: DeliveryMethodProps) => {
  const dispatch = useDispatch();
  const onCloseHandler = () => setIsOpen(false);
  const widgetRef = useRef<HTMLDivElement>(null);
  const [canClose, setCanClose] = useState(false);
  const deliveryLocation = useSelector(
    (state: RootState) => state.cart.deliveryPickupLocation
  );

  useEffect(() => {
    if (isOpen) {
      setTimeout(() => {
        initMap(dispatch, deliveryLocation, setIsOpen);
        checkPointsLoaded(setCanClose);
      }, 0);
    } else {
      setCanClose(false);
    }
  }, [deliveryLocation, dispatch, isOpen, setIsOpen]);

  const mapWidth = useBreakpointValue({
    base: "340px",
    sm: "420px",
    md: "600px",
    lg: "934px",
  });
  const modalSize = useBreakpointValue({
    base: "md",
    sm: "lg",
    md: "2xl",
    lg: "5xl",
  });

  return (
    <Modal
      isCentered
      closeOnOverlayClick={false}
      isOpen={isOpen}
      onClose={onCloseHandler}
      size={modalSize}
    >
      <ModalOverlay backdropFilter="blur(3px)" />
      <ModalContent w="100%">
        <ModalHeader
          fontSize="25px"
          bg="primary.400"
          color="white"
          textStyle="h1"
        >
          Select a pickup point
        </ModalHeader>
        <Divider />
        <ModalCloseButton fontSize="15px" disabled={!canClose} color="white" />
        <ModalBody as={VStack} w="100%">
          <Box
            ref={widgetRef}
            id="bpWidget"
            height="73vh"
            width={mapWidth}
          ></Box>
        </ModalBody>
      </ModalContent>
    </Modal>
  );
};

export default DeliveryMethodModal;
