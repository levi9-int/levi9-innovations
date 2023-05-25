package org.example.service;

import org.example.builder.EmployeeBuilder;
import org.example.builder.ProductBuilder;
import org.example.dto.BuyProductRequest;
import org.example.dto.BuyProductResponse;
import org.example.dto.ProductRequestDTO;
import org.example.exception.BadRequestException;
import org.example.model.Employee;
import org.example.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductBuilder productBuilder = ProductBuilder.createBuilder();
    private final EmployeeBuilder employeeBuilder = EmployeeBuilder.createBuilder();

    public List<Product> getProducts(){
        return productBuilder.getAll();
    }

    public void createProduct(ProductRequestDTO productRequest){
        Product p = new Product(productRequest.getName(), productRequest.getTokenPrice(), productRequest.getAmount());
        productBuilder.save(p);
    }

    public BuyProductResponse buyProduct(BuyProductRequest buyProductRequest){
        String employeeId = buyProductRequest.getEmployee();
        String productId = buyProductRequest.getProduct();

        Employee employee = employeeBuilder.findById(employeeId);
        Product product = productBuilder.findById(productId);

        if(employee==null || product==null) return null;

        Long employeesToken = employee.getTokens();
        Long productCost = product.getTokenPrice();

       if(employeesToken < productCost) throw new BadRequestException("Not enough tokens!");
        if(product.getAmount() < 1) throw new BadRequestException("Product not available!");

        product.setAmount(product.getAmount()-1);
        employee.setTokens(employeesToken-productCost);

        if(employee.getProductIdList()==null) employee.setProductIdList(new ArrayList<String>());

        employee.getProductIdList().add(product.getProductId());

        productBuilder.save(product);
        employeeBuilder.save(employee);

        return new BuyProductResponse(employee.getTokens(), getUsersProducts(employee));
    }

    public List<Product> getUsersProducts(Employee employee){
        List<Product> products = new ArrayList<>();
        List<String> productsIds = employee.getProductIdList();
        if(productsIds!=null && !productsIds.isEmpty()){
            for (String productId : employee.getProductIdList()){
                products.add(productBuilder.findById(productId));
            }
        }
        return products;
    }
}
