package org.example.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewInnovationRequest {

    private String innovationId;
    private String userId;
    private boolean approved;
    private String comment;
    private Integer tokenAmount;

}
