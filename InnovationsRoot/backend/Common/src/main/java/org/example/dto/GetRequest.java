package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.InnovationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetRequest {
    private String id;
    private String title;
    private String description;
    private InnovationStatus status;
    private String userId;
    private String comment;
}
