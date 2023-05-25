package org.example.service;

import org.example.builder.EmployeeBuilder;
import org.example.builder.InnovationBuilder;
import org.example.dto.InnovationRequest;
import org.example.dto.InnovationUserIdResponse;
import org.example.dto.InnovationWithUserDetails;
import org.example.dto.ReviewInnovationRequest;
import org.example.enums.InnovationStatus;
import org.example.exception.NotFoundException;
import org.example.model.Employee;
import org.example.model.Innovation;
import org.example.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InnovationService {

    private final InnovationBuilder innovationRepo = InnovationBuilder.createBuilder();
    private final EmployeeBuilder employeeRepo = EmployeeBuilder.createBuilder();
    private final MailService mailService;
    private final ProductService productService;

    public InnovationService(MailService mailService, ProductService productService) {
        this.mailService = mailService;
        this.productService = productService;
    }

    public void reviewInnovation(ReviewInnovationRequest reviewInnovationRequest) {

        Innovation innovation = innovationRepo.findByUserIdAndInnovationId(
                reviewInnovationRequest.getInnovationId(),
                reviewInnovationRequest.getUserId());

        if (innovation == null) throw new NotFoundException("Innovation doesn't exists!");

        if (reviewInnovationRequest.isApproved()) {
            innovation.setInnovationStatus(InnovationStatus.APPROVED);
            addTokensForUser(innovation, reviewInnovationRequest.getTokenAmount());
        } else {
            innovation.setInnovationStatus(InnovationStatus.REJECTED);
        }

        if (reviewInnovationRequest.getComment() != null && !reviewInnovationRequest.getComment().isBlank())
            innovation.setComment(reviewInnovationRequest.getComment());

        innovationRepo.save(innovation);
        Employee employee = getEmployeeForInnovation(innovation);
        this.mailService.sendMailToEmployee(innovation, employee.getEmail());
    }

    private void addTokensForUser(Innovation innovation, Integer tokenAmount) {
        Employee employee = employeeRepo.findById(innovation.getUserId());
        employee.setTokens(employee.getTokens() + tokenAmount);
        employeeRepo.save(employee);
    }

    private Employee getEmployeeForInnovation(Innovation innovation) {
        Employee employee = employeeRepo.findById(innovation.getUserId());
        if (employee == null) throw new NotFoundException("Employee doesn't exists!");
        return employee;
    }


    public InnovationUserIdResponse getInnovationsForUser(String userId) {
        Employee emp = employeeRepo.findById(userId);
        if (emp == null) throw new NotFoundException("Employee doesn't exists!");
        List<Innovation> usersInnovations = innovationRepo.getByUserId(userId);
        List<Product> usersProducts = productService.getUsersProducts(emp);
        return new InnovationUserIdResponse(emp, usersInnovations, usersProducts);
    }

    public List<InnovationWithUserDetails> getByStatus(String status) {
        List<Innovation> innovations = innovationRepo.getByStatus(status);
        List<InnovationWithUserDetails> responseList = new ArrayList<>();
        for (Innovation i : innovations) {
            Employee emp = employeeRepo.findById(i.getUserId());
            responseList.add(new InnovationWithUserDetails(i, emp));
        }
        return responseList;
    }

    public List<Innovation> getAll() {
        return this.innovationRepo.getAll();
    }

    public void createInnovation(InnovationRequest innovationRequest) {
        Innovation innovation = new Innovation(innovationRequest.getTitle(), innovationRequest.getDescription(),
                InnovationStatus.PENDING, innovationRequest.getUserId());

        innovationRepo.save(innovation);

        Employee employee = employeeRepo.findById(innovation.getUserId());
        mailService.sendMailForNewInnovation(innovation, employee);
    }
}
