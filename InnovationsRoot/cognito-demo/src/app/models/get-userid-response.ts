import { Innovation } from "./innovation";
import { Product } from "./product";

export interface fullUserInfoResponse {
    name: string;
    lastname: string;
    tokens: number;
    innovations: Innovation[];
    products: Product[];
}