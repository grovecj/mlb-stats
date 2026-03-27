package com.mlbstats.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${mlb.api.base-url}")
    private String mlbApiBaseUrl;

    @Value("${mlb.api.timeout:30000}")
    private int timeout;

    @Bean
    public RestClient mlbApiRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);

        return RestClient.builder()
                .baseUrl(mlbApiBaseUrl)
                .requestFactory(factory)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Bean
    public RestClient baseballSavantRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(60000); // Longer timeout for CSV downloads

        return RestClient.builder()
                .requestFactory(factory)
                .defaultHeader("Accept", "text/csv")
                .defaultHeader("User-Agent", "MLB-Stats-App/1.0")
                .build();
    }
}
