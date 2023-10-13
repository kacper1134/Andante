import { useEffect, useState } from "react";
import { State } from "../components/Shop/MainPage/FilterMenu/ProductStateFilter";
import { ProductFeatureType } from "../components/Shop/ProductPage/ProductBasicInfo/ProductTypes";
import { VariantsGroup } from "../components/Shop/ProductPage/ProductVariants/ProductsVariantsMenu";
import { AmplifiersOutputDTO } from "../store/api/result/dto/product/base/AmplifiersOutputDTO";
import { GramophonesOutputDTO } from "../store/api/result/dto/product/base/GramophonesOutputDTO";
import { HeadphonesOutputDTO } from "../store/api/result/dto/product/base/HeadphonesOutputDTO";
import { MicrophonesOutputDTO } from "../store/api/result/dto/product/base/MicrophonesOutputDTO";
import { ProductOutputDTO, ProductType } from "../store/api/result/dto/product/base/ProductOutputDTO";
import { SpeakersOutputDTO } from "../store/api/result/dto/product/base/SpeakersOutputDTO";
import { SubwoofersOutputDTO } from "../store/api/result/dto/product/base/SubwoofersOutputDTO";
import { AmplifiersVariantOutputDTO, isAmplifiersVariantArray } from "../store/api/result/dto/product/variant/AmplifiersVariantOutputDTO";
import { GramophonesVariantOutputDTO, isGramophonesVariantOutputDTOArray } from "../store/api/result/dto/product/variant/GramophonesVariantOutputDTO";
import { HeadphonesVariantOutputDTO, isHeadphonesVariantOutputDTOArray } from "../store/api/result/dto/product/variant/HeadphonesVariantOutputDTO";
import { isMicrophoneVariantOutputDTOArray, MicrophonesVariantOutputDTO } from "../store/api/result/dto/product/variant/MicrophonesVariantOutputDTO";
import { ProductVariantOutputDTO } from "../store/api/result/dto/product/variant/ProductVariantOutputDTO";
import { isSpeakersVariantOutputDTO, isSpeakersVariantOutputDTOArray, SpeakersVariantOutputDTO } from "../store/api/result/dto/product/variant/SpeakersVariantOutputDTO";
import { isSubwoofersVariantOutputDTOArray, SubwoofersVariantOutputDTO } from "../store/api/result/dto/product/variant/SubwoofersVariantOutputDTO";
import { SelectedCategory } from "../store/inner/innerSlice";

class Utils {
    static range(from: number, to: number): number[] {
        if (from > to) return [];
        else if(from === to) return [from];
        else return [from, ...Utils.range(from + 1, to)];
    }
    static rescale(value: number, min: number, max: number, targetMin: number, targetMax: number) {
        return targetMin + ((value - min) / (max - min)) * (targetMax - targetMin);
    }
}

export interface VariantSelectorResults {
    variantGroups: VariantsGroup[],
    selectedVariant?: ProductVariantOutputDTO,
}

function isAmplifiers(product: ProductOutputDTO): product is AmplifiersOutputDTO {
    return product.productType === ProductType.AMPLIFIERS;
}

function isAmplifiersVariant(variant: ProductVariantOutputDTO): variant is AmplifiersVariantOutputDTO {
    return "amplifiersId" in variant;
}

function isGramophones(product: ProductOutputDTO): product is GramophonesOutputDTO {
    return product.productType === ProductType.GRAMOPHONES;
}

function isGramophonesVariant(variant: ProductVariantOutputDTO): variant is GramophonesVariantOutputDTO {
    return "gramophonesId" in variant;
}

function isHeadphones(product: ProductOutputDTO): product is HeadphonesOutputDTO {
    return product.productType === ProductType.HEADPHONES;
}

function isHeadphonesVariant(variant: ProductVariantOutputDTO): variant is HeadphonesVariantOutputDTO {
    return "headphonesId" in variant;
}

function isMicrophones(product: ProductOutputDTO): product is MicrophonesOutputDTO {
    return product.productType === ProductType.MICROPHONES;
}

function isMicrophonesVariant(variant: ProductVariantOutputDTO): variant is MicrophonesVariantOutputDTO {
    return "microphoneId" in variant;
}

function isSpeakers(product: ProductOutputDTO): product is SpeakersOutputDTO {
    return product.productType === ProductType.SPEAKERS;
}

function isSpeakersVariant(variant: ProductVariantOutputDTO): variant is SpeakersVariantOutputDTO {
    return "speakersId" in variant;
}

