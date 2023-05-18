package org.example;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.jaxrs.AwsProxySecurityContext;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.json.GsonJsonParser;

import java.io.InputStream;

public class PostConfirmationLambdaHandler implements RequestHandler<InputStream, Object> {

    GsonJsonParser gsonJsonParser = new GsonJsonParser();

    @Override
    public Object handleRequest(InputStream input, final Context context) {
        input.
        System.out.println(input.getClaims().getEmail());
        System.out.println(input.getName());

        return input;
    }
}
