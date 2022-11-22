package com.example.takehome.client.dto;

import lombok.Data;

@Data
public class CountryDto {

    private String code;

    private ContinentDto continent;
}
