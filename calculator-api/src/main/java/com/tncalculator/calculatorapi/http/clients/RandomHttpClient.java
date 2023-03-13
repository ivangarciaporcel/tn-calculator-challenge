package com.tncalculator.calculatorapi.http.clients;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.tncalculator.calculatorapi.constants.MessageConstants.RANDOM_STRING_NOT_GENERATED;

@Component
@Log4j2
public class RandomHttpClient {

    private final String BASE_URL = "https://www.random.org/strings/?num=1";
    private final RestTemplate restTemplate;

    @Autowired
    public RandomHttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateRandomString() {
        String url = getUrl();
        log.debug(String.format("URL used to generate random strings: %s.", url));
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        checkArgument(HttpStatus.OK.value() == response.getStatusCode().value(), RANDOM_STRING_NOT_GENERATED);
        return response.getBody();
    }

    private String getUrl() {
        StringBuilder sb = new StringBuilder(BASE_URL);
        sb.append(getQueryParameter("len", 20));
        sb.append(getQueryParameter("digits", "on"));
        sb.append(getQueryParameter("upperalpha", "on"));
        sb.append(getQueryParameter("loweralpha", "on"));
        sb.append(getQueryParameter("unique", "off"));
        sb.append(getQueryParameter("format", "plain"));
        sb.append(getQueryParameter("rnd", "new"));
        return sb.toString();
    }

    private String getQueryParameter(String key, Object value) {
        return String.format("&%s=%s", key, value);
    }
}
