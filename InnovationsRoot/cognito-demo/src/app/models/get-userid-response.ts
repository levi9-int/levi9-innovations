import { Innovation } from "./innovation";

export interface GetUserIdResponse {
    name: string;
    lastname: string;
    tokens: number;
    innovations: Innovation[];
}