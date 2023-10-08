import { TopicSortingOrder } from "../../../store/api/forum-api-slice";
import ForumFilterAndSort from "../common/ForumFilterAndSort";

const options = [
  { value: TopicSortingOrder.NEWEST_FIRST, text: "Newest First" },
  { value: TopicSortingOrder.OLDEST_FIRST, text: "Oldest First" },
  { value: TopicSortingOrder.ALPHABETICAL, text: "Alphabetical: Ascending" },
  {
    value: TopicSortingOrder.REVERSE_ALPHABETICAL,
    text: "Alphabetical: Descending",
  },
];

type PostsFilterAndSortProps = {
  setSortingOrder: React.Dispatch<React.SetStateAction<TopicSortingOrder>>;
  setSearchPhrase: React.Dispatch<React.SetStateAction<string>>;
  setReload: React.Dispatch<React.SetStateAction<boolean>>;
};

const PostsFilterAndSort = ({
  setSortingOrder,
  setSearchPhrase,
  setReload,
}: PostsFilterAndSortProps) => {
  return (
    <ForumFilterAndSort
      options={options}
      placeholder="Search Posts"
      isMarginLeft={false}
      setSortingOrder={setSortingOrder}
      setSearchPhrase={setSearchPhrase}
      setReload={setReload}
    />
  );
};

export default PostsFilterAndSort;
