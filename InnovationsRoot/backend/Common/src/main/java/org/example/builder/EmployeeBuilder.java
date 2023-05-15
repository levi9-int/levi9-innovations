package org.example.builder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.example.model.Employee;
import org.example.model.Innovation;

public class EmployeeBuilder {

    private final DynamoDBMapper mapper;
    private final DynamoDBMapperConfig mapperConfig;

    public EmployeeBuilder(DynamoDBMapper mapper, DynamoDBMapperConfig config) {
        this.mapper = mapper;
        this.mapperConfig = config;
    }

    public static EmployeeBuilder createBuilder() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("eu-north-1")
                .build();

        String tableName = "employees";
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName))
                .build();

        return new EmployeeBuilder(new DynamoDBMapper(client, mapperConfig), mapperConfig);
    }

    public void save(Employee employee) {
        mapper.save(employee);
    }
    public Employee findById(String id) {
        return mapper.load(Employee.class, id, mapperConfig);
    }


}
