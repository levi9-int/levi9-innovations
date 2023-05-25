package org.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDTO {

    private String name;

    private Long tokenPrice;

    private  int amount;


}
