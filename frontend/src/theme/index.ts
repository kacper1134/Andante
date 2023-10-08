import { extendTheme } from "@chakra-ui/react";
import { colors } from "./colors/colors";
import textStyles from "./textStyles/textStyles";

const theme = extendTheme({
  colors: colors,
  textStyles: textStyles,
});

export default theme;
