import { Spacer, Image, HStack, useBreakpointValue } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import MenuIcons from "./MenuIcons";
import MenuLinks from "./MenuLinks";

export interface NavbarProps {
  height: string,
};

const Navbar: React.FC<NavbarProps> = ({ height }) => {
  const isDropdown = useBreakpointValue({ base: true, lg: false })!;
  const navigate = useNavigate();

  return (
    <HStack
      w="100%"
      px="8px"
      h={height}
      lineHeight={height}
      bg="purple.100"
      position="sticky"
      top="0"
      zIndex="docked"
    >
      <Image
        src="/Andante.png"
        h="75%"
        onClick={() => navigate("/home")}
        cursor="pointer"
      />
      {!isDropdown && <>
        <Spacer />
        <MenuLinks />
      </>}
      <Spacer />
      <MenuIcons isDropdown={isDropdown} />
    </HStack>
  );
};

export default Navbar;
