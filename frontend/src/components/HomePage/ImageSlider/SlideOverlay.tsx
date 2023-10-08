import { Box, VStack, HStack, Spacer, Flex } from "@chakra-ui/react";

type SlideOverlayProps = {
  children: React.ReactNode;
};

const SlideOverlay = ({ children }: SlideOverlayProps) => {
  return (
    <Flex h="100vh" position="absolute" top="0" zIndex="0">
      <Box w="100%">
        <HStack height="100%">
          <VStack spacing="8" pl={{ lg: "5em", md: "3em", base: "1em" }}>
            {children}
          </VStack>
          <Spacer />
        </HStack>
      </Box>
    </Flex>
  );
};

export default SlideOverlay;
