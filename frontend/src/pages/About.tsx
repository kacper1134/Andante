import SlideShow, { Slide } from "../components/About/SlideShow/SlideShow";
import Offer from "../components/About/Offer/Offer";
import Statistics from "../components/About/Statistics/Statistics";
import { AspectRatio } from '@chakra-ui/react';

const HOME_SLIDE_DISPLAY_TIME = 45000;
const HOME_FILTER="blur(0.75px)";

const slides: Slide[] = [
    {
        URL: 'https://images.unsplash.com/photo-1656152323648-2c5104d1aad7?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80',
        content: {
            title: 'aboutPage.slide.first.title',
            content: 'aboutPage.slide.first.content',
        }
    },
    {
        URL: 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80',
        content: {
            title: 'aboutPage.slide.second.title',
            content: 'aboutPage.slide.second.content',
        }
    },
    {
        URL: 'https://images.unsplash.com/photo-1666550307380-851a561fe580?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80',
        content: {
            title: 'aboutPage.slide.third.title',
            content: 'aboutPage.slide.third.content',
        }
    },
    {
        URL: 'https://images.unsplash.com/photo-1534350939076-1b8e3520b4ae?q=80&w=2652&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        content: {
            title: 'aboutPage.slide.fourth.title',
            content: 'aboutPage.slide.fourth.content',
        }
    }
]

const Home: React.FC<{}> = () => {
    const responsiveRatio = {base: 4/3, md: 16/9}
    return (
    <>
        <AspectRatio ratio = {responsiveRatio}>   
            <SlideShow slides={slides} displayTime={HOME_SLIDE_DISPLAY_TIME} filter={HOME_FILTER} color='white' />
        </AspectRatio>
        <Offer />
        <Statistics /> 
    </>     
    );    
}

export default Home;