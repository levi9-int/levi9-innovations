package org.example.builder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.example.model.Innovation;


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

        String tableName = "innovation";
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName))
                .build();

        return new InnovationBuilder(new DynamoDBMapper(client, mapperConfig), mapperConfig);
    }

    public DynamoDBMapper getMapper() {
        return this.mapper;
    }

    public void save(Innovation innovation) {
        mapper.save(innovation);
    }

//    public InnovationBuilder update() {
//        mapper.save(innovation, mapperConfig);
//        return this;
//    }
//
//    public InnovationBuilder delete() {
//        mapper.delete(innovation, mapperConfig);
//        return this;
//    }

    public Innovation findById(String id) {
        return mapper.load(Innovation.class, id, mapperConfig);
    }

}
