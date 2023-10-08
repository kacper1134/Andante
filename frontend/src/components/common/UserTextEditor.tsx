import { Box } from "@chakra-ui/react";
import { Editor } from "@tinymce/tinymce-react";
import { useRef } from "react";

type UserTextEditorProps = {
  initialValue?: string;
  setContent: React.Dispatch<React.SetStateAction<string>>;
  content?: string;
  padding?: string;
};

const UserTextEditor = ({
  initialValue,
  padding,
  setContent,
  content,
}: UserTextEditorProps) => {
  const editorRef = useRef<any>(null);

  return (
    <Box w="100%" px={padding}>
      <Editor
        apiKey="2ee56e9oiyjbmqzp7wz21w7x64dta3hy0d9ibg7mtpyru6ar"
        onInit={(evt, editor) => (editorRef.current = editor)}
        initialValue={initialValue}
        value={content}
        onEditorChange={(content) => setContent(content)}
        init={{
          menubar: false,
          plugins: [
            "image",
            "media",
            "wordcount",
            "fullscreen",
            "emoticons",
            "autoresize",
            "lists"
          ],
          toolbar:
            "undo redo | formatselect | " +
            "bold italic |numlist bullist| | alignleft aligncenter " +
            "alignright alignjustify | emoticons |" +
            "removeformat",
          content_style: `body { font-family:Helvetica,Arial,sans-serif}`,
          content_css: "/UserTextEditor.css",
        }}
      />
    </Box>
  );
};

export default UserTextEditor;
