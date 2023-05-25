package com.myorg;


import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.dynamodb.*;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.CfnFunction;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class AnaStack extends Stack {

    public AnaStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AnaStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Table productTable = buildProductTable();

        Function submitProductLambda = buildSubmitProductLambda();
        productTable.grantReadWriteData(submitProductLambda);

        Function getProductsLambda = buildGetAllProductLambda();
        productTable.grantReadWriteData(getProductsLambda);




        RestApi api = buildApiGateway();


        api.getRoot()
                .addResource("add-products")
                .addMethod("POST", new LambdaIntegration(submitProductLambda));

        api.getRoot()
                .addResource("get-products")
                .addMethod("GET", new LambdaIntegration(getProductsLambda));



    }

    private Function buildGetAllProductLambda() {

        Function springBootGetFunction = Function.Builder.create(this, "GetProductsLambda")
                .handler("org.example.StreamLambdaHandler")
                .runtime(Runtime.JAVA_11)
                .memorySize(512)
                .timeout(Duration.seconds(20))
                .code(Code.fromAsset("../assets/GetProductsLambda.jar"))
                .build();

        // Enable Snapstart
        CfnFunction cfnGetFunction = (CfnFunction) springBootGetFunction.getNode().getDefaultChild();
        cfnGetFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        return springBootGetFunction;
    }

    private RestApi buildApiGateway() {

        return RestApi.Builder.create(this, "MyRestApi")
                .description("This is REST API")
                .defaultCorsPreflightOptions(CorsOptions.builder()
                        .allowCredentials(true)
                        .allowOrigins(singletonList("*")).build())
                .build();
    }

    private Function buildSubmitProductLambda() {

        Function submitProductLambda = Function.Builder.create(this, "SubmitProductLambda")
                .handler("org.example.SubmitProductLambdaHandler")
                .runtime(Runtime.JAVA_11)
                .memorySize(512)
                .timeout(Duration.seconds(20))
                .code(Code.fromAsset("../assets/SubmitProductLambda.jar"))
                .build();

        // Enable Snapstart
        CfnFunction cfnFunction = (CfnFunction) submitProductLambda.getNode().getDefaultChild();
        cfnFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        return submitProductLambda;
    }

    private Table buildProductTable() {

        TableProps tableProps = TableProps.builder()
                    .partitionKey(Attribute.builder()
                            .name("productId")
                            .type(AttributeType.STRING)
                            .build())
                    .billingMode(BillingMode.PROVISIONED)
                    .readCapacity(1)
                    .writeCapacity(1)
                    .removalPolicy(RemovalPolicy.DESTROY)
                    .tableName("product")
                    .build();
            return new Table(this, "product", tableProps);
    }

}
