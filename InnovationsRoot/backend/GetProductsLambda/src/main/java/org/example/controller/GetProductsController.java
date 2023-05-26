package org.example.controller;

import org.apache.http.HttpHeaders;
import org.example.builder.ProductBuilder;
import org.example.dto.BoughtProduct;
import org.example.dto.InnovationUserIdResponse;
import org.example.dto.InnovationWithUserDetails;
import org.example.model.Product;
import org.example.service.ProductService;
import org.example.utils.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class GetProductsController {

    private final ProductService productService;

    public GetProductsController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(
            value = "/get-products",
            produces = "application/json"
    )
    public ResponseEntity<?> getAllProducts(@RequestParam Map<String, String> params,
                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {

        String sub = JWTUtil.getSub(accessToken, "sub");
        if (sub == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (params.containsKey("bought")) {
            Collection<BoughtProduct> boughtProducts = productService.getBoughtProductsByUserId(sub);
            return new ResponseEntity<>(boughtProducts, HttpStatus.OK);

        } else if (params.isEmpty()) {
            List<Product> productsList = productService.getProducts();
            return new ResponseEntity<>(productsList, HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
