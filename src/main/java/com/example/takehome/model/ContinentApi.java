package com.example.takehome.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class ContinentApi {

    private List<String> countries;

    private String name;

    private List<String> otherCountries;
}