import { Dispatch, Fragment, SetStateAction } from "react";
import SlideButton from "./SlideButton";

export interface SlideButtonsProps {
  noOfSlides: number,
  current: number,
  setCurrent: Dispatch<SetStateAction<number>>,
}

const SlideButtons: React.FC<SlideButtonsProps> = ({noOfSlides, current, setCurrent}) => {
  return (
    <Fragment>
      {[...Array(noOfSlides).keys()].map((value) => (
        <SlideButton
          id={value}
          key={value}
          current={current}
          setCurrent={setCurrent}
          color="white"
          activeColor="primary.500"
        />
      ))}
    </Fragment>
  );
};

export default SlideButtons;
