import { PostResponseLikeDTO } from "./PostResponseLikeDTO";
import { TopicOutputDTO } from "./TopicOutputDTO";
import { UserDTO } from "./UserDTO";

export interface PostOutputDTO {
    id: number;
    title: string;
    content: string;
    creationTimestamp: string;
    modificationTimestamp: string;
    topic: TopicOutputDTO;
    user: UserDTO;
    isLiked: boolean;
    likes: PostResponseLikeDTO[];
    likesAmount: number;
    responsesAmount: number;
}