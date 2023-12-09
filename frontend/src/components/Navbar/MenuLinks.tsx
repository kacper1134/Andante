import { HStack } from "@chakra-ui/react";
import MenuLink from "./MenuLink";

export type Link = {
  text: string;
  path?: string;
  submenu?: Link[];
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
    text: "blog",
    submenu: [
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
    ],
  },
  {
    text: "forum",
    path: "/forum",
  },
  {
    text: "about",
    path: "/about",
  },
  {
    text: "faq",
    path: "/faq",
  }
];

const MenuLinks: React.FC<{}> = () => {

  return <HStack h="inherit" lineHeight="inherit" spacing={{ base: "0.65em", xl: "1.5em" }}>
    {LINKS.map((link) => (
      <MenuLink
        key={link.text}
        link={link} />
    ))}
  </HStack>
}
export default MenuLinks;
