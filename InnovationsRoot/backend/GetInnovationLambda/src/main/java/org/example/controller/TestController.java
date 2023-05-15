package org.example.controller;

import org.example.dto.InnovationRequest;
import org.example.model.Innovation;
//import org.example.repository.InnovationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
//@Import({InnovationRepository.class})
public class TestController {
//
//    @Autowired
////    private InnovationRepository innovationRepository;
//
//    @PostMapping(
//            value = "/add-innovation",
//            produces = "application/json",
//            consumes = "application/json"
//    )
//    public ResponseEntity<?> test(@RequestBody InnovationRequest innovationRequest) {
//
//        Innovation i = new Innovation(innovationRequest.getTitle(), innovationRequest.getDescription());
//        innovationRepository.addInnovation(i);
//
//        return new ResponseEntity<>(innovationRepository.getInnovations(), HttpStatus.OK);

//    }
}
