package org.example.builder;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.example.model.Innovation;

public class InnovationBuilder {

    private int inserted;
    private final DynamoDBMapper mapper;
    private final DynamoDBMapperConfig mapperConfig;

    public InnovationBuilder(DynamoDBMapper mapper, DynamoDBMapperConfig config) {
        this.mapper = mapper;
        this.mapperConfig = config;
    }

    public DynamoDBMapper getMapper() {
        return this.mapper;
    }

    public int count() {
        return inserted;
    }

    public void save(Innovation innovation) {
        mapper.save(innovation);
        inserted+=1;
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
