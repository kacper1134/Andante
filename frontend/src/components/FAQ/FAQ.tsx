import {
  VStack,
  Text,
  Tabs,
  TabList,
  Tab,
  TabPanels,
  TabPanel,
  useBreakpointValue,
} from "@chakra-ui/react";
import { useTranslation } from "react-i18next";
import QuestionsAndAnswers from "./QuestionsAndAnswers";

const shopQuestions = [
  "faqPage.questions.shop.first.title",
  "faqPage.questions.shop.second.title",
  "faqPage.questions.shop.third.title",
  "faqPage.questions.shop.fourth.title",
  "faqPage.questions.shop.fifth.title",
  "faqPage.questions.shop.sixth.title",
];
const shopAnswers = [
  "faqPage.questions.shop.first.content",
  "faqPage.questions.shop.second.content",
  "faqPage.questions.shop.third.content",
  "faqPage.questions.shop.fourth.content",
  "faqPage.questions.shop.fifth.content",
  "faqPage.questions.shop.sixth.content",
];

const forumQuestions = [
  "faqPage.questions.forum.first.title",
  "faqPage.questions.forum.second.title",
  "faqPage.questions.forum.third.title",
  "faqPage.questions.forum.fourth.title",
];
const forumAnswers = [
  "faqPage.questions.forum.first.content",
  "faqPage.questions.forum.second.content",
  "faqPage.questions.forum.third.content",
  "faqPage.questions.forum.fourth.content",
];

const blogQuestions = [
  "faqPage.questions.blog.first.title",
  "faqPage.questions.blog.second.title",
  "faqPage.questions.blog.third.title",
  "faqPage.questions.blog.fourth.title",
];
const blogAnswers = [
  "faqPage.questions.blog.first.content",
  "faqPage.questions.blog.second.content",
  "faqPage.questions.blog.third.content",
  "faqPage.questions.blog.fourth.content",
];

const FAQ = () => {
  const { t } = useTranslation();
  const headingSize = useBreakpointValue({
    base: "12px",
    md: "16px",
    lg: "20px",
    xl: "24px",
  })!;
  return (
    <VStack>
      <Text
        pt="8"
        color="primary.400"
        fontSize={{ base: "xl", sm: "2xl", md: "4xl" }}
        textStyle="h1"
        mb="1%"
      >
        {t("faqPage.content")}
      </Text>
      <Tabs isFitted variant="enclosed" w="100%">
        <TabList mb="1em">
          <Tab fontSize={headingSize}>{t("faqPage.questions.shop.title")}</Tab>
          <Tab fontSize={headingSize}>{t("faqPage.questions.forum.title")}</Tab>
          <Tab fontSize={headingSize}>{t("faqPage.questions.blog.title")}</Tab>
        </TabList>
        <TabPanels>
          <TabPanel>
            <QuestionsAndAnswers
              questions={shopQuestions}
              answers={shopAnswers}
            />
          </TabPanel>
          <TabPanel>
            <QuestionsAndAnswers
              questions={forumQuestions}
              answers={forumAnswers}
            />
          </TabPanel>
          <TabPanel>
            <QuestionsAndAnswers
              questions={blogQuestions}
              answers={blogAnswers}
            />
          </TabPanel>
        </TabPanels>
      </Tabs>
    </VStack>
  );
};

export default FAQ;
