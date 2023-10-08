export interface TopicOutputDTO {
  id: number;
  name: string;
  imageUrl: string;
  parentTopicId: number;
  posts: number[];
  postAmount: number;
}
