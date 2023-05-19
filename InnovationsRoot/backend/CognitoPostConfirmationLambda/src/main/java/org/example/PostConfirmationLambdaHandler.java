package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.lambda.runtime.events.CognitoUserPoolPostConfirmationEvent;
import org.example.builder.EmployeeBuilder;
import org.example.builder.InnovationBuilder;
import org.example.model.Employee;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupResponse;

import java.util.Map;

public class PostConfirmationLambdaHandler implements RequestHandler<CognitoUserPoolPostConfirmationEvent, CognitoUserPoolPostConfirmationEvent> {

    private static final String EMPLOYEE_GROUP_NAME = "EmployeeGroup";
    private final EmployeeBuilder employeeRepo = EmployeeBuilder.createBuilder();

    private final CognitoIdentityProviderClient cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder()
            .region(Region.EU_NORTH_1)
            .build();

    @Override
    public CognitoUserPoolPostConfirmationEvent handleRequest(CognitoUserPoolPostConfirmationEvent cognitoUserPoolPostConfirmationEvent, Context context) {

        Map<String, String> userAttributes = cognitoUserPoolPostConfirmationEvent.getRequest().getUserAttributes();
        addUserToGroup(userAttributes.get("sub"), cognitoUserPoolPostConfirmationEvent.getUserPoolId());

        Employee employee = new Employee(userAttributes);
        employeeRepo.save(employee);

        return cognitoUserPoolPostConfirmationEvent;
    }

    private void addUserToGroup(String username, String userPoolId) {
        AdminAddUserToGroupRequest addUserToGroupRequest = AdminAddUserToGroupRequest.builder()
                .userPoolId(userPoolId)
                .groupName(EMPLOYEE_GROUP_NAME)
                .username(username)
                .build();

        AdminAddUserToGroupResponse response = cognitoIdentityProviderClient.adminAddUserToGroup(addUserToGroupRequest);

        // Check if the operation was successful, handle errors if needed
        // You can access the response using response. methods
    }
}
