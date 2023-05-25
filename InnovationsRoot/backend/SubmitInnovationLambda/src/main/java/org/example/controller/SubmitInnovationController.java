package org.example.controller;

import org.example.builder.EmployeeBuilder;
import org.example.builder.InnovationBuilder;
import org.example.dto.InnovationRequest;
import org.example.enums.InnovationStatus;
import org.example.mail.MailSender;
import org.example.model.Employee;
import org.example.model.Innovation;
import org.example.service.InnovationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class SubmitInnovationController {
    private final InnovationService innovationService;

    public SubmitInnovationController(InnovationService innovationService) {
        this.innovationService = innovationService;
    }

    @PostMapping(
            value = "/add-innovation",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> createInnovation(@RequestBody InnovationRequest innovationRequest) {

//        System.out.println(principal.getName()); try with Principal
        this.innovationService.createInnovation(innovationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);


    }
}
