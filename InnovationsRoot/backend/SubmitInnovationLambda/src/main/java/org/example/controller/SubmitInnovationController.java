package org.example.controller;

import org.example.builder.InnovationBuilder;
import org.example.dto.InnovationRequest;
import org.example.enums.InnovationStatus;
import org.example.model.Innovation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@CrossOrigin
public class SubmitInnovationController {

    private final InnovationBuilder builder = InnovationBuilder.createBuilder();

    @PostMapping(
            value = "/add-innovation",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> createInnovation(@RequestBody InnovationRequest innovationRequest, Principal principal) {

        System.out.println("PRINCIPAAAAAAAAAAAAAAAAAL");
        System.out.println(principal.getName());

        Innovation i = new Innovation(innovationRequest.getTitle(), innovationRequest.getDescription(),
                InnovationStatus.PENDING, innovationRequest.getUserId());

        // send mail to lead

        builder.save(i);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
