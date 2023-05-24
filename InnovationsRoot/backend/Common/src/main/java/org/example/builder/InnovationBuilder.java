package org.example.builder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import org.example.enums.InnovationStatus;
import org.example.model.Innovation;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InnovationBuilder {

    private final DynamoDBMapper mapper;
    private final DynamoDBMapperConfig mapperConfig;

    public InnovationBuilder(DynamoDBMapper mapper, DynamoDBMapperConfig config) {
        this.mapper = mapper;
        this.mapperConfig = config;
    }

    public static InnovationBuilder createBuilder() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("eu-north-1")
                .build();

        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder().build();
        return new InnovationBuilder(new DynamoDBMapper(client, mapperConfig), mapperConfig);
    }

    public DynamoDBMapper getMapper() {
        return this.mapper;
    }

    public void save(Innovation innovation) {
        mapper.save(innovation);
    }

    public List<Innovation> getAll() {
        return mapper.scan(Innovation.class, new DynamoDBScanExpression());
    }

    public Innovation getById(String id) {
        return mapper.load(Innovation.class, id);
    }

    public List<Innovation> getByUserId(String userId) {

        // Prepare the query request
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":u", new AttributeValue().withS(userId));

        DynamoDBQueryExpression<Innovation> queryExpression = new DynamoDBQueryExpression<Innovation>()
                .withKeyConditionExpression("userId = :u")
                .withExpressionAttributeValues(expressionAttributeValues);

        return mapper.query(Innovation.class, queryExpression);
    }

//        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
//
//        Map<String, Condition> scanFilter = new HashMap<String, Condition>();
//        Condition scanCondition = new Condition()
//                .withComparisonOperator(ComparisonOperator.EQ)
//                .withAttributeValueList(new AttributeValue().withS(userId));
//        scanFilter.put("userId", scanCondition);
//        scanExpression.setScanFilter(scanFilter);
//
//        return mapper.scan(Innovation.class, scanExpression);
//    }

    public List<Innovation> getByStatus(String status) {

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":s", new AttributeValue().withS(status));

        DynamoDBQueryExpression<Innovation> queryExpression = new DynamoDBQueryExpression<Innovation>()
                .withIndexName("innovationStatus-index")
                .withKeyConditionExpression("innovationStatus = :s")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withConsistentRead(false);

        return mapper.query(Innovation.class, queryExpression);

//        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
//
//        Map<String, Condition> scanFilter = new HashMap<String, Condition>();
//        Condition scanCondition = new Condition()
//                .withComparisonOperator(ComparisonOperator.EQ)
//                .withAttributeValueList(new AttributeValue().withS(status));
//        scanFilter.put("status", scanCondition);
//        scanExpression.setScanFilter(scanFilter);
//
//        return mapper.scan(Innovation.class, scanExpression);
    }

    public Innovation findById(String id) {
        return mapper.load(Innovation.class, id, mapperConfig);
    }

    public Innovation findByUserIdAndInnovationId(String innovationId, String userId) {

        // Prepare the query request
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":u", new AttributeValue().withS(userId));
        expressionAttributeValues.put(":i", new AttributeValue().withS(innovationId));

        DynamoDBQueryExpression<Innovation> queryExpression = new DynamoDBQueryExpression<Innovation>()
                .withKeyConditionExpression("userId = :u AND innovationId = :i")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withLimit(1);

        List<Innovation> innovations = mapper.query(Innovation.class, queryExpression);

        if (!innovations.isEmpty()) {
            return innovations.get(0);
        } else {
            System.out.println("No matching innovation found.");
            return null;
        }
    }
}
