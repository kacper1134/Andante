import { Editor } from "@tinymce/tinymce-react";
import { useRef } from "react";

type BlogContentEditorProps = {
  setContent: React.Dispatch<React.SetStateAction<string>>;
  initialValue: string;
  content: string;
};

const BlogContentEditor = ({
  setContent,
  initialValue,
  content,
}: BlogContentEditorProps) => {
  const editorRef = useRef<any>(null);

  return (
    <>
      <Editor
        apiKey="2ee56e9oiyjbmqzp7wz21w7x64dta3hy0d9ibg7mtpyru6ar"
        onInit={(evt, editor) => (editorRef.current = editor)}
        initialValue={initialValue}
        onEditorChange={(value) => {
          setContent(value);
        }}
        value={content}
        init={{
          menubar: true,
          plugins: [
            "image",
            "media",
            "wordcount",
            "preview",
            "searchreplace",
            "fullscreen",
            "link",
            "code",
            "emoticons",
            "quickbars",
            "autoresize",
            "anchor",
          ],
          content_style:
            "body { font-family:Helvetica,Arial,sans-serif; font-size:18px }",
        }}
      />
    </>
  );
};

export default BlogContentEditor;
