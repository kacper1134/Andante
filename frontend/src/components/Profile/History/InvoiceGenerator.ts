import jsPDFInvoiceTemplate, {
  OutputType,
  jsPDF,
} from "jspdf-invoice-template";
import { OrderDetails } from "./OrdersList";
import "./Roboto-Regular-normal";
import { callAddFont } from "./Roboto-Regular-normal";

const generateInvoice = (order: OrderDetails, save: boolean) => {
  const invoiceId = "AN" + order.orderId.toString().padStart(6, "0");
  const fileName = "INVOICE_AN" + order.orderId.toString().padStart(6, "0");
  const startDate = order.orderDate.toJSDate();
  const endDate = new Date(startDate);
  endDate.setDate(endDate.getDate() + 10);
  const phoneParts = order.addressedDetails.phone.match(/.{1,3}/g);
  const phone = phoneParts?.join(" ");

  const totalPrice = order.orderedItems.reduce(
    (a, b) => a + b.variant.price * b.quantity,
    0
  );
  const vat: number = 23;
  const subPrice = (totalPrice * (100 - vat)) / 100;

  const props = {
    outputType: OutputType.Blob,
    returnJsPDFDocObject: true,
    fileName,
    orientationLandscape: false,
    compress: true,
    logo: {
      src: "/Andante.png",
      width: 68.94,
      height: 26.66,
      margin: {
        top: 0,
        left: 0,
      },
    },
    stamp: {
      inAllPages: true,
      src: "/AndanteStamp.png",
      width: 20,
      height: 20,
      margin: {
        top: 0,
        left: 0,
      },
    },
    business: {
      name: "Andante",
      address: "Poland, Wroclaw, wybrzeże Stanisława Wyspiańskiego 27",
      phone: "(+48) 123 456 789",
      email: "andante@gmail.com",
      email_1: "andante_info@gmail.com",
      website: "www.andante.com",
    },
    contact: {
      label: "Invoice issued for:",
      name: order.addressedDetails.name + " " + order.addressedDetails.surname,
      address:
        order.shippingAddress.country +
        ", " +
        order.shippingAddress.city +
        ", " +
        order.shippingAddress.street +
        ", " +
        order.shippingAddress.postalCode,
      phone,
      email: order.addressedDetails.email,
    },
    invoice: {
      label: "Invoice #: ",
      num: invoiceId,
      invDate: "Payment Date: " + startDate.toLocaleDateString("pl"),
      invGenDate: "Invoice Date: " + endDate.toLocaleDateString("pl"),
      headerBorder: true,
      tableBodyBorder: true,
      header: [
        {
          title: "#",
          style: {
            width: 10,
          },
        },
        {
          title: "Title",
          style: {
            width: 120,
          },
        },
        { title: "Price" },
        { title: "Quantity" },
        { title: "Total" },
      ],
      table: Array.from(order.orderedItems, (item, index) => [
        index + 1,
        item.variant.productName,
        order.orderCurrency + item.variant.price,
        item.quantity,
        order.orderCurrency + item.variant.price * item.quantity,
      ]),
      additionalRows: [
        {
          col1: "Total:",
          col2: order.orderCurrency + totalPrice.toFixed(2),
          col3: "ALL",
          style: {
            fontSize: 13,
          },
        },
        {
          col1: "VAT:",
          col2: vat.toString(),
          col3: "%",
          style: {
            fontSize: 13,
          },
        },
        {
          col1: "SubTotal:",
          col2: order.orderCurrency + subPrice.toFixed(2),
          col3: "ALL",
          style: {
            fontSize: 13,
          },
        },
      ],
    },
    footer: {
      text: "The invoice is created on a computer and is valid without the signature and stamp.",
    },
    pageEnable: true,
    pageLabel: "Page ",
  };

  jsPDF.API.events.push(["initialized", callAddFont]);
  // @ts-ignore
  const {jsPDFDocObject, blob} = jsPDFInvoiceTemplate(props);
  if(save) jsPDFDocObject?.save(fileName);
  return blob;
};

export default generateInvoice;
