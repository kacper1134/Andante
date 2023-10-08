import { PostResponseLikeDTO } from "./PostResponseLikeDTO";
import { UserDTO } from "./UserDTO";

export interface PostResponseOutputDTO {
  id: number;
  content: string;
  creationTimestamp: string;
  modificationTimestamp: string;
  post: number;
  user: UserDTO;
  likes: PostResponseLikeDTO[];
  likesAmount: number;
}
