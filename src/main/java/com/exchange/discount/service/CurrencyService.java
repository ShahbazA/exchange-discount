package com.exchange.discount.service;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Log4j2
public class CurrencyService {

    @Value("${exchange-base-url}")
    private String exchangeBaseUrl;

    @Value("${api-key}")
    private String apiKey;

    private final WebClient webClient;

    public CurrencyService(){
        this.webClient = WebClient.create();
    }

    @Cacheable(cacheNames = "exchangeRatesCache", key = "#originalCurrency + '_' + #targetCurrency")
    public Double currencyConversion(String originalCurrency, String targetCurrency){
        String jsonPath = "$.rates." + targetCurrency;

        Object result = JsonPath.read(getExchangeRates(originalCurrency), jsonPath);
        if(result instanceof Integer){
            String resultStr = Integer.toString((Integer) result);
            return Double.valueOf(resultStr);
        }
        return (Double)result;
    }

    private String getExchangeRates(String originalCurrency){
        log.info("getExchangeRates() called, originalCurrency: {}", originalCurrency);
        return webClient.get()
                .uri(exchangeBaseUrl + originalCurrency + "?apiKey=" + apiKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}