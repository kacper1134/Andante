import { TopicSortingOrder } from "../../../store/api/forum-api-slice";
import ForumFilterAndSort, { OptionType } from "../common/ForumFilterAndSort";

const options: OptionType[] = [
  { value: TopicSortingOrder.NEWEST_FIRST, text: "Newest First" },
  { value: TopicSortingOrder.OLDEST_FIRST, text: "Oldest First" },
  { value: TopicSortingOrder.ALPHABETICAL, text: "Alphabetical: Ascending" },
  {
    value: TopicSortingOrder.REVERSE_ALPHABETICAL,
    text: "Alphabetical: Descending",
  },
];

type AllTopicsFilterAndSortProps = {
  setSortingOrder: React.Dispatch<React.SetStateAction<TopicSortingOrder>>;
  setSearchPhrase: React.Dispatch<React.SetStateAction<string>>;
  disableSortFilter?: boolean;
};

const AllTopicsFilterAndSort = ({
  setSortingOrder,
  setSearchPhrase,
  disableSortFilter,
}: AllTopicsFilterAndSortProps) => {
  return (
    <ForumFilterAndSort
      options={options}
      placeholder="Search Topics"
      setSortingOrder={setSortingOrder}
      setSearchPhrase={setSearchPhrase}
      disableSortFilter={disableSortFilter}
      isMarginLeft
    />
  );
};

export default AllTopicsFilterAndSort;
