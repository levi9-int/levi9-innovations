package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import org.example.utils.JWTUtil;
import org.example.util.PolicyDocument;
import org.example.util.Response;
import org.example.util.Statement;

import java.util.*;

public class LambdaAuthorizerHandler implements RequestHandler<APIGatewayProxyRequestEvent, Response> {
    private static final String EMPLOYEE_GROUP = "EmployeeGroup";
    private static final String LEAD_GROUP = "EngineeringLeadGroup";


    @Override
    public Response handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String jwtToken = event.getHeaders().get("Authorization");

        String sub = JWTUtil.getSub(jwtToken, "sub");
        if (sub == null) {
            throw new RuntimeException("Unauthorized");
        }
        List<String> userGroup = JWTUtil.getCognitoGroups(jwtToken, "cognito:groups");

        Map<String, String> ctx = new HashMap<>();
        ctx.put("sub", sub);

        String auth;
        List<Statement> statements = new ArrayList<>();

        APIGatewayProxyRequestEvent.ProxyRequestContext proxyContext = event.getRequestContext();
        APIGatewayProxyRequestEvent.RequestIdentity identity = proxyContext.getIdentity();

        if (userGroup.contains(EMPLOYEE_GROUP)) {
            addStatement(statements, "get-innovation", "GET", proxyContext);
            addStatement(statements, "add-innovation", "POST", proxyContext);

        } else if (userGroup.contains(LEAD_GROUP)) {
            addStatement(statements, "get-innovation", "GET", proxyContext);
            addStatement(statements, "review-innovation", "PUT", proxyContext);
        }

        PolicyDocument policyDocument = PolicyDocument.builder().statements(statements)
                .build();

        return Response.builder().principalId(identity.getAccountId()).policyDocument(policyDocument)
                .context(ctx).build();
    }

    private void addStatement(List<Statement> statements, String endpoint, String httpMethod,
                              APIGatewayProxyRequestEvent.ProxyRequestContext proxyContext) {
        String auth = "Allow";
        String arn = String.format("arn:aws:execute-api:%s:%s:%s/%s/%s/%s",
                "eu-north-1", proxyContext.getAccountId(),
                proxyContext.getApiId(), proxyContext.getStage(), httpMethod, endpoint);
        statements.add(Statement.builder().effect(auth).resource(arn).build());
    }
}
