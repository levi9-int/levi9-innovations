export interface ReviewRequest {
    innovationId: string;
    userId: string;
    title: string;
    description: string;
    approved: boolean;
    comment: string;
}