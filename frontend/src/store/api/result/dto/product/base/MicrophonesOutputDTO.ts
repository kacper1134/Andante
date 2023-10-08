import { useToast } from "@chakra-ui/react";
import { useState, useEffect } from "react";
import { ProductQuerySpecification, useLazyGetMicrophonesByQueryQuery } from "../../../../productSlice";
import { getEmptyPage, Page } from "../../../Page";
import { ProductOutputDTO } from "./ProductOutputDTO";

export enum MicrophoneType {
    DYNAMIC = "Dynamic",
    LARGE_CONDENSER = "Large-Condenser",
    SMALL_CONDENSER = "Small-Condenser",
    RIBBON = "Ribbon"
};

export interface MicrophonesOutputDTO extends ProductOutputDTO {
    wireless: boolean,
    bluetoothStandard: number,
    type: MicrophoneType
};

export function isMicrophonesPage(value: Page<MicrophonesOutputDTO> | string[]): value is Page<MicrophonesOutputDTO> {
    return "content" in value;
}

export function useGetMicrophonesByQuery(querySpecification: ProductQuerySpecification, minimumRating: number): Page<MicrophonesOutputDTO> {
    const toast = useToast();
    const [ trigger ] = useLazyGetMicrophonesByQueryQuery();
    const [microphones, setMicrophones] = useState<Page<MicrophonesOutputDTO>>(getEmptyPage());
    const [errorMessages, setErrorMessages] = useState<string[]>([]);
    const generalErrorMessage = "Could not fetch microphones from our service";

    useEffect(() => {
        const fetchMicrophones = async () => {
            const response = await trigger({query: querySpecification, rating: minimumRating});

            if (!response.data) {
                setErrorMessages([generalErrorMessage]);
            } else if (isMicrophonesPage(response.data)) {
                setMicrophones(response.data); 
            } else {
                setErrorMessages(response.data);
            }
        }

        fetchMicrophones().catch(() => setErrorMessages([generalErrorMessage]));
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

    return microphones; 
}