package org.example.controller;

import org.example.builder.EmployeeBuilder;
import org.example.builder.InnovationBuilder;
import org.example.dto.ReviewInnovationRequest;
import org.example.enums.InnovationStatus;
import org.example.mail.MailSender;
import org.example.model.Employee;
import org.example.model.Innovation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.sesv2.SesV2Client;

@RestController
@CrossOrigin
public class ReviewInnovationController {

    private final InnovationBuilder innovationRepo = InnovationBuilder.createBuilder();
    private final EmployeeBuilder employeeRepo = EmployeeBuilder.createBuilder();
    private final MailSender mailSender = MailSender.createMailSender();

    @PutMapping(
            value = "/review-innovation",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> reviewInnovation(@RequestBody ReviewInnovationRequest reviewInnovationRequest) {

        Innovation innovation = innovationRepo.findByUserIdAndInnovationId(
                reviewInnovationRequest.getInnovationId(),
                reviewInnovationRequest.getUserId());

        if (innovation == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (reviewInnovationRequest.isApproved()) {
            innovation.setInnovationStatus(InnovationStatus.APPROVED);
            addTokensForUser(innovation);
        } else {
            innovation.setInnovationStatus(InnovationStatus.REJECTED);
        }

        if (reviewInnovationRequest.getComment() != null && !reviewInnovationRequest.getComment().isBlank())
            innovation.setComment(reviewInnovationRequest.getComment());

        innovationRepo.save(innovation);

//        send mail to user
        String recipient = getUserMail(innovation);
        String subject = "Your innovation "+innovation.getTitle()+" is "+innovation.getInnovationStatus();
        String body = innovation.getComment() != null ? innovation.getComment() : "";
        boolean successSending = mailSender.send(recipient, subject, body);
        if(!successSending)
            new ResponseEntity<>("Mail isn't succesfully sent!", HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void addTokensForUser(Innovation innovation) {
        Employee employee = employeeRepo.findById(innovation.getUserId());
        employee.setTokens(employee.getTokens() + 1);
        employeeRepo.save(employee);
    }

    private String getUserMail(Innovation innovation) {
        Employee employee = employeeRepo.findById(innovation.getUserId());
        return employee.getEmail();
    }
}
