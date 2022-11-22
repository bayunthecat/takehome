package com.example.takehome.controller;

import com.example.takehome.model.CountryQuizApi;
import com.example.takehome.service.CountryQuizService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CountryQuizController {

    private final CountryQuizService quizService;

    public CountryQuizController(CountryQuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/country-quiz/solution")
    public CountryQuizApi solve(@RequestParam("codes") List<String> countryCodes) {
        return quizService.solveQuiz(countryCodes);
    }
}
