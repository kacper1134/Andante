import {
  Center,
  Icon,
  Menu,
  MenuButton,
  MenuItem,
  MenuList,
} from "@chakra-ui/react";
import { useState } from "react";
import { AiFillDelete, AiFillEdit } from "react-icons/ai";
import { BsThreeDots } from "react-icons/bs";
import { useNavigate } from "react-router-dom";
import BlogDeleteModal from "./BlogDeleteModal";
import {
  postAuthorFontSize,
  postDescriptionFontSize,
  postOptionPadding,
} from "./BlogMainPageSizes";

const menuItems = [
  { text: "Edit", icon: AiFillEdit, color: "orange" },
  { text: "Delete", icon: AiFillDelete, color: "red" },
];

type BlogPostOptionsProps = {
  title: string;
  id: string;
  setUpdated: React.Dispatch<React.SetStateAction<boolean>>;
  image: any;
  slug: {
    _type: string;
    current: string;
  };
};

const BlogPostOptions = ({
  title,
  id,
  image,
  setUpdated,
  slug,
}: BlogPostOptionsProps) => {
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const navigate = useNavigate();

  return (
    <>
      <Menu>
        <MenuButton>
          <Center h="100%">
            <Icon
              as={BsThreeDots}
              color="primary.700"
              fontSize={postDescriptionFontSize}
              cursor="pointer"
            />
          </Center>
        </MenuButton>
        <MenuList
          bg="primary.50"
          color="primary.500"
          fontSize={postAuthorFontSize}
          minW="0"
          w="fit-content"
        >
          {menuItems.map((item, index) => (
            <MenuItem
              key={index}
              _hover={{ bg: "#D1C4E9" }}
              _focus={{ bg: "#D1C4E9" }}
              fontWeight="bold"
              textStyle="p"
              pr={postOptionPadding}
              onClick={() =>
                item.text === "Delete"
                  ? setIsDeleteModalOpen(true)
                  : navigate("./edit/" + slug.current)
              }
            >
              <Icon as={item.icon} mr="10px" color={item.color} /> {item.text}
            </MenuItem>
          ))}
        </MenuList>
      </Menu>
      <BlogDeleteModal
        isOpen={isDeleteModalOpen}
        setIsOpen={setIsDeleteModalOpen}
        title={title}
        id={id}
        image={image}
        setUpdated={setUpdated}
      />
    </>
  );
};

export default BlogPostOptions;
