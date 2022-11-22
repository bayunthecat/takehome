package com.example.takehome.client;

import com.example.takehome.client.dto.CountryDto;

import java.util.List;

public interface CountryServiceClient {

    List<CountryDto> getCountriesByContinents(List<String> continentCodes, List<String> excludeCountryCodes);

    List<CountryDto> getCountriesByCodes(List<String> countryCodes);
}
