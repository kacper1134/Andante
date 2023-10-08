import { CommentOutputDTO } from "../../../../store/api/result/dto/product/CommentOutputDTO";

export interface CommentCardProps {
    comment: CommentOutputDTO,
}

function roundToThousands(value: number): string {
    if (value < 1000) {
        return "" + value;
    }

    return Math.round(value / 100) / 10 + "K";
}


const CommentCard: React.FC<CommentCardProps> = ({comment}) => {
    return <></>;
}

export default CommentCard;