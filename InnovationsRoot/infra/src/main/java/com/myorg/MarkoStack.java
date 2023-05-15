package com.myorg;


import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.CorsOptions;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.dynamodb.*;
import software.amazon.awscdk.services.lambda.CfnFunction;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.s3.BlockPublicAccess;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketAccessControl;
import software.amazon.awscdk.services.s3.deployment.BucketDeployment;
import software.amazon.awscdk.services.s3.deployment.ISource;
import software.amazon.awscdk.services.s3.deployment.Source;
import software.constructs.Construct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class MarkoStack extends Stack {
    public MarkoStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MarkoStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Bucket siteBucket = buildS3Bucket();

        Table innovationTable = buildInnovationTable();
        Table employeesTable = buildEmployeeTable();

        Function submitInnovationLambda = buildSubmitInnovationLambda();
        innovationTable.grantReadWriteData(submitInnovationLambda);
        employeesTable.grantReadWriteData(submitInnovationLambda);

        Function approveDeclineInnovationLambda = buildApproveDeclineLambda();
        innovationTable.grantReadWriteData(approveDeclineInnovationLambda);
        employeesTable.grantReadWriteData(approveDeclineInnovationLambda);


        RestApi api = buildApiGateway();
        api.getRoot()
                .addResource("add-innovation")
                .addMethod("POST", new LambdaIntegration(submitInnovationLambda));

        api.getRoot()
                .addResource("get-innovation")
                .addMethod("GET", new LambdaIntegration(submitInnovationLambda));

        api.getRoot()
                .addResource("review-innovation")
                .addMethod("PUT", new LambdaIntegration(approveDeclineInnovationLambda));
    }

    private Bucket buildS3Bucket() {
        Bucket siteBucket = Bucket.Builder.create(this, "AngularBucket")
                .websiteIndexDocument("index.html")
                .websiteErrorDocument("index.html")
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
                .destinationBucket(siteBucket).build();

        return siteBucket;
    }

    private RestApi buildApiGateway() {

        return RestApi.Builder.create(this, "MyRestApi")
                .description("This is REST API")
                .defaultCorsPreflightOptions(CorsOptions.builder()
                        .allowCredentials(true)
                        .allowOrigins(singletonList("*")).build())
                .build();

        // Deploy the REST API to a stage
//        Deployment deployment = Deployment.Builder.create(this, "MyDeployment")
//                .api(api)
//                .description("Initial deployment")
//                .build();
//
//        Stage stage = Stage.Builder.create(this, "MyStage")
//                .deployment(deployment)
//                .stageName("testStage")
//                .build();
    }

    private Function buildApproveDeclineLambda() {
        Function lambda = Function.Builder.create(this, "ApproveDeclineInnovationLambda")
                .handler("org.example.ApproveDeclineLambdaHandler")
                .runtime(Runtime.JAVA_11)
                .memorySize(512)
                .timeout(Duration.seconds(20))
                .code(Code.fromAsset("../assets/ApproveDeclineInnovationLambda.jar"))
                .build();

        // Enable Snapstart
        CfnFunction cfnFunction = (CfnFunction) lambda.getNode().getDefaultChild();
        cfnFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        return lambda;
    }

    private Function buildSubmitInnovationLambda() {
        Function submitInnovationLambda = Function.Builder.create(this, "SubmitInnovationLambda")
                .handler("org.example.StreamLambdaHandler")
                .runtime(Runtime.JAVA_11)
                .memorySize(512)
                .timeout(Duration.seconds(20))
                .code(Code.fromAsset("../assets/SubmitInnovationLambda.jar"))
                .build();

        // Enable Snapstart
        CfnFunction cfnFunction = (CfnFunction) submitInnovationLambda.getNode().getDefaultChild();
        cfnFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        return submitInnovationLambda;
    }

    private Table buildInnovationTable() {
        TableProps tableProps = TableProps.builder()
                .partitionKey(Attribute.builder()
                        .name("innovationId")
                        .type(AttributeType.STRING)
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
//                .readCapacity(1)
//                .writeCapacity(1)
                .removalPolicy(RemovalPolicy.DESTROY)
                .tableName("innovation")
                .build();
        return new Table(this, "innovation", tableProps);
    }

    private Table buildEmployeeTable() {
        TableProps tableProps = TableProps.builder()
                .partitionKey(Attribute.builder()
                        .name("employeeId")
                        .type(AttributeType.STRING)
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
//                .readCapacity(1)
//                .writeCapacity(1)
                .removalPolicy(RemovalPolicy.DESTROY)
                .tableName("employees")
                .build();
        return new Table(this, "employees", tableProps);
    }
}
