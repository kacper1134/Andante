import { Box, Flex, useToast } from "@chakra-ui/react";
import { AnimatePresence, motion } from "framer-motion";
import { useEffect, useState } from "react";
import variants from "./AnimationVariants";
import Slide from "./Slide";
import SlideButtons from "./SlideButtons";
import SlideOverlay from "./SlideOverlay";
import { useDispatch } from "react-redux";
import { imageSliderActions } from "../../../store/image-slider/image-slider";
import { useGetPopularProductsQuery } from "../../../store/api/productSlice";
import { isProductOutputDTOArray, ProductOutputDTO } from "../../../store/api/result/dto/product/base/ProductOutputDTO";
import { getDownloadURL, ref } from "firebase/storage";
import noimage from "../../../static/noimage.png";
import storage from "../../../config/firebase-config";

const ImageSlider = () => {
  const dispatch = useDispatch();
  const [errorMessages, setErrorMessages] = useState<string[]>([]);
  const { data: observedProductsData } = useGetPopularProductsQuery({page: 0, size: 3}, {refetchOnMountOrArgChange: true});
  const [images, setImages] = useState<Map<number, string>>(new Map());
  const [topProducts, setTopProducts] = useState<ProductOutputDTO[]>([]);
  const [currentPage, setCurrentPage] = useState<number>(0);
  const toast = useToast();


  useEffect(() => {
    if (observedProductsData) {
      if (isProductOutputDTOArray(observedProductsData)) {
        const fetchImages = async (product: ProductOutputDTO) => {
          const downloadURL = product.variants.length > 0 ? await getDownloadURL(ref(storage,product.variants[0].imageUrl)) : noimage;

          setImages((previousState) => {
            previousState.set(product.id, downloadURL);

            return previousState;
          });
        };

        Promise.all(observedProductsData.map(product => fetchImages(product)))
          .then(() => setTopProducts(observedProductsData));
      } else {
        setErrorMessages(observedProductsData);
      }
    }
  }, [observedProductsData]);
  
  useEffect(() => {
    if (topProducts && topProducts.length > 0) {
      const timer = setTimeout(() => {
        setCurrentPage((currentPage + 1) % topProducts.length);
        dispatch(imageSliderActions.startAnimation())
      }, 10000);

      return () => clearTimeout(timer);
    }
  }, [dispatch, currentPage, topProducts]);

  useEffect(() => {
    errorMessages.forEach(message => toast({
      title: "Something went wrong",
      description: message,
      status: "error",
      duration: 9000,
      isClosable: true,
    }))
  }, [errorMessages]);

  return (
    <Box overflow="hidden" position="relative">
      {topProducts.length !== 0 && 
      <>
      <AnimatePresence
        initial={false}
        mode="popLayout"
        onExitComplete={() => dispatch(imageSliderActions.stopAnimation())}
      >
        <motion.div
          key={currentPage}
          variants={variants}
          initial="init"
          animate="animate"
          exit="exit"
          transition={{
            x: { type: "spring", stiffness: 50, damping: 35 },
            opacity: { duration: 2 },
          }}
        >
          <Flex h="100vh">
            <Slide product={topProducts[currentPage]} image={images.get(topProducts[currentPage].id) ?? noimage} />
          </Flex>
        </motion.div>
      </AnimatePresence>
      <SlideOverlay>
        <SlideButtons noOfSlides={topProducts.length} current={currentPage} setCurrent={setCurrentPage} />
      </SlideOverlay>
      </>
}
    </Box>
  );
};

export default ImageSlider;
