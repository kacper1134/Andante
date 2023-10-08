export interface CommentInputDTO {
    id?: number,
    username: string,
    rating: number,
    title: string,
    content: string,
    productId: number,
    observers: string[]
};