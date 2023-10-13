import { Collapse, Text, VStack, HStack, useBreakpointValue, useToast } from "@chakra-ui/react";
import NewProductComment from "./NewProductComment/NewProductComment";
import ProductComment from "./ProductComment";
import PageChanger from "./PageChanger";
import ProductCommentSort from "./ProductCommentSort";
import { useEffect, useState } from "react";
import { ProductOutputDTO } from "../../../../store/api/result/dto/product/base/ProductOutputDTO";
import { CommentSortingOrder, useGetCommentsByQueryQuery } from "../../../../store/api/productSlice";
import { CommentOutputDTO, isCommentOutputDTOPage } from "../../../../store/api/result/dto/product/CommentOutputDTO";
import { useLazyGetUsersImageQuery } from "../../../../store/api/profile-api-slice";
import { RootState } from "../../../../store";
import { useSelector } from "react-redux";
import noprofile from "../../../../static/noprofile.png";
import { getDownloadURL, ref } from "firebase/storage";
import storage from "../../../../config/firebase-config";
import { UserProfileImage } from "../../../Blog/BlogPostPage/PostComments/PostComments";

type ProductFeaturesProps = {
  product: ProductOutputDTO,
  isOpen: boolean;
  canAnimate: boolean;
  setAnimationEnd: React.Dispatch<React.SetStateAction<boolean>>;
  width: {
    base: number;
    lg: number;
    xl: number;
    "2xl": number;
  };
};

const ProductComments = ({
  product,
  isOpen,
  width,
  canAnimate,
  setAnimationEnd,
}: ProductFeaturesProps) => {
  const [page, setPage] = useState(0);
  const userDetails = useSelector((current: RootState) => current.auth.userDetails);
  const [sortingOrder, setSortingOrder] = useState<string>(CommentSortingOrder.NEWEST_FIRST);
  const commentsPerPage = 6;
  const numberOfPages = Math.ceil(product.comments.length / commentsPerPage);
  const [selectedComments, setSelectedComments] = useState<CommentOutputDTO[]>([]);
  const [avatars, setAvatars] = useState<Map<String, string>>(new Map());
  const { data } = useGetCommentsByQueryQuery({
    query: `productId==${product.id}`,
    pageNumber: Math.max(page, 0),
    pageSize: commentsPerPage,
    sortingOrder: CommentSortingOrder[sortingOrder as keyof typeof CommentSortingOrder]
  }, {refetchOnMountOrArgChange: true});
  const [ getUsersImagesTrigger ] = useLazyGetUsersImageQuery();
  const [errorMessages, setErrorMessages] = useState<string[]>([]);
  const [isCreate, setIsCreate] = useState(true);
  const genericImagesErrorMessage = "Could not fetch images from our service";
  const toast = useToast();
  const fontSize = useBreakpointValue({
    base: "14px",
    sm: "16px",
    md: "18px",
    lg: "20px",
    xl: "22px",
    "2xl": "24px"
  });

  const initializeComments = () => {
    if(isCreate) {
      setSelectedComments(product.comments.slice(0, Math.min(commentsPerPage, product.comments.length)));
      setSortingOrder(CommentSortingOrder.NEWEST_FIRST);
      setPage(0);
      setIsCreate(false);
    } else {
      setSelectedComments(product.comments.slice(page * commentsPerPage, Math.min((page + 1) * commentsPerPage, product.comments.length)));
    }
  }

  useEffect(() => {
    initializeComments();
  }, [product]);

  useEffect(() => {
    if (data) {
      if (isCommentOutputDTOPage(data)) {
        setSelectedComments(data.content);

        const uniqueUsernames = Array.from(new Set(data.content.map(comment => comment.username)));

        const fetchImages = async () => {
          const response = await getUsersImagesTrigger(uniqueUsernames.toString());

          if (!response.data) {
            setErrorMessages([genericImagesErrorMessage]);
          } else {
            const fetchImage = async (profileImage: UserProfileImage) => {
              const downloadURL = profileImage.imageUrl ? await getDownloadURL(ref(storage, profileImage.imageUrl)) : noprofile;

              setAvatars((currentState) => currentState.set(profileImage.username, downloadURL));
            }

            response.data.map(userProfile => fetchImage(userProfile));
          }
        }
        if(uniqueUsernames.length !== 0) {
          fetchImages().catch(() => setErrorMessages([genericImagesErrorMessage]));
        }
      } else {
        setErrorMessages(data);
      }
    }
  }, [data]);

  useEffect(() => {
    errorMessages.forEach(message => toast({
      title: "Something went wrong",
      description: message,
      status: "error",
      duration: 9000,
      isClosable: true,
    }))
  }, [errorMessages]);

  return (
    <VStack
      as={Collapse}
      in={isOpen && canAnimate}
      width={width}
      height="fit-content"
      boxShadow="1px 2px 2px 1px rgba(0,0,0,0.25)"
      bgColor="primary.100"
      justifyContent="center"
      pb="16px"
      rounded="2xl"
      animateOpacity
      onAnimationComplete={(definion: any) =>
        setAnimationEnd(definion === "exit")
      }
    >
      <HStack w="inherit" justifyContent="space-between" px="16px">
        {userDetails && <NewProductComment productId={product.id} userDetails={userDetails} setIsCreate={setIsCreate}  />}
        {numberOfPages !== 0 && <ProductCommentSort sortingOrder={sortingOrder} setSortingOrder={setSortingOrder} />}
      </HStack>
        {selectedComments.map((comment, index) => (
          <ProductComment
            key={comment.id}
            avatarUrl={avatars.get(comment.username)}
            comment={comment}
            isLast={product.comments.length - 1 === index}
          />
        ))}
      {numberOfPages > 0 ? <PageChanger page={page} setPage={setPage} numberOfPages={numberOfPages} /> : 
      <Text textStyle="h2" fontSize={fontSize} color="primary.500" textAlign="start" px="16px">It appears that noone have placed a comment yet. Be first to start a discussion.</Text>}
    </VStack>
  );
};

export default ProductComments;