function isSubwoofers(product: ProductOutputDTO): product is SubwoofersOutputDTO {
    return product.productType === ProductType.SUBWOOFERS;
}

function isSubwoofersVariant(variant: ProductVariantOutputDTO): variant is SubwoofersVariantOutputDTO {
    return "subwoofersId" in variant;
}

function extractBaseFeatures(product: ProductOutputDTO): ProductFeatureType[] {
    let productFeatures: ProductFeatureType[] = [];

    productFeatures.push({name: "Producer", value: product.producer.name});
    productFeatures.push({name: "Weight", value: roundWeight(product.weight)});
    productFeatures.push({name: "Frequency", value: product.minimumFrequency + " - " + product.maximumFrequency + " Hz"});

    return productFeatures;
}

function extractTypeSpecificFeatures(product: ProductOutputDTO): ProductFeatureType[] {
    if (isAmplifiers(product)) {
        return extractAmplifiersFeatures(product);
    } else if(isGramophones(product)) {
        return extractGramophonesFeatures(product);
    } else if(isHeadphones(product)) {
        return extractHeadphonesFeatures(product);
    } else if(isMicrophones(product)) {
        return extractMicrophonesFeatures(product);
    } else if (isSpeakers(product)) {
        return extractSpeakersFeatures(product);
    } else if (isSubwoofers(product)) {
        return extractSubwoofersFeatures(product);
    } else {
        return [];
    }
}

function extractTypeSpecificVariantFeatures(variant: ProductVariantOutputDTO): ProductFeatureType[] {
    if (isAmplifiersVariant(variant)) {
        return extractAmplifiersVariantFeatures(variant);
    } else if (isGramophonesVariant(variant)) {
        return extractGramophonesVariantFeatures(variant);
    } else if (isHeadphonesVariant(variant)) {
        return extractHeadphonesVariantFeatures(variant);
    } else if (isMicrophonesVariant(variant)) {
        return extractMicrophonesVariantFeatures(variant);
    } else if (isSpeakersVariant(variant)) {
        return extractSpeakersVariantFeatures(variant);
    } else if (isSubwoofersVariant(variant)) {
        return extractSubwoofersVariantFeatures(variant)
    } else {
        return [];
    }
}

function roundWeight(weight: number) {
    if (weight < 1000) {
        return weight + "g";
    }

    return Math.round(weight / 100) / 10 + "kg";
}

function extractAmplifiersFeatures(amplifiers: AmplifiersOutputDTO): ProductFeatureType[] {
    let amplifiersFeatures: ProductFeatureType[] = [];

    amplifiersFeatures.push({name: "Power", value: amplifiers.power + " W"});
    amplifiersFeatures.push({name: "Type", value: amplifiers.amplifierType});

    return amplifiersFeatures;
}

function extractAmplifiersVariantFeatures(amplifiersVariant: AmplifiersVariantOutputDTO): ProductFeatureType[] {
    let variantFeatures: ProductFeatureType[] = [];

    variantFeatures.push({name: "Color", value: amplifiersVariant.color});

    return variantFeatures;
}

function useAmplifiersVariantGroup(product: ProductOutputDTO): VariantSelectorResults {
    const [selectedColor, setSelectedColor] = useState<string>("");

    useEffect(() => {
        if (product.variants.length > 0 && isAmplifiersVariantArray(product.variants)) {
            setSelectedColor(product.variants[0].color);
        }
    }, [product]);

    if (isAmplifiersVariantArray(product.variants)) {
        const allColors = product.variants.map(variant => variant.color).filter((variant, index, array) => array.indexOf(variant) === index);

        const colorVariantGroup: VariantsGroup = {
            name: "Color",
            options: allColors,
            selectedOption: selectedColor,
            setSelectedOption: setSelectedColor,
        };

        const selectedVariant = product.variants.find(variant => variant.color === selectedColor);

        return {variantGroups: [colorVariantGroup], selectedVariant: selectedVariant};
    }

    return {variantGroups: []};
}

function extractGramophonesFeatures(gramophones: GramophonesOutputDTO): ProductFeatureType[] {
    let gramophonesFeatures: ProductFeatureType[] = [];

    gramophonesFeatures.push({name: "Connectivity technology", value: gramophones.connectivityTechnology});
    gramophonesFeatures.push({name: "Turntable material", value: gramophones.turntableMaterial});
    gramophonesFeatures.push({name: "Motor type", value: gramophones.motorType});
    gramophonesFeatures.push({name: "Power source", value: gramophones.powerSource});
    gramophonesFeatures.push({name: "Rotational speed", value: gramophones.maximumRotationalSpeed + " rpm"});

    return gramophonesFeatures;
}

