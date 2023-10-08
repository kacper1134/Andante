import { useToast } from "@chakra-ui/react";
import { useState, useEffect } from "react";
import { ProductQuerySpecification, useLazyGetSpeakersByQueryQuery } from "../../../../productSlice";
import { getEmptyPage, Page } from "../../../Page";
import { ProductOutputDTO } from "./ProductOutputDTO";

export interface SpeakersOutputDTO extends ProductOutputDTO {
    wireless: boolean,
    bluetoothStandard: number
}

export function isSpeakersPage(value: Page<SpeakersOutputDTO> | string[]): value is Page<SpeakersOutputDTO> {
    return "content" in value;
}

export function useGetSpeakersByQuery(querySpecification: ProductQuerySpecification, minimumRating: number): Page<SpeakersOutputDTO> {
    const toast = useToast();
    const [ trigger ] = useLazyGetSpeakersByQueryQuery();
    const [speakers, setSpeakers] = useState<Page<SpeakersOutputDTO>>(getEmptyPage());
    const [errorMessages, setErrorMessages] = useState<string[]>([]);
    const generalErrorMessage = "Could not fetch speakers from our service";

    useEffect(() => {
        const fetchSpeakers = async () => {
            const response = await trigger({query: querySpecification, rating: minimumRating});

            if (!response.data) {
                setErrorMessages([generalErrorMessage]);
            } else if (isSpeakersPage(response.data)) {
                setSpeakers(response.data); 
            } else {
                setErrorMessages(response.data);
            }
        }

        fetchSpeakers().catch(() => setErrorMessages([generalErrorMessage]));
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