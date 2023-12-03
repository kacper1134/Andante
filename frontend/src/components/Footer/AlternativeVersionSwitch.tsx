import { FormControl, FormLabel, Switch } from "@chakra-ui/react";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../store";
import { authActions } from "../../store/auth/auth-slice";
import { useTranslation } from "react-i18next";
const switchFontSize = {
  base: "xs",
  md: "md",
};

const AlternativeVersionSwitch = () => {
  const alternativeVersionOfInterface = useSelector(
    (state: RootState) => state.auth.alternativeVersionOfInterface
  );
  const dispatch = useDispatch();

  const changeAppVersion = () => {
    dispatch(authActions.changeInterfaceVersion());
  };
  const {t} = useTranslation();
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
        {t("alternativeVersion")}
      </FormLabel>
      <Switch
        id="email-alerts"
        isChecked={alternativeVersionOfInterface}
        onChange={changeAppVersion}
      />
    </FormControl>
  );
};

export default AlternativeVersionSwitch;
