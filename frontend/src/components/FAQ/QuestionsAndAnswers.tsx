import { Accordion, Spacer, VStack } from "@chakra-ui/react";
import QuestionAndAnswer from "./QuestionAndAnswer";

export interface QuestionsAndAnswersProps {
  questions: string[];
  answers: string[];
}
const QuestionsAndAnswers = ({
  questions,
  answers,
}: QuestionsAndAnswersProps) => {
  return (
    <VStack>
    <Spacer />
      <Accordion allowToggle w="70%">
        {questions.map((question, index) => {
          return (
            <QuestionAndAnswer
              key={index}
              question={question}
              answer={answers[index]}
            />
          );
        })}
      </Accordion>
    </VStack>
  );
};

export default QuestionsAndAnswers;
