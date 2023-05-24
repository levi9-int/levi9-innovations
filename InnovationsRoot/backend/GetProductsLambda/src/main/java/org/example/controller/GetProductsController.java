package org.example.controller;

import org.example.builder.ProductBuilder;
import org.example.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class GetProductsController {

    private final ProductBuilder productBuilder = ProductBuilder.createBuilder();

    @GetMapping(
            value = "/get-products",
            produces = "application/json"
    )


    public ResponseEntity<?> getAllProducts(){

        System.out.println("dobaviiiiiiiiiiiiii");
        List<Product> productsList = productBuilder.getAll();
        System.out.println(productsList);
        return new ResponseEntity<>(productsList,HttpStatus.OK);
    }
}
