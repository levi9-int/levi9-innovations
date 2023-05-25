package org.example.builder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import org.example.model.Innovation;
import org.example.model.Product;

import java.util.List;

public class ProductBuilder {

    private final DynamoDBMapper mapper;

    private final DynamoDBMapperConfig config;

    public ProductBuilder(DynamoDBMapper mapper, DynamoDBMapperConfig config) {
        this.mapper = mapper;
        this.config = config;
    }

    public static ProductBuilder createBuilder() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("eu-north-1")
                .build();

        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder().build();
        return new ProductBuilder(new DynamoDBMapper(client, mapperConfig), mapperConfig);
    }

    public DynamoDBMapper getMapper() {
        return this.mapper;
    }

    public void save(Product product) {
        mapper.save(product);
    }

    public List<Product> getAll() {
        return mapper.scan(Product.class, new DynamoDBScanExpression());
    }

}
