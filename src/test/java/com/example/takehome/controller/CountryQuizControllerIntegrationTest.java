package com.example.takehome.controller;

import com.example.takehome.configuration.EmbeddedRedis;
import com.example.takehome.model.CountryQuizApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EmbeddedRedis.class)
public class CountryQuizControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private String url;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockRestServiceServer;

    @BeforeEach
    void init() {
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testNominal() {
        mockCountriesByIdsRequest();
        mockCountriesByContinentRequest();
        final var codes = List.of("US", "CA");
        final var response = testRestTemplate.getForEntity(buildUrl(List.of("US", "CA")), CountryQuizApi.class);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        final var continent = response.getBody()
                .getContinents()
                .stream()
                .findFirst()
                .orElse(null);
        assertNotNull(continent);
        final var countries = continent.getCountries();
        assertTrue(codes.size() == countries.size() && codes.containsAll(countries) && countries.containsAll(codes));
        assertEquals("North America", continent.getName());
        assertFalse(continent.getOtherCountries().isEmpty());
    }

    private void mockCountriesByIdsRequest() {
        final var request = "{\"operationName\":null,\"variables\":{},\"query\":\"{\\n  countries(filter: {code: {in: [\\\"US\\\",\\\"CA\\\"]}}) {\\n    code\\n    continent {\\n      code\\n    name\\n}\\n  }\\n}\\n\"}";
        final var countries = new ClassPathResource("countries_by_ids.json");
        mockRestServiceServer.expect(requestTo("https://countries.trevorblades.com/graphql"))
                .andExpect(content().string(request))
                .andRespond(withSuccess(countries, MediaType.APPLICATION_JSON));
    }

    private void mockCountriesByContinentRequest() {
        final var request = "{\"operationName\":null,\"variables\":{},\"query\":\"{\\n  countries(filter: {continent: {in: [\\\"NA\\\"]}, code: {nin: [\\\"CA\\\",\\\"US\\\"]}}) {\\n    code\\n continent {\\n name\\n}\\n  }\\n}\\n\"}";
        final var countries = new ClassPathResource("countries_by_continents.json");
        mockRestServiceServer.expect(requestTo("https://countries.trevorblades.com/graphql"))
                .andExpect(content().string(request))
                .andRespond(withSuccess(countries, MediaType.APPLICATION_JSON));
    }

    private String buildUrl(List<String> codes) {
        return String.format("http://localhost:%s/country-quiz/solution?codes=", port) + String.join(",", codes);
    }
}