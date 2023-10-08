import {
  Box,
  Button,
  Collapse,
  HStack,
  Icon,
  Input,
  Text,
  useBreakpointValue,
  useToast,
  useToken,
} from "@chakra-ui/react";
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { BiCommentCheck } from "react-icons/bi";
import { Rating } from "react-simple-star-rating";
import {
  useCreateCommentMutation,
  useEditCommentMutation,
} from "../../../../../store/api/productSlice";
import UserTextEditor from "../../../../common/UserTextEditor";
import {
  contentInputFontSize,
  headerInputFontSize,
  headerSize,
  labelMargin,
  spacing,
} from "./NewProductFontSizes";
import { CommentInputDTO } from "../../../../../store/api/result/dto/product/CommentInputDTO";
import { useKeycloak } from "@react-keycloak/web";
import { getUserDetails } from "../../../../../utils/KeycloakUtils";
import add_interaction, {
  InteractionType,
} from "../../../../../functions/recommendation-functions";

type NewProductCommentFormProps = {
  isOpen: boolean;
  isEdit: boolean;
  productId: number;
  setIsOpen: Dispatch<SetStateAction<boolean>>;
  currentRating?: number;
  currentTitle?: string;
  setIsCreate?: React.Dispatch<React.SetStateAction<boolean>>,
  currentContent?: string;
  observers?: string[];
  commentId?: number;
};

const NewProductCommentForm = ({
  isOpen,
  isEdit,
  setIsOpen,
  productId,
  currentRating,
  currentTitle,
  currentContent,
  commentId,
  observers,
  setIsCreate,
}: NewProductCommentFormProps) => {
  const starSize = parseInt(useBreakpointValue(headerSize)!);
  const [secondary400] = useToken("colors", ["secondary.400"]);
  const [username, setUsername] = useState<string>();
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [rating, setRating] = useState(0);
  const toast = useToast();
  const [createComment] = useCreateCommentMutation();
  const [editComment] = useEditCommentMutation();
  const { keycloak } = useKeycloak();

  useEffect(() => {
    if (keycloak.idTokenParsed) {
      const userDetails = getUserDetails(keycloak.idTokenParsed);

      setUsername(userDetails.personal.username);
    }
  }, [keycloak]);

  useEffect(() => {
    if (isOpen) {
      setRating(currentRating ? currentRating : 0);
      setTitle(currentTitle ? currentTitle : "");
      setContent(currentContent ? currentContent : "");
    }
  }, [currentContent, currentRating, currentTitle, isOpen]);

  const onRatingChange = (rate: number) => {
    if (rate === rating) setRating(0);
    else setRating(rate);
  };

  const width = useBreakpointValue({
    base: "250px",
    sm: "325px",
    md: "400px",
    lg: "600px",
    xl: "750px",
    "2xl": "900px",
  });

  const saveComment = () => {
    const errorMessage = validateComment();

    if (errorMessage) {
      toast({
        title: "Could not save your comment",
        description: errorMessage,
        status: "error",
        duration: 9000,
        isClosable: true,
      });
    } else {
      const comment: CommentInputDTO = {
        username: username!,
        rating: rating,
        title: title,
        content: content,
        productId: productId,
        observers: [],
      };

      createComment(comment).then(() => {
        setIsCreate!(true);
        setIsOpen(false);
        clearInput();
        add_interaction(username!, productId, InteractionType.RATING, rating);
        toast({
          title: "Comment successfully saved",
          description: "Your comment was successfully saved within Andante",
          status: "success",
          duration: 9000,
          isClosable: true,
        });
      });
    }
  };

  const editCommentHandler = () => {
    const errorMessage = validateComment();

    if (errorMessage) {
      toast({
        title: "Could not save your comment",
        description: errorMessage,
        status: "error",
        duration: 9000,
        isClosable: true,
      });
    } else {
      const comment: CommentInputDTO = {
        id: commentId,
        username: username!,
        rating: rating,
        title: title,
        content: content,
        productId: productId,
        observers: observers!,
      };

      editComment(comment).then(() => {
        setIsOpen(false);
        clearInput();
        toast({
          title: "Comment successfully saved",
          description: "Your comment was successfully edited within Andante",
          status: "success",
          duration: 9000,
          isClosable: true,
        });
      });
    }
  };

  const clearInput = () => {
    setTitle("");
    setContent("");
    setRating(0);
  };

  const validateComment = () => {
    if (title.length < 3) {
      return "Title must be at least 3 characters long";
    }

    if (!username) {
      return "Your personal details are not available right now";
    }
  };

  return (
    <Box
      as={Collapse}
      in={isOpen}
      rounded="2xl"
      mt="10px"
      bg="white"
      boxShadow="1px 2px 2px 1px rgba(0,0,0,0.25)"
      width={width}
    >
      <Box
        margin={{
          base: "14px",
          sm: "16px",
          md: "18px",
          lg: "20px",
          xl: "22px",
        }}
      >
        <HStack mb={labelMargin}>
          <Text fontSize={headerSize} textStyle="p">
            Your rating
          </Text>
          <Rating
            key={rating}
            initialValue={rating}
            fillColor={secondary400}
            size={starSize}
            SVGstorkeWidth={1.5}
            onClick={onRatingChange}
            allowFraction
          />
        </HStack>
        <Box mt={spacing}>
          <Input
            placeholder="Your amazing title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            fontSize={headerInputFontSize}
            fontWeight="bold"
            textStyle="p"
          />
        </Box>

        <Box mt={spacing}>
          <UserTextEditor
            setContent={setContent}
            initialValue={currentContent}
            content={content}
          />
        </Box>

        <Button
          fontSize={contentInputFontSize}
          height="fit-content"
          mt={spacing}
          colorScheme="primary"
          py="1.5"
          textStyle="p"
        >
          <HStack onClick={isEdit ? editCommentHandler : saveComment}>
            {isEdit ? (
              <Text>Edit your comment</Text>
            ) : (
              <Text>Save your comment</Text>
            )}
            <Icon as={BiCommentCheck} />
          </HStack>
        </Button>
      </Box>
    </Box>
  );
};

export default NewProductCommentForm;
