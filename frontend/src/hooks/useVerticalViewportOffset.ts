import React, { useEffect, useState } from "react";
import { useDimensions } from '@chakra-ui/react';
import useWindowDimensions from "./useWindowDimensions";
import Utils from "../utils/Utils";

export interface ClosedRange {
    from: number,
    to: number
};

function useVerticalViewportOffset(ref: React.RefObject<HTMLElement>, offsetRange: ClosedRange) {
    const [offset, setOffset] = useState(0);
    const dimensions = useDimensions(ref, true);
    const viewport = useWindowDimensions();

    useEffect(() => {
        if (dimensions?.borderBox.top! < viewport.height &&
            dimensions?.borderBox.bottom! > 0) {
            setOffset(Utils.rescale(dimensions?.borderBox.top!, viewport.height, -dimensions?.borderBox.height!, offsetRange.from, offsetRange.to));
        }
    }, [dimensions, viewport]);

    return offset;
};

export default useVerticalViewportOffset;