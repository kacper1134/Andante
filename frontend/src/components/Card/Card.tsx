import { Box, ResponsiveValue } from '@chakra-ui/react';

export interface CardProps {
    width?: string,
    height?: string,
    mx?: string,
    my?: string,
    px?: string,
    py?: string,
    borderRadius?: string,
    children: JSX.Element | JSX.Element[],
};

const Card: React.FC<CardProps> = ( {width, height, borderRadius, children, px, mx, py, my} ) => {
    return (
        <Box minWidth={`calc(${width} - 2 * ${mx})`}
        position="relative"
        minHeight={height}
        px={px}
        mx={mx}
        py={py} 
        my={my}
        borderRadius={borderRadius}
        boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)">
            {children}
        </Box>
    );
}

Card.defaultProps = {
    borderRadius: '0px',
    mx: '0px',
    my: '0px',
    px: '0px',
    py: '0px',
};

export default Card;