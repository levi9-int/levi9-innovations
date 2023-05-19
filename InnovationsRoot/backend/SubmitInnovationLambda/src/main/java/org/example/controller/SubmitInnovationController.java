package org.example.controller;

import org.example.builder.EmployeeBuilder;
import org.example.builder.InnovationBuilder;
import org.example.dto.InnovationRequest;
import org.example.enums.InnovationStatus;
import org.example.model.Employee;
import org.example.model.Innovation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@CrossOrigin
public class SubmitInnovationController {

    private static final String LEAD_MAIL = "sfetel21@outlook.com";
    private final InnovationBuilder builder = InnovationBuilder.createBuilder();
    private final EmployeeBuilder employeeRepo = EmployeeBuilder.createBuilder();
    private final org.example.mail.MailSender mailSender = org.example.mail.MailSender.createMailSender();

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

        Employee emp = getEmployee(i);
        String recipient = LEAD_MAIL;
        String subject = "New innovation \""+i.getTitle()+"\" was created by "+emp.getName()+" "+emp.getLastName();
        String body = "";
        boolean successSending = mailSender.send(recipient, subject, body);
        if(!successSending)
            new ResponseEntity<>("Mail isn't succesfully sent!", HttpStatus.INTERNAL_SERVER_ERROR);

        builder.save(i);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Employee getEmployee(Innovation innovation) {
        return employeeRepo.findById(innovation.getUserId());
    }
}
