import { InnovationStatus } from "../enum/innovationstatus";

export interface Innovation {
    id: string;
    userId: string;
    title: string;
    description: string;
    innovationStatus: InnovationStatus;
    comment: string;
    tokenAmount: number;
}