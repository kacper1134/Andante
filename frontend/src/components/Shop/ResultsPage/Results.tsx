import AdvancedProductCard from './AdvancedProductCard/AdvancedProductCard';
import { Wrap } from '@chakra-ui/react';
import { ProductOutputDTO } from '../../../store/api/result/dto/product/base/ProductOutputDTO';

export interface ResultsProps {
    products: ProductOutputDTO[]
}

const Results: React.FC<ResultsProps> = ({products}) => {
    return <Wrap>
        {products.map((item, index) => <AdvancedProductCard key={index} data={item} />)}
    </Wrap>
}

export default Results