export interface UserDTO {
    email: string;
    name: string;
    surname: string;
    username: string;
    posts: number[];
    responses: number[];
    likedPosts: number[];
    likedResponses: number[];
}