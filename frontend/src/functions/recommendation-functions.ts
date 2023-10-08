import recombeeClient from "../config/recombee-config";
import recombee from "recombee-js-api-client";

export enum InteractionType {
  VIEW,
  CART,
  RATING,
  PURCHASE,
}

const add_interaction = (
  username: string,
  productId: number,
  iteractionType: InteractionType,
  rating?: number
) => {
  if (iteractionType === InteractionType.VIEW) {
    add_view_interaction(username, productId);
  } else if (iteractionType === InteractionType.CART) {
    add_cart_interaction(username, productId);
  } else if (iteractionType === InteractionType.RATING) {
    add_rating_interaction(username, productId, rating!);
  }
};

const add_view_interaction = (username: string, productId: number) => {
  recombeeClient.send(
    new recombee.AddDetailView(username, productId),
    (err: any, response: any) => {}
  );
};

const add_cart_interaction = (username: string, productId: number) => {
  recombeeClient.send(
    new recombee.AddCartAddition(username, productId),
    (err: any, response: any) => {}
  );
};

const add_rating_interaction = (
  username: string,
  productId: number,
  rating: number
) => {
  recombeeClient.send(
    new recombee.AddRating(username, productId, rating / 10),
    (err: any, response: any) => {}
  );
};

export const add_purchase_interaction = (
  username: string,
  productIds: number[]
) => {
  productIds.forEach((productId) =>
    recombeeClient.send(
      new recombee.AddPurchase(username, productId),
      (err: any, response: any) => {}
    )
  );
};

export default add_interaction;
