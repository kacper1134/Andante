import { useToast } from "@chakra-ui/react";
import { useState, useEffect } from "react";
import { ProductQuerySpecification, useLazyGetHeadphonesByQueryQuery } from "../../../../productSlice";
import { getEmptyPage, Page } from "../../../Page";
import { ProductOutputDTO } from "./ProductOutputDTO";

export enum ConstructionType {
    OPENED = "Opened",
    SEMI_OPENED = "Semi-Opened",
    CLOSED = "Closed"
};

export enum DriverType {
    DYNAMIC = "Dynamic",
    PLANAR = "Planar",
    ELECTROSTATIC = "Electrostatic",
    BALANCED_ARMATURE = "Balanced Armature",
    BONE_CONDUCTION = "Bone Conduction"
};

export interface HeadphonesOutputDTO extends ProductOutputDTO {
    constructionType: ConstructionType,
    driverType: DriverType,
    wireless: boolean,
    bluetoothStandard: number,
}

export function isHeadphonesPage(value: Page<HeadphonesOutputDTO> | string[]): value is Page<HeadphonesOutputDTO> {
    return "content" in value;
}

export function useGetHeadphonesByQuery(querySpecification: ProductQuerySpecification, minimumRating: number): Page<HeadphonesOutputDTO> {
    const toast = useToast();
    const [ trigger ] = useLazyGetHeadphonesByQueryQuery();
    const [headphones, setHeadphones] = useState<Page<HeadphonesOutputDTO>>(getEmptyPage());
    const [errorMessages, setErrorMessages] = useState<string[]>([]);
    const generalErrorMessage = "Could not fetch headphones from our service";

    useEffect(() => {
        const fetchHeadphones = async () => {
            const response = await trigger({query: querySpecification, rating: minimumRating});

            if (!response.data) {
                setErrorMessages([generalErrorMessage]);
            } else if (isHeadphonesPage(response.data)) {
                setHeadphones(response.data); 
            } else {
                setErrorMessages(response.data);
            }
        }

        fetchHeadphones().catch(() => setErrorMessages([generalErrorMessage]));
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

    return headphones; 
}