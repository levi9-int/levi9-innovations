package org.example.controller;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.example.builder.InnovationBuilder;
import org.example.dto.GetRequest;
import org.example.dto.InnovationRequest;
import org.example.enums.InnovationStatus;
import org.example.model.Innovation;
//import org.example.repository.InnovationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
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
