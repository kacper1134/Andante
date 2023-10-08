import { useToast } from "@chakra-ui/react";
import { useState, useEffect } from "react";
import { ProductQuerySpecification, useLazyGetGramophonesByQueryQuery } from "../../../../productSlice";
import { getEmptyPage, Page } from "../../../Page";
import { ProductOutputDTO } from "./ProductOutputDTO";

export enum ConnectivityTechnology {
    AUXILIARY = "Auxiliary",
    BLUETOOTH = "Bluetooth",
    USB = "Usb",
    WIRED = "Wired"
};

export enum TurntableMaterial {
    ACRYLIC = "Acrylic",
    ALLOY_STEEL = "Alloy Steel",
    CARBON_FIBER = "Carbon Fiber",
    ENGINEERED_WOOD = "Engineered Wood",
    METAL = "Metal",
    PLASTIC = "Plastic",
    WOOD = "Wood"
};

export enum MotorType {
    AC = "Ac",
    BRUSHLESS_DC = "Brushless Dc",
    DC = "Dc",
    STEPPING = "Stepping"
};

export enum PowerSource {
    AC = "Ac",
    CORDED_ELECTRIC = "Corded Electric",
    POWER_ADAPTER = "Power Adapter"
};

export interface GramophonesOutputDTO extends ProductOutputDTO {
    connectivityTechnology: ConnectivityTechnology,
    turntableMaterial: TurntableMaterial,
    motorType: MotorType,
    powerSource: PowerSource,
    maximumRotationalSpeed: number,
};

export function isGramophonesPage(value: Page<GramophonesOutputDTO> | string[]): value is Page<GramophonesOutputDTO> {
    return "content" in value;
}

export function useGetGramophonesByQuery(querySpecification: ProductQuerySpecification, minimumRating: number): Page<GramophonesOutputDTO> {
    const toast = useToast();
    const [ trigger ] = useLazyGetGramophonesByQueryQuery();
    const [gramophones, setGramophones] = useState<Page<GramophonesOutputDTO>>(getEmptyPage());
    const [errorMessages, setErrorMessages] = useState<string[]>([]);
    const generalErrorMessage = "Could not fetch gramophones from our service";

    useEffect(() => {
        const fetchGramophones = async () => {
            const response = await trigger({query: querySpecification, rating: minimumRating});

            if (!response.data) {
                setErrorMessages([generalErrorMessage]);
            } else if (isGramophonesPage(response.data)) {
                setGramophones(response.data); 
            } else {
                setErrorMessages(response.data);
            }
        }

        fetchGramophones().catch(() => setErrorMessages([generalErrorMessage]));
    }, [trigger, querySpecification, minimumRating]);

    useEffect(() => {
        errorMessages.forEach(message => toast({
            title: 'Something went wrong',
            description: message,
            status: 'error',
            duration: 9000,
            isClosable: true, 
        }));   
     }, [errorMessages, toast]);

    return gramophones; 
}