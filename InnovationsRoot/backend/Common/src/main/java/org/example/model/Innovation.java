package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Innovation {

    private int id;
    private String title;
    private String description;

    public Innovation(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
