package org.example.controller;

import org.example.builder.EmployeeBuilder;
import org.example.builder.InnovationBuilder;
import org.example.dto.ReviewInnovationRequest;
import org.example.enums.InnovationStatus;
import org.example.model.Employee;
import org.example.model.Innovation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ReviewInnovationController {

    private final InnovationBuilder innovationRepo = InnovationBuilder.createBuilder();
    private final EmployeeBuilder employeeRepo = EmployeeBuilder.createBuilder();

    @PutMapping(
            value = "/review-innovation",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> reviewInnovation(@RequestBody ReviewInnovationRequest reviewInnovationRequest) {

        Innovation innovation = innovationRepo.findById(reviewInnovationRequest.getInnovationId());
        if (innovation == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (reviewInnovationRequest.isApproved()) {
            innovation.setStatus(InnovationStatus.APPROVED);
            addTokensForUser(innovation);
        } else {
            innovation.setStatus(InnovationStatus.REJECTED);
        }

        if (reviewInnovationRequest.getComment() != null && !reviewInnovationRequest.getComment().isBlank())
            innovation.setComment(reviewInnovationRequest.getComment());

        innovationRepo.save(innovation);

//        send mail to user
//        User user = userRepo.findById(innovation.getUserId());
//        .... mailService.sendMail(user, innovation)

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void addTokensForUser(Innovation innovation) {
        Employee employee = employeeRepo.findById(innovation.getUserId());
        employee.setTokens(employee.getTokens() + 1);
        employeeRepo.save(employee);
    }
}
