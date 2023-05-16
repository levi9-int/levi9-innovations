package org.example.builder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import org.example.model.Innovation;
import com.amazonaws.services.dynamodbv2.model.*;

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

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

        Map<String, Condition> scanFilter = new HashMap<String, Condition>();
        Condition scanCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(userId));
        scanFilter.put("userId", scanCondition);
        scanExpression.setScanFilter(scanFilter);

        List result = mapper.scan(Innovation.class, scanExpression);

        return result;
    }

    public List<Innovation> getByStatus(String status) {

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

        Map<String, Condition> scanFilter = new HashMap<String, Condition>();
        Condition scanCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(status));
        scanFilter.put("status", scanCondition);
        scanExpression.setScanFilter(scanFilter);

        List result = mapper.scan(Innovation.class, scanExpression);

        return result;
    }

    public Innovation findById(String id) {
        return mapper.load(Innovation.class, id, mapperConfig);
    }

}
