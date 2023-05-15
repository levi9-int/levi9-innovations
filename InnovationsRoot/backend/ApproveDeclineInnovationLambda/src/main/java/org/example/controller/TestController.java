package org.example.controller;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.example.builder.InnovationBuilder;
import org.example.dto.InnovationRequest;
import org.example.model.Innovation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
//@Import({InnovationRepository.class})
public class TestController {

//    @Autowired
//    private InnovationRepository innovationRepository;


    @PostMapping(
            value = "/add-innovation",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> test(@RequestBody InnovationRequest innovationRequest) {
        //Specify credential details
//        AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(
//                new BasicAWSCredentials(System.getenv("ACCESS_KEY"),
//                        System.getenv("SECRET_ACCESS_KEY")));
//
//        //Create client
//        AmazonDynamoDB ddbClient = AmazonDynamoDBClientBuilder.standard()
//                .withCredentials(credentials)
//                .withRegion("eu-north-1") //Remember to change your region!
//                .build();
//
//        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
//
//
//        Innovation i = new Innovation(innovationRequest.getTitle(), innovationRequest.getDescription());
////        innovationRepository.addInnovation(i);
//        new InnovationBuilder(i, 1, mapper).save();

        return new ResponseEntity<>(HttpStatus.OK);

    }
}
