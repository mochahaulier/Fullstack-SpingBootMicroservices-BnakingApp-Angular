package dev.mochahaulier.clientproductservice.config;

import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import dev.mochahaulier.clientproductservice.client.ClientClient;
import dev.mochahaulier.clientproductservice.client.ProductClient;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    @Value("${client.service.url}")
    private String clientServiceUrl;
    @Value("${product.service.url}")
    private String productServiceUrl;
    private final ObservationRegistry observationRegistry;

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder()
                .requestFactory(getClientRequestFactory())
                .observationRegistry(observationRegistry);
    }

    @Bean
    public HttpServiceProxyFactory clientServiceProxyFactory(@LoadBalanced RestClient.Builder restClientBuilder) {
        RestClient restClient = restClientBuilder
                .baseUrl(clientServiceUrl)
                .build();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        return HttpServiceProxyFactory.builderFor(restClientAdapter).build();
    }

    @Bean
    public ClientClient clientClient(HttpServiceProxyFactory clientServiceProxyFactory) {
        return clientServiceProxyFactory.createClient(ClientClient.class);
    }

    @Bean
    public HttpServiceProxyFactory productServiceProxyFactory(@LoadBalanced RestClient.Builder restClientBuilder) {
        RestClient restClient = restClientBuilder
                .baseUrl(productServiceUrl)
                .build();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        return HttpServiceProxyFactory.builderFor(restClientAdapter).build();
    }

    @Bean
    public ProductClient productClient(HttpServiceProxyFactory productServiceProxyFactory) {
        return productServiceProxyFactory.createClient(ProductClient.class);
    }

    private ClientHttpRequestFactory getClientRequestFactory() {
        ClientHttpRequestFactorySettings clientHttpRequestFactorySettings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3))
                .withReadTimeout(Duration.ofSeconds(3));
        return ClientHttpRequestFactories.get(clientHttpRequestFactorySettings);
    }
}