function useGramophonesVariantGroup(product: ProductOutputDTO): VariantSelectorResults {
    const [selectedColor, setSelectedColor] = useState<string>("");    

    useEffect(() => {
        if (product.variants.length > 0 && isGramophonesVariantOutputDTOArray(product.variants)) {
            setSelectedColor(product.variants[0].color);
        }
    }, [product]);
    if (isGramophonesVariantOutputDTOArray(product.variants)) {
        const allColors = product.variants.map(variant => variant.color)
            .filter((variant, index, array) => array.indexOf(variant) === index);

        const colorVariantGroup: VariantsGroup = {
            name: "Color",
            options: allColors,
            selectedOption: selectedColor,
            setSelectedOption: setSelectedColor,
        };

        const selectedVariant = product.variants.find(variant => variant.color === selectedColor);

        return {variantGroups: [colorVariantGroup], selectedVariant: selectedVariant}
    }

    return {variantGroups: []};
}

function extractGramophonesVariantFeatures(gramophonesVariant: GramophonesVariantOutputDTO): ProductFeatureType[] {
    let variantFeatures: ProductFeatureType[] = [];

    variantFeatures.push({name: "Color", value: gramophonesVariant.color});

    return variantFeatures;
}

function extractHeadphonesFeatures(headphones: HeadphonesOutputDTO): ProductFeatureType[] {
    let headphonesFeatures: ProductFeatureType[] = [];

    headphonesFeatures.push({name: "Construction type", value: headphones.constructionType});
    headphonesFeatures.push({name: "Driver type", value: headphones.driverType});
    headphonesFeatures.push({name: "Wireless", value: headphones.wireless ? "Yes" : "No"});
    headphonesFeatures.push({name: "Bluetooth standard", value: headphones.bluetoothStandard ? "" + headphones.bluetoothStandard : "Does not apply"});

    return headphonesFeatures;
}

function extractHeadphonesVariantFeatures(headphonesVariant: HeadphonesVariantOutputDTO): ProductFeatureType[] {
    let variantFeatures: ProductFeatureType[] = [];

    variantFeatures.push({name: "Nominal impedance", value: headphonesVariant.nominalImpedance + " Ohm"});
    variantFeatures.push({name: "Loudness", value: headphonesVariant.loudness + " db"});
    variantFeatures.push({name: "Color", value: headphonesVariant.color});

    return variantFeatures;
}

function useHeadphonesVariantGroup(product: ProductOutputDTO): VariantSelectorResults {
    const [nominalImpedance, setNominalImpedance] = useState<string>("");
    const [loudness, setLoudness] = useState<string>("");
    const [color, setColor] = useState<string>("");

    useEffect(() => {
        if (product.variants.length > 0 && isHeadphonesVariantOutputDTOArray(product.variants)) {
            setNominalImpedance(product.variants[0].nominalImpedance + "");
            setLoudness(product.variants[0].loudness + "");
            setColor(product.variants[0].color);
        }
    }, [product]);

    if (isHeadphonesVariantOutputDTOArray(product.variants)) {
        const allImpedances = product.variants.map(variant => variant.nominalImpedance + "")
            .filter((variant, index, array) => array.indexOf(variant) === index);
        const allLoudness = product.variants.map(variant => variant.loudness + "")
            .filter((variant, index, array) => array.indexOf(variant) === index);
        const allColors = product.variants.map(variant => variant.color)
            .filter((variant, index, array) => array.indexOf(variant) === index);

        const impedancesVariantGroup: VariantsGroup = {
            name: "Impedance",
            options: allImpedances,
            selectedOption: nominalImpedance,
            setSelectedOption: (selectedOption: string) => {
                const selectedVariant = product.variants.find(variant => isHeadphonesVariant(variant) && variant.nominalImpedance + "" === selectedOption) as HeadphonesVariantOutputDTO;

                setNominalImpedance(selectedOption);
                setLoudness(selectedVariant.loudness + "");
                setColor(selectedVariant.color);
            },
        };
        
        const loudnessVariantGroup: VariantsGroup = {
            name: "Loudness",
            options: allLoudness,
            selectedOption: loudness,
            setSelectedOption: (selectedOption: string) => {
                const selectedVariant = product.variants.find(variant => isHeadphonesVariant(variant) && variant.loudness + "" === selectedOption) as HeadphonesVariantOutputDTO;

                setNominalImpedance(selectedVariant.nominalImpedance + "");
                setLoudness(selectedVariant.loudness + "");
                setColor(selectedVariant.color);
            },
        };

        const colorVariantGroup: VariantsGroup = {
            name: "Color",
            options: allColors,
            selectedOption: color,
            setSelectedOption: (selectedOption: string) => {
                const selectedVariant = product.variants.find(variant => isHeadphonesVariant(variant) && variant.color === selectedOption) as HeadphonesVariantOutputDTO;

                setNominalImpedance(selectedVariant.nominalImpedance + "");
                setLoudness(selectedVariant.loudness + "");
                setColor(selectedVariant.color);
            },
        };
        

        const selectedVariant = product.variants.find(variant => variant.color === color && variant.nominalImpedance + "" === nominalImpedance && variant.loudness + "" === loudness)

        return {variantGroups: [impedancesVariantGroup, loudnessVariantGroup, colorVariantGroup], selectedVariant: selectedVariant};
    }

    return {variantGroups: []}; 
}

function extractMicrophonesFeatures(microphone: MicrophonesOutputDTO): ProductFeatureType[] {
    let microphoneFeatures: ProductFeatureType[] = [];

    microphoneFeatures.push({name: "Wireless", value: microphone.wireless ? "Yes" : "No"});
    microphoneFeatures.push({name: "Bluetooth standard", value: microphone.bluetoothStandard ? "" + microphone.bluetoothStandard : "Does not apply"});
    microphoneFeatures.push({name: "Type", value: microphone.type});

    return microphoneFeatures;
}

function extractMicrophonesVariantFeatures(microphoneVariant: MicrophonesVariantOutputDTO): ProductFeatureType[] {
    let variantFeatures: ProductFeatureType[] = [];

    variantFeatures.push({name: "Color", value: microphoneVariant.color});

    return variantFeatures;
}

function useMicrophonesVariantsGroup(product: ProductOutputDTO): VariantSelectorResults {
    const [color, setColor] = useState<string>("");

    useEffect(() => {
        if (product.variants.length > 0 && isMicrophoneVariantOutputDTOArray(product.variants)) {
            setColor(product.variants[0].color);
        }
    }, [product]);

    if (isMicrophoneVariantOutputDTOArray(product.variants)) {
        const allColors = product.variants.map(variant => variant.color)
            .filter((variant, index, array) => array.indexOf(variant) === index);
        
        const colorVariantsGroup: VariantsGroup = {
            name: "Color",
            options: allColors,
            selectedOption: color,
            setSelectedOption: setColor,
        };

        const selectedVariant = product.variants.find(variant => variant.color === color);

        return {variantGroups: [colorVariantsGroup], selectedVariant: selectedVariant};
    }

    return {variantGroups: []};
}

function extractSpeakersFeatures(speakers: SpeakersOutputDTO): ProductFeatureType[] {
    let speakersFeatures: ProductFeatureType[] = [];

    speakersFeatures.push({name: "Wireless", value: speakers.wireless ? "Yes" : "No"});
    speakersFeatures.push({name: "Bluetooth standard", value: speakers.bluetoothStandard ? "" + speakers.bluetoothStandard : "Does not apply"});

    return speakersFeatures;
}

function extractSpeakersVariantFeatures(speakersVariant: SpeakersVariantOutputDTO): ProductFeatureType[] {
    let variantFeatures: ProductFeatureType[] = [];

    variantFeatures.push({name: "Loudness", value: speakersVariant.loudness + " db"});
    variantFeatures.push({name: "Color", value: speakersVariant.color});

    return variantFeatures;
}

function useSpeakersVariantGroup(product: ProductOutputDTO): VariantSelectorResults {
    const [loudness, setLoudness] = useState<string>("0");
    const [color, setColor] = useState<string>("");


    useEffect(() => {
        if (product.variants.length > 0 && isSpeakersVariantOutputDTOArray(product.variants)) {
            setLoudness(product.variants[0].loudness + "");
            setColor(product.variants[0].color);
        }
    }, [product]);
    if (isSpeakersVariantOutputDTOArray(product.variants)) {
        const allLoudness = product.variants.map(variant => variant.loudness + "")
            .filter((variant, index, array) => array.indexOf(variant) === index);
        const allColors = product.variants.map(variant => variant.color)
            .filter((variant, index, array) => array.indexOf(variant) === index);
        
        const loudnessVariantsGroup: VariantsGroup = {
            name: "Loudness",
            options: allLoudness,
            selectedOption: loudness,
            setSelectedOption: (selectedOption: string) => {
                const variant = product.variants.find(variant => isSpeakersVariantOutputDTO(variant) && variant.loudness + "" === selectedOption) as SpeakersVariantOutputDTO;
                setLoudness(variant.loudness + "");
                setColor(variant.color);
            },
        };

        const colorsVariantsGroup: VariantsGroup = {
            name: "Color",
            options: allColors,
            selectedOption: color,
            setSelectedOption: (selectedOption: string) => {
                const variant = product.variants.find(variant => isSpeakersVariantOutputDTO(variant) && variant.color === selectedOption) as SpeakersVariantOutputDTO;
                setLoudness(variant.loudness + "");
                setColor(variant.color);
            },
        };

        const selectedVariant = product.variants.find(variant => variant.loudness + "" === loudness && variant.color === color);

        return {variantGroups: [loudnessVariantsGroup, colorsVariantsGroup], selectedVariant: selectedVariant};
    }

    return {variantGroups: []};
}

function extractSubwoofersFeatures(subwoofers: SubwoofersOutputDTO): ProductFeatureType[] {
    let subwoofersFeatures: ProductFeatureType[] = [];

    subwoofersFeatures.push({name: "Power", value: subwoofers.power + " W"});
    subwoofersFeatures.push({name: "Type", value: subwoofers.type});

    return subwoofersFeatures;
}

function extractSubwoofersVariantFeatures(subwoofersVariant: SubwoofersVariantOutputDTO): ProductFeatureType[] {
    let variantFeatures: ProductFeatureType[] = [];

    variantFeatures.push({name: "Color", value: subwoofersVariant.color});

    return variantFeatures;
}

function useSubwoofersVariantsGroup(product: ProductOutputDTO): VariantSelectorResults {
    const [color, setColor] = useState<string>("");

    useEffect(() => {
        if (product.variants.length > 0 && isSubwoofersVariantOutputDTOArray(product.variants)) {
            setColor(product.variants[0].color);
        }
    }, [product]);
    if (isSubwoofersVariantOutputDTOArray(product.variants)) {
        const allColors = product.variants.map(variant => variant.color)
            .filter((variant, index, array) => array.indexOf(variant) === index);

        const colorsVariantGroup: VariantsGroup = {
            name: "Color",
            options: allColors,
            selectedOption: color,
            setSelectedOption: setColor,
        };

        const selectedVariant = product.variants.find(variant => variant.color === color);

        return {variantGroups: [colorsVariantGroup], selectedVariant: selectedVariant};
    }

    return {variantGroups: []};
}

export function extractFeatures(product: ProductOutputDTO, selectedVariantId: number): ProductFeatureType[] {
    let variant: ProductVariantOutputDTO = product.variants[selectedVariantId];
    
    let features: ProductFeatureType[] = extractBaseFeatures(product);
    features.push(...extractTypeSpecificFeatures(product));

    if (variant) {
        features.push(...extractTypeSpecificVariantFeatures(variant));
    }

    return features;
}

export function useVariantGroups(product: ProductOutputDTO): VariantSelectorResults {

    let hookToCall: (product: ProductOutputDTO) => VariantSelectorResults;

    if (isAmplifiers(product)) {
        hookToCall = useAmplifiersVariantGroup;
    } else if (isGramophones(product)) {
        hookToCall = useGramophonesVariantGroup;
    } else if (isHeadphones(product)) {
        hookToCall = useHeadphonesVariantGroup;
    } else if (isMicrophones(product)) {
        hookToCall = useMicrophonesVariantsGroup;
    } else if (isSpeakers(product)) {
        hookToCall = useSpeakersVariantGroup;
    } else {
        hookToCall = useSubwoofersVariantsGroup;
    }

    return hookToCall(product);
}

export function buildQuery(name: string, category: SelectedCategory | undefined, priceRange: {min?: number, max?: number}, states: State[]): string {
    let query = "";

    if (name) {
        const nameQuery = `name==*${name}*`;

        query += nameQuery;
    }

    if (category && !category.selectedAll && category.selectedSubcategories.length > 0) {
        if (query) query += ";";

        const categoryQuery = `${category.subcategoryName}=in=(` + category.selectedSubcategories.map(category => category.value) + ")";

        query += categoryQuery;
    }

    if (priceRange.min) {
        if (query) query += ";";

        query += `basePrice=ge=${priceRange.min}`;
    }

    if (priceRange.max) {
        if (query) query += ";";

        query += `basePrice=le=${priceRange.max}`;
    }
    
    return query ? query : "id=ge=0";
}

export default Utils;