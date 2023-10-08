import {
  Collapse,
  FormControl,
  FormLabel,
  HStack,
  Icon,
  Input,
  VStack,
  Image,
  Text,
  Button,
} from "@chakra-ui/react";
import { useEffect, useRef, useState } from "react";
import { BiHide, BiShow } from "react-icons/bi";
import { FaSave } from "react-icons/fa";
import UserTextEditor from "../../common/UserTextEditor";
import BlogContentEditor from "./BlogContentEditor";
import { inputSize } from "./BlogCreatePageSizes";
import { RiImageAddFill } from "react-icons/ri";
import BlogCreateModal from "./BlogCreateModal";
import { readClient } from "../../../client";
import { useParams } from "react-router-dom";
import { AiOutlineClear } from "react-icons/ai";

type BlogContentFormProps = {
  topic: string;
  mode: "create" | "edit";
};

const BlogContentForm = ({ topic, mode }: BlogContentFormProps) => {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [picture, setPicture] = useState<File | null>();
  const [content, setContent] = useState("");
  const [showPreview, setShowPreview] = useState(false);
  const [isOpen, setIsOpen] = useState(false);
  const [inititalDescription, setInititalDescription] = useState("");
  const [inititalContent, setInititalContent] = useState("");
  const [inititalPicture, setInititalPicture] = useState<File | null>();

  const hiddenFileInput = useRef<HTMLInputElement>(null);
  const [post, setPost] = useState<any>();
  const { slug } = useParams();

  useEffect(() => {
    const query = `*[_type == "post" && slug.current == "${slug}"]{
        _id,
        title,
        description,
        image,
        body,
        "imageUrl": image.asset->url,
        author,
        publishedAt,
        slug
      }`;

    readClient
      .fetch(query)
      .then((data) => {
        if (data.length > 0) {
          const postData = data.at(0);
          setPost(postData);
          setTitle(postData.title);
          setDescription(postData.description);
          setContent(postData.body);
          setInititalDescription(postData.description);
          setInititalContent(postData.body);
          readClient.getDocument(postData.image.asset._ref).then((image) => {
            fetch(image?.url).then((res) => {
              res.blob().then((blob) => {
                const image = new File([blob], "image.png", {
                  type: blob.type,
                });
                setPicture(image);
                setInititalPicture(image);
              });
            });
          });
        }
      })
      .catch(console.log);
  }, [slug]);

  const clearHandler = () => {
    setTitle(post.title);
    setPicture(inititalPicture);
    setDescription(post.description);
    setContent(post.body);
  };

  return (
    <VStack my="5px" w="1200px" color="primary.600" spacing={3} textStyle="p">
      <FormControl>
        <FormLabel fontSize={inputSize} fontWeight="semibold">
          Title*
        </FormLabel>
        <Input
          name="title"
          value={title}
          id="title"
          placeholder="Enter a title"
          onChange={(e) => setTitle(e.target.value)}
        />
      </FormControl>
      <FormControl>
        <FormLabel fontSize={inputSize} fontWeight="semibold">
          Picture*
        </FormLabel>
        <Input
          name="picture"
          id="picture"
          type="file"
          accept="image/*"
          placeholder="Enter a picture url"
          onChange={(e) => setPicture(e.target.files?.item(0))}
          display="none"
          ref={hiddenFileInput}
        />
        <HStack>
          <Button
            onClick={() => {
              hiddenFileInput.current?.click();
            }}
            colorScheme="primary"
          >
            Upload a picture <Icon as={RiImageAddFill} ml="10px" />
          </Button>
          <Text>{picture?.name}</Text>
        </HStack>
      </FormControl>
      <FormControl>
        <FormLabel>
          <HStack>
            <Text fontSize={inputSize} fontWeight="semibold">
              Preview picture
            </Text>
            <Icon
              fontSize={inputSize}
              cursor="pointer"
              as={showPreview ? BiHide : BiShow}
              onClick={() => setShowPreview((prev) => !prev)}
            />
          </HStack>
        </FormLabel>
        <Collapse in={showPreview}>
          <Image
            boxSize="sm"
            objectFit="contain"
            src={picture ? URL.createObjectURL(picture) : ""}
          />
        </Collapse>
      </FormControl>
      <FormControl>
        <FormLabel fontSize={inputSize} fontWeight="semibold">
          Description*
        </FormLabel>
        <UserTextEditor
          setContent={setDescription}
          content={description}
          initialValue={inititalDescription}
        />
      </FormControl>
      <FormControl>
        <FormLabel fontSize={inputSize} fontWeight="semibold">
          Content*
        </FormLabel>
        <BlogContentEditor
          setContent={setContent}
          content={content}
          initialValue={inititalContent}
        />
        <Button
          w="fit-content"
          mt="15px"
          px="20px"
          colorScheme="primary"
          onClick={() => setIsOpen(true)}
        >
          <Icon as={FaSave} mr="10px" />
          <Text textStyle="p" fontWeight="semibold">
            Save
          </Text>
        </Button>
        <Button
          w="fit-content"
          mt="15px"
          ml="15px"
          px="20px"
          onClick={clearHandler}
        >
          <Icon as={AiOutlineClear} mr="10px" />
          <Text textStyle="p" fontWeight="semibold">
            Clear
          </Text>
        </Button>
      </FormControl>
      <BlogCreateModal
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        newBlogPost={{
          id: post?._id,
          title,
          description,
          picture,
          content,
          topic,
          inititalPicture,
          inititalTitle: post?.title,
        }}
        mode={mode}
      />
    </VStack>
  );
};

export default BlogContentForm;
