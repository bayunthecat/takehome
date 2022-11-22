package com.example.takehome.client;

import com.example.takehome.client.dto.CountriesResponseDataDto;
import com.example.takehome.client.dto.CountriesResponseDto;
import com.example.takehome.client.dto.CountryDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class HttpCountryServiceClient implements CountryServiceClient {

    private static final String FETCH_COUNTRIES_BY_CODES = "{\"operationName\":null,\"variables\":{},\"query\":\"{\\n  countries(filter: {code: {in: [%s]}}) {\\n    code\\n    continent {\\n      code\\n    name\\n}\\n  }\\n}\\n\"}";

    private static final String FETCH_COUNTRIES_BY_CONTINENT_WITH_EXCLUDE = "{\"operationName\":null,\"variables\":{},\"query\":\"{\\n  countries(filter: {continent: {in: [%s]}, code: {nin: [%s]}}) {\\n    code\\n continent {\\n name\\n}\\n  }\\n}\\n\"}";

    private static final String LIST_ELEM_FORMAT = "\\\"%s\\\"";

    private static final String HTTP_SERVICE_URL = "https://countries.trevorblades.com/graphql";

    private final RestTemplate restTemplate;

    public HttpCountryServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<CountryDto> getCountriesByCodes(List<String> countryCodes) {
        final var formattedCode = formatCodes(countryCodes);
        return Optional.ofNullable(restTemplate.postForObject(HTTP_SERVICE_URL,
                        buildPostBody(() -> String.format(FETCH_COUNTRIES_BY_CODES, formattedCode)),
                        CountriesResponseDto.class))
                .map(CountriesResponseDto::getData)
                .map(CountriesResponseDataDto::getCountries)
                .orElse(List.of());
    }

    @Override
    public List<CountryDto> getCountriesByContinents(List<String> continentCodes, List<String> excludeCountryCodes) {
        final var formattedContinentCodes = formatCodes(continentCodes);
        final var formattedExcludes = formatCodes(excludeCountryCodes);
        return Optional.ofNullable(restTemplate.postForObject(HTTP_SERVICE_URL,
                        buildPostBody(() -> String.format(FETCH_COUNTRIES_BY_CONTINENT_WITH_EXCLUDE, formattedContinentCodes, formattedExcludes)),
                        CountriesResponseDto.class))
                .map(CountriesResponseDto::getData)
                .map(CountriesResponseDataDto::getCountries)
                .orElse(List.of());
    }

    private HttpEntity<String> buildPostBody(Supplier<String> bodySupplier) {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(bodySupplier.get(), headers);
    }

    private String formatCodes(List<String> codes) {
        return codes.stream().map(code -> String.format(LIST_ELEM_FORMAT, code)).collect(Collectors.joining(","));
    }

}
