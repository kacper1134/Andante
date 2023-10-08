import { Page } from "../../Page";

export interface CommentOutputDTO {
    id: number,
    username: string,
    creationTimestamp: string,
    rating: number,
    title: string,
    content: string,
    productId: number,
    productName: string,
    observers: string[],
}

export function isCommentOutputDTO(value: any): value is CommentOutputDTO {
    return "username" in value && "title" in value;
}

export function isCommentOutputDTOArray(value: any[]): value is CommentOutputDTO[] {
    return value.every(isCommentOutputDTO);
}

export function isCommentOutputDTOPage(value: any): value is Page<CommentOutputDTO> {
    return "content" in value;
}