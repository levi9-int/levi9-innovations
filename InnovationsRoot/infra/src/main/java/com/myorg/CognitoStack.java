package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.cognito.*;
import software.amazon.awscdk.services.iam.CfnUser;
import software.amazon.awscdk.services.lambda.CfnFunction;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CognitoStack extends Stack {
    public CognitoStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public CognitoStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Function buildCognitoPostConfigurationLambda = buildCognitoPostConfigurationLambda();


        UserPool userPool = UserPool.Builder.create(this, "test-pool")
                .userPoolName("test-pool")
                .selfSignUpEnabled(true)

                .userVerification(UserVerificationConfig.builder()
                        .emailSubject("Verify email.")
                        .emailBody("Thanks for signing up to our awesome app! Your verification code is {####}")
                        .emailStyle(VerificationEmailStyle.CODE)
                        .smsMessage("Thanks for signing up to our awesome app! Your verification code is {####}")
                        .build())
                .autoVerify(AutoVerifiedAttrs.builder().email(true).build())
                .standardAttributes(StandardAttributes.builder()
                        .fullname(StandardAttribute.builder().required(true).mutable(true).build())
                        .email(StandardAttribute.builder().required(true).mutable(true).build())
                        .phoneNumber(StandardAttribute.builder().required(true).mutable(true).build())
                        .build())
                .passwordPolicy(PasswordPolicy.builder()
                        .minLength(8)
                        .requireDigits(true)
                        .requireUppercase(true)
                        .requireLowercase(true)
                        .requireSymbols(false)
                        .build())
                .removalPolicy(RemovalPolicy.DESTROY)
//                .accountRecovery(AccountRecovery.EMAIL_ONLY)
                .build();

        UserPoolClient userPoolClient = UserPoolClient.Builder.create(this, "test_pool_client")
                .userPool(userPool)
                .authFlows(AuthFlow.builder().userPassword(true).adminUserPassword(true).build())
                .build();

        CfnUserPoolGroup employeeGroup = CfnUserPoolGroup.Builder.create(this, "employee_group")
                .userPoolId(userPool.getUserPoolId())
                .groupName("EmployeeGroup")
                .build();

        CfnUserPoolGroup leadGroup = CfnUserPoolGroup.Builder.create(this, "engeneering_lead_group")
                .userPoolId(userPool.getUserPoolId())
                .groupName("EngeneeringLeadGroup")
                .build();

        List<CfnUserPoolUser.AttributeTypeProperty> attributesList = new ArrayList<>();
        attributesList.add(CfnUserPoolUser.AttributeTypeProperty.builder().name("email").value("innovation.lead@outlook.com").build());
        attributesList.add(CfnUserPoolUser.AttributeTypeProperty.builder().name("name").value("Marko Markovic").build());
        attributesList.add(CfnUserPoolUser.AttributeTypeProperty.builder().name("phone_number").value("+381612904025").build());

        CfnUserPoolUser leadUser = new CfnUserPoolUser(this, "engineeringLead",
                CfnUserPoolUserProps.builder().userPoolId(userPool.getUserPoolId())
                        .username("engineeringLead")
                        .desiredDeliveryMediums(List.of("EMAIL"))
                        .userAttributes(attributesList)
                        .build());

        CfnUserPoolUserToGroupAttachment attachLeadToGroup = CfnUserPoolUserToGroupAttachment.Builder.create(this, "attach_lead_to_group")
                .userPoolId(userPool.getUserPoolId())
                .groupName(leadGroup.getGroupName())
                .username(leadUser.getUsername())
                .build();

        attachLeadToGroup.getNode().addDependency(leadUser);

    }

    private Function buildCognitoPostConfigurationLambda() {
        Function springBootGetFunction = Function.Builder.create(this, "CognitoPostConfigurationLambda")
                .handler("org.example.StreamLambdaHandler")
                .runtime(Runtime.JAVA_11)
                .memorySize(512)
                .timeout(Duration.seconds(20))
                .code(Code.fromAsset("../assets/CognitoPostConfigurationLambda.jar"))
                .build();

        // Enable Snapstart
        CfnFunction cfnGetFunction = (CfnFunction) springBootGetFunction.getNode().getDefaultChild();
        cfnGetFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        return springBootGetFunction;
    }
}