package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.Employee;
import org.example.model.Innovation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InnovationWithUserDetails {
    private Innovation innovation;
    private Employee employee;
}
