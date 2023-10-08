import { useToast } from "@chakra-ui/react";
import { useState, useEffect } from "react";
import { ProductQuerySpecification, useLazyGetSubwoofersByQueryQuery } from "../../../../productSlice";
import { getEmptyPage, Page } from "../../../Page";
import { ProductOutputDTO } from "./ProductOutputDTO";

export enum SubwooferType {
    ACTIVE = "Active",
    PASSIVE = "Passive",
    PORTED = "Ported",
    SEALED_CABINET = "Sealed-Cabinet",
    PASSIVE_RADIATOR = "Passive-Radiator",
    BANDPASS = "Bandpass",
    HORN_LOADED = "Horn-Loaded"
};

export interface SubwoofersOutputDTO extends ProductOutputDTO {
    power: number,
    type: SubwooferType
};

export function isSubwoofersPage(value: Page<SubwoofersOutputDTO> | string[]): value is Page<SubwoofersOutputDTO> {
    return "content" in value;
}

export function useGetSubwoofersByQuery(querySpecification: ProductQuerySpecification, minimumRating: number): Page<SubwoofersOutputDTO> {
    const toast = useToast();
    const [ trigger ] = useLazyGetSubwoofersByQueryQuery();
    const [speakers, setSpeakers] = useState<Page<SubwoofersOutputDTO>>(getEmptyPage());
    const [errorMessages, setErrorMessages] = useState<string[]>([]);
    const generalErrorMessage = "Could not fetch subwoofers from our service";

    useEffect(() => {
        const fetchSubwoofers = async () => {
            const response = await trigger({query: querySpecification, rating: minimumRating});

            if (!response.data) {
                setErrorMessages([generalErrorMessage]);
            } else if (isSubwoofersPage(response.data)) {
                setSpeakers(response.data); 
            } else {
                setErrorMessages(response.data);
            }
        }

        fetchSubwoofers().catch(() => setErrorMessages([generalErrorMessage]));
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

    return speakers; 
}