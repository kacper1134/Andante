import { Textarea, TextareaProps } from "@chakra-ui/react";
import reactTextareaAutosize from "react-textarea-autosize";
import React from "react";

export const AutoResizeTextarea = React.forwardRef<
  HTMLTextAreaElement,
  TextareaProps
>((props, ref) => {
  return (
    <Textarea
      minH="unset"
      overflow="hidden"
      w="100%"
      resize="none"
      ref={ref}
      minRows={2}
      as={reactTextareaAutosize}
      {...props}
    />
  );
});