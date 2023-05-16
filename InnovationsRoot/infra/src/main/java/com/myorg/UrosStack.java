package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.CorsOptions;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;
import software.amazon.awscdk.services.lambda.CfnFunction;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;
import java.util.Map;
import static java.util.Collections.singletonList;

public class UrosStack extends Stack {
    public UrosStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public UrosStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        TableProps innovationTableProps = TableProps.builder()
                .partitionKey(Attribute.builder()
                        .name("innovationId")
                        .type(AttributeType.STRING)
                        .build())
                .readCapacity(1)
                .writeCapacity(1)
                .removalPolicy(RemovalPolicy.DESTROY)
                .tableName("innovation-uros")
                .build();
        Table innovationDynamoDbTable = new Table(this, "innovation-uros", innovationTableProps);

        Function springBootSubmitFunction = Function.Builder.create(this, "SubmitInnovationLambda")
                .functionName("SubmitInnovationLambda-Uros")
                .handler("org.example.StreamLambdaHandler")
                .runtime(Runtime.JAVA_11)
                .memorySize(1024)
                .timeout(Duration.seconds(20))
                .code(Code.fromAsset("../assets/SubmitInnovationLambda.jar"))
                .build();

        // Enable Snapstart
        CfnFunction cfnSubmitFunction = (CfnFunction) springBootSubmitFunction.getNode().getDefaultChild();
        cfnSubmitFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        Function springBootGetFunction = Function.Builder.create(this, "GetInnovationLambda")
                .functionName("GetInnovationLambda-Uros")
                .handler("org.example.StreamLambdaHandler")
                .runtime(Runtime.JAVA_11)
                .memorySize(1024)
                .timeout(Duration.seconds(20))
                .code(Code.fromAsset("../assets/GetInnovationLambda.jar"))
                .build();

        // Enable Snapstart
        CfnFunction cfnGetFunction = (CfnFunction) springBootGetFunction.getNode().getDefaultChild();
        cfnGetFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        innovationDynamoDbTable.grantReadWriteData(springBootSubmitFunction);
        innovationDynamoDbTable.grantReadWriteData(springBootGetFunction);

        RestApi api = RestApi.Builder.create(this, "RestApi-Uros")
                .restApiName("RestApi-Uros")
                .description("This is REST API")
                .defaultCorsPreflightOptions(CorsOptions.builder()
                        .allowCredentials(true)
                        .allowOrigins(singletonList("*")).build())
                .build();

        Resource resourceSubmit = api.getRoot().addResource("add-innovation");
        resourceSubmit.addMethod("POST", new LambdaIntegration(springBootSubmitFunction));
//        Resource resourceGet = api.getRoot().addResource("get-all");
//        resourceGet.addMethod("POST", new LambdaIntegration(springBootGetFunction));
        Resource resourceGetByUserId = api.getRoot().addResource("get-innovation");
        resourceGetByUserId.addMethod("GET", new LambdaIntegration(springBootGetFunction));

        /*Bucket siteBucket = Bucket.Builder.create(this, "AngularBacket")
                .websiteIndexDocument("index.html")
                .publicReadAccess(true)
                .blockPublicAccess(BlockPublicAccess.BLOCK_ACLS)
                .accessControl(BucketAccessControl.BUCKET_OWNER_FULL_CONTROL)
                .removalPolicy(RemovalPolicy.DESTROY)
                .autoDeleteObjects(true)
                .build();

        List<ISource> sources = new ArrayList<>(1);
        sources.add(Source.asset("../frontend/dist/frontend"));

        BucketDeployment.Builder.create(this, "DeployAngularApp")
                .sources(sources)
                .destinationBucket(siteBucket).build();*/
    }
}
