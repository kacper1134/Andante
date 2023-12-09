import {
  AccordionButton,
  AccordionIcon,
  AccordionItem,
  AccordionPanel,
  Box,
  useBreakpointValue,
} from "@chakra-ui/react";
import { useTranslation } from "react-i18next";

export interface QuestionAndAnswerProps {
  question: string;
  answer: string;
}

const QuestionAndAnswer = ({ question, answer }: QuestionAndAnswerProps) => {
  const { t } = useTranslation();
  const headingSize = useBreakpointValue({
    base: "12px",
    md: "16px",
    lg: "20px",
    xl: "24px",
  })!;
  const contentSize = useBreakpointValue({
    base: "10px",
    md: "11px",
    lg: "14px",
    xl: "18px",
  })!;

  return (
    <AccordionItem w="100%" my="2%">
      <h2>
        <AccordionButton>
          <Box
            as="span"
            flex="1"
            textAlign="left"
            fontWeight="bold"
            color="primary.700"
            fontSize={headingSize}
          >
            {t(question)}
          </Box>
          <AccordionIcon />
        </AccordionButton>
      </h2>
      <AccordionPanel pb={4} bg="gray.50" fontSize={contentSize}>
        {t(answer)}
      </AccordionPanel>
    </AccordionItem>
  );
};

export default QuestionAndAnswer;
