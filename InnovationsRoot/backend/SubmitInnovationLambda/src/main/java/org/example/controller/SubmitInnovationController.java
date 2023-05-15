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
import org.example.enums.InnovationStatus;
import org.example.model.Innovation;
//import org.example.repository.InnovationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class SubmitInnovationController {

    private final InnovationBuilder builder = createBuilder();


    @PostMapping(
            value = "/add-innovation",
            produces = "application/json",
            consumes = "application/json"
    )
    public ResponseEntity<?> test(@RequestBody InnovationRequest innovationRequest) {

        Innovation i = new Innovation(innovationRequest.getTitle(), innovationRequest.getDescription(), InnovationStatus.ACCEPTED);
        builder.save(i);

        return new ResponseEntity<>(builder.findById("2"), HttpStatus.OK);
    }

    private InnovationBuilder createBuilder() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("eu-north-1")
                .build();


        String tableName = "innovation";
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName))
                .build();

        //Use of the Partition Index (state) and the sort key(city) for querying data
        //Let's just see if there is any data (for Alberta in this case)
        //We are only going to add data once, so if there is data we will not do again
        return new InnovationBuilder(new DynamoDBMapper(client, mapperConfig), mapperConfig);
    }
}
