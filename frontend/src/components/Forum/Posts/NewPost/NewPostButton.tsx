import { Button, Icon } from "@chakra-ui/react";
import { MdPostAdd } from "react-icons/md";
import { buttonTextFontSize } from "../../common/ForumDimensions";
import { useTranslation } from "react-i18next";

type NewPostButtonProps = {
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

const NewPostButton = ({ setIsOpen }: NewPostButtonProps) => {

  const { t } = useTranslation();

  return (
    <Button
      colorScheme="purple"
      height="fit-content"
      py="3%"
      fontSize={buttonTextFontSize}
      onClick={() => setIsOpen((prevValue) => !prevValue)}
      textStyle="p"
    >
      {t("forum-section.new-button")}
      <Icon as={MdPostAdd} ml="3" />
    </Button>
  );
};

export default NewPostButton;
