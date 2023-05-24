package org.example.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

import java.util.Map;

@Data
@DynamoDBTable(tableName = "product")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Product {


    @DynamoDBHashKey(attributeName = "productId")
    private String productId;

    @DynamoDBAttribute
    private String name;

    @DynamoDBAttribute
    private Long tokenPrice;

    @DynamoDBAttribute
    private int amount;

    public Product(String name, Long tokenPrice, int amount) {
        this.name = name;
        this.tokenPrice = tokenPrice;
        this.amount = amount;
    }
}
