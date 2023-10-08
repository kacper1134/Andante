import { Button } from "@chakra-ui/react";
import { useDispatch, useSelector } from "react-redux";
import { imageSliderActions } from "../../../store/image-slider/image-slider";
import { Dispatch, SetStateAction } from "react";
import { RootState } from "../../../store";

type SlideButtonProps = {
  id: number,
  color: string,
  activeColor: string,
  current: number,
  setCurrent: Dispatch<SetStateAction<number>>,
};

const SlideButton = ({ id, color, activeColor, current, setCurrent }: SlideButtonProps) => {
  const active = current === id;
  const dispatch = useDispatch();
  const isAnimating = useSelector((state: RootState) => state.imageSlider.isAnimation);
  const bgColor = active ? activeColor : color;
  const border = active ? "1px solid black" : "none";
  const hoverStyles = active
    ? { color: activeColor, cursor: "auto" }
    : { opacity: 0.8 };
  const activeStyles = active ? { color: activeColor } : { opacity: 0.7 };

  const clickHandler = () => {
    if (!active) {
      setCurrent(id);
      dispatch(imageSliderActions.startAnimation());
    }
  };

  return (
    <Button
      rounded="full"
      variant="solid"
      border={border}
      bgColor={bgColor}
      size="xs"
      _hover={hoverStyles}
      _active={activeStyles}
      onClick={clickHandler}
      disabled={isAnimating}
    ></Button>
  );
};

export default SlideButton;
