import { HStack } from "@chakra-ui/react";
import MenuLink from "./MenuLink";

export type Link = {
  text: string;
  path: string;
};

export const LINKS: Link[] = [
  {
    text: "home",
    path: "/home",
  },
  {
    text: "shop",
    path: "/shop",
  },
  {
    text: "music",
    path: "/music",
  },
  {
    text: "reviews",
    path: "/reviews",
  },
  {
    text: "recommended",
    path: "/recommended",
  },
  {
    text: "forum",
    path: "/forum",
  },
  {
    text: "about",
    path: "/about",
  },
];

const MenuLinks: React.FC<{}> = () => {

  return <HStack h="inherit" lineHeight="inherit" spacing={{ base: "0.65em", xl: "1.5em" }}>
    {LINKS.map((link) => (
      <MenuLink
        key={link.text}
        to={link.path}
        text={link.text} />
    ))}
  </HStack>
}
export default MenuLinks;
