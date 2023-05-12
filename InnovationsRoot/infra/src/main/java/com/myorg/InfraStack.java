package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.pipelines.Step;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.apigateway.Stage;
import software.amazon.awscdk.services.apigatewayv2.alpha.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.alpha.PayloadFormatVersion;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegration;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegrationProps;
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
import software.constructs.Construct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class InfraStack extends Stack {
    public InfraStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfraStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Function springBootFunction = Function.Builder.create(this, "SubmitInnovationLambda")
                .handler("org.example.StreamLambdaHandler")
                .runtime(Runtime.JAVA_11)
                .memorySize(1024)
                .timeout(Duration.seconds(20))
                .code(Code.fromAsset("../assets/SubmitInnovationLambda.jar"))
                .build();

        // Enable Snapstart
        CfnFunction cfnFunction = (CfnFunction) springBootFunction.getNode().getDefaultChild();
        cfnFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        RestApi api = RestApi.Builder.create(this, "MyRestApi")
                .description("This is REST API")
                .defaultCorsPreflightOptions(CorsOptions.builder()
                        .allowCredentials(true)
                        .allowOrigins(singletonList("*")).build())
                .build();

        Resource resource = api.getRoot().addResource("add-innovation");
        resource.addMethod("POST", new LambdaIntegration(springBootFunction));

        Bucket siteBucket = Bucket.Builder.create(this, "AngularBucket")
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
                .destinationBucket(siteBucket).build();

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

//        RestApi restApi = RestApi.Builder.create(this, "RestApi")
//                .restApiName("MyRestApi")
//                .build();
//
//        restApi.getRoot().addMethod("GET", StepFunctionsIntegration.startExecution((IStateMachine) springBootFunction));
//        restApi.root.addMethod("GET", StepFunctionsIntegration.startExecution(stateMachine));
//
////        HttpApi httpApi = new HttpApi(this, "HttpApi");
//
//
//        HttpLambdaIntegration httpLambdaIntegration = new HttpLambdaIntegration(
//                "this",
//                springBootFunction,
//                HttpLambdaIntegrationProps.builder()
//                        .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
//                        .build()
//        );
//        restApi.addRoutes(AddRoutesOptions.builder()
//                .path("/lambda-test")
//                .methods(singletonList(HttpMethod.GET))
//                .integration(httpLambdaIntegration)
//                .build()
//        );
//
//        new CfnOutput(this, "HttApi", CfnOutputProps.builder()
//                .description("HTTP API URL")
//                .value(httpApi.getApiEndpoint())
//                .build());


//        LambdaRestApi api = LambdaRestApi.Builder.create(this, "myapi")
//                .handler(springBootFunction)
//                .proxy(false)
//                .build();
//
//        Resource items = api.root.addResource("items");
//        items.addMethod("GET"); // GET /items
//        items.addMethod("POST"); // POST /items

    }
}
