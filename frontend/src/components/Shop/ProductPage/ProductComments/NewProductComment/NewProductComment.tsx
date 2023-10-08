import { VStack } from "@chakra-ui/react";
import { useState } from "react";
import { UserDetails } from "../../../../../utils/KeycloakUtils";
import NewProductCommentButton from "./NewProductCommentButton";
import NewProductCommentForm from "./NewProductCommentForm";

export interface NewProductCommentProps {
  productId: number,
  userDetails: UserDetails,
  setIsCreate: React.Dispatch<React.SetStateAction<boolean>>,
}

const NewProductComment: React.FC<NewProductCommentProps> = ({productId, userDetails, setIsCreate}) => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <VStack>
      <NewProductCommentButton setIsOpen={setIsOpen} />
      <NewProductCommentForm
        isOpen={isOpen}
        isEdit={false}
        setIsOpen={setIsOpen}
        productId={productId}
        setIsCreate={setIsCreate}
      />
    </VStack>
  );
};

export default NewProductComment;
