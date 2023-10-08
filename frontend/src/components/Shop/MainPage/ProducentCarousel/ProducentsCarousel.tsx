import { SlideType } from "../../../Carousel/Slide";
import CategoriesCarousel from "../../../Carousel/CategoriesCarousel";
import {
  producentsCarouselHeight,
  slideHeight,
  slideMarginX,
  slideWidth,
} from "./ProducentsCarouselDimensions";
import { useEffect, useState } from "react";
import { isProducerDTOArray, ProducerDTO } from "../../../../store/api/result/dto/product/ProducerDTO";
import { useLazyGetTopProducersQuery } from "../../../../store/api/productSlice";
import { useToast } from "@chakra-ui/react";

function toSlideType(producer: ProducerDTO): SlideType {
  return {
    text: producer.name,
    image: producer.imageUrl,
    path: producer.websiteUrl,
    isExternal: true,
  }
}

const ProducentsCarousel = () => {
  const [producers, setProducers] = useState<ProducerDTO[]>([]);
  const [errorMessages, setErrorMessages] = useState<string[]>([]);
  const [ getTopProducersTrigger ] = useLazyGetTopProducersQuery();
  const generalErrorMessage = "Could not fetch producers from our service";
  const toast = useToast();

  useEffect(() => {
    const fetchProducers = async () => {
      const pageRequest = {
        page: 0,
        size: 6,
      };

      const response = await getTopProducersTrigger(pageRequest);

      if (!response.data) {
        setErrorMessages([generalErrorMessage]);
      } else if(isProducerDTOArray(response.data)) {
        setProducers(response.data);
      } else {
        setErrorMessages(response.data);
      }
    }

    fetchProducers().catch(() => setErrorMessages([generalErrorMessage]))
  }, [getTopProducersTrigger]);

  useEffect(() => {
    errorMessages.forEach(message => toast({
        title: 'Something went wrong',
        description: message,
        status: 'error',
        duration: 9000,
        isClosable: true, 
    }));   
 }, [errorMessages, toast]);

  const fontSize = {
    base: "14px",
    sm: "18px",
    md: "22px",
    lg: "26px",
    xl: "30px",
  };

  return (
    <CategoriesCarousel
      categories={producers.map(toSlideType)}
      slideWidth={slideWidth}
      slideHeight={slideHeight}
      slideMargin={slideMarginX}
      carouselHeight={producentsCarouselHeight}
      slideTextFontSize={fontSize}
      otherComponentsWidth={0}
      isTextVisible={false}
    />
  );
};

export default ProducentsCarousel;
