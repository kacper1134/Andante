import { useToast } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { ProductQuerySpecification, useLazyGetAmplifiersByQueryQuery } from "../../../../productSlice";
import { getEmptyPage, Page } from "../../../Page";
import { ProductOutputDTO } from "./ProductOutputDTO";

export enum AmplifierType {
    CURRENT = "Current",
    VOLTAGE = "Voltage",
    TRANSCONDUCTANCE = "Transconductance",
    TRANSRESISTANCE = "Transresistance",
    POWER = "Power",
    OPERATIONAL = "Operational",
    VACUUM_TUBE = "Vacuum Tube",
    DISTRIBUTED = "Distributed"
};

export interface AmplifiersOutputDTO extends ProductOutputDTO {
    power: number,
    amplifierType: AmplifierType
};

export function isAmplifiersPage(value: Page<AmplifiersOutputDTO> | string[]): value is Page<AmplifiersOutputDTO> {
    return "content" in value;
}


export function useGetAmplifiersByQuery(querySpecification: ProductQuerySpecification, minimumRating: number): Page<AmplifiersOutputDTO> {
    const toast = useToast();
    const [ trigger ] = useLazyGetAmplifiersByQueryQuery();
    const [amplifiers, setAmplifiers] = useState<Page<AmplifiersOutputDTO>>(getEmptyPage());
    const [errorMessages, setErrorMessages] = useState<string[]>([]);
    const generalErrorMessage = "Could not fetch amplifiers from our service";

    useEffect(() => {
        const fetchAmplifiers = async () => {
            const response = await trigger({query: querySpecification, rating: minimumRating});

            if (!response.data) {
                setErrorMessages([generalErrorMessage]);
            } else if (isAmplifiersPage(response.data)) {
                setAmplifiers(response.data); 
            } else {
                setErrorMessages(response.data);
            }
        }

        fetchAmplifiers().catch(() => setErrorMessages([generalErrorMessage]));
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

    return amplifiers; 
}