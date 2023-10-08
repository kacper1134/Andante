import {
  Button,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  Text,
  useToast,
  Spinner,
} from "@chakra-ui/react";
import { SanityImageAssetDocument } from "@sanity/client";
import { DateTime } from "luxon";
import { useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { writeClient } from "../../../client";
import { RootState } from "../../../store";

type BlogCreateModalProps = {
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  newBlogPost: NewBlogPostType;
  mode: "create" | "edit";
};

export type NewBlogPostType = {
  id?: string;
  title: string;
  content: string;
  description: string;
  picture: File | null | undefined;
  inititalPicture: File | null | undefined;
  topic: string;
  inititalTitle: string;
};

const slugify = (slug: string) => {
  return slug
    .toLowerCase()
    .replace(/\s+/g, "-")
    .replace(/[&/\\#,+()$~%.'":*?<>{}]/g, "");
};

const BlogCreateModal = ({
  isOpen,
  setIsOpen,
  newBlogPost,
  mode,
}: BlogCreateModalProps) => {
  const toast = useToast();
  const navigate = useNavigate();
  const userData = useSelector((state: RootState) => state.auth.userDetails);
  const [saving, setSaving] = useState(false);

  const onSubmit = () => {
    if (!newBlogPost.picture || !newBlogPost.picture.type.match("image/*")) {
      showToast("Invalid data", "error", "File is not an image!");
      setIsOpen(false);
      return;
    }

    if (!newBlogPost.title || newBlogPost.title.length < 3) {
      showToast(
        "Invalid data",
        "error",
        "Length of post title must be greater than 3"
      );
      setIsOpen(false);
      return;
    }

    if (!newBlogPost.description) {
      showToast("Invalid data", "error", "Description can not be empty!");
      setIsOpen(false);
      return;
    }

    if (!newBlogPost.content) {
      showToast("Invalid data", "error", "Content can not be empty!");
      setIsOpen(false);
      return;
    }

    const blogWithTitle = `*[_type == "post" && title == "${newBlogPost.title}"]{
      _id,
      title,
      description,
      image,
      "imageUrl": image.asset->url,
      author,
      publishedAt,
      slug
    }`;
    setSaving(true);
    if (mode === "create") {
      writeClient.assets.upload("image", newBlogPost.picture!).then((data) => {
        createPost(blogWithTitle, data);
      });
    } else {
      updatePost(blogWithTitle);
    }
  };

  const createPost = (
    blogWithTitle: string,
    image: SanityImageAssetDocument
  ) => {
    const newBlogPostQuery = {
      _type: "post",
      category: newBlogPost.topic,
      image: {
        _type: "image",
        asset: {
          _type: "reference",
          _ref: image._id,
        },
      },
      title: newBlogPost.title,
      slug: {
        _type: "slug",
        current: slugify(newBlogPost.title),
      },
      body: newBlogPost.content,
      author: userData?.personal.name + " " + userData?.personal.surname,
      publishedAt: DateTime.now(),
      description: newBlogPost.description,
    };

    writeClient.fetch(blogWithTitle).then((data) => {
      if (data.length === 0) {
        writeClient.create(newBlogPostQuery).then(() => {
          navigate("..");
          showToast(
            "Create new post",
            "success",
            "New post was created successfully!"
          );
        });
      } else {
        setIsOpen(false);
        setSaving(false);
        showToast(
          "Invalid data",
          "error",
          "Blog post with given title already exist!"
        );
      }
    });
  };

  const updatePost = (blogWithTitle: string) => {
    const oldBlogPost = `*[_type == "post" && title == "${newBlogPost.inititalTitle}"]{
      _id,
      title,
      description,
      image,
      "imageUrl": image.asset->url,
      author,
      publishedAt,
      slug
    }`;

    const updateBlogPostQuery = {
      _id: newBlogPost.id,
      _type: "post",
      category: newBlogPost.topic,
      image: {
        _type: "image",
        asset: {
          _type: "reference",
          _ref: "",
        },
      },
      title: newBlogPost.title,
      slug: {
        _type: "slug",
        current: slugify(newBlogPost.title),
      },
      body: newBlogPost.content,
      author: userData?.personal.name + " " + userData?.personal.surname,
      publishedAt: DateTime.now(),
      description: newBlogPost.description,
    };

    writeClient.fetch(blogWithTitle).then((data) => {
      if (
        data.length === 0 ||
        newBlogPost.title === newBlogPost.inititalTitle
      ) {
        writeClient.fetch(oldBlogPost).then((oldBlog) => {
          if (newBlogPost.inititalPicture === newBlogPost.picture) {
            updateBlogPostQuery.image.asset._ref =
              oldBlog.at(0).image.asset._ref;
            sentUpdateQuery(updateBlogPostQuery);
          } else {
            writeClient.assets
              .upload("image", newBlogPost.picture!)
              .then((image) => {
                updateBlogPostQuery.image.asset._ref = image._id;
                sentUpdateQuery(
                  updateBlogPostQuery,
                  oldBlog.at(0).image.asset._ref
                );
              });
          }
        });
      } else {
        setIsOpen(false);
        setSaving(false);
        showToast(
          "Invalid data",
          "error",
          "Blog post with given title already exist!"
        );
      }
    });
  };

  const sentUpdateQuery = (updateBlogPostQuery: any, oldImageRef?: string) => {
    writeClient.createOrReplace(updateBlogPostQuery).then(() => {
      if (oldImageRef) writeClient.delete(oldImageRef!).catch(() => {});
      navigate("..");
      showToast(
        "Update post",
        "success",
        "Your post was updated successfully!"
      );
    });
  };

  const showToast = (
    title: string,
    status: "success" | "error",
    message: string
  ) => {
    toast({
      title: title,
      description: message,
      status: status,
      isClosable: true,
      duration: 2000,
    });
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={() => setIsOpen(false)}
      isCentered
      closeOnOverlayClick={false}
    >
      <ModalOverlay backdropFilter="blur(2px)" />
      <ModalContent textStyle="p">
        <ModalHeader>
          {mode === "create" ? "Create blog post" : "Update your post"}
        </ModalHeader>
        <ModalCloseButton disabled={saving} />
        <ModalBody>
          <Text>
            Are you sure you want to{" "}
            {mode === "create" ? "create new blog post?" : "update your post?"}
          </Text>
        </ModalBody>
        <ModalFooter>
          <Button
            colorScheme="primary"
            onClick={() => onSubmit()}
            mr={3}
            disabled={saving}
          >
            {!saving ? <Text>Confirm</Text> : <Spinner />}
          </Button>
          <Button
            colorScheme="gray"
            onClick={() => setIsOpen(false)}
            disabled={saving}
          >
            {!saving ? <Text>Cancel</Text> : <Spinner />}
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default BlogCreateModal;
