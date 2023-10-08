import { FormControl, FormLabel, Input } from "@chakra-ui/react";
import { normalTextFontSize } from "./ProfileEditSizes";

type FormEditInputProps = {
  label: string;
  placeholder: string;
  value: string;
  setValue: React.Dispatch<React.SetStateAction<string>>;
};

const FormEditInput = ({
  label,
  placeholder,
  value,
  setValue,
}: FormEditInputProps) => {
  return (
    <FormControl w="100%">
      <FormLabel
        fontSize={normalTextFontSize}
        textStyle="h1"
        fontWeight="semibold"
      >
        {label}
      </FormLabel>
      <Input
        fontSize={normalTextFontSize}
        textStyle="p"
        placeholder={placeholder}
        value={value}
        onChange={(event) => setValue(event.currentTarget.value)}
      />
    </FormControl>
  );
};

export default FormEditInput;
