import { useToast } from "@chakra-ui/react";
import { DateTime } from "luxon";
import { useState, useEffect } from "react";
import { isProductPage } from "../../../../../../components/HomePage/NewItems/NewItems";
import productSlice, { ProductQuerySpecification } from "../../../../productSlice";
import { Page, getEmptyPage } from "../../../Page";
import { CommentOutputDTO } from "../CommentOutputDTO";
import { ProducerDTO } from "../ProducerDTO";
import { ProductVariantOutputDTO } from "../variant/ProductVariantOutputDTO";

export enum ProductType {
    AMPLIFIERS = "Amplifiers",
    GRAMOPHONES = "Gramophones",
    HEADPHONES = "Headphones",
    MICROPHONES = "Microphones",
    SPEAKERS = "Speakers",
    SUBWOOFERS = "Subwoofers"
};

export interface ProductOutputDTO {
    id: number,
    name: string,
    description: string,
    weight: number,
    price: number,
    minimumFrequency: number,
    maximumFrequency: number,
    productType: ProductType,
    creationTimestamp: DateTime,
    modificationTimestamp: DateTime,
    comments: CommentOutputDTO[],
    averageRating: number,
    observers: string[],
    producer: ProducerDTO,
    variants: ProductVariantOutputDTO[]
};

export function isProductOutputDTO(value: any): value is ProductOutputDTO {
    return "productType" in value && "id" in value;
}

export function isProductOutputDTOArray(value: any[]): value is ProductOutputDTO[] {
    return value.every(isProductOutputDTO);
}

export function useGetByQuery(querySpecification: ProductQuerySpecification, minimumRating: number): Page<ProductOutputDTO> {
    const toast = useToast();
    const [trigger] = productSlice.useLazyGetByQueryQuery();
    const [products, setProducts] = useState<Page<ProductOutputDTO>>(getEmptyPage());
    const [errorMessages, setErrorMessages] = useState<string[]>([]);
    const generalErrorMessage = "Could not fetch products from our service";

    useEffect(() => {
        const fetchProducts = async () => {
            const response = await trigger({query: querySpecification, rating: minimumRating});

            if (!response.data) {
                setErrorMessages([generalErrorMessage]);
            } else if (isProductPage(response.data)) {
                setProducts(response.data); 
            } else {
                setErrorMessages(response.data);
            }
        }

        fetchProducts().catch(() => setErrorMessages([generalErrorMessage]));
    }, [minimumRating, querySpecification, trigger]);

    useEffect(() => {
        errorMessages.forEach(message => toast({
            title: 'Something went wrong',
            description: message,
            status: 'error',
            duration: 9000,
            isClosable: true, 
        }));   
     }, [errorMessages, toast]);

     return products;
}