package org.example.controller;

import org.example.builder.InnovationBuilder;
import org.example.model.Innovation;
//import org.example.repository.InnovationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class GetInnovationController {

    private final InnovationBuilder builder = InnovationBuilder.createBuilder();

    @GetMapping(
            value = "/get-innovation",
            produces = "application/json"
    )
    public ResponseEntity<?> getByUserId(@RequestParam Map<String,String> allParams) {

        ResponseEntity<List<Innovation>> response;
        if(allParams.containsKey("userId"))
            response = new ResponseEntity<>(builder.getByUserId(allParams.get("userId")), HttpStatus.OK);
        else if(allParams.containsKey("status"))
            response = new ResponseEntity<>(builder.getByStatus(allParams.get("status")), HttpStatus.OK);
        else if(allParams.isEmpty())
            response = new ResponseEntity<>(builder.getAll(), HttpStatus.OK);
        else
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return response;
    }
}
