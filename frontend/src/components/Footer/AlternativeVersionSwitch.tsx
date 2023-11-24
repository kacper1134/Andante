import { FormControl, FormLabel, Switch } from "@chakra-ui/react";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../store";
import { authActions } from "../../store/auth/auth-slice";
const switchFontSize = {
  base: "xs",
  md: "md",
};

const AlternativeVersionSwitch = () => {
  const isAuthenticated = useSelector(
    (state: RootState) => state.auth.alternativeVersionOfInterface
  );
  const dispatch = useDispatch();

  const changeAppVersion = () => {
    dispatch(authActions.changeInterfaceVersion());
  };

  return (
    <FormControl display="flex" alignItems="center" w="35%">
      <FormLabel
        htmlFor="email-alerts"
        mb="0"
        color="primary.500"
        fontWeight="semibold"
        fontSize={switchFontSize}
        textStyle="p"
      >
        Show alternative version?
      </FormLabel>
      <Switch
        id="email-alerts"
        isChecked={isAuthenticated}
        onChange={changeAppVersion}
      />
    </FormControl>
  );
};

export default AlternativeVersionSwitch;
