import { Box, Image, useBreakpointValue } from "@chakra-ui/react";
import { imageHeight } from "./ProductDimensions";
import noimage from "../../../../static/noimage.png";
import { useEffect, useState } from "react";
import { getDownloadURL, ref } from "firebase/storage";
import storage from "../../../../config/firebase-config";

type ProductImageProps = {
  url?: string;
};

const ProductImage = ({ url }: ProductImageProps) => {
  const [image, setImage] = useState(noimage);

  useEffect(() => {
    if (url) {
      const fetchImage = async () => {
        const downloadURL = await getDownloadURL(ref(storage, url));

        setImage(downloadURL);
      }

      fetchImage().catch(() => setImage(noimage));
    } else {
      setImage(noimage);
    } 
  }, [url])

  const height = useBreakpointValue(imageHeight);

  return (
    <Box width={parseInt(height!) * 1.5} height={height}>
      <Image src={image} fit="fill" w="inherit" h="inherit" />
    </Box>
  );
};

export default ProductImage;
