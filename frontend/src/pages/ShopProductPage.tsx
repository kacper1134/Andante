import { Flex } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Product from "../components/Shop/ProductPage/Product";
import { ProductOutputDTO } from "../store/api/result/dto/product/base/ProductOutputDTO";
import { useGetAllByIdQuery } from "../store/api/productSlice";
import { useDispatch } from "react-redux";
import { innerActions } from "../store/inner/innerSlice";

function isProductOutputDTOList(value: ProductOutputDTO[] | string[]): value is ProductOutputDTO[] {  
  if (value.length === 0) {
    return false;
  }

  const firstElement = value[0];

  if (typeof firstElement === "string") {
    return false;
  }

  return true;
}


const ShopProductPage = () => {
  const { id } = useParams();
  const [product, setProduct] = useState<ProductOutputDTO>();
  const { data } = useGetAllByIdQuery([+id!], {refetchOnMountOrArgChange: true});
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const generalErrorMessage = "Could not fetch requested product from our service";

  useEffect(() => {
      if (data) {
        if (!isProductOutputDTOList(data)) {
          if (data.length === 0) {
            dispatch(innerActions.addErrorMessages([generalErrorMessage]));
          } else {
            dispatch(innerActions.addErrorMessages(data));
          }
          navigate("/home");
        } else {
          setProduct(data[0]);
        }
      }
  }, [data, dispatch, navigate]);

  return <Flex width="full" minHeight="full" direction="column">
    {product && <Product data={product}></Product>}
  </Flex>;
};

export default ShopProductPage;
