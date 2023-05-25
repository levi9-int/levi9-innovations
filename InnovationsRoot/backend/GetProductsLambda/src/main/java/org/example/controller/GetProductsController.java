package org.example.controller;

import org.example.builder.ProductBuilder;
import org.example.model.Product;
import org.example.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class GetProductsController {

    private final ProductService productService;

    public GetProductsController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping(
            value = "/get-products",
            produces = "application/json"
    )public ResponseEntity<?> getAllProducts(){

        List<Product> productsList = productService.getProducts();
        return new ResponseEntity<>(productsList,HttpStatus.OK);
    }
}
