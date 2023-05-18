package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.cognito.*;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.CfnFunction;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class CognitoStack extends Stack {
    public CognitoStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public CognitoStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Function cognitoPostConfirmationLambda = buildCognitoPostConfirmationLambda();

        UserPool userPool = UserPool.Builder.create(this, "user-pool")
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
                .lambdaTriggers(UserPoolTriggers.builder().postConfirmation(cognitoPostConfirmationLambda).build())
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

        addLeadToGroup(userPool, leadGroup);


    }

    private void addLeadToGroup(UserPool userPool, CfnUserPoolGroup leadGroup) {
        List<CfnUserPoolUser.AttributeTypeProperty> attributesList = new ArrayList<>();
        attributesList.add(CfnUserPoolUser.AttributeTypeProperty.builder().name("email").value("savic.jana15@gmail.com").build());
        attributesList.add(CfnUserPoolUser.AttributeTypeProperty.builder().name("given_name").value("Nenad").build());
        attributesList.add(CfnUserPoolUser.AttributeTypeProperty.builder().name("family_name").value("Miljanov").build());

        CfnUserPoolUser leadUser = new CfnUserPoolUser(this, "engineeringLead",
                CfnUserPoolUserProps.builder().userPoolId(userPool.getUserPoolId())
                        .username("savic.jana15@gmail.com")
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

    private Function buildCognitoPostConfirmationLambda() {
        Function lambda = Function.Builder.create(this, "CognitoPostConfigurationLambda")
                .handler("org.example.PostConfirmationLambdaHandler")
                .runtime(Runtime.JAVA_11)
                .memorySize(512)
                .timeout(Duration.seconds(20))
                .code(Code.fromAsset("../assets/CognitoPostConfigurationLambda.jar"))
                .initialPolicy(singletonList(PolicyStatement.Builder.create()
                        .actions(List.of("cognito-idp:AdminAddUserToGroup"))
                        .resources(List.of("*"))
                        .build()))
                .build();

        // Enable Snapstart
        CfnFunction cfnGetFunction = (CfnFunction) lambda.getNode().getDefaultChild();
        cfnGetFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));

        return lambda;
    }
}