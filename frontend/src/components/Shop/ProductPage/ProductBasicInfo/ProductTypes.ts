export type ProductType = {
  id: number;
  name: string;
  category: string;
  description: string;
  previousePrice: string;
  price: string;
  features: ProductFeatureType[];
  image: string;
  averageRating: number;
  comments: CommentType[];
};

export type ProductFeatureType = {
  name: string;
  value: string;
};

export type CommentType = {
  id: number;
  user: string;
  rating: number;
  timestamp: string;
  title: string;
  content: string;
};
