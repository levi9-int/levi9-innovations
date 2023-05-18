package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.CorsOptions;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.dynamodb.*;
import software.amazon.awscdk.services.iam.PolicyStatement;
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
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.GetIdentityVerificationAttributesRequest;
import software.amazon.awssdk.services.ses.model.GetIdentityVerificationAttributesResponse;
import software.amazon.awssdk.services.ses.model.IdentityVerificationAttributes;
import software.amazon.awssdk.services.ses.model.VerifyEmailIdentityRequest;
import software.constructs.Construct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.util.Collections.singletonList;

public class UrosStack extends Stack {
    private static final String AWS_SES_IDENTITY = "zaricu22@gmail.com";

    public UrosStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public UrosStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Bucket siteBucket = buildS3Bucket();

        Table innovationTable = buildInnovationTable();
        Table employeesTable = buildEmployeeTable();

        Function submitInnovationLambda = buildSubmitInnovationLambda();
        innovationTable.grantReadWriteData(submitInnovationLambda);
        employeesTable.grantReadWriteData(submitInnovationLambda);

        Function approveDeclineInnovationLambda = buildApproveDeclineLambda();
        innovationTable.grantReadWriteData(approveDeclineInnovationLambda);
        employeesTable.grantReadWriteData(approveDeclineInnovationLambda);

        Function getInnovationsLambda = buildGetInnovationsLambda();
        innovationTable.grantReadWriteData(getInnovationsLambda);

        verifyMailBySES(AWS_SES_IDENTITY);

        RestApi api = buildApiGateway();
        api.getRoot()
                .addResource("add-innovation")
                .addMethod("POST", new LambdaIntegration(submitInnovationLambda));

        api.getRoot()
                .addResource("get-innovation")
                .addMethod("GET", new LambdaIntegration(getInnovationsLambda));

        api.getRoot()
                .addResource("review-innovation")
                .addMethod("PUT", new LambdaIntegration(approveDeclineInnovationLambda));
    }

    private void verifyMailBySES(String mail) {
        Region region = Region.EU_NORTH_1;
        SesClient sesClient = SesClient.builder()
                .region(region)
                .build();

        // if verification process hasn't been initiated for the identity
        GetIdentityVerificationAttributesResponse verificationResponse = sesClient.getIdentityVerificationAttributes(
                GetIdentityVerificationAttributesRequest.builder()
                    .identities(mail)
                    .build());
        boolean verificationAttributesAreEmpty = verificationResponse.verificationAttributes().entrySet().isEmpty();
        boolean verificationStatusIsPending = false;
        if(!verificationAttributesAreEmpty)
            verificationStatusIsPending = verificationResponse.verificationAttributes().entrySet().iterator()
                    .next().getValue().verificationStatusAsString().equals("Pending");
        if(verificationAttributesAreEmpty || verificationStatusIsPending)
            sesClient.verifyEmailIdentity(VerifyEmailIdentityRequest.builder()
                    .emailAddress(mail)
                    .build());
    }

    private Function buildGetInnovationsLambda() {
        Function springBootGetFunction = Function.Builder.create(this, "GetInnovationLambda")
                .handler("org.example.StreamLambdaHandler")
                .runtime(Runtime.JAVA_11)
                .memorySize(512)
                .timeout(Duration.seconds(20))
                .code(Code.fromAsset("../assets/GetInnovationLambda.jar"))
                .build();

        // Enable Snapstart
        CfnFunction cfnGetFunction = (CfnFunction) springBootGetFunction.getNode().getDefaultChild();
        cfnGetFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        return springBootGetFunction;
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
        sources.add(Source.asset("../cognito-demo/dist/cognito-demo"));

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
                .initialPolicy(singletonList(PolicyStatement.Builder.create()
                        .actions(List.of("ses:SendEmail"))
                        .resources(List.of("*"))
                        .build()))
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
                .initialPolicy(singletonList(PolicyStatement.Builder.create()
                        .actions(List.of("ses:SendEmail"))
                        .resources(List.of("*"))
                        .build()))
                .build();

        // Enable Snapstart
        CfnFunction cfnFunction = (CfnFunction) submitInnovationLambda.getNode().getDefaultChild();
        cfnFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        return submitInnovationLambda;
    }

    private Table buildInnovationTable() {
        TableProps tableProps = TableProps.builder()
                .partitionKey(Attribute.builder()
                        .name("userId")
                        .type(AttributeType.STRING)
                        .build())
                .sortKey(Attribute.builder()
                        .name("innovationId")
                        .type(AttributeType.STRING)
                        .build())
                .billingMode(BillingMode.PROVISIONED)
                .readCapacity(1)
                .writeCapacity(1)
                .removalPolicy(RemovalPolicy.DESTROY)
                .tableName("innovation-uros")
                .build();

        GlobalSecondaryIndexProps gsi = GlobalSecondaryIndexProps.builder()
                .indexName("innovationStatus-index")
                .partitionKey(Attribute.builder()
                        .name("innovationStatus")
                        .type(AttributeType.STRING)
                        .build())
                .projectionType(ProjectionType.ALL)
                .readCapacity(1)
                .writeCapacity(1)
                .build();

        Table table = new Table(this, "innovation-uros", tableProps);
        table.addGlobalSecondaryIndex(gsi);

        return table;
    }

    private Table buildEmployeeTable() {
        TableProps tableProps = TableProps.builder()
                .partitionKey(Attribute.builder()
                        .name("employeeId")
                        .type(AttributeType.STRING)
                        .build())
                .billingMode(BillingMode.PROVISIONED)
                .readCapacity(1)
                .writeCapacity(1)
                .removalPolicy(RemovalPolicy.DESTROY)
                .tableName("employees-uros")
                .build();
        return new Table(this, "employees-uros", tableProps);
    }
}
