package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Arrays;

public class InfraApp {
    public static void main(final String[] args) {
        App app = new App();


        // MAIN STACK
        new InfraStack(app, "InfraStack", StackProps.builder()
                .build());

        // MARKO DEVELOP STACK
//        new MarkoStack(app, "MarkoStack", StackProps.builder()
//                .build());

//        new UrosStack(app, "UrosStack", StackProps.builder()
//               .build());

        new CognitoStack(app, "CognitoStack", StackProps.builder().build());

//        new CognitoStackMD(app, "CognitoStackMD", StackProps.builder().build());
        app.synth();
    }
}

