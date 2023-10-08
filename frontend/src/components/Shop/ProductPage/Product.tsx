import { VStack, Flex, useBreakpointValue } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { ProductOutputDTO } from "../../../store/api/result/dto/product/base/ProductOutputDTO";
import ProductBasicInfo from "./ProductBasicInfo/ProductBasicInfo";
import ProductImage from "./ProductBasicInfo/ProductImage";
import ProductOptionsMenu from "./ProductOptionsMenu";
import { getDownloadURL, ref } from "firebase/storage";
import storage from "../../../config/firebase-config";
import { useKeycloak } from "@react-keycloak/web";
import useUserProfile from "../../../hooks/useUserProfile";
import add_interaction, { InteractionType } from "../../../functions/recommendation-functions";
import ProductsVariantsMenu from "./ProductVariants/ProductsVariantsMenu";
import { useVariantGroups } from "../../../utils/Utils";

export interface ProductProps {
  data: ProductOutputDTO;
}

const Product: React.FC<ProductProps> = ({ data }) => {
  const flexDirection = useBreakpointValue({ base: "column", lg: "row" });
  const [image, setImage] = useState<string>();
  const { keycloak, initialized } = useKeycloak();
  const variantSelectorResult = useVariantGroups(data);
  const userProfile = useUserProfile();

  useEffect(() => {
    const fetchVariantImage = async () => {
      const variant = variantSelectorResult.selectedVariant;

      if (variant) {
        const downloadUrl = await getDownloadURL(
          ref(storage, variant.imageUrl)
        );

        setImage(downloadUrl);
      }
    };

    fetchVariantImage().catch(() => setImage(undefined));
  }, [data.variants, variantSelectorResult]);

  useEffect(() => {
    if (initialized && keycloak.authenticated && userProfile?.username !== undefined) {
      add_interaction(userProfile?.username, data.id, InteractionType.VIEW);
    }
  }, [data.id, initialized, keycloak.authenticated, userProfile?.username]);

  return (
    <VStack
      boxShadow="
    0px 0px 0.8px rgba(0, 0, 0, 0.11),
  0px 0px 1.7px rgba(0, 0, 0, 0.087),
  0px 0px 2.9px rgba(0, 0, 0, 0.076),
  0px 0px 4.3px rgba(0, 0, 0, 0.068),
  0px 0px 6.3px rgba(0, 0, 0, 0.061),
  0px 0px 8.9px rgba(0, 0, 0, 0.055),
  0px 0px 12.6px rgba(0, 0, 0, 0.049),
  0px 0px 18.3px rgba(0, 0, 0, 0.042),
  0px 0px 28.1px rgba(0, 0, 0, 0.034),
  0px 0px 50px rgba(0, 0, 0, 0.023)"
      alignSelf="center"
      width="95%"
      minHeight="95%"
      mt="1%"
      pb={{
        base: "12px",
        sm: "14px",
        md: "16px",
        lg: "18px",
        xl: "20px",
      }}
      pt="2%"
      rounded="xl"
    >
      <Flex mb="1%" direction={flexDirection! === "row" ? "row" : "column"}>
        <ProductImage url={image} />
        <ProductBasicInfo
          data={data}
          selectedVariant={variantSelectorResult.selectedVariant}
        />
      </Flex>
      <ProductsVariantsMenu variantsGroups={variantSelectorResult.variantGroups} />
      <ProductOptionsMenu
        product={data}
        selectedVariantId={variantSelectorResult.selectedVariant?.id ?? 0}
      />
    </VStack>
  );
};

export default Product;
