import { Button, Icon } from "@chakra-ui/react";
import { useTranslation } from "react-i18next";
import { MdOutlineAddComment } from "react-icons/md";

type NewProductCommentButtonProps = {
    setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

const NewProductCommentButton = ({setIsOpen} : NewProductCommentButtonProps) => {
  const {t} = useTranslation();
  return (
      <Button
        alignSelf="start"
        mt="16px"
        py="16px"
        colorScheme="purple"
        height="fit-content"
        fontSize={{
          base: "12px",
          sm: "14px",
          md: "16px",
          lg: "18px",
          xl: "20px",
        }}
        onClick={() => setIsOpen(prevValue => !prevValue)}
        textStyle="p"
      >
        {t("shopPage.productPage.commentsSection.newCommentButton")}<Icon as={MdOutlineAddComment} ml="3" /> 
      </Button>
  );
};

export default NewProductCommentButton;
