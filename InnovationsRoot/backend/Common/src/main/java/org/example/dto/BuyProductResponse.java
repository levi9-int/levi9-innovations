package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.Product;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyProductResponse {
    private Long usersTokens;
    private List<Product> products;
}
