import { HStack, useBreakpointValue } from "@chakra-ui/react";
import { GrTwitter } from "react-icons/gr";
import { FaFacebook } from "react-icons/fa";
import { AiOutlineInstagram, AiFillLinkedin } from "react-icons/ai";
import SocialMediaIcon, { SocialMediaIconType } from "./SocialMediaIcon";

export const socialMediaIcons: SocialMediaIconType[] = [
  {
    label: "Twitter",
    url: "https://twitter.com/",
    color: "twitter.500",
    icon: GrTwitter,
  },
  {
    label: "Facebook",
    url: "https://facebook.com/",
    color: "facebook.500",
    icon: FaFacebook,
  },
  {
    label: "Instagram",
    url: "https://instagram.com/",
    color: "#c13584",
    icon: AiOutlineInstagram,
  },
  {
    label: "Linkedin",
    url: "https://linkedin.com/",
    color: "#0E76A8",
    icon: AiFillLinkedin,
  },
];

const SocialMediaIcons = () => {
  const iconSize = useBreakpointValue({
    base: "16px",
    sm: "20px",
    md: "24px",
    lg: "28px",
    xl: "32px",
  })!;

  return (
    <HStack spacing={{ base: "2", sm: "5" }}>
      {socialMediaIcons.map((icon, index) => (
        <SocialMediaIcon key={index} icon={icon} size={iconSize} />
      ))}
    </HStack>
  );
};

export default SocialMediaIcons;
