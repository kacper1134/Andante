import Card from "../../Card/Card";
import noimage from "../../../static/noimage.png";
import { VStack, HStack, Image, Text, Button, Icon } from "@chakra-ui/react";
import { GiShop } from "react-icons/gi";
import { useFirebase } from "../../../hooks/useFirebase";
import { useNavigate } from "react-router-dom";

export interface ProductCardData {
  id: number;
  image: string;
  name: string;
  price: string;
}

export interface ProductCardProps {
  data: ProductCardData;
  width: string;
}

const ProductCard: React.FC<ProductCardProps> = ({ data, width }) => {
  const image = useFirebase(data.image, noimage);

  return (
    <Card width={width} height="350px" borderRadius="16px" px="8px" py="8px">
      <VStack userSelect="none">
        <Image
          width="220px"
          height="220px"
          src={image}
          fit="contain"
          userSelect="none"
        />
        <Text
          textAlign="center"
          color="primary.800"
          textStyle="h3"
        >
          {data.name}
        </Text>
        <HStack w="100px" justifyContent="space-around" fontSize="20px">
          <Text color="black" textStyle="p">
            {data.price}
          </Text>
        </HStack>
        <GoToShopButton id={data.id} />
      </VStack>
    </Card>
  );
};

const GoToShopButton: React.FC<{id: number}> = ({id}) => {
  const navigate = useNavigate();

  return (
    <Button
      borderRadius="16px"
      leftIcon={
        <Icon as={GiShop} color="white" boxSize="20px" pb="4px" />
      }
      colorScheme="primary"
      onClick={() => navigate("/shop/product/" + id)}
    >
      Check it Out
    </Button>
  );
};

export default ProductCard;
