package com.example.takehome.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class CountriesResponseDataDto {

    private List<CountryDto> countries;
}