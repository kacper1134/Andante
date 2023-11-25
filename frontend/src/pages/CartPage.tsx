import { Route, Routes, useSearchParams } from "react-router-dom";
import Cart from "../components/Cart/Cart";
import useAuthentication from "../hooks/useAuthentication";
import PaymentPage from "./PaymentPage";

export interface CartPageProps {}

const CartPage: React.FC<CartPageProps> = () => {
  useAuthentication("/cart");
  const [searchParams] = useSearchParams();
  const startCartStep = searchParams.get("startCartStep");

  return (
    <Routes>
      <Route path="/" element={<Cart startCartStep={startCartStep !== null ? +startCartStep : 0} />} />
      <Route path="/payment" element={<PaymentPage />} />
    </Routes>
  );
};

export default CartPage;
