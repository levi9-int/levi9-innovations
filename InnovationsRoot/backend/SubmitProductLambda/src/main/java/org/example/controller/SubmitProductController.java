package org.example.controller;

import org.example.builder.EmployeeBuilder;
import org.example.builder.InnovationBuilder;
import org.example.builder.ProductBuilder;
import org.example.dto.InnovationRequest;
import org.example.dto.ProductRequestDTO;
import org.example.enums.InnovationStatus;
import org.example.mail.MailSender;
import org.example.model.Employee;
import org.example.model.Innovation;
import org.example.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class SubmitProductController {

    private final ProductBuilder productBuilder = ProductBuilder.createBuilder();

    @PostMapping(
            value = "/add-products",
            produces = "application/json",
            consumes = "application/json"
    )

    public ResponseEntity<?> createProduct(@RequestBody ProductRequestDTO productRequest) {

        System.out. println("Kreiraaajajjjjjjj");
        Product p = new Product(productRequest.getName(), productRequest.getTokenPrice(), productRequest.getAmount());

        System.out.println(p);

        productBuilder.save(p);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }



}
