package org.example.controller;

import org.example.dto.BuyProductRequest;
import org.example.dto.BuyProductResponse;
import org.example.model.Product;
import org.example.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class BuyProductController {

    private final ProductService productService;

    public BuyProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping(
            value = "/buy-product",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> buyProduct(@RequestBody BuyProductRequest buyProductRequest){
        BuyProductResponse buyProductResponse = productService.buyProduct(buyProductRequest);
        
        return new ResponseEntity<>(buyProductResponse, HttpStatus.CREATED);
    }
}
