import { Employee } from "./employee";
import { Innovation } from "./innovation";

export interface InnovationUserDetailsResponse {
    innovation: Innovation;
    employee: Employee;
}