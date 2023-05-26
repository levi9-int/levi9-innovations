package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.cognito.*;
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
import software.amazon.awscdk.services.stepfunctions.IStateMachine;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.GetIdentityVerificationAttributesRequest;
import software.amazon.awssdk.services.ses.model.GetIdentityVerificationAttributesResponse;
import software.amazon.awssdk.services.ses.model.VerifyEmailIdentityRequest;
import software.constructs.Construct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class InfraStack extends Stack {

    private static final String AWS_SES_IDENTITY = "zaricu22@gmail.com";

    public InfraStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfraStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Bucket siteBucket = buildS3Bucket();

        Table innovationTable = buildInnovationTable();
        Table employeesTable = buildEmployeeTable();
        Table productTable = buildProductTable();

        Function submitInnovationLambda = buildSubmitInnovationLambda();
        innovationTable.grantReadWriteData(submitInnovationLambda);
        employeesTable.grantReadWriteData(submitInnovationLambda);

        Function approveDeclineInnovationLambda = buildApproveDeclineLambda();
        innovationTable.grantReadWriteData(approveDeclineInnovationLambda);
        employeesTable.grantReadWriteData(approveDeclineInnovationLambda);

        Function getInnovationsLambda = buildGetInnovationsLambda();
        innovationTable.grantReadWriteData(getInnovationsLambda);
        employeesTable.grantReadWriteData(getInnovationsLambda);
        productTable.grantReadWriteData(getInnovationsLambda);

        Function cognitoPostConfirmationLambda = buildCognitoPostConfirmationLambda();
        employeesTable.grantReadWriteData(cognitoPostConfirmationLambda);

        Function submitProductLambda = buildSubmitProductLambda();
        productTable.grantReadWriteData(submitProductLambda);

        Function getProductsLambda = buildGetAllProductLambda();
        productTable.grantReadWriteData(getProductsLambda);
        employeesTable.grantReadWriteData(getProductsLambda);

        Function buyProductLambda = buildBuyProductLambda();
        employeesTable.grantReadWriteData(buyProductLambda);
        productTable.grantReadWriteData(buyProductLambda);

        UserPool userPool = buildUserPool(cognitoPostConfirmationLambda);

        verifyMailBySES(AWS_SES_IDENTITY);

        // Create the custom authorizer Lambda function
        Function authorizerFunction = buildAuthorizerLambda();


        RequestAuthorizer customAuthorizer = RequestAuthorizer.Builder.create(this, "CustomAuthorizer")
                .handler(authorizerFunction.getCurrentVersion())
                .identitySources(singletonList("method.request.header.Authorization"))
                .resultsCacheTtl(Duration.hours(1))
                .build();

        RestApi api = buildApiGateway();

        api.getRoot()
                .addResource("add-innovation")
                .addMethod("POST", new LambdaIntegration(submitInnovationLambda.getCurrentVersion()),
                        MethodOptions.builder()
                                .authorizationType(AuthorizationType.CUSTOM)
                                .authorizer(customAuthorizer)
                                .build());

        api.getRoot()
                .addResource("get-innovation")
                .addMethod("GET", new LambdaIntegration(getInnovationsLambda.getCurrentVersion()),
                        MethodOptions.builder()
                                .authorizationType(AuthorizationType.CUSTOM)
                                .authorizer(customAuthorizer)
                                .build());

        api.getRoot()
                .addResource("review-innovation")
                .addMethod("PUT", new LambdaIntegration(approveDeclineInnovationLambda.getCurrentVersion()),
                        MethodOptions.builder()
                                .authorizationType(AuthorizationType.CUSTOM)
                                .authorizer(customAuthorizer)
                                .build());


        api.getRoot()
                .addResource("add-products")
                .addMethod("POST", new LambdaIntegration(submitProductLambda.getCurrentVersion()),
                        MethodOptions.builder()
                                .authorizationType(AuthorizationType.CUSTOM)
                                .authorizer(customAuthorizer)
                                .build());

        api.getRoot()
                .addResource("get-products")
                .addMethod("GET", new LambdaIntegration(getProductsLambda.getCurrentVersion()),
                            MethodOptions.builder()
                                    .authorizationType(AuthorizationType.CUSTOM)
                                    .authorizer(customAuthorizer)
                                    .build());

        api.getRoot()
                .addResource("buy-product")
                .addMethod("POST", new LambdaIntegration(buyProductLambda.getCurrentVersion()),
                MethodOptions.builder()
                        .authorizationType(AuthorizationType.CUSTOM)
                        .authorizer(customAuthorizer)
                        .build());

    }

    private Function buildAuthorizerLambda() {
        // Create the custom authorizer Lambda function
        Function authorizerFunction = Function.Builder.create(this, "AuthorizerFunction")
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("../assets/LambdaAuthorizerHandler.jar"))
                .handler("org.example.LambdaAuthorizerHandler")
                .timeout(Duration.seconds(30))
                .memorySize(512)
                .build();

        // Enable Snapstart
        CfnFunction cfnGetFunction = (CfnFunction) authorizerFunction.getNode().getDefaultChild();
        cfnGetFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        return authorizerFunction;
    }

    private Function buildCognitoPostConfirmationLambda() {
        Function lambda = Function.Builder.create(this, "CognitoPostConfigurationLambda")
                .handler("org.example.PostConfirmationLambdaHandler")
                .runtime(Runtime.JAVA_11)
                .memorySize(512)
                .timeout(Duration.seconds(20))
                .code(Code.fromAsset("../assets/CognitoPostConfigurationLambda.jar"))
                .initialPolicy(singletonList(PolicyStatement.Builder.create()
                        .actions(List.of("cognito-idp:AdminAddUserToGroup"))
                        .resources(List.of("aws:ResourceTag/"))
                        .resources(List.of("*"))
                        .build()))
                .build();

        // Enable Snapstart
        CfnFunction cfnGetFunction = (CfnFunction) lambda.getNode().getDefaultChild();
        cfnGetFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        return lambda;
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


    private UserPool buildUserPool(Function cognitoPostConfirmationLambda) {
        UserPool userPool = UserPool.Builder.create(this, "user-pool-1")
                .selfSignUpEnabled(true)
                .signInAliases(SignInAliases.builder().email(true).username(false).build())
                .autoVerify(AutoVerifiedAttrs.builder().email(true).build())
                .userVerification(UserVerificationConfig.builder()
                        .emailSubject("Verify your email.")
                        .emailBody("Thanks for signing up to our awesome app! Your verification code is {####}")
                        .emailStyle(VerificationEmailStyle.CODE)
                        .build())
                .standardAttributes(StandardAttributes.builder()
                        .givenName(StandardAttribute.builder().required(true).mutable(true).build())
                        .familyName(StandardAttribute.builder().required(true).mutable(true).build())
                        .email(StandardAttribute.builder().required(true).mutable(true).build())
                        .build())
                .passwordPolicy(PasswordPolicy.builder()
                        .minLength(8)
                        .requireDigits(true)
                        .requireUppercase(true)
                        .requireLowercase(true)
                        .requireSymbols(false)
                        .build())
                .removalPolicy(RemovalPolicy.DESTROY)
                .accountRecovery(AccountRecovery.EMAIL_ONLY)
                .lambdaTriggers(UserPoolTriggers.builder()
                        .postConfirmation(cognitoPostConfirmationLambda.getCurrentVersion()).build())
                .build();

        UserPoolClient userPoolClient = UserPoolClient.Builder.create(this, "user_pool_client")
                .userPool(userPool)
                .authFlows(AuthFlow.builder().userSrp(true).userPassword(true).adminUserPassword(true).build())
                .build();

        CfnUserPoolGroup employeeGroup = CfnUserPoolGroup.Builder.create(this, "employee_group")
                .userPoolId(userPool.getUserPoolId())
                .groupName("EmployeeGroup")
                .build();

        CfnUserPoolGroup leadGroup = CfnUserPoolGroup.Builder.create(this, "engineering_lead_group")
                .userPoolId(userPool.getUserPoolId())
                .groupName("EngineeringLeadGroup")
                .build();

        addLeadToGroup(leadGroup);

        CfnUserPoolGroup adminGroup = CfnUserPoolGroup.Builder.create(this, "admin_group")
                .userPoolId(userPool.getUserPoolId())
                .groupName("AdminGroup")
                .build();

        addAdminToGroup(adminGroup);

        return userPool;
    }

    private void addLeadToGroup(CfnUserPoolGroup leadGroup) {
        List<CfnUserPoolUser.AttributeTypeProperty> attributesList = new ArrayList<>();
        attributesList.add(CfnUserPoolUser.AttributeTypeProperty.builder().name("email").value("savic.jana15@gmail.com").build());
        attributesList.add(CfnUserPoolUser.AttributeTypeProperty.builder().name("given_name").value("Nenad").build());
        attributesList.add(CfnUserPoolUser.AttributeTypeProperty.builder().name("family_name").value("Miljanov").build());

        CfnUserPoolUser leadUser = new CfnUserPoolUser(this, "engineeringLead",
                CfnUserPoolUserProps.builder().userPoolId(leadGroup.getUserPoolId())
                        .username("savic.jana15@gmail.com")
                        .desiredDeliveryMediums(List.of("EMAIL"))
                        .userAttributes(attributesList)
                        .build());

        CfnUserPoolUserToGroupAttachment attachLeadToGroup = CfnUserPoolUserToGroupAttachment.Builder.create(this, "attach_lead_to_group")
                .userPoolId(leadGroup.getUserPoolId())
                .groupName(leadGroup.getGroupName())
                .username(leadUser.getUsername())
                .build();

        attachLeadToGroup.getNode().addDependency(leadUser);

    }


    private void addAdminToGroup(CfnUserPoolGroup adminGroup) {
        List<CfnUserPoolUser.AttributeTypeProperty> attributesList = new ArrayList<>();
        attributesList.add(CfnUserPoolUser.AttributeTypeProperty.builder().name("email").value("janasavic47@gmail.com").build());
        attributesList.add(CfnUserPoolUser.AttributeTypeProperty.builder().name("given_name").value("Jana").build());
        attributesList.add(CfnUserPoolUser.AttributeTypeProperty.builder().name("family_name").value("Savic").build());

        CfnUserPoolUser adminUser = new CfnUserPoolUser(this, "admin",
                CfnUserPoolUserProps.builder().userPoolId(adminGroup.getUserPoolId())
                        .username("janasavic47@gmail.com")
                        .desiredDeliveryMediums(List.of("EMAIL"))
                        .userAttributes(attributesList)
                        .build());

        CfnUserPoolUserToGroupAttachment attachAdminToGroup = CfnUserPoolUserToGroupAttachment.Builder.create(this, "attach_admin_to_group")
                .userPoolId(adminGroup.getUserPoolId())
                .groupName(adminGroup.getGroupName())
                .username(adminUser.getUsername())
                .build();

        attachAdminToGroup.getNode().addDependency(adminUser);
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
    private Function buildBuyProductLambda(){
        Function lambda = Function.Builder.create(this, "BuyProductLambda")
                .handler("org.example.BuyProductLambdaHandler")
                .runtime(Runtime.JAVA_11)
                .memorySize(512)
                .timeout(Duration.seconds(20))
                .code(Code.fromAsset("../assets/BuyProductLambda.jar"))
                .build();
        CfnFunction cfnFunction = (CfnFunction) lambda.getNode().getDefaultChild();
        cfnFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        return lambda;
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
        if (!verificationAttributesAreEmpty)
            verificationStatusIsPending = verificationResponse.verificationAttributes().entrySet().iterator()
                    .next().getValue().verificationStatusAsString().equals("Pending");
        if (verificationAttributesAreEmpty || verificationStatusIsPending)
            sesClient.verifyEmailIdentity(VerifyEmailIdentityRequest.builder()
                    .emailAddress(mail)
                    .build());
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
                .tableName("innovation")
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

        Table table = new Table(this, "innovation", tableProps);
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
                .tableName("employees")
                .build();
        return new Table(this, "employees", tableProps);
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
                .tableName("products")
                .build();
        return new Table(this, "products", tableProps);
    }
}
