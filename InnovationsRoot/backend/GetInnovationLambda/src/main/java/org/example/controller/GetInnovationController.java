package org.example.controller;

import org.example.builder.EmployeeBuilder;
import org.example.builder.InnovationBuilder;
import org.example.dto.innovationUserIdResponse;
import org.example.dto.innovationWithUserDetails;
import org.example.model.Employee;
//import org.example.repository.InnovationRepository;
import org.example.model.Innovation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class GetInnovationController {

    private final InnovationBuilder innovationBuilder = InnovationBuilder.createBuilder();
    private final EmployeeBuilder employeeBuilder = EmployeeBuilder.createBuilder();

    @GetMapping(
            value = "/get-innovation",
            produces = "application/json"
    )
    public ResponseEntity<?> getByUserId(@RequestParam Map<String,String> allParams) {

        ResponseEntity<?> response;
        if(allParams.containsKey("userId")) {
            Employee emp = employeeBuilder.findById(allParams.get("userId"));
            innovationUserIdResponse getResponse = new innovationUserIdResponse(emp.getName(), emp.getLastName(), emp.getTokens(), innovationBuilder.getByUserId(allParams.get("userId")));
            response = new ResponseEntity<>(getResponse, HttpStatus.OK);
        }
        else if(allParams.containsKey("status")) {
            List<Innovation> innovations = innovationBuilder.getByStatus(allParams.get("status"));
            List<innovationWithUserDetails> responseList = new ArrayList<>();
            for (Innovation i : innovations) {
                Employee emp = employeeBuilder.findById(i.getUserId());
                responseList.add(new innovationWithUserDetails(i, emp));
            }
            response = new ResponseEntity<>(responseList, HttpStatus.OK);
        }
        else if(allParams.isEmpty())
            response = new ResponseEntity<>(innovationBuilder.getAll(), HttpStatus.OK);
        else
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return response;
    }
}
