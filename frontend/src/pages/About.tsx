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
            title: 'Localizations',
            content: 'We are present all around the world',
        }
    },
    {
        URL: 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80',
        content: {
            title: 'International collaboration',
            content: 'All decisions and business activities we make are characterized by honesty towards our employees, associates and contractors',
        }
    },
    {
        URL: 'https://images.unsplash.com/photo-1666550307380-851a561fe580?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80',
        content: {
            title: 'Advanced technology',
            content: 'Technological peace of mind',
        }
    },
    {
        URL: 'https://www.globalization-partners.com/wp-content/uploads/2020/07/highrisecitybuildings.jpg',
        content: {
            title: 'Main office',
            content: 'Main headquarters of our company',
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