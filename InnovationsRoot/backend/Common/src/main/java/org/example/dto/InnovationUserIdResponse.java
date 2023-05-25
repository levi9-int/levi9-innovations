package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.Employee;
import org.example.model.Innovation;
import org.example.model.Product;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InnovationUserIdResponse {
    private String name;
    private String lastname;
    private Long tokens;
    private List<Innovation> innovations;
    private List<Product> products;

    public InnovationUserIdResponse(Employee employee, List<Innovation> innovations, List<Product> products) {
        this.tokens = employee.getTokens();
        this.name = employee.getName();
        this.lastname = employee.getLastName();
        this.innovations = innovations;
        this.products = products;
    }
}
