import {
  VStack,
  Text,
  Wrap,
  WrapItem,
  Avatar,
  useToken,
} from "@chakra-ui/react";
import { motion } from "framer-motion";
import { useState } from "react";
import CommunitiesModal from "./CommunitiesModal";
import headphones from "../../../static/headphones.png";
import useFirebaseWithMultipleImages from "../../../hooks/useFirebaseWithMultipleImages";

export interface Community {
  id: number;
  name: string;
}

export interface CommunitiesProps {
  communities: Community[];
}

const communitiesImages = new Map([
  ["Best Headphones", "/communities/bestheadphones.jpg"],
  ["Music", "/communities/music.jpg"],
  ["Your stories", "/communities/stories.jpg"],
  ["General", "/communities/general.jpg"],
]);

const Communities: React.FC<CommunitiesProps> = ({ communities }) => {
  const primary400 = useToken("colors", "primary.400");
  const [modalState, setModalState] = useState({
    isModalOpen: false,
    chatId: 0,
    name: "",
  });

  const images = useFirebaseWithMultipleImages(
    communities.map((c) => communitiesImages.get(c.name) ?? ""),
    headphones
  );

  return (
    <VStack w="100%" bg="purple.50" px="16px" py="8px">
      <Text
        color="primary.400"
        textStyle="h3"
        fontSize="16px"
        userSelect="none"
        alignSelf="start"
      >
        COMMUNITIES
      </Text>
      <Wrap alignSelf="start">
        {communities.map((community, index) => (
          <VStack as={WrapItem} key={index} px="8px" userSelect="none">
            <Avatar
              src={images[index]}
              boxSize="64px"
              bg="white"
              p="4px"
              border={`1px solid ${primary400}`}
            />
            <Text
              as={motion.p}
              whileHover={{ scale: 1.1 }}
              textStyle="p"
              fontWeight={400}
              fontSize="18px"
              color="primary.400"
              cursor="pointer"
              onClick={() => {
                setModalState({
                  isModalOpen: true,
                  chatId: community.id,
                  name: community.name,
                });
              }}
            >
              {community.name.toUpperCase()}
            </Text>
          </VStack>
        ))}
      </Wrap>
      <CommunitiesModal
        isOpen={modalState.isModalOpen}
        name={modalState.name}
        chatId={modalState.chatId}
        setModalState={setModalState}
      />
    </VStack>
  );
};

export default Communities;
