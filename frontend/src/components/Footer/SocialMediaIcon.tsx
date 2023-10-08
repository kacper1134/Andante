import { Center, Icon, Link } from "@chakra-ui/react";
import { IconType } from "react-icons";
import { motion, Variants } from "framer-motion";

export type SocialMediaIconType = {
  label: string;
  url: string;
  color: string;
  icon: IconType;
};

type SocialMediaIconProps = {
  icon: SocialMediaIconType;
  size: string;
};

const SocialMediaIcon = ({ icon, size }: SocialMediaIconProps) => {
  const variants: Variants = {
    hover: {
      scale: 1.1,
    },
  };
  return (
    <Link href={icon.url} alignContent="center" boxSize={size} isExternal>
      <Center
        as={motion.div}
        color={icon.color}
        variants={variants}
        whileHover="hover"
      >
        <Icon as={icon.icon} boxSize={size} />
      </Center>
    </Link>
  );
};

export default SocialMediaIcon;
