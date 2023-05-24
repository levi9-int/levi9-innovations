package org.example.controller;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpHead;
import org.example.dto.InnovationUserIdResponse;
import org.example.dto.InnovationWithUserDetails;
import org.example.service.InnovationService;
import org.example.utils.JWTUtil;
import org.example.utils.JwtDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class GetInnovationController {
    private final InnovationService innovationService;

    public GetInnovationController(InnovationService innovationService) {
        this.innovationService = innovationService;
    }

    @GetMapping(
            value = "/get-innovation",
            produces = "application/json"
    )
    public ResponseEntity<?> getInnovations(@RequestParam Map<String, String> allParams, @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {

        for(String key: allParams.keySet())
            System.out.println(key);

        System.out.println(JwtDecoder.getUsername(accessToken));
//        String sub = JWTUtil.getSub(accessToken, "sub");
//        System.out.println(sub);

        if (allParams.containsKey("userId")) {
            InnovationUserIdResponse innovationDTO = innovationService.getInnovationsForUser(allParams.get("userId"));
            return new ResponseEntity<>(innovationDTO, HttpStatus.OK);

        } else if (allParams.containsKey("status")) {
            List<InnovationWithUserDetails> innovationsByStatus = innovationService.getByStatus(allParams.get("status"));
            return new ResponseEntity<>(innovationsByStatus, HttpStatus.OK);

        } else if (allParams.isEmpty())
            return new ResponseEntity<>(innovationService.getAll(), HttpStatus.OK);

        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
