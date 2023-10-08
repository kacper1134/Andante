import { Route, Routes } from "react-router-dom";
import Cart from "../components/Cart/Cart";
import useAuthentication from "../hooks/useAuthentication";
import PaymentPage from "./PaymentPage";

export interface CartPageProps {}

const CartPage: React.FC<CartPageProps> = () => {
  useAuthentication("/cart");

  return (
    <Routes>
      <Route path="/" element={<Cart />}/>
      <Route path="/payment" element={<PaymentPage />}/>
    </Routes>
  );
};

export default CartPage;
