export default {
  name: "comment",
  title: "Comment",
  type: "document",
  fields: [
    {
      name: "postTitle",
      title: "Post title",
      type: "string",
    },
    {
      name: "slug",
      title: "Slug",
      type: "string",
    },
    {
      name: "username",
      title: "Username",
      type: "string",
    },
    {
      name: "author",
      title: "Author",
      type: "string",
    },
    {
      name: "publishedAt",
      title: "Published at",
      type: "datetime",
    },
    {
      name: "content",
      title: "Content",
      type: "string",
    },
  ],
};
