package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.Innovation;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class innovationUserIdResponse {
    private String name;
    private String lastname;
    private Long tokens;
    private List<Innovation> innovations;
}
