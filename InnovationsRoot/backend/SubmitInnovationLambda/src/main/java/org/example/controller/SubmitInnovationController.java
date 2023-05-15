package org.example.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.example.builder.InnovationBuilder;
import org.example.dto.InnovationRequest;
import org.example.enums.InnovationStatus;
import org.example.model.Innovation;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class SubmitInnovationController {

    private final InnovationBuilder builder = InnovationBuilder.createBuilder();

    @PostMapping(
            value = "/add-innovation",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> createInnovation(@RequestBody InnovationRequest innovationRequest) {

        Innovation i = new Innovation(innovationRequest.getTitle(), innovationRequest.getDescription(),
                InnovationStatus.PENDING, innovationRequest.getUserId());

        builder.save(i);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
