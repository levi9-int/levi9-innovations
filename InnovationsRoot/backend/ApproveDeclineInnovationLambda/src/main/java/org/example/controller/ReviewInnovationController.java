package org.example.controller;

import org.example.dto.ReviewInnovationRequest;
import org.example.service.InnovationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
//@Import({InnovationService.class})
public class ReviewInnovationController {

    private final InnovationService innovationService;

    public ReviewInnovationController(InnovationService innovationService) {
        this.innovationService = innovationService;
    }

    @PutMapping(
            value = "/review-innovation",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> reviewInnovation(@RequestBody ReviewInnovationRequest reviewInnovationRequest) {

        this.innovationService.reviewInnovation(reviewInnovationRequest);
        return new ResponseEntity<>(HttpStatus.OK);

    }
}
