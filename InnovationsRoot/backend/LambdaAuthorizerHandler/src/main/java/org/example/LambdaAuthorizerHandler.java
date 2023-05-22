package org.example;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.amazonaws.regions.Region;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.util.JWTUtil;
import org.example.util.PolicyDocument;
import org.example.util.Response;
import org.example.util.Statement;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LambdaAuthorizerHandler implements RequestHandler<APIGatewayProxyRequestEvent, Response> {
    private static final String EMPLOYEE_GROUP = "EmployeeGroup";
    private static final String LEAD_GROUP = "LeadGroup";


    @Override
    public Response handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String jwtToken = event.getHeaders().get("Authorization");

//        try {
        // Verify and parse the JWT token

        String sub = JWTUtil.getSub(jwtToken, "sub");
        System.out.println(sub);
        List<String> userGroup = JWTUtil.getCognitoGroups(jwtToken, "cognito:groups");
        for (String s : userGroup) {
            System.out.println(s);
        }
//        Claims claims = Jwts.parser().parseClaimsJwt(jwtToken).getBody();
//        String userGroup = claims.get("cognito:groups", String.class);
//        String sub = claims.get("sub", String.class);
        Map<String, String> ctx = new HashMap<String, String>();
        ctx.put("sub", sub);

        String auth = "Deny";

        if (userGroup != null && userGroup.contains(EMPLOYEE_GROUP)) {
            // Allow access to /add-innovation and /get-innovation?userId=... for EmployeeGroup
            if (event.getPath().equals("/add-innovation") || event.getPath().startsWith("/get-innovation")) {
                auth = "Allow";
            }
        } else if (userGroup != null && userGroup.contains(LEAD_GROUP)) {
            // Allow access to /get-innovation and /review-innovation for LeadGroup
            if (event.getPath().startsWith("/get-innovation") || event.getPath().equals("/review-innovation")) {
                auth = "Allow";
            }
        }

        System.out.println(auth);
        APIGatewayProxyRequestEvent.ProxyRequestContext proxyContext = event.getRequestContext();
        APIGatewayProxyRequestEvent.RequestIdentity identity = proxyContext.getIdentity();

        String arn = String.format("arn:aws:execute-api:%s:%s:%s/%s/%s%s", "eu-north-1", proxyContext.getAccountId(),
                proxyContext.getApiId(), proxyContext.getStage(), proxyContext.getHttpMethod(), proxyContext.getResourcePath());

        System.out.println("************************** ARN **************************");
        System.out.println(arn);
        Statement statement = Statement.builder().effect(auth).resource(arn).build();

        PolicyDocument policyDocument = PolicyDocument.builder().statements(Collections.singletonList(statement))
                .build();

        return Response.builder().principalId(identity.getAccountId()).policyDocument(policyDocument)
                .context(ctx).build();

//            if (userGroup != null && userGroup.contains(EMPLOYEE_GROUP)) {
//                // Allow access to /add-innovation and /get-innovation?userId=... for EmployeeGroup
//                if (event.getPath().equals("/add-innovation") || event.getPath().startsWith("/get-innovation")) {
//                    return generateAllowResponse(event, claims);
//                }
//            } else if (userGroup != null && userGroup.contains(LEAD_GROUP)) {
//                // Allow access to /get-innovation and /review-innovation for LeadGroup
//                if (event.getPath().startsWith("/get-innovation") || event.getPath().equals("/review-innovation")) {
//                    return generateAllowResponse(event, claims);
//                }
//            }
//
//            // Deny access if the user group is not authorized for the requested endpoint
//            return generateDenyResponse(event);
//        } catch (Exception e) {
//            // Deny access if the JWT token is invalid or an error occurred
//            return generateDenyResponse(event);
//        }
    }

//    private APIGatewayProxyResponseEvent generateAllowResponse(APIGatewayProxyRequestEvent event, Claims claims) {
//        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
//
//        response.setStatusCode(200);
//        response.setBody("Authorized");
//        response.setHeaders(generateResponseHeaders(event, claims));
//        return response;
//    }
//
//    private APIGatewayProxyResponseEvent generateDenyResponse(APIGatewayProxyRequestEvent event) {
//        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
//        response.setStatusCode(403);
//        response.setBody("Unauthorized");
//        response.setHeaders(generateResponseHeaders(event, null));
//        return response;
//    }
//
//    private Map<String, String> generateResponseHeaders(APIGatewayProxyRequestEvent event, Claims claims) {
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Access-Control-Allow-Origin", "*");
//        // You can add any additional headers if needed
//
//        if (claims != null) {
//            // Add any claims or additional headers you want to include in the response
//            headers.put("userId", claims.getSubject());
//        }
//
//        return headers;
//    }
}
