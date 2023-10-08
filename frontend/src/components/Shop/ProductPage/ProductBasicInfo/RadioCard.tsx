import { Box, Input, useRadio } from "@chakra-ui/react";

export interface RadioCardProps {
    children: React.ReactNode,
    name?: string,
    onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void,
}

const RadioCard: React.FC<RadioCardProps> = (props) => {
    const { getInputProps, getCheckboxProps } = useRadio(props);

    const input = getInputProps();
    const checkbox = getCheckboxProps();

    return (
        <Box as='label'>
            <Input {...input}/>
            <Box {...checkbox} cursor='pointer' borderWidth='1px' p="4px"
                textStyle="h3"
                boxShadow='md'
                userSelect="none"
                fontSize={{base: "10px", sm: "12px", md: "14px", lg: "16px"}} 
                _checked={{bg: 'white',color: 'primary.300', borderColor: 'primary.300'}}>
                {props.children}
            </Box>
        </Box>
    )
}

export default RadioCard;