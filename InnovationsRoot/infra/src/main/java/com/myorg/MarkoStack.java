package com.myorg;


import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.lambda.CfnFunction;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.Map;

public class MarkoStack extends Stack {
    public MarkoStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MarkoStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Function springBootFunction = Function.Builder.create(this, "SubmitInnovationLambda")
                .runtime(Runtime.JAVA_11)
                .handler("org.example.StreamLambdaHandler")
                .memorySize(1024)
                .timeout(Duration.seconds(20))
                .functionName("StreamLambdaHandler")
                .code(Code.fromAsset("../assets/function.jar"))
                .build();

        // Enable Snapstart
        CfnFunction cfnFunction = (CfnFunction) springBootFunction.getNode().getDefaultChild();
        cfnFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        RestApi api = RestApi.Builder.create(this, "MyRestApi")
                .restApiName("My Rest API")
                .description("This is my REST API")
                .build();

        Resource resource = api.getRoot().addResource("add-innovation");
        resource.addMethod("POST", new LambdaIntegration(springBootFunction));

    }
}
