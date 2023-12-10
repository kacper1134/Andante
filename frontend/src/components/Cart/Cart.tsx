import { useState } from 'react';
import { Tabs, Tab, TabList, TabPanels, TabPanel, useBreakpointValue, Stack } from '@chakra-ui/react';
import CartContent from './CartContent';
import CartSummary from './CartSummary';
import { RootState } from '../../store';
import { useSelector } from 'react-redux';
import CartForms from './CartForms';
import CartConfirmation from './CartConfirmation';
import { useTranslation } from 'react-i18next';


export interface CartProps {
    startCartStep: number
}


const Cart: React.FC<CartProps> = ({startCartStep}) => {
    const cartItems = useSelector((state: RootState) => state.cart.cartItems);
    const [currentCartStep, setCurrentCartStep] = useState(startCartStep);
    const direction = useBreakpointValue({
        base: "column",
        lg: "row",
    })!;
    
    const tabsFontSize = useBreakpointValue({
        base: "16px",
        md: "18px",
        lg: "20px",
    })!;
    const {t} = useTranslation();
    return <Tabs colorScheme="primary" index={currentCartStep}>
        <TabList justifyContent="center" textStyle="p">
            <Tab fontSize={tabsFontSize} w="300px" onClick={() => setCurrentCartStep(0)} isDisabled={currentCartStep===2}>{t("orderPage.steps.first")}</Tab>
            <Tab fontSize={tabsFontSize} w="300px" isDisabled={currentCartStep < 1 || currentCartStep===2} onClick={() => setCurrentCartStep(1)}>{t("orderPage.steps.second")}</Tab>
            <Tab fontSize={tabsFontSize} w="300px" isDisabled={currentCartStep < 2}>{t("orderPage.steps.third")}</Tab>
        </TabList>
        <TabPanels>
            <TabPanel>
                <Stack direction={direction === 'column' ? 'column' : 'row'} justifyContent="space-around" w="inherit" mt="24px">
                    <CartContent cartItems={cartItems} />
                    <CartSummary changeCurrentCardStep={setCurrentCartStep} />
                </Stack>
            </TabPanel>
            <TabPanel>
                <CartForms changeCurrentCartStep={setCurrentCartStep} />
            </TabPanel>
            <TabPanel>
                <CartConfirmation />
            </TabPanel>
        </TabPanels>
    </Tabs>
}


export default Cart;