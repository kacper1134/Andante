export interface CommentStatistics {
    username: string,
    commentsCount: number,
    upvoteCount: number,
};

export function isCommentStatistics(value: any): value is CommentStatistics {
    return "commentsCount" in value && "upvoteCount" in value;
}

export function isCommentStatisticsArray(value: any[]): value is CommentStatistics[] {
    return value.every(isCommentStatistics);
}