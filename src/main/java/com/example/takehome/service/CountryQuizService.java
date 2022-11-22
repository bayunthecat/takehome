package com.example.takehome.service;

import com.example.takehome.client.HttpCountryServiceClient;
import com.example.takehome.client.dto.ContinentDto;
import com.example.takehome.client.dto.CountryDto;
import com.example.takehome.model.ContinentApi;
import com.example.takehome.model.CountryQuizApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CountryQuizService {

    private final HttpCountryServiceClient client;

    public CountryQuizService(HttpCountryServiceClient client) {
        this.client = client;
    }

    public CountryQuizApi solveQuiz(List<String> countryCodes) {
        final var countries = client.getCountriesByCodes(countryCodes);
        final var countriesByContinent = groupCountriesByContinents(countries);
        final var continents = countriesByContinent.entrySet().stream().map(entry -> {
                    final var continent = entry.getKey();
                    final var inputCountries = entry.getValue();
                    final var otherCountries = client.getCountriesByContinents(List.of(continent), inputCountries);
                    return ContinentApi.builder()
                            .countries(inputCountries)
                            .otherCountries(otherCountries.stream().map(CountryDto::getCode).toList())
                            .name(otherCountries.stream().findAny().map(CountryDto::getContinent).map(ContinentDto::getName).orElse("Unknown"))
                            .build();
                })
                .toList();
        return CountryQuizApi.builder()
                .continents(continents)
                .build();
    }

    private Map<String, List<String>> groupCountriesByContinents(List<CountryDto> countries) {
        return countries.stream().collect(Collectors.groupingBy(country -> country.getContinent().getCode(), Collectors.collectingAndThen(Collectors.toList(), groupedCountries -> groupedCountries.stream().map(CountryDto::getCode).toList())));
    }
}