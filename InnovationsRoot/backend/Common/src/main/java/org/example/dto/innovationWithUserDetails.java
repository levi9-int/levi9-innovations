package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.Employee;
import org.example.model.Innovation;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class innovationWithUserDetails {
    private Innovation innovation;
    private Employee employee;
}
