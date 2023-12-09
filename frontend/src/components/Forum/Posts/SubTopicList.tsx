import { SlideType } from "../../Carousel/Slide";
import { Button, SimpleGrid, VStack, Text, InputGroup, InputLeftElement, Input, Box } from "@chakra-ui/react";
import Topic from "../common/Topic";
import { useEffect, useState } from "react";
import { AnimatePresence } from "framer-motion";
import LazyLoadingList from "../common/LazyLoadingList";
import { buttonTextFontSize } from "../common/ForumDimensions";
import React from "react";
import { useLazyGetSubtopicsQuery } from "../../../store/api/forum-api-slice";
import { useKeycloak } from "@react-keycloak/web";
import { TopicOutputDTO } from "../../../store/api/result/dto/forum/TopicOutputDTO";
import { useParams } from "react-router-dom";
import { SearchIcon } from "@chakra-ui/icons";
import { useTranslation } from "react-i18next";


const PAGE_AMOUNT = 4;

const SubTopicList = React.forwardRef<HTMLDivElement>((_, ref) => {
    const [currentPage, setCurrentPage] = useState(0);
    const [numberOfTopics, setNumberOfTopics] = useState(0);
    const [isBottom, setIsBottom] = useState(false);
    const [searchPhrase, setSearchPhrase] = useState("");
    const { id } = useParams();
    const [fetchSubtopics] = useLazyGetSubtopicsQuery();
    const { keycloak, initialized } = useKeycloak();
    const [topics, setTopics] = useState<SlideType[]>([]);
    const [renderList, setRenderList] = useState(false);

    const fetchMoreData = (
        setIsBottom: React.Dispatch<React.SetStateAction<boolean>>
    ) => {
        if (id !== undefined)
            fetchSubtopics({ topicId: +id }).then((result) => {
                if (result.data) {
                    const subtopics = (result.data as TopicOutputDTO[]).map(
                        (subtopic) => {
                            return {
                                id: subtopic.id,
                                text: subtopic.name,
                                image: subtopic.imageUrl,
                                path: "../topic",
                            };
                        }
                    );
                    setTopics(subtopics);
                    setRenderList(true);
                }
            });

        setCurrentPage((prevCurrentPage) => prevCurrentPage + 1);
        setIsBottom(true);
    };

    const [showMore, setShowMore] = useState(false);

    useEffect(() => {
        if (initialized && keycloak.authenticated && id !== undefined) {
            fetchSubtopics({ topicId: +id }).then((result) => {
                if (result.data) {
                    const subtopics = (result.data as TopicOutputDTO[]).map(
                        (subtopic) => {
                            return {
                                id: subtopic.id,
                                text: subtopic.name,
                                image: subtopic.imageUrl,
                                path: "../topic",
                            };
                        }
                    );
                    setTopics(subtopics);
                    setNumberOfTopics(subtopics.length)
                    setTopics(subtopics.slice(0, 4))
                }
            });
        }
    }, [fetchSubtopics, id, initialized, keycloak.authenticated]);

    const filteredTopics = topics.filter((topic) =>
        topic.text.toLowerCase().includes(searchPhrase.toLowerCase())
    );

    const { t } = useTranslation();

    return (
        <VStack ref={ref} width="fit-content">
            <Box
                w={"100%"}>
                <InputGroup
                    w={{ base: "50%", sm: "45%", md: "40%", lg: "35%", xl: "30%" }}
                >
                    <InputLeftElement
                        pointerEvents="none"
                        children={<SearchIcon color="primary.200" />}
                    />
                    <Input
                        fontSize={buttonTextFontSize}
                        value={searchPhrase}
                        variant="outline"
                        textStyle="p"
                        placeholder="Search topics..."
                        onChange={(event) => {
                            setSearchPhrase(event.target.value);
                            fetchMoreData(setIsBottom);
                        }}
                        _placeholder={{ color: "primary.200" }}
                    />
                </InputGroup>
            </Box>
            <LazyLoadingList
                list={topics}
                showMore={showMore}
                maximumListLength={numberOfTopics}
                fetchMoreData={fetchMoreData}
                isBottom={isBottom}
                setIsBottom={setIsBottom}
            >
                <SimpleGrid
                    columns={{
                        base: 2,
                        md: 3,
                        xl: 4,
                    }}
                    spacing={4}
                >
                    {searchPhrase === "" ?
                        topics.map((topic, index) => (
                            <AnimatePresence key={index}>
                                <Topic topic={topic} />
                            </AnimatePresence>
                        )) :
                        filteredTopics.map((topic, index) => (
                            <AnimatePresence key={index}>
                                <Topic topic={topic} />
                            </AnimatePresence>
                        ))
                    }
                </SimpleGrid>
            </LazyLoadingList>

            {!showMore && topics.length != numberOfTopics && (
                <VStack width="100%">
                    <Text fontSize={buttonTextFontSize} fontWeight="bold" textStyle="p">
                        {t("forum-section.showing")} {topics.length} {t("forum-section.showing-of")} {numberOfTopics}
                    </Text>
                    <Button
                        fontSize={buttonTextFontSize}
                        height="fit-content"
                        py="8px"
                        colorScheme="purple"
                        fontWeight="bold"
                        px="60px"
                        textStyle="p"
                        onClick={() => { setShowMore(true); fetchMoreData(setIsBottom) }}
                    >
                        {t("forum-section.show-more")}
                    </Button>
                </VStack>
            )}
        </VStack>
    );
});

export default SubTopicList;
