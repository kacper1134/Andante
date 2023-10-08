import { useSearchParams } from "react-router-dom";
import PaymentWrapper from "../components/Cart/Payment/PaymentWrapper";
import useAuthentication from "../hooks/useAuthentication";

const PaymentPage = () => {
  useAuthentication("/payment");
  const [searchParams] = useSearchParams();
  const orderId = searchParams.get("orderId");
  return <PaymentWrapper orderId={+orderId!} />;
};

export default PaymentPage;
