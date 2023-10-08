import { useState, useEffect } from 'react';

const useWindowDimensions = () => {
    const [ windowDimensions, setWindowDimensions ] = useState(getWindowDimensions());

    useEffect(() => {
        function handleResize() {
            setWindowDimensions(getWindowDimensions());
        }

        window.addEventListener('resize', handleResize);

        return () => window.removeEventListener('resize', handleResize);
    }, [])

    return windowDimensions;
}

const getWindowDimensions = () => {
    const {innerWidth: width, innerHeight: height} = window;

    return {
        width,
        height
    };
}

export default useWindowDimensions